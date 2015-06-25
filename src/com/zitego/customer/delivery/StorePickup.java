package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;

/**
 * A simple class for StorePickup.
 *
 * @author John Glorioso
 * @version $Id: StorePickup.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class StorePickup extends NotShipped
{
    /**
     * Creates a new store pickup.
     *
     * @param config The properties of the delivery method.
     */
    public StorePickup(String config) { }

    public DeliveryMethodType getType()
    {
        return DeliveryMethodType.STORE_PICKUP;
    }
}
