<%--
  - Allows edit and update of subscription information. Any security needs to be
  - performed in a wrapper page. Also, the transportPath must be passed in if
  - there is one. Lastly, the customer_attr attribute must be passed in to identify
  - what the customer is stored as in the session.
  - @author John Glorioso
  - @version $Id: subscription_info.jsp,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
  --%>

<%@ page
    import="com.zitego.customer.Customer"
    import="com.zitego.customer.product.subscription.CustomerSubscription"
    import="com.zitego.customer.product.*"
    import="com.zitego.util.StringValidation"
    import="java.text.SimpleDateFormat"
    import="java.util.Date"
    errorPage="/error.jsp"
%>
<%@ taglib uri="/taglib.tld" prefix="util" %>
<%@ taglib uri="/customer.tld" prefix="customer" %>
<util:param id="customer_attr" name="customer_attr" default="customer" />
<customer:obj id="customer" attributeName="<%= customer_attr %>" />
<% if (customer == null) throw new RuntimeException("Missing customer information"); %>
<util:param id="subscription_id" name="subscription_id" default="-1" />
<util:param id="transportPath" name="transport_path" />
<util:param id="wrapperPage" name="wrapper_page" default="/account/contact_info" />
<util:param id="cancelLink" name="cancel_link" default="customer" />
<util:param id="upgradeLink" name="upgrade_link" />
<%
CustomerSubscription sub = new CustomerSubscription( Long.parseLong(subscription_id), customer.getDBHandle() );
if (sub.getId() > -1) sub.init();
String contextPath = request.getContextPath();
%>
<%! private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy"); %>

         <script language="Javascript" src="<%=contextPath%>/js/form_functions.js"></script>
         <script language="Javascript" src="<%=contextPath%>/js/string_validation_funcs.js"></script>
         <script language="Javascript">
         clicks = 0;
         function save()
         {
          var frm = document.subscription;
          if ( !checkForm() ) return;
          frm.submit();
         }
         function checkForm()
         {
          var frm = document.subscription;
          trimFields(frm);
          frm.save.value = 1;
          checkForm = new function() { return false; }
          return true;
         }
         </script>
         <form name="subscription" action="<%= contextPath %>/<%=transportPath%>" method="POST" onSubmit="return checkForm()">
         <input type="hidden" name="d" value="<%= wrapperPage %>">
         <input type="hidden" name="subscription_id" value="<%= subscription_id %>">
         <input type="hidden" name="save" value="0">
         <table cellpadding="3" cellspacing="0" border="0" width="95%">
          <tr><td><b>Subscription Information</b></td></tr>
          <tr>
           <td class="page_divider">
            <% String link = (upgradeLink.startsWith("javascript:") ? upgradeLink : "javascript:transport('"+upgradeLink+"')"); %>
            <% if ( link.endsWith(")") ) link = link.substring(0, link.length()-1); %>
            <% link +=  ",'id=" + sub.getId() + "&customer_attr=" + customer_attr + "')"; %>
            <% if ( !"".equals(upgradeLink) ) { %>
            <util:link href="<%= link %>" toolTip="Upgrade" classAttribute="light" linkText="Upgrade" />
            <% } %>
            <% link = (cancelLink.startsWith("javascript:") ? cancelLink : "javascript:transport('"+cancelLink+"')"); %>
            <util:link href="<%= link %>" toolTip="Back" classAttribute="light" linkText="Back" />
           </td>
          </tr>
          <tr><td class="form_label_field"><b><%= sub.getName() %></b></td></tr>
          <tr><td class="form_label_field">Start Date:&nbsp;<util:format obj="<%= sub.getCreationDate() %>" format="<%= DATE_FORMAT %>" /></td></tr>
          <tr><td class="form_label_field">Expiration Date:&nbsp;<util:format obj="<%= sub.getEndDate() %>" format="<%= DATE_FORMAT %>" /></td></tr>
          <% Date dt = sub.getEndDate(); %>
          <% if (dt != null) { %>
          <tr><td class="form_label_field">Contract Expires:&nbsp;<util:format obj="<%= dt %>" format="<%= DATE_FORMAT %>" /></td></tr>
          <% } %>
          <tr><td>Details:</td></tr>
          <tr>
           <td>
            <table border="0" width="100%">
             <util:iterate id="feature" type="com.zitego.customer.product.subscription.CustomerSubscriptionDetail" collection="<%= sub.getActiveFeatures() %>">
             <tr>
              <td>
               <table width="100%">
                <tr>
                 <td width="5%">&nbsp;</td>
                 <td valign="top" width="5%"><img src="<%= contextPath %>/images/ball83.gif"></td>
                 <td>
                  <u><%= feature.getDisplayText() %></u>:<br>
                  <% if (feature.getOption() != null) { %>
                  <%= feature.getOption().getDescription() %>
                  <% } else { %>
                  <%= feature.getDescription() %>
                  <% } %>
                 </td>
                </tr>
               </table>
              </td>
              <td align="right">
               <% if ( feature.showPrice() ) { %>
               <%= feature.formatPrice( feature.getAmountPaid() ) %> per month
               <% } else { %>
               &nbsp;
               <% } %>
              </td>
             </tr>
             </util:iterate>
             <tr><td colspan="2">&nbsp;</td></tr>
             <tr>
              <td>Total Monthly Charge:</td>
              <td align="right"><%= sub.formatPrice( sub.getAmountPaid() ) %></td>
             </tr>
            </table>
           </td>
          </tr>
          <tr>
           <td class="page_divider">
            <% link = (upgradeLink.startsWith("javascript:") ? upgradeLink : "javascript:transport('"+upgradeLink+"')"); %>
            <% if ( link.endsWith(")") ) link = link.substring(0, link.length()-1); %>
            <% link +=  ",'id=" + sub.getId() + "&customer_attr=" + customer_attr + "')"; %>
            <% if ( !"".equals(upgradeLink) ) { %>
            <util:link href="<%= link %>" toolTip="Upgrade" classAttribute="light" linkText="Upgrade" />
            <% } %>
            <% link = (cancelLink.startsWith("javascript:") ? cancelLink : "javascript:transport('"+cancelLink+"')"); %>
            <util:link href="<%= link %>" toolTip="Back" classAttribute="light" linkText="Back" />
           </td>
          </tr>
         </table>