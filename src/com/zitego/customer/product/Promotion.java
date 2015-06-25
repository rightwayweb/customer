package com.zitego.customer.product;

import com.zitego.sql.DBHandle;
import com.zitego.sql.NoDataException;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.DBConfig;
import com.zitego.sql.PreparedStatementSupport;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Random;
import java.util.Date;

/**
 * <p>This class is a representation of a promotion on a product feature. A promotion
 * has a name, description, a generated code, a definition, start date, end date,
 * and the usual status, creation date, and last updated.</p>
 *
 * <p>The definition tells how to apply the discount and for how long it is good for
 * after their first purchase or in other words how many times they can use the promo.
 * The format of the definition is (in regular expression terminology):
 * ^(\$|%)(\d*\.?\d+):?(\d+)?$ In other words, either a $ or % to denote absolute or
 * percentage discount. A numerical value to denote the discount amount. Followed by an
 * optional number of months length. For example, a definition of
 * $5 would mean a one time discount of five dollars. %5 means a one time discount of
 * five percent. The time length is only really applicable toward subscriptions. For
 * example, a code of %100:1 means 100% off for one month (or first month free).
 * Including the colon, but not specifying a time length means indefinite.</p>
 *
 * TO DO - make this more generic to be able to apply to number of products, such as
 *         buy one get one free, etc. See http://www.cartmanager.net/administration/howto.php3?goto=CouponInfo
 *         for examples
 *
 * @author John Glorioso
 * @version $Id: Promotion.java,v 1.2 2009/05/04 03:36:16 jglorioso Exp $
 */
public class Promotion extends DatabaseEntity
{
    private String _name;
    private String _description;
    private String _code;
    private PromoDef _definition;
    private Date _startDate;
    private Date _endDate;
    private static final int[][] PROMO_RANGES =
    {
        { 48, 57 },
        { 65, 90 }
    };

    public static void main(String[] args) throws Exception
    {
        //create with an actual db handle or null pointer will occur
        Promotion p = new Promotion( (DBConfig)null );
        System.out.println("code="+p.getCode());
        p.setDefinition(args[0]);
        Date time = null;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        if (args.length > 2) time = format.parse(args[2]);
        System.out.println("applied to: "+args[1]+", with time: "+time+" = "+p.applyPromotion(Float.parseFloat(args[1]), time));
    }

