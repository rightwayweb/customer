package com.zitego.customer.creditCard.authnet;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIRequest;
import com.zitego.web.thirdPartyAPI.APIResponse;
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
 * This is the Authorize.net aim implementation of the credit card api. It provides only
 * charge functionality.
 *
 * @author John Glorioso
 * @version $Id: AimCreditCardAPI.java,v 1.6 2010/08/05 02:04:57 jglorioso Exp $
 */
public class AimCreditCardAPI extends CreditCardAPI
{
    private String _aimUrl;
    private String _login;
    private String _password;
    private String _orderDescription;
    private String _invoiceNumber;
    private int _dupWindowSec = 120;
    private boolean _testing = false;

    public static void main(String[] args) throws Exception
    {
        BaseConfigServlet.getWebappProperties().setProperty("customer.db.config", new com.zitego.sql.DBConfig
        (
            "jdbc:mysql://zg01dev.zitego.com:3306/customer",
            (java.sql.Driver)Class.forName("com.mysql.jdbc.Driver").newInstance(),
            "velocityserver", "n3wVS", com.zitego.sql.DBConfig.MYSQL
        ) );
        Customer c = new Customer( 116, (com.zitego.sql.DBConfig)BaseConfigServlet.getWebappProperties().getProperty("customer.db.config") );
        c.init();
        c.getBillingInfo().setCcNumber("4007000000027");
        c.getBillingInfo().setExpDate("01/08");
        AimCreditCardAPI api = new AimCreditCardAPI();
        api.setAimUrl("https://secure.authorize.net/gateway/transact.dll");
        api.setLogin("velocity2411");
        //api.setPassword("");
        api.setSendRequest(true);
        api.setDebug(true);
        api.setTesting(true);
        //CreditCardResponse resp = api.voidTransaction(c, "0", 1);
        CreditCardResponse resp = api.preauthorizeTransaction(c, 12, new com.zitego.customer.product.OrderItemShell[] { new com.zitego.customer.product.OrderItemShell("test", 1, 1) } );
        System.out.println("Status: "+resp.getStatus());
        System.out.println("Error Msg: "+resp.getErrorMessage());
        System.out.println("Auth Code: "+resp.getAuthCode());
        System.out.println("Transaction Id: "+resp.getTransactionId());
        System.out.println("Total: "+resp.getTotalCharged());
        System.out.println("Shipping: "+resp.getShippingInfo());
        System.out.println("Billing: "+resp.getBillingInfo());
    }

