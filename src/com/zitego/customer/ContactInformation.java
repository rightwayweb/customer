package com.zitego.customer;

import com.zitego.util.InformationEntity;
import com.zitego.sql.DBHandle;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class represents customer contact information such as name, address, phone
 * numbers, email, etc.
 *
 * @author John Glorioso
 * @version $Id: ContactInformation.java,v 1.4 2012/03/13 04:53:30 jglorioso Exp $
 */
public class ContactInformation extends InformationEntity
{
    private String _prefix;
    private String _firstName;
    private String _lastName;
    private String _suffix;
    private String _displayName;
    private String _companyName;
    private String _address1;
    private String _address2;
    private String _city;
    private int _stateId;
    private String _stateName;
    private String _stateAbbr;
    private String _postalCode;
    private int _countryId;
    private String _countryName;
    private String _countryAbbr;
    private String _primaryPhone;
    private String _secondaryPhone;
    private String _fax;
    private String _mobile;
    private String _secondaryMobile;
    private String _email;

    /**
     * Creates a new ContactInformation.
     */
    public ContactInformation() { }

    /**
     * Sets the address1.
     *
     * @param address1 The address1.
     */
    public void setAddress1(String address1)
    {
        _address1 = address1;
    }

    /**
     * Returns the address1.
     *
     * @return String
     */
    public String getAddress1()
    {
        return _address1;
    }

    /**
     * Sets the address2.
     *
     * @param address2 The address2.
     */
    public void setAddress2(String address2)
    {
        _address2 = address2;
    }

    /**
     * Returns the address2.
     *
     * @return String
     */
    public String getAddress2()
    {
        return _address2;
    }

    /**
     * Sets the city.
     *
     * @param city The city.
     */
    public void setCity(String city)
    {
        _city = city;
    }

    /**
     * Returns the city.
     *
     * @return String
     */
    public String getCity()
    {
        return _city;
    }

    /**
     * Sets the companyName.
     *
     * @param companyName The companyName.
     */
    public void setCompanyName(String companyName)
    {
        _companyName = companyName;
    }

    /**
     * Returns the companyName.
     *
     * @return String
     */
    public String getCompanyName()
    {
        return _companyName;
    }

    /**
     * Sets the displayName.
     *
     * @param displayName The displayName.
     */
    public void setDisplayName(String displayName)
    {
        _displayName = displayName;
    }

    /**
     * Returns the displayName.
     *
     * @return String
     */
    public String getDisplayName()
    {
        return _displayName;
    }

    /**
     * Sets the firstName.
     *
     * @param firstName The firstName.
     */
    public void setFirstName(String firstName)
    {
        _firstName = firstName;
    }

    /**
     * Returns the firstName.
     *
     * @return String
     */
    public String getFirstName()
    {
        return _firstName;
    }

    /**
     * Sets the lastName.
     *
     * @param lastName The lastName.
     */
    public void setLastName(String lastName)
    {
        _lastName = lastName;
    }

    /**
     * Returns the lastName.
     *
     * @return String
     */
    public String getLastName()
    {
        return _lastName;
    }

