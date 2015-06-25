package com.zitego.customer.beans;

import com.zitego.util.TextUtils;
import java.util.Hashtable;

/**
 * Wraps a comma delimited string of fields to mark as required by being constructed
 * with the string from a request variable (or however you want to construct it). It
 * creates a mapping of these names and calls to isExcluded will return true or false.
 *
 * @author John Glorioso
 * @version $Id: RequiredFieldsBean.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class RequiredFieldsBean extends Hashtable
{
    /**
     * Creates an empty required fields bean.
     */
    public RequiredFieldsBean()
    {
        super();
    }

    /**
     * Creates a new required fields bean with a comma delimited string of fields to be required.
     *
     * @param fields The fields to be required.
     */
    public RequiredFieldsBean(String fields)
    {
        this();
        setRequiredFields(fields);
    }

    /**
     * Sets the fields to be required.
     *
     * @param fields The fields to be required.
     */
    public void setRequiredFields(String fields)
    {
        if (fields != null)
        {
            String[] tokens = TextUtils.split(fields, ',');
            for (int i=0; i<tokens.length; i++)
            {
                put(tokens[i], "1");
            }
        }
    }

    /**
     * Returns whether the given field is required.
     *
     * @param field The field to check for required status.
     * @return boolean
     */
    public boolean isRequired(String field)
    {
        if (field == null) return false;
        else return (get(field) != null);
    }
}