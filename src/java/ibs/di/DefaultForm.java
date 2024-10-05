/*
 * Class: DefaultForm.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.Buttons;


/******************************************************************************
 * Default class for XMLForms. <BR/>
 * Enables the configuration of the buttons of objects in a configuration file.
 * This file must reside in the directory m2/app/fromcfg, the file name must
 * match the typecode of the object.
 *
 * @version     $Id: DefaultForm.java,v 1.11 2008/09/17 16:44:27 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)
 *
 * @deprecated  KR 20060622 This class is not longer necessary. The
 *              functionality for setting info buttons (and content buttons)
 *              and the transformation is now part of the document template
 *              definition (<CODE>CONFIG</CODE> section).
 ******************************************************************************
 */
public class DefaultForm extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DefaultForm.java,v 1.11 2008/09/17 16:44:27 kreimueller Exp $";


    /**
     * List of visible buttons in the info view. <BR/>
     */
    private String p_buttonList = null;

    /**
     * The name of the transformation file wich should be used for
     * transformation in an workflow. <BR/>
     */
    private String p_transformFile = "";


    /**************************************************************************
     * Creates new DefaultForm. <BR/>
     */
    public DefaultForm ()
    {
        // nothing to do
    } // DefaultForm


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
        this.loadConfigFile ();

        if (this.p_buttonList != null)  // are there buttons to set in the
                                        // cfg-file ?
        {
            int[] buttons = new int[9];

            if (this.p_buttonList.indexOf ("EDIT") >= 0)
            {
                buttons[0] = Buttons.BTN_EDIT;
            } // if

            if (this.p_buttonList.indexOf ("DELETE") >= 0)
            {
                buttons[1] = Buttons.BTN_DELETE;
            } // if

            if (this.p_buttonList.indexOf ("CUT") >= 0)
            {
                buttons[2] = Buttons.BTN_CUT;
            } // if

            if (this.p_buttonList.indexOf ("COPY") >= 0)
            {
                buttons[3] = Buttons.BTN_COPY;
            } // if

            if (this.p_buttonList.indexOf ("DISTRIBUTE") >= 0)
            {
                buttons[4] = Buttons.BTN_DISTRIBUTE;
            } // if

            if (this.p_buttonList.indexOf ("WORKFLOW") >= 0)
            {
                buttons[5] = Buttons.BTN_STARTWORKFLOW;
            } // if

            if (this.p_buttonList.indexOf ("CHECKOUT") >= 0)
            {
                buttons[6] = Buttons.BTN_CHECKOUT;
            } // if

            if (this.p_buttonList.indexOf ("CHECKIN") >= 0)
            {
                buttons[7] = Buttons.BTN_CHECKIN;
            } // if

            if (this.p_buttonList.indexOf ("FORWARD") >= 0)
            {
                buttons[8] = Buttons.BTN_FORWARD;
            } // if

            return buttons;
        } // if are there buttons to set in the cfg-file

        // there was no entry in the cfg-file
        // -> use the standard buttons:
        return super.setInfoButtons ();
    } // setInfoButtons


    /**************************************************************************
     * Returns the file name of the transformation file (xsl). <BR/>
     * Overwrite this method to select the correct stylesheet file.
     * The file must be located in the TRANSFORM_XSLT_PATH directory.
     *
     * @return      the file name of the xsl file for transformations
     */
    public String getTransformationFileName ()
    {
        this.loadConfigFile ();

        return this.p_transformFile;
    } // getTransformationFileName

    /**************************************************************************
     * Parse a line from the form configuration file. <BR/>
     *
     * @param   line    The line to be parsed.
     *
     * @return  <CODE>true</CODE> if the line was successfully parsed,
     *          <CODE>false</CODE> if the line could not be parsed or is not
     *          valid.
     */
    protected boolean parseConfigLine (String line)
    {
        boolean retVal = false;

        // check if the line could already been parsed
        if (!super.parseConfigLine (line))
        {
            if (line.startsWith ("Buttons="))
            {
                this.p_buttonList = line.substring (8).toUpperCase ();
            } // if (line.startsWith ("Buttons=")

            // search the Buttons line
        } // if (!super.parseConfigLine (line))

        // return the result:
        return retVal;
    } // parseConfigLine


} // DefaultForm
