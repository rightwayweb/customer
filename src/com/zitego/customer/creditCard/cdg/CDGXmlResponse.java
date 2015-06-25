package com.zitego.customer.creditCard.cdg;

import com.zitego.markup.xml.XmlTag;
import com.zitego.web.thirdPartyAPI.APIException;
import com.zitego.web.thirdPartyAPI.XmlAPIResponse;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;

/**
 * An xml response from cdg.
 *
 * @see com.zitego.customer.creditCard.CreditCardResponse
 * @author John Glorioso
 * @version $Id: CDGXmlResponse.java,v 1.2 2010/08/23 02:45:50 jglorioso Exp $
 */
public class CDGXmlResponse extends XmlAPIResponse implements CreditCardResponse, CDGResponse
{
    /** An xml document to hold all of the data. */
    private CDGXmlResponseDocument _xml;

    /**
     * Creates a new empty CDGXmlResponse.
     */
    public CDGXmlResponse()
    {
        super();
    }


    /**
     * Creates a new CDGXmlResponse from text.
     *
     * @param text The text response.
     */
    public CDGXmlResponse(String text) throws APIException
    {
        super(text);
    }

    protected XmlTag createXmlTag()
    {
        _xml = new CDGXmlResponseDocument();
        return _xml;
    }

    public UserId getRegisteredCustomerData()
    {
        return _xml.getRegisteredCustomerData();
    }

    public String getEmail()
    {
        return _xml.getEmail();
    }

    public ContactInformation getShippingInfo()
    {
        return _xml.getShippingInfo();
    }

    public BillingInformation getBillingInfo()
    {
        return _xml.getBillingInfo();
    }

    public String getStatus()
    {
        return _xml.getStatus();
    }

    public String getErrorCategory()
    {
        return _xml.getErrorCategory();
    }

    public String getErrorMessage()
    {
        return _xml.getErrorMessage();
    }

    public String getAuthCode()
    {
        return _xml.getAuthCode();
    }

    public String getAVSResponse()
    {
        return _xml.getAVSResponse();
    }

    public String getAVSResponseCategory()
    {
        return _xml.getAVSResponseCategory();
    }

    public String getCVV2Response()
    {
        return _xml.getCVV2Response();
    }

    public String getRespCode()
    {
        return null;
    }

    public float getTotalCharged()
    {
        return _xml.getTotalCharged();
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