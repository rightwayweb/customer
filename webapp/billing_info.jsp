<%--
  - Allows edit and update of billing information. Any security needs to be
  - performed in a wrapper page. In addition, state and country drop downs
  - need to be passed in through the request attributes as "state_dropdown"
  - and "country_dropdown" respectively. If they are not, then a text field
  - will be displayed for each. A customer object must be passed in through
  - the request attribute "customer". A request parameter called "excludes"
  - can be passed in as a comma delimited list of fields to disclude. The
  - format is the getter method name without the "get" and the first letter
  - of the word after get in lower case. For example: excludes=secondaryPhone
  - would disclude the secondary phone field. You can also pass in custom
  - label fields for each using the naming format described above in a comma
  - delimited string as follows: labels=name:label,name2:label2,etc.
  - Ex: contactPostalCode=Zipcode
  - Required fields are marked in the same fashion. required_fields=name1,name2,etc
  - If you do not wish to have the save/cancel links at the top, then pass in the
  - show_top_save_links parameter with the value of 0. If you do not want the title
  - Billing Information to appear, then pass in show_title=0. Pass in the linkText 
  - request attribute if you do not wish to have the standard save/cancel links to show.
  - Finally, the transportPath must be passed in if there is one. If the value for
  - parameter last_four is 1 then when saving, the partial cc number will be the
  - last four digits. Otherwise, it will be the first four.
  - @author John Glorioso
  - @version $Id: billing_info.jsp,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
  --%>
<%@ page
    import="com.zitego.customer.beans.RequiredFieldsBean"
    import="com.zitego.customer.beans.LabelBean"
    import="com.zitego.customer.Customer"
    import="com.zitego.customer.BillingInformation"
    import="com.zitego.customer.creditCard.*"
    import="com.zitego.util.StringValidation"
    errorPage="/error.jsp"
%>
<%@ taglib uri="/taglib.tld" prefix="util" %>
<util:request id="customer" name="customer" type="com.zitego.customer.Customer" />
<% if (customer == null) throw new RuntimeException("Missing customer information"); %>
<% BillingInformation info = customer.getBillingInfo(); %>
<% if (info == null) throw new RuntimeException("Missing customer information"); %>
<util:param id="cc_type" name="cc_type" default="<%= (info.getCcType() != null ? String.valueOf( info.getCcType().getValue() ) : "") %>" />
<util:param id="cc_number" name="cc_number" default="<%= info.getCcNumber() %>" />
<util:param id="exp_date" name="exp_date" default="<%= info.getFormattedExpDate() %>" />
<util:param id="name_on_card" name="name_on_card" default="<%= info.getNameOnCard() %>" />
<util:param id="cvv2" name="cvv2" default="<%= info.getCVV2Number() %>" />
<util:param id="company" name="company" default="<%= info.getCompanyName() %>" />
<util:param id="primary_phone" name="primary_phone" default="<%= info.getPrimaryPhone() %>" />
<util:param id="secondary_phone" name="secondary_phone" default="<%= info.getSecondaryPhone() %>" />
<util:param id="fax" name="fax" default="<%= info.getFax() %>" />
<util:param id="mobile" name="mobile" default="<%= info.getMobile() %>" />
<util:param id="email" name="email" default="<%= info.getEmail() %>" />
<util:param id="address1" name="address1" default="<%= info.getAddress1() %>" />
<util:param id="address2" name="address2" default="<%= info.getAddress2() %>" />
<util:param id="city" name="city" default="<%= info.getCity() %>" />
<util:param id="state_id" name="state_id" default="<%= String.valueOf( info.getStateId() ) %>" />
<util:param id="state_name" name="state_name" default="<%= info.getStateName() %>" />
<util:param id="postal_code" name="postal_code" default="<%= info.getPostalCode() %>" />
<util:param id="country_id" name="country_id" default="<%= String.valueOf( info.getCountryId() ) %>" />
<util:param id="country_name" name="country_name" default="<%= info.getCountryName() %>" />
<util:param id="save" name="save" default="0" />
<util:param id="transportPath" name="transport_path" />
<util:param id="cancelLink" name="cancel_link" default="customer" />
<util:param id="wrapperPage" name="wrapper_page" default="/account/contact_info" />
<util:param id="showTopSaveLinks" name="show_top_save_links" default="1" />
<util:param id="showBotSaveLinks" name="show_bot_save_links" default="1" />
<util:param id="showSameAsContact" name="show_same_as_contact" />
<util:param id="showTitle" name="show_title" default="1" />
<util:param id="showCc" name="show_cc" default="1" />
<jsp:useBean id="excludes" scope="request" class="com.zitego.customer.beans.ExcludeBean">
 <jsp:setProperty name="excludes" property="excludes" value="<%= request.getParameter("excludes") %>" />
