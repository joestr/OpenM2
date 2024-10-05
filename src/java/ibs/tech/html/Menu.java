/*
 * Class: Menu.java
 */

// package:
package ibs.tech.html;

// imports:
import ibs.tech.html.Element;


/******************************************************************************
 * This is the abstract Menu Object.
 *
 * @version     $Id: Menu.java,v 1.5 2007/07/20 12:59:27 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public abstract class Menu extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Menu.java,v 1.5 2007/07/20 12:59:27 kreimueller Exp $";


    /**************************************************************************
     * Is the Menu a MenuItem?
     *
     * @return info about menu
     */
    public abstract boolean isItem ();


    /**************************************************************************
     * Changes the status of the menu
     *
     * @param   pId     ?????
     *
     * @return success
     */
    public abstract boolean changeStatus (String pId);

} // class Menu
