package com.zitego.customer.creditCard.cdg;

import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.markup.IllegalMarkupException;
import com.zitego.markup.MarkupContent;
import com.zitego.markup.TextContent;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;

/**
 * A response from cdg vault. The response will be one of the following:<br>
 * "approve|xxxxxxxx" - Transaction Approved with Authorization Code.<br>
 * "decline|00000001" - Transaction Declined by Bankcard Network.<br>
 * "decline|00000002" - Transaction Not Attempted due to Duplicate Transaction.<br>
 * "successful" - Request Successfully Completed (on non-trans. request).<br>
 * "failed" - Request Failed to Complete (on non-trans. request)<br>
 * <br>
 * All other information is supplied by the request.
 *
 * @author John Glorioso
 * @version $Id: CDGVaultResponse.java,v 1.2 2010/08/23 02:45:50 jglorioso Exp $
 */
public class CDGVaultResponse extends APIResponse implements CreditCardResponse, CDGResponse
{
    /** The response text. */
    private String _responseText;
    /** The CDGVaultRequest. */
    protected CDGVaultRequest _request;
    /** The status. */
    private String _status;
    /** The error message. */
    private String _errorMsg;
    /** The authorization code. */
    private String _authCode;

    /**
     * This is for extending classes to use to set the request.
     *
     * @param request The request.
     */
    protected CDGVaultResponse(CDGVaultRequest request)
    {
        super();
        _request = request;
    }

    /**
     * Creates a new CDGVaultResponse from text.
     *
     * @param request The request.
     * @param text The text response.
     */
    public CDGVaultResponse(CDGVaultRequest request, String text) throws APIException
    {
        super(text, FormatType.TEXT);
        _request = request;
    }

    public UserId getRegisteredCustomerData()
    {
        return new UserId(_request.getField("tokenID"), null);
    }

    public String getEmail()
    {
        return _request.getField("email");
    }

    public ContactInformation getShippingInfo()
    {
        return getBillingInfo();
    }

    public BillingInformation getBillingInfo()
    {
        BillingInformation info = new BillingInformation();
        info.setPartialCcNumber( _request.getField("cardPrefix") );
        info.setFirstName( _request.getField("fname") );
        info.setLastName( _request.getField("lname") );
        info.setAddress1( _request.getField("address") );
        info.setCity( _request.getField("city") );
        info.setStateName( _request.getField("state") );
        info.setPostalCode( _request.getField("zip") );
        info.setCountryName( _request.getField("country") );
        info.setPrimaryPhone( _request.getField("phone") );
        info.setCcNumber( _request.getField("prefix")+_request.getField("suffix") );
        try
        {
            info.setExpDate( _request.getField("cardexpmo")+"/"+_request.getField("cardexpyear") );
        }
        catch (Exception e) { }
        info.setPartialCcNumber( _request.getField("prefix") );
        info.setIdToken( _request.getField("tokenID") );

        return info;
    }

    /**
     * Sets the status. This can only be called by extending classes.
     *
     * @param status The status.
     */
    protected void setStatus(String status)
    {
        _status = status;
    }

    public String getStatus()
    {
        return _status;
    }

    public String getErrorCategory()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return _errorMsg;
    }

    /**
     * Sets the authcode. This can only be called by extending classes.
     *
     * @param code The authcode.
     */
    protected void setAuthCode(String code)
    {
        _authCode = code;
    }

    public String getAuthCode()
    {
        return _authCode;
    }

    public String getAVSResponse()
    {
        return null;
    }

    public String getAVSResponseCategory()
    {
        return null;
    }

    public String getCVV2Response()
    {
        return null;
    }

    public String getRespCode()
    {
        return null;
    }

    public float getTotalCharged()
    {
        return Float.parseFloat( _request.getField("amount") );
    }

    public void parse(Object objToParse, FormatType type) throws IllegalMarkupException, UnsupportedFormatException
    {
        _responseText = objToParse.toString();
        if ( _responseText.startsWith("approve|") )
        {
            setStatus("Approved");
            _authCode = _responseText.substring( _responseText.indexOf("|")+1 );
        }
        else if ( _responseText.startsWith("decline|") )
        {
            _status = "Declined";
            if ( _responseText.endsWith("1") ) _errorMsg = "Transaction Declined by Bankcard Network.";
            else if ( _responseText.endsWith("2") ) _errorMsg = "Transaction Not Attempted due to Duplicate Transaction.";
            else _errorMsg = "Unrecognized decline code: "+_responseText.substring( _responseText.indexOf("|")+1 );
        }
        else if ( _responseText.equalsIgnoreCase("success") )
        {
            _status = "Success";
        }
        else if ( _responseText.equalsIgnoreCase("failed") )
        {
            _status = "Failed";
            _errorMsg = "Failed";
        }
        else
        {
            throw new IllegalMarkupException("Could not parse: "+_responseText);
        }
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

    public CreditCardResponse getCreditCardResponse()
    {
        return this;
    }

    public String getTransactionId()
    {
        return null;
    }
}