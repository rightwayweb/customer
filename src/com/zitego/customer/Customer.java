package com.zitego.customer;

import com.zitego.customer.creditCard.CreditCardType;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.sql.DatabaseEntity;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This class represents a customer including contact information, billing
 * information, and a list of subscribed products.
 *
 * @author John Glorioso
 * @version $Id: Customer.java,v 1.4 2009/05/04 03:38:17 jglorioso Exp $
 */
public class Customer extends DatabaseEntity
{
    /** The prefix number appended to the customer id to create the account number. */
    public static final String ACCOUNT_NUMBER_PREFIX = "420";
    private String _accountNumber;
    private BillingInformation _billingInfo = new BillingInformation();
    private ContactInformation _contactInfo = new ContactInformation();
    private String _prefix = "zitego.";
    private Hashtable _customFields = new Hashtable();

    /**
     * Creates a new Customer with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use Customer(DBConfig) instead.
     */
    public Customer(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new Customer with a DBConfig.
     *
     * @param config The config to use for querying.
     */
    public Customer(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new Customer with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use Customer(long, DBConfig) instead.
     */
    public Customer(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new Customer with a DBConfig.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public Customer(long id, DBConfig config)
    {
        super(id, config);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT c.prefix, c.first_name, c.last_name, c.suffix, c.company_name, c.email, c.address1, ")
            .append(    "c.address2, c.city, c.state_id, s1.name state_name, c.postal_code, c.country_id, ")
            .append(    "co1.name country_name, c.cc_partial, c.cvv2_number, c.id_token, c.token_password, c.cc_type_id, ")
            .append(    "c.exp_date, c.name_on_card, c.billing_address1, c.billing_address2, c.billing_city, ")
            .append(    "c.billing_state_id, s2.name billing_state_name, c.billing_postal_code, c.billing_country_id, ")
            .append(    "co2.name billing_country_name, c.primary_phone, c.secondary_phone, c.fax, ")
            .append(    "c.status_id, c.creation_date, c.last_updated ")
            .append("FROM customer c LEFT JOIN ").append(_prefix).append("state s2 ON (c.billing_state_id = s2.state_id) ")
            .append(    "LEFT JOIN ").append(_prefix).append("country co2 ON (c.billing_country_id = co2.country_id), ")
            .append(    _prefix).append("state s1, ").append(_prefix).append("country co1 ")
            .append("WHERE c.customer_id = ? AND c.state_id = s1.state_id AND c.country_id = co1.country_id");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() )
            {
                throw new NoDataException( "No such customer: id- " + getId() );
            }
            loadFromResultSet(rs);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Loads the data for this customer from the given result set.
     *
     * @param rs The result set.
     * @throws SQLException if a database error occurs.
     */
    protected void loadFromResultSet(ResultSet rs) throws SQLException
    {
        _contactInfo = new ContactInformation();
        _billingInfo = new BillingInformation();
        _contactInfo.setPrefix( rs.getString("prefix") );
        _contactInfo.setFirstName( rs.getString("first_name") );
        _contactInfo.setLastName( rs.getString("last_name") );
        _contactInfo.setSuffix( rs.getString("suffix") );
        _contactInfo.setCompanyName( rs.getString("company_name") );
        _contactInfo.setEmail( rs.getString("email") );
        _contactInfo.setAddress1( rs.getString("address1") );
        _contactInfo.setAddress2( rs.getString("address2") );
        _contactInfo.setCity( rs.getString("city") );
        _contactInfo.setStateId( rs.getInt("state_id") );
        _contactInfo.setStateName( rs.getString("state_name") );
        _contactInfo.setPostalCode( rs.getString("postal_code") );
        _contactInfo.setCountryId( rs.getInt("country_id") );
        _contactInfo.setCountryName( rs.getString("country_name") );
        _billingInfo.setPartialCcNumber( rs.getString("cc_partial") );
        _billingInfo.setCVV2Number( rs.getString("cvv2_number") );
        _billingInfo.setIdToken( rs.getString("id_token") );
        _billingInfo.setTokenPassword( rs.getString("token_password") );
        _billingInfo.setCcType( CreditCardType.evaluate(rs.getInt("cc_type_id")) );
        _billingInfo.setExpDate( rs.getTimestamp("exp_date") );
        _billingInfo.setNameOnCard( rs.getString("name_on_card") );
        String val = _billingInfo.getNameOnCard();
        if (val == null)
        {
            _billingInfo.setFirstName( _contactInfo.getFirstName() );
            _billingInfo.setLastName( _contactInfo.getLastName() );
        }
        else
        {
            _billingInfo.setFirstAndLastFromNameOnCard();
        }
        _billingInfo.setAddress1( rs.getString("billing_address1") );
        _billingInfo.setAddress2( rs.getString("billing_address2") );
        _billingInfo.setCity( rs.getString("billing_city") );
        _billingInfo.setStateId( rs.getInt("billing_state_id") );
        _billingInfo.setStateName( rs.getString("billing_state_name") );
        _billingInfo.setPostalCode( rs.getString("billing_postal_code") );
        _billingInfo.setCountryId( rs.getInt("billing_country_id") );
        _billingInfo.setCountryName( rs.getString("billing_country_name") );
        _contactInfo.setPrimaryPhone( rs.getString("primary_phone") );
        _contactInfo.setSecondaryPhone( rs.getString("secondary_phone") );
        _billingInfo.setPrimaryPhone( _contactInfo.getPrimaryPhone() );
        _billingInfo.setSecondaryPhone( _contactInfo.getSecondaryPhone() );
        _contactInfo.setFax( rs.getString("fax") );
        setStatus( rs.getInt("status_id") );
        setCreationDate( rs.getTimestamp("creation_date") );
        setLastUpdated( rs.getTimestamp("last_updated") );
    }

    protected void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE customer SET prefix = ?, first_name = ?, last_name = ?, suffix = ?, ")
            .append(    "company_name = ?, email = ?, address1 = ?, address2 = ?, city = ?, state_id = ?, ")
            .append(    "postal_code = ?, country_id = ?, cc_partial = ?, cvv2_number = ?, id_token = ?, token_password = ?, ")
            .append(    "cc_type_id = ?, exp_date = ?, name_on_card = ?, billing_address1 = ?, billing_address2 = ?, ")
            .append(    "billing_city = ?, billing_state_id = ?, billing_postal_code = ?, billing_country_id = ?, ")
            .append(    "primary_phone = ?, secondary_phone = ?, fax = ?, status_id = ? ")
            .append("WHERE customer_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setString( n++, _contactInfo.getPrefix() );
            pst.setString( n++, _contactInfo.getFirstName() );
            pst.setString( n++, _contactInfo.getLastName() );
            pst.setString( n++, _contactInfo.getSuffix() );
            pst.setString( n++, _contactInfo.getCompanyName() );
            pst.setString( n++, _contactInfo.getEmail() );
            pst.setString( n++, _contactInfo.getAddress1() );
            pst.setString( n++, _contactInfo.getAddress2() );
            pst.setString( n++, _contactInfo.getCity() );
            pst.setInt( n++, _contactInfo.getStateId() );
            pst.setString( n++, _contactInfo.getPostalCode() );
            pst.setInt( n++, _contactInfo.getCountryId() );
            //TO DO - encode/decode cc num
            pst.setString( n++, _billingInfo.getPartialCcNumber() );
            pst.setString( n++, _billingInfo.getCVV2Number() );
            pst.setString( n++, _billingInfo.getIdToken() );
            pst.setString( n++, _billingInfo.getTokenPassword() );
            if (_billingInfo.getCcType() != null) pst.setInt( n++, _billingInfo.getCcType().getValue() );
            else pst.setNull(n++, Types.NUMERIC);
            if (_billingInfo.getExpDate() != null) pst.setTimestamp( n++, new Timestamp(_billingInfo.getExpDate().getTime()) );
            else pst.setNull(n++, Types.TIMESTAMP);
            pst.setString( n++, _billingInfo.getNameOnCard() );
            pst.setString( n++, _billingInfo.getAddress1() );
            pst.setString( n++, _billingInfo.getAddress2() );
            pst.setString( n++, _billingInfo.getCity() );
            pst.setInt( n++, _billingInfo.getStateId() );
            pst.setString( n++, _billingInfo.getPostalCode() );
            pst.setInt( n++, _billingInfo.getCountryId() );
            pst.setString( n++, _contactInfo.getPrimaryPhone() );
            pst.setString( n++, _contactInfo.getSecondaryPhone() );
            pst.setString( n++, _contactInfo.getFax() );
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
            .append("INSERT INTO customer ")
            .append(    "(prefix, first_name, last_name, suffix, company_name, email, address1, ")
            .append(     "address2, city, state_id, postal_code, country_id, cc_partial, cvv2_number, id_token, token_password, ")
            .append(     "cc_type_id, exp_date, name_on_card, billing_address1, billing_address2, ")
            .append(     "billing_city, billing_state_id, billing_postal_code, billing_country_id, ")
            .append(     "primary_phone, secondary_phone, fax, status_id, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setString( n++, _contactInfo.getPrefix() );
            pst.setString( n++, _contactInfo.getFirstName() );
            pst.setString( n++, _contactInfo.getLastName() );
            pst.setString( n++, _contactInfo.getSuffix() );
            pst.setString( n++, _contactInfo.getCompanyName() );
            pst.setString( n++, _contactInfo.getEmail() );
            pst.setString( n++, _contactInfo.getAddress1() );
            pst.setString( n++, _contactInfo.getAddress2() );
            pst.setString( n++, _contactInfo.getCity() );
            pst.setInt( n++, _contactInfo.getStateId() );
            pst.setString( n++, _contactInfo.getPostalCode() );
            pst.setInt( n++, _contactInfo.getCountryId() );
            pst.setString( n++, _billingInfo.getPartialCcNumber() );
            pst.setString( n++, _billingInfo.getCVV2Number() );
            pst.setString( n++, _billingInfo.getIdToken() );
            pst.setString( n++, _billingInfo.getTokenPassword() );
            if (_billingInfo.getCcType() != null) pst.setInt( n++, _billingInfo.getCcType().getValue() );
            else pst.setNull(n++, Types.NUMERIC);
            if (_billingInfo.getExpDate() != null) pst.setTimestamp( n++, new Timestamp(_billingInfo.getExpDate().getTime()) );
            else pst.setNull(n++, Types.TIMESTAMP);
            pst.setString( n++, _billingInfo.getNameOnCard() );
            pst.setString( n++, _billingInfo.getAddress1() );
            pst.setString( n++, _billingInfo.getAddress2() );
            pst.setString( n++, _billingInfo.getCity() );
            pst.setInt( n++, _billingInfo.getStateId() );
            pst.setString( n++, _billingInfo.getPostalCode() );
            pst.setInt( n++, _billingInfo.getCountryId() );
            pst.setString( n++, _contactInfo.getPrimaryPhone() );
            pst.setString( n++, _contactInfo.getSecondaryPhone() );
            pst.setString( n++, _contactInfo.getFax() );
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
     * Sets the billingInfo.
     *
     * @param billingInfo The billingInfo.
     */
    public void setBillingInfo(BillingInformation billingInfo)
    {
        _billingInfo = billingInfo;
    }

    /**
     * Returns the billingInfo.
     *
     * @return BillingInformation
     */
    public BillingInformation getBillingInfo()
    {
        return _billingInfo;
    }

    /**
     * Sets the contactInfo.
     *
     * @param contactInfo The contactInfo.
     */
    public void setContactInfo(ContactInformation contactInfo)
    {
        _contactInfo = contactInfo;
    }

    /**
     * Returns the contactInfo.
     *
     * @return ContactInformation
     */
    public ContactInformation getContactInfo()
    {
        return _contactInfo;
    }

    /**
     * Sets the table prefix to use when querying the state and country table.
     * By default, the prefix is "zitego.". If the prefix is set to null, none
     * will be used.
     *
     * @param prefix The prefix.
     */
    public void setStateCountryPrefix(String prefix)
    {
        if (prefix == null) prefix = "";
        _prefix = prefix;
    }

    /**
     * Overrides set id to set the account number.
     *
     * @param id The id
     */
    public void setId(long id)
    {
        super.setId(id);
        _accountNumber = null;
    }

    /**
     * Returns the account number.
     *
     * @return String
     */
    public String getAccountNumber()
    {
        if (_accountNumber == null) _accountNumber = ACCOUNT_NUMBER_PREFIX + getId();
        return _accountNumber;
    }

    /**
     * Sets a given custom field by name.
     *
     * @param name The name of the custom field.
     * @param val The value of the custom field.
     * @throws IllegalArgumentException if the name is null.
     */
    public void setCustomField(String name, String val) throws IllegalArgumentException
    {
        if (name == null) throw new IllegalArgumentException("Custom field name cannot be null");
        if (val != null) _customFields.put(name, val);
        else _customFields.remove(name);
    }

    /**
     * Returns the custom field by name.
     *
     * @param name The custom field to return.
     * @return Object
     */
    public Object getCustomField(String name)
    {
        if (name != null) return _customFields.get(name);
        else return null;
    }
}
