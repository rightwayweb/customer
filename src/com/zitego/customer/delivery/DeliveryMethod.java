package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;
import com.zitego.util.NameValueObject;

/**
 * A delivery method. This class is abstract. getShippingCharges(Product)
 * must be defined by extending classes.
 *
 * @author John Glorioso
 * @version $Id: DeliveryMethod.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public abstract class DeliveryMethod
{
    private String _description;
    private NameValueObject[] _options = new NameValueObject[0];
    private int _selectedOption = -1;

    /**
     * Returns the shipping charges for the given product.
     *
     * @return float
     */
    public abstract float getShippingCharges(Product p);

    /**
     * Returns the type of delivery method.
     *
     * @return DeliveryMethodType
     */
    public abstract DeliveryMethodType getType();

    /**
     * Returns a string representation of this delivery method's configuration.
     *
     * @return String
     */
    public abstract String getConfig();

    /**
     * Sets the description.
     *
     * @param desc The description.
     */
    public void setDescription(String desc)
    {
        _description = desc;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the options.
     *
     * @param options The options.
     */
    public void setOptions(NameValueObject[] options)
    {
        _options = options;
        if (_options == null) _options = new NameValueObject[0];
    }

    /**
     * Returns the options.
     *
     * @return NameValueObject[]
     */
    public NameValueObject[] getOptions()
    {
        return _options;
    }

    /**
     * Sets the selected option.
     *
     * @param opt The option.
     */
    public void setSelectedOption(int opt)
    {
        _selectedOption = opt;
    }

    /**
     * Returns the selected option or null if there is none.
     *
     * @return NameValueObject
     */
    public NameValueObject getSelectedOption()
    {
        if (_selectedOption >= 0 && _selectedOption < _options.length) return _options[_selectedOption];
        else return null;
    }
}