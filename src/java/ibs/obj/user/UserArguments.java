/*
 * Class: UserArguments.java
 */

// package:
package ibs.obj.user;

// imports:


/******************************************************************************
 * Arguments for the user part of the intranet basis architecture. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: UserArguments.java,v 1.8 2009/07/24 13:19:32 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980617
 ******************************************************************************
 */
public abstract class UserArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserArguments.java,v 1.8 2009/07/24 13:19:32 kreimueller Exp $";


    // argument handling for url:
    /**
     * argumentname for the operation new. <BR/>
     */
    public static final String ARG_OPNEW       = "opNw";

    /**
     * argumentname for the operation view. <BR/>
     */
    public static final String ARG_OPVIEW       = "opVw";

    /**
     * argumentname for the operation read. <BR/>
     */
    public static final String ARG_OPREAD       = "opRd";

    /**
     * argumentname for the operation change. <BR/>
     */
    public static final String ARG_OPCHANGE       = "opChn";

    /**
     * argumentname for the operation delete. <BR/>
     */
    public static final String ARG_OPDELETE       = "opDel";

    /**
     * argumentname for the operation set rights. <BR/>
     */
    public static final String ARG_OPSETRIGHTS    = "opSR";

    /**
     * argumentname for the operation create link. <BR/>
     */
    public static final String ARG_OPCREATELINK   = "opLnk";

    /**
     * argumentname for the operation addElement to a Container. <BR/>
     */
    public static final String ARG_OPADDELEM       = "opAddE";

    /**
     * argumentname for the operation delElement from a Container. <BR/>
     */
    public static final String ARG_OPDELELEM       = "opDelE";

    /**
     * argumentname for the operation view Elements of a Container. <BR/>
     */
    public static final String ARG_OPVIEWELEMS       = "opVwE";

    /**
     * prefix for the arguments of the operations
     */
    public static final String ARG_OPPREFIX       = "OP";

    /**
     * The oid of the Object for which the Rights are to be set.
     */
    public static final String ARG_ROID       = "roid";

    /**
     * Argument to select groups
     */
    public static final String ARG_SELGROUPS       = "selgrp";

    /**
     * Argument to delete groups
     */
    public static final String ARG_DELGROUPS       = "delgrp";

    /**
     * Argument for the upper group
     */
    public static final String ARG_GROUPID       = "grpid";

    /**
     * Argument - active or not?
     */
    public static final String ARG_ACTIVE       = "act";

    /**
     * Argument for the fullname of a user
     */
    public static final String ARG_FULLNAME       = "fulln";

    /**
     * Argument for the password-check
     */
    public static final String ARG_CHECKPASSWORD  = "chp";

    /**
     * Argument for change password
     */
    public static final String ARG_CHANGEPASSWORD  = "changepwd";

    /**
     * Argument for the linked Person
     */
    public static final String ARG_LINKED  = "lkd";

    /**
     * The last name of a person. <BR/>
     */
    public static final String ARG_LNAME = "lastName";

    /**
     * The first name of a person. <BR/>
     */
    public static final String ARG_FNAME = "firstName";

    /**
     * rightalias 'READ'. <BR/>
     */
    public static final String ARG_RAREAD = "rar";

    /**
     * rightalias 'WRITE'. <BR/>
     */
    public static final String ARG_RAWRITE = "raw";

    /**
     * rightalias 'ADMIN'. <BR/>
     */
    public static final String ARG_RAADMIN = "raa";

    /**
     * rightaliases
     */
    public static final String [] ARG_RIGHTALIASES =
    {
        UserArguments.ARG_RAREAD,
        UserArguments.ARG_RAWRITE,
        UserArguments.ARG_RAADMIN,
    }; // ARG_RIGHTALIASES

    /**
     * argument for checkbox (nochange) in changelist-right form . <BR/>
     */
    public static final String ARG_NOCHANGE = "nch";

    /**
     * argument for hidden field wich contents the rights of one right-object. <BR/>
     */
    public static final String ARG_RIGHTS = "rght";

    /**
     * argument for the emailaddress field. <BR/>
     */
    public static final String ARG_NOTIFICATION_EMAILADRESS = "email";

    /**
     * argument for the smsemailaddress field. <BR/>
     */
    public static final String ARG_NOTIFICATION_SMSADRESS = "smsemail";

} // class UserArguments
