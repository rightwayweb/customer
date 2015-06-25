package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;
import com.zitego.util.NameValueObject;

/**
 * A class for returning rates for a product based on a custom include file.
 *
 * @author John Glorioso
 * @version $Id: CustomDeliveryMethod.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class CustomDeliveryMethod extends DeliveryMethod
{
    private String _includeFile;
    private float _charges;

    /**
     * Creates a new custom delivery method with config information.
     *
     * @param config The properties of the delivery method.
     */
    public CustomDeliveryMethod(String config)
    {
        _includeFile = config;
    }

    public float getShippingCharges(Product p)
    {
        NameValueObject obj = getSelectedOption();
        if (obj != null) return Float.parseFloat( obj.getValue() );
        else return 0;
    }

    public DeliveryMethodType getType()
    {
        return DeliveryMethodType.CUSTOM;
    }

    public String getConfig()
    {
        return _includeFile;
    }
}
