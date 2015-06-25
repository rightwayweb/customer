package com.zitego.customer.delivery;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.customer.CustomerOrderItem;
import com.zitego.util.DatabaseConstant;
import com.zitego.util.StatusType;
import com.zitego.sql.DBHandle;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 * This is a constant class that describes types of delivery methods.
 *
 * @author John Glorioso
 * @version $Id: DeliveryMethodType.java,v 1.2 2008/07/07 01:23:11 jglorioso Exp $
 */
public class DeliveryMethodType extends DatabaseConstant
{
    public static final DeliveryMethodType NOT_SHIPPED = new DeliveryMethodType(1, "Not Shipped");
    public static final DeliveryMethodType REALTIME_RATES = new DeliveryMethodType(2, "Realtime Shipping Rates");
    public static final DeliveryMethodType PRODUCT_BASED = new DeliveryMethodType(3, "Product Based Shipping");
    public static final DeliveryMethodType STORE_PICKUP = new DeliveryMethodType(4, "Store Pickup");
    public static final DeliveryMethodType CUSTOM = new DeliveryMethodType(5, "Custom");
    public static final DeliveryMethodType CHOSEN = new DeliveryMethodType(6, "Chosen");
    static
    {
        //Load the names and descriptions from the database.
        loadData();
    }
    private static Vector _types;

    /**
     * Creates a new DeliveryMethodType given the id.
     *
     * @param id The id.
     * @param desc The description.
     */
    private DeliveryMethodType(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a DeliveryMethodType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return DeliveryMethodType
     */
    public static DeliveryMethodType evaluate(int id)
    {
        return (DeliveryMethodType)evaluate(id, _types);
    }

    /**
     * No longer loads from the database. This is now hard-coded. Get over it. It as a stupid idea.
     */
    public static void loadData()
    {
        
    }

    public Vector getTypes()
    {
        return _types;
    }

    public static DatabaseConstant getInstance()
    {
        return NOT_SHIPPED;
    }
}