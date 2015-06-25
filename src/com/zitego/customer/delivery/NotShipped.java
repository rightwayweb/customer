package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;

/**
 * A simple class for NotShipped.
 *
 * @author John Glorioso
 * @version $Id: NotShipped.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class NotShipped extends DeliveryMethod
{
    /**
     * Creates a new not shipped delivery method.
     */
    public NotShipped() { }

    public float getShippingCharges(Product p)
    {
        return 0f;
    }

    public DeliveryMethodType getType()
    {
        return DeliveryMethodType.NOT_SHIPPED;
    }

    public String getConfig()
    {
        return null;
    }
}