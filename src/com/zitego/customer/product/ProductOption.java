package com.zitego.customer.product;

import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.util.Constant;
import com.zitego.util.StatusType;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

/**
 * This class represents an option that is associated with a product. The option has
 * one or more choices associated with it. For example, lets say that a product
 * is a shirt. One option might be size and the choices might be small,
 * medium, and large. Another option might be color and choices might be blue,
 * green, red, etc. Options are not priced, but choices may or may not be.
 *
 * @author John Glorioso
 * @version $Id: ProductOption.java,v 1.4 2011/10/16 17:20:34 jglorioso Exp $
 */
public class ProductOption extends DatabaseEntity
{
    private long _productId = -1;
    private String _label;
    private String _description;
    private boolean _required = false;
    private boolean _isInventoryTracked = false;
    private String _errMsg;
    private String _addlHtml;
    private ProductOptionType _type;
    private int _orderId = 0;
    private Vector _choices = new Vector();

    /**
     * Creates a new ProductOption with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use ProductOption(DBConfig) instead.
     */
    public ProductOption(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new ProductOption with a DBHandle.
     *
     * @param config The db config to use for querying.
     */
    public ProductOption(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new ProductOption with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use ProductOption(long, DBConfig) instead.
     */
    public ProductOption(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new ProductOption with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public ProductOption(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT product_id, label, description, option_type_id, required, is_inventory_tracked, err_msg, addl_html, order_id, status, creation_date, last_updated ")
            .append("FROM product_option ")
            .append("WHERE product_option_id = ?");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such product option: id- " + getId() );
            _productId = rs.getLong("product_id");
            _label = rs.getString("label");
            _description = rs.getString("description");
            _type = ProductOptionType.evaluate( rs.getInt("option_type_id") );
            _required = (rs.getInt("required") == 1);
            _isInventoryTracked = (rs.getInt("is_inventory_tracked") == 1);
            _errMsg = rs.getString("err_msg");
            _orderId = rs.getInt("order_id");
            setStatus( rs.getInt("status") );
            setCreationDate( rs.getTimestamp("creation_date") );
            setLastUpdated( rs.getTimestamp("last_updated") );
        }
        finally
        {
            db.disconnect();
        }
    }

    protected void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE product_option SET product_id = ?, label = ?, option_type_id = ?, ")
            .append(    "description = ?, required = ?, is_inventory_tracked = ?, err_msg = ?, addl_html = ?, order_id = ?, status = ?  ")
            .append("WHERE product_option_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong(n++, _productId);
            pst.setString(n++, _label);
            pst.setInt( n++, _type.getValue() );
            pst.setString(n++, _description);
            pst.setInt( n++, (_required ? 1 : 0) );
            pst.setInt( n++, (_isInventoryTracked ? 1 : 0) );
            pst.setString(n++, _errMsg);
            pst.setString(n++, _addlHtml);
            pst.setInt(n++, _orderId);
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
            .append("INSERT INTO product_option ")
            .append(    "(product_id, label, description, option_type_id, required, is_inventory_tracked, err_msg, addl_html, order_id, status, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong(n++, _productId);
            pst.setString(n++, _label);
            pst.setString(n++, _description);
            pst.setInt( n++, _type.getValue() );
            pst.setInt( n++, (_required ? 1 : 0) );
            pst.setInt( n++, (_isInventoryTracked ? 1 : 0) );
            pst.setString(n++, _errMsg);
            pst.setString(n++, _addlHtml);
            pst.setInt(n++, _orderId);
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
     * Loads the product option choices that belong to this product option in the given order. This will
     * load ProductOptionChoice. Only the label, and price adjustment are loaded.
     *
     * @throws SQLException
     */
    public void loadChoices() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT poc.product_option_choice_id, poc.label, popoc.price_adjustment, order_id ")
                .append("FROM product_option_choice poc ")
                .append("WHERE poc.product_option_id = ? AND poc.status = ? ")
                .append("ORDER BY poc.order_id");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            pst.setInt(2, 1);
            ResultSet rs = pst.executeQuery();

            _choices = new Vector();
            while ( rs.next() )
            {
                ProductOptionChoice o = createProductOptionChoice( rs.getLong("product_option_choice_id"), getDBConfig() );
                o.setProductOptionId( getId() );
                o.setLabel( rs.getString("label") );
                o.setPriceAdjustment( rs.getFloat("price_adjustment") );
                o.setOrder( rs.getInt("order_id") );
                addChoice(o);
            }
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Saves the choices in the product option. It will delete the rows from the
     * database, then re-insert them.
     *
     * @throws SQLException
     */
    public void saveChoiceOrder() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("DELETE FROM product_option_product_option_choice WHERE product_option_id = ?");

            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            pst.executeUpdate();

            pst = db.prepareStatement("UPDATE product_option_choice SET order_id = ? WHERE product_option_choice_id = ?");
            int size = _choices.size();
            for (int i=0; i<size; i++)
            {
                ProductOptionChoice o = (ProductOptionChoice)_choices.get(i);
                pst.setInt( 1, (i+1) );
                pst.setLong( 2, o.getId() );
                pst.executeUpdate();
            }
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the product id.
     *
     * @param id The id.
     */
    public void setProductId(long id)
    {
        _productId = id;
    }

    /**
     * Returns the product id.
     *
     * @return long
     */
    public long getProductId()
    {
        return _productId;
    }

    /**
     * Sets the label.
     *
     * @param label The label.
     */
    public void setLabel(String label)
    {
        _label = label;
    }

    /**
     * Returns the label.
     *
     * @return String
     */
    public String getLabel()
    {
        return _label;
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
     * Sets the option type.
     *
     * @param type The type.
     */
    public void setType(ProductOptionType type)
    {
        _type = type;
    }

    /**
     * Returns the product option type.
     *
     * @return ProductOptionType
     */
    public ProductOptionType getType()
    {
        return _type;
    }

    /**
     * Sets whether this choice is required.
     *
     * @param required The flag.
     */
    public void setRequired(boolean required)
    {
        _required = required;
    }

    /**
     * Returns whether or not this choice is required.
     *
     * @return boolean
     */
    public boolean getRequired()
    {
        return _required;
    }

    /**
     * Sets whether this choice is inventory tracked.
     *
     * @param tracked The flag.
     */
    public void setIsInventoryTracked(boolean tracked)
    {
        _isInventoryTracked = tracked;
    }

    /**
     * Returns whether or not this choice is inventory tracked.
     *
     * @return boolean
     */
    public boolean isInventoryTracked()
    {
        return _isInventoryTracked;
    }

    /**
     * Sets the error message.
     *
     * @param name The error message.
     */
    public void setErrMsg(String err)
    {
        _errMsg = err;
    }

    /**
     * Returns the error message.
     *
     * @return String
     */
    public String getErrMsg()
    {
        return _errMsg;
    }

    /**
     * Sets the additional html.
     *
     * @param html The additional html.
     */
    public void setAdditionalHtml(String addl)
    {
        _addlHtml = addl;
    }

    /**
     * Returns the additional html.
     *
     * @return String
     */
    public String getAdditionalHtml()
    {
        return _addlHtml;
    }

    /**
     * Sets the order.
     *
     * @param order The order.
     */
    public void setOrder(int order)
    {
        _orderId = order;
    }

    /**
     * Returns the order.
     *
     * @return int
     */
    public int getOrder()
    {
        return _orderId;
    }

    /**
     * Adds a choice to this option.
     *
     * @param c The choice to add.
     */
    public void addChoice(ProductOptionChoice c)
    {
        if (c != null) _choices.add(c);
    }

    /**
     * Returns the option choice with the specified id.
     *
     * @param id The choice id.
     * @return ProductOptionChoice
     */
    public ProductOptionChoice getChoice(long id)
    {
        int size = _choices.size();
        for (int i=0; i<size; i++)
        {
            ProductOptionChoice c = (ProductOptionChoice)_choices.get(i);
            if (c.getId() == id) return c;
        }
        return null;
    }

    /**
     * Removes a choice from this option based on the id.
     *
     * @param id The id of choice to remove.
     */
    public void removeChoice(long id)
    {
        ProductOptionChoice c = getChoice(id);
        if (c != null) _choices.remove(c);
    }

    /**
     * Sets the product option choices.
     *
     * @param choices The choices.
     */
    public void setChoices(Vector choices)
    {
        if (choices == null) choices = new Vector();
        _choices = choices;
    }

    /**
     * Returns the choices.
     *
     * @return Vector
     */
    public Vector getChoices()
    {
        return _choices;
    }

    /**
     * Moves a option choice up or down in order based on the id and the direction parameter.
     * A value greater then 0 will be moved up and a number less or equal to 0 will be moved down.
     * If it cannot be moved in the specified direction, nothing will happen.
     *
     * @param id The choice id.
     * @param int The direction.
     */
    public void moveChoice(long id, int dir)
    {
        if (dir > 0) dir = 1;
        else dir = -1;
        ProductOptionChoice c = getChoice(id);
        if (c != null)
        {
            int index = _choices.indexOf(c);
            index += dir;
            if ( index >= 0 && index < _choices.size() )
            {
                _choices.remove(c);
                _choices.insertElementAt(c, index);
            }
        }
    }

    /**
     * Returns all of the product options as a Vector of Constants. The id is the value and
     * the name is the description. If there are no options, then an empty Vector is returned.
     *
     * @param db The database handle to use.
     * @throws SQLException
     */
    public static Vector getAllProductOptions(DBHandle db) throws SQLException
    {
        Vector ret = new Vector();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT product_option_id, name ")
                .append("FROM product_option ")
                .append("WHERE AND status = ? ")
                .append("ORDER BY name");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                Constant c = new Constant( rs.getInt("product_option_id"), rs.getString("name") );
                if ( "".equals(c.getDescription()) ) c.setDescription("[BLANK LABEL]");
                ret.add(c);
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    public Vector getParents() throws SQLException
    {
        Vector ret = new Vector();
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT p.product_id, p.reference_string name, p.label ")
            .append("FROM product_product_option ppo, product p ")
            .append("WHERE ppo.product_option_id = ? AND ppo.product_id = p.product_id");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                Product p = new Product( rs.getLong("product_id"), getDBConfig() );
                p.setName( rs.getString("name") );
                p.setLabel( rs.getString("label") );
                ret.add(p);
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    /**
     * Used to create a product option choice.
     *
     * @param id The product option choice id.
     * @param db The database handle.
     * @deprecated Use createProductOptionChoice(long, DBConfig) instead.
     */
    public ProductOptionChoice createProductOptionChoice(long id, DBHandle db)
    {
        return createProductOptionChoice( id, db.getConfig() );
    }

    public ProductOptionChoice createProductOptionChoice(long id, DBConfig config)
    {
        return new ProductOptionChoice(id, config);
    }
}
