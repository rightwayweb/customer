package com.zitego.customer.pricing;

/**
 * A pricing. This class is abstract. getPrice(Object[])
 * must be defined by extending classes.
 *
 * @author John Glorioso
 * @version $Id: Pricing.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public abstract class Pricing
{
    /**
     * Returns a price given an array of objects as arguments.
     *
     * @return float
     * @throws IllegalArgumentException if required arguments are missing.
     */
    public abstract float getPrice(Object[] args);
    
    /**
     * Returns the type of pricing.
     *
     * @return PricingType
     */
    public abstract PricingType getType();
}