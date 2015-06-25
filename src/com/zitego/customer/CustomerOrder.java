package com.zitego.customer;

import com.zitego.customer.product.OrderItem;
import com.zitego.customer.product.Product;
import com.zitego.customer.delivery.ChosenDeliveryMethod;
import com.zitego.customer.pricing.PricingType;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.NoDataException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * This class encapsulates the information about a customer's order.
 *
 * @author John Glorioso
 * @version $Id: CustomerOrder.java,v 1.5 2011/10/16 17:20:32 jglorioso Exp $
 */
public class CustomerOrder extends DatabaseEntity
{
    private long _customerId = -1;
    private String _description;
    private String _invoiceId;
    private String _transactionId;
    private TransactionType _transactionType = TransactionType.CHARGE;
    private ResultType _result;
    private String _approvalCode;
    private String _respCode;
    private String _avsCode;
    private String _cvv2Code;
    private String _error;
    private String _clientIp;
    private Vector<CustomerOrderItem> _items = new Vector<CustomerOrderItem>();

    /**
     * Creates a new CustomerOrder.
     *
     * @param db The database handle.
     * @deprecated Use CustomerOrder(DBConfig) instead.
     */
    public CustomerOrder(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new CustomerOrder.
     *
     * @param config The database config.
     */
    public CustomerOrder(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new CustomerOrder.
     *
     * @param id The id.
     * @param db The database handle.
     * @deprecated Use CustomerOrder(long, DBConfig) instead.
     */
    public CustomerOrder(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new CustomerOrder.
     *
     * @param id The id.
     * @param config The database config.
     */
    public CustomerOrder(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        StringBuffer sql = new StringBuffer()
            .append("SELECT customer_id, description, invoice_id, transaction_id, transaction_type, total, result, approval_code, ")
            .append(       "resp_code, avs_code, cvv2_code, client_ip, error_msg, billing_date ")
            .append("FROM customer_order ")
            .append("WHERE customer_order_id = ? ");
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();
            if ( !rs.next() ) throw new NoDataException("No such customer order: "+getId());
            _customerId = rs.getLong("customer_id");
            _description = rs.getString("description");
            _invoiceId = rs.getString("invoice_id");
            _transactionId = rs.getString("transaction_id");
            String type = rs.getString("transaction_type");
            _transactionType = ( "preauth".equals(type) ? TransactionType.PRE_AUTH : ("refund".equals(type) ? TransactionType.REFUND : TransactionType.CHARGE) );
            _result = ResultType.evaluate( rs.getString("result") );
            _approvalCode = rs.getString("approval_code");
            _respCode = rs.getString("resp_code");
            _avsCode = rs.getString("avs_code");
            _cvv2Code = rs.getString("cvv2_code");
            _clientIp = rs.getString("client_ip");
            _error = rs.getString("error_msg");
            setCreationDate( rs.getTimestamp("billing_date") );
        }
        finally
        {
            db.disconnect();
        }
        loadItems();
    }

    /**
     * Loads the order items from this order from the database.
     *
     * @throws SQLException if a database error occurs.
     */
    public void loadItems() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT i.customer_order_item_id, p.product_id, p.reference_string name, p.reference_string, p.label, p.product_number, ")
            .append(       "p.pricing_type_id, p.regular_price, p.sale_price, p.volume_pricing_config, p.search_keywords, p.apply_category_discount, ")
            .append(       "p.apply_state_tax, p.apply_country_tax, p.inventory_count, p.max_quantity, p.thumbnail_image_url, p.standard_image_url, ")
            .append(       "p.enlarged_image_url, p.short_description, p.long_description, p.delivery_method_type_id, p.delivery_method_config, ")
            .append(       "p.allow_store_pickup, p.free_shipping, p.related_products, p.status, p.creation_date, p.last_updated, i.notes item_notes, ")
            .append(       "i.price item_price, i.delivery_method_config item_delivery, i.quantity, i.status item_status, i.creation_date item_created, i.last_updated item_updated, ")
            .append(       "d.customer_order_item_detail_id, po.product_option_id, po.name po_name, po.label po_label, po.description po_desc, ")
            .append(       "po.option_type_id, po.required, po.is_inventory_tracked, po.status po_status, po.creation_date po_created, po.last_updated po_updated, ")
            .append(       "poc.product_option_choice_id, poc.name poc_name, poc.label poc_label, poc.value poc_value, poc.status poc_status, ")
            .append(       "poc.creation_date poc_created, poc.last_updated poc_updated, d.notes notes, d.price, d.status detail_status, ")
            .append(       "d.creation_date detail_created, d.last_updated detail_updated ")
            .append("FROM customer_order_item i LEFT JOIN customer_order_item_detail d ")
            .append(          "ON (i.customer_order_item_id = d.customer_order_item_id) LEFT JOIN product_option po ")
            .append(          "ON (d.product_option_id = po.product_option_id) LEFT JOIN product_option_choice poc ")
            .append(          "ON (d.product_option_choice_id = poc.product_option_choice_id), product p ")
            .append("WHERE i.customer_order_id = ? AND i.product_id = p.product_id ")
            .append("ORDER BY d.customer_order_item_id, po.product_option_id");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            CustomerOrderItem i = null;
            long lastItem = -1;
            long item = -1;
            while ( rs.next() )
            {
                item = rs.getLong("customer_order_item_id");
                if (item != lastItem)
                {
                    if (i != null) addItem(i);
                    i = createOrderItem( rs.getLong( "customer_order_item_id"), getDBConfig() );
                    i.setCustomerOrderId( getId() );
                    i.setProductId( rs.getLong("product_id") );
                    Product p = i.getProduct();
                    p.loadFromResultSet(rs);
                    i.setNotes( rs.getString("item_notes") );
                    i.setPrice( rs.getFloat("item_price") );
                    String config = rs.getString("item_delivery");
                    if (config != null) i.setDeliveryMethod( new ChosenDeliveryMethod(config) );
                    i.setQuantity( rs.getInt("quantity") );
                    i.setStatus( rs.getInt("item_status") );
                    i.setCreationDate( rs.getTimestamp("item_created") );
                    i.setLastUpdated( rs.getTimestamp("item_updated") );
                    lastItem = item;
                }
                long did = rs.getLong("customer_order_item_detail_id");
                if ( !rs.wasNull() )
                {
                    CustomerOrderItemDetail detail = new CustomerOrderItemDetail( did, getDBConfig() );
                    detail.loadFromResultSet(rs);
                    detail.setStatus( rs.getInt("detail_status") );
                    detail.setCreationDate( rs.getTimestamp("detail_created") );
                    detail.setLastUpdated( rs.getTimestamp("detail_updated") );
                    i.addDetail(detail);
                }
            }
            if (i != null) addItem(i);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Creates a new customer order item.
     *
     * @param id The order item id.
     * @param db The database handle.
     * @deprecated Use createOrderItem(long, DBConfig) instead.
     */
    public CustomerOrderItem createOrderItem(long id, DBHandle db)
    {
        return createOrderItem(id, db);
    }

    public CustomerOrderItem createOrderItem(long id, DBConfig config)
    {
        return new CustomerOrderItem(id, config);
    }

    /**
     * Saves the order items to the database.
     *
     * @throws SQLException when a database error occurs.
     */
    public void saveItems() throws SQLException
    {
        int size = _items.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItem item = _items.get(i);
            item.setCustomerOrderId( getId() );
            item.save();
        }
    }

    protected void update() throws SQLException
    {
        StringBuffer sql = new StringBuffer()
            .append("UPDATE customer_order SET customer_id = ?, description = ?, invoice_id = ?, transaction_id = ?, transaction_type = ?, total = ?, ")
            .append(       "result = ?, approval_code = ?, resp_code = ?, avs_code = ?, cvv2_code = ?, client_ip = ?, error_msg = ? ")
            .append("WHERE customer_order_id = ?");
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong(1, _customerId);
            pst.setString(2, _description);
            pst.setString(3, _invoiceId);
            pst.setString(4, _transactionId);
            pst.setString( 5, (_transactionType == TransactionType.PRE_AUTH ? "preauth" : (_transactionType == TransactionType.REFUND ? "refund" : "charge")) );
            pst.setFloat( 6, getTotal() );
            pst.setString( 7, _result.getDescription() );
            pst.setString(8, _approvalCode);
            pst.setString(9, _respCode);
            pst.setString(10, _avsCode);
            pst.setString(11, _cvv2Code);
            pst.setString(12, _clientIp);
            pst.setString(13, _error);
            pst.setLong( 14, getId() );
            pst.executeUpdate();
        }
        finally
        {
            db.disconnect();
        }
        saveItems();
    }