    /**
     * Creates a new Promotion with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use Promotion(DBConfig) instead.
     */
    public Promotion(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new Promotion with a DBConfig.
     *
     * @param config The db config to use for querying.
     */
    public Promotion(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new Promotion with an id and DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use Promotion(long, DBConfig) instead.
     */
    public Promotion(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new Promotion with an id and DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public Promotion(long id, DBConfig config)
    {
        super(id, config);
    }

    /**
     * Creates a new Promotion with a code and DBHandle.
     *
     * @param code The code.
     * @param db The db handle to use for querying.
     * @deprecated Use Promotion(String, DBConfig) instead.
     */
    public Promotion(String code, DBHandle db)
    {
        this(code, db.getConfig() );
    }

    /**
     * Creates a new Promotion with a code and DBConfig.
     *
     * @param code The code.
     * @param config The db config to use for querying.
     */
    public Promotion(String code, DBConfig config)
    {
        super(config);
        _code = code;
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT promotion_id, name, description, code, definition, start_date, end_date, ")
            .append(       "status_id, creation_date, last_updated ")
            .append("FROM promotion ")
            .append("WHERE ");
        PreparedStatementSupport support = new PreparedStatementSupport();
        if (getId() > -1)
        {
            sql.append("promotion_id = ?");
            support.add( getId() );
        }
        else
        {
            sql.append("code = ?");
            support.add(_code);
        }
        support.setSql(sql);

        db.connect();
        try
        {
            ResultSet rs = support.bindValues(db).executeQuery();

            if ( !rs.next() )
            {
                throw new NoDataException( "No such promotion: id-" + getId() + ", code-" + _code );
            }

            _name = rs.getString("name");
            _description = rs.getString("description");
            _code = rs.getString("code");
            _definition = new PromoDef( rs.getString("definition") );
            _startDate = rs.getTimestamp("start_date");
            _endDate = rs.getTimestamp("end_date");
            setStatus( rs.getInt("status_id") );
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
            .append("UPDATE promotion SET name = ?, description = ?, code = ?, definition = ?, ")
            .append(       "start_date = ?, end_date = ?, status_id = ? ")
            .append("WHERE promotion_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setString(1, _name);
            pst.setString(2, _description);
            pst.setString(3, _code);
            pst.setString( 4, getDefinition() );
            pst.setTimestamp( 5, new Timestamp(_startDate.getTime()) );
            if (_endDate != null) pst.setTimestamp( 6, new Timestamp(_endDate.getTime()) );
            else pst.setNull(6, Types.TIMESTAMP);
            pst.setInt(7, getStatus() );
            pst.setLong( 8, getId() );
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
            .append("INSERT INTO promotion ")
            .append(    "(name, description, code, definition, start_date, end_date, status_id, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, now())");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setString(1, _name);
            pst.setString(2, _description);
            pst.setString(3, _code);
            pst.setString( 4, getDefinition() );
            pst.setTimestamp( 5, new Timestamp(_startDate.getTime()) );
            if (_endDate != null) pst.setTimestamp( 6, new Timestamp(_endDate.getTime()) );
            else pst.setNull(6, Types.TIMESTAMP);
            pst.setInt(7, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the name.
     *
     * @param name The name.
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the name.
     *
     * @return String
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the description.
     *
     * @param description The description.
     */
    public void setDescription(String description)
    {
        _description = description;
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
     * Sets the code. This is not recommended. Calling getCode() auto-generates a code.
     * The only time that you want to set it is if the generated one is not sufficient
     * for some reason. Note that save() needs to be called once the code is set or
     * generated in order for it to stick. (duh) It is recommended to call isCodeUnique
     * in order to determine that this code is not already being used.
     *
     * @param code The code.
     */
    public void setCode(String code)
    {
        _code = code;
    }

    /**
     * Returns the code. If the code is not yet set, it will auto-generate a unique 6 character
     * promotion code. Codes are typically all uppercase and numeric.
     *
     * @return String
     * @throws SQLException if an error occurs checking the code generated against the database.
     */
    public String getCode() throws SQLException
    {
        if (_code == null)
        {
            boolean codeIsUnique = false;
            while (!codeIsUnique)
            {
                Random rand = new java.util.Random();
                //Make all numeric (48-57), upper case (65-90), or lower case (97-122)
                byte[] code = new byte[6];
                int index = 0;
                //First get a range
                for (int i=0; i<code.length; i++)
                {
                    index = (int)(rand.nextFloat()*PROMO_RANGES.length);
                    code[i] = (byte)
                    (
                        ( (int)(rand.nextFloat()*(PROMO_RANGES[index][1]-PROMO_RANGES[index][0]+1)) ) + PROMO_RANGES[index][0]
                    );
                }
                _code = new String(code);
                codeIsUnique = isCodeUnique(_code);
            }
        }
        return _code;
    }

    /**
     * Checks to see if the given code is unique in the database. null returns false.
     *
     * @param code The code to check.
     * @return boolean
     * @throws SQLException if an error occurs checking.
     */
    public boolean isCodeUnique(String code) throws SQLException
    {
        if (code == null) return false;
        boolean ret = false;
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement("SELECT promotion_id FROM promotion WHERE code = ?");
            pst.setString(1, code);
            ResultSet rs = pst.executeQuery();
            ret = !rs.next();
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    /**
     * Sets the definition. See the class notes for information about the definition. If
     * the provided definition is null or not valid, then an IllegalArgumentException is
     * thrown.
     *
     * @param def The definition.
     * @throws IllegalArgumentException if the definition is invalid.
     */
    public void setDefinition(String def) throws IllegalArgumentException
    {
        _definition = new PromoDef(def);
    }

    /**
     * Returns the definition.
     *
     * @return String
     */
    public String getDefinition()
    {
        return (_definition != null ? _definition.toString() : null);
    }

    /**
     * Sets the startDate.
     *
     * @param startDate The startDate.
     */
    public void setStartDate(Date startDate)
    {
        _startDate = startDate;
    }

    /**
     * Returns the startDate.
     *
     * @return Date
     */
    public Date getStartDate()
    {
        return _startDate;
    }

    /**
     * Sets the endDate.
     *
     * @param endDate The endDate.
     */
    public void setEndDate(Date endDate)
    {
        _endDate = endDate;
    }

    /**
     * Returns the endDate.
     *
     * @return Date
     */
    public Date getEndDate()
    {
        return _endDate;
    }

    /**
     * Applies the promotion to the given price taking into the consideration the first
     * transaction date using this promotion and returns the result. If the transaction
     * date is null, then it means this is the first transaction.
     *
     * @param price The price to apply to.
     * @param firstTrans The date of the first transaction using this promotion.
     * @return float
     */
    public float applyPromotion(float price, Date firstTrans)
    {
        return _definition.applyPromotion(price, firstTrans);
    }

    private class PromoDef
    {
        private String _textDef;
        private boolean _isPercentage = false;
        private float _amtToApply = 0f;
        private boolean _isIndefinite = false;
        private int _months = 0;

        private PromoDef(String def) throws IllegalArgumentException
        {
            if ( def == null || !def.matches("^(\\$|%)(\\d*\\.?\\d+):?(\\d+)?$") )
            {
                throw new IllegalArgumentException("Provided definition syntax is invalid");
            }
            parseDef(def);
        }

        private void parseDef(String def)
        {
            if (def == null || def.length() == 0) return;
            _textDef = def;
            _isPercentage = (def.charAt(0) == '%');
            int index = def.indexOf(":");
            if (index > -1)
            {
                _amtToApply = Float.parseFloat( def.substring(1, index) );
                String time = def.substring(index+1);
                //See if indefinite
                int len = time.length();
                if (len > 0) _months = Integer.parseInt(time);
                else _isIndefinite = true;
            }
            else
            {
                _amtToApply = Float.parseFloat( def.substring(1) );
            }
            if (_isPercentage) _amtToApply = _amtToApply/100;
        }

        private float applyPromotion(float price, Date firstTrans)
        {
            //See if the promotion gets applied.
            boolean apply = false;
            if (firstTrans == null || _isIndefinite)
            {
                apply = true;
            }
            else
            {
                Calendar cal = new GregorianCalendar();
                //Set to midnight because time is unimportant. Only the day matters. If the time were taken
                //into consideration, a first transaction that occurred n months ago before the time of day
                //this is executed will falsely grant the promotion.
                //Ex: #months=1 firstTrans=1/15/2000 03:45:34, current=2/15/2000 05:22:56 (substracting one
                //    month from current causes firstTrans to still appear before it even though 1 month has
                //    passed.
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                //Subtract the months (this works for one time cause months is 0)
                cal.add(Calendar.MONTH, _months*-1);
                apply = ( cal.getTime().before(firstTrans) );
            }
            if (apply)
            {
                if (_isPercentage) price -= price * _amtToApply;
                else price -= _amtToApply;
                if (price < 0) price = 0;
            }
            return price;
        }
    }
}