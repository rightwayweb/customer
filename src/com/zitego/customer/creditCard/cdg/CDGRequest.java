package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.Customer;
import com.zitego.web.thirdPartyAPI.APIRequest;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

/**
 * This is a class that encapsulates all data to be sent to CDG to run
 * transactions against a credit card. This handles storing the customer
 * if they are new.
 *
 * @author John Glorioso
 * @version $Id: CDGRequest.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public interface CDGRequest
{
    /** The add customer command. */
    public static final int ADD_CUSTOMER = 0;
    /** The edit customer command. */
    public static final int EDIT_CUSTOMER = 1;
    /** The delete customer command. */
    public static final int DELETE_CUSTOMER = 2;
    /** The charge sale command. */
    public static final int CHARGE_CUSTOMER = 3;
    /** The credit card month format. */
    public static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
    /** The credit card year format. */
    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    /** The currency format. */
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#.00");

    /**
     * Sets the registered customer data. This or CustomerData can be
     * set, but not both.
     *
     * @param code The customer code.
     * @param password The password.
     * @throws IllegalStateException if the CustomerData is already set.
     * @throws IllegalArgumentException if code or password are null.
     */
    public void setRegisteredCustomerData(String code, String password) throws IllegalStateException;

    /**
     * Sets the customer data. The required data is specified by the implementing class.
     *
     * @param data The data.
     * @throws IllegalArgumentException if the customer is null or any required data
     *                                  is missing.
     */
    public void setCustomerData(Customer data) throws IllegalArgumentException;

    /**
     * Sets the CDG vendor id (gateway id).
     *
     * @param vendor The id.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorId(String vendor) throws IllegalArgumentException;

    /**
     * Sets the CDG vendor password (gateway password).
     *
     * @param pass The password.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorPassword(String pass) throws IllegalArgumentException;

    /**
     * Sets the home page of the gateway configuration.
     *
     * @param homePage The home page.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setHomePage(String homePage) throws IllegalArgumentException;

    /**
     * Adds a line of text to include in the customer receipt email.
     *
     * @param text The email text to add.
     * @throws IllegalArgumentException if the data is null.
     */
    public void addEmailText(String text) throws IllegalArgumentException;

    /**
     * Adds an item to the order to process.
     *
     * @param desc The item description.
     * @param cost The price.
     * @param quantity The quantity.
     * @throws IllegalArgumentException if any data is null.
     */
    public void addOrderItem(String desc, float cost, int quantity) throws IllegalArgumentException;

    /**
     * Sets whether we are debugging or not.
     *
     * @param flag The debug flag.
     */
    public void setDebug(boolean flag);

    /**
     * Returns an APIRequest to be used with the CDG api class.
     *
     * @return APIRequest
     */
    public APIRequest getAPIRequest();
}