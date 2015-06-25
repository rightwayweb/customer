package com.zitego.customer.creditCard.paypal;

import com.zitego.customer.Customer;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.customer.product.OrderItem;
import com.zitego.web.thirdPartyAPI.PostAPIRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * This is a request to the paypal interface. This will send a post
 * and read back the response parsing it to determine whether the charge succeeded.
 *
 * @author John Glorioso
 * @version $Id: PayPalRequest.java,v 1.3 2012/03/19 15:27:26 jglorioso Exp $
 */
public class PayPalRequest extends PostAPIRequest
{
    public static int SET_EXPRESS_CHECKOUT = 0;
    public static int GET_EXPRESS_CHECKOUT_DETAILS = 1;
    public static int EXPRESS_CHECKOUT_PAYMENT = 2;
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.00");
    public static final SimpleDateFormat EXP_FORMAT = new SimpleDateFormat("MMyy");
    private float _amount = 0;

    /**
     * Creates a new PaypalRequest.
     *
     * @param url The url to connect to.
     * @param testing Whether we are testing.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public PayPalRequest(String url, boolean testing) throws IllegalArgumentException
    {
        super(url);
        addField("VERSION", "72.0");
    }

    public void setCustomerData(Customer c)
    {
        ContactInformation contact = c.getContactInfo();
        addField( "ADDROVERRIDE", "1" );
        addField( "EMAIL", contact.getEmail() );
        addField( "PAYMENTREQUEST_0_SHIPTONAME", contact.getFirstName() + " " + contact.getLastName() );
        addField( "PAYMENTREQUEST_0_SHIPTOSTREET", contact.getAddress1() );
        if (contact.getAddress2() != null) addField( "PAYMENTREQUEST_0_SHIPTOSTREET2", contact.getAddress2() );
        addField( "PAYMENTREQUEST_0_SHIPTOCITY", contact.getCity() );
        if (contact.getCountryId() == 1) addField( "PAYMENTREQUEST_0_SHIPTOSTATE", contact.getStateAbbreviation() );
        else addField( "PAYMENTREQUEST_0_SHIPTOSTATE", "Other");
        addField( "PAYMENTREQUEST_0_SHIPTOZIP", contact.getPostalCode() );
        addField( "PAYMENTREQUEST_0_SHIPTOCOUNTRYCODE", contact.getCountryAbbreviation() );
    }

    /**
     * Sets the api user.
     *
     * @param user The api user.
     * @throws IllegalArgumentException if the user is null.
     */
    public void setApiUser(String user) throws IllegalArgumentException
    {
        if (user == null) throw new IllegalArgumentException("api user cannot be null");
        addField("USER", user);
    }

    /**
     * Sets the api password.
     *
     * @param user The api password.
     * @throws IllegalArgumentException if the password is null.
     */
    public void setApiPassword(String pass) throws IllegalArgumentException
    {
        if (pass == null) throw new IllegalArgumentException("api password cannot be null");
        addField("PWD", pass);
    }

    /**
     * Sets the api signature.
     *
     * @param user The api signature.
     * @throws IllegalArgumentException if the signature is null.
     */
    public void setApiSignature(String sig) throws IllegalArgumentException
    {
        if (sig == null) throw new IllegalArgumentException("api signature cannot be null");
        addField("SIGNATURE", sig);
    }

    /**
     * Sets the return url.
     *
     * @param url The return url.
     * @throws IllegalArgumentException if the url is null.
     */
    public void setReturnUrl(String url) throws IllegalArgumentException
    {
        if (url == null) throw new IllegalArgumentException("return url cannot be null");
        addField("RETURNURL", url);
    }

    /**
     * Sets the cancel url.
     *
     * @param url The cancel url.
     * @throws IllegalArgumentException if the url is null.
     */
    public void setCancelUrl(String url) throws IllegalArgumentException
    {
        if (url == null) throw new IllegalArgumentException("cancel url cannot be null");
        addField("CANCELURL", url);
    }

