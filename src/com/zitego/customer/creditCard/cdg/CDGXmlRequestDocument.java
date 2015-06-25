package com.zitego.customer.creditCard.cdg;

import com.zitego.customer.Customer;
import com.zitego.customer.creditCard.CreditCardType;
import com.zitego.customer.creditCard.UserId;
import com.zitego.customer.ContactInformation;
import com.zitego.customer.BillingInformation;
import com.zitego.markup.xml.XmlTag;
import com.zitego.format.FormatType;
import com.zitego.format.UnsupportedFormatException;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * <p>This is the xml document that is sent to cdg to process a sale. It contains either
 * a registered customer data section (including id and password), or a customer data
 * section which includes all billing, shipping, and credit card information contained
 * within a customer object.</p>
 * <p>There is also an order detail section which allows you to add order items. Finally,
 * there is an email text section which allows you to specify specific lines of text to
 * include in the email that is sent to the customer upon success.</p>
 *
 * @author John Glorioso
 * @version $Id: CDGXmlRequestDocument.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CDGXmlRequestDocument extends XmlTag
{
    /** The registered data tag. */
    protected RegisteredCustomerDataTag _registeredCustomerData;
    /** The CustomerData tag. */
    protected CustomerDataTag _customerData;
    /** The transaction data tag. */
    protected TransactionDataTag _transactionData;

    public static void main(String[] args) throws Exception
    {
        CDGXmlRequestDocument doc = new CDGXmlRequestDocument();
        doc.parse(args[0], FormatType.XML);
        System.out.println("Parsed:");
        System.out.println( doc.format(FormatType.XML) );
    }

    /**
     * Creates a new document with a root tag name.
     *
     * @param tag The tag name.
     */
    protected CDGXmlRequestDocument(String tag)
    {
        super(tag);
        _transactionData = new TransactionDataTag(this);
    }

    /**
     * Creates a new CDGRequest document.
     */
    CDGXmlRequestDocument()
    {
        this("SaleRequest");
    }

    /**
     * Sets the registered customer data. This or CustomerData can be
     * set, but not both.
     *
     * @param code The customer code.
     * @param password The password.
     * @throws IllegalStateException if the CustomerData is already set.
     * @throws IllegalArgumentException if code or password are null.
     */
    public void setRegisteredCustomerData(String code, String password) throws IllegalStateException
    {
        if (_customerData != null) throw new IllegalStateException("Cannot set both customer data and registered customer data");
        if (_registeredCustomerData == null)
        {
            _registeredCustomerData = new RegisteredCustomerDataTag(this);
        }
        _registeredCustomerData.setCustomerCode(code);
        _registeredCustomerData.setPassword(password);
    }

    /**
     * Returns the registered customer data as a String.
     *
     * @return UserId
     */
    protected UserId getRegisteredCustomerData()
    {
        if (_registeredCustomerData != null) return new UserId(_registeredCustomerData.getCustomerCode(), null);
        else return null;
    }

    /**
     * Sets the customer data. This or RegisteredCustomerData can be
     * set, but not both. The Customer object must contain the following:
     * ContactInformation - email, (address1, city, state, postal code, country,
     * and primary phone are optional, but required if any one is set. This is
     * the shipping address if different then billing). BillingInformation -
     * address1, city, state, postal code, country, and primary phone. Credit
     * card number and expiration date.
     *
     * @param data The data.
     * @throws IllegalArgumentException if the customer is null or any required data
     *                                  is missing.
     * @throws IllegalStateException if the RegisteredCustomerData is already set.
     */
    public void setCustomerData(Customer data) throws IllegalArgumentException, IllegalStateException
    {
        if (_registeredCustomerData != null) throw new IllegalStateException("Cannot set both customer data and registered customer data");
        if (_customerData == null) _customerData = new CustomerDataTag(this);
        _customerData.setCustomerInfo(data);
    }

    /**
     * Returns the customer data email address.
     *
     * @return String
     */
    protected String getEmail()
    {
        if (_customerData != null) return _customerData.getEmail();
        else return null;
    }

    /**
     * Returns the customer data shipping information.
     *
     * @return ContactInformation
     */
    protected ContactInformation getShippingInfo()
    {
        if (_customerData != null) return _customerData.getShippingInfo();
        else return null;
    }

    /**
     * Returns the customer data billing information.
     *
     * @return BillingInformation
     */
    protected BillingInformation getBillingInfo()
    {
        if (_customerData != null) return _customerData.getBillingInfo();
        else return null;
    }

    /**
     * Returns the total cost of the order.
     *
     * @return float
     */
    protected float getTotalCost()
    {
        if (_transactionData != null) return _transactionData.getTotalCost();
        else return -1;
    }

    /**
     * Sets the CDG vendor id (gateway id).
     *
     * @param vendor The id.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorId(String vendor) throws IllegalArgumentException
    {
        _transactionData.setVendorId(vendor);
    }

    /**
     * Sets the CDG vendor password (gateway password).
     *
     * @param pass The password.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setVendorPassword(String pass) throws IllegalArgumentException
    {
        _transactionData.setVendorPassword(pass);
    }

    /**
     * Sets the home page of the gateway configuration.
     *
     * @param homePage The home page.
     * @throws IllegalArgumentException if the data is null.
     */
    public void setHomePage(String homePage) throws IllegalArgumentException
    {
        _transactionData.setHomePage(homePage);
    }

    /**
     * Adds a line of text to include in the customer receipt email.
     *
     * @param text The email text to add.
     * @throws IllegalArgumentException if the data is null.
     */
    public void addEmailText(String text) throws IllegalArgumentException
    {
        _transactionData.addEmailText(text);
    }

    /**
     * Adds an item to the order to process.
     *
     * @param desc The item description.
     * @param cost The price.
     * @param quantity The quantity.
     * @throws IllegalArgumentException if any data is null.
     */
    public void addOrderItem(String desc, float cost, int quantity) throws IllegalArgumentException
    {
        _transactionData.addOrderItem(desc, cost, quantity);
    }

    public void addChild(Element child)
    {
        XmlTag toParse = null;
        String tag = child.getTagName();
        if ( tag.equalsIgnoreCase("RegisteredCustomerData") ) toParse = _registeredCustomerData = new RegisteredCustomerDataTag(this);
        else if ( tag.equalsIgnoreCase("CustomerData") ) toParse = _customerData = new CustomerDataTag(this);
        //Trans data is already created...
        else if ( tag.equalsIgnoreCase("TransactionData") ) toParse = _transactionData;

        if (toParse != null)
        {
            try
            {
                toParse.parse(child, FormatType.XML);
            }
            catch (UnsupportedFormatException ufe)
            {
                throw new RuntimeException("Could not parse: "+child, ufe);
            }
        }
        else
        {
            super.addChild(child);
        }
    }

    protected class RegisteredCustomerDataTag extends XmlTag
    {
        private XmlTag _customerCode;
        private XmlTag _password;

        protected RegisteredCustomerDataTag(XmlTag parent)
        {
            super("RegisteredCustomerData" , parent);
            parent.moveBodyContentTo(0, this);
            _customerCode = new XmlTag("CustCode", this);
            _password = new XmlTag("Pwd", this);
        }

        protected void setCustomerCode(String code)
        {
            if (code == null) throw new IllegalArgumentException("code cannot be null");
            _customerCode.setValue(code);
        }

        protected String getCustomerCode()
        {
            return _customerCode.getValue();
        }

        protected void setPassword(String pass)
        {
            if (pass == null) throw new IllegalArgumentException("password cannot be null");
            _password.setValue(pass);
        }

        protected String getPassword()
        {
            return _password.getValue();
        }

        public void buildFromXml(Element root)
        {
            //Null out the tags and let them be added...
            _customerCode = null;
            _password = null;
            clearContent();
            super.buildFromXml(root);
        }

        public void addChild(Element child)
        {
            String tag = child.getTagName();
            XmlTag toParse = null;
            if ( tag.equalsIgnoreCase("CustCode") ) toParse = _customerCode = new XmlTag("CustCode", this);
            else if ( tag.equalsIgnoreCase("Pwd") ) toParse = _password = new XmlTag("Pwd", this);

            if (toParse != null)
            {
                try
                {
                    toParse.parse(child, FormatType.XML);
                }
                catch (UnsupportedFormatException ufe)
                {
                    throw new RuntimeException("Could not parse: "+child, ufe);
                }
            }
            else
            {
                super.addChild(child);
            }
        }
    }

    protected class CustomerDataTag extends XmlTag
    {
        private XmlTag _email;
        private XmlTag _billingAddress;
        private XmlTag _shippingAddress;
        private XmlTag _accountInfo;

        protected CustomerDataTag(XmlTag parent)
        {
            super("CustomerData", parent);
            parent.moveBodyContentTo(0, this);

            _email = new XmlTag("Email", this);
            _billingAddress = new XmlTag("BillingAddress", this);
            _accountInfo = new XmlTag("AccountInfo", this);
        }

        protected void setCustomerInfo(Customer data)
        {
            if (data == null) throw new IllegalArgumentException("customer data cannot be null");
            ContactInformation contact = data.getContactInfo();
            String val = contact.getEmail();
            if (val == null) throw new IllegalArgumentException("ContactInformation email is required");
            _email.setValue(val);

            BillingInformation billing = data.getBillingInfo();
            //Billing info
            val = billing.getFirstName();
            if (val == null) throw new IllegalArgumentException("BillingInformation first name is required");
            _billingAddress.setChildValue("FirstName", val);
            val = billing.getLastName();
            if (val == null) val = "";
            _billingAddress.setChildValue("LastName", val);
            val = billing.getAddress1();
            if (val == null) throw new IllegalArgumentException("BillingInformation address 1 is required");
            _billingAddress.setChildValue("Address1", val);
            val = billing.getCity();
            if (val == null) throw new IllegalArgumentException("BillingInformation city is required");
            _billingAddress.setChildValue("City", val);
            val = billing.getStateName();
            if (val == null) throw new IllegalArgumentException("BillingInformation state name is required");
            _billingAddress.setChildValue("State", val);
            val = billing.getPostalCode();
            if (val == null) throw new IllegalArgumentException("BillingInformation postal code is required");
            _billingAddress.setChildValue("Zip", val);
            val = billing.getCountryName();
            if (val == null) throw new IllegalArgumentException("BillingInformation country name is required");
            _billingAddress.setChildValue("Country", val);
            val = billing.getPrimaryPhone();
            if (val == null) val = contact.getPrimaryPhone();
            if (val == null) throw new IllegalArgumentException("BillingInformation primary phone is required");
            _billingAddress.setChildValue("Phone", val);

            //Only set shipping address if it differs
            if ( contact.getAddress1() != null && !contact.getAddress1().equalsIgnoreCase(billing.getAddress1()) )
            {
                if (_shippingAddress == null)
                {
                    _shippingAddress = new XmlTag("ShippingAddress", this);
                    moveBodyContentToAfter(_billingAddress, _shippingAddress);
                }
                val = contact.getAddress1();
                if (val == null) throw new IllegalArgumentException("ContactInformation address 1 is required");
                _shippingAddress.setChildValue("Address1", val);
                val = contact.getFirstName();
                if (val == null) throw new IllegalArgumentException("ContactInformation first name is required");
                _shippingAddress.setChildValue("FirstName", val);
                val = contact.getLastName();
                if (val == null) throw new IllegalArgumentException("ContactInformation last name is required");
                _shippingAddress.setChildValue("LastName", val);
                val = contact.getCity();
                if (val == null) throw new IllegalArgumentException("ContactInformation city is required");
                _shippingAddress.setChildValue("City", val);
                val = contact.getStateName();
                if (val == null) throw new IllegalArgumentException("ContactInformation state name is required");
                _shippingAddress.setChildValue("State", val);
                val = contact.getPostalCode();
                if (val == null) throw new IllegalArgumentException("ContactInformation postal code is required");
                _shippingAddress.setChildValue("Zip", val);
                val = contact.getCountryName();
                if (val == null) throw new IllegalArgumentException("ContactInformation country name is required");
                _shippingAddress.setChildValue("Country", val);
                val = contact.getPrimaryPhone();
                if (val == null) throw new IllegalArgumentException("ContactInformation primary phone is required");
                _shippingAddress.setChildValue("Phone", val);
            }

            //Account info
            XmlTag cardInfo = (XmlTag)_accountInfo.getFirstOccurrenceOf("CardInfo");
            if (cardInfo == null) cardInfo = (XmlTag)_accountInfo.addBodyContent( new XmlTag("CardInfo", _accountInfo) );
            val = billing.getCcNumber();
            if (val == null) throw new IllegalArgumentException("BillingInformation credit card number is required");
            cardInfo.setChildValue("CCNum", val);
            Date dt = billing.getExpDate();
            if (dt == null) throw new IllegalArgumentException("BillingInformation expiration date is required");
            cardInfo.setChildValue( "CCMo", CDGRequest.MONTH_FORMAT.format(dt) );
            cardInfo.setChildValue( "CCYr", CDGRequest.YEAR_FORMAT.format(dt) );
        }

        /**
         * Returns the email address.
         *
         * @return String
         */
        protected String getEmail()
        {
            return _email.getValue();
        }

        /**
         * Returns the shipping information as a ContactInformation object. If there
         * is no shipping information, null is returned.
         *
         * @return ContactInformation
         */
        protected ContactInformation getShippingInfo()
        {
            ContactInformation data = null;
            if (_shippingAddress != null)
            {
                data = new ContactInformation();
                data.setAddress1( _shippingAddress.getChildValue("Address1") );
                data.setFirstName( _shippingAddress.getChildValue("FirstName") );
                data.setLastName( _shippingAddress.getChildValue("LastName") );
                data.setCity( _shippingAddress.getChildValue("City") );
                data.setStateName( _shippingAddress.getChildValue("State") );
                data.setPostalCode( _shippingAddress.getChildValue("Zip") );
                data.setCountryName( _shippingAddress.getChildValue("Country") );
                data.setPrimaryPhone( _shippingAddress.getChildValue("Phone") );
            }
            return data;
        }

        /**
         * Returns the billing information as a BillingInformation object.
         *
         * @return BillingInformation
         */
        protected BillingInformation getBillingInfo()
        {
            BillingInformation data = new BillingInformation();
            data.setAddress1( _billingAddress.getChildValue("Address1") );
            data.setFirstName( _billingAddress.getChildValue("FirstName") );
            data.setLastName( _billingAddress.getChildValue("LastName") );
            data.setCity( _billingAddress.getChildValue("City") );
            data.setStateName( _billingAddress.getChildValue("State") );
            data.setPostalCode( _billingAddress.getChildValue("Zip") );
            data.setCountryName( _billingAddress.getChildValue("Country") );
            data.setPrimaryPhone( _billingAddress.getChildValue("Phone") );

            XmlTag cardInfo = _accountInfo.getFirstOccurrenceOf("CardInfo");
            data.setCcNumber( cardInfo.getChildValue("CCNum") );
            if (data.getCcNumber() == null) data.setCcNumber( cardInfo.getChildValue("CCLastFour") );
            data.setCcType( CreditCardType.evaluate(cardInfo.getChildValue("CCName")) );
            try
            {
                data.setExpDate( cardInfo.getChildValue("CCMo")+"/"+cardInfo.getChildValue("CCYr") );
            }
            catch (Exception e) { }

            return data;
        }

        public void buildFromXml(Element root)
        {
            //Null out the tags and let them be added...
            _email = null;
            _billingAddress = null;
            _shippingAddress = null;
            _accountInfo = null;
            clearContent();
            super.buildFromXml(root);
        }

        public void addChild(Element child)
        {
            String tag = child.getTagName();
            XmlTag toParse = null;
            if ( tag.equalsIgnoreCase("Email") ) toParse = _email = new XmlTag("Email", this);
            else if ( tag.equalsIgnoreCase("BillingAddress") ) toParse = _billingAddress = new XmlTag("BillingAddress", this);
            else if ( tag.equalsIgnoreCase("ShippingAddress") ) toParse = _shippingAddress = new XmlTag("ShippingAddress", this);
            else if ( tag.equalsIgnoreCase("AccountInfo") ) toParse = _accountInfo = new XmlTag("AccountInfo", this);

            if (toParse != null)
            {
                try
                {
                    toParse.parse(child, FormatType.XML);
                }
                catch (UnsupportedFormatException ufe)
                {
                    throw new RuntimeException("Could not parse: "+child, ufe);
                }
            }
            else
            {
                super.addChild(child);
            }
        }
    }

    protected class TransactionDataTag extends XmlTag
    {
        private XmlTag _vendorId;
        private XmlTag _vendorPassword;
        private XmlTag _homePage;
        private XmlTag _emailText;
        private XmlTag _orderItems;
        private float _totalCost = 0;

        protected TransactionDataTag(XmlTag parent)
        {
            super("TransactionData", parent);

            _vendorId = new XmlTag("VendorId", this);
            _vendorPassword = new XmlTag("VendorPassword", this);
            _homePage = new XmlTag("HomePage", this);
            _orderItems = new XmlTag("OrderItems", this);
        }

        protected void setVendorId(String vendor)
        {
            if (vendor == null) throw new IllegalArgumentException("vendor id cannot be null");
            _vendorId.setValue(vendor);
        }

        protected void setVendorPassword(String pass)
        {
            if (pass == null) throw new IllegalArgumentException("vendor password cannot be null");
            _vendorPassword.setValue(pass);
        }

        protected void setHomePage(String homePage)
        {
            if (homePage == null) throw new IllegalArgumentException("home page cannot be null");
            _homePage.setValue(homePage);
        }

        protected void addEmailText(String text)
        {
            if (text == null) throw new IllegalArgumentException("email text cannot be null");
            if (_emailText == null)
            {
                _emailText = new XmlTag("EmailText", this);
                moveBodyContentToAfter(_homePage, _emailText);
            }
            XmlTag item = new XmlTag("EmailTextItem", _emailText);
            item.setValue(text);
        }

        protected void addOrderItem(String desc, float cost, int quantity)
        {
            if (desc == null) throw new IllegalArgumentException("order description cannot be null");
            if (cost < 0) throw new IllegalArgumentException("cost cannot be negative");
            if (quantity < 1) throw new IllegalArgumentException("quantity cannot be less then 1");
            XmlTag item = new XmlTag("Item", _orderItems);
            item.setChildValue("Description", desc);
            item.setChildValue( "Cost", CDGRequest.CURRENCY_FORMAT.format(new Float(cost)) );
            item.setChildValue( "Qty", String.valueOf(quantity) );
            _totalCost += cost;
        }

        protected float getTotalCost()
        {
            return _totalCost;
        }

        public void buildFromXml(Element root)
        {
            //Null out the tags and let them be added...
            _vendorId = null;
            _vendorPassword = null;
            _homePage = null;
            _emailText = null;
            _orderItems = null;
            clearContent();
            super.buildFromXml(root);
        }

        public void addChild(Element child)
        {
            String tag = child.getTagName();
            XmlTag toParse = null;
            if ( tag.equalsIgnoreCase("VendorId") ) toParse = _vendorId = new XmlTag("VendorId", this);
            else if ( tag.equalsIgnoreCase("VendorPassword") ) toParse = _vendorPassword = new XmlTag("VendorPassword", this);
            else if ( tag.equalsIgnoreCase("HomePage") ) toParse = _homePage = new XmlTag("HomePage", this);
            else if ( tag.equalsIgnoreCase("EmailText") ) toParse = _emailText = new XmlTag("EmailText", this);
            else if ( tag.equalsIgnoreCase("OrderItems") ) toParse = _orderItems = new XmlTag("OrderItems", this);

            if (toParse != null)
            {
                try
                {
                    toParse.parse(child, FormatType.XML);
                }
                catch (UnsupportedFormatException ufe)
                {
                    throw new RuntimeException("Could not parse: "+child, ufe);
                }
            }
            else
            {
                super.addChild(child);
            }
        }
    }
}