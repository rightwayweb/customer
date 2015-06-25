package com.zitego.customer.creditCard.orbital;

import com.zitego.customer.creditCard.CreditCardType;
import com.zitego.customer.Customer;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.web.thirdPartyAPI.XmlAPIRequest;
import com.zitego.markup.xml.XmlTag;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * This is a request to the orbital gateway. This will send a post
 * and read back the response parsing it to determine whether the transaction succeeded.
 *
 * @author John Glorioso
 * @version $Id: OrbitalRequest.java,v 1.4 2010/08/01 23:34:19 jglorioso Exp $
 */
public class OrbitalRequest extends XmlAPIRequest
{
    public static final int CHARGE = 0;
    public static final int PREAUTH = 1;
    public static final int PROCESS_PRIOR_PREAUTH = 2;
    public static final int REFUND = 4;
    public static final int VOID = 5;
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0");
    public static final SimpleDateFormat EXP_FORMAT = new SimpleDateFormat("MMyy");
    private String _rootTag;
    private float _amount = 0f;
    private int _type = CHARGE;
    private Hashtable<String, String> _fields = new Hashtable<String, String>();

    /**
     * Creates a new OrbitalRequest.
     *
     * @param url The url to connect to.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public OrbitalRequest(String url) throws IllegalArgumentException
    {
        this(url, "NewOrder");
    }

    /**
     * Creates a new OrbitalRequest with the root tag.
     *
     * @param url The url to connect to.
     * @param rootTag The root tag.
     * @throws IllegalArgumentException if the url is null or has an invalid format.
     */
    public OrbitalRequest(String url, String rootTag) throws IllegalArgumentException
    {
        super(url);
        _rootTag = rootTag;
        getPostData().setEncode(false);
        getPostData().setContentType("application/PTI43");
        setHeader("request-number", "1");
        setHeader("document-type", "Request");
        setHeader("interface-version", "OrbitalRequest v1.2");
        setHeader("mime-version", "1.0");
        setHeader("content-transfer-encoding", "text");
    }

    public void setCustomerData(Customer c)
    {
        BillingInformation billing = c.getBillingInfo();
        addField( "AVSname", billing.getNameOnCard() );
        addField( "AVSaddress1", billing.getAddress1() );
        addField( "AVSaddress2", (billing.getAddress2() != null ? billing.getAddress2() : "") );
        addField( "AVScity", billing.getCity() );
        addField( "AVSstate", billing.getStateAbbreviation() );
        addField( "AVSzip", billing.getPostalCode() );
        addField( "AVScountryCode", (billing.getCountryId() == 1 ? "US" : (billing.getCountryId() == 2 ? "CA" : "")) );

        ContactInformation contact = c.getContactInfo();
        addField( "AVSphoneNum", contact.getPrimaryPhone() );

        addField( "AccountNum", billing.getCcNumber() );
        addField( "Exp", EXP_FORMAT.format(billing.getExpDate()) );
        if (billing.getCVV2Number() != null)
        {
            addField
            (
                "CardSecValInd",
                (billing.getCcType() == CreditCardType.VISA || billing.getCcType() == CreditCardType.DISCOVER ? "1" : "")
            );
            addField( "CardSecVal", billing.getCVV2Number() );
        }
        //Card Brand Not Required for four major. Only others
        /*if (billing.getCcType() == CreditCardType.VISA) addField("CardBrand", "VI");
        else if (billing.getCcType() == CreditCardType.MASTERCARD) addField("CardBrand", "MC");
        else if (billing.getCcType() == CreditCardType.DISCOVER) addField("CardBrand", "DI");
        else if (billing.getCcType() == CreditCardType.AMEX) addField("CardBrand", "AX");*/
    }

    /**
     * Sets the merchant login.
     *
     * @param login The login.
     * @throws IllegalArgumentException if the merchant id is null.
     */
    public void setMerchantId(String id) throws IllegalArgumentException
    {
        if (id == null) throw new IllegalArgumentException("merchantId cannot be null");
        addField("MerchantID", id);
        setHeader( "merchant-id", getField("MerchantID") );
        setHeader( "auth-mid", getField("MerchantID") );
    }

    /**
     * Returns the merchant id.
     *
     * @return String
     */
    public String getMerchantId()
    {
        return getField("MerchantID");
    }

    /**
     * Sets the BIN.
     *
     * @param bin The BIN.
     * @throws IllegalArgumentException if the BIN is null.
     */
    public void setBin(String bin) throws IllegalArgumentException
    {
        if (bin == null) throw new IllegalArgumentException("BIN cannot be null");
        addField("BIN", bin);
    }

    /**
     * Sets the terminal id.
     *
     * @param id The terminal id.
     * @throws IllegalArgumentException if the terminal id is null.
     */
    public void setTerminalId(String id) throws IllegalArgumentException
    {
        if (id == null) throw new IllegalArgumentException("terminalId cannot be null");
        addField("TerminalID", id);
        setHeader( "auth-tid", getField("TerminalID") );
    }

