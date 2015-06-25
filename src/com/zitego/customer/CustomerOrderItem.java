package com.zitego.customer;

import com.zitego.customer.product.Product;
import com.zitego.customer.delivery.ChosenDeliveryMethod;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.NoDataException;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.customer.product.OrderItem;
import com.zitego.customer.product.ProductOption;
import com.zitego.customer.product.ProductOptionChoice;
import com.zitego.util.StringValidation;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * This class encapsulates a customer order item from an order. It has a product that contains the
 * information about the order item. Additionally, it has notes, a price that was paid for the
 * item, and a quantity. Lastly, there is a vector of customer order items that are part of this.
 *
 * @author John Glorioso
 * @version $Id: CustomerOrderItem.java,v 1.5 2011/10/16 17:20:32 jglorioso Exp $
 */
public class CustomerOrderItem extends DatabaseEntity implements OrderItem
{
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("$#.00");
    private Product _product;
    private String _notes;
    private long _customerOrderId = -1;
    private float _price = 0f;
    private ChosenDeliveryMethod _deliveryMethod;
    private int _quantity = 0;
    private Vector _details = new Vector();

    /**
     * Creates a new CustomerOrderItem with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerOrderItem(DBConfig) instead.
     */
    public CustomerOrderItem(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new CustomerOrderItem with a DBConfig.
     *
     * @param config The db config to use for querying.
     */
    public CustomerOrderItem(DBConfig config)
    {
        super(config);
        _product = new Product(config);
    }

    /**
     * Creates a new CustomerOrderItem with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerOrderItem(long, DBConfig) instead.
     */
    public CustomerOrderItem(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new CustomerOrderItem with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public CustomerOrderItem(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT i.customer_order_id, p.product_id, p.reference_string name, p.reference_string, p.label, p.product_number, ")
            .append(       "p.pricing_type_id, p.regular_price, p.sale_price, p.volume_pricing_config, p.search_keywords, p.apply_category_discount, ")
            .append(       "p.apply_state_tax, p.apply_country_tax, p.inventory_count, p.max_quantity, p.thumbnail_image_url, p.standard_image_url, ")
            .append(       "p.enlarged_image_url, p.short_description, p.long_description, p.delivery_method_type_id, p.delivery_method_config, ")
            .append(       "p.allow_store_pickup, p.free_shipping, p.related_products, p.status, p.creation_date, p.last_updated, i.notes, i.price, ")
            .append(       "i.delivery_method_config item_delivery, i.quantity, i.status item_status, i.creation_date item_created, i.last_updated item_updated ")
            .append("FROM customer_order_item i, product p ")
            .append("WHERE i.customer_order_item_id = ? AND i.product_id = p.product_id");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such customer order item: id- " + getId() );
            setCustomerOrderId( rs.getLong("customer_order_id") );
            _product = new Product( rs.getLong("product_id"), getDBConfig() );
            _product.loadFromResultSet(rs);
            setNotes( rs.getString("notes") );
            setPrice( rs.getFloat("price") );
            String config = rs.getString("item_delivery");
            if (config != null) _deliveryMethod = new ChosenDeliveryMethod(config);
            setQuantity( rs.getInt("quantity") );
            setStatus( rs.getInt("item_status") );
            setCreationDate( rs.getTimestamp("item_created") );
            setLastUpdated( rs.getTimestamp("item_updated") );
        }
        finally
        {
            db.disconnect();
        }
        loadDetails();
    }

    /**
     * Loads the order details.
     *
     * @throws SQLException
     */
    public void loadDetails() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT d.customer_order_item_id, d.customer_order_item_detail_id, po.product_option_id, po.label po_label, po.description po_desc, ")
                .append(       "po.option_type_id, po.required, po.status po_status, po.creation_date po_created, po.last_updated po_updated, ")
                .append(       "poc.product_option_choice_id, poc.label poc_label, poc.value poc_value, poc.status poc_status, ")
                .append(       "poc.creation_date poc_created, poc.last_updated poc_updated, d.notes, d.price, d.status, d.creation_date, ")
                .append(       "d.last_updated ")
                .append("FROM customer_order_item_detail d LEFT JOIN product_option_choice poc ")
                .append(                            "ON (d.product_option_choice_id = poc.product_option_choice_id), product_option po ")
                .append("WHERE d.customer_order_item_id = ? ")
                .append(      "AND d.product_option_id = po.product_option_id ")
                .append("ORDER BY product_option_id");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            _details = new Vector();
            while ( rs.next() )
            {
                CustomerOrderItemDetail detail = new CustomerOrderItemDetail( rs.getLong("customer_order_item_detail_id"), getDBConfig() );
                detail.loadFromResultSet(rs);
                _details.add(detail);
            }
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Saves the order details.
     *
     * @throws SQLException
     */
    public void saveDetails() throws SQLException
    {
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItemDetail detail = (CustomerOrderItemDetail)_details.get(i);
            detail.setCustomerOrderItemId( getId() );
            detail.save();
        }
    }

