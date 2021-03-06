package com.zitego.customer.creditCard.cdg;

/**
 * A mock response from cdg vault. Nothing is ever sent so the response that is returned is
 * an assumed success.
 *
 * @author John Glorioso
 * @version $Id: MockCDGVaultResponse.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class MockCDGVaultResponse extends CDGVaultResponse
{
    /**
     * Creates a new MockCDGResponse from a request. Status will be "Approved" and
     * authcode will be "000000".
     *
     * @param request The request.
     */
    public MockCDGVaultResponse(CDGVaultRequest request)
    {
        super(request);
        setStatus("Approved");
        setAuthCode("000000");
    }
    
    public float getTotalCharged()
    {
        return _request.getTotal();
    }
}