package com.zitego.customer.pricing;

/**
 * A factory for creating pricings. There is only one static method and it takes
 * a PricingType, the regular price, the sale price, and the volume pricing
 * configuration. Depending on what type of pricing it is, it will return a
 * price given an array of Objects as the arguments.
 *
 * @author John Glorioso
 * @version $Id: PricingFactory.java,v 1.2 2008/03/12 18:31:31 jglorioso Exp $
 */
public class PricingFactory
{
    /**
     * Returns a pricing object given the type and the config string.
     *
     * @param type The delivery method type.
     * @param config The config string of the delivery method.
     */
    public static Pricing createPricing(PricingType type, float reg, float sale, VolumePricingConfig vol)
    {
        if (type == PricingType.REGULAR_PRICE)
        {
            return new RegularPrice(reg, sale);
        }
        else if (type == PricingType.SALE_PRICE)
        {
            return new SalePrice(sale);
        }
        else if (type == PricingType.VOLUME_BASED)
        {
            return new VolumeBased(vol);
        }
        else if (type == PricingType.OPTION_BASED)
        {
            return new OptionBased();
        }
        else
        {
            return null;
        }
    }
}