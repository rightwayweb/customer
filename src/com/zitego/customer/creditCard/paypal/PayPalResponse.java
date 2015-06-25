package com.zitego.customer.creditCard.paypal;

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
import java.util.Hashtable;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

/**
 * A response from the paypal interface.
 *
 * @author John Glorioso
 * @version $Id: PayPalResponse.java,v 1.3 2012/03/19 15:27:26 jglorioso Exp $
 */
public class PayPalResponse extends APIResponse implements CreditCardResponse
{
    private String _responseText;
    private PayPalRequest _request;
    private Hashtable<String, String> _fields;

    /**
     * Creates a new PayPalResponse from delimited text.
     *
     * @param request The request.
     * @param text The text response.
     */
    public PayPalResponse(PayPalRequest request, String text) throws APIException
    {
        super(text, FormatType.TEXT);
        _request = request;
    }

    public UserId getRegisteredCustomerData()
    {
        return new UserId( _fields.get("PAYERID") );
    }

    public String getToken()
    {
        return _fields.get("TOKEN");
    }

    public String getPayerId()
    {
        return _fields.get("PAYERID");
    }

    public String getEmail()
    {
        return _fields.get("EMAIL");
    }

    public ContactInformation getShippingInfo()
    {
        ContactInformation info = new ContactInformation();
        info.setEmail( _fields.get("EMAIL") );
        String name = _fields.get("PAYMENTREQUEST_0_SHIPTONAME");
        int index = name.indexOf(" ");
        if (index > -1)
        {
            info.setFirstName( name.substring(0, index) );
            info.setLastName( name.substring(index+1) );
        }
        else
        {
            info.setFirstName(name);
        }
        info.setAddress1( _fields.get("PAYMENTREQUEST_0_SHIPTOSTREET") );
        info.setAddress2( _fields.get("PAYMENTREQUEST_0_SHIPTOSTREET2") );
        info.setCity( _fields.get("PAYMENTREQUEST_0_SHIPTOCITY") );
        info.setStateAbbreviation( _fields.get("PAYMENTREQUEST_0_SHIPTOSTATE") );
        info.setPostalCode( _fields.get("PAYMENTREQUEST_0_SHIPTOZIP") );
        info.setCountryAbbreviation( _fields.get("PAYMENTREQUEST_0_SHIPTOCOUNTRY_CODE") );
        return info;
    }

    public BillingInformation getBillingInfo()
    {
        BillingInformation info = new BillingInformation();
        return info;
    }

    public boolean wasSuccess()
    {
        return "Success".equals(_fields.get("ACK"));
    }

    public String getStatus()
    {
        if ( _request.getField("METHOD").equals("DoExpressCheckoutPayment") ) return _fields.get("PAYMENTINFO_0_PAYMENTSTATUS");
        else return _fields.get("TOKENSTATUS");
    }

    public String getErrorCategory()
    {
        return _fields.get("L_ERRORCODE0");
    }

    public String getErrorMessage()
    {
        return _fields.get("L_LONGMESSAGE0");
    }

    public String getAuthCode()
    {
        return null;
    }

    public String getTransactionId()
    {
        return _fields.get("PAYMENTINFO_0_TRANSACTIONID");
    }

    public String getAVSResponse()
    {
        return null;
    }

    public String getRespCode()
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

    public float getTotalCharged()
    {
        return _request.getAmount();
    }

    public void parse(Object objToParse, FormatType type) throws IllegalMarkupException, UnsupportedFormatException
    {
        try
        {
            _responseText = URLDecoder.decode(objToParse.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new IllegalMarkupException( uee.getMessage() );
        }
        _fields = new Hashtable<String, String>();
        String[] fields = TextUtils.split(_responseText, '&');
        for (int i=0; i<fields.length; i++)
        {
            int index = fields[i].indexOf("=");
            if (index > -1) _fields.put( fields[i].substring(0, index), fields[i].substring(index+1) );
            else _fields.put(fields[i], "");
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
}
