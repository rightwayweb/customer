package com.zitego.customer.product;

import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.util.Constant;
import com.zitego.util.StatusType;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * This class represents a choice that is part of a set of a product option. For example,
 * lets say that a product is a shirt. One option might be size and the choices might be small,
 * medium, and large. Another option might be color and choices might be blue,
 * green, red, etc. Options are not priced, but choices may or may not be.
 *
 * @author John Glorioso
 * @version $Id: ProductOptionChoice.java,v 1.3 2011/10/16 17:20:34 jglorioso Exp $
 */
public class ProductOptionChoice extends DatabaseEntity
{
    private long _productOptionId = -1;
    private String _label;
    private String _value;
    private float _priceAdjustment = 0f;
    private int _orderId = 0;

    /**
     * Creates a new ProductOptionChoice with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use ProductOptionChoice(DBConfig) instead.
     */
    public ProductOptionChoice(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new ProductOptionChoice with a DBConfig.
     *
     * @param db The db handle to use for querying.
     */
    public ProductOptionChoice(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new ProductOptionChoice with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use ProductOptionChoice(long, DBHandle) instead.
     */
    public ProductOptionChoice(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new ProductOptionChoice with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public ProductOptionChoice(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT product_option_id, label, value, price_adjustment, order_id, status, creation_date, last_updated ")
            .append("FROM product_option_choice ")
            .append("WHERE product_option_choice_id = ?");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such product option choice: id- " + getId() );
            _productOptionId = rs.getLong("product_option_id");
            _label = rs.getString("label");
            _value = rs.getString("value");
            _priceAdjustment = rs.getFloat("price_adjustment");
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
            .append("UPDATE product_option_choice SET product_option_id = ?, label = ?, value = ?, price_adjustment = ?, order_id = ?, status = ?  ")
            .append("WHERE product_option_choice_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong(n++, _productOptionId);
            pst.setString(n++, _label);
            pst.setString(n++, _value);
            pst.setFloat(n++, _priceAdjustment);
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
            .append("INSERT INTO product_option_choice ")
            .append(    "(product_option_id, label, value, price_adjustment, order_id, status, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setLong(n++, _productOptionId);
            pst.setString(n++, _label);
            pst.setString(n++, _value);
            pst.setFloat(n++, _priceAdjustment);
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
     * Sets the value.
     *
     * @param value The value.
     */
    public void setValue(String value)
    {
        _value = value;
    }

    /**
     * Returns the value.
     *
     * @return String
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * Returns all of the product option choices as a Vector of Constants.
     * The id is the value and the name is the description. If there are no
     * choices, then an empty Vector is returned.
     *
     * @param db The database handle to use.
     * @throws SQLException
     */
    public static Vector getAllChoices(DBHandle db) throws SQLException
    {
        Vector ret = new Vector();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT product_option_choice_id, label ")
                .append("FROM product_option_choice ")
                .append("WHERE status = ? ")
                .append("ORDER BY name");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                Constant c = new Constant( rs.getInt("product_option_choice_id"), rs.getString("label") );
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
            .append("SELECT po.product_option_id, po.label ")
            .append("FROM product_option_choice poc, product_option po ")
            .append("WHERE poc.product_option_choice_id = ? AND poc.product_option_id = po.product_option_id ");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                ProductOption po = new ProductOption( rs.getLong("product_option_id"), getDBConfig() );
                po.setLabel( rs.getString("label") );
                ret.add(po);
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    public void setProductOptionId(long id)
    {
        _productOptionId = id;
    }

    public long getProductOptionId()
    {
        return _productOptionId;
    }

    /**
     * Sets the price adjustment.
     *
     * @param adj The adjustment.
     */
    public void setPriceAdjustment(float adj)
    {
        _priceAdjustment = adj;
    }

    /**
     * Returns the price adjustment.
     *
     * @return float
     */
    public float getPriceAdjustment()
    {
        return _priceAdjustment;
    }

    public void setOrder(int order)
    {
        _orderId = order;
    }

    public int getOrder()
    {
        return _orderId;
    }
}