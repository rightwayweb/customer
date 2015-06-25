package com.zitego.customer.pricing;

/**
 * A simple class for sale priced products.
 *
 * @author John Glorioso
 * @version $Id: SalePrice.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class SalePrice extends Pricing
{
    private float _salePrice = 0f;
    
    /**
     * Creates a new sale price pricing object.
     *
     * @param sale The sale price.
     */
    public SalePrice(float sale)
    {
        _salePrice = sale;
    }

    public float getPrice(Object[] args)
    {
        return _salePrice;
    }
    
    public PricingType getType()
    {
        return PricingType.SALE_PRICE;
    }
}