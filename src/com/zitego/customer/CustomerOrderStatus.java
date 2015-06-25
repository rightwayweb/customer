package com.zitego.customer;

import java.util.Vector;
import com.zitego.util.StatusType;
import com.zitego.util.Constant;

/**
 * This constant class defines different status types.
 *
 * @author John Glorioso
 * @version $Id: CustomerOrderStatus.java,v 1.3 2010/08/23 02:46:33 jglorioso Exp $
 */
public class CustomerOrderStatus extends Constant
{
    private static int n = 3;
    public static final CustomerOrderStatus /* 1 */ COMPLETE = new CustomerOrderStatus(StatusType.ACTIVE.getValue(), "Complete");
    public static final CustomerOrderStatus /* 2 */ CANCELLED = new CustomerOrderStatus(StatusType.INACTIVE.getValue(), "Cancelled");
    public static final CustomerOrderStatus /* 3 */ PENDING = new CustomerOrderStatus(n++, "Pending");
    public static final CustomerOrderStatus /* 4 */ RECEIVED = new CustomerOrderStatus(n++, "Received");
    public static final CustomerOrderStatus /* 5 */ AUTHORIZATION_FAILED = new CustomerOrderStatus(n++, "Authorization Failed");
    public static final CustomerOrderStatus /* 6 */ AWAITING_SHIPMENT = new CustomerOrderStatus(n++, "Awaiting Shipment");
    public static final CustomerOrderStatus /* 7 */ BACK_ORDERED = new CustomerOrderStatus(n++, "Back Ordered");
    public static final CustomerOrderStatus /* 8 */ SHIPPED = new CustomerOrderStatus(n++, "Shipped");
    public static final CustomerOrderStatus /* 9 */ READY_FOR_PICKUP = new CustomerOrderStatus(n++, "Ready For Pickup");
    /** To keep track of each type. */
    private static Vector _types;

    /**
     * For extending. It does nothing.
     */
    protected CustomerOrderStatus() {}

    /**
     * Creates a new CustomerOrderStatus given the id and description.
     *
     * @param int The id.
     * @param String The description.
     */
    private CustomerOrderStatus(int id, String desc)
    {
        super(id, desc);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns an CustomerOrderStatus based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param int The constant id.
     * @return CustomerOrderStatus
     */
    public static CustomerOrderStatus evaluate(int id)
    {
        return (CustomerOrderStatus)Constant.evaluate(id, _types);
    }

    /**
     * Returns an CustomerOrderStatus based on the description passed in. If the description does not match one of
     * a constant, then we return null. If there are two constants with the same description, then
     * the first one is returned.
     *
     * @param String The constant description.
     * @return CustomerOrderStatus
     */
    public static CustomerOrderStatus evaluate(String desc)
    {
        return (CustomerOrderStatus)Constant.evaluate(desc, _types);
    }

    public Vector getTypes()
    {
        return _types;
    }
}
