package com.zitego.customer.product;

import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.sql.PreparedStatementSupport;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

/**
 * This class is an extension of product so that we can load a specific customer's
 * product information.
 *
 * @author John Glorioso
 * @version $Id: CustomerProduct.java,v 1.2 2009/05/04 03:37:54 jglorioso Exp $
 */
public class CustomerProduct extends Product implements OrderItem
{
    private long _customerId;
    private long _productId = -1;
    private float _amountPaid = 0f;
    private long _promotionId = -1;

    /**
     * Creates a new CustomerProduct with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerProduct(DBConfig) instead.
     */
    public CustomerProduct(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new CustomerProduct with a DBConfig.
     *
     * @param config The db config to use for querying.
     */
    public CustomerProduct(DBConfig config)
    {
        super(config);
        _customerId = -1;
    }

    /**
     * Creates a new CustomerProduct with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerProduct(long, DBConfig) instead.
     */
    public CustomerProduct(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new CustomerProduct with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public CustomerProduct(long id, DBConfig config)
    {
        super(id, config);
        _customerId = -1;
    }

    /**
     * Creates a new CustomerProduct with a universal identifier, customer id, and a DBHandle.
     *
     * @param id The id.
     * @param customerId The customer id.
     * @param db The db handle to use for querying.
     * @deprecated Use CustomerProduct(String, long, DBConfig) instead.
     */
    public CustomerProduct(String id, long customerId, DBHandle db)
    {
        this( id, customerId, db.getConfig() );
    }

    /**
     * Creates a new CustomerProduct with a universal identifier, customer id, and a DBConfig.
     *
     * @param id The id.
     * @param customerId The customer id.
     * @param config The db config to use for querying.
     */
    public CustomerProduct(String id, long customerId, DBConfig config)
    {
        super(id, config);
        _customerId = customerId;
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT c.customer_product_id, c.product_id, c.customer_id, c.amount_paid, c.promotion_id, ")
            .append(       "c.status_id, c.creation_date, c.last_updated, p.reference_string, p.name, ")
            .append(       "p.description, p.price ")
            .append("FROM customer_product c, product p ")
            .append("WHERE c.product_id = p.product_id ");
        PreparedStatementSupport supp = new PreparedStatementSupport();
        if (getId() > -1)
        {
            sql.append("c.customer_product_id = ?");
            supp.add( getId() );
        }
        else if (_customerId > -1 && getReferenceString() != null)
        {
            sql.append("c.customer_id = ? AND p.reference_string = ?");
            supp.add(_customerId);
            supp.add( getReferenceString() );
        }
        else
        {
            throw new NoDataException("Both id and universal identifier/customer id are null");
        }
        supp.setSql(sql);

        db.connect();
        try
        {
            PreparedStatement pst = supp.bindValues(db);
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such customer product: "+getId()+", customer id: "+_customerId+", universal id: "+getReferenceString() );

            setId( rs.getLong("customer_product_id") );
            _productId = rs.getLong("product_id");
            _customerId = rs.getLong("customer_id");
            _amountPaid = rs.getFloat("amount_paid");
            _promotionId = rs.getLong("promotion_id");
            if ( rs.wasNull() ) _promotionId = -1;
            setStatus( rs.getInt("status_id") );
            setCreationDate( rs.getTimestamp("creation_date") );
            setLastUpdated( rs.getTimestamp("last_updated") );
            setReferenceString( rs.getString("reference_string") );
            setLabel( rs.getString("name") );
            setShortDescription( rs.getString("description") );
            setRegularPrice( rs.getFloat("price") );

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
            .append("UPDATE customer_product SET product_id = ?, customer_id = ?, amount_paid = ?, promotion_id = ?, ")
            .append(       "status_id = ? ")
            .append("WHERE customer_product_id = ?");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong(1, _productId);
            pst.setLong(2, _customerId);
            pst.setFloat(3, _amountPaid);
            if (_promotionId > -1) pst.setLong(4, _promotionId);
            else pst.setNull(4, Types.NUMERIC);
            pst.setInt( 5, getStatus() );
            pst.setLong( 6, getId() );
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
            .append("INSERT INTO customer_product ")
            .append(    "(product_id, customer_id, amount_paid, promotion_id, status_id, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, now())");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong(1, _productId);
            pst.setLong(2, _customerId);
            pst.setFloat(3, _amountPaid);
            if (_promotionId > -1) pst.setLong(4, _promotionId);
            else pst.setNull(4, Types.NUMERIC);
            pst.setInt( 5, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the customerId.
     *
     * @param customerId The customerId.
     */
    public void setCustomerId(long customerId)
    {
        _customerId = customerId;
    }

    /**
     * Returns the customerId.
     *
     * @return long
     */
    public long getCustomerId()
    {
        return _customerId;
    }

    /**
     * Sets the productId.
     *
     * @param productId The productId.
     */
    public void setProductId(long productId)
    {
        _productId = productId;
    }

    /**
     * Returns the productId.
     *
     * @return long
     */
    public long getProductId()
    {
        return _productId;
    }

    /**
     * Sets the amount paid.
     *
     * @param amount The amount paid.
     */
    public void setAmountPaid(float amount)
    {
        _amountPaid = amount;
    }

    /**
     * Returns the amount paid.
     *
     * @return float
     */
    public float getAmountPaid()
    {
        return _amountPaid;
    }

    /**
     * Sets the promotionId.
     *
     * @param promotionId The promotionId.
     */
    public void setPromotionId(long promotionId)
    {
        _promotionId = promotionId;
    }

    /**
     * Returns the promotionId.
     *
     * @return long
     */
    public long getPromotionId()
    {
        return _promotionId;
    }

    public float getCost()
    {
        return getAmountPaid();
    }

    /**
     * Always returns 1 for now.
     *
     * @return int
     */
    public int getQuantity()
    {
        //For now
        return 1;
    }
}