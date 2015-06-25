package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.Customer;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.web.thirdPartyAPI.APIRequest;
import com.zitego.web.thirdPartyAPI.XmlAPIRequest;

/**
 * This is a class that encapsulates all data to be sent to CDG via an
 * xml document to run transactions against a credit card.
 *
 * @author John Glorioso
 * @version $Id: CDGXmlRequest.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CDGXmlRequest extends XmlAPIRequest implements CDGRequest
{
    /** An xml document to hold all of the data. */
    private CDGXmlRequestDocument _xml;

    /**
     * Creates a new CDGXmlRequest.
     *
     * @param url The url to connect to.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public CDGXmlRequest(String url) throws IllegalArgumentException
    {
        super(url, "xml");
        _xml = new CDGXmlRequestDocument();
    }

    /**
     * Sets the registered customer data. This or CustomerData can be
     * set, but not both.
     *
     * @param code The customer code.
     * @param password The password.
     * @throws IllegalStateException if the CustomerData is already set.
     * @throws IllegalArgumentException if code or password are null.
     */
    public void setRegisteredCustomerData(String code, String password) throws IllegalStateException
    {
        _xml.setRegisteredCustomerData(code, password);
    }

    /**
     * Sets the customer data. This or RegisteredCustomerData can be
     * set, but not both. The Customer object must contain the following:
     * ContactInformation - email, (address1, city, state, postal code, country,
     * and primary phone are optional, but required if any one is set. This is
     * the shipping address if different then billing). BillingInformation -
     * address1, city, state, postal code, country, and primary phone. Credit
     * card number and expiration date.
     *
     * @param data The data.
     * @throws IllegalArgumentException if the customer is null or any required data
     *                                  is missing.
     * @throws IllegalStateException if the RegisteredCustomerData is already set.
     */
    public void setCustomerData(Customer data) throws IllegalArgumentException, IllegalStateException
    {
        _xml.setCustomerData(data);
    }

    /**
     * Sets the CDG vendor id (gateway id).
     *
     * @param vendor The id.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorId(String vendor) throws IllegalArgumentException
    {
        _xml.setVendorId(vendor);
    }

    /**
     * Sets the CDG vendor password (gateway password).
     *
     * @param pass The password.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorPassword(String pass) throws IllegalArgumentException
    {
        _xml.setVendorPassword(pass);
    }

    /**
     * Sets the home page of the gateway configuration.
     *
     * @param homePage The home page.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setHomePage(String homePage) throws IllegalArgumentException
    {
        _xml.setHomePage(homePage);
    }

    /**
     * Adds a line of text to include in the customer receipt email.
     *
     * @param text The email text to add.
     * @throws IllegalArgumentException if the data is null.
     */
    public void addEmailText(String text) throws IllegalArgumentException
    {
        _xml.addEmailText(text);
    }

    /**
     * Adds an item to the order to process.
     *
     * @param desc The item description.
     * @param cost The price.
     * @param quantity The quantity.
     * @throws IllegalArgumentException if any data is null.
     */
    public void addOrderItem(String desc, float cost, int quantity) throws IllegalArgumentException
    {
        _xml.addOrderItem(desc, cost, quantity);
    }

    public String format(FormatType type) throws UnsupportedFormatException
    {
        return _xml.format(type);
    }

    /**
     * Returns the xml document.
     *
     * @return CDGXmlRequestDocument
     */
    protected CDGXmlRequestDocument getRequestDocument()
    {
        return _xml;
    }

    public APIRequest getAPIRequest()
    {
        return this;
    }
}