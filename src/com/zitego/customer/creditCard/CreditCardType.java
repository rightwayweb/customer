package com.zitego.customer.creditCard;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.util.DatabaseConstant;
import com.zitego.util.Constant;
import com.zitego.util.StatusType;
import com.zitego.util.StaticProperties;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBHandleFactory;
import com.zitego.sql.DBConfig;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * This constant class defines different credit card types. In addition, a DBConfig
 * must exist in either BaseConfigServlet.getWebappProperties() or StaticProperties
 * and be called "db.customer.config".
 *
 * @author John Glorioso
 * @version $Id: CreditCardType.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public final class CreditCardType extends DatabaseConstant
{
    //This is getting changed to a regular constant as there is nothing important stored in the database.
    //Look into having it extend just Constant later, but getInstance may be used somewhere.
    public static final CreditCardType VISA = new CreditCardType(1, "Visa");
    public static final CreditCardType MASTERCARD = new CreditCardType(2, "Mastercard");
    public static final CreditCardType DISCOVER = new CreditCardType(3, "Discover");
    public static final CreditCardType AMEX = new CreditCardType(4, "American Express");
    private static Vector _types;

    /**
     * Creates a new CreditCardType given the id.
     *
     * @param id The id.
     * @param desc The description.
     */
    private CreditCardType(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns an CreditCardType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return CreditCardType
     */
    public static CreditCardType evaluate(int id)
    {
        return (CreditCardType)Constant.evaluate(id, _types);
    }

    /**
     * Returns an CreditCardType based on the name passed in. If the name does not match the name of
     * a constant, then we return null.
     *
     * @param name The credit card name.
     * @return CreditCardType
     */
    public static CreditCardType evaluate(String name)
    {
        return (CreditCardType)Constant.evaluate(name, _types);
    }

    /**
     * No longer does anything as this does not load from a database anymore
     */
    public static void loadData()
    {

    }

    public static DatabaseConstant getInstance()
    {
        return VISA;
    }

    public Vector getTypes()
    {
        return _types;
    }
}
