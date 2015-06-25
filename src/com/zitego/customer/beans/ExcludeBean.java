package com.zitego.customer.beans;

import com.zitego.util.TextUtils;
import java.util.Hashtable;

/**
 * Wraps a comma delimited string of fields to exclude by being constructed with the
 * string from a request variable (or however you want to construct it). It creates
 * a mapping of these names and calls to isExcluded will return true or false.
 *
 * @author John Glorioso
 * @version $Id: ExcludeBean.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class ExcludeBean extends Hashtable
{
    /**
     * Creates an empty exclude bean.
     */
    public ExcludeBean()
    {
        super();
    }

    /**
     * Creates a new exclude bean with a comma delimited string of fields to exclude.
     *
     * @param excludes The fields to exclude.
     */
    public ExcludeBean(String excludes)
    {
        this();
        setExcludes(excludes);
    }

    /**
     * Sets the fields to exclude.
     *
     * @param excludes The fields to exclude.
     */
    public void setExcludes(String excludes)
    {
        if (excludes != null)
        {
            String[] tokens = TextUtils.split(excludes, ',');
            for (int i=0; i<tokens.length; i++)
            {
                put(tokens[i], "1");
            }
        }
    }

    /**
     * Returns whether the given field is excluded.
     *
     * @param field The field to check.
     * @return boolean
     */
    public boolean isExcluded(String field)
    {
        if (field == null) return false;
        else return (get(field) != null);
    }
}