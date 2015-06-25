package com.zitego.customer.creditCard.authnet;

import com.zitego.customer.Customer;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.web.thirdPartyAPI.PostAPIRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * This is a request to authorize.net via the aim post interface. This will send a post
 * and read back the response parsing it to determine whether the charge succeeded.
 *
 * @author John Glorioso
 * @version $Id: AimRequest.java,v 1.2 2008/05/15 18:56:29 jglorioso Exp $
 */
public class AimRequest extends PostAPIRequest
{
    public static int CHARGE = 0;
    public static int PREAUTH = 1;
    public static int PROCESS_PRIOR_PREAUTH = 2;
    public static int CREDIT = 3;
    public static int VOID = 4;
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.00");
    public static final SimpleDateFormat EXP_FORMAT = new SimpleDateFormat("MMyy");
    private float _amount = 0f;
    private String _description;
    private String _invoiceNumber;

    /**
     * Creates a new AimRequest.
     *
     * @param url The url to connect to.
     * @param dupWindowSec The number of seconds between duplicate submissions.
     * @param testing Whether we are testing.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public AimRequest(String url, int dupWindowSec, boolean testing) throws IllegalArgumentException
    {
        super(url);
        addField( "x_duplicate_window", String.valueOf(dupWindowSec) );
        addField( "x_test_request", String.valueOf(testing).toUpperCase() );
        //TO DO - Make this more robust
        addField("x_version", "3.1");
        addField("x_currency_code", "USD");
        addField("x_method", "CC");
        addField("x_delim_data", "TRUE");
        addField("x_delim_char", "|");
    }

    public void setCustomerData(Customer c)
    {
        addField( "x_cust_id", String.valueOf(c.getId()) );
        BillingInformation billing = c.getBillingInfo();
        addField( "x_first_name", billing.getFirstName() );
        addField( "x_last_name", billing.getLastName() );
        if (billing.getCompanyName() != null) addField( "x_company", billing.getCompanyName() );
        addField( "x_address", billing.getAddress1() );
        addField( "x_city", billing.getCity() );
        addField( "x_state", billing.getStateName() );
        addField( "x_zip", billing.getPostalCode() );
        addField( "x_country", billing.getCountryName() );

        ContactInformation contact = c.getContactInfo();
        addField( "x_phone", contact.getPrimaryPhone() );
        if (contact.getFax() != null) addField( "x_fax", billing.getFax() );

        addField( "x_card_num", billing.getCcNumber() );
        addField( "x_exp_date", EXP_FORMAT.format(billing.getExpDate()) );
        if (billing.getCVV2Number() != null) addField( "x_card_code", billing.getCVV2Number() );
    }

    /**
     * Sets the merchant login.
     *
     * @param login The login.
     * @throws IllegalArgumentException if the login is null.
     */
    public void setLogin(String login) throws IllegalArgumentException
    {
        if (login == null) throw new IllegalArgumentException("login cannot be null");
        addField("x_login", login);
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param amt The amount.
     * @throws IllegalArgumentException if the amount is less than .01.
     */
    public void setAmount(float amt) throws IllegalArgumentException
    {
        if (amt < .01) throw new IllegalArgumentException("Amount cannot be less than .01");
        _amount = amt;
        addField("x_amount", PRICE_FORMAT.format(_amount) );
    }

    /**
     * Returns the amount to charge.
     *
     * @return float
     */
    public float getAmount()
    {
        return _amount;
    }

    /**
     * Sets the description of the order.
     *
     * @param desc The description.
     */
    public void setDescription(String desc)
    {
        _description = desc;
        addField("x_description", desc);
    }

    /**
     * Returns the order description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the invoice number of the order.
     *
     * @param num The invoice number.
     */
    public void setInvoiceNumber(String num)
    {
        _invoiceNumber = num;
        addField("x_invoice_num", num);
    }

    /**
     * Returns the invoice number.
     *
     * @return String
     */
    public String getInvoiceNumber()
    {
        return _invoiceNumber;
    }

    /**
     * Sets the type of transaction to perform.
     *
     * @param type The transaction type.
     */
    public void setType(int type)
    {
        String stype = "AUTH_CAPTURE";
        if (type == PREAUTH) stype = "AUTH_ONLY";
        else if (type == PROCESS_PRIOR_PREAUTH) stype = "PRIOR_AUTH_CAPTURE";
        else if (type == CREDIT) stype = "CREDIT";
        else if (type == VOID) stype = "VOID";
        addField("x_type", stype);
    }

    /**
     * Sets the password for credits and authorizations.
     *
     * @param pass The password.
     */
    public void setPassword(String pass)
    {
        if (pass != null) addField("x_password", pass);
    }

    /**
     * Sets the transaction id.
     *
     * @param transId
     */
    public void setTransactionId(String transId)
    {
        if (transId != null) addField("x_trans_id", transId);
    }
}
