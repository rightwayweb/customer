package com.zitego.customer.creditCard.authnet;

import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.markup.MarkupContent;
import com.zitego.markup.TextContent;
import com.zitego.markup.IllegalMarkupException;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;
import com.zitego.util.TextUtils;
import java.text.ParseException;

/**
 * A response from the authorize.net aim interface. The response is a delimited string of information
 * specifying the results. The string can be delimited with any character. Additionally, each field can
 * be enclosed with a character such as a quote. See below for the fields.
 *
 * @author John Glorioso
 * @version $Id: AimResponse.java,v 1.2 2010/08/23 02:45:50 jglorioso Exp $
 */
public class AimResponse extends APIResponse implements CreditCardResponse
{
    /** The response code field. */
    public static int RESPONSE_CODE = 0;
    /** The response subcode field. */
    public static int RESPONSE_SUBCODE = 1;
    /** The response reason code field. */
    public static int RESPONSE_REASON_CODE = 2;
    /** The response reason text field. */
    public static int RESPONSE_REASON_TEXT = 3;
    /** The approval code field. */
    public static int APPROVAL_CODE = 4;
    /** The avs result code field. */
    public static int AVS_RESULT_CODE = 5;
    /** The transaction id field. */
    public static int TRANSACTION_ID = 6;
    /** The invoice number field. */
    public static int INVOICE_NUMBER = 7;
    /** The description field. */
    public static int DESCRIPTION = 8;
    /** The amount field. */
    public static int AMOUNT = 9;
    /** The method field. */
    public static int METHOD = 10;
    /** The transaction type field. */
    public static int TRANSACTION_TYPE = 11;
    /** The customer id field. */
    public static int CUSTOMER_ID = 12;
    /** The card holder's first name field. */
    public static int CARDHOLDER_FIRST_NAME = 13;
    /** The card holder's last name field. */
    public static int CARDHOLDER_LAST_NAME = 14;
    /** The company field. */
    public static int COMPANY = 15;
    /** The billing address field. */
    public static int ADDRESS = 16;
    /** The city field. */
    public static int CITY = 17;
    /** The state field. */
    public static int STATE = 18;
    /** The zipcode field. */
    public static int ZIPCODE = 19;
    /** The country field. */
    public static int COUNTRY = 20;
    /** The phone number field. */
    public static int PHONE = 21;
    /** The fax number field. */
    public static int FAX = 22;
    /** The email field. */
    public static int EMAIL = 23;
    /** The ship to first name field. */
    public static int SHIPPING_FIRST_NAME = 24;
    /** The shipping last name field. */
    public static int SHIPPING_LAST_NAME = 25;
    /** The ship to company field. */
    public static int SHIPPING_COMPANY = 26;
    /** The ship to address. */
    public static int SHIPPING_ADDRESS = 27;
    /** The ship to city field. */
    public static int SHIPPING_CITY = 28;
    /** The ship to state field. */
    public static int SHIPPING_STATE = 29;
    /** The ship to zipcode field. */
    public static int SHIPPING_ZIPCODE = 30;
    /** The ship to country field. */
    public static int SHIPPING_COUNTRY = 31;
    /** The tax amount field. */
    public static int TAX_AMOUNT = 32;
    /** The duty amount field. */
    public static int DUTY_AMOUNT = 33;
    /** The freight amount field. */
    public static int FREIGHT_AMOUNT = 34;
    /** The tax exempt flag field. */
    public static int TAX_EXEMPT = 35;
    /** The PO number field. */
    public static int PO_NUMBER = 36;
    /** The MD5 hash field. */
    public static int MD5_HASH = 37;
    /** The card code response field. */
    public static int CARD_CODE_RESPONSE = 38;
    /** The CAVV response code field. */
    public static int CAVV_RESPONSE = 39;

    private String _responseText;
    private AimRequest _request;
    private String[] _fields;

    /**
     * Creates a new AimResponse from delimited text.
     *
     * @param request The request.
     * @param text The text response.
     */
    public AimResponse(AimRequest request, String text) throws APIException
    {
        super(text, FormatType.TEXT);
        _request = request;
    }

    public UserId getRegisteredCustomerData()
    {
        return null;
    }

    public String getEmail()
    {
        return getField(EMAIL);
    }

    public ContactInformation getShippingInfo()
    {
        ContactInformation info = new ContactInformation();
        info.setFirstName( getField(SHIPPING_FIRST_NAME) );
        info.setLastName( getField(SHIPPING_LAST_NAME) );
        info.setAddress1( getField(SHIPPING_ADDRESS) );
        info.setCity( getField(SHIPPING_CITY) );
        info.setStateName( getField(SHIPPING_STATE) );
        info.setPostalCode( getField(SHIPPING_ZIPCODE) );
        info.setCountryName( getField(SHIPPING_COUNTRY) );
        return info;
    }

    public BillingInformation getBillingInfo()
    {
        BillingInformation info = new BillingInformation();
        info.setFirstName( getField(CARDHOLDER_FIRST_NAME) );
        info.setLastName( getField(CARDHOLDER_LAST_NAME) );
        info.setAddress1( getField(ADDRESS) );
        info.setCity( getField(CITY) );
        info.setStateName( getField(STATE) );
        info.setPostalCode( getField(ZIPCODE) );
        info.setCountryName( getField(COUNTRY) );
        info.setPrimaryPhone( getField(PHONE) );
        info.setEmail( getField(EMAIL) );
        info.setCcNumber( _request.getField("x_card_num") );
        try
        {
            info.setExpDate( AimRequest.EXP_FORMAT.parse(_request.getField("x_exp_date")) );
        }
        catch (ParseException e) { }
        info.setCVV2Number( _request.getField("x_card_code") );
        info.setNameOnCard( getField(CARDHOLDER_FIRST_NAME) + " " + getField(CARDHOLDER_LAST_NAME) );

        return info;
    }

    public String getStatus()
    {
        return ("1".equals( getField(RESPONSE_CODE) ) ? "Approved" : "Declined");
    }

    public String getErrorCategory()
    {
        return (!"1".equals( getField(RESPONSE_CODE) ) ? getField(RESPONSE_REASON_CODE) : null);
    }

    public String getErrorMessage()
    {
        return (!"1".equals( getField(RESPONSE_CODE) ) ? getField(RESPONSE_REASON_TEXT) : null);
    }

    public String getAuthCode()
    {
        return getField(APPROVAL_CODE);
    }

    public String getTransactionId()
    {
        return getField(TRANSACTION_ID);
    }

    public String getAVSResponse()
    {
        return getField(AVS_RESULT_CODE);
    }

    public String getAVSResponseCategory()
    {
        return null;
    }

    public String getCVV2Response()
    {
        return getField(CARD_CODE_RESPONSE);
    }

    public String getRespCode()
    {
        return null;
    }

    public float getTotalCharged()
    {
        return _request.getAmount();
    }

    public void parse(Object objToParse, FormatType type) throws IllegalMarkupException, UnsupportedFormatException
    {
        _responseText = objToParse.toString();
        //TO DO - Make this more robust - Need to change in AimRequest to if changed here
        _fields = TextUtils.split(_responseText, '|');
    }

    public String format(FormatType type) throws UnsupportedFormatException
    {
        return _responseText;
    }

    public MarkupContent getAsMarkupContent()
    {
        TextContent ret = new TextContent();
        ret.setText(_responseText);
        return ret;
    }

    /**
     * Returns the response value of the requested field.
     *
     * @param field The field to return.
     * @return String
     */
    public String getField(int field)
    {
        if (field < 0 || field >= _fields.length) return null;
        else return _fields[field];
    }
}