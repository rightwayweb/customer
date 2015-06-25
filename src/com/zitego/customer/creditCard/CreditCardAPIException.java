package com.zitego.customer.creditCard;

import com.zitego.web.thirdPartyAPI.APIException;

/**
 * An exception to be thrown when an error occurs querying a domain api.
 *
 * @author John Glorioso
 * @version $Id: CreditCardAPIException.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
*/
public class CreditCardAPIException extends APIException
{
    /**
     * Creates a new exception with a message.
     *
     * @param msg The message.
     */
    public CreditCardAPIException(String msg)
    {
        this(msg, (String)null);
    }

    /**
     * Constructs a new exception with the specified error message and a cause.
     *
     * @param err The error message.
     * @param cause The cause.
     */
    public CreditCardAPIException(String err, Throwable cause)
    {
        super(err, cause);
    }

    /**
     * Creates a new exception with a message and a detailed message.
     *
     * @param msg The message.
     * @param detailedMessage The detailed message.
     */
    public CreditCardAPIException(String msg, String detailedMessage)
    {
        this(msg, detailedMessage, null, null);
    }

    /**
     * Creates a new exception with a message, detailed message, request, and response.
     *
     * @param msg The message.
     * @param detailedMessage The detailed message.
     * @param request The request.
     * @param response The response.
     */
    public CreditCardAPIException(String msg, String detailedMessage, String request, String response)
    {
        super(msg, detailedMessage, request, response);
    }

    /**
     * Creates a new exception with a message and root cause.
     *
     * @param msg The message.
     * @param detailedMessage The detailed message.
     * @param request The request.
     * @param response The response.
     * @param cause The root cause.
     */
    public CreditCardAPIException(String msg, String detailedMessage, String request, String response, Throwable cause)
    {
        super(msg, detailedMessage, request, response, cause);
    }
}