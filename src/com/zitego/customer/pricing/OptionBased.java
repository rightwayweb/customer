package com.zitego.customer.pricing;

import com.zitego.customer.product.ProductOption;

/**
 * A class for calculating option based products.
 *
 * @author John Glorioso
 * @version $Id: OptionBased.java,v 1.2 2011/10/16 17:20:34 jglorioso Exp $
 */
public class OptionBased extends Pricing
{
    /**
     * Creates a new option based pricing object.
     */
    public OptionBased() { }

    /**
     * Returns a price given an array of ProductOptions with specified
     * choices.
     *
     * @return float
     */
    public float getPrice(Object[] args)
    {
        //ProductOption[] options = (args != null && args.length > 0 ? (ProductOption[])args[0] : new ProductOption[0]);
        float total = 0;
        //for (int i=0; i<options.length; i++)
        //{
            //TO DO - finish this
        //}
        return total;
    }

    public PricingType getType()
    {
        return PricingType.OPTION_BASED;
    }
}