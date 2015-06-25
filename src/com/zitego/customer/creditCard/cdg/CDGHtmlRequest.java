package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.Customer;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.web.thirdPartyAPI.PostAPIRequest;
import com.zitego.web.thirdPartyAPI.APIRequest;
import java.text.SimpleDateFormat;
import java.net.HttpURLConnection;
import java.io.IOException;

/**
 * This is a request to CDG via the html post interface. This will send a post
 * and read back the response parsing it to determine whether the charge succeeded.
 *
 * @author John Glorioso
 * @version $Id: CDGHtmlRequest.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CDGHtmlRequest extends PostAPIRequest implements CDGRequest
{
    public static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMMM");
    private float _total = 0f;
    private int _counter = 0;

    /**
     * Creates a new CDGHtmlRequest.
     *
     * @param url The url to connect to.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public CDGHtmlRequest(String url) throws IllegalArgumentException
    {
        super(url);
        addField("lookup", "authcode");
    }

    /**
     * Does nothing. There is no registered customer with the html interface.
     *
     * @param code Not used.
     * @param password Not used.
     */
    public void setRegisteredCustomerData(String code, String password) throws IllegalArgumentException { }

    public void setCustomerData(Customer c)
    {
        BillingInformation billing = c.getBillingInfo();
        addField( "first_name", billing.getFirstName() );
        addField( "last_name", billing.getLastName() );
        addField( "address", billing.getAddress1() );
        addField( "city", billing.getCity() );
        addField( "state", billing.getStateName() );
        addField( "zip", billing.getPostalCode() );
        addField( "country", billing.getCountryName() );
        
        ContactInformation contact = c.getContactInfo();
        addField( "phone", contact.getPrimaryPhone() );
        addField( "email", contact.getEmail() );
        addField( "sfname", contact.getFirstName() );
        addField( "slname", contact.getLastName() );
        addField( "saddr", contact.getAddress1() );
        addField( "scity", contact.getCity() );
        addField( "sstate", contact.getStateName() );
        addField( "szip", contact.getPostalCode() );
        addField( "sctry", contact.getCountryName() );
        
        addField( "ccnum", billing.getCcNumber() );
        addField( "ccmo", MONTH_FORMAT.format(billing.getExpDate()) );
        addField( "ccyr", CDGRequest.YEAR_FORMAT.format(billing.getExpDate()) );
    }

    /**
     * Sets the CDG vendor id (mid).
     *
     * @param vendor The id.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorId(String vendor) throws IllegalArgumentException
    {
        addField("vendor_id", vendor);
    }

    /**
     * Not used.
     *
     * @param notused not used.
     * @throws IllegalArgumentException
     */
    public void setVendorPassword(String notused) throws IllegalArgumentException { }

    /**
     * Not used.
     *
     * @param homePage not used.
     * @throws IllegalArgumentException
     */
    public void setHomePage(String homePage) throws IllegalArgumentException
    {
        addField("ret_addr", homePage);
    }

    /**
     * Not used.
     *
     * @param text not used
     * @throws IllegalArgumentException
     */
    public void addEmailText(String text) throws IllegalArgumentException { }

    /**
     * Adds an item to the order to process. The total amount to charge will be incremented.
     * The cost should be passed in per item.
     *
     * @param desc The item description.
     * @param cost The price.
     * @param quantity The quantity.
     * @throws IllegalArgumentException if any data is null.
     */
    public void addOrderItem(String desc, float cost, int quantity) throws IllegalArgumentException
    {
        addField( ++_counter+"_cost", CURRENCY_FORMAT.format(cost) );
        addField(_counter+"_desc", desc);
        addField( _counter+"_qty", String.valueOf(quantity) );
        _total += (cost*quantity);
    }

    public APIRequest getAPIRequest()
    {
        return this;
    }
    
    /**
     * Sets the total amount to charge. This overrides prices set in addOrderItem.
     *
     * @param total The total amount to charge.
     */
    public void setTotal(float total)
    {
        _total = total;
    }
    
    /**
     * Returns the total amount to charge.
     *
     * @return float
     */
    public float getTotal()
    {
        return _total;
    }
}