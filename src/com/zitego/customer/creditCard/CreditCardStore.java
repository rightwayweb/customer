package com.zitego.customer.creditCard;

/**
 * This is an interface that handles storing credit card information. The
 * methods that must be implemented are addCc, editCc, and deleteCc.
 *
 * @author John Glorioso
 * @version $Id: CreditCardStore.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public interface CreditCardStore
{
    /**
     * This method should add the credit card number to the store given a pass key.
     *
     * @param cc The credit card number.
     * @param key The pass key.
     * @throws CreditCardAPIException if an error occurs.
     */
    public void addCc(String cc, String key) throws CreditCardAPIException;
    
    /**
     * This method should edit the credit card number in the store given a pass key.
     *
     * @param cc The credit card number.
     * @param key The pass key.
     * @throws CreditCardAPIException if an error occurs.
     */
    public void editCc(String cc, String key) throws CreditCardAPIException;
    
    /**
     * This method should delete the credit card number to the store given a pass key.
     *
     * @param cc The credit card number.
     * @param key The pass key.
     * @throws CreditCardAPIException if an error occurs.
     */
    public void deleteCc(String cc, String key) throws CreditCardAPIException;
    
    /**
     * This method should return the credit card number from the store given a pass key.
     *
     * @param key The pass key.
     * @return String
     * @throws CreditCardAPIException if an error occurs.
     */
    public String getCc(String key) throws CreditCardAPIException;
}