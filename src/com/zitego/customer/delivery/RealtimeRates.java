package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;

/**
 * A class for obtaining realtime rates for a product.
 *
 * @author John Glorioso
 * @version $Id: RealtimeRates.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class RealtimeRates extends DeliveryMethod
{
    /**
     * Creates a new real time rates delivery method with config information.
     *
     * @param config The properties of the delivery method.
     */
    public RealtimeRates(String config)
    {
        //TO DO - Finish this
    }

    public float getShippingCharges(Product p)
    {
        return 0f;
    }

    public DeliveryMethodType getType()
    {
        return DeliveryMethodType.REALTIME_RATES;
    }

    public String getConfig()
    {
        return null;
    }
}