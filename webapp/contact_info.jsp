<%--
  - Allows edit and update of contact information. Any security needs to be
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
  - Contact Information to appear, then pass in show_title=0. Pass in the linkText
  - request attribute if you do not wish to have the standard save/cancel links to show.
  - Finally, the transportPath must be passed in if there is one.
  - @author John Glorioso
  - @version $Id: contact_info.jsp,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
  --%>
<%@ page
    import="com.zitego.customer.beans.RequiredFieldsBean"
    import="com.zitego.customer.beans.LabelBean"
    import="com.zitego.customer.Customer"
    import="com.zitego.customer.ContactInformation"
    import="com.zitego.util.StringValidation"
    import="com.zitego.util.StatusType"
    errorPage="/error.jsp"
%>
<%@ taglib uri="/taglib.tld" prefix="util" %>
<util:request id="customer" name="customer" type="com.zitego.customer.Customer" />
<% if (customer == null) throw new RuntimeException("Missing customer information"); %>
<% ContactInformation info = customer.getContactInfo(); %>
<% if (info == null) throw new RuntimeException("Missing customer information"); %>
<util:param id="prefix" name="prefix" default="<%= info.getPrefix() %>" />
<util:param id="first_name" name="first_name" default="<%= info.getFirstName() %>" />
<util:param id="last_name" name="last_name" default="<%= info.getLastName() %>" />
<util:param id="suffix" name="suffix" default="<%= info.getSuffix() %>" />
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
<util:param id="active" name="active" default="<%= (\"1\".equals(save) ? String.valueOf(StatusType.INACTIVE.getValue()) : String.valueOf(customer.getStatus())) %>" />
<util:param id="transportPath" name="transport_path" />
<util:param id="cancelLink" name="cancel_link" default="customer" />
<util:param id="wrapperPage" name="wrapper_page" default="/account/contact_info" />
<util:param id="showTopSaveLinks" name="show_top_save_links" default="1" />
<util:param id="showBotSaveLinks" name="show_bot_save_links" default="1" />
<util:param id="showTitle" name="show_title" default="1" />
<util:param id="showId" name="show_id" default="0" />
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
         <script language="Javascript">
         clicks = 0;
         function save()
         {
          var frm = document.contact;
          if ( !checkForm() ) return;
          frm.submit();
         }
         function checkForm()
         {
          var frm = document.contact;
          trimFields(frm);
          <% if ( required.isRequired("contactPrefix") ) { %>
          if ( isEmpty(frm.prefix.value) )
          {
            return fieldError(frm.prefix, "-You must enter your <%= labels.getLabel("contactPrefix", "name prefix").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactFirstName") ) { %>
          if ( isEmpty(frm.first_name.value) )
          {
            return fieldError(frm.first_name, "-You must enter your <%= labels.getLabel("contactFirstName", "first name").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactLastName") ) { %>
          if ( isEmpty(frm.last_name.value) )
          {
            return fieldError(frm.last_name, "-You must enter your <%= labels.getLabel("contactLastName", "last name").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactSuffix") ) { %>
          if ( isEmpty(frm.suffix.value) )
          {
            return fieldError(frm.suffix, "-You must enter your <%= labels.getLabel("contactSuffix", "name suffix").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactCompanyName") ) { %>
          if ( isEmpty(frm.company.value) )
          {
            return fieldError(frm.last_name, "-You must enter your <%= labels.getLabel("contactCompanyName", "company name").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactPrimaryPhone") ) { %>
          if ( isEmpty(frm.primary_phone.value) )
          {
            return fieldError(frm.primary_phone, "-You must enter your <%= labels.getLabel("contactPrimaryPhone", "primary phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("contactSecondaryPhone") ) { %>
          if ( isEmpty(frm.secondary_phone.value) )
          {
            return fieldError(frm.secondary_phone, "-You must enter your <%= labels.getLabel("contactSecondaryPhone", "secondary phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("contactMobile") ) { %>
          if ( isEmpty(frm.mobile.value) )
          {
            return fieldError(frm.mobile, "-You must enter your <%= labels.getLabel("contactMobile", "mobile phone").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("contactFax") ) { %>
          if ( isEmpty(frm.fax.value) )
          {
            return fieldError(frm.fax, "-You must enter your <%= labels.getLabel("contactFax", "fax number").toLowerCase() %> number");
          }
          <% } %>
          <% if ( required.isRequired("contactEmail") ) { %>
          if ( doesNotMatch(frm.email.value, /<%= com.zitego.mail.EmailAddress.getEmailRegexp() %>/) )
          {
            return fieldError(frm.email, "-You must provide a valid <%= labels.getLabel("contactEmail", "email address").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactAddress1") ) { %>
          if ( isEmpty(frm.address1.value) )
          {
            return fieldError(frm.address1, "-You must enter <%= labels.getLabel("contactAddress1", "address 1").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactAddress2") ) { %>
          if ( isEmpty(frm.address2.value) )
          {
            return fieldError(frm.address2, "-You must enter <%= labels.getLabel("contactAddress2", "address 2").toLowerCase() %>");
          }
          <% } %>
          <% if ( required.isRequired("contactCity") ) { %>
          if ( isEmpty(frm.city.value) )
          {
            return fieldError(frm.city, "-You must enter a <%= labels.getLabel("contactCity", "city").toLowerCase() %>");
          }
          <% } %>
          <% if (stateDropDown != null) { %>
          <%    if (required.isRequired("contactStateId") ) { %>
          if (frm.state_id.selectedIndex == 0)
          {
            return fieldError(frm.state_id, "-You must select a <%= labels.getLabel("contactStateId", "state").toLowerCase() %>");
          }
          frm.state_name.value = frm.state_id.options[frm.state_id.selectedIndex].text;
          <%    } %>
          <% } else { %>
          <%    if (required.isRequired("contactStateName") ) { %>
          if ( isEmpty(frm.state_name.value) )
          {
            return fieldError(frm.state_name, "-You must enter a <%= labels.getLabel("contactStateName", "state").toLowerCase() %>");
          }
          <%    } %>
          <% } %>
          <% if ( required.isRequired("contactPostalCode") ) { %>
          if ( isEmpty(frm.postal_code.value) )
          {
            return fieldError(frm.postal_code, "-You must enter a <%= labels.getLabel("contactPostalCode", "postal code").toLowerCase() %>");
          }
          <% } %>
          <% if (countryDropDown != null) { %>
          <%    if ( required.isRequired("contactCountryId") ) { %>
          if (frm.country_id.selectedIndex == 0)
          {
            return fieldError(frm.country_id, "-You must select a <%= labels.getLabel("contactCountryId", "country").toLowerCase() %>");
          }
          frm.country_name.value = frm.country_id.options[frm.country_id.selectedIndex].text;
          <%    } %>
          <% } else { %>
          <%    if (required.isRequired("contactCountryName") ) { %>
          if ( isEmpty(frm.country_name.value) )
          {
            return fieldError(frm.country_name, "-You must enter a <%= labels.getLabel("contactCountryName", "country").toLowerCase() %>");
          }
          <%    } %>
          <% } %>
          frm.save.value = 1;
          if (++clicks > 1) return false;
          return true;
         }
         </script>
         <form name="contact" action="<%= contextPath %>/<%=transportPath%>" method="POST" onSubmit="return checkForm()">
         <input type="hidden" name="id" value="<%= customer.getId() %>">
         <input type="hidden" name="d" value="<%= wrapperPage %>">
         <input type="hidden" name="excludes" value="<%= request.getParameter("excludes") %>">
         <input type="hidden" name="labels" value="<%= request.getParameter("labels") %>">
         <input type="hidden" name="required_fields" value="<%= request.getParameter("required_fields") %>">
         <input type="hidden" name="save" value="0">
         <table cellpadding="3" cellspacing="0" border="0" width="95%">
         <% if ( "1".equals(showTitle) ) { %>
          <tr><td colspan="2"><b>Contact Information</b></td></tr>
          <% } %>
          <% if ( "1".equals(showTopSaveLinks) ) { %>
          <tr>
           <td class="page_divider" colspan="2">
            <% if (linkText != null) { %>
            <%= linkText %>
            <% } else { %>
            <util:link href="javascript:save()" toolTip="Save Your Contact Information" classAttribute="light" linkText="Save" />
            <util:link href="<%= \"javascript:transport('\"+cancelLink+\"')\" %>" toolTip="Cancel" classAttribute="light" linkText="Cancel" />
            <% } %>
           </td>
          </tr>
          <% } %>
          <% if (msg != null) { %>
          <tr><td colspan="2" align="center"><%= msg %></td></tr>
          <% } %>
          <% if ( "1".equals(showId) ) { %>
          <tr>
           <td class="form_label_field">Id:</td>
           <td><%= (customer.getId() > -1 ? String.valueOf(customer.getId()) : "") %></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactPrefix") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactPrefix") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactPrefix", "Prefix") %>:
           </td>
           <td><input type="text" name="prefix" size="5" maxlength="5" value="<%= prefix %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactFirstName") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactFirstName") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactFirstName", "First Name") %>:
           </td>
           <td><input type="text" name="first_name" size="25" maxlength="50" value="<%= first_name %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactLastName") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactLastName") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactLastName", "Last Name") %>:
           </td>
           <td><input type="text" name="last_name" size="25" maxlength="75" value="<%= last_name %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactSuffix") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactSuffix") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactSuffix", "Suffix") %>:
           </td>
           <td><input type="text" name="suffix" size="5" maxlength="10" value="<%= suffix %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactCompanyName") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactCompanyName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactCompanyName", "Company") %>:
           </td>
           <td><input type="text" name="company" size="25" maxlength="100" value="<%= company %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactPrimaryPhone") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactPrimaryPhone") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactPrimaryPhone", "Primary Phone") %>:
           </td>
           <td><input type="text" name="primary_phone" size="15" maxlength="25" value="<%= primary_phone %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactSecondaryPhone") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactSecondaryPhone") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactSecondaryPhone", "Secondary Phone") %>:
           </td>
           <td><input type="text" name="secondary_phone" size="15" maxlength="25" value="<%= secondary_phone %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactMobile") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactMobile") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactMobile", "Mobile") %>:
           </td>
           <td><input type="text" name="mobile" size="12" maxlength="12" value="<%= mobile %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactFax") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactFax") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactFax", "Fax") %>:
           </td>
           <td><input type="text" name="fax" size="15" maxlength="25" value="<%= fax %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactEmail") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactEmail") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactEmail", "Email Address") %>:
           </td>
           <td><input type="text" name="email" size="25" maxlength="255" value="<%= email %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactAddress1") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactAddress1") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactAddress1", "Address 1") %>:
           </td>
           <td><input type="text" name="address1" size="25" maxlength="50" value="<%= address1 %>"tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactAddress2") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactAddress2") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactAddress2", "Address 2") %>:
           </td>
           <td><input type="text" name="address2" size="25" maxlength="50" value="<%= address2 %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactCity") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactCity") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactCity", "City") %>:
           </td>
           <td><input type="text" name="city" size="25" maxlength="50" value="<%= city %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactStateId") || !excludes.isExcluded("contactStateName") ) { %>
          <tr>
           <% if (stateDropDown != null) { %>
           <td class="<%= (required.isRequired("contactStateId") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactStateId", "State") %>:
           </td>
           <td>
            <% stateDropDown.setSelected(state_id); %>
            <input type="hidden" name="state_name" value="">
            <%= stateDropDown %>
           </td>
           <% } else { %>
           <td class="<%= (required.isRequired("contactStateName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactStateName", "State") %>:
           </td>
           <td><input type="text" name="state_name" size="25" maxlength="50" value="<%= state_name %>" tabindex="1"></td>
           <% } %>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactPostalCode") ) { %>
          <tr>
           <td class="<%= (required.isRequired("contactPostalCode") ? "required_" : "") %>form_label_field" nowrap>
            <%= labels.getLabel("contactPostalCode", "Postal Code") %>:
           </td>
           <td><input type="text" name="postal_code" size="10" maxlength="10" value="<%= postal_code %>" tabindex="1"></td>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("contactCountryId") || !excludes.isExcluded("contactCountryName") ) { %>
          <tr>
           <% if (countryDropDown != null) { %>
           <td class="<%= (required.isRequired("contactCountryId") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactCountryName", "Country") %>:
           </td>
           <td>
            <% countryDropDown.setSelected(country_id); %>
            <%= countryDropDown %>
            <input type="hidden" name="country_name" value="">
           </td>
           <% } else { %>
           <td class="<%= (required.isRequired("contactCountryName") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("contactCountryName", "Country") %>:
           </td>
           <td><input type="text" name="country_name" size="25" maxlength="50" value="<%= country_name %>" tabindex="1"></td>
           <% } %>
          </tr>
          <% } %>
          <% if ( !excludes.isExcluded("active") ) { %>
          <tr>
           <td class="<%= (required.isRequired("active") ? "required_" : "") %>form_label_field">
            <%= labels.getLabel("active", "Active?") %>:
           </td>
           <td><input type="checkbox" name="active" value="1" tabindex="1"<%= ("1".equals(active) ? " checked" : "") %>></td>
          </tr>
          <% } %>
          <% if ( "1".equals(showBotSaveLinks) ) { %>
          <tr>
           <td class="page_divider" colspan="2">
            <% if (linkText != null) { %>
            <%= linkText %>
            <% } else { %>
            <util:link href="javascript:save()" toolTip="Save Your Contact Information" classAttribute="light" linkText="Save" />
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
    ContactInformation info = c.getContactInfo();
    String val = request.getParameter("prefix");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setPrefix(val);
    }
    else if ( required.isRequired("contactPrefix") )
    {
        msg.append("-You must enter your name ").append( labels.getLabel("contactPrefix", "name prefix").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setPrefix(null);
    }
    val = request.getParameter("first_name");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setFirstName(val);
    }
    else if ( required.isRequired("contactFirstName") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactFirstName", "first name").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setFirstName(null);
    }
    val = request.getParameter("last_name");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setLastName(val);
    }
    else if ( required.isRequired("contactLastName") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactLastName", "last name").toLowerCase() ).append("<br>");
    }
    val = request.getParameter("suffix");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setSuffix(val);
    }
    else if ( required.isRequired("contactSuffix") )
    {
        msg.append("-You must enter your name ").append( labels.getLabel("contactSuffix", "suffix").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setSuffix(null);
    }
    val = request.getParameter("company");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setCompanyName(val);
    }
    else if ( required.isRequired("contactCompanyName") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactCompanyName", "company name").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactPrimaryPhone") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactPrimaryPhone", "primary phone number").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactSecondaryPhone") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactSecondaryPhone", "secondary phone number").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactMobile") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactMobile", "mobile phone number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setMobile(null);
    }
    val = request.getParameter("fax");
    if ( StringValidation.isNotEmpty(val) )
    {
        info.setFax(val);
    }
    else if ( required.isRequired("contactFax") )
    {
        msg.append("-You must enter your ").append( labels.getLabel("contactFax", "fax number").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setFax(null);
    }
    val = request.getParameter("email");
    if ( StringValidation.isNotEmpty(val) )
    {
        if ( StringValidation.matches(val, com.zitego.mail.EmailAddress.getEmailRegexp()) ) info.setEmail(val);
        else msg.append("-You must provide a valid ").append( labels.getLabel("contactEmail", "email address").toLowerCase() ).append("<br>");
    }
    else if ( required.isRequired("contactEmail") )
    {
        msg.append("-You must provide a valid ").append( labels.getLabel("contactEmail", "email address").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactAddress1") )
    {
        msg.append("-You must enter ").append( labels.getLabel("contactAddress1", "address 1").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactAddress2") )
    {
        msg.append("-You must enter ").append( labels.getLabel("contactAddress2", "address ").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactCity") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("contactCity", "city").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactStateId") )
    {
        msg.append("-You must select a ").append( labels.getLabel("contactStateId", "state").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactStateName") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("contactStateName", "state").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactPostalCode") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("contactPostalCode", "postal code").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactCountryId") )
    {
        msg.append("-You must select a ").append( labels.getLabel("contactCountryId", "state").toLowerCase() ).append("<br>");
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
    else if ( required.isRequired("contactCountryName") )
    {
        msg.append("-You must enter a ").append( labels.getLabel("contactCountryName", "country").toLowerCase() ).append("<br>");
    }
    else
    {
        info.setCountryName(null);
    }
    val = request.getParameter("active");
    c.setStatus( ("1".equals(val) ? StatusType.ACTIVE.getValue() : StatusType.INACTIVE.getValue()) );

    if ( msg.length() > 0 )
    {
        return "<span class=\"error_text\">" + msg + "</span>";
    }
    else
    {
        c.save();
        return "<span class=\"success_text\">Contact Information Saved</span>";
    }
}
%>
