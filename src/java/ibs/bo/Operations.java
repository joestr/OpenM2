/*
 * Class: Operations.java
 */

// package:
package ibs.bo;

// imports:


/******************************************************************************
 * Constants for operations within intranet business solutions. <BR/>
 *
 * @version     $Id: Operations.java,v 1.13 2011/11/21 12:39:54 gweiss Exp $
 *
 * @author      Klaus Reimüller (KR), 980427
 ******************************************************************************
 */
public abstract class Operations extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Operations.java,v 1.13 2011/11/21 12:39:54 gweiss Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = BOTokens.TOK_BUNDLE;

    // base operations that may be performed on business objects:
    /**
     * No operation. <BR/>
     */
    public static final int OP_NONE              =   0x00000000;
    /**
     * Create a new object. <BR/>
     */
    public static final int OP_NEW              =   0x00000001;
    /**
     * View the object within a container. <BR/>
     */
    public static final int OP_VIEW             =   0x00000002;
    /**
     * Read the data of an object. <BR/>
     */
    public static final int OP_READ             =   0x00000004;
    /**
     * Change an object's state. <BR/>
     */
    public static final int OP_CHANGESTATE      =   Operations.OP_READ;
    /**
     * Edit an object's properties. <BR/>
     */
    public static final int OP_EDIT             =   0x00000008;
    /**
     * Change an object's properties. <BR/>
     */
    public static final int OP_CHANGE           =   Operations.OP_EDIT;
    /**
     * Delete an object. <BR/>
     */
    public static final int OP_DELETE           =   0x00000010;
    /**
     * Log in. <BR/>
     */
    public static final int OP_LOGIN            =   0x00000020;
    /**
     * View rights of the object. <BR/>
     */
    public static final int OP_VIEWRIGHTS       =   0x00000100;
    /**
     * Edit the rights on an object. <BR/>
     */
    public static final int OP_EDITRIGHTS       =   0x00000200;
    /**
     * Set the rights on an object. <BR/>
     */
    public static final int OP_SETRIGHTS        =   Operations.OP_EDITRIGHTS;
    /**
     * Create a link to an object. <BR/>
     */
    public static final int OP_CREATELINK       =   0x00001000;
    /**
     * Distribute the object. <BR/>
     */
    public static final int OP_DISTRIBUTE       =   0x00002000;

    // operations that may be performed on containers:
    /**
     * Add an element to a container. <BR/>
     */
    public static final int OP_ADDELEM          =   0x00100000;
    /**
     * Delete an element from a container. <BR/>
     */
    public static final int OP_DELELEM          =   0x00200000;
    /**
     * Maximum operation id. <BR/>
     */
    public static final int OP_MAX              =   0x40000000;
    /**
     * Make a copy of a other BO. <BR/>
     */
    public static final int OP_COPY             =   Operations.OP_READ;
    /**
     * Move a BO to another Container. <BR/>
     */
    public static final int OP_MOVE             =   Operations.OP_CHANGE;

    /**
     * View the elements of the Container. <BR/>
     */
    public static final int OP_VIEWELEMS        =   0x00400000;


    /**
     * View the elements of the protocolContainer. <BR/>
     */
    public static final int OP_VIEWPROTOCOL     =   0x01000000;

    /**
     * Change the processState. <BR/>
     */
    public static final int OP_CHANGEPROCSTATE  =   0x02000000;

    /**
     * Forward the object. <BR/>
     */
    public static final int OP_FORWARD          =   0x04000000;

    /**
     * All rights. <BR/>
     */
    public static final int OP_ALL              =   0x7FFFFFFF;


    /**
     * Name for right to create a new object. <BR/>
     */
    public static String ML_OPN_NEW                = "ML_OPN_NEW";
    /**
     * Name for right to view the object within a container. <BR/>
     */
    public static String ML_OPN_VIEW               = "ML_OPN_VIEW";
    /**
     * Name for right to read the data of an object. <BR/>
     */
    public static String ML_OPN_READ               = "ML_OPN_READ";
    /**
     * Name for right to change an object's properties. <BR/>
     */
    public static String ML_OPN_CHANGE             = "ML_OPN_CHANGE";
    /**
     * Name for right to delete an object. <BR/>
     */
    public static String ML_OPN_DELETE             = "ML_OPN_DELETE";
    /**
     * Name for right to log in. <BR/>
     */
    public static String ML_OPN_LOGIN              = "ML_OPN_LOGIN";
    /**
     * Name for right to view the rights of an object. <BR/>
     */
    public static String ML_OPN_VIEWRIGHTS         = "ML_OPN_VIEWRIGHTS";
    /**
     * Name for right to set the rights on an object. <BR/>
     */
    public static String ML_OPN_SETRIGHTS          = "ML_OPN_SETRIGHTS";
    /**
     * Name for right to create a link to an object. <BR/>
     */
    public static String ML_OPN_CREATELINK         = "ML_OPN_CREATELINK";
    /**
     * Name for right to distribute an object. <BR/>
     */
    public static String ML_OPN_DISTRIBUTE         = "ML_OPN_DISTRIBUTE";
    /**
     * Name for right to add an element to a container. <BR/>
     */
    public static String ML_OPN_ADDELEM            = "ML_OPN_ADDELEM";
    /**
     * Name for right to delete an element from a container. <BR/>
     */
    public static String ML_OPN_DELELEM            = "ML_OPN_DELELEM";
    /**
     * Name for right to view the elements of a container. <BR/>
     */
    public static String ML_OPN_VIEWELEMS          = "ML_OPN_VIEWELEMS";
    /**
     * Name for right to view the ProtocolContainer. <BR/>
     */
    public static String ML_OPN_VIEWPROTOCOL       = "ML_OPN_VIEWPROTOCOL";


    /**
     * Short name for right to create a new object. <BR/>
     */
    public static String ML_OPNS_NEW               = "ML_OPNS_NEW";
    /**
     * Short name for right to view the object within a container. <BR/>
     */
    public static String ML_OPNS_VIEW              = "ML_OPNS_VIEW";
    /**
     * Short name for right to read the data of an object. <BR/>
     */
    public static String ML_OPNS_READ              = "ML_OPNS_READ";
    /**
     * Short name for right to change an object's properties. <BR/>
     */
    public static String ML_OPNS_CHANGE            = "ML_OPNS_CHANGE";
    /**
     * Short name for right to delete an object. <BR/>
     */
    public static String ML_OPNS_DELETE            = "ML_OPNS_DELETE";
    /**
     * Short name for right to log in. <BR/>
     */
    public static String ML_OPNS_LOGIN             = "ML_OPNS_LOGIN";
    /**
     * Short name for right to view the rights of an object. <BR/>
     */
    public static String ML_OPNS_VIEWRIGHTS        = "ML_OPNS_VIEWRIGHTS";
    /**
     * Short name for right to set the rights on an object. <BR/>
     */
    public static String ML_OPNS_SETRIGHTS         = "ML_OPNS_SETRIGHTS";
    /**
     * Short name for right to create a link to an object. <BR/>
     */
    public static String ML_OPNS_CREATELINK        = "ML_OPNS_CREATELINK";
    /**
     * Short name for right to distribute an object. <BR/>
     */
    public static String ML_OPNS_DISTRIBUTE        = "ML_OPNS_DISTRIBUTE";
    /**
     * Short name for right to add an element to a container. <BR/>
     */
    public static String ML_OPNS_ADDELEM           = "ML_OPNS_ADDELEM";
    /**
     * Short name for right to delete an element from a container. <BR/>
     */
    public static String ML_OPNS_DELELEM           = "ML_OPNS_DELELEM";
    /**
     * Short name for right to view the elements of a container. <BR/>
     */
    public static String ML_OPNS_VIEWELEMS         = "ML_OPNS_VIEWELEMS";
    /**
     * Short name for right to view the ProtocolContainer. <BR/>
     */
    public static String ML_OPNS_VIEWPROTOCOL      = "ML_OPNS_VIEWPROTOCOL";


    /**
     * Array of ids of the defined rights.
     */
    public static final int[] OP_IDS =
    {
        // rightalias 'READ'
        Operations.OP_VIEW,
        Operations.OP_READ,
        Operations.OP_CREATELINK,
        Operations.OP_DISTRIBUTE,
        Operations.OP_VIEWELEMS,
        // rightalias 'WRITE'
        Operations.OP_CHANGE,
        Operations.OP_DELETE,
        Operations.OP_NEW,
        Operations.OP_ADDELEM,
        Operations.OP_DELELEM,
        // rightalias 'ADMIN'
        Operations.OP_VIEWRIGHTS,
        Operations.OP_SETRIGHTS,
        Operations.OP_VIEWPROTOCOL,
    }; // OP_IDS

    /**
     * Array of ShortNames of the Rights. <BR/>
     */
    public static final String[] OP_SHORTNAMES =
    {
        // rightalias 'READ'
        Operations.ML_OPNS_VIEW,
        Operations.ML_OPNS_READ,
        Operations.ML_OPNS_CREATELINK,
        Operations.ML_OPNS_DISTRIBUTE,
        Operations.ML_OPNS_VIEWELEMS,
        // rightalias 'WRITE'
        Operations.ML_OPNS_CHANGE,
        Operations.ML_OPNS_DELETE,
        Operations.ML_OPNS_NEW,
        Operations.ML_OPNS_ADDELEM,
        Operations.ML_OPNS_DELELEM,
        // rightalias 'ADMIN'
        Operations.ML_OPNS_VIEWRIGHTS,
        Operations.ML_OPNS_SETRIGHTS,
        Operations.ML_OPNS_VIEWPROTOCOL,
    }; // OP_SHORTNAMES

    /**
     * Array of Names of the Rights. <BR/>
     */
    public static final String[] OP_NAMES =
    {
        // rightalias 'READ'
        Operations.ML_OPN_VIEW,
        Operations.ML_OPN_READ,
        Operations.ML_OPN_CREATELINK,
        Operations.ML_OPN_DISTRIBUTE,
        Operations.ML_OPN_VIEWELEMS,
        // rightalias 'WRITE'
        Operations.ML_OPN_CHANGE,
        Operations.ML_OPN_DELETE,
        Operations.ML_OPN_NEW,
        Operations.ML_OPN_ADDELEM,
        Operations.ML_OPN_DELELEM,
        // rightalias 'ADMIN'
        Operations.ML_OPN_VIEWRIGHTS,
        Operations.ML_OPN_SETRIGHTS,
        Operations.ML_OPN_VIEWPROTOCOL,
    }; // OP_NAMES

