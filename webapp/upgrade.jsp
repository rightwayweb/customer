<%--
  - This page loads the subscription based on the id, then forwards to a page that coincides
  - with the universal identifier of the subscription. If no upgrade page exists, then a message
  - is displayed. Any security checks should be performed by the page forwarded to specific to
  - the subscription. Lastly, the customer_attr attribute must be passed in to identify
  - what the customer is stored as in the session.
  - @author John Glorioso
  - @version $Id: upgrade.jsp,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
  --%>
<%@ page
    import="com.zitego.customer.Customer"
    import="com.zitego.customer.product.subscription.*"
    import="java.util.Hashtable"
    errorPage="/error.jsp"
%>
<%@ taglib uri="/taglib.tld" prefix="util" %>
<%@ taglib uri="/customer.tld" prefix="customer" %>
<util:param id="customer_attr" name="customer_attr" default="customer" />
<customer:obj id="customer" attributeName="<%= customer_attr %>" />
<util:param id="id" name="id" default="-1" />
<%
CustomerSubscription sub = new CustomerSubscription(Long.parseLong(id), customer.getDBHandle() );
sub.init();
String pg = (String)PAGES.get( sub.getUniversalIdentifier() );
%>
<% if (pg != null) { %>
<util:request id="subscription" name="subscription" value="<%= sub %>" />
<jsp:include page="<%= pg %>" flush="true" />
<% } else { %>
<div align="center"><span class="error_text">Invalid Upgrade Request</span></div>
<% } %>

<%!
private static final Hashtable PAGES = new Hashtable();
static
{
    PAGES.put("ZITEGO_MAIL", "/account/upgrade_mail.jsp");
    PAGES.put("WEBMANAGER_TRIAL_MEMBERSHIP", "/account/upgrade_trial.jsp");
    PAGES.put("BASIC_CONTENT_HOSTING_PACKAGE", "/account/upgrade_hosting.jsp");
    PAGES.put("ECONOMY_CONTENT_HOSTING_PACKAGE", "/account/upgrade_hosting.jsp");
    PAGES.put("PREMIUM_CONTENT_HOSTING_PACKAGE", "/account/upgrade_hosting.jsp");
}
%>