    protected void insert() throws SQLException
    {
        StringBuffer sql = new StringBuffer()
            .append("INSERT INTO customer_order ")
            .append(    "(customer_id, description, invoice_id, transaction_id, transaction_type, total, result, ")
            .append(     "approval_code, resp_code, avs_code, cvv2_code, client_ip, error_msg, billing_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())");
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong(1, _customerId);
            pst.setString(2, _description);
            pst.setString(3, _invoiceId);
            pst.setString(4, _transactionId);
            pst.setString( 5, (_transactionType == TransactionType.PRE_AUTH ? "preauth" : (_transactionType == TransactionType.REFUND ? "refund" : "charge")) );
            pst.setFloat( 6, getTotal() );
            pst.setString( 7, _result.getDescription() );
            pst.setString(8, _approvalCode);
            pst.setString(9, _respCode);
            pst.setString(10, _avsCode);
            pst.setString(11, _cvv2Code);
            pst.setString(12, _clientIp);
            pst.setString(13, _error);
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
        saveItems();
    }

    /**
     * Adds another order item to the order.
     *
     * @param item The order item.
     */
    public void addItem(CustomerOrderItem item)
    {
        _items.add(item);
    }

    /**
     * Removes the order item at the given index.
     *
     * @param item The order item to remove.
     */
    public void removeItem(int item)
    {
        if ( item >= 0 && item < _items.size() ) _items.removeElementAt(item);
    }

