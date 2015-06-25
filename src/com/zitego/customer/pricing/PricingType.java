package com.zitego.customer.pricing;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.util.DatabaseConstant;
import com.zitego.util.StatusType;
import com.zitego.sql.DBHandle;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 * This is a constant class that describes types of pricing.
 *
 * @author John Glorioso
 * @version $Id: PricingType.java,v 1.2 2008/07/07 01:23:13 jglorioso Exp $
 */
public class PricingType extends DatabaseConstant
{
    public static final PricingType REGULAR_PRICE = new PricingType(1, "Regular Price");
    public static final PricingType SALE_PRICE = new PricingType(2, "Sale Price");
    public static final PricingType OPTION_BASED = new PricingType(3, "Option Based");
    public static final PricingType VOLUME_BASED = new PricingType(4, "Volume Based");
    static
    {
        //Load the names and descriptions from the database.
        loadData();
    }
    private static Vector _types;

    /**
     * Creates a new PricingType given the id.
     *
     * @param id The id.
     * @param desc The description.
     */
    private PricingType(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a PricingType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return PricingType
     */
    public static PricingType evaluate(int id)
    {
        return (PricingType)evaluate(id, _types);
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
        return REGULAR_PRICE;
    }
}