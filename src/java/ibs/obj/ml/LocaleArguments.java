/*
 * Class: LocaleArguments.java
 */

// package:
package ibs.obj.ml;

// imports:


/******************************************************************************
 * Arguments for the locale. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: LocaleArguments.java,v 1.1 2010/03/23 12:55:02 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann 20100322
 ******************************************************************************
 */
public abstract class LocaleArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LocaleArguments.java,v 1.1 2010/03/23 12:55:02 btatzmann Exp $";


    // argument handling for url:
    /**
     * argument for the language field. <BR/>
     */
    public static final String ARG_LANGUAGE = "language";

    /**
     * argument for the country field. <BR/>
     */
    public static final String ARG_COUNTRY = "country";
    
    /**
     * argument for the isDefault field. <BR/>
     */
    public static final String ARG_ISDEFAULT = "isdefault";
} // class LocaleArguments
