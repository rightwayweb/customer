package com.zitego.customer.product;

import com.zitego.report.DataSet;

/**
 * This represents a custom value for a product. It contains a field for most database
 * column types. The column type is specified by CustomValueType in the getType() method.
 * getValue returns an Object. There are coorresponding methods for getIntValue,
 * getFloatValue, getLongValue, getShortValue, getStringValue, and getDateValue().
 *
 * @author John Glorioso
 * @version $Id: CustomValue.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CustomValue extends DataSet
{
    private String _name;
    
    /**
     * Creates a new custom value with a name.
     *
     * @param name The name.
     * @throws IllegalArgumentException if the name is null.
     */
    public CustomValue(String name)
    {
        super();
        _name = name;
    }
    
    /**
     * Sets the name of this custom value.
     *
     * @param name The name.
     */
    public void setName(String name)
    {
        _name = name;
    }
    
    /**
     * Returns the name of this custom value.
     *
     * @return String
     */
    public String getName()
    {
        return _name;
    }
}