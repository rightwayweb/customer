package com.zitego.customer.jsp;

import com.zitego.customer.CustomerHolder;
import com.zitego.customer.Customer;
import com.zitego.customer.CustomerException;
import com.zitego.web.util.PageInfo;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This tag checks the customer object in the customer holder identified by the given attrbibute
 * name to see if it is a paying customer. If it is not, then the page processing is stopped and
 * they are forwarded to the billing information collection screen. Once they complete the form,
 * they will be forwarded back to the original screen. The billing_page can be passed in to denote
 * what page to forward to if they are not a paying customer.
 *
 * @author John Glorioso
 * @version $Id: IsPayingCustomerTag.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class IsPayingCustomerTag extends TagSupport
{
    /** The customer holder attribute name in the session. */
    private String _attributeName;
    /** The billing screen. The default is /billing_info.jsp. */
    private String _billingPage = "/billing_info.jsp";

    /**
     * Sets the attribute name to look for in the session to get a CustomerHolder object.
     *
     * @param name The attribute name.
     */
    public void setAttributeName(String name)
    {
        _attributeName = name;
    }

    /**
     * Sets the billing page to forward to.
     *
     * @param url The page url.
     */
    public void setBillingPage(String url)
    {
        _billingPage = url;
    }

    /**
     * Processes all the work. Always returns EVAL_PAGE unless we are forwarding, then
     * it returns SKIP_PAGE.
     *
     * @return int
     */
    public int doEndTag() throws JspException
    {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = request.getSession();
        CustomerHolder h = (CustomerHolder)session.getAttribute(_attributeName);

        //Set it if it is there
        if (h != null)
        {
            Customer c = null;
            try
            {
                c = h.getCustomer();
            }
            catch (CustomerException ce)
            {
                throw new JspException("An error occurred retrieving the customer", ce);
            }
            if (c != null)
            {
                if (c.getBillingInfo() == null)
                {
                    //Forward to the edit_billing page with all the information about the
                    //page we are currently on
                    request.setAttribute( "forward_page", new PageInfo(request) );
                    try
                    {
                        pageContext.include(_billingPage);
                    }
                    catch (Exception e)
                    {
                        throw new JspException("Could not include billing page: "+_billingPage, e);
                    }
                }
                return EVAL_PAGE;
            }
        }
        throw new JspException("An error occurred retrieving the customer");
    }
}