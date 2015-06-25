package com.zitego.customer.product;

import com.zitego.util.InformationEntity;

/**
 * This is an empty shell to be used when you just need an implementation of order item.
 *
 * @author John Glorioso
 * @version $Id: OrderItemShell.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class OrderItemShell extends InformationEntity implements OrderItem
{
    /** The description. */
    private String _description;
    /** The cost. */
    private float _cost = 0;
    /** The quantity. */
    private int _quantity = 0;
    
    /**
     * Creates a OrderItemShell with all properties.
     *
     * @param desc The description.
     * @param cost The cost.
     * @param quantity The quantity.
     */
    public OrderItemShell(String desc, float cost, int quantity)
    {
        super();
        _description = desc;
        _cost = cost;
        _quantity = quantity;
    }
    
    /**
     * Sets the description.
     *
     * @param description The description.
     */
    public void setDescription(String description)
    {
        _description = description;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the cost.
     *
     * @param cost The cost.
     */
    public void setCost(float cost)
    {
        _cost = cost;
    }

    /**
     * Returns the cost.
     *
     * @return float
     */
    public float getCost()
    {
        return _cost;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity The quantity.
     */
    public void setQuantity(int quantity)
    {
        _quantity = quantity;
    }

    /**
     * Returns the quantity.
     *
     * @return int
     */
    public int getQuantity()
    {
        return _quantity;
    }
}