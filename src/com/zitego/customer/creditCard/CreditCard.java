package com.zitego.customer.creditCard;

/**
 * This class encapsulates the data about a credit card including the
 * name on the card, the credit card number, the credit card type,
 * and the expiration date. Additionally, it can include the cvv2
 * number.
 *
 * @author John Glorioso
 * @version $Id: CreditCard.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CreditCard
{
    private String _ccNumber;
    private String _expDate;
    private String _nameOnCard;
    private CreditCardType _type;
    private String _cvv2Number;
    
    /**
     * Creates a new credit card with type, number, expiration date, and the
     * name on the card.
     *
     * @param type The credit card type.
     * @param num The credit card number.
     * @param exp The expiration date in string format. Ex: 01/07
     * @param name The name on the card.
     */
    public CreditCard(CreditCardType type, String num, String exp, String name)
    {
        setType(type);
        setCcNumber(num);
        setExpDate(exp);
        setNameOnCard(name);
    }
    
    /**
     * Creates a new credit card with type, number, expiration date, the
     * name on the card, and the cvv2 code.
     *
     * @param type The credit card type.
     * @param num The credit card number.
     * @param exp The expiration date in string format. Ex: 01/07
     * @param name The name on the card.
     * @param cvv2 The cvv2 code.
     */
    public CreditCard(CreditCardType type, String num, String exp, String name, String cvv2)
    {
        setType(type);
        setCcNumber(num);
        setExpDate(exp);
        setNameOnCard(name);
        setCVV2Number(cvv2);
    }
    
    /**
     * Sets the credit card type.
     *
     * @param type The type.
     */
    public void setType(CreditCardType type)
    {
        _type = type;
    }
    
    /**
     * Returns the credit card type.
     *
     * @return CreditCardType
     */
    public CreditCardType getType()
    {
        return _type;
    }
    
    /**
     * Sets the credit card number.
     *
     * @param num The number.
     */
    public void setCcNumber(String num)
    {
        _ccNumber = num;
    }
    
    /**
     * Returns the credit card number.
     *
     * @return String
     */
    public String getCcNumber()
    {
        return _ccNumber;
    }
    
    /**
     * Sets the credit card expiration date in string format. Ex: 01/07
     *
     * @param exp The date.
     */
    public void setExpDate(String exp)
    {
        _expDate = exp;
    }
    
    /**
     * Returns the credit card expiration date.
     *
     * @return String
     */
    public String getExpDate()
    {
        return _expDate;
    }
    
    /**
     * Sets the name on the card.
     *
     * @param name The name on the card.
     */
    public void setNameOnCard(String name)
    {
        _nameOnCard = name;
    }
    
    /**
     * Returns the name on the card.
     *
     * @return String
     */
    public String getNameOnCard()
    {
        return _nameOnCard;
    }
    
    /**
     * Sets the cvv2 number.
     *
     * @param num The number.
     */
    public void setCVV2Number(String num)
    {
        _cvv2Number = num;
    }
    
    /**
     * Returns the cvv2 number.
     *
     * @return String
     */
    public String getCVV2Number()
    {
        return _cvv2Number;
    }
}