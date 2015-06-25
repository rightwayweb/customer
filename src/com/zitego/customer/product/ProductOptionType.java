package com.zitego.customer.product;

import com.zitego.markup.html.tag.form.FormElement;
import com.zitego.markup.html.tag.form.Checkbox;
import com.zitego.markup.html.tag.form.Radio;
import com.zitego.markup.html.tag.form.Select;
import com.zitego.markup.html.tag.form.Text;
import com.zitego.markup.html.tag.form.TextArea;
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
 * This is a constant class that describes types of product options. These options
 * basically describe the web form elements that will be rendered.
 *
 * @author John Glorioso
 * @version $Id: ProductOptionType.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class ProductOptionType extends DatabaseConstant
{
    public static final ProductOptionType CHECKBOX = new ProductOptionType(1);
    public static final ProductOptionType RADIO = new ProductOptionType(2);
    public static final ProductOptionType DROPDOWN = new ProductOptionType(3);
    public static final ProductOptionType MULTI_SELECT = new ProductOptionType(4);
    public static final ProductOptionType TEXT_FIELD = new ProductOptionType(5);
    public static final ProductOptionType TEXT_AREA = new ProductOptionType(6);
    static
    {
        //Load the names and descriptions from the database.
        loadData();
    }
    private String _name;
    private static Vector _types;

    /**
     * Creates a new ProductOptionType given the id.
     *
     * @param id The id.
     */
    private ProductOptionType(int id)
    {
        super(id, null);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Sets the name of the subscription type.
     *
     * @param name The name.
     */
    private void setName(String name)
    {
        _name = name;
    }

    /**
     * Returns the name of the subscription type.
     *
     * @return String
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Returns a ProductOptionType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return ProductOptionType
     */
    public static ProductOptionType evaluate(int id)
    {
        return (ProductOptionType)evaluate(id, _types);
    }

    /**
     * Loads the id and descriptions for the constants. These can be loaded at any time.
     */
    public static void loadData()
    {
        StringBuffer sql = new StringBuffer()
            .append("SELECT option_type_id, name, description ")
            .append("FROM option_type ")
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
                ProductOptionType t = evaluate( rs.getInt("option_type_id") );
                if (t != null)
                {
                    t.setName( rs.getString("name") );
                    t.setDescription( rs.getString("description") );
                }
            }
        }
        catch (SQLException sqle)
        {
            throw new RuntimeException("Could not load product option types", sqle);
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
        return CHECKBOX;
    }

    /**
     * Creates and returns a form element given a parent and this type.
     *
     * @param parent The html element parent.
     * @return FormElement
     */
    public FormElement createFormElement(HtmlMarkupTag parent)
    {
        if (this == CHECKBOX) return new Checkbox(parent);
        else if (this == RADIO) return new Radio(parent);
        else if (this == DROPDOWN) return new Select(parent);
        else if (this == MULTI_SELECT)
        {
            //TO DO, change this to a swap box
            Select ret = new Select(parent);
            ret.setMultiple(true);
            return ret;
        }
        else if (this == TEXT_FIELD) return new Text(parent);
        else if (this == TEXT_AREA) return new TextArea(parent);
        else return null;
    }
}