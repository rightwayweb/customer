<%--
  - This is a generic screen that displays a customer's information. The calling page must pass in
  - the attribute_name parameter which will identify what the CustomerHolder object is stored in the
  - session as. If it is not provided, a default of "customer" is used. It will display all customer
  - fields, contact fields, and billing fields. A request parameter called "excludes" can be passed
  - in as a comma delimited list of fields to disclude. The format is the getter method name without
  - the "get". The get would be replaced by whether it is contact or billing information. For example:
  - exclude=contactSecondaryPhone would disclude the ContactInformation's secondary phone field.
  - State and Country names are the only values displayed. Ids are not, so if you want to disclude
  - either simply put in (contact|billing)StateName (or CountryName). You can also pass in custom
  - label fields for each using the naming format described above in a comma delimited string as
  - follows: labels=name:label,name2:label2,etc. Ex: contactPostalCode=Zipcode
  -
  - In addition, additional customer information can be displayed based on the additional_pages request
  - parameter. By default, only contact, billing, and product information is displayed. The
  - additional_pages parameter is formatted as <page_path>,<section link text>,<section title>:<etc,>.
  - For example, additional_pages=account/domain,Domains,Registered+Domains.
  -
  - Finally, you must specify the absolute path to the contact_info.jsp and billing_info.jsp pages
  - in attributes named contact_info_page and billing_info_page respectively.
  -
  - In addition to paid product subscriptions that are stored in the database, you can pass in a
  - Trial Subscription object that will be listed at the top of the subscriptions section allowing
  - them to upgrade at will. This attribute is to passed in the request as "trial_subscription".
  - If this is provided, then the request parameter "trial_page" will need to be provided as well in
  - order to make the trial membership upgradable.
  -
  - @author John Glorioso
  - @version $Id: customer.jsp,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
  --%>
<%@ page
    import="com.zitego.customer.*"
    import="com.zitego.customer.product.ProductManager"
    import="com.zitego.customer.product.subscription.CustomerSubscription"
    import="com.zitego.sql.*"
    import="com.zitego.report.*"
    import="com.zitego.util.TextUtils"
    import="com.zitego.util.StatusType"
    import="java.util.Vector"
    import="java.sql.*"
    import="java.text.SimpleDateFormat"
    errorPage="/error.jsp"
%>
<%@ taglib uri="/taglib.tld" prefix="util" %>
<%@ taglib uri="/customer.tld" prefix="customer" %>
<util:param id="attributeName" name="attribute_name" default="customer" />
<customer:obj id="customer" attributeName="<%= attributeName %>" />
<% if (customer == null) throw new RuntimeException("No customer to display"); %>
<util:param id="include_domains" name="include_domains" />
<util:param id="contact_info_page" name="contact_info_page" default="/customer/contact_info.jsp" />
<util:param id="billing_info_page" name="billing_info_page" default="/customer/billing_info.jsp" />
<util:param id="product_page" name="product_page" default="/customer/product_info.jsp" />
<util:param id="trial_page" name="trial_page" />
<util:param id="excludeString" name="excludes" />
<util:param id="labelString" name="labels" />
<util:param id="additionalPagesString" name="additional_pages" />
<jsp:useBean id="excludes" scope="request" class="com.zitego.customer.beans.ExcludeBean">
 <jsp:setProperty name="excludes" property="excludes" value="<%= excludeString %>" />
</jsp:useBean>
<jsp:useBean id="labels" scope="request" class="com.zitego.customer.beans.LabelBean">
 <jsp:setProperty name="labels" property="labels" value="<%= labelString %>" />
