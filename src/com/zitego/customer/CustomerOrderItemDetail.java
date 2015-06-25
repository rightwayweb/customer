package com.zitego.customer;

import com.zitego.customer.product.ProductOption;
import com.zitego.customer.product.ProductOptionChoice;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.NoDataException;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

/**
 * This class represents an option in a customer order item. For example, if the item ordered was a shirt, then
 * one of the order item details might be the size. Another might be the color. The customer order item detail
 * CustomerOrderItem extends the DatabaseEntityClass and has a product option and an optional product option choice
 * property that links it to the actual data.
 *
 * @author John Glorioso
 * @version $Id: CustomerOrderItemDetail.java,v 1.3 2011/10/16 17:20:32 jglorioso Exp $
 */
public class CustomerOrderItemDetail extends DatabaseEntity
{
    private long _customerOrderItemId = -1;
    private ProductOption _productOption;
    private ProductOptionChoice _productOptionChoice;
    private String _notes;
    private float _price = 0f;

    /**
     * Creates a new customer order item detail.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerOrderItemDetail(DBConfig) instead.
     */
    public CustomerOrderItemDetail(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new customer order item detail.
     *
     * @param config The db config to use for querying.
     */
    public CustomerOrderItemDetail(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new CustomerOrderItemDetail with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerOrderItemDetail(long, DBConfig) instead.
     */
    public CustomerOrderItemDetail(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new CustomerOrderItemDetail with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public CustomerOrderItemDetail(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT d.customer_order_item_id, po.product_option_id, po.label po_label, po.description po_desc, ")
            .append(       "po.option_type_id, po.required, po.is_inventory_tracked, po.status po_status, ")
            .append(       "po.creation_date po_created, po.last_updated po_updated, poc.product_option_choice_id, ")
            .append(       "poc.label poc_label, poc.value poc_value, poc.status poc_status, poc.creation_date poc_created, ")
            .append(       "poc.last_updated poc_updated, d.notes, d.price, d.status, d.creation_date, d.last_updated ")
            .append("FROM customer_order_item_detail d LEFT JOIN product_option_choice poc ")
            .append(                            "ON (d.product_option_choice_id = poc.product_option_choice_id), product_option po ")
            .append("WHERE d.customer_order_item_detail_id = ? ")
            .append(      "AND d.product_option_id = po.product_option_id");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such customer order item detail: id-" + getId() );

            loadFromResultSet(rs);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Loads the data for this order item from the given result set.
     *
     * @throws SQLException
     */
    public void loadFromResultSet(ResultSet rs) throws SQLException
    {
        _customerOrderItemId = rs.getLong("customer_order_item_id");
        _productOption = new ProductOption( rs.getLong("product_option_id"), getDBConfig() );
        _productOption.setLabel( rs.getString("po_label") );
        _productOption.setDescription( rs.getString("po_desc") );
        _productOption.setRequired( (rs.getInt("required") == 1) );
        _productOption.setIsInventoryTracked( (rs.getInt("is_inventory_tracked") == 1) );
        _productOption.setStatus( rs.getInt("po_status") );
        _productOption.setCreationDate( rs.getTimestamp("po_created") );
        _productOption.setLastUpdated( rs.getTimestamp("po_updated") );
        long id = rs.getLong("product_option_choice_id");
        _productOptionChoice = null;
        if ( !rs.wasNull() )
        {
            _productOptionChoice = new ProductOptionChoice( id, getDBConfig() );
            _productOptionChoice.setLabel( rs.getString("poc_label") );
            _productOptionChoice.setValue( rs.getString("poc_value") );
            _productOptionChoice.setStatus( rs.getInt("poc_status") );
            _productOptionChoice.setCreationDate( rs.getTimestamp("poc_created") );
            _productOptionChoice.setLastUpdated( rs.getTimestamp("poc_updated") );
        }
        setNotes( rs.getString("notes") );
        setPrice( rs.getFloat("price") );
        setStatus( rs.getInt("status") );
        setCreationDate( rs.getTimestamp("creation_date") );
        setLastUpdated( rs.getTimestamp("last_updated") );
    }

    public void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE customer_order_item_detail SET customer_order_item_id = ?, product_option_id = ?, product_option_choice_id = ?, ")
            .append(       "notes = ?, price = ?, status = ? ")
            .append("WHERE customer_order_item_detail_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong( n++, getCustomerOrderItemId() );
            pst.setLong( n++, _productOption.getId() );
            if (_productOptionChoice != null) pst.setLong( n++, _productOptionChoice.getId() );
            else pst.setNull( n++, Types.NUMERIC );
            pst.setString( n++, getNotes() );
            pst.setFloat( n++, getPrice() );
            pst.setInt( n++, getStatus() );
            pst.setLong( n++, getId() );
            pst.executeUpdate();
        }
        finally
        {
            db.disconnect();
        }
    }

    protected void insert() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("INSERT INTO customer_order_item_detail ")
            .append(    "(customer_order_item_id, product_option_id, product_option_choice_id, notes, price, status, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong( n++, getCustomerOrderItemId() );
            pst.setLong( n++, _productOption.getId() );
            if (_productOptionChoice != null) pst.setLong( n++, _productOptionChoice.getId() );
            else pst.setNull( n++, Types.NUMERIC );
            pst.setString( n++, getNotes() );
            pst.setFloat( n++, getPrice() );
            pst.setInt( n++, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Deletes the item detail from the database and resets the id to -1.
     *
     * @throws SQLException if a database error occurs.
     */
    public void delete() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement("DELETE FROM customer_order_item_detail WHERE customer_order_item_detail_id = ?");
            pst.setLong( 1, getId() );
            pst.executeUpdate();
            setId(-1);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the customer order item id.
     *
     * @param id The customer order item id.
     */
    public void setCustomerOrderItemId(long id)
    {
        _customerOrderItemId = id;
    }

    /**
     * Returns the customer order item id.
     *
     * @return long
     */
    public long getCustomerOrderItemId()
    {
        return _customerOrderItemId;
    }

    /**
     * Sets the product option id. Calling this method causes the product option to be reset completely. If you
     * want the product option information loaded, you will need to call initProductOption.
     *
     * @param id The product option id.
     */
    public void setProductOptionId(long id)
    {
        _productOption = new ProductOption( id, getDBConfig() );
    }

    /**
     * Initializes the product option. This is done automatically when calling init.
     *
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no data for the product.
     */
    public void initProductOption() throws SQLException, NoDataException
    {
        _productOption.init();
    }

    /**
     * Returns the product option.
     *
     * @return ProductOption
     */
    public ProductOption getProductOption()
    {
        return _productOption;
    }

    /**
     * Sets the product option choice id. Calling this method causes the product option choice to be reset completely. If you
     * want the product option choice information loaded, you will need to call initProductOptionChoice.
     *
     * @param id The product option choice id.
     */
    public void setProductOptionChoiceId(long id)
    {
        _productOptionChoice = new ProductOptionChoice( id, getDBConfig() );
    }

    /**
     * Initializes the product option choice. This is done automatically when calling init.
     *
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no data for the product.
     */
    public void initProductOptionChoice() throws SQLException, NoDataException
    {
        _productOptionChoice.init();
    }

    /**
     * Returns the product option choice.
     *
     * @return ProductOption
     */
    public ProductOptionChoice getProductOptionChoice()
    {
        return _productOptionChoice;
    }

    /**
     * Sets the price.
     *
     * @param price The price.
     */
    public void setPrice(float price)
    {
        _price = price;
    }

    /**
     * Returns the price.
     *
     * @return long
     */
    public float getPrice()
    {
        return _price;
    }

    /**
     * Sets the notes.
     *
     * @param notes The notes.
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
}