</jsp:useBean>
<jsp:useBean id="labels" scope="request" class="com.zitego.customer.beans.LabelBean">
 <jsp:setProperty name="labels" property="labels" value="<%= request.getParameter("labels") %>" />
</jsp:useBean>
<jsp:useBean id="required" scope="request" class="com.zitego.customer.beans.RequiredFieldsBean">
 <jsp:setProperty name="required" property="requiredFields" value="<%= request.getParameter("required_fields") %>" />
</jsp:useBean>
<util:request id="stateDropDown" name="state_drop_down" type="com.zitego.web.dropDown.DropDown" />
<util:request id="countryDropDown" name="country_drop_down" type="com.zitego.web.dropDown.DropDown" />
<util:request id="linkText" name="link_text" type="java.lang.String" />
<%
String msg = null;
if ( "1".equals(save) ) msg = saveInfo(customer, request, required, labels);
String contextPath = request.getContextPath();
%>

         <script language="Javascript" src="<%=contextPath%>/js/form_functions.js"></script>
         <script language="Javascript" src="<%=contextPath%>/js/string_validation_funcs.js"></script>
         <script language="Javascript" src="<%=contextPath%>/js/credit_card_functions.js"></script>
         <script language="Javascript">
         clicks = 0;
         function save()
         {
          var frm = document.billing;
          if ( !checkForm() ) return;
          frm.submit();
         }
         function checkForm()
         {
          var frm = document.billing;
          trimFields(frm);
          <% if ( required.isRequired("billingCcType") ) { %>
          if (frm.cc_type.selectedIndex == 0)
          {
            return fieldError(frm.cc_type, "-You must select a <%= labels.getLabel("billingCcType", "credit card type").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingCcNumber") ) { %>
          if ( frm.cc_number.value.charAt(0) != "*" && !validCreditCardNumber(frm.cc_number.value, frm.cc_type.options[frm.cc_type.selectedIndex].text) )
          {
            <% if ( CreditCardManager.isDebugging() ) { %> if (frm.cc_number.value != "5454545454545454") <% } %> return fieldError(frm.cc_number, "-You must enter a valid <%= labels.getLabel("billingCcNumber", "credit card number").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingExpDate") ) { %>
          if ( !validExpDate(frm.exp_date.value) )
          {
            return fieldError(frm.exp_date, "-You must enter a valid <%= labels.getLabel("billingExpDate", "expiration date").toLowerCase() %> for the credit card");
          }
          <% } %>
          <% if ( required.isRequired("billingNameOnCard") ) { %>
          if ( isEmpty(frm.name_on_card.value) )
          {
            return fieldError(frm.name_on_card, "-You must enter the <%= labels.getLabel("billingNameOnCard", "name on the credit card").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingCVV2Number") ) { %>
          if ( isEmpty(frm.cvv2.value) || isNotNumeric(frm.cvv2.value) )
          {
            return fieldError(frm.cvv2, "-You must enter a valid <%= labels.getLabel("billingCVV2Number", "credit card verification number").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingCompanyName") ) { %>
          if ( isEmpty(frm.company.value) )
          {
            return fieldError(frm.company, "-You must enter your <%= labels.getLabel("billingCompanyName", "company name").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingPrimaryPhone") ) { %>
          if ( isEmpty(frm.primary_phone.value) )
          {
            return fieldError(frm.primary_phone, "-You must enter your <%= labels.getLabel("billingPrimaryPhone", "primary phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("billingSecondaryPhone") ) { %>
          if ( isEmpty(frm.secondary_phone.value) )
          {
            return fieldError(frm.secondary_phone, "-You must enter your <%= labels.getLabel("billingSecondaryPhone", "secondary phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("billingMobile") ) { %>
          if ( isEmpty(frm.mobile.value) )
          {
            return fieldError(frm.mobile, "-You must enter your <%= labels.getLabel("billingMobile", "mobile phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("billingFax") ) { %>
          if ( isEmpty(frm.fax.value) )
          {
            return fieldError(frm.fax, "-You must enter your <%= labels.getLabel("billingFax", "fax number").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("billingEmail") ) { %>
          if ( doesNotMatch(frm.email.value, /<%= com.zitego.mail.EmailAddress.getEmailRegexp() %>/) )
          {
            return fieldError(frm.email, "-You must provide a valid <%= labels.getLabel("billingEmail", "email address").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingAddress1") ) { %>
          if ( isEmpty(frm.address1.value) )
          {
            return fieldError(frm.address1, "-You must enter <%= labels.getLabel("billingAddress1", "address 1").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingAddress2") ) { %>
          if ( isEmpty(frm.address2.value) )
          {
            return fieldError(frm.address2, "-You must enter <%= labels.getLabel("billingAddress2", "address 2").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("billingCity") ) { %>
          if ( isEmpty(frm.city.value) )
          {
            return fieldError(frm.city, "-You must enter a <%= labels.getLabel("billingCity", "city").toLowerCase() %>");
          }
          <% } %>
          <% if (stateDropDown != null) { %>
          <%    if (required.isRequired("billingStateId") ) { %>
          if (frm.state_id.selectedIndex == 0)
          {
            return fieldError(frm.state_id, "-You must select a <%= labels.getLabel("billingStateId", "state").toLowerCase() %>");
          }
          frm.state_name.value = frm.state_id.options[frm.state_id.selectedIndex].text;
          <%    } %>
          <% } else { %>
          <%    if (required.isRequired("billingStateName") ) { %>
          if ( isEmpty(frm.state_name.value) )
          {
            return fieldError(frm.state_name, "-You must enter a <%= labels.getLabel("billingStateName", "state").toLowerCase() %>");
          }
          <%    } %>
          <% } %>
          <% if ( required.isRequired("billingPostalCode") ) { %>
          if ( isEmpty(frm.postal_code.value) )
          {
            return fieldError(frm.postal_code, "-You must enter a <%= labels.getLabel("billingPostalCode", "postal code").toLowerCase() %>");
          }
          <% } %>
          <% if (countryDropDown != null) { %>
          <%    if ( required.isRequired("billingCountryId") ) { %>
          if (frm.country_id.selectedIndex == 0)
          {
            return fieldError(frm.country_id, "-You must select a <%= labels.getLabel("billingCountryId", "country").toLowerCase() %>");
          }
          frm.country_name.value = frm.country_id.options[frm.country_id.selectedIndex].text;
          <%    } %>
          <% } else { %>
          <%    if (required.isRequired("billingCountryName") ) { %>
          if ( isEmpty(frm.country_name.value) )
          {
            return fieldError(frm.country_name, "-You must enter a <%= labels.getLabel("billingCountryName", "country").toLowerCase() %>");
          }
          <%    } %>
          <% } %>
          frm.save.value = 1;
          if (++clicks > 1) return false;
          return true;
         }
         </script>
         <form name="billing" action="<%= contextPath %>/<%=transportPath%>" method="POST" onSubmit="return checkForm()">
         <input type="hidden" name="d" value="<%= wrapperPage %>">
         <input type="hidden" name="excludes" value="<%= request.getParameter("excludes") %>">
         <input type="hidden" name="labels" value="<%= request.getParameter("labels") %>">
         <input type="hidden" name="required_fields" value="<%= request.getParameter("required_fields") %>">
         <input type="hidden" name="save" value="0">
         <table cellpadding="3" cellspacing="0" border="0">
          <% if ( "1".equals(showTitle) ) { %>
          <tr><td colspan="2"><b>Billing Information</b></td></tr>
          <% } %>
          <% if ( "1".equals(showTopSaveLinks) ) { %>
          <tr>
           <td class="page_divider" colspan="2">
            <% if (linkText != null) { %>
            <%= linkText %>
            <% } else { %>
            <util:link href="javascript:save()" toolTip="Save Your Billing Information" classAttribute="light" linkText="Save" />
            <util:link href="<%= \"javascript:transport('\"+cancelLink+\"')\" %>" toolTip="Cancel" classAttribute="light" linkText="Cancel" />
            <% } %>
           </td>
          </tr>
          <% } %>
          <% if (msg != null) { %>
          <tr><td colspan="2" align="center"><%= msg %></td></tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCcType") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingCcType") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCcType", "Credit Card Type") %>:
           </td>
           <td>
            <select name="cc_type" tabindex="1">
             <option value="-1">
             <util:iterate id="type" type="com.zitego.customer.creditCard.CreditCardType" collection="<%= CreditCardType.getInstance().getTypes() %>">
             <option value="<%= type.getValue() %>"<%= (String.valueOf( type.getValue() ).equals(cc_type) ? " selected" : "") %>><%= type.getDescription() %>
             </util:iterate>
            </select>
           </td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCcNumber") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingCcNumber") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCcNumber", "Credit Card Number") %>:
           </td>
           <td>
            <% if ( !"1".equals(showCc) ) cc_number = (info.getPartialCcNumber() != null ? "************"+info.getPartialCcNumber() : ""); %>
            <input type="text" name="cc_number" value="<%= cc_number %>"size="16" maxlength="16" autocomplete="off" tabindex="1">
           </td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingExpDate") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingExpDate") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingExpDate", "Expiration Date") %>:
           </td>
           <td><input type="text" name="exp_date" value="<%= exp_date %>"size="5" maxlength="5" tabindex="1"> (mm/yy format)</td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingNameOnCard") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingNameOnCard") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingNameOnCard", "Name On Credit Card") %>:
           </td>
           <td><input type="text" name="name_on_card" size="25" maxlength="100" value="<%= name_on_card %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCVV2Number") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingCVV2Number") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCVV2Number", "Card Validation Code") %>*:
           </td>
           <td>
            <input type="text" name="cvv2" value="<%= cvv2 %>"size="4" maxlength="4" tabindex="1">
           </td>
          </tr>
          <% } %>
          <% if ( "1".equals(showSameAsContact) ) { %>
          <tr>
           <td>&nbsp;</td>
           <td><input type="checkbox" name="same" value="1" onClick="setAddress()" tabIndex="1"> Same as Contact Information</td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCompanyName") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingCompanyName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCompanyName", "Company") %>:
           </td>
           <td><input type="text" name="company" size="25" maxlength="100" value="<%= company %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingPrimaryPhone") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingPrimaryPhone") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingPrimaryPhone", "Primary Phone") %>:
           </td>
           <td><input type="text" name="primary_phone" size="15" maxlength="25" value="<%= primary_phone %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingSecondaryPhone") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingSecondaryPhone") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingSecondaryPhone", "Secondary Phone") %>:
           </td>
           <td><input type="text" name="secondary_phone" size="15" maxlength="25" value="<%= secondary_phone %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingMobile") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingMobile") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingMobile", "Mobile") %>:
           </td>
           <td><input type="text" name="mobile" size="12" maxlength="12" value="<%= mobile %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingFax") ) { %>
          <tr>
           <td class="<%= (required.isRequired("fax") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingFax", "Fax") %>:
           </td>
           <td><input type="text" name="fax" size="15" maxlength="25" value="<%= fax %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingEmail") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingEmail") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingEmail", "Email Address") %>:
           </td>
           <td><input type="text" name="email" size="25" maxlength="255" value="<%= email %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingAddress1") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingAddress1") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingAddress1", "Address 1") %>:
           </td>
           <td><input type="text" name="address1" size="25" maxlength="50" value="<%= address1 %>"tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingAddress2") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingAddress2") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingAddress2", "Address 2") %>:
           </td>
           <td><input type="text" name="address2" size="25" maxlength="50" value="<%= address2 %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCity") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingCity") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCity", "City") %>:
           </td>
           <td><input type="text" name="city" size="25" maxlength="50" value="<%= city %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingStateId") || !excludes.isExcluded("billingStateName") ) { %>
          <tr>
           <% if (stateDropDown != null) { %>
           <td class="<%= (required.isRequired("billingStateId") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingStateId", "State") %>:
           </td>
           <td>
            <% stateDropDown.setSelected(state_id); %>
            <input type="hidden" name="state_name" value="">
            <%= stateDropDown %>
           </td>
           <% } else { %>
           <td class="<%= (required.isRequired("billingStateName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingStateName", "State") %>:
           </td>
           <td><input type="text" name="state_name" size="25" maxlength="50" value="<%= state_name %>" tabindex="1"></td>
           <% } %>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingPostalCode") ) { %>
          <tr>
           <td class="<%= (required.isRequired("billingPostalCode") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingPostalCode", "Postal Code") %>:
           </td>
           <td><input type="text" name="postal_code" size="10" maxlength="10" value="<%= postal_code %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCountryId") || !excludes.isExcluded("billingCountryName") ) { %>
          <tr>
           <% if (countryDropDown != null) { %>
           <td class="<%= (required.isRequired("billingCountryId") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCountryName", "Country") %>:
           </td>
           <td>
            <% countryDropDown.setSelected(country_id); %>
            <%= countryDropDown %>
            <input type="hidden" name="country_name" value="">
           </td>
           <% } else { %>
           <td class="<%= (required.isRequired("billingCountryName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("billingCountryName", "Country") %>:
           </td>
           <td><input type="text" name="country_name" size="25" maxlength="50" value="<%= country_name %>" tabindex="1"></td>
           <% } %>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("billingCVV2Number") ) { %>
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr><td colspan="2" align="center"><table><tr><td valign="top">*</td><td><img src="/images/cvv2.gif"></td></tr></table></td></tr>
          <% } %>
          <% if ( "1".equals(showBotSaveLinks) ) { %>
          <tr>
           <td class="page_divider" colspan="2">
            <% if (linkText != null) { %>
            <%= linkText %>
            <% } else { %>
            <util:link href="javascript:save()" toolTip="Save Your Billing Information" classAttribute="light" linkText="Save" />
            <util:link href="<%= \"javascript:transport('\"+cancelLink+\"')\" %>" toolTip="Cancel" classAttribute="light" linkText="Cancel" />
            <% } %>
           </td>
          </tr>
          <% } %>
         </table>
         <input type="image" src="<%=contextPath%>/images/blank.gif" border="0" width="1" height="1">
         </form>

