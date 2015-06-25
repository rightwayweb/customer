package com.zitego.customer.creditCard.orbital;

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
 * This is the Orbital payment gateway implementation of the credit card api.
 *
 * @author John Glorioso
 * @version $Id: OrbitalCreditCardAPI.java,v 1.5 2010/08/05 02:05:08 jglorioso Exp $
 */
public class OrbitalCreditCardAPI extends CreditCardAPI
{
    private String _orbitalUrl;
    private String _bin;
    private String _merchantId;
    private String _terminalId;
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
        OrbitalCreditCardAPI api = new OrbitalCreditCardAPI();
        api.setOrbitalUrl("https://orbitalvar1.paymentech.net/authorize");
        //api.setOrbitalUrl("https://orbital1.paymentech.net/authorize");
        api.setMerchantId("123");
        api.setBin("000002");
        api.setTerminalId("001");
        api.setSendRequest(false);
        api.setDebug(true);
        api.setTesting(true);
        //CreditCardResponse resp = api.voidTransaction(c, "0", 1);
        CreditCardResponse resp = api.preauthorizeTransaction(c, 1, new com.zitego.customer.product.OrderItemShell[] { new com.zitego.customer.product.OrderItemShell("test", 1, 1) } );
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
     * the orbital request.
     *
     * @param c The customer.
     * @param items The order items.
     * @throws CreditCardAPIException if an error occurs.
     */
    public CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items) throws CreditCardAPIException
    {
        APIRequest request = getRequest(c, items, OrbitalRequest.CHARGE);
        ( (OrbitalRequest)request ).setOrderId(orderId);
        return getCreditCardResponse(request, "charging the customer's credit card");
    }

    /**
     * Sends a transaction in for pre-authorization.
     *
     * If custom fields for the customer are set called "description" or "invoice_num" they will be set in
     * the orbital request.
     *
     * @param c The customer to preauth to.
     * @param orderId The order id.
     * @param items The items to order.
     * @param emailLines Any lines of email text to send in the receipt letter.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse preauthorizeTransaction(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException
    {
        APIRequest request = getRequest(c, items, OrbitalRequest.PREAUTH);
        ( (OrbitalRequest)request ).setOrderId(orderId);
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
        APIRequest request = getRequest(c, transId, amt, OrbitalRequest.PROCESS_PRIOR_PREAUTH);
        return getCreditCardResponse(request, "processing a prior preauthorized transaction for the customer");
    }

    /**
     * Refunds a previously processed transaction.
     *
     * @param c The customer to process the refund for.
     * @param transId The transaction id of the transaction.
     * @param amt The amount to process.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public CreditCardResponse postCredit(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        APIRequest request = getRequest(c, transId, amt, OrbitalRequest.REFUND);
        return getCreditCardResponse(request, "posting a credit to the customer's credit card");
    }

    /**
     * Voids an unsettled transaction by issuing a refund call to an uncaptured authorization.
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
        APIRequest request = getRequest(c, transId, amt, OrbitalRequest.REFUND);
        return getCreditCardResponse(request, "posting a void to the customer's credit card");
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @param emailLines Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Not supported");
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @param emailLines Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Not supported");
    }

    /**
     * Not supported. Just throws an exception.
     *
     * @param c Ignored.
     * @param items Ignored.
     * @param emailLines Ignored.
     * @throws CreditCardAPIException
     */
    public CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Not supported");
    }

    private MockOrbitalResponse getMockResponse() throws CreditCardAPIException
    {
        try
        {
            return new MockOrbitalResponse( new OrbitalRequest(_orbitalUrl) );
        }
        catch (APIException ae)
        {
            throw new CreditCardAPIException("Could not create mock response", ae);
        }
    }

    /**
     * For reflection. Does nothing.
     *
     * @notused Not used.
     */
    public void setLogin(String notused) {}

    /**
     * For reflection. Does nothing.
     *
     * @notused Not used.
     */
    public void setDupWindowSec(String notused) {}

    /**
     * For reflection. Does nothing.
     *
     * @notused Not used.
     */
    public void setPassword(String notused) {}

    /**
     * For reflection.
     *
     * @param url The url.
     */
    public void setUrl(String url)
    {
        setOrbitalUrl(url);
    }

    /**
     * Sets the orbital url.
     *
     * @param url The url.
     */
    public void setOrbitalUrl(String url)
    {
        _orbitalUrl = url;
    }

    /**
     * Returns the orbital url.
     *
     * @return String
     */
    public String getOrbitalUrl()
    {
        return _orbitalUrl;
    }

    /**
     * Sets the BIN from orbital.
     *
     * @param bin The bin.
     */
    public void setBin(String bin)
    {
        _bin = bin;
    }

    /**
     * Returns the BIN from orbital.
     *
     * @return String
     */
    public String getBin()
    {
        return _bin;
    }

    /**
     * Sets the merchant id.
     *
     * @id String The id.
     */
    public void setMerchantId(String id)
    {
        _merchantId = id;
    }

    /**
     * Returns the merchant id.
     *
     * @return String
     */
    public String getMerchantId()
    {
        return _merchantId;
    }

    /**
     * Sets the terminal id.
     *
     * @param id The terminal id.
     */
    public void setTerminalId(String id)
    {
        _terminalId = id;
    }

    /**
     * Returns the terminal id.
     *
     * @return String
     */
    public String getTerminalId()
    {
        return _terminalId;
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
        OrbitalRequest ret = new OrbitalRequest(_orbitalUrl);
        ret.setType(type);
        ret.setCustomerData(c);

        if (type == OrbitalRequest.CHARGE || type == OrbitalRequest.PREAUTH)
        {
            //Add order items
            OrderItem[] items = (OrderItem[])args[2];
            float total = 0f;
            for (int i=0; i<items.length; i++)
            {
                total += ( items[i].getCost() * items[i].getQuantity() );
            }
            ret.setAmount(total);
        }
        else if (type == OrbitalRequest.PROCESS_PRIOR_PREAUTH || type == OrbitalRequest.REFUND || type == OrbitalRequest.VOID)
        {
            ret.setTransactionId( (String)args[2] );
            ret.setAmount( ((Float)args[3]).floatValue() );
        }
        ret.setMerchantId(_merchantId);
        ret.setBin(_bin);
        ret.setTerminalId(_terminalId);

        if ( debugging() ) ret.setDebug(true);
        return ret;
    }

    protected APIResponse createResponse(APIRequest request, String text) throws APIException
    {
        return new OrbitalResponse( (OrbitalRequest)request, text );
    }

    /**
     * Gets a response from the orbital gateway given a request. If an error occurs, an APIException is thrown
     * with details.
     *
     * @param request The request.
     * @param errSnippet The detailed error message snippet.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs.
     */
    protected CreditCardResponse getCreditCardResponse(APIRequest request, String errSnippet) throws CreditCardAPIException
    {
        OrbitalResponse response = null;
        try
        {
            if ( getSendRequest() )
            {
                response = (OrbitalResponse)getResponse(request, errSnippet);
            }
            else
            {
                response = new MockOrbitalResponse( (OrbitalRequest)request );
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
            .append("  orbitalUrl=").append(_orbitalUrl).append(",\n")
            .append("  merchantId=").append(_merchantId).append(",\n")
            .append("  BIN=").append(_bin).append(",\n")
            .append("  terminalId=").append(_terminalId).append(",\n")
            .append("  testing=").append(_testing);
        return ret.toString();
    }

    public boolean isStateAbbreviationRequired()
    {
        return true;
    }
}