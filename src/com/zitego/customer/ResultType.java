package com.zitego.customer;

import com.zitego.web.servlet.BaseConfigServlet;
import com.zitego.util.DatabaseConstant;
import com.zitego.util.StatusType;
import com.zitego.sql.DBHandle;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 * This is a constant class that describes types of processing results.
 *
 * @author John Glorioso
 * @version $Id: ResultType.java,v 1.2 2008/07/07 01:23:08 jglorioso Exp $
 */
public class ResultType extends DatabaseConstant
{
    public static final ResultType APPROVED = new ResultType(1, "Approved");
    public static final ResultType DECLINED = new ResultType(2, "Declined");
    public static final ResultType VOIDED = new ResultType(3, "Voided");
    public static final ResultType PENDING = new ResultType(4, "Pending");
    static
    {
        //Load the descriptions from the database.
        loadData();
    }
    private static Vector _types;

    /**
     * Creates a new ResultType given the id.
     *
     * @param id The id.
     * @param desc The description.
     */
    private ResultType(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a ResultType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return ResultType
     */
    public static ResultType evaluate(int id)
    {
        return (ResultType)evaluate(id, _types);
    }

    /**
     * Returns a ResultType based on the description passed in. If it does not match the description of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param desc The constant description.
     * @return ResultType
     */
    public static ResultType evaluate(String desc)
    {
        return (ResultType)evaluate(desc, _types);
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
        return APPROVED;
    }
}