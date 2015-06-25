package com.zitego.customer.creditCard;

/**
 * The user id associated with a customer. This id contains a user identifier
 * (usually a pin or username) and an optional password.
 *
 * @author John Glorioso
 * @version $Id: UserId.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class UserId
{
    private String _id;
    private String _pass;

    /**
     * Creates a new empty user id.
     */
    public UserId() { }

    /**
     * Creates a new user id with an id.
     *
     * @param id The id.
     * @throws IllegalArgumentException if the id is null.
     */
    public UserId(String id) throws IllegalArgumentException
    {
        setId(id);
    }

    /**
     * Creates a new user id with an id and password.
     *
     * @param id The id.
     * @param pass The password.
     * @throws IllegalArgumentException if the id is null.
     */
    public UserId(String id, String pass) throws IllegalArgumentException
    {
        setId(id);
        setPassword(pass);
    }

    /**
     * Sets the user id.
     *
     * @param id The id.
     * @throws IllegalArgumentException if the id is null.
     */
    public void setId(String id) throws IllegalArgumentException
    {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        _id = id;
    }

    /**
     * Returns the id.
     *
     * @return String
     */
    public String getId()
    {
        return _id;
    }

    /**
     * Sets the password.
     *
     * @param pass The password.
     */
    public void setPassword(String pass)
    {
        _pass = pass;
    }

    /**
     * Returns the password.
     *
     * @return String
     */
    public String getPassword()
    {
        return _pass;
    }
}