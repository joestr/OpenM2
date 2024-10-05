/*
 * Class: LayoutElement.java
 */

// package:
package ibs.io;

// imports:


/******************************************************************************
 * This is the LayoutElement Object, which holds the Informations necessary
 * for a LayoutElement of the application
 *
 * @version     $Id: LayoutElement.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $
 *
 * @author      Keim Christine (CK) 981216
 ******************************************************************************
 */
public class LayoutElement extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LayoutElement.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $";


    /**
     * The name of the file where the stylesheets for the LayoutElement is stored. <BR/>
     */
    public String styleSheet = "";

    /**
     * The path where the images to this LayoutElement can be found. <BR/>
     */
    public String images = "";

    /**
     * The name of the file where the Javascriptcode for this LayoutElement is stored. <BR/>
     */
    public String javascript = "";


    /**************************************************************************
     * Create a new instance of a LayoutElement. <BR/>
     */
    public LayoutElement ()
    {
        // nothing to do
    } // LayoutElement

} // class LayoutElement
