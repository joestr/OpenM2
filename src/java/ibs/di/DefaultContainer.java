/*
 * Class: DefaultContainer.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.bo.type.Type;
import ibs.di.XMLContainer_01;
import ibs.io.IOHelpers;
//KR TODO: unsauber
import ibs.service.user.User;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


/******************************************************************************
 * Default class for XMLContainers.
 * Enables the configuration of the buttons of container objects in a
 * configuration file. This file must reside in the directory
 * m2/app/fromcfg, the file name must match the typecode of the object.
 *
 * For example:
 *
 *      TypeCode                Configuration file
 *      ===========================================
 *      MyFormContainer         MyFormContainer.cfg
 *      ArticleContainer        ArticleContainer.cfg
 *
 *
 * The configuration file msut contain a line with the
 * button names witch should be shown.
 *
 * Example of an configuration file:
 *
 *
 *      ######################################################################
 *      #
 *      # Konfigurations-Datei für eine XML-Ablage.
 *      #
 *      # Wird über CLASS="ibs.di.DefaultContainer" in der Vorlage aktiviert.
 *      #
 *      # Mögliche Buttons in der Info-Ansicht (Buttons=):
 *      #
 *      # EDIT,DELETE,CUT,COPY,DISTRIBUTE,WORKFLOW,CHECKOUT,CHECKIN,FORWARD
 *      #
 *      # Mögliche Buttons in der Inhalts-Ansicht (ContentButtons=):
 *      #
 *      # NEW,DELETE,CUT,COPY,PASTE,PASTELINK,SEARCH,WORKFLOW,DISTRIBUTE,FORWARD
 *      #
 *      ######################################################################
 *
 *      #Folgende Buttons anzeigen
 *      Buttons=WORKFLOW,COPY
 *      ContentButtons=PASTE,WORKFLOW,COPY
 *
 * @version     $Id: DefaultContainer.java,v 1.12 2009/07/25 09:30:36 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */
