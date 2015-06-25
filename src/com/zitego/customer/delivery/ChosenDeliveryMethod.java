package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;
/**
 * This is a chosen devliery method complete with description and price.
 *
 * @author John Glorioso
 * @version $Id: ChosenDeliveryMethod.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class ChosenDeliveryMethod extends DeliveryMethod
{
    private float _cost = 0f;
    private DeliveryMethodType _type;

    /**
     * Creates a chosen delivery method.
     *
     * @param desc The description.
     * @param cost The cost.
     * @param type The delivery method type.
     */
    public ChosenDeliveryMethod(String desc, float cost, DeliveryMethodType type)
    {
        setDescription(desc);
        _cost = cost;
        if (_cost < 0) _cost = 0;
        _type = type;
    }

    /**
     * Creates a chosen delivery method based on a delimited string in the format
     * TYPE|COST|DESCRIPTION.
     *
     * @param config The coinfig.
     */
    public ChosenDeliveryMethod(String config)
    {
        int index1 = config.indexOf("|");
        int index2 = config.indexOf("|", index1+1);
        _type = DeliveryMethodType.evaluate( Integer.parseInt(config.substring(0, index1)) );
        _cost = Float.parseFloat( config.substring(index1+1, index2) );
        setDescription( config.substring(index2+1) );
    }

    public float getShippingCharges(Product p)
    {
        return _cost;
    }

    public float getShippingCost()
    {
        return _cost;
    }

    public String getConfig()
    {
        return _type.getValue() + "|" + _cost + "|" + getDescription();
    }

    public DeliveryMethodType getType()
    {
        return _type;
    }
}