package com.zitego.customer.pricing;

/**
 * A class for calculating volume based products.
 *
 * @author John Glorioso
 * @version $Id: VolumeBased.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class VolumeBased extends Pricing
{
    private VolumePricingConfig _volumeConfig;

    /**
     * Creates a new volume based pricing object with a volume config.
     *
     * @param cfg The volume configuration.
     * @throws IllegalArgumentException if the volume config is null.
     */
    public VolumeBased(VolumePricingConfig cfg)
    {
        if (cfg == null) throw new IllegalArgumentException("VolumePricingConfig cannot be null");
        _volumeConfig = cfg;
    }

    /**
     * Returns a price given a quantity.
     *
     * @return float
     * @throws IllegalArgumentException if the quantity is not valid.
     */
    public float getPrice(Object[] args)
    {
        //Need to have a quantity
        int qty = -1;
        Object tmp = (args != null && args.length >= 1 ? (Object)args[0] : null);
        if (tmp != null)
        {
            if (tmp instanceof Integer)
            {
                qty = ( (Integer)tmp ).intValue();
            }
            else if (tmp instanceof String)
            {
                try
                {
                    qty = Integer.parseInt( (String)tmp );
                }
                catch (NumberFormatException nfe) { }
            }
        }

        if (qty == -1)
        {
            throw new IllegalArgumentException("Invalid quantity passed in: "+tmp);
        }

        return _volumeConfig.getPrice(qty);
    }

    /**
     * Returns the volume config.
     *
     * @return VolumePricingConfig
     */
    public VolumePricingConfig getConfig()
    {
        return _volumeConfig;
    }

    public PricingType getType()
    {
        return PricingType.VOLUME_BASED;
    }
}