package com.zitego.customer.creditCard.paypal;

import com.zitego.web.thirdPartyAPI.APIException;

/**
 * An exception to be thrown when an error occurs querying a domain api.
 *
 * @author John Glorioso
 * @version $Id: PayPalAPIException.java,v 1.1 2009/12/12 18:18:46 jglorioso Exp $
*/
public class PayPalAPIException extends APIException
{
    /**
     * Creates a new exception with a message.
     *
     * @param msg The message.
     */
    public PayPalAPIException(String msg)
    {
        this(msg, (String)null);
    }

    /**
     * Constructs a new exception with the specified error message and a cause.
     *
     * @param err The error message.
     * @param cause The cause.
     */
    public PayPalAPIException(String err, Throwable cause)
    {
        super(err, cause);
    }

    /**
     * Creates a new exception with a message and a detailed message.
     *
     * @param msg The message.
     * @param detailedMessage The detailed message.
     */
    public PayPalAPIException(String msg, String detailedMessage)
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
    public PayPalAPIException(String msg, String detailedMessage, String request, String response)
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
    public PayPalAPIException(String msg, String detailedMessage, String request, String response, Throwable cause)
    {
        super(msg, detailedMessage, request, response, cause);
    }
}
