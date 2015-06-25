package com.zitego.customer.product;

import com.zitego.customer.Customer;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.DBHandleFactory;
import com.zitego.sql.NoDataException;
import com.zitego.util.StatusType;
import com.zitego.util.TextUtils;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This class is a worker to load and save products for a particular customer. It
 * is constructed with a customer id and a database handle. At that point, you can
 * call getProducts to access the products this customer is subscribed to. You can
 * optionally construct the object with a DateRange in order to view the products
 * for a particular time frame.
 *
 * @author John Glorioso
 * @version $Id: ProductManager.java,v 1.3 2009/05/04 03:36:46 jglorioso Exp $
 */
public class ProductManager
{
    /**
     * Returns a product given the universal identifier and a db handle.
     *
     * @param db The database handle to use for querying.
     * @param id The universal identifier.
     * @return Product
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no product with the given id.
     */
    public static Product getProduct(DBHandle db, String id) throws SQLException, NoDataException
    {
        Product[] prods = getProducts(db, new String[] {id});
        if (prods.length > 0) return prods[0];
        else return null;
    }

    /**
     * Returns an array of products given an array of universal identifiers and a db handle. If the products
     * are found, they will be returned in an array in the same order in which they were passed in.
     *
     * @param db The database handle to use for querying.
     * @param id The universal identifier.
     * @return Product
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no product with the given id.
     * @deprecated Use getProducts(DBConfig, String[]) instead.
     */
    public static Product[] getProducts(DBHandle db, String[] ids) throws SQLException, NoDataException
    {
        return getProducts(db.getConfig(), ids);
    }

    /**
     * Returns an array of products given an array of universal identifiers and a db config. If the products
     * are found, they will be returned in an array in the same order in which they were passed in.
     *
     * @param config The database config to use for querying.
     * @param id The universal identifier.
     * @return Product
     * @throws SQLException if a database error occurs.
     * @throws NoDataException if there is no product with the given id.
     */
    public static Product[] getProducts(DBConfig config, String[] ids) throws SQLException, NoDataException
    {
        Product[] ret = new Product[0];
        if (ids == null || ids.length == 0) return ret;
        StringBuffer sql = new StringBuffer()
            .append("SELECT product_id, reference_string, name, description, ")
            .append(       "price, status_id, creation_date, last_updated ")
            .append("FROM product ")
            .append("WHERE reference_string IN ('").append( TextUtils.join(ids, "','") ).append("')");

        DBHandle db = DBHandleFactory.getDBHandle(config);
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            Hashtable tmp = new Hashtable();
            while( rs.next() )
            {
                Product p = new Product(rs.getLong("product_id"), config);
                p.loadFromResultSet(rs);
                tmp.put(p.getReferenceString(), p);
            }
            ret = new Product[tmp.size()];
            int count = 0;
            for (int i=0; i<ids.length; i++)
            {
                Product p = (Product)tmp.get(ids[i]);
                if (p != null) ret[count++] = p;
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }
}