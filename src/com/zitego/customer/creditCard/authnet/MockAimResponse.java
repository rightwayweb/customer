package com.zitego.customer.creditCard.authnet;

import com.zitego.web.thirdPartyAPI.APIException;

/**
 * A mock response from authorize.net. Nothing is ever sent so the response that is returned is
 * an assumed success.
 *
 * @author John Glorioso
 * @version $Id: MockAimResponse.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class MockAimResponse extends AimResponse
{
    /**
     * Creates a new AimResponse from a request. Status will be "Approved" and
     * authcode will be "000000".
     *
     * @param request The request.
     */
    public MockAimResponse(AimRequest request) throws APIException
    {
        super(request, "1||||000000");
    }
}