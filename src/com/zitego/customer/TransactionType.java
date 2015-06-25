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
 * This is a constant class that describes types of transactions.
 *
 * @author John Glorioso
 * @version $Id: TransactionType.java,v 1.2 2008/07/07 01:23:08 jglorioso Exp $
 */
public class TransactionType extends DatabaseConstant
{
    public static final TransactionType CHARGE = new TransactionType(1, "Charge", "Authorize and Capture a transaction (charge)");
    public static final TransactionType PRE_AUTH = new TransactionType(2, "Pre-Authorization", "Authorize a transaction (pre-auth)");
    public static final TransactionType PROCESS_PRE_AUTH = new TransactionType(2, "Processed Pre-Authorization", "Capture an authorized transaction (process pre-auth)");
    public static final TransactionType VOID_PRE_AUTH = new TransactionType(3, "Voided Pre-Authorization", "Void a pre-auth");
    public static final TransactionType REFUND = new TransactionType(4, "Refund", "Refund a captured transaction");
    public static final TransactionType OFFLINE_TRANSACTION = new TransactionType(5, "Offline Transaction", "Offline Transaction");
    static
    {
        //Load the names and descriptions from the database.
        loadData();
    }
    private String _label;
    private static Vector _types;

    /**
     * Creates a new TransactionType given the id.
     *
     * @param id The id.
     * @param label The label for the transaction type.
     * @param desc The description.
     */
    private TransactionType(int id, String label, String desc)
    {
        super(id, desc);
        _label = label;
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns a TransactionType based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param id The constant id.
     * @return TransactionType
     */
    public static TransactionType evaluate(int id)
    {
        return (TransactionType)evaluate(id, _types);
    }

    /**
     * No longer loads from the database. This is now hard-coded. Get over it. It as a stupid idea.
     */
    public static void loadData()
    {
        
    }
    
    /**
     * Returns the transaction type label.
     *
     * @return String
     */
    public String getLabel()
    {
        return _label;
    }

    public Vector getTypes()
    {
        return _types;
    }

    public static DatabaseConstant getInstance()
    {
        return CHARGE;
    }
}