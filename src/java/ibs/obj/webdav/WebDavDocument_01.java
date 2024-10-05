/**
 * Class: WebDavDocument_01.java
 */

// package:
package ibs.obj.webdav;

// imports:
import ibs.bo.Buttons;
import ibs.di.XMLViewer_01;


/******************************************************************************
 * WebDavDocument_01 handels the Document Form with WebDAV support. <BR/>
 *
 * @version     $Id: WebDavDocument_01.java,v 1.4 2008/07/21 14:25:14 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW), 20020911
 ******************************************************************************
 */
public class WebDavDocument_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: WebDavDocument_01.java,v 1.4 2008/07/21 14:25:14 kreimueller Exp $";


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define the buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_STARTWORKFLOW,
            Buttons.BTN_FORWARD,
            Buttons.BTN_FINISHWORKFLOW,
            Buttons.BTN_WEBDAVCHECKOUT,
            Buttons.BTN_WEBDAVCHECKIN,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // WevDavDocument_01