    /**
     * Returns the terminal id.
     *
     * @return String
     */
    public String getTerminalId()
    {
        return getField("TerminalID");
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param amt The amount.
     * @throws IllegalArgumentException if the amount is less than .01.
     */
    public void setAmount(float amt) throws IllegalArgumentException
    {
        if (amt < .01) throw new IllegalArgumentException("Amount cannot be less than .01");
        _amount = amt;
        addField("Amount", PRICE_FORMAT.format(_amount*100) );
    }

    /**
     * Sets the order id.
     *
     * @param id The order id.
     */
    public void setOrderId(long id)
    {
        addField( "OrderID", String.valueOf(id) );
        setHeader( "trace-number", String.valueOf(id) );
    }

    /**
     * Returns the amount to charge.
     *
     * @return float
     */
    public float getAmount()
    {
        return _amount;
    }

    /**
     * Sets the type of transaction to perform.
     *
     * @param type The transaction type.
     */
    public void setType(int type)
    {
        String stype = "AC";
        if (type == PREAUTH) stype = "A";
        else if (type == PROCESS_PRIOR_PREAUTH) stype = "FC";
        else if (type == REFUND) stype = "R";
        _type = type;
        addField("MessageType", stype);
    }

    /**
     * Sets the transaction id. This must be done AFTER setType.
     *
     * @param transId
     */
    public void setTransactionId(String transId)
    {
        if (transId != null) addField( (_type == PREAUTH ? "PriorAuthID" : "TxRefNum"), transId );
    }

    public String format(FormatType type) throws UnsupportedFormatException
    {
        XmlTag ret = new XmlTag("Request");
        XmlTag root = new XmlTag(_rootTag, ret);
        XmlTag field = new XmlTag("IndustryType", root);
        field.setValue("EC");
        field = new XmlTag("MessageType", root);
        field.setValue( _fields.get("MessageType") );
        field = new XmlTag("BIN", root);
        field.setValue( _fields.get("BIN") );
        field = new XmlTag("MerchantID", root);
        field.setValue( _fields.get("MerchantID") );
        field = new XmlTag("TerminalID", root);
        field.setValue( _fields.get("TerminalID") );
        /*field = new XmlTag("CardBrand", root);
        field.setValue( _fields.get("CardBrand") );*/
        field = new XmlTag("AccountNum", root);
        field.setValue( _fields.get("AccountNum") );
        field = new XmlTag("Exp", root);
        field.setValue( _fields.get("Exp") );
        field = new XmlTag("CurrencyCode", root);
        field.setValue("840");
        field = new XmlTag("CurrencyExponent", root);
        field.setValue("2");
        field = new XmlTag("CardSecValInd", root);
        field.setValue( _fields.get("CardSecValInd") );
        field = new XmlTag("CardSecVal", root);
        field.setValue( _fields.get("CardSecVal") );
        field = new XmlTag("AVSzip", root);
        field.setValue( _fields.get("AVSzip") );
        field = new XmlTag("AVSaddress1", root);
        field.setValue( _fields.get("AVSaddress1") );
        field = new XmlTag("AVSaddress2", root);
        field.setValue( _fields.get("AVSaddress2") );
        field = new XmlTag("AVScity", root);
        field.setValue( _fields.get("AVScity") );
        field = new XmlTag("AVSstate", root);
        field.setValue( _fields.get("AVSstate") );
        field = new XmlTag("AVSphoneNum", root);
        field.setValue( _fields.get("AVSphoneNum") );
        field = new XmlTag("AVSname", root);
        field.setValue( _fields.get("AVSname") );
        field = new XmlTag("AVScountryCode", root);
        field.setValue( _fields.get("AVScountryCode") );
        field = new XmlTag("CustomerProfileFromOrderInd", root);
        field.setValue("EMPTY");
        field = new XmlTag("OrderID", root);
        field.setValue( _fields.get("OrderID") );
        field = new XmlTag("Amount", root);
        field.setValue( _fields.get("Amount") );
        return ret.format(type);
    }

    public void addField(String name, String val) throws IllegalArgumentException
    {
        if (val != null) _fields.put(name, val);
        else _fields.remove(name);
        /*XmlTag tag = _xml.getFirstOccurrenceOf(_rootTag);
        XmlTag tag2 = tag.getFirstOccurrenceOf(name);
        if (tag2 == null) tag2 = new XmlTag(name, tag);
        tag2.setValue(val);*/
    }

    public String getField(String name)
    {
        return _fields.get(name);
        /*XmlTag tag = _xml.getFirstOccurrenceOf(_rootTag).getFirstOccurrenceOf(name);
        if (tag != null) return tag.getValue();
        else return null;*/
    }
}
