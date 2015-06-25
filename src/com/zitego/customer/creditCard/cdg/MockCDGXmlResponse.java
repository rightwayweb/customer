package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;

/**
 * A mock xml response from cdg. Nothing is ever sent so the response that is returned is
 * an assumed success. getStatus always returns "OK" and getAuthCode always returns "000000".
 *
 * @see com.zitego.customer.creditCard.CreditCardResponse
 * @author John Glorioso
 * @version $Id: MockCDGXmlResponse.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class MockCDGXmlResponse extends CDGXmlResponse
{
    private CDGXmlRequestDocument _request;

    /**
     * Creates a new MockCDGXmlResponse.
     *
     * @param request The request.
     */
    public MockCDGXmlResponse(CDGXmlRequest request)
    {
        super();
        _request = request.getRequestDocument();
    }

    public UserId getRegisteredCustomerData()
    {
        return _request.getRegisteredCustomerData();
    }

    public String getEmail()
    {
        return _request.getEmail();
    }

    public ContactInformation getShippingInfo()
    {
        return _request.getShippingInfo();
    }

    public BillingInformation getBillingInfo()
    {
        return _request.getBillingInfo();
    }

    public String getStatus()
    {
        return "OK";
    }

    public String getErrorCategory()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return null;
    }

    public String getAuthCode()
    {
        return "000000";
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

    public float getTotalCharged()
    {
        return _request.getTotalCost();
    }
}