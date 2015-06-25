package com.zitego.customer.beans;

import com.zitego.util.TextUtils;
import java.util.Hashtable;

/**
 * Wraps a comma delimited string of field labels by being constructed with the
 * string from a request variable (or however you want to construct it). The
 * naming format is as follows: labels=name:label,name2:label2,etc.
 * Ex: contactPostalCode=Zipcode.
 *
 * @author John Glorioso
 * @version $Id: LabelBean.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class LabelBean extends Hashtable
{
    /**
     * Creates an empty label bean.
     */
    public LabelBean()
    {
        super();
    }

    /**
     * Creates a new label bean with a comma delimited string of field labels.
     *
     * @param labels The field labels.
     */
    public LabelBean(String labels)
    {
        this();
        setLabels(labels);
    }

    public void setLabels(String labelString)
    {
        if (labelString != null)
        {
            String[] labels = TextUtils.split(labelString, ',');
            for (int i=0; i<labels.length; i++)
            {
                int colonIndex = labels[i].indexOf(":");
                if (colonIndex > -1) put( labels[i].substring(0, colonIndex), labels[i].substring(colonIndex+1) );
            }
        }
    }

    /**
     * Returns a label given the field name and a default.
     *
     * @param f the fields name.
     * @param def The default.
     * @return String
     */
    public String getLabel(String f, String def)
    {
        String ret = (String)get(f);
        if (ret == null) return def;
        else return ret;
    }
}