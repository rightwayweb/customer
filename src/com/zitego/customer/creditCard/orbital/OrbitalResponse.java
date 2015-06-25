package com.zitego.customer.creditCard.orbital;

import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.APIResponse;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.markup.MarkupContent;
import com.zitego.markup.TextContent;
import com.zitego.markup.IllegalMarkupException;
import com.zitego.markup.xml.XmlTag;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;
import java.text.ParseException;

/**
 * A response from the orbital payment gateway.
 *
 * @author John Glorioso
 * @version $Id: OrbitalResponse.java,v 1.9 2010/10/31 17:16:22 jglorioso Exp $
 */
public class OrbitalResponse extends APIResponse implements CreditCardResponse
{
    private XmlTag _xml;
    private OrbitalRequest _request;

    /**
     * Creates a new OrbitalResponse from delimited text.
     *
     * @param request The request.
     * @param text The text response.
     */
    public OrbitalResponse(OrbitalRequest request, String text) throws APIException
    {
        super(text, FormatType.XML);
        _request = request;
    }

    public UserId getRegisteredCustomerData()
    {
        return null;
    }

    public String getEmail()
    {
        return null;
    }

    public ContactInformation getShippingInfo()
    {
        ContactInformation info = new ContactInformation();
        String name = _request.getField("AVSname");
        if (name != null)
        {
            int index = name.indexOf(" ");
            if (index > -1)
            {
                info.setFirstName( name.substring(0, index) );
                info.setLastName( name.substring(index+1) );
            }
            else
            {
                info.setLastName(name);
            }
        }
        info.setAddress1( _request.getField("AVSaddress1") );
        info.setAddress2( _request.getField("AVSaddress2") );
        info.setCity( _request.getField("AVScity") );
        info.setStateName( _request.getField("AVSstate") );
        info.setPostalCode( _request.getField("AVSzip") );
        info.setCountryName( _request.getField("AVScountryCode") );
        return info;
    }

    public BillingInformation getBillingInfo()
    {
        BillingInformation info = new BillingInformation();
        info.setNameOnCard( getField("AVSname") );
        info.setAddress1( getField("AVSaddress1") );
        info.setAddress2( getField("AVSaddress2") );
        info.setCity( getField("AVScity") );
        info.setStateName( getField("AVSstate") );
        info.setPostalCode( getField("AVSzip") );
        info.setCountryName( getField("AVScountryCode") );
        info.setPrimaryPhone( getField("AVSphone") );
        info.setCcNumber( _request.getField("AccountNum") );
        try
        {
            info.setExpDate( OrbitalRequest.EXP_FORMAT.parse(_request.getField("Exp")) );
        }
        catch (ParseException e) { }
        info.setCVV2Number( _request.getField("CardSecVal") );

        return info;
    }

    public String getStatus()
    {
        //Note 100 is specific to Chase. When there is another Orbital gateway host, we will need
        //to account for that value instead
        return ("1".equals(getField("ApprovalStatus")) && "100".equals(getField("HostRespCode")) ? "Approved" : "Declined");
    }

    public String getErrorCategory()
    {
        return null;
    }

    public String getErrorMessage()
    {
        if (!"1".equals(getField("ApprovalStatus")) )
        {
            return getField("StatusMsg");
        }
        else
        {
            //Check avs (note these are Chase specific values). Need to be generic and need to have configurable allowed values
            String avs = getAVSResponse();
            if ( "i1".equalsIgnoreCase(avs) || "i3".equalsIgnoreCase(avs) || "i4".equalsIgnoreCase(avs) ) return null;//|| "i8".equalsIgnoreCase(avs) ) return null;
            else return "Invalid Address Verification: " + getAVSResponseCategory();
        }
    }

    public String getAuthCode()
    {
        return getField("AuthCode");
    }

    public String getTransactionId()
    {
        return getField("TxRefNum");
    }

    public String getAVSResponse()
    {
        return getField("HostAVSRespCode");
    }

    public String getAVSResponseCategory()
    {
        String avs = getAVSResponse();
        //Note, these are Chase specific. Need to Genericize
        if (avs != null)
        {
            if ( "ID".equalsIgnoreCase(avs) ) avs = "Not Verified - Issuer does not participate in AVS";
            else if ( "IS".equalsIgnoreCase(avs) ) avs = "System Unavailable";
            else if ( "IU".equalsIgnoreCase(avs) ) avs = "Address Info Unavailable";
            else avs = "Bad Address";
        }
        return avs;
    }

    public String getRespCode()
    {
        return getField("HostRespCode");
    }

    public String getCVV2Response()
    {
        return getField("HostCVV2RespCode");
    }

    public float getTotalCharged()
    {
        return _request.getAmount();
    }

    public void parse(Object objToParse, FormatType type) throws IllegalMarkupException, UnsupportedFormatException
    {
        _xml = new XmlTag();
        _xml.setValidateXml(false);
        _xml.parse(objToParse, type);
    }

    public String format(FormatType type) throws UnsupportedFormatException
    {
        return _xml.format(type);
    }

    public MarkupContent getAsMarkupContent()
    {
        return _xml;
    }

    private String getField(String field)
    {
        XmlTag tag = _xml.getFirstOccurrenceOf("Response");
        if (tag != null) tag = tag.getFirstOccurrenceOf("NewOrderResp");
        if (tag == null) tag = _xml.getFirstOccurrenceOf("QuickResp");
        if (tag != null)
        {
            String val = tag.getChildValue(field);
            if (val != null) val = val.trim();
            return val;
        }
        else return null;
    }
}
