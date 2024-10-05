/*
 * Class: ConfHelpers.java
 */

// package:
package ibs.service.conf;

// imports:
import ibs.bo.Buttons;

import java.util.StringTokenizer;


/******************************************************************************
 * This class contains some helper methods used throughout configuration. <BR/>
 *
 * @version     $Id: ConfHelpers.java,v 1.3 2009/09/14 14:53:24 bbuchegger Exp $
 *
 * @author      Klaus Reimüller (KR), 20060622
 ******************************************************************************
 */
public abstract class ConfHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfHelpers.java,v 1.3 2009/09/14 14:53:24 bbuchegger Exp $";



    /**************************************************************************
     * Parse a configuration value. <BR/>
     * A configuration value may contain literals and variables. This method
     * parses a configuration value and splits it into its several literal and
     * variable parts. <BR/>
     * The result has the form <CODE>{literal,fieldname,literal,...}</CODE>. <BR/>
     * <CODE>literal</CODE> is any String literal. <BR/>
     * <CODE>fieldname</CODE> is the name of a field within the actual object.
     * The value of the field is filled into the resulting string at runtime.
     * <BR/>
     *
     * @param   value   The value to be parsed
     *
     * @return  The tokenized value.
     *          Array with one empty value if the value was an empty String.
     *          <CODE>null</CODE> if the value was <CODE>null</CODE>.
     */
    public static String[] parseConfValue (String value)
    {
        String[] tokens = null;

        // check if a valid value was set:
        if (value != null)
        {
            if (value.length () > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer (value,
                    ConfConstants.CONF_VARIABLEDELIM, false);
                int i = 0;

                // check if the string starts with a delimiter:
                if (value.startsWith (ConfConstants.CONF_VARIABLEDELIM))
                {
                    // set the first literal as empty String:
                    tokens = new String[tokenizer.countTokens () + 1];
                    tokens[i++] = "";
                } // if
                else
                {
                    // create the tokens array:
                    tokens = new String[tokenizer.countTokens ()];
                } // else

                // loop through all tokens and add each of the to the string array:
                while (tokenizer.hasMoreTokens ())
                {
                    // get the next token:
                    tokens[i++] = tokenizer.nextToken ();
                } // while
            } // if
            else
            {
                tokens = new String [] {""};
            } // else
        } // if

        // return the result:
        return tokens;
    } // parseConfValue


    /**************************************************************************
     * Parse a comma-separated list of button names and get the button ids. <BR/>
     *
     * @param   buttonList  The button list to be parsed.
     *
     * @return  An array with button ids from the list.
     *          Empty list if buttonList is an empty string.
     *          <CODE>null</CODE> if no buttons were defined.
     */
    public static int[] parseButtonList (String buttonList)
    {
        int[] buttons = null;

        // check if a valid value was set:
        if (buttonList != null)
        {
            if (buttonList.length () > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer (buttonList,
                    ConfConstants.CONF_VALUESEP, false);
                int i = 0;

                // check if the string starts with a separator:
                if (buttonList.startsWith (ConfConstants.CONF_VALUESEP))
                {
                    // set the first literal as empty String:
                    buttons = new int[tokenizer.countTokens () + 1];
                    buttons[i++] = Buttons.BTN_NONE;
                } // if
                else
                {
                    // create the tokens array:
                    buttons = new int[tokenizer.countTokens ()];
                } // else

                // loop through all tokens and add each of the to the string array:
                while (tokenizer.hasMoreTokens ())
                {
                    // get the next token:
                    String token = tokenizer.nextToken ();

                    // parse the button string and assign the correct value:
                    if (token.equalsIgnoreCase ("EDIT"))
                    {
                        buttons[i++] = Buttons.BTN_EDIT;
                    } // if
                    else if (token.equalsIgnoreCase ("DELETE"))
                    {
                        buttons[i++] = Buttons.BTN_DELETE;
                    } // else if
                    else if (token.equalsIgnoreCase ("CUT"))
                    {
                        buttons[i++] = Buttons.BTN_CUT;
                    } // else if
                    else if (token.equalsIgnoreCase ("COPY"))
                    {
                        buttons[i++] = Buttons.BTN_COPY;
                    } // else if
                    else if (token.equalsIgnoreCase ("DISTRIBUTE"))
                    {
                        buttons[i++] = Buttons.BTN_DISTRIBUTE;
                    } // else if
                    else if (token.equalsIgnoreCase ("WORKFLOW"))
                    {
                        buttons[i++] = Buttons.BTN_STARTWORKFLOW;
                    } // else if
                    else if (token.equalsIgnoreCase ("FORWARD"))
                    {
                        buttons[i++] = Buttons.BTN_FORWARD;
                    } // else if
                    else if (token.equalsIgnoreCase ("CHECKOUT"))
                    {
                        buttons[i++] = Buttons.BTN_CHECKOUT;
                    } // else if
                    else if (token.equalsIgnoreCase ("CHECKIN"))
                    {
                        buttons[i++] = Buttons.BTN_CHECKIN;
                    } // else if
                    else if (token.equalsIgnoreCase ("WDCHECKOUT"))
                    {
                        buttons[i++] = Buttons.BTN_WEBDAVCHECKOUT;
                    } // else if
                    else if (token.equalsIgnoreCase ("WDCHECKIN"))
                    {
                        buttons[i++] = Buttons.BTN_WEBDAVCHECKIN;
                    } // else if
                    
                } // while
            } // if
            else
            {
                buttons = new int[0];
            } // else
        } // if

        // return the result:
        return buttons;
    } // parseButtonList

} // class ConfHelpers