//    /**************************************************************************
//     * Set the dependent properties. <BR/>
//     * Properties from this and other files (initialized Arrays)
//     * that are build of the attributes of this class.
//     */
//    
//    commented out by gw
//    reason: empty function
//    
//    public static void setDependentProperties ()
//    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage
/*

        // names of rights:
        // rightalias 'READ'
        Operations.OP_NAMES[0] = Operations.OPN_VIEW;
        Operations.OP_NAMES[1] = Operations.OPN_READ;
        Operations.OP_NAMES[2] = Operations.OPN_CREATELINK;
        Operations.OP_NAMES[3] = Operations.OPN_DISTRIBUTE;
        Operations.OP_NAMES[4] = Operations.OPN_VIEWELEMS;
        // rightalias 'WRITE'
        Operations.OP_NAMES[5] = Operations.OPN_CHANGE;
        Operations.OP_NAMES[6] = Operations.OPN_DELETE;
        Operations.OP_NAMES[7] = Operations.OPN_NEW;
        Operations.OP_NAMES[8] = Operations.OPN_ADDELEM;
        Operations.OP_NAMES[9] = Operations.OPN_DELELEM;
        // rightalias 'ADMIN'
        Operations.OP_NAMES[10] = Operations.OPN_VIEWRIGHTS;
        Operations.OP_NAMES[11] = Operations.OPN_SETRIGHTS;
        Operations.OP_NAMES[12] = Operations.OPN_VIEWPROTOCOL;

        // short names of rights:
        // rightalias 'READ'
        Operations.OP_SHORTNAMES[0] = Operations.OPNS_VIEW;
        Operations.OP_SHORTNAMES[1] = Operations.OPNS_READ;
        Operations.OP_SHORTNAMES[2] = Operations.OPNS_CREATELINK;
        Operations.OP_SHORTNAMES[3] = Operations.OPNS_DISTRIBUTE;
        Operations.OP_SHORTNAMES[4] = Operations.OPNS_VIEWELEMS;
        // rightalias 'WRITE'
        Operations.OP_SHORTNAMES[5] = Operations.OPNS_CHANGE;
        Operations.OP_SHORTNAMES[6] = Operations.OPNS_DELETE;
        Operations.OP_SHORTNAMES[7] = Operations.OPNS_NEW;
        Operations.OP_SHORTNAMES[8] = Operations.OPNS_ADDELEM;
        Operations.OP_SHORTNAMES[9] = Operations.OPNS_DELELEM;
        // rightalias 'ADMIN'
        Operations.OP_SHORTNAMES[10] = Operations.OPNS_VIEWRIGHTS;
        Operations.OP_SHORTNAMES[11] = Operations.OPNS_SETRIGHTS;
        Operations.OP_SHORTNAMES[12] = Operations.OPNS_VIEWPROTOCOL;
*/
//    } // setDependentProperties

} // class Operations
