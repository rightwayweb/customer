package com.zitego.customer.creditCard;

import com.zitego.web.thirdPartyAPI.ThirdPartyAPIManager;
import com.zitego.customer.product.OrderItem;
import com.zitego.customer.Customer;
import com.zitego.util.StaticProperties;

/**
 * <p>This is a manager class to handle transactions to a 3rd party service to
 * make transactions to credit cards. The manager relies on some
 * configurations being set in order to function properly. Namely, the
 * CreditCardAPI class needs to be setup properly. If this class is null, then
 * we look for a property configuration in BaseConfigServlet.getWebappProperties()
 * called "credit_card.api". If this property does not exist, we then check
 * StaticProperties. If it is not there either then an
 * IllegalStateException is thrown from whichever method is being called.</p>
 *
 * <p>The properties should be in the following format.<br>
 * <pre>
 * credit_card.api=<classpath>=\
 *                 property1=value1,\
 *                 property2=value2,\
 *                 etc...
 * </pre>
 * The classpath is the fully qualified class to the path of the domain api.
 * The property/value pairs are optional and will set a string property of
 * the given property name to the given value. The property uses java bean
 * reflection and looks for a setProperty method that matches the property.
 *
 * @see CreditCardAPI
 * @author John Glorioso
 * @version $Id: CreditCardManager.java,v 1.3 2010/08/05 02:07:56 jglorioso Exp $
 */
public class CreditCardManager extends ThirdPartyAPIManager
{
    private static final String PROPERTY_NAME = "credit_card.api";

    public static void main(String[] args) throws Exception
    {

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
     * @throws IllegalArgumentException if the customer is null.
     */
    public static CreditCardResponse chargeCard(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");
        if (items == null || items.length == 0) throw new IllegalArgumentException("Order items must be provided");

        return getAPI().chargeCard(c, orderId, items);
    }

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
    public static CreditCardResponse preauthorizeTransaction(Customer c, long orderId, OrderItem[] items)
    throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");
        if (items == null || items.length == 0) throw new IllegalArgumentException("Order items must be provided");

        return getAPI().preauthorizeTransaction(c, orderId, items);
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
    public static CreditCardResponse processPriorAuth(Customer c, String transId, float amt)
    throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");
        if (transId == null) throw new IllegalArgumentException("Transaction id must not be null");

        return getAPI().processPriorAuth(c, transId, amt);
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
    public static CreditCardResponse postCredit(Customer c, String transId, float amt)
    throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");
        if (transId == null) throw new IllegalArgumentException("Transaction id must not be null");

        return getAPI().postCredit(c, transId, amt);
    }

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
    public static CreditCardResponse voidTransaction(Customer c, String transId, float amt)
    throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");
        if (transId == null) throw new IllegalArgumentException("Transaction id must not be null");

        return getAPI().voidTransaction(c, transId, amt);
    }

    /**
     * Stores the customer information with the third party api (or somewhere) and returns
     * a usercode/password in the form of a UserId in the CreditCardResponse under
     * getRegisteredCustomerData(). The userId is automatically already set
     * in the customer, but the save() method is not called.
     *
     * @param c The customer to store.
     * @return CreditCardResponse
     * @throws IllegalArgumentException if the customer is null.
     * @throws CreditCardAPIException if an error occurs querying the api.
     */
    public static CreditCardResponse addCustomer(Customer c) throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");

        return getAPI().addCustomer(c);
    }

    /**
     * Edits the customer information for the given customer and returns a CreditCardResponse.
     *
     * @param c The customer to edit.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public static CreditCardResponse editCustomer(Customer c) throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");

        return getAPI().editCustomer(c);
    }

    /**
     * Deletes the customer from the 3rd party api and returns a CreditCardResponse.
     *
     * @param c The customer to delete.
     * @return CreditCardResponse
     * @throws CreditCardAPIException if an error occurs querying the api.
     * @throws IllegalArgumentException if the customer is null.
     */
    public static CreditCardResponse deleteCustomer(Customer c) throws CreditCardAPIException, IllegalArgumentException
    {
        if (c == null) throw new IllegalArgumentException("Customer must be provided");

        return getAPI().deleteCustomer(c);
    }

    private static CreditCardAPI getAPI()
    {
        return (CreditCardAPI)getAPI(PROPERTY_NAME);
    }

    public static boolean isDebugging()
    {
        return getAPI().debugging();
    }
}