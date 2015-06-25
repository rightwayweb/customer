package com.zitego.customer.creditCard.orbital;

import com.zitego.web.thirdPartyAPI.APIException;

/**
 * A mock response from the orbital payment gateway. Nothing is ever sent so the response that is returned is
 * an assumed success.
 *
 * @author John Glorioso
 * @version $Id: MockOrbitalResponse.java,v 1.4 2010/10/12 05:22:03 jglorioso Exp $
 */
public class MockOrbitalResponse extends OrbitalResponse
{
    /**
     * Creates a new MockOrbitalResponse from a request. Status will be "Approved" and
     * unless a cvv2 number of 999 is sent in. Then a decline will be issued.
     *
     * @param request The request.
     */
    public MockOrbitalResponse(OrbitalRequest request) throws APIException
    {
        super
        (
            request,
            new StringBuffer()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<Response>")
                 .append("<NewOrderResp>")
                  .append("<IndustryType></IndustryType>")
                  .append("<MessageType>A</MessageType>")
                  .append("<MerchantID>").append( request.getMerchantId() ).append("</MerchantID>")
                  .append("<TerminalID>").append( request.getTerminalId() ).append("</TerminalID>")
                  .append("<CardBrand>VI</CardBrand>")
                  .append("<AccountNum>4055011111111111</AccountNum>")
                  .append("<OrderID>122003SA</OrderID>")
                  .append("<TxRefNum>45BDF8F144032EE58042B7081F9D0BE186FB5443</TxRefNum>")
                  .append("<TxRefIdx>0</TxRefIdx>")
                  .append("<ProcStatus>0</ProcStatus>")
                  .append("<ApprovalStatus>").append( ("999".equals(request.getField("CardSecVal")) ? "0" : "1") ).append("</ApprovalStatus>")
                  .append("<RespCode>").append( ("999".equals(request.getField("CardSecVal")) ? "C8" : "00") ).append("</RespCode>")
                  .append("<AVSRespCode>F </AVSRespCode>")
                  .append("<CVV2RespCode>M</CVV2RespCode>")
                  .append("<AuthCode>090339</AuthCode>")
                  .append("<RecurringAdviceCd></RecurringAdviceCd>")
                  .append("<CAVVRespCode></CAVVRespCode>")
                  .append("<StatusMsg>").append( ("999".equals(request.getField("CardSecVal")) ? "Declined" : "Approved") ).append("</StatusMsg>")
                  .append("<RespMsg></RespMsg>")
                  .append("<HostRespCode>100</HostRespCode>")
                  .append("<HostAVSRespCode>I1</HostAVSRespCode>")
                  .append("<HostCVV2RespCode>M</HostCVV2RespCode>")
                  .append("<CustomerRefNum></CustomerRefNum>")
                  .append("<CustomerName></CustomerName>")
                  .append("<ProfileProcStatus></ProfileProcStatus>")
                  .append("<CustomerProfileMessage></CustomerProfileMessage>")
                  .append("<BillerReferenceNumber></BillerReferenceNumber>")
                  .append("<RespTime>083858</RespTime>")
                 .append("</NewOrderResp>")
                .append("</Response>").toString()
        );
    }
}