package com.zitego.customer.delivery;

/**
 * A factory for creating delivery types. There is only one static method and it takes
 * a DeliveryType and a config string as the arguments.
 *
 * @author John Glorioso
 * @version $Id: DeliveryMethodFactory.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class DeliveryMethodFactory
{
    /**
     * Returns a delivery method given the type and the config string.
     *
     * @param type The delivery method type.
     * @param config The config string of the delivery method.
     */
    public static DeliveryMethod getDeliveryMethod(DeliveryMethodType type, String config)
    {
        if (type == DeliveryMethodType.NOT_SHIPPED)
        {
            return new NotShipped();
        }
        else if (type == DeliveryMethodType.REALTIME_RATES)
        {
            return new RealtimeRates(config);
        }
        else if (type == DeliveryMethodType.PRODUCT_BASED)
        {
            return new ProductBased(config);
        }
        else if (type == DeliveryMethodType.STORE_PICKUP)
        {
            return new StorePickup(config);
        }
        else if (type == DeliveryMethodType.CUSTOM)
        {
            return new CustomDeliveryMethod(config);
        }
        else
        {
            return null;
        }
    }
}