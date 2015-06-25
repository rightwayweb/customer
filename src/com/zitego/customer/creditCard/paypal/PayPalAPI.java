package com.zitego.customer.creditCard.paypal;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIRequest;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.creditCard.CreditCardAPI;
import com.zitego.customer.creditCard.CreditCardAPIException;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.CreditCardStore;
import com.zitego.customer.Customer;
import com.zitego.customer.product.OrderItem;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.util.StaticProperties;

/**
 * This is the paypal implementation of a credit card api. It implements ExpressCheckout.
 *
 * @author John Glorioso
 * @version $Id: PayPalAPI.java,v 1.3 2012/03/13 04:53:41 jglorioso Exp $
 */
public class PayPalAPI extends CreditCardAPI
{
    private String _url;
    private String _user;
    private String _password;
    private String _signature;
    private String _returnUrl;
    private String _cancelUrl;
    private String _expressCheckoutUrl;
    private String _headerImageUrl;
    private boolean _testing = false;

    public static void main(String[] args) throws Exception
    {

    }

    /**
     * Charging credit cards is not yet implemented. null is returned.
     *
     * @param c The customer.
     * @param orderId The order id.
     * @param items The order items.
     * @throws CreditCardAPIException if an error occurs.
     */
    public CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items) throws CreditCardAPIException
    {
        APIRequest request = getRequest(PayPalRequest.EXPRESS_CHECKOUT_PAYMENT, c, items);
        return (PayPalResponse)getCreditCardResponse(request, "finalizing the express checkout payment");
    }

    /**
     * Pre-auth is not yet implemented. null is returend.
     *
     * @param c The customer to preauth to.
     * @param orderId The order id.
     * @param items The items to order.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse preauthorizeTransaction(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Process pre-auth is not yet implemented. null is returned.
     *
     * @param c The customer to process the previous preauth transaction for.
     * @param transId The transaction id of the preauthorization.
     * @param amt The amount to process.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse processPriorAuth(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Post credit is not yet implemented. null is returned.
     *
     * @param c The customer to process the credit for.
     * @param transId The transaction id of the transaction.
     * @param amt The amount to process.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse postCredit(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Voiding transactions is not yet implemented. null is returned.
     *
     * @param c The customer to process the void for.
     * @param transId The transaction id of the transaction.
     * @param amt The amount to process.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse voidTransaction(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Not supported. null is returned.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Not supported. null is returned.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Not supported. null is returned.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException
    {
        return null;
    }

    /**
     * Sends the order information to paypal to setup the paypal transaction. The token
     * is returned in the paypal response.
     *
     * @param cust The customer.
     * @param items The order items.
     * @return PayPalResponse
     * @throws CreditCardAPIException if an error occurs.
     */
    public PayPalResponse setExpressCheckout(Customer cust, OrderItem[] items) throws CreditCardAPIException
    {
        APIRequest request = getRequest(PayPalRequest.SET_EXPRESS_CHECKOUT, cust, items);
        return (PayPalResponse) getCreditCardResponse(request, "setting the express checkout transaction");
    }

    /**
     * Sends a request to get the express checkout details. It sets the values in the customer
     * contact information for shipping details and returns the paypal user id.
     *
     * @para cust The customer to set the shipping details to.
     * @param token The paypal session token.
     * @return String
     * @throws PayPalAPIException if the call resulted in an error response from paypal.
     * @throws CreditCardAPIException if the call failed.
     */
    public String getExpressCheckoutDetails(Customer cust, String token) throws CreditCardAPIException, PayPalAPIException
    {
        try
        {
            APIRequest request = getRequest(new Object[] { new Integer(PayPalRequest.GET_EXPRESS_CHECKOUT_DETAILS), token });
            PayPalResponse response = (PayPalResponse)getCreditCardResponse(request, "retrieving the express checkout details");
            if (!response.wasSuccess() ) throw new PayPalAPIException("Failure retrieving the paypal checkout information. Details: "+response.getErrorMessage());
            String payerId = response.getPayerId();
            ContactInformation shippingInfo = response.getShippingInfo();
            cust.getContactInfo().setFirstName( shippingInfo.getFirstName() );
            cust.getContactInfo().setLastName( shippingInfo.getLastName() );
            cust.getContactInfo().setAddress1( shippingInfo.getAddress1() );
            cust.getContactInfo().setAddress2( shippingInfo.getAddress2() );
            cust.getContactInfo().setCity( shippingInfo.getCity() );
            cust.getContactInfo().setStateName( shippingInfo.getStateName() );
            cust.getContactInfo().setPostalCode( shippingInfo.getPostalCode() );
            cust.getContactInfo().setCountryName( shippingInfo.getCountryName() );
            cust.getBillingInfo().setEmail( shippingInfo.getEmail() );
            return payerId;
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("An error occurred getting the request.", ae);
        }
    }

    /**
     * Sets the url.
     *
     * @param url The url.
     */
    public void setUrl(String url)
    {
        _url = url;
    }

    /**
     * Returns the url.
     *
     * @return String
     */
    public String getUrl()
    {
        return _url;
    }

    /**
     * For reflection.
     *
     * @url String The api user.
     */
    public void setLogin(String user)
    {
        setApiUser(user);
    }

    public void setDupWindowSec(String sec) { }

    /**
     * Sets the api user.
     *
     * @url String The api user.
     */
    public void setApiUser(String user)
    {
        _user = user;
    }

    /**
     * Returns the api user.
     *
     * @return String
     */
    public String getApiUser()
    {
        return _user;
    }

    /**
     * For reflection.
     *
     * @param pass The password.
     */
    public void setPassword(String pass)
    {
        setApiPassword(pass);
    }

    /**
     * Sets the api password.
     *
     * @param pass The password.
     */
    public void setApiPassword(String pass)
    {
        _password = pass;
    }

    /**
     * Returns the api password.
     *
     * @return _password
     */
    public String getApiPassword()
    {
        return _password;
    }

    /**
     * Sets the api signature.
     *
     * @param pass The signature.
     */
    public void setApiSignature(String sig)
    {
        _signature = sig;
    }

    /**
     * Returns the api signature.
     *
     * @return String
     */
    public String getApiSignature()
    {
        return _signature;
    }

    /**
     * Sets the return url.
     *
     * @param url The return url.
     */
    public void setReturnUrl(String url)
    {
        _returnUrl = url;
    }

    /**
     * Returns the return url.
     *
     * @return String
     */
    public String getReturnUrl()
    {
        return _returnUrl;
    }

    /**
     * Sets the cancel url.
     *
     * @param url The cancel url.
     */
    public void setCancelUrl(String url)
    {
        _cancelUrl = url;
    }

    /**
     * Returns the cancel url.
     *
     * @return String
     */
    public String getCancelUrl()
    {
        return _cancelUrl;
    }

    /**
     * Sets the express checkout url.
     *
     * @param url The express checkout url.
     */
    public void setExpressCheckoutUrl(String url)
    {
        _expressCheckoutUrl = url;
    }

    /**
     * Returns the express checkout url.
     *
     * @return String
     */
    public String getExpressCheckoutUrl()
    {
        return _expressCheckoutUrl;
    }

    /**
     * Sets the header image url.
     *
     * @param url The header image url.
     */
    public void setHeaderImageUrl(String url)
    {
        _headerImageUrl = url;
    }

    /**
     * Returns the header image url.
     *
     * @return String
     */
    public String getHeaderImageUrl()
    {
        return _headerImageUrl;
    }

    /**
     * Sets whether we are testing or not. Default is false.
     *
     * @param testing The testing flag.
     */
    public void setTesting(boolean testing)
    {
        _testing = testing;
    }

    /**
     * Sets whether we are testing or not. Default is false.
     *
     * @param testing The testing flag.
     */
    public void setTesting(String testing)
    {
        setTesting( new Boolean(testing).booleanValue() );
    }

    /**
     * Returns whether we are testing or not.
     *
     * @return boolean
     */
    public boolean getTesting()
    {
        return _testing;
    }

    private APIRequest getRequest(int type, Customer c, String transId, float amt)
    throws IllegalArgumentException, CreditCardAPIException
    {
        try
        {
            return getRequest( new Object[] { new Integer(type), c, transId, new Float(amt) } );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("An error occurred getting the request.", ae);
        }
    }

    private APIRequest getRequest(int type, Customer c, OrderItem[] items)
    throws IllegalArgumentException, CreditCardAPIException
    {
        try
        {
            return getRequest( new Object[] { new Integer(type), c, items } );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("An error occurred getting the request.", ae);
        }
    }

    protected APIRequest getRequest(Object[] args) throws APIException
    {
        int type = ( (Integer)args[0] ).intValue();
        PayPalRequest ret = new PayPalRequest(_url, _testing);

        if (type == PayPalRequest.SET_EXPRESS_CHECKOUT)
        {
            Customer c = (Customer)args[1];
            ret.setCustomerData(c);
            ret.setMethod("SetExpressCheckout");
            ret.setPaymentAction("Sale");
            ret.setReturnUrl(_returnUrl);
            ret.setCancelUrl(_cancelUrl);
            if (_headerImageUrl != null) ret.setHeaderImageUrl(_headerImageUrl);
            //Add order items
            OrderItem[] items = (OrderItem[])args[2];
            float total = 0f;
            float itemTotal = 0f;
            int itemCount = 0;
            for (int i=0; i<items.length; i++)
            {
                if ( "Sales Tax".equals(items[i].getDescription()) )
                {
                    ret.setSalesTax( items[i].getCost() );
                }
                else if ( "Shipping".equals(items[i].getDescription()) )
                {
                    ret.setShippingAmount( items[i].getCost() );
                }
                else
                {
                    ret.addOrderItem( items[i], itemCount++ );
                    itemTotal += ( items[i].getCost() * items[i].getQuantity() );
                }
                total += ( items[i].getCost() * items[i].getQuantity() );
            }
            ret.setAmount(total);
            ret.setItemAmount(itemTotal);
        }
        else if (type == PayPalRequest.GET_EXPRESS_CHECKOUT_DETAILS)
        {
            ret.setMethod("GetExpressCheckoutDetails");
            ret.setToken( (String)args[1] );
        }
        else if (type == PayPalRequest.EXPRESS_CHECKOUT_PAYMENT)
        {
            Customer c = (Customer)args[1];
            ret.setCustomerData(c);
            ret.setMethod("DoExpressCheckoutPayment");
            ret.setPaymentAction("Sale");

            //Add order items
            OrderItem[] items = (OrderItem[])args[2];
            float total = 0f;
            float itemTotal = 0f;
            int itemCount = 0;
            for (int i=0; i<items.length; i++)
            {
                if ( "Sales Tax".equals(items[i].getDescription()) )
                {
                    ret.setSalesTax( items[i].getCost() );
                }
                else if ( "Shipping".equals(items[i].getDescription()) )
                {
                    ret.setShippingAmount( items[i].getCost() );
                }
                else
                {
                    ret.addOrderItem( items[i], itemCount++ );
                    itemTotal += ( items[i].getCost() * items[i].getQuantity() );
                }
                total += ( items[i].getCost() * items[i].getQuantity() );
            }
            ret.setAmount(total);
            ret.setItemAmount(itemTotal);
            ret.setToken( (String)c.getCustomField("token") );
            ret.setPayerId( (String)c.getCustomField("payerId") );
        }
        ret.setApiUser(_user);
        ret.setApiPassword(_password);
        ret.setApiSignature(_signature);

        if ( debugging() ) ret.setDebug(true);

        return ret;
    }

    protected APIResponse createResponse(APIRequest request, String text) throws APIException
    {
        return new PayPalResponse( (PayPalRequest)request, text );
    }

    /**
     * Gets a response from authorize.net given a request. If an error occurs, an APIException is thrown
     * with details.
     *
     * @param request The request.
     * @param errSnippet The detailed error message snippet.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs.
     */
    protected CreditCardResponse getCreditCardResponse(APIRequest request, String errSnippet) throws CreditCardAPIException
    {
        PayPalResponse response = null;
        try
        {
            response = (PayPalResponse)getResponse(request, errSnippet);
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException(ae.getMessage(), ae);
        }
        return response;
    }

    /**
     * Throws a new CreditCardAPIException with a message, root exception, a details message, the
     * request, and the response. Except for the message, any value can be null.
     *
     * @param msg The message.
     * @param root The root exception.
     * @param details The details message.
     * @param request The request.
     * @param response The response.
     * @throws CreditCardAPIException
     */
    protected void handleError(String msg, Throwable root, String details, APIRequest request, APIResponse response)
    throws CreditCardAPIException
    {
        String responseText = null;
        if (response != null)
        {
            try
            {
                responseText = response.format(FormatType.XML);
            }
            catch (UnsupportedFormatException ufe)
            {
                responseText = "Unable to format: "+ufe.toString();
            }
        }
        String requestText = (request != null ? request.toString() : null);
        if (root != null) throw new CreditCardAPIException(msg, details, requestText, responseText, root);
        else throw new CreditCardAPIException(msg, details, requestText, responseText);
    }

    private CreditCardStore getCcStore() throws CreditCardAPIException
    {
        CreditCardStore store = (CreditCardStore)BaseConfigServlet.getWebappProperties().getProperty("cc_store");
        if (store == null) store = (CreditCardStore)StaticProperties.getProperty("cc_store");
        if (store == null) throw new CreditCardAPIException("Could not retrieve the credit card store.");
        return store;
    }

    public String getPropertyString()
    {
        StringBuffer ret = new StringBuffer()
            .append( getClass().getName() ).append("=\n")
            .append("  url=").append(_url).append(",\n")
            .append("  apiUser=").append(_user).append(",\n")
            .append("  apiPassword=").append(_password).append(",\n")
            .append("  apiSignature=").append(_signature).append(",\n")
            .append("  returnUrl=").append(_returnUrl).append(",\n")
            .append("  cancelUrl=").append(_cancelUrl).append(",\n")
            .append("  expressCheckoutUrl=").append(_expressCheckoutUrl).append(",\n");
       if (_headerImageUrl != null) ret.append("  headerImageUrl=").append(_headerImageUrl).append(",\n");
       ret.append("  testing=").append(_testing);

       return ret.toString();
    }

    public boolean isStateAbbreviationRequired()
    {
        return true;
    }
}
