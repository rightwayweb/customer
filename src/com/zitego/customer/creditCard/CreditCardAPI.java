package com.zitego.customer.creditCard;

import com.zitego.web.thirdPartyAPI.ThirdPartyAPI;
import com.zitego.web.thirdPartyAPI.APIRequest;
import com.zitego.customer.Customer;
import com.zitego.customer.product.OrderItem;

/**
 * This is an abstract class that needs to be defined to specify how specific
 * actions are to be performed against a 3rd party credit card service.
 *
 * @author John Glorioso
 * @version $Id: CreditCardAPI.java,v 1.4 2010/08/05 02:04:54 jglorioso Exp $
 */
public abstract class CreditCardAPI extends ThirdPartyAPI
{
    /** Whether or not we should actually send the request. Default is true. */
    private boolean _sendRequest = true;

    /**
     * Charges the given amount to the card by making a call to the 3rd party credit card
     * payment gateway.
     *
     * @param c The customer to charge to.
     * @param items The items to order.
     * @param emailLines The email lines to send.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @deprecated Use chargeCart(Customer c, long orderId, OrderItem[] items)
     */
    public CreditCardResponse chargeCard(Customer c, OrderItem[] items, String[] emailLines)
    throws CreditCardAPIException
    {
        return chargeCard(c, -1, items);
    }

    /**
     * Charges the given amount to the card by making a call to the 3rd party credit card
     * payment gateway.
     *
     * @param c The customer to charge to.
     * @param orderId The order id.
     * @param items The items to order.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     */
    public abstract CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException;

    /**
     * Sends a transaction in for pre-authorization.
     *
     * @param c The customer to preauth to.
     * @param orderId The order id.
     * @param items The items to order.
     * @param emailLines Any lines of email text to send in the receipt letter.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public abstract CreditCardResponse preauthorizeTransaction(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException;

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
    public abstract CreditCardResponse processPriorAuth(Customer c, String transId, float amt)
    throws CreditCardAPIException;

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
    public abstract CreditCardResponse postCredit(Customer c, String transId, float amt)
    throws CreditCardAPIException;

    /**
     * Voids a transaction not yet sent for settlement.
     *
     * @param c The customer to process the void for.
     * @param transId The transaction id of the transaction.
     * @param amt The amount to void.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public abstract CreditCardResponse voidTransaction(Customer c, String transId, float amt)
    throws CreditCardAPIException, IllegalArgumentException;

    /**
     * Stores the customer information with the third party api (or somewhere) and returns
     * a usercode/password in the form of a UserId in the CreditCardResponse under
     * getRegisteredCustomerData(). The userId is automatically already set
     * in the customer, but the save() method is not called.
     *
     * @param Customer The customer to store.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     */
    public abstract CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException;

    /**
     * Edits the customer information for the given customer and returns a CreditCardResponse.
     *
     * @param Customer The customer to edit.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     */
    public abstract CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException;

    /**
     * Deletes the customer from the 3rd party api and returns a CreditCardResponse.
     *
     * @param Customer The customer to delete.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     */
    public abstract CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException;

    /**
     * Gets a response from cdg given a request. If an error occurs, an APIException is thrown
     * with details.
     *
     * @param APIRequest The request.
     * @param String The detailed error message snippet.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs.
     */
    protected abstract CreditCardResponse getCreditCardResponse(APIRequest request, String errSnippet) throws CreditCardAPIException;

    /**
     * Returns a property string of this credit card api in the format of:
     * <pre>
     * <classpath>=\
     *  prop1=<prop>,\
     *  prop2=<prop>,\
     *  ...
     * </pre>
     *
     * @return String
     */
    public abstract String getPropertyString();

    /**
     * Sets the window in seconds allowed between duplicate transactions.
     *
     * @param sec The number of seconds between duplicates.
     */
    public abstract void setDupWindowSec(String sec);

    /**
     * Sets the merchant login.
     *
     * @url String The login.
     */
    public abstract void setLogin(String login);

    /**
     * Sets the password for credits and authorizations.
     *
     * @param pass The password.
     */
    public abstract void setPassword(String pass);

    /**
     * Sets whether or not we should send the request over http. If not, then all transactions
     * are assumed to succeed.
     *
     * @param send Should be "true" or "false".
     */
    public void setSendRequest(String send)
    {
        setSendRequest( new Boolean(send).booleanValue() );
    }

    /**
     * Sets whether or not we should send the request over http. If not, then all transactions
     * are assumed to succeed.
     *
     * @param send Whether to send the requests.
     */
    public void setSendRequest(boolean send)
    {
        _sendRequest = send;
    }

    /**
     * Returns whether or not we will actually send a request or not when a method is called.
     *
     * @return boolean
     */
    public boolean getSendRequest()
    {
        return _sendRequest;
    }

    /**
     * Returns whether or not state abbreviation is required. Default is false.
     *
     * @return boolean
     */
    public boolean isStateAbbreviationRequired()
    {
        return false;
    }
}
