package com.zitego.customer.creditCard.cdg;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIRequest;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.customer.creditCard.CreditCardAPI;
import com.zitego.customer.creditCard.CreditCardAPIException;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.BillingInformation;
import com.zitego.customer.Customer;
import com.zitego.customer.product.OrderItem;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;

/**
 * This is the CDGCommerce implementation of the credit card api. The provide only
 * charge/authorize functionality as well as existing customer billing.
 *
 * @author John Glorioso
 * @version $Id: CDGCreditCardAPI.java,v 1.5 2010/08/05 02:05:00 jglorioso Exp $
 */
public class CDGCreditCardAPI extends CreditCardAPI
{
    private String _cdgUrl;
    private String _vaultUrl;
    private String _vendorId;
    private String _password;
    private String _homePage;
    private boolean _xmlPost = false;

    public static void main(String[] args) throws Exception
    {
        BaseConfigServlet.getWebappProperties().setProperty("customer.db.config", new com.zitego.sql.DBConfig
        (
            "jdbc:mysql://zg01dev:3306/customer",
            (java.sql.Driver)Class.forName("com.mysql.jdbc.Driver").newInstance(),
            "james", "ZgM$vrD8", com.zitego.sql.DBConfig.MYSQL
        ) );
        Customer c = new Customer( 116, (com.zitego.sql.DBConfig)BaseConfigServlet.getWebappProperties().getProperty("customer.db.config") );
        c.init();
        c.getBillingInfo().setCcNumber("1234123412341234");
        c.getBillingInfo().setIdToken(null);
        CDGCreditCardAPI api = new CDGCreditCardAPI();
        api.setCDGUrl("https://secure.paymentclearing.com/cgi-bin/rc/ord.cgi");
        api.setVendorId("48495");
        api.setHomePage("http://menuoutlet.com");
        api.setSendRequest(true);
        api.setDebug(true);
        CreditCardResponse resp = api.chargeCard(c, 22, new OrderItem[] {new com.zitego.customer.product.OrderItemShell("testA", 3.45f, 1)});
        System.out.println("Status: "+resp.getStatus());
        System.out.println("Error Msg: "+resp.getErrorMessage());
        System.out.println("Auth Code: "+resp.getAuthCode());
        System.out.println("Total: "+resp.getTotalCharged());
        System.out.println("Shipping: "+resp.getShippingInfo());
        System.out.println("Billing: "+resp.getBillingInfo());
    }