    /**
     * Charging credit cards is done only with a total amount. No other data is necessary. The api will loop
     * through each item and add the product of the quantity and cost to the total. Email lines are ignored
     * as they are not supported.
     *
     * If custom fields for the customer are set called "description" or "invoice_num" they will be set in
     * the authorize.net request.
     *
     * @param c The customer.
     * @param orderId The order id.
     * @param items The order items.
     * @throws CreditCardAPIException if an error occurs.
     */
    public CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items) throws CreditCardAPIException
    {
        _orderDescription = (String)c.getCustomField("description");
        _invoiceNumber = (String)c.getCustomField("invoice_num");
        APIRequest request = getRequest(c, items, AimRequest.CHARGE);
        return getCreditCardResponse(request, "charging the customer's credit card");
    }

    /**
     * Sends a transaction in for pre-authorization.
     *
     * If custom fields for the customer are set called "description" or "invoice_num" they will be set in
     * the authorize.net request.
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
        _orderDescription = (String)c.getCustomField("description");
        _invoiceNumber = (String)c.getCustomField("invoice_num");
        APIRequest request = getRequest(c, items, AimRequest.PREAUTH);
        return getCreditCardResponse(request, "preauthorizing the customer's credit card");
    }

    /**
     * Processes a prior preauthorized transaction given the original transaction id.
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
        APIRequest request = getRequest(c, transId, amt, AimRequest.PROCESS_PRIOR_PREAUTH);
        return getCreditCardResponse(request, "processing a prior preauthorized transaction for the customer");
    }

    /**
     * Post a credit to a previously processed transaction.
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
        APIRequest request = getRequest(c, transId, amt, AimRequest.CREDIT);
        return getCreditCardResponse(request, "posting a credit to the customer's credit card");
    }

    /**
     * Voids an unsettled transaction.
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
        APIRequest request = getRequest(c, transId, amt, AimRequest.VOID);
        return getCreditCardResponse(request, "posting a void to the customer's credit card");
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException
    {
        getCcStore().addCc( c.getBillingInfo().getCcNumber(), c.getAccountNumber() );
        //Success if we got this far
        return getMockResponse();
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException
    {
        getCcStore().editCc( c.getBillingInfo().getCcNumber(), c.getAccountNumber() );
        //Success if we got this far
        return getMockResponse();
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException
    {
        getCcStore().deleteCc( c.getBillingInfo().getCcNumber(), c.getAccountNumber() );
        //Success if we got this far
        return getMockResponse();
    }

    private MockAimResponse getMockResponse() throws CreditCardAPIException
    {
        try
        {
            return new MockAimResponse( new AimRequest(_aimUrl, _dupWindowSec, _testing) );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("Could not create mock response", ae);
        }
    }

    /**
     * For reflection.
     *
     * @param url The url.
     */
    public void setUrl(String url)
    {
        setAimUrl(url);
    }

    /**
     * Sets the aim url.
     *
     * @param url The url.
     */
    public void setAimUrl(String url)
    {
        _aimUrl = url;
    }

    /**
     * Returns the aim url.
     *
     * @return String
     */
    public String getAimUrl()
    {
        return _aimUrl;
    }

    /**
     * Sets the merchant login.
     *
     * @url String The login.
     */
    public void setLogin(String login)
    {
        _login = login;
    }

    /**
     * Returns the merchant login.
     *
     * @return String
     */
    public String getLogin()
    {
        return _login;
    }

    /**
     * Sets the password for credits and authorizations.
     *
     * @param pass The password.
     */
    public void setPassword(String pass)
    {
        _password = pass;
    }

    /**
     * Returns the password.
     *
     * @return _password
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the window in seconds allowed between duplicate transactions. Default
     * is 120 (2 minutes).
     *
     * @param sec The number of seconds between duplicates.
     */
    public void setDupWindowSec(String sec)
    {
        setDupWindowSec( Integer.parseInt(sec) );
    }

    /**
     * Sets the window in seconds allowed between duplicate transactions. Default
     * is 120 (2 minutes).
     *
     * @param sec The number of seconds between duplicates.
     */
    public void setDupWindowSec(int sec)
    {
        _dupWindowSec = sec;
    }

    /**
     * Returns the number of seconds between duplicate submissions.
     *
     * @return int
     */
    public int getDupWindowSec()
    {
        return _dupWindowSec;
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

    private APIRequest getRequest(Customer c, String transId, float amt, int type)
    throws IllegalArgumentException, CreditCardAPIException
    {
        try
        {
            return getRequest( new Object[] { c, new Integer(type), transId, new Float(amt) } );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("An error occurred getting the request.", ae);
        }
    }

    private APIRequest getRequest(Customer c, OrderItem[] items, int type)
    throws IllegalArgumentException, CreditCardAPIException
    {
        try
        {
            return getRequest( new Object[] { c, new Integer(type), items } );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("An error occurred getting the request.", ae);
        }
    }

    protected APIRequest getRequest(Object[] args) throws APIException
    {
        Customer c = (Customer)args[0];
        int type = ( (Integer)args[1] ).intValue();
        AimRequest ret = new AimRequest(_aimUrl, _dupWindowSec, _testing);
        ret.setType(type);
        ret.setCustomerData(c);

        if (type == AimRequest.CHARGE || type == AimRequest.PREAUTH)
        {
            //Add order items
            OrderItem[] items = (OrderItem[])args[2];
            float total = 0f;
            for (int i=0; i<items.length; i++)
            {
                total += ( items[i].getCost() * items[i].getQuantity() );
            }
            if (_orderDescription != null) ret.setDescription(_orderDescription);
            if (_invoiceNumber != null) ret.setInvoiceNumber(_invoiceNumber);
            ret.setAmount(total);
        }
        else if (type == AimRequest.PROCESS_PRIOR_PREAUTH || type == AimRequest.CREDIT || type == AimRequest.VOID)
        {
            ret.setTransactionId( (String)args[2] );
            ret.setAmount( ((Float)args[3]).floatValue() );
        }
        ret.setLogin(_login);
        if (_password != null) ret.setPassword(_password);

        if ( debugging() ) ret.setDebug(true);

        return ret;
    }

    protected APIResponse createResponse(APIRequest request, String text) throws APIException
    {
        return new AimResponse( (AimRequest)request, text );
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
        AimResponse response = null;
        try
        {
            if ( getSendRequest() )
            {
                response = (AimResponse)getResponse(request, errSnippet);
            }
            else
            {
                response = new MockAimResponse( (AimRequest)request );
                if ( debugging() ) BaseConfigServlet.logError("CC Request (NOT SENT): "+request);
            }
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
            .append("  aimUrl=").append(_aimUrl).append(",\n")
            .append("  login=").append(_login).append(",\n")
            .append("  dupWindowSec=").append(_dupWindowSec).append(",\n")
            .append("  testing=").append(_testing);
        return ret.toString();
    }
}
