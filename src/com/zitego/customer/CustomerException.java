package com.zitego.customer;

/**
 * This is a generic exception that is to be thrown for customer related errors.
 *
 * @author John Glorioso
 * @version $Id: CustomerException.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CustomerException extends Exception
{
    /**
     * Constructs a new exception with the specified error message.
     *
     * @param err The error message.
     */
    public CustomerException(String err)
    {
        super(err);
    }

    /**
     * Constructs a new exception with the specified error message and a cause.
     *
     * @param err The error message.
     * @param cause The cause.
     */
    public CustomerException(String err, Throwable cause)
    {
        super(err, cause);
    }
}