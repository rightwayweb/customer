package com.zitego.customer.product;

/**
 * This is an interface to be implemented by anything that can be charged to
 * a credit card. It only needs to define getDescription, getCost, and getQuantity.
 *
 * @author John Glorioso
 * @version $Id: OrderItem.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public interface OrderItem
{
    /**
     * Returns the description of this order item.
     *
     * @return String
     */
    public String getDescription();

    /**
     * Returns the cost of this order item.
     *
     * @return float
     */
    public float getCost();

    /**
     * Returns the quantity of this order item.
     *
     * @return int
     */
    public int getQuantity();
}