public class DefaultContainer extends XMLContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DefaultContainer.java,v 1.12 2009/07/25 09:30:36 kreimueller Exp $";


    /**
     * Table with the button names for the info view. <BR/>
     */
    private static final String[] INFO_BUTTON_NAMES =
    {
        "EDIT",
        "DELETE",
        "CUT",
        "COPY",
        "DISTRIBUTE",
        "WORKFLOW",
        "CHECKOUT",
        "CHECKIN",
        "FORWARD",
    };

    /**
     * Table with the codes for the info view. <BR/>
     */
    private static final int[] INFO_BUTTON_CODES =
    {
        Buttons.BTN_EDIT,
        Buttons.BTN_DELETE,
        Buttons.BTN_CUT,
        Buttons.BTN_COPY,
        Buttons.BTN_DISTRIBUTE,
        Buttons.BTN_STARTWORKFLOW,
        Buttons.BTN_CHECKOUT,
        Buttons.BTN_CHECKIN,
        Buttons.BTN_FORWARD,
    };


    /**
     * Table with the button names for the content view. <BR/>
     */
    private static final String[] CONTENT_BUTTON_NAMES =
    {
        "NEW",
        "PASTE",
        "PASTELINK",
        "SEARCH",
        "DELETE",
        "COPY",
        "CUT",
        "DISTRIBUTE",
        "FORWARD",
        "WORKFLOW",
    };


    /**
     * Table with the codes for the content view. <BR/>
     */
    private static final int[] CONTENT_BUTTON_CODES =
    {
        Buttons.BTN_NEW,
        Buttons.BTN_PASTE,
        Buttons.BTN_REFERENCE,
        Buttons.BTN_SEARCH,
        Buttons.BTN_LISTDELETE,
        Buttons.BTN_LIST_COPY,
        Buttons.BTN_LIST_CUT,
        Buttons.BTN_DISTRIBUTE,
        Buttons.BTN_LISTFORWARD,
        Buttons.BTN_STARTWORKFLOW,
    };


    /**
     * List of visible buttons in the info view. <BR/>
     */
    private String p_buttonList = "";
    /**
     * List of visible buttons in the content view. <BR/>
     */
    private String p_contentButtonList = "";


    /**************************************************************************
     * This constructor creates a new instance of the class DefaultContainer.
     * <BR/>
     */
    public DefaultContainer ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // DefaultContainer


    /**************************************************************************
     * This constructor creates a new instance of the class DefaultContainer.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DefaultContainer (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // DefaultContainer


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

        if (this.p_buttonList != null)
                                        // are there buttons to set in the
                                        // cfg-file ?
        {
            return this.getVisibleButtons (DefaultContainer.INFO_BUTTON_NAMES,
                DefaultContainer.INFO_BUTTON_CODES, this.p_buttonList);
        } // if are there buttons to set in the cfg-file

        // there was no entry in the cfg-file
        // -> use the standard buttons
        return super.setInfoButtons ();
    } // setInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        this.loadConfigFile ();

        if (this.p_contentButtonList != null)
                                        // are there buttons to set in the
                                        // cfg-file ?
        {
            return this.getVisibleButtons (DefaultContainer.CONTENT_BUTTON_NAMES,
                DefaultContainer.CONTENT_BUTTON_CODES, this.p_contentButtonList);
        } // if are there buttons to set in the cfg-file

        // there was no entry in the cfg-file
        // -> use the standard buttons
        return super.setContentButtons ();
    } // setContentButtons


    /**************************************************************************
     * Load the configuration file. <BR/>
     */
    protected void loadConfigFile ()
    {
        try
        {
            // get the type info for the current object
            Type type = this.getTypeCache ().getType (this.oid.tVersionId);

            // set the button strings to null:
            this.p_buttonList = null;
            this.p_contentButtonList = null;

            // normally the type info must be found in any case
            if (type != null)
            {
                // the file name is the type code of the object
                StringBuffer cfgFilePath =
                    new StringBuffer (this.app.p_system.p_m2AbsBasePath)
                        .append ("app/formcfg/")
                        .append (type.getCode ())
                        .append (".cfg");
                File cfgFile = new File (cfgFilePath.toString ());
                // check if the file exists
                // if not there will be no error message be displayed
                if (cfgFile.exists () && cfgFile.isFile ())
                {
                    char[] buffer = new char[0x1000];
                    // open the config file
                    FileReader f = new FileReader (cfgFile);
                    // read in the configuration
                    int len = f.read (buffer);

                    // the tokenizer is used to get the file content line by line
                    StringTokenizer token = new StringTokenizer (new String (
                        buffer, 0, len), "\n");
                    while (token.hasMoreTokens ())
                    {
                        String line = token.nextToken ();

                        // search the Buttons line
                        if (line.startsWith ("Buttons="))
                        {
                            this.p_buttonList = line.substring (8)
                                .toUpperCase ();
                        } // if (line.startsWith ("Buttons=")
                        // search the Buttons line
                        else if (line.startsWith ("ContentButtons="))
                        {
                            this.p_contentButtonList = line.substring (15)
                                .toUpperCase ();
                        } // if (line.startsWith ("ContentButtons=")
                    } // while (token.hasMoreTokens ())
                    f.close ();
                } // cfg file does not exist
            } // if type found
        } // try
        catch (IOException e)
        {
            // should not occur
            // display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // loadConfigFile


    /**************************************************************************
     * Returns the codes of all visible buttons
     *
     * @param   names       Names of all buttons.
     * @param   codes       Names of all codes.
     * @param   buttonList  List of all buttons.
     *
     * @return  The visible buttons.
     */
    protected int[] getVisibleButtons (String[] names, int[] codes,
        String buttonList)
    {
        int[] buttons = new int[names.length];

        if (buttonList != null)         // should the buttons from the config
                                        // file be taken ?
        {
            // the tokenizer is used to get the single button names in the list
            // add a tailing ',' for the tokenizer. without this the last token
            // is not recognized.
            StringTokenizer token = new StringTokenizer (buttonList + ",", ",\n\r");
            while (token.hasMoreTokens ())
            {
                String button = token.nextToken ().trim ();

                for (int i = 0; i < names.length; i++)
                {
                    if (button.equals (names[i]))
                    {
                        buttons[i] = codes[i];
                        break;
                    } // if button found
                } // for i
            } // while (token.hasMoreTokens ())
        } // if should the buttons from the config file be taken
        else
        {
            // set the first element ot the button list to button none because
            // the standard buttons should be taken:
            buttons[0] = Buttons.BTN_NONE;
        } // else

        return buttons;
    } // getVisibleButtons

} // DefaultContainer