</jsp:useBean>
<% ContactInformation contact = customer.getContactInfo(); %>
<% BillingInformation billing = customer.getBillingInfo(); %>
<% AdditionalPage[] additionalPages = getAdditionalPages(additionalPagesString); %>
<util:request id="trial" name="trial_subscription" type="com.zitego.customer.product.subscription.TrialSubscription" />
<%! private static SimpleDateFormat START_FORMAT = new SimpleDateFormat("MM/dd/yyyy"); %>

   <script language="Javascript">
   function editContactInfo()
   {
    transport
    (
        '<%= contact_info_page %>', '?excludes=<%= excludeString %>&labels=<%= labelString %>'
    );
   }
   function editBillingInfo()
   {
    transport
    (
        '<%= billing_info_page %>', '?excludes=<%= excludeString %>&labels=<%= labelString %>'
    );
   }
   </script>
   <table cellpadding="3" cellspacing="0" border="0" width="95%">
    <tr><td colspan="2"><b>Account Information</b></td></tr>
    <tr>
     <td>
      <util:link href="#products" toolTip="Product Subscriptions" linkText="Subscriptions" />
      <% for (int i=0; i<additionalPages.length; i++) { %>
      |&nbsp;<util:link href="<%= \"#\"+additionalPages[i].url %>" toolTip="<%= additionalPages[i].linkText %>" linkText="<%= additionalPages[i].linkText %>" />
      <% } %>
      <% if (contact != null) { %>
      |&nbsp;<util:link href="#contact" toolTip="Contact Information" linkText="Contact Information" />
      <% } %>
      <% if (billing != null) { %>
      |&nbsp;<util:link href="#billing" toolTip="Billing Information" linkText="Billing Information" />
      <% } %>
      <hr>
     </td>
    </tr>
    <% if (!excludes.isExcluded("accountNumber") && customer.getId() > -1) { %>
    <tr>
     <td class="form_label_field">
      <%= labels.getLabel("accountNumber", "Account Number") %>:&nbsp;
      <util:format obj="<%= customer.getAccountNumber() %>" />
     </td>
    </tr>
    <% } %>
    <tr><td class="page_divider" colspan="2"><a name="products">Subscriptions</td></tr>
    <tr>
     <td colspan="2">
      <table border="0" width="100%">
       <tr>
        <td class="column_header">Product</td>
        <td class="column_header">Start Date</td>
        <td class="column_header">Base Monthly Fee</td>
       </tr>
       <% if (trial != null) { %>
       <tr>
        <td>
         <util:link href="<%= \"javascript:transport('\"+trial_page+\"')\" %>" toolTip="Upgrade your trial membership">
          <%= trial.getName() %></util:link>
          - <span class="error_text">Expires on <util:format obj="<%= trial.getExpirationDate() %>" format="<%= START_FORMAT %>" /></span>
        </td>
        <td><util:format obj="<%= trial.getStartDate() %>" format="<%= START_FORMAT %>" /></td>
        <td>Free</td>
       </tr>
       <% } %>
       <util:iterate id="product" type="com.zitego.report.DataSet"
                     collection="<%= getSubscriptions(customer) %>"
                     color1="#efefef" color2="#ffffff">
       <util:iteraterow>
        <td>
         <util:link href="<%= \"javascript:transport('\"+product_page+\"','?subscription_id=\"+product.getInt(\"ID\")+\"')\" %>"
                    toolTip="Manage this subscription" linkText="<%= (String)product.get(\"NAME\") %>" />
        </td>
        <td><util:format obj="<%= (java.util.Date)product.get(\"START\") %>" format="<%= START_FORMAT %>" /></td>
        <td><%= product.get("FEE") %></td>
       </util:iteraterow>
       </util:iterate>
      </table>
     </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
    <% for (int i=0; i<additionalPages.length; i++) { %>
    <tr><td class="page_divider" colspan="2"><a name="<%= additionalPages[i].url %>"><%= additionalPages[i].title %></td></tr>
    <jsp:include page="<%= additionalPages[i].url %>" flush="true" />
    <tr><td colspan="2">&nbsp;</td></tr>
    <% } %>
    <% if (contact != null) { %>
    <tr>
     <td class="page_divider"><a name="contact"></a>Contact Information</td>
     <td class="page_divider" align="right">
      <util:link href="javascript:editContactInfo()" toolTip="Edit Your Contact Information" classAttribute="light" linkText="Edit" />
     </td>
    </tr>
    <tr>
     <td colspan="2">
      <table border="0">
       <% if ( !excludes.isExcluded("contactFullName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactFullName", "Name") %>:</td>
        <td><util:format obj="<%= contact.getFullName() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactCompanyName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactCompanyName", "Company") %>:</td>
        <td><util:format obj="<%= contact.getCompanyName() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactPrimaryPhone") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactPrimaryPhone", "Primary Phone Number") %>:</td>
        <td><util:format obj="<%= contact.getPrimaryPhone() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactSecondaryPhone") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactSecondaryPhone", "Secondary Phone Number") %>:</td>
        <td><util:format obj="<%= contact.getSecondaryPhone() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactFax") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactFax", "Fax Number") %>:</td>
        <td><util:format obj="<%= contact.getFax() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactMobile") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactMobile", "Mobile Phone Number") %>:</td>
        <td><util:format obj="<%= contact.getMobile() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactEmail") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactEmail", "Email Address") %>:</td>
        <td><util:format obj="<%= contact.getEmail() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactAddress1") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactAddress1", "Address 1") %>:</td>
        <td><util:format obj="<%= contact.getAddress1() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactAddress2") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactAddress2", "Address 2") %>:</td>
        <td><util:format obj="<%= contact.getAddress2() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactCity") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactCity", "City") %>:</td>
        <td><util:format obj="<%= contact.getCity() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactStateName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactStateName", "State") %>:</td>
        <td><util:format obj="<%= contact.getStateName() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactPostalCode") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactPostalCode", "Postal Code") %>:</td>
        <td><util:format obj="<%= contact.getPostalCode() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("contactCountryName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("contactCountryName", "Country") %>:</td>
        <td><util:format obj="<%= contact.getCountryName() %>" /></td>
       </tr>
       <% } %>
      </table>
     </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
    <% } %>
    <% if (billing != null) { %>
    <tr>
     <td class="page_divider"><a name="billing">Billing Information</td>
     <td class="page_divider" align="right">
      <util:link href="javascript:editBillingInfo()" toolTip="Edit Your Billing Information" classAttribute="light" linkText="Edit" />
     </td>
    </tr>
    <tr>
     <td colspan="2">
      <table border="0">
       <% if ( !excludes.isExcluded("billingCcType") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingCcType", "Credit Card Type") %>:</td>
        <td><util:format obj="<%= billing.getCcType() %>" methods="getDescription" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingCcNumber") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingCcNumber", "Credit Card Number") %>:</td>
        <td>
         <% if (billing.getCcNumber() != null) { %>
         <%= TextUtils.repeat("*", 16) %>
         <% } else { %>
         &nbsp;
         <% } %>
        </td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingExpDate") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingExpDate", "Expiration Date") %>:</td>
        <td><util:format obj="<%= billing.getFormattedExpDate() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingNameOnCard") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingNameOnCard", "Name On Credit Card") %>:</td>
        <td><util:format obj="<%= billing.getNameOnCard() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingCompanyName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingCompanyName", "Company") %>:</td>
        <td><util:format obj="<%= billing.getCompanyName() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingPrimaryPhone") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingPrimaryPhone", "Primary Phone Number") %>:</td>
        <td><util:format obj="<%= billing.getPrimaryPhone() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingSecondaryPhone") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingSecondaryPhone", "Secondary Phone Number") %>:</td>
        <td><util:format obj="<%= billing.getSecondaryPhone() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingFax") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingFax", "Fax Number") %>:</td>
        <td><util:format obj="<%= billing.getFax() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingMobile") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingMobile", "Mobile Phone Number") %>:</td>
        <td><util:format obj="<%= billing.getMobile() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingEmail") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingEmail", "Email Address") %>:</td>
        <td><util:format obj="<%= billing.getEmail() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingAddress1") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingAddress1", "Address 1") %>:</td>
        <td><util:format obj="<%= billing.getAddress1() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingAddress2") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingAddress2", "Address 2") %>:</td>
        <td><util:format obj="<%= billing.getAddress2() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingCity") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingCity", "City") %>:</td>
        <td><util:format obj="<%= billing.getCity() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingStateName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingStateName", "State") %>:</td>
        <td><util:format obj="<%= billing.getStateName() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingPostalCode") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingPostalCode", "Postal Code") %>:</td>
        <td><util:format obj="<%= billing.getPostalCode() %>" /></td>
       </tr>
       <% } %>
       <% if ( !excludes.isExcluded("billingCountryName") ) { %>
       <tr>
        <td class="form_label_field"><%= labels.getLabel("billingCountryName", "Country") %>:</td>
        <td><util:format obj="<%= billing.getCountryName() %>" /></td>
       </tr>
       <% } %>
      </table>
     </td>
    </tr>
    <% } %>
    <tr><td colspan="2">&nbsp;</td></tr>
   </table>

