/*
 * Class: HtmlConstants.java
 */

// package:
package ibs.io;

// imports:


/******************************************************************************
 * This class contains constants used somewhere for HTML output. <BR/>
 *
 * @version     $Id: HtmlConstants.java,v 1.3 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Klaus, 12.10.2003
 ******************************************************************************
 */
public abstract class HtmlConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HtmlConstants.java,v 1.3 2007/07/31 19:13:56 kreimueller Exp $";


    // frame names:
    /**
     * Name used for opening a new window. <BR/>
     */
    public static final String FRM_NEW              = new String ("_new");
    /**
     * Name used for opening a new window. <BR/>
     */
    public static final String FRM_BLANK              = new String ("_blank");

    /**
     * Name used for addressing the topmost frame. <BR/>
     */
    public static final String FRM_TOP              = new String ("_top");

    /**
     * Name used for logo frame. <BR/>
     */
    public static final String FRM_LOGO             = new String ("logo");

    /**
     * Name used for buttons frame. <BR/>
     */
    public static final String FRM_BUTTONS          = new String ("buttons");

    /**
     * Name used for frame with sheet tabs. <BR/>
     */
    public static final String FRM_SHEETTABS        = new String ("sheettabs");

    /**
     * Name used for main sheet frame. <BR/>
     */
    public static final String FRM_SHEET            = new String ("sheet");

    /**
     * Name used for upper sheet frame. <BR/>
     */
    public static final String FRM_SHEET1           = new String ("sheet1");

    /**
     * Name used for lower sheet frame. <BR/>
     */
    public static final String FRM_SHEET2           = new String ("sheet2");

    /**
     * Name used for sheet navigation frame. <BR/>
     */
    public static final String FRM_SHEETNAVIGATION  = new String ("sheetnavigation");

    /**
     * Name used for navigation frame. <BR/>
     */
    public static final String FRM_NAVIGATION       = new String ("navigation");

    /**
     * Name used for frame with navigation tabs. <BR/>
     */
    public static final String FRM_NAVIGATIONTABS   = new String ("navigationtabs");

    /**
     * Name used for frame with temporary content. <BR/>
     */
    public static final String FRM_TEMP             = new String ("temp");

    /**
     * Name used for upload frame. <BR/>
     */
    public static final String FRM_UPLOAD           = new String ("upload");

    /**
     * Name used for frame with document content. <BR/>
     */
    public static final String FRM_DOCUMENT         = new String ("document");

    /**
     * Tag attribute value: full width. <BR/>
     * Can be used wherever a width attribute shall have a value designated to
     * the full page width.
     */
    public static final String TAV_FULLWIDTH            =  new String ("100%");


    // javascript references for forms:
    /**
     * Javascript reference for sheet form. <BR/>
     * This reference already includes the "." at the end.
     */
    public static final String JREF_SHEETFORM = "document.sheetForm.";
    /**
     * Javascript reference for target in sheet form. <BR/>
     * This reference already includes the "=" at the end.
     */
    public static final String JREF_SHEETFORMTARGET =
        HtmlConstants.JREF_SHEETFORM + "target=";
    /**
     * Javascript reference for submit in sheet form. <BR/>
     * This reference already includes the complete method call with "();" at
     * the end.
     */
    public static final String JREF_SHEETFORMSUBMIT =
        HtmlConstants.JREF_SHEETFORM + "submit ();";
    /**
     * Javascript reference for assigning a value to a form field. <BR/>
     * This reference already includes the "=" at the end.
     */
    public static final String JREF_VALUEASSIGN = ".value = ";


    // form validation functions:
    /**
     * Form validation function: isText. <BR/>
     * Contains opening parenthesis.
     */
    public  static final String FV_ISTEXT = "top.iTx (";
    /**
     * Form validation function: isDate. <BR/>
     * Contains opening parenthesis.
     */
    public static final String FV_ISDATE = "top.iD (";
    /**
     * Form validation function: isTime. <BR/>
     * Contains opening parenthesis.
     */
    public static final String FV_ISTIME = "top.iT (";
    /**
     * Form validation function: isDateTimeAfter. <BR/>
     * Contains opening parenthesis.
     */
    public static final String FV_ISDATETIMEAFTER = "top.iDTA (";
    /**
     * Form validation function: isDateTimeBefore. <BR/>
     * Contains opening parenthesis.
     */
    public static final String FV_ISDATETIMEBEFORE = "top.iDTB (";

} // class HtmlConstants
