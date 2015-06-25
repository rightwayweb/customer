package com.zitego.customer;

import com.zitego.customer.creditCard.CreditCardType;
import com.zitego.customer.creditCard.CreditCard;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
*  This class is an extension of contact information and includes credit card
 *
 * @author John Glorioso
 * @version $Id: BillingInformation.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class BillingInformation extends ContactInformation
{
    private String _ccNumber;
    private String _partialCcNumber;
    private CreditCardType _ccType;
    private String _idToken;
    private String _tokenPassword;
    private Date _expDate;
    private SimpleDateFormat _expFormat;
    private String _nameOnCard;
    private String _cvv2Number;
    private static final SimpleDateFormat EXP_FORMAT = new SimpleDateFormat("MM/yy");
    private static final SimpleDateFormat ALT_EXP_FORMAT = new SimpleDateFormat("MM/yyyy");

    /**
     * Creates a new BillingInformation.
     */
    public BillingInformation() {}

    /**
     * Creates a new BillingInformation from ContactInformation.
     *
     * @param info The ContactInformation.
     */
    public BillingInformation(ContactInformation info)
    {
        if (info != null)
        {
            setPrefix( info.getPrefix() );
            setFirstName( info.getFirstName() );
            setLastName( info.getLastName() );
            setSuffix( info.getSuffix() );
            setDisplayName( info.getDisplayName() );
            setCompanyName( info.getCompanyName() );
            setAddress1( info.getAddress1() );
            setAddress2( info.getAddress2() );
            setCity( info.getCity() );
            setStateId( info.getStateId() );
            setStateName( info.getStateName() );
            setPostalCode( info.getPostalCode() );
            setCountryId( info.getCountryId() );
            setCountryName( info.getCountryName() );
            setPrimaryPhone( info.getPrimaryPhone() );
            setSecondaryPhone( info.getSecondaryPhone() );
            setFax( info.getFax() );
            setMobile( info.getMobile() );
            setSecondaryMobile( info.getSecondaryMobile() );
            setEmail( info.getEmail() );
        }
    }
    
    /**
     * Sets the credit card information from the given CreditCard.
     *
     * @param card The information.
     * @throws ParseException if the expiration date is not formatted properly.
     */
    public void setCcInfo(CreditCard card) throws ParseException
    {
        if (card != null)
        {
            setCcNumber( card.getCcNumber() );
            setCcType( card.getType() );
            setExpDate( card.getExpDate() );
            setNameOnCard( card.getNameOnCard() );
            setCVV2Number( card.getCVV2Number() );
        }
        else
        {
            setCcNumber(null);
            setCcType(null);
            setExpDate( (Date)null );
            setNameOnCard(null);
            setCVV2Number(null);
        }
    }
    
    /**
     * Returns the credit card information.
     *
     * @return CreditCard
     */
    public CreditCard getCcInfo()
    {
        return new CreditCard(_ccType, _ccNumber, getFormattedExpDate(), _nameOnCard, _cvv2Number);
    }

    /**
     * Sets the ccNumber.
     *
     * @param ccNumber The ccNumber.
     */
    public void setCcNumber(String ccNumber)
    {
        _ccNumber = ccNumber;
    }

    /**
     * Returns the ccNumber.
     *
     * @return String
     */
    public String getCcNumber()
    {
        return _ccNumber;
    }

    /**
     * Returns the last 4 digits of the credit card number.
     */
    public String getCcNumberLastFourDigits()
    {
        if (_ccNumber == null) return null;
        else if (_ccNumber.length() < 4) return _ccNumber;
        else return _ccNumber.substring(_ccNumber.length()-4);
    }

    /**
     * Sets the partialCcNumber.
     *
     * @param partialCcNumber The partialCcNumber.
     */
    public void setPartialCcNumber(String partialCcNumber)
    {
        _partialCcNumber = partialCcNumber;
    }

    /**
     * Returns the partialCcNumber.
     *
     * @return String
     */
    public String getPartialCcNumber()
    {
        return _partialCcNumber;
    }

    /**
     * Sets the idToken.
     *
     * @param idToken The idToken.
     */
    public void setIdToken (String idToken)
    {
        _idToken = idToken;
    }

    /**
     * Returns the idToken.
     *
     * @return String
     */
    public String getIdToken()
    {
        return _idToken;
    }

    /**
     * Sets the tokenPassword.
     *
     * @param tokenPassword The tokenPassword.
     */
    public void setTokenPassword(String tokenPassword)
    {
        _tokenPassword = tokenPassword;
    }

    /**
     * Returns the tokenPassword.
     *
     * @return String
     */
    public String getTokenPassword()
    {
        return _tokenPassword;
    }

    /**
     * Sets the ccType.
     *
     * @param ccType The ccType.
     */
    public void setCcType(CreditCardType ccType)
    {
        _ccType = ccType;
    }

    /**
     * Returns the ccType.
     *
     * @return CreditCardType
     */
    public CreditCardType getCcType()
    {
        return _ccType;
    }

    /**
     * Sets the expDate given the string. If the string does not format properly a parse
     * exception is thrown.
     *
     * @param expDate The expDate.
     * @throws ParseException if the date format is not valid.
     */
    public void setExpDate(String expDate) throws ParseException
    {
        if (expDate == null) throw new ParseException("Expiration date cannot be null", -1);
        try
        {
            _expDate = EXP_FORMAT.parse(expDate);
            _expFormat = EXP_FORMAT;
        }
        catch (ParseException pe)
        {
            _expDate = ALT_EXP_FORMAT.parse(expDate);
            _expFormat = ALT_EXP_FORMAT;
        }
    }

    /**
     * Sets the expDate.
     *
     * @param expDate The expDate.
     */
    public void setExpDate(Date expDate)
    {
        _expDate = expDate;
    }

    /**
     * Returns the expDate.
     *
     * @return Date
     */
    public Date getExpDate()
    {
        return _expDate;
    }

    /**
     * Returns the expDate formatted as it was when it was set. If it was not set using a string,
     * then it is formatted with EXP_FORMAT.
     *
     * @return String
     */
    public String getFormattedExpDate()
    {
        if (_expDate == null) return null;
        if (_expFormat != null) return _expFormat.format(_expDate);
        else return EXP_FORMAT.format(_expDate);
    }

    /**
     * Sets the nameOnCard.
     *
     * @param nameOnCard The nameOnCard.
     */
    public void setNameOnCard(String nameOnCard)
    {
        _nameOnCard = nameOnCard;
    }

    /**
     * Returns the nameOnCard.
     *
     * @return String
     */
    public String getNameOnCard()
    {
        return _nameOnCard;
    }
    
    /**
     * Sets the cvv2Number.
     *
     * @param cvv2Number The cvv2Number.
     */
    public void setCVV2Number(String cvv2Number)
    {
        _cvv2Number = cvv2Number;
    }

    /**
     * Returns the cvv2Number.
     *
     * @return String
     */
    public String getCVV2Number()
    {
        return _cvv2Number;
    }

    /**
     * Sets the first and last name from the name on the credit card.
     */
    public void setFirstAndLastFromNameOnCard()
    {
        String val = _nameOnCard;
        if (val == null) return;

        //Split it in two (lame, but fix for now). TO DO - make this more robust
        int index = val.indexOf(" ");
        if (index > -1)
        {
            setFirstName( val.substring(0, index) );
            setLastName( val.substring(index+1) );
        }
        else
        {
            setFirstName(val);
        }
    }
}