package com.zitego.customer.creditCard.cdg;

import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.markup.MarkupContent;
import com.zitego.markup.IllegalMarkupException;
import com.zitego.markup.html.tag.Html;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * A response from the cdg html interface. The response will be either success or
 * failure. This is stored in the title tag of the html response. If the tag title
 * is "Error" then the document is parsed for the reason.
 *
 * @author John Glorioso
 * @version $Id: CDGHtmlResponse.java,v 1.2 2010/08/23 02:45:50 jglorioso Exp $
 */
public class CDGHtmlResponse extends APIResponse implements CreditCardResponse, CDGResponse
{
    private String _responseText;
    protected CDGHtmlRequest _request;
    private String _status;
    private String _errorMsg;
    private String _authCode;

    /**
     * This is for extending classes to use to set the request.
     *
     * @param request The request.
     */
    protected CDGHtmlResponse(CDGHtmlRequest request)
    {
        super();
        _request = request;
    }

    /**
     * Creates a new CDGHtmlResponse from html text.
     *
     * @param request The request.
     * @param text The html text response.
     */
    public CDGHtmlResponse(CDGHtmlRequest request, String text) throws APIException
    {
        super(text, FormatType.HTML);
        _request = request;
    }

    public UserId getRegisteredCustomerData()
    {
        return null;
    }

    public String getEmail()
    {
        return _request.getField("email");
    }

    public ContactInformation getShippingInfo()
    {
        ContactInformation info = new ContactInformation();
        info.setFirstName( _request.getField("sfname") );
        info.setLastName( _request.getField("slname") );
        info.setAddress1( _request.getField("saddr") );
        info.setCity( _request.getField("scity") );
        info.setStateName( _request.getField("sstate") );
        info.setPostalCode( _request.getField("szip") );
        info.setCountryName( _request.getField("sctry") );
        return info;
    }

    public BillingInformation getBillingInfo()
    {
        BillingInformation info = new BillingInformation();
        info.setFirstName( _request.getField("first_name") );
        info.setLastName( _request.getField("last_name") );
        info.setAddress1( _request.getField("address") );
        info.setCity( _request.getField("city") );
        info.setStateName( _request.getField("state") );
        info.setPostalCode( _request.getField("zip") );
        info.setCountryName( _request.getField("country") );
        info.setPrimaryPhone( _request.getField("phone") );
        info.setEmail( _request.getField("email") );
        info.setCcNumber( _request.getField("ccnum") );
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        try
        {
            info.setExpDate( sdf.parse(_request.getField("ccmo")+" "+_request.getField("ccyr")) );
        }
        catch (ParseException e) { }

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
        return _request.getTotal();
    }

    public void parse(Object objToParse, FormatType type) throws IllegalMarkupException, UnsupportedFormatException
    {
        _responseText = objToParse.toString();
        int index = _responseText.indexOf("<!-- BEGIN ERROR");
        if (index == -1)
        {
            _status = "Approved";
            index = _responseText.indexOf("authcode=");
            _authCode = _responseText.substring( index+9, _responseText.indexOf("&", index) );
        }
        else
        {
            _status = "Declined";
            _errorMsg = _responseText.substring( index, _responseText.indexOf("<!-- END ERROR", index) );
        }
    }

    public String format(FormatType type) throws UnsupportedFormatException
    {
        return _responseText;
    }

    public MarkupContent getAsMarkupContent()
    {
        Html ret = new Html();
        try
        {
            ret.parse(_responseText, FormatType.HTML);
        }
        catch (UnsupportedFormatException ufe) { }
        return ret;
    }

    public CreditCardResponse getCreditCardResponse()
    {
        return this;
    }

    /**
     * Returns null.
     *
     * @return String
     */
    public String getTransactionId()
    {
        return null;
    }
}