    /**
     * Deletes the order item details.
     *
     * @throws SQLException
     */
    public void deleteDetails() throws SQLException
    {
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItemDetail detail = (CustomerOrderItemDetail)_details.get(i);
            detail.delete();
        }
    }

    protected void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE customer_order_item SET customer_order_id = ?, product_id = ?, notes = ?, price = ?, delivery_method_config = ?, quantity = ?, status = ? ")
            .append("WHERE customer_order_item_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong( n++, getCustomerOrderId() );
            pst.setLong( n++, _product.getId() );
            pst.setString( n++, getNotes() );
            pst.setFloat( n++, getPrice() );
            pst.setString( n++, (_deliveryMethod != null ? _deliveryMethod.getConfig() : null) );
            pst.setInt( n++, getQuantity() );
            pst.setInt( n++, getStatus() );
            pst.setLong( n++, getId() );
            pst.executeUpdate();
        }
        finally
        {
            db.disconnect();
        }
        saveDetails();
    }

    protected void insert() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("INSERT INTO customer_order_item ")
            .append(    "(customer_order_id, product_id, notes, price, delivery_method_config, quantity, status, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong( n++, getCustomerOrderId() );
            pst.setLong( n++, _product.getId() );
            pst.setString( n++, getNotes() );
            pst.setFloat( n++, getPrice() );
            pst.setString( n++, (_deliveryMethod != null ? _deliveryMethod.getConfig() : null) );
            pst.setInt( n++, getQuantity() );
            pst.setInt( n++, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
        saveDetails();
    }

    /**
     * Deletes the item from the database and resets the id to -1. This also deletes the details.
     *
     * @throws SQLException if a database error occurs.
     */
    public void delete() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement("DELETE FROM customer_order_item WHERE customer_order_item_id = ?");
            pst.setLong( 1, getId() );
            pst.executeUpdate();
            setId(-1);
        }
        finally
        {
            db.disconnect();
        }
        deleteDetails();
    }

    /**
     * Adds a customer order item detail to this order item.
     *
     * @param detail The detail.
     */
    public void addDetail(CustomerOrderItemDetail detail)
    {
        if (detail != null) _details.add(detail);
    }

    /**
     * Returns the customer order item details associated with this order item.
     *
     * @return Vector
     */
    public Vector getDetails()
    {
        return _details;
    }

    /**
     * Removes all customer order item details associated with this order item.
     */
    public void clearDetails()
    {
        _details.clear();
    }

    /**
     * Returns the customer order item detail with the specified id.
     *
     * @param id The customer order item detail id.
     * @return CustomerOrderItemDetail
     */
    public CustomerOrderItemDetail getDetail(long id)
    {
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItemDetail d = (CustomerOrderItemDetail)_details.get(i);
            if (d.getId() == id) return d;
        }
        return null;
    }

    /**
     * Returns the customer order item detail with the specified product option id.
     *
     * @param id The product option id.
     * @return CustomerOrderItemDetail
     */
    public CustomerOrderItemDetail getDetailByOptionId(long id)
    {
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItemDetail d = (CustomerOrderItemDetail)_details.get(i);
            ProductOption opt = d.getProductOption();
            if (opt != null && opt.getId() == id) return d;
        }
        return null;
    }

    /**
     * Removes a customer order item detail given the id.
     *
     * @param id The customer order item detail.
     */
    public void removeDetail(long id)
    {
        CustomerOrderItemDetail d = getDetail(id);
        removeDetail(d);
    }

    /**
     * Removes the given customer order item detail.
     *
     * @param detail The order item detail.
     */
    public void removeDetail(CustomerOrderItemDetail detail)
    {
        if (detail != null) _details.remove(detail);
    }

    /**
     * Sets the product.
     *
     * @param prod The product.
     */
    public void setProduct(Product p)
    {
        _product = p;
    }

    /**
     * Sets the product id. Calling this method causes the product to be reset completely. If you
     * want the product information loaded, you will need to call initProduct.
     *
     * @param long The product id.
     */
    public void setProductId(long id)
    {
        _product = new Product( id, getDBConfig() );
    }

    /**
     * Initializes the product. This is done automatically when calling init.
     *
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no data for the product.
     */
    public void initProduct() throws SQLException, NoDataException
    {
        _product.init();
    }

    /**
     * Returns the product.
     *
     * @return Product
     */
    public Product getProduct()
    {
        return _product;
    }

    /**
     * Sets the notes.
     *
     * @param String The notes.
     */
    public void setNotes(String notes)
    {
        _notes = notes;
    }

    /**
     * Returns the notes.
     *
     * @return String
     */
    public String getNotes()
    {
        return _notes;
    }

    /**
     * Sets the customerOrderId.
     *
     * @param long The customerOrderId.
     */
    public void setCustomerOrderId(long id)
    {
        _customerOrderId = id;
    }

    /**
     * Returns the customerOrderId.
     *
     * @return long
     */
    public long getCustomerOrderId()
    {
        return _customerOrderId;
    }

    /**
     * Sets the price.
     *
     * @param float The price.
     */
    public void setPrice(float price)
    {
        _price = price;
    }

    /**
     * Returns the price.
     *
     * @return float
     */
    public float getPrice()
    {
        return _price;
    }

    /**
     * Sets the chosen delivery method.
     *
     * @param delivery The delivery method.
     */
    public void setDeliveryMethod(ChosenDeliveryMethod method)
    {
        _deliveryMethod = method;
    }

    /**
     * Returns the chosen delivery method.
     *
     * @return ChosenDeliveryMethod
     */
    public ChosenDeliveryMethod getDeliveryMethod()
    {
        return _deliveryMethod;
    }

    /**
     * Returns the shipping amount based on the chosen delivery method.
     *
     * @return float
     */
    public float getShippingPrice()
    {
        if (_deliveryMethod != null) return _deliveryMethod.getShippingCost();
        else return 0f;
    }

    /**
     * Sets the quantity.
     *
     * @param qty The quantity.
     */
    public void setQuantity(int qty)
    {
        _quantity = qty;
    }

    /**
     * Returns the quantity.
     *
     * @return int
     */
    public int getQuantity()
    {
        return _quantity;
    }

    public String getDescription()
    {
        if ( StringValidation.isNotEmpty(_notes) ) return _notes;
        else return _product.getLabel();
    }

    public float getCost()
    {
        return _price;
    }

    /**
     * Returns a text detailed summary of the order item.
     *
     * @return String
     */
    public String getTextDetailSummary()
    {
        StringBuffer ret = new StringBuffer();
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            CustomerOrderItemDetail detail = (CustomerOrderItemDetail)_details.get(i);
            ret.append( (i <= 0 ? "" : ", ") ).append( detail.getProductOption().getLabel() ).append( (detail.getProductOptionChoice() == null && detail.getNotes() == null ? "" : ": ") );
            ProductOptionChoice choice = detail.getProductOptionChoice();
            if (choice != null && choice.getLabel() != null) ret.append( choice.getLabel() );
            if (detail.getNotes() != null) ret.append(" - ").append( detail.getNotes() );
            if (detail.getPrice() != 0.0F) ret.append(" ").append( (detail.getPrice() <= 0.0F ? "" : "+") ).append( PRICE_FORMAT.format(detail.getPrice()) );
        }

        return ret.toString();
    }

    /**
     * Returns an html detailed summary of the order item. It is returned in an ul html tag with li
     * list items. The css names for decorative purposes will be ul class="order_item_detail_list"
     * and li class="order_item_detail". Each item will have a label span tag identified by
     * class="order_item_detail_label", product option choice span tag identified by
     * class="order_item_detail_choice_label", notes span tag identified by class="order_item_detail_notes",
     * price span tag identified by class="order_item_detail_price", item notes span identified
     * by class="order_item_notes", item notes label identified by class="order_item_notes_label",
     * shipping method label identified by class="order_item_shipping_label", and shipping method
     * identified by class="order_item_shipping".
     *
     * @return String
     */
    public String getHtmlDetailSummary()
    {
        StringBuffer ret = new StringBuffer("<ul class=\"order_item_detail_list\">");
        int size = _details.size();
        for (int i=0; i<size; i++)
        {
            ret.append("<li class=\"order_item_detail\"><span class=\"order_item_detail_label\">");
            CustomerOrderItemDetail detail = (CustomerOrderItemDetail)_details.get(i);
            ret.append( detail.getProductOption().getLabel() ).append("</span>");
            if (detail.getProductOptionChoice() != null || detail.getNotes() != null) ret.append(": ");
            ProductOptionChoice choice = detail.getProductOptionChoice();
            if (choice != null && choice.getLabel() != null) ret.append("<span class=\"order_item_detail_choice_label\">").append( choice.getLabel() ).append("</span>");
            if (detail.getNotes() != null && detail.getProductOptionChoice() != null && detail.getProductOptionChoice().getLabel() != null) ret.append(" - ");
            if (detail.getNotes() != null) ret.append("<span class=\"order_item_detail_notes\">").append( detail.getNotes() ).append("</span>");
            if (detail.getPrice() != 0.0F) ret.append("&nbsp;<span class=\"order_item_detail_price\">").append( (detail.getPrice() > 0.0F ? "+" : "") ).append( PRICE_FORMAT.format(detail.getPrice()) ).append("</span>");
        }
        ret.append("</ul>");
        if (getNotes() != null)
        {
            ret.append("<span class=\"order_item_notes_label\">Notes:</span><span class=\"order_item_notes\">").append( getNotes() ).append("</span><br>");
        }
        ChosenDeliveryMethod method = getDeliveryMethod();
        if (method != null)
        {
            ret.append("<span class=\"order_item_shipping_label\">Shipping Method:</span> <span class=\"order_item_shipping\">").append( method.getDescription() ).append(" - ").append( PRICE_FORMAT.format(method.getShippingCost()) ).append("</span>");
        }

        return ret.toString();
    }
}
