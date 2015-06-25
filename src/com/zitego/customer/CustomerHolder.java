package com.zitego.customer;

/**
 * This is an interface to be implemented by any class that is connected to a customer
 * object somehow. The implementation is class dependant as far as caching the object
 * or loading it fresh each time. The only required method to implement is getCustomer.
 *
 * @author John Glorioso
 * @version $Id: CustomerHolder.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public interface CustomerHolder
{
    /**
     * Returns a Customer object.
     *
     * @return Customer
     * @throws CustomerException if an error occurs retrieving the customer.
     */
    public Customer getCustomer() throws CustomerException;
}