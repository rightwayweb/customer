package com.zitego.customer.jsp;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
 * Sets the variable information for storing a customer in the page.
 *
 * @author John Glorioso
 * @version $Id: CustomerTagExtraInfo.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CustomerTagExtraInfo extends TagExtraInfo
{
	public CustomerTagExtraInfo() {}

	public VariableInfo[] getVariableInfo(TagData data)
	{
	    return new VariableInfo[]
	    {
	        new VariableInfo( (String)data.getAttribute("id"), "com.zitego.customer.Customer", true, VariableInfo.AT_BEGIN )
	    };
	}
}