    public CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items) throws CreditCardAPIException
    {
        APIRequest request = getRequest(CDGRequest.CHARGE_CUSTOMER, c, items);
        return getCreditCardResponse(request, "charging the customer's credit card");
    }

    public CreditCardResponse preauthorizeTransaction(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Preauthorize is not supported by this CDG API");
    }

    public CreditCardResponse processPriorAuth(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Process prior authorization is not supported by this CDG API");
    }

    public CreditCardResponse postCredit(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Post credit is not supported by this CDG API");
    }

    public CreditCardResponse voidTransaction(Customer c, String transId, float amt)
    throws CreditCardAPIException
    {
        throw new CreditCardAPIException("Post void is not supported by this CDG API");
    }

    public CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException
    {
        APIRequest request = getRequest(CDGRequest.ADD_CUSTOMER, c, null);
        return getCreditCardResponse(request, "adding the customer's credit card information");
    }

    public CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException
    {
        APIRequest request = getRequest(CDGRequest.EDIT_CUSTOMER, c, null);
        return getCreditCardResponse(request, "editing the customer's credit card information");
    }

    public CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException
    {
        APIRequest request = getRequest(CDGRequest.DELETE_CUSTOMER, c, null);
        return getCreditCardResponse(request, "deleting the customer's credit card information");
    }

    /**
     * For reflection.
     *
     * @param url The url.
     */
    public void setUrl(String url)
    {
        setCDGUrl(url);
    }

    /**
     * Sets the base url.
     *
     * @param url The base url.
     */
    public void setCDGUrl(String url)
    {
        _cdgUrl = url;
    }

    /**
     * Returns the base url.
     *
     * @return String
     */
    public String getCDGUrl()
    {
        return _cdgUrl;
    }

    /**
     * Sets the vault url.
     *
     * @url String The url.
     */
    public void setVaultUrl(String url)
    {
        _vaultUrl = url;
    }

    /**
     * Returns the vault url.
     *
     * @return String
     */
    public String getVaultUrl()
    {
        return _vaultUrl;
    }

    /**
     * Sets whether this is an xml post or not.
     *
     * @param xmlPost The xml post flag.
     */
    public void setXmlPost(boolean flag)
    {
        _xmlPost = flag;
    }

    /**
     * Returns whether this is an xml post or not.
     *
     * @return boolean
     */
    public boolean getXmlPost()
    {
        return _xmlPost;
    }

    /**
     * Sets the vendor id.
     *
     * @id String The uid.
     */
    public void setVendorId(String id)
    {
        _vendorId = id;
    }

    /**
     * Returns the vendor id.
     *
     * @return String
     */
    public String getVendorId()
    {
        return _vendorId;
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
     * Sets the password.
     *
     * @param pass The password.
     */
    public void setPassword(String pass)
    {
        _password = pass;
    }

    /**
     * Returns the vendor password.
     *
     * @return String.
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Sets the home page.
     *
     * @param homePage The home page.
     */
    public void setHomePage(String homePage)
    {
        _homePage = homePage;
    }

    /**
     * Returns the home page.
     *
     * @return String
     */
    public String getHomePage()
    {
        return _homePage;
    }

    private APIRequest getRequest(int command, Customer c, OrderItem[] items)
    throws IllegalArgumentException, CreditCardAPIException
    {
        APIRequest ret = null;
        try
        {
            if (items == null) ret = getRequest( new Object[] { new Integer(command), c } );
            else ret = getRequest( new Object[] { new Integer(command), c, items } );
        }
        catch (IllegalArgumentException iae)
        {
            throw iae;
        }
        catch (CreditCardAPIException cae)
        {
            throw cae;
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred getting the request.", e);
        }
        return ret;
    }

    protected APIRequest getRequest(Object[] args) throws APIException
    {
        int command = ( (Integer)args[0] ).intValue();

        //Get a customer to charge
        Customer c = (Customer)args[1];

        //Figure out if they are pre-existing and build the request based on that
        CDGRequest ret = null;
        if (command == CDGRequest.CHARGE_CUSTOMER && c.getBillingInfo().getIdToken() == null)
        {
            if (_xmlPost) ret = getCDGXmlRequest(c);
            else ret = getCDGHtmlRequest(c);
        }
        else
        {
            ret = getCDGVaultRequest(command, c);
        }

        //Add order items
        if (args.length > 2)
        {
            OrderItem[] items = (OrderItem[])args[2];
            for (int i=0; i<items.length; i++)
            {
                ret.addOrderItem( items[i].getDescription(), items[i].getCost(), items[i].getQuantity() );
            }
        }

        //Add email text
        /*if (args.length > 3)
        {
            String[] emailText = (String[])args[3];
            for (int i=0; i<emailText.length; i++)
            {
                ret.addEmailText( emailText[i] );
            }
        }*/

        ret.setVendorId(_vendorId);
        ret.setVendorPassword(_password);
        ret.setHomePage(_homePage);

        if ( debugging() ) ret.setDebug(true);

        return ret.getAPIRequest();
    }

    private CDGRequest getCDGVaultRequest(int command, Customer c) throws IllegalArgumentException
    {
        CDGVaultRequest ret = new CDGVaultRequest(_vaultUrl);

        if (command == CDGRequest.ADD_CUSTOMER) ret.addField("action", "add");
        else if (command == CDGRequest.EDIT_CUSTOMER) ret.addField("action", "edit");
        else if (command == CDGRequest.DELETE_CUSTOMER) ret.addField("action", "delete");
        else if (command == CDGRequest.CHARGE_CUSTOMER) ret.addField("action", "sale");
        else throw new IllegalArgumentException("Invalid command: "+command);

        String token = c.getBillingInfo().getIdToken();
        //Create a token out of their account number
        if (token == null)
        {
            if (command == CDGRequest.ADD_CUSTOMER)
            {
                //Make sure this is not a new customer
                if (c.getId() == -1) throw new IllegalStateException("Customer must be saved before processing transactions");
                token = c.getAccountNumber();
            }
            else
            {
                throw new IllegalArgumentException("token id is required");
            }
        }
        ret.setRegisteredCustomerData(token, null);
        ret.setCustomerData(c);

        if (command == CDGRequest.ADD_CUSTOMER || command == CDGRequest.EDIT_CUSTOMER)
        {
            BillingInformation billing = c.getBillingInfo();
            String suffix = billing.getCcNumber();
            if (suffix == null) throw new IllegalArgumentException("credit card number is required");
            suffix = suffix.substring(4);
            ret.addField( "cardsuffix", suffix );
        }

        return ret;
    }

    private CDGRequest getCDGXmlRequest(Customer c) throws IllegalArgumentException
    {
        CDGXmlRequest ret = new CDGXmlRequest(_cdgUrl);
        String id = c.getBillingInfo().getIdToken();
        //Registered customer
        if (id != null) ret.setRegisteredCustomerData( id, c.getBillingInfo().getTokenPassword() );
        else ret.setCustomerData(c);

        return ret;
    }

    private CDGRequest getCDGHtmlRequest(Customer c) throws IllegalArgumentException
    {
        CDGHtmlRequest ret = new CDGHtmlRequest(_cdgUrl);
        ret.setCustomerData(c);
        return ret;
    }

    protected APIResponse createResponse(APIRequest request, String text) throws APIException
    {
        if (request instanceof CDGVaultRequest) return new CDGVaultResponse( (CDGVaultRequest)request, text );
        else if (request instanceof CDGXmlRequest) return new CDGXmlResponse(text);
        else return new CDGHtmlResponse( (CDGHtmlRequest)request, text);
    }

    /**
     * Gets a response from cdg given a request. If an error occurs, an APIException is thrown
     * with details.
     *
     * @param request The request.
     * @param errSnippet The detailed error message snippet.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs.
     */
    protected CreditCardResponse getCreditCardResponse(APIRequest request, String errSnippet) throws CreditCardAPIException
    {
        CDGResponse response = null;
        if ( getSendRequest() )
        {
            try
            {
                response = (CDGResponse)getResponse(request, errSnippet);
            }
            catch (APIException ae)
            {
                throw new CreditCardAPIException(ae.getMessage(), ae);
            }
        }
        else
        {
            if (request instanceof CDGVaultRequest) response = new MockCDGVaultResponse( (CDGVaultRequest)request );
            else if (request instanceof CDGXmlRequest) response = new MockCDGXmlResponse( (CDGXmlRequest)request );
            else response = new MockCDGHtmlResponse( (CDGHtmlRequest)request );
            if ( debugging() ) BaseConfigServlet.logError("CC Request (NOT SENT): "+request);
        }
        return response.getCreditCardResponse();
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

    public String getPropertyString()
    {
        StringBuffer ret = new StringBuffer()
            .append( getClass().getName() ).append("=\n")
            .append("  CDGUrl=").append(_cdgUrl).append(",\n")
            .append("  vendorId=").append(_vendorId).append(",\n")
            .append("  homePage=").append(_homePage);
        return ret.toString();
    }
}
