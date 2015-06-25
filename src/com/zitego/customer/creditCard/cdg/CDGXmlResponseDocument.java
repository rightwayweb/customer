package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.customer.creditCard.CreditCardResponse;
import com.zitego.customer.creditCard.UserId;
import com.zitego.markup.xml.XmlTag;

/**
 * This is the CDG xml document that is returned from processing a sale.
 * It essentiall contains the same information as the request document
 * with some added data in the TransactionData section to specify the
 * status, errors, authorization code, etc.
 *
 * @author John Glorioso
 * @version $Id: CDGXmlResponseDocument.java,v 1.2 2010/08/23 02:51:25 jglorioso Exp $
 */
public class CDGXmlResponseDocument extends CDGXmlRequestDocument implements CreditCardResponse
{
    private String _status;
    private String _errorCategory;
    private String _errorMessage;
    private String _authCode;
    private String _avsResponse;
    private String _avsResponseCategory;
    private String _cvv2Response;
    private float _totalCharged = -1;

    public static void main(String[] args) throws Exception
    {
        CDGXmlResponseDocument doc = new CDGXmlResponseDocument();
        doc.parse(args[0], com.zitego.format.FormatType.XML);
        //System.out.println("Parsed:");
        //System.out.println( doc.format(com.zitego.format.FormatType.XML) );
        System.out.println("Values:");
        System.out.println("Email: "+doc.getEmail());
        System.out.println("Shipping Info: "+doc.getShippingInfo());
        System.out.println("Billing Info: "+doc.getBillingInfo());
        System.out.println("Status: "+doc.getStatus());
        System.out.println("ErrorCategory: "+doc.getErrorCategory());
        System.out.println("AuthCode: "+doc.getAuthCode());
        System.out.println("AVS Response: "+doc.getAVSResponse());
        System.out.println("AVS Response Category: "+doc.getAVSResponseCategory());
        System.out.println("CVV2 Response: "+doc.getCVV2Response());
        System.out.println("Total Charged: "+doc.getTotalCharged());
    }

    /**
     * Creates a new CDGResponse document.
     */
    CDGXmlResponseDocument()
    {
        super("SaleResponse");
    }

    public UserId getRegisteredCustomerData()
    {
        if (_registeredCustomerData != null)
        {
            return new UserId( _registeredCustomerData.getCustomerCode(), _registeredCustomerData.getPassword() );
        }
        else
        {
            return null;
        }
    }

    public String getEmail()
    {
        if (_customerData != null) return _customerData.getEmail();
        else return null;
    }

    public ContactInformation getShippingInfo()
    {
        if (_customerData != null) return _customerData.getShippingInfo();
        return null;
    }

    public BillingInformation getBillingInfo()
    {
        if (_customerData != null) return _customerData.getBillingInfo();
        else return null;
    }

    public String getStatus()
    {
        if (_status == null)
        {
            XmlTag tag = getFirstOccurrenceOf("Status");
            if (tag != null) _status = tag.getValue();
        }
        return _status;
    }

    public String getErrorCategory()
    {
        if (_errorCategory == null)
        {
            XmlTag tag = getFirstOccurrenceOf("ErrorCategory");
            if (tag != null) _errorCategory = tag.getValue();
        }
        return _errorCategory;
    }

    public String getErrorMessage()
    {
        if (_errorMessage == null)
        {
            XmlTag tag = getFirstOccurrenceOf("ErrorMessage");
            if (tag != null) _errorMessage = tag.getValue();
        }
        return _errorMessage;
    }

    public String getAuthCode()
    {
        if (_authCode == null) _authCode = _transactionData.getChildValue("AuthCode");
        return _authCode;
    }

    public String getAVSResponse()
    {
        if (_avsResponse == null) _avsResponse = _transactionData.getChildValue("AVSResponse");
        return _avsResponse;
    }

    public String getAVSResponseCategory()
    {
        if (_avsResponseCategory == null) _avsResponseCategory = _transactionData.getChildValue("AVSResponseCategory");
        return _avsResponseCategory;
    }

    public String getCVV2Response()
    {
        if (_cvv2Response == null) _cvv2Response = _transactionData.getChildValue("CVV2Response");
        return _cvv2Response;
    }

    public float getTotalCharged()
    {
        if (_totalCharged < 0)
        {
            try
            {
                String val = _transactionData.getChildValue("Total");
                if (val != null) _totalCharged = Float.parseFloat(val);
            }
            catch (NumberFormatException nfe) { }
        }
        return _totalCharged;
    }

    public String getTransactionId()
    {
        return null;
    }

    public String getRespCode()
    {
        return null;
    }
}