    /**
     * Returns the full name as <prefix> <first> <last> <suffix>.
     *
     * @return String
     */
    public String getFullName()
    {
        if (_firstName != null && _lastName != null)
        {
            return new StringBuffer()
                .append( (_prefix != null ? _prefix+" " : "") )
                .append(_firstName).append(" ")
                .append(_lastName)
                .append( (_suffix != null ? " "+_suffix : "") ).toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the postalCode.
     *
     * @param postalCode The postalCode.
     */
    public void setPostalCode(String postalCode)
    {
        _postalCode = postalCode;
    }

    /**
     * Returns the postalCode.
     *
     * @return String
     */
    public String getPostalCode()
    {
        return _postalCode;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix The prefix.
     */
    public void setPrefix(String prefix)
    {
        _prefix = prefix;
    }

    /**
     * Returns the prefix.
     *
     * @return String
     */
    public String getPrefix()
    {
        return _prefix;
    }

    /**
     * Sets the stateId.
     *
     * @param stateId The stateId.
     */
    public void setStateId(int stateId)
    {
        _stateId = stateId;
    }

    /**
     * Returns the stateId.
     *
     * @return int
     */
    public int getStateId()
    {
        return _stateId;
    }

    /**
     * Sets the stateName.
     *
     * @param stateName The stateName.
     */
    public void setStateName(String stateName)
    {
        _stateName = stateName;
    }

    /**
     * Returns the stateName.
     *
     * @return String
     */
    public String getStateName()
    {
        return _stateName;
    }

    /**
     * Sets the state abbreviation.
     *
     * @param abbr The state abbreviation.
     */
    public void setStateAbbreviation(String abbr)
    {
        _stateAbbr = abbr;
    }

    /**
     * Returns the state abbreviation.
     *
     * @return String
     */
    public String getStateAbbreviation()
    {
        return _stateAbbr;
    }

    /**
     * Sets the countryId.
     *
     * @param countryId The countryId.
     */
    public void setCountryId(int countryId)
    {
        _countryId = countryId;
    }

    /**
     * Returns the countryId.
     *
     * @return int
     */
    public int getCountryId()
    {
        return _countryId;
    }

    /**
     * Sets the countryName.
     *
     * @param countryName The countryName.
     */
    public void setCountryName(String countryName)
    {
        _countryName = countryName;
    }

    /**
     * Returns the countryName.
     *
     * @return String
     */
    public String getCountryName()
    {
        return _countryName;
    }

    /**
     * Sets the country abbreviation.
     *
     * @param abbr The country abbreviation.
     */
    public void setCountryAbbreviation(String abbr)
    {
        _countryAbbr = abbr;
    }

    /**
     * Returns the country abbreviation.
     *
     * @return String
     */
    public String getCountryAbbreviation()
    {
        return _countryAbbr;
    }

    /**
     * Sets the suffix.
     *
     * @param suffix The suffix.
     */
    public void setSuffix(String suffix)
    {
        _suffix = suffix;
    }

    /**
     * Returns the suffix.
     *
     * @return String
     */
    public String getSuffix()
    {
        return _suffix;
    }

    /**
     * Sets the email.
     *
     * @param email The email.
     */
    public void setEmail(String email)
    {
        _email = email;
    }

    /**
     * Returns the email.
     *
     * @return String
     */
    public String getEmail()
    {
        return _email;
    }

    /**
     * Sets the primaryPhone.
     *
     * @param primaryPhone The primaryPhone.
     */
    public void setPrimaryPhone(String primaryPhone)
    {
        _primaryPhone = primaryPhone;
    }

    /**
     * Returns the primaryPhone.
     *
     * @return String
     */
    public String getPrimaryPhone()
    {
        return _primaryPhone;
    }

    /**
     * Sets the secondaryPhone.
     *
     * @param secondaryPhone The secondaryPhone.
     */
    public void setSecondaryPhone(String secondaryPhone)
    {
        _secondaryPhone = secondaryPhone;
    }

    /**
     * Returns the secondaryPhone.
     *
     * @return String
     */
    public String getSecondaryPhone()
    {
        return _secondaryPhone;
    }

    /**
     * Sets the fax.
     *
     * @param fax The fax.
     */
    public void setFax(String fax)
    {
        _fax = fax;
    }

    /**
     * Returns the fax.
     *
     * @return String
     */
    public String getFax()
    {
        return _fax;
    }

    /**
     * Sets the mobile.
     *
     * @param mobile The mobile.
     */
    public void setMobile(String mobile)
    {
        _mobile = mobile;
    }

    /**
     * Returns the mobile.
     *
     * @return String
     */
    public String getMobile()
    {
        return _mobile;
    }

    /**
     * Sets the secondary mobile.
     *
     * @param mobile The secondary mobile.
     */
    public void setSecondaryMobile(String mobile)
    {
        _secondaryMobile = mobile;
    }

    /**
     * Returns the secondaryMobile.
     *
     * @return String
     */
    public String getSecondaryMobile()
    {
        return _secondaryMobile;
    }

    /**
     * Sets the country name in the given contact info from the id.
     *
     * @param id The country id.
     * @param db The DBHandle.
     * @throws SQLException if an error occurs.
     */
    public static void setCountryName(ContactInformation info, DBHandle db) throws SQLException
    {
        String ret = null;
        try
        {
            db.connect();
            PreparedStatement pst = db.prepareStatement("SELECT name FROM country WHERE country_id = ?");
            pst.setInt( 1, info.getCountryId() );
            ResultSet rs = pst.executeQuery();
            if ( rs.next() ) info.setCountryName( rs.getString(1) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the state name and abbreviation in the given contact info from the id.
     *
     * @param id The state id.
     * @param db The DBHandle.
     * @throws SQLException if an error occurs.
     */
    public static void setStateName(ContactInformation info, DBHandle db) throws SQLException
    {
        String ret = null;
        try
        {
            db.connect();
            PreparedStatement pst = db.prepareStatement("SELECT name, abbr FROM state WHERE state_id = ?");
            pst.setInt( 1, info.getStateId() );
            ResultSet rs = pst.executeQuery();
            if ( rs.next() )
            {
                info.setStateName( rs.getString(1) );
                info.setStateAbbreviation( rs.getString(2) );
            }
        }
        finally
        {
            db.disconnect();
        }
    }
}