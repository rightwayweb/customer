package com.zitego.customer.pricing;

/**
 * A simple class for regular priced products.
 *
 * @author John Glorioso
 * @version $Id: RegularPrice.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class RegularPrice extends Pricing
{
    private float _regularPrice = 0f;
    private float _salePrice = 0f;

    /**
     * Creates a new regular price pricing object.
     *
     * @param reg The regular price.
     */
    public RegularPrice(float reg)
    {
        _regularPrice = reg;
    }

    /**
     * Creates a new regular price pricing object with a sale price.
     *
     * @param reg The regular price.
     * @param sale The sale price.
     */
    public RegularPrice(float reg, float sale)
    {
        this(reg);
        _salePrice = sale;
    }

    public float getPrice(Object[] args)
    {
        if (_salePrice > 0) return _salePrice;
        else return _regularPrice;
    }

    public PricingType getType()
    {
        return PricingType.REGULAR_PRICE;
    }
}