    /**
     * Sets the header image url.
     *
     * @param url The header image url.
     */
    public void setHeaderImageUrl(String url) throws IllegalArgumentException
    {
        if (url != null) addField("HDRIMG", url);
    }

    /**
     * Sets the method.
     *
     * @param method The method.
     * @throws IllegalArgumentException if the method is null.
     */
    public void setMethod(String method) throws IllegalArgumentException
    {
        if (method == null) throw new IllegalArgumentException("method cannot be null");
        addField("METHOD", method);
        if ( "SetExpressCheckout".equals(method) )
        {
            addField("PAYMENTREQUEST_0_CURRENCYCODE", "USD");
            addField("ALLOWNOTE", "0");
        }
        else if ( "DoExpressCheckoutPayment".equals(method) )
        {
            addField("PAYMENTREQUEST_0_CURRENCYCODE", "USD");
        }
    }

    /**
     * Sets the payer id.
     *
     * @param id The payer id.
     * @throws IllegalArgumentException if the id is null.
     */
    public void setPayerId(String id) throws IllegalArgumentException
    {
        if (id == null) throw new IllegalArgumentException("payer id cannot be null");
        addField("PAYERID", id);
    }

    /**
     * Sets the token.
     *
     * @param token The token.
     * @throws IllegalArgumentException if the token is null.
     */
    public void setToken(String token) throws IllegalArgumentException
    {
        if (token == null) throw new IllegalArgumentException("token cannot be null");
        addField("TOKEN", token);
    }

    /**
     * Sets the payment action.
     *
     * @param action The payment action.
     * @throws IllegalArgumentException if the payment action is null.
     */
    public void setPaymentAction(String action) throws IllegalArgumentException
    {
        if (action == null) throw new IllegalArgumentException("payment action cannot be null");
        addField("PAYMENTREQUEST_0_PAYMENTACTION", action);
    }

    /**
     * Adds an order item to the request.
     *
     * @param item The order item to add.
     * @param num The order item number.
     */
    public void addOrderItem(OrderItem item, int num)
    {
        addField( "PAYMENTREQUEST_0_L_NAME"+num, item.getDescription() );
        addField( "PAYMENTREQUEST_0_L_AMT"+num, PRICE_FORMAT.format(item.getCost()) );
        addField( "PAYMENTREQUEST_0_L_QTY"+num, String.valueOf(item.getQuantity()) );
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
        addField("PAYMENTREQUEST_0_AMT", PRICE_FORMAT.format(_amount) );
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
     * Sets the sales tax of the transaction.
     *
     * @param tax The tax.
     * @throws IllegalArgumentException if the amount is less than 0.
     */
    public void setSalesTax(float tax) throws IllegalArgumentException
    {
        if (tax < 0) throw new IllegalArgumentException("Sales Tax cannot be less than 0");
        addField("PAYMENTREQUEST_0_TAXAMT", PRICE_FORMAT.format(tax) );
    }

    /**
     * Sets the shipping amount of the transaction.
     *
     * @param amt The shipping amount.
     * @throws IllegalArgumentException if the amount is less than 0.
     */
    public void setShippingAmount(float amt) throws IllegalArgumentException
    {
        if (amt < 0) throw new IllegalArgumentException("Shipping amount cannot be less than 0");
        addField("PAYMENTREQUEST_0_SHIPPINGAMT", PRICE_FORMAT.format(amt) );
    }

    /**
     * Sets the item amount of the transaction.
     *
     * @param amt The item amount.
     * @throws IllegalArgumentException if the amount is less than 0.
     */
    public void setItemAmount(float amt) throws IllegalArgumentException
    {
        if (amt < 0) throw new IllegalArgumentException("Item amount cannot be less than .01");
        addField("PAYMENTREQUEST_0_ITEMAMT", PRICE_FORMAT.format(amt) );
    }
}