<%!
private DataSetCollection getSubscriptions(Customer c) throws SQLException
{
    CustomerSubscription[] subs = ProductManager.getCustomerSubscriptions(c);

    DataSetCollection ret = new DataSetCollection();
    for (int i=0; i<subs.length; i++)
    {
        DataSet ds = new DataSet();
        ds.put( "ID", subs[i].getId() );
        ds.put( "NAME", subs[i].getName() );
        ds.put( "START", subs[i].getCreationDate() );
        ds.put( "FEE", subs[i].formatPrice(subs[i].getAmountPaid()) );
        ret.add(ds);
    }
    return ret;
}
private AdditionalPage[] getAdditionalPages(String pagesString)
{
    Vector tmp = new Vector();
    String[] pageTokens = TextUtils.split(pagesString, ':');
    for (int i=0; i<pageTokens.length; i++)
    {
        String[] tokens = TextUtils.split(pageTokens[i], ',');
        tmp.add( new AdditionalPage(tokens[0], tokens[1], (tokens.length > 2 ? tokens[2] : null)) );
    }
    AdditionalPage[] ret = new AdditionalPage[pageTokens.length];
    tmp.copyInto(ret);
    return ret;
}

private class AdditionalPage
{
    private String url;
    private String linkText;
    private String title;

    private AdditionalPage(String url, String text, String title)
    {
        this.url = url;
        this.linkText = text;
        this.title = title;
    }
}
%>