    /**
     * Returns the order items.
     *
     * @return Vector<CustomerOrderItem>
     */
    public Vector<CustomerOrderItem> getItems()
    {
        return _items;
    }

    /**
     * Returns the item with the given index.
     *
     * @param index The item index.
     * @return CustomerOrderItem
     */
    public CustomerOrderItem getItem(int index)
    {
        return _items.get(index);
    }

    /**
     * Returns the item with the given id.
     *
     * @param id The item id.
     * @return CustomerOrderItem
     */
    public CustomerOrderItem getItem(long id)
    {
        int size = _items.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItem ret = _items.get(i);
            if (ret.getId() == id) return ret;
            else ret = null;
        }
        return null;
    }

    /**
     * Sets the customer id.
     *
     * @param id The customer id.
     */
    public void setCustomerId(long id)
    {
        _customerId = id;
    }

    /**
     * Returns the customer id.
     *
     * @return long
     */
    public long getCustomerId()
    {
        return _customerId;
    }

    /**
     * Sets the description.
     *
     * @param desc The description.
     */
    public void setDescription(String desc)
    {
        _description = desc;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the quickbooks invoice id.
     *
     * @param id The invoice id.
     * @deprecated Use setInvoiceId
     */
    public void setQbInvoiceId(String id)
    {
        setInvoiceId(id);
    }

    /**
     * Returns the quickbooks invoice id.
     *
     * @return String
     * @deprecated Use getInvoiceId
     */
    public String getQbInvoiceId()
    {
        return getInvoiceId();
    }

    /**
     * Sets the invoice id.
     *
     * @param id The invoice id.
     */
    public void setInvoiceId(String id)
    {
        _invoiceId = id;
    }

    /**
     * Returns the invoice id.
     *
     * @return String
     */
    public String getInvoiceId()
    {
        return _invoiceId;
    }

    /**
     * Sets the transaction id.
     *
     * @param id The transaction id.
     */
    public void setTransactionId(String id)
    {
        _transactionId = id;
    }

    /**
     * Returns the transaction id.
     *
     * @return String
     */
    public String getTransactionId()
    {
        return _transactionId;
    }

    /**
     * Sets the transaction type.
     *
     * @param type The transaction type.
     * @throws IllegalArgumentException if the type is invalid.
     */
    public void setTransactionType(TransactionType type)
    {
        _transactionType = type;
    }

    /**
     * Returns the transaction type.
     *
     * @return TransactionType
     */
    public TransactionType getTransactionType()
    {
        return _transactionType;
    }

    /**
     * Sets the total.
     *
     * @param t The total.
     * @deprecated Total is calculated by cart items.
     */
    public void setTotal(float t) { }

    /**
     * Returns the total based on the items in the cart.
     *
     * @return float
     */
    public float getTotal()
    {
        float total = 0f;
        int size = _items.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItem item = _items.get(i);
            Product p = item.getProduct();
            float itemPrice = item.getPrice();
            int qty = item.getQuantity();
            if (p.getPricing().getType() == PricingType.VOLUME_BASED)
            {
                float noOptPrice = p.getPricing().getPrice( new Object[] { new Integer(qty) } );
                float optionPrice = itemPrice - noOptPrice;
                itemPrice = noOptPrice * qty;
                itemPrice += (optionPrice * qty);
            }
            else
            {
                itemPrice = itemPrice * qty;
            }
            total += itemPrice;
        }
        return total;
    }

    /**
     * Sets the processing result.
     *
     * @param res The result.
     * @deprecated Use setResultType
     */
    public void setResult(String res)
    {
        setResultType( ResultType.evaluate(res) );
    }

    /**
     * Returns the processing result.
     *
     * @return String
     * @deprecated Use getResultType
     */
    public String getResult()
    {
        return (_result != null ? _result.getDescription() : null);
    }

    /**
     * Sets the processing result type.
     *
     * @param res The result type.
     */
    public void setResultType(ResultType res)
    {
        _result = res;
    }

    /**
     * Returns the processing result type.
     *
     * @return ResultType
     */
    public ResultType getResultType()
    {
        return _result;
    }

    /**
     * Sets the approval code.
     *
     * @param code The approval code.
     */
    public void setApprovalCode(String code)
    {
        _approvalCode = code;
    }

    /**
     * Returns the approval code.
     *
     * @return String
     */
    public String getApprovalCode()
    {
        return _approvalCode;
    }

    /**
     * Sets the response code.
     *
     * @param code The response code.
     */
    public void setRespCode(String code)
    {
        _respCode = code;
    }

    /**
     * Returns the resp code.
     *
     * @return String
     */
    public String getRespCode()
    {
        return _respCode;
    }

    /**
     * Sets the avs code.
     *
     * @param code The avs code.
     */
    public void setAVSCode(String code)
    {
        _avsCode = code;
    }

    /**
     * Returns the avs code.
     *
     * @return String
     */
    public String getAVSCode()
    {
        return _avsCode;
    }

    /**
     * Sets the cvv2 code.
     *
     * @param code The cvv2 code.
     */
    public void setCVV2Code(String code)
    {
        _cvv2Code = code;
    }

    /**
     * Returns the cvv2 code.
     *
     * @return String
     */
    public String getCVV2Code()
    {
        return _cvv2Code;
    }

    /**
     * Sets the error message.
     *
     * @param err The error message.
     */
    public void setErrorMsg(String err)
    {
        _error = err;
    }

    /**
     * Returns the error message.
     *
     * @return String
     */
    public String getErrorMsg()
    {
        return _error;
    }

    /**
     * Sets the client ip.
     *
     * @param ip The client ip.
     */
    public void setClientIp(String ip)
    {
        _clientIp = ip;
    }

    /**
     * Returns the client ip.
     *
     * @return String
     */
    public String getClientIp()
    {
        return _clientIp;
    }

    /**
     * Returns the CustomerOrderItems as an OrderItem[].
     *
     * @return OrderItem[]
     */
    public OrderItem[] getOrderItems()
    {
        OrderItem[] ret = new OrderItem[_items.size()];
        _items.copyInto(ret);
        return ret;
    }
}
