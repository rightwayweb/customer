package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.BillingInformation;
import com.zitego.customer.Customer;
import com.zitego.web.thirdPartyAPI.PostAPIRequest;
import com.zitego.web.thirdPartyAPI.APIRequest;
import java.net.HttpURLConnection;
import java.io.IOException;

/**
 * This is a request to the CDGVault api to add, edit, or delete a
 * customer. In addition, sales can be processed.
 *
 * @author John Glorioso
 * @version $Id: CDGVaultRequest.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CDGVaultRequest extends PostAPIRequest implements CDGRequest
{
    private StringBuffer _desc = new StringBuffer();
    private float _total = 0f;

    /**
     * Creates a new CDGVaultRequest.
     *
     * @param url The url to connect to.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public CDGVaultRequest(String url) throws IllegalArgumentException
    {
        super(url);
    }

    /**
     * Sets the tokenID in the request.
     *
     * @param code The customer code.
     * @param password Not used.
     * @throws IllegalArgumentException if code is null.
     */
    public void setRegisteredCustomerData(String code, String password) throws IllegalArgumentException
    {
        addField("tokenID", code);
    }

    public void setCustomerData(Customer c)
    {
        BillingInformation billing = c.getBillingInfo();
        if (billing.getPartialCcNumber() == null) throw new IllegalArgumentException("credit card prefix is required");
        addField( "cardprefix", billing.getPartialCcNumber() );
        addField( "fname", billing.getFirstName() );
        addField( "lname", billing.getLastName() );
        addField( "address", billing.getAddress1() );
        addField( "city", billing.getCity() );
        addField( "state", billing.getStateName() );
        addField( "zip", billing.getPostalCode() );
        addField( "country", billing.getCountryName() );
        addField( "phone", billing.getPrimaryPhone() );
        addField( "email", c.getContactInfo().getEmail() );
        addField( "cardexpmo", CDGRequest.MONTH_FORMAT.format(billing.getExpDate()) );
        addField( "cardexpyr", CDGRequest.YEAR_FORMAT.format(billing.getExpDate()) );
    }

    /**
     * Sets the CDG vendor id (mid).
     *
     * @param vendor The id.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorId(String vendor) throws IllegalArgumentException
    {
        addField("mid", vendor);
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
    public void setHomePage(String homePage) throws IllegalArgumentException { }

    /**
     * Not used.
     *
     * @param text not used
     * @throws IllegalArgumentException
     */
    public void addEmailText(String text) throws IllegalArgumentException { }

    /**
     * Adds an item to the order to process. This will create a description from all
     * orders added and the total amount to charge will be incremented. The description
     * will be a new line for each new item.
     *
     * @param desc The item description.
     * @param cost The price.
     * @param quantity The quantity.
     * @throws IllegalArgumentException if any data is null.
     */
    public void addOrderItem(String desc, float cost, int quantity) throws IllegalArgumentException
    {
        //Adds to the description buffer and running total
        if (_desc.length() > 0) _desc.append("\r\n");
        _desc.append(desc);
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

    /**
     * This sets the description and total if there are any.
     *
     * @param conn The connection.
     * @throws IllegalArgumentException if the connection is null.
     * @throws IOException if an error occurs setting the data.
     */
    public void setRequestData(HttpURLConnection conn) throws IllegalArgumentException, IOException
    {
        prepareRequest();
        super.setRequestData(conn);
    }

    public String toString()
    {
        prepareRequest();
        return super.toString();
    }

    private void prepareRequest()
    {
        if (_desc.length() > 0)
        {
            addField( "description", _desc.toString() );
            addField( "amount", CDGRequest.CURRENCY_FORMAT.format(new Float(_total)) );
        }
    }
}