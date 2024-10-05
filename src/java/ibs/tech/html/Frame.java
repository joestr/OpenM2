/*
 * Class: Frame.java
 */

// package:
package ibs.tech.html;

// imports:
import ibs.tech.html.Element;


/******************************************************************************
 * This is the abstract Frame Object.
 *
 * @version     $Id: Frame.java,v 1.5 2007/07/20 12:59:26 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public abstract class Frame extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Frame.java,v 1.5 2007/07/20 12:59:26 kreimueller Exp $";


    /**
     * Border of a Frame
     * default : false (no frame)
     */
    public boolean frameborder;

} // class Frame
