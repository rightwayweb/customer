package com.zitego.customer.creditCard;

import com.zitego.customer.BillingInformation;
import com.zitego.customer.ContactInformation;

/**
 * An interface for response objects to implement in order to return
 * credit card transaction response information.
 *
 * @author John Glorioso
 * @version $Id: CreditCardResponse.java,v 1.2 2010/08/23 02:45:48 jglorioso Exp $
 */
public interface CreditCardResponse
{
    /**
     * Returns the registered customer data as a UserId object.
     *
     * @return UserId
     */
    public UserId getRegisteredCustomerData();

    /**
     * Returns the customer's email address.
     *
     * @return String
     */
    public String getEmail();

    /**
     * Returns the shipping information as a ContactInformation object. If there
     * is no shipping information, null is returned.
     *
     * @return ContactInformation
     */
    public ContactInformation getShippingInfo();

    /**
     * Returns the billing information as a BillingInformation object.
     *
     * @return BillingInformation
     */
    public BillingInformation getBillingInfo();

    /**
     * Returns the status of the transaction as a string.
     *
     * @return String
     */
    public String getStatus();

    /**
     * Returns the error category (if applicable).
     *
     * @return String
     */
    public String getErrorCategory();

    /**
     * Returns the error message.
     *
     * @return String
     */
    public String getErrorMessage();

    /**
     * Returns the authorization code.
     *
     * @return String
     */
    public String getAuthCode();

    /**
     * Returns the transaction id.
     *
     * @return String
     */
    public String getTransactionId();

    /**
     * Returns the AVS response (if applicable).
     *
     * @return String
     */
    public String getAVSResponse();

    /**
     * Returns the AVSResponse category (if applicable).
     *
     * @return String
     */
    public String getAVSResponseCategory();

    /**
     * Returns the CVV2Response (if applicable).
     *
     * @return String
     */
    public String getCVV2Response();

    /**
     * Returns the ResponseCode (if applicable).
     *
     * @return String
     */
    public String getRespCode();

    /**
     * Returns the total amount charged.
     *
     * @return float
     */
    public float getTotalCharged();
}