<%!
private String saveInfo(Customer c, HttpServletRequest request, RequiredFieldsBean required, LabelBean labels)
throws Exception
{
    StringBuffer msg = new StringBuffer();
    BillingInformation info = c.getBillingInfo();
    String val = request.getParameter("cc_type");
    if ( StringValidation.isNotEmpty(val) )
    {
        if ( StringValidation.isNotNumeric(val) ) msg.append("-You must select a ").append( labels.getLabel("billingCcType", "credit card type").toLowerCase() ).append("<br>");
        else info.setCcType( CreditCardType.evaluate(Integer.parseInt(val)) );
    }
    else if ( required.isRequired("billingCcType") )
    {
        msg.append("-You must select a ").append( labels.getLabel("billingCcType", "credit card type").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCcType(null);
    }
    val = request.getParameter("cc_number");
    if ( StringValidation.isNotEmpty(val) )
    {
        if ( !val.startsWith("*") )
        {
            info.setCcNumber(val);
            if ( "1".equals(request.getParameter("last_four")) ) info.setPartialCcNumber( val.substring(val.length()-4) );
            else info.setPartialCcNumber( val.substring(0, 4) );
        }
    }
    else if ( required.isRequired("billingCcNumber") )
    {
        msg.append("-You must enter a valid ").append( labels.getLabel("billingCcNumber", "credit card number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCcNumber(null);
        info.setPartialCcNumber(null);
    }
    val = request.getParameter("exp_date");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setExpDate(val);
    }
    else if ( required.isRequired("billingExpDate") )
    {
        msg.append("-You must enter a valid ").append( labels.getLabel("billingExpDate", "expiration date").toLowerCase() ).append(" for the credit card<br>");
    }
    else
    {
        info.setExpDate( (String)null );
    }
    val = request.getParameter("name_on_card");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setNameOnCard(val);
    }
    else if ( required.isRequired("billingNameOnCard") )
    {
        msg.append("-You must enter the ").append( labels.getLabel("nameOnCard", "name on the credit card").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setNameOnCard(null);
    }
    val = request.getParameter("cvv2");
    if ( StringValidation.isNumericNotNull(val) )
    {
        info.setCVV2Number(val);
    }
    else if ( required.isRequired("billingCVV2Number") )
    {
        msg.append("-You must enter a valid ").append( labels.getLabel("billingCVV2Number", "credit card verification number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCVV2Number(null);
    }
    val = request.getParameter("company");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setCompanyName(val);
    }
    else if ( required.isRequired("billingCompanyName") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("billingCompanyName", "company name").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCompanyName(null);
    }
    val = request.getParameter("primary_phone");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setPrimaryPhone(val);
    }
    else if ( required.isRequired("billingPrimaryPhone") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("billingPrimaryPhone", "primary phone number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setPrimaryPhone(null);
    }
    val = request.getParameter("secondary_phone");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setSecondaryPhone(val);
    }
    else if ( required.isRequired("billingSecondaryPhone") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("billingSecondaryPhone", "secondary phone number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setSecondaryPhone(null);
    }
    val = request.getParameter("mobile");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setMobile(val);
    }
    else if ( required.isRequired("billingMobile") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("billingMobile", "mobile phone number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setMobile(null);
    }
    val = request.getParameter("fax");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setPrimaryPhone(val);
    }
    else if ( required.isRequired("billingFax") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("billingFax", "fax number").toLowerCase() ).append("<br>");
    }
    val = request.getParameter("email");
    if ( StringValidation.isNotEmpty(val) )
    {
        if ( StringValidation.matches(val, com.zitego.mail.EmailAddress.getEmailRegexp()) ) info.setEmail(val);
        else msg.append("-You must provide a valid ").append( labels.getLabel("billingEmail", "email address").toLowerCase() ).append("<br>");
    }
    else if ( required.isRequired("billingEmail") )
    {
        msg.append("-You must provide a valid ").append( labels.getLabel("billingEmail", "email address").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setEmail(null);
    }
    val = request.getParameter("address1");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setAddress1(val);
    }
    else if ( required.isRequired("billingAddress1") )
    {
        msg.append("-You must enter ").append( labels.getLabel("billingAddress1", "address 1").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setAddress1(null);
    }
    val = request.getParameter("address2");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setAddress2(val);
    }
    else if ( required.isRequired("billingAddress2") )
    {
        msg.append("-You must enter ").append( labels.getLabel("billingAddress2", "address ").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setAddress2(null);
    }
    val = request.getParameter("city");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setCity(val);
    }
    else if ( required.isRequired("billingCity") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("billingCity", "city").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCity(null);
    }
    val = request.getParameter("state_id");
    if ( StringValidation.isNumericNotNull(val) )
    {
        info.setStateId( Integer.parseInt(val) );
    }
    else if ( required.isRequired("billingStateId") )
    {
        msg.append("-You must select a ").append( labels.getLabel("billingStateId", "state").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setStateId(-1);
    }
    val = request.getParameter("state_name");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setStateName(val);
    }
    else if ( required.isRequired("billingStateName") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("billingStateName", "state").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setStateName(null);
    }
    val = request.getParameter("postal_code");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setPostalCode(val);
    }
    else if ( required.isRequired("billingPostalCode") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("billingPostalCode", "postal code").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setPostalCode(null);
    }
    val = request.getParameter("country_id");
    if ( StringValidation.isNumericNotNull(val) )
    {
        info.setCountryId( Integer.parseInt(val) );
    }
    else if ( required.isRequired("billingCountryId") )
    {
        msg.append("-You must select a ").append( labels.getLabel("billingCountryId", "state").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCountryId(-1);
    }
    val = request.getParameter("country_name");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setCountryName(val);
    }
    else if ( required.isRequired("billingCountryName") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("billingCountryName", "country").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCountryName(null);
    }

    if ( msg.length() > 0 )
    {
        return "<span class=\"error_text\">" + msg + "</span>";
    }
    else
    {
        c.save();
        CreditCardResponse response = CreditCardManager.editCustomer(c);
        if (response.getErrorMessage() != null) return "<span class=\"error_text\">Error updating your credit card information</span>";
        else return "<span class=\"success_text\">Billing Information Saved</span>";
    }
}
%>
