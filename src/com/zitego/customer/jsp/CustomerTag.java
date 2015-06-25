package com.zitego.customer.jsp;

import com.zitego.customer.CustomerHolder;
import com.zitego.customer.Customer;
import com.zitego.customer.CustomerException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A Tag for retrieving a Customer object out of the session. This uses the sessionAttribute
 * property to retrieve a CustomerHolder object to get the Customer. If the sessionAttribute
 * property is not set, then a customer object is not stored in the page context.
 *
 * @author John Glorioso
 * @version $Id: CustomerTag.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CustomerTag extends TagSupport
{
    private String _attributeName;

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
     * Processes all the work. Always returns skip body.
     *
     * @return int
     */
    public int doStartTag() throws JspException
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
            if (c != null) pageContext.setAttribute(getId(), c);
        }
        return SKIP_BODY;
    }
}