package com.zitego.customer.product;

import com.zitego.markup.html.tag.form.FormElement;
import com.zitego.markup.html.tag.form.Checkbox;
import com.zitego.markup.html.tag.form.Radio;
import com.zitego.markup.html.tag.form.Select;
import com.zitego.markup.html.tag.form.Text;
import com.zitego.markup.html.tag.HtmlMarkupTag;
import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.util.DatabaseConstant;
import com.zitego.util.StatusType;
import com.zitego.sql.DBHandle;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 * This is a constant class that describes types of discounts.
 *
 * @author John Glorioso
 * @version $Id: DiscountType.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class DiscountType extends DatabaseConstant
{
    public static final DiscountType CURRENCY = new DiscountType(1);
    public static final DiscountType PERCENT = new DiscountType(2);
    static
    {
        //Load the names and descriptions from the database.
        loadData();
    }
    private static Vector _types;

    /**
     * Creates a new DiscountType given the id.
     *
     * @param id The id.
     */
    private DiscountType(int id)
    {
        super(id, null);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a DiscountType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return DiscountType
     */
    public static DiscountType evaluate(int id)
    {
        return (DiscountType)evaluate(id, _types);
    }

    /**
     * Loads the id and descriptions for the constants. These can be loaded at any time.
     */
    public static void loadData()
    {
        StringBuffer sql = new StringBuffer()
            .append("SELECT discount_type_id, name ")
            .append("FROM discount_type ")
            .append("WHERE status = ?");
        DBHandle db = null;
        try
        {
            db = BaseConfigServlet.getWebappProperties().getDBHandle();
            db.connect();
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                DiscountType t = evaluate( rs.getInt("discount_type_id") );
                if (t != null)
                {
                    t.setDescription( rs.getString("name") );
                }
            }
        }
        catch (SQLException sqle)
        {
            throw new RuntimeException("Could not load discount types", sqle);
        }
        finally
        {
            if (db != null) try { db.disconnect(); } catch (SQLException sqle2) { }
        }
    }

    public Vector getTypes()
    {
        return _types;
    }

    public static DatabaseConstant getInstance()
    {
        return CURRENCY;
    }
}