/*
 * Class: RightDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.di.DIConstants;
//KR TODO: unsauber
import ibs.obj.user.UserConstants;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The RightDataElement hold the information of an VALUES section from the
 * XML import file. <BR/>
 *
 * @version     $Id: RightDataElement.java,v 1.9 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class RightDataElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightDataElement.java,v 1.9 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     *  name attribute of an RIGHT element. <BR/>
     */
    public String name;

    /**
     *  type attribute of an RIGHT element. <BR/>
     */
    public String type;

    /**
     *  Vector that holds the profile attributes of an RIGHT element. <BR/>
     */
    public Vector<String> profiles;


    /**************************************************************************
     * Creates an RightDataElement. <BR/>
     */
    public RightDataElement ()
    {
        // call constructor of super class ObjectReference:
        this.name = null;
        this.type = null;
        this.profiles = new Vector<String> ();
    } // RightDataElement


    /**************************************************************************
     * Creates an RightDataElement. <BR/>
     *
     * @param name      name of the RIGHT element
     * @param type      type of the RIGHT element
     * @param profile   profile of the RIGHT element
     */
    public RightDataElement (String name, String type, String profile)
    {
        // call constructor of super class ObjectReference:
        this.name = name;
        this.type = type;
        this.profiles = new Vector<String> ();
        this.profiles.addElement (profile);
    } // RightDataElement


    /**************************************************************************
     * Checks is this RightElement as a certain name and Type. <BR/>
     *
     * @param name      name of the RIGHT element
     * @param type      type of the RIGHT element
     *
     * @return  true if name and type is the same as set in the RightElement or
     *          false otherwise.
     */
    public boolean isEqual (String name, String type)
    {
        return this.name.equals (name) && this.type.equals (type);
    } // isEqual


    /**************************************************************************
     * Adds an profile to the profiles vector. <BR/>
     *
     * @param profile   profile of the RIGHT element
     */
    public void addProfile (String profile)
    {
        // call constructor of super class ObjectReference:
        this.profiles.addElement (profile);
    } // addProfile


    /**************************************************************************
     * Checks if type of right is set to user. otherwise is is set for a group.
     * <BR/>
     *
     * @return true if type is set to user or false otherwise
     */
    public boolean isUser ()
    {
        return this.type.equalsIgnoreCase (DIConstants.RIGHT_ISUSER);
    } // isUser


    /**************************************************************************
     * Checks if type of right is set to user. otherwise is is set for a group.
     * <BR/>
     *
     * @return true if type is set to user or false otherwise
     */
    public int getRights ()
    {
        int rights = 0;
        String profile;

        if (this.profiles != null)
        {
            for (Iterator<String> iter = this.profiles.iterator (); iter.hasNext ();)
            {
                profile = iter.next ();

                // cumulate the rights
                if (profile.equals (DIConstants.PROFILE_READ))
                {
                    rights = rights | UserConstants.RA_READ;
                } // if
                else if (profile.equals (DIConstants.PROFILE_WRITE))
                {
                    rights = rights | UserConstants.RA_WRITE;
                } // else if
                else if (profile.equals (DIConstants.PROFILE_ADMIN))
                {
                    rights = rights | UserConstants.RA_ADMIN;
                } // else if
            } // for iter
        } // if (profiles != null)
        return rights;
    } // getRights

} // class RightDataElement
