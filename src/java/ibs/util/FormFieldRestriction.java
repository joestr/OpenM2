/*
 * Class: FormFieldRestriction.java
 */

 // package:
package ibs.util;

// imports:
//KR TODO: unsauber
import ibs.BaseObject;
import ibs.bo.BOMessages;
import ibs.bo.Datatypes;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.ml.MultilingualTextProvider;

import java.util.StringTokenizer;


/******************************************************************************
 * With FormFieldRestriction it is possible to define restrictions for form
 * fields which will be translated to JavaScript code. <BR/>
 * Attention-Attention: The created JavaScript code contains top.* references
 * to the top frame of the browser, because all functions concerning type
 * or range checks are held there!.
 * You will often encounter the term 'compound datatypes'. Compound datatypes
 * will be displayed in two fields, e.g. Datatypes.DT_DATETIME,
 * Datatypes.DT_INTEGERRANGE.
 *
 * @version     $Id: FormFieldRestriction.java,v 1.21 2010/04/14 13:23:16 btatzmann Exp $
 *
 * @author          Horst Pichler (HP) 980508
 ******************************************************************************
 */
public class FormFieldRestriction extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormFieldRestriction.java,v 1.21 2010/04/14 13:23:16 btatzmann Exp $";


    /**
     * Name of the form field. <BR/>
     */
    public String name = null;

    /**
     * Datatype of form field. <BR/>
     * Note that following datatypes (compound) include two seperate form
     * fields.
     *      - Datatypes.DT_DATETIME
     *      - Datatypes.DT_INTEGERRANGE
     *      - Datatypes.DT_DATERANGE
     *      - Datatypes.DT_TIMERANGE
     * Due to this fact there will be some constraints on those
     * types (programmer begs for mercy).
     */
    public int dataType = Datatypes.DT_UNKNOWN;

    /**
     * Max. length of form fields content. <BR/>
     * Note that if no maxLength is given or it is 0 then
     * the default maxLength of the datatype will be used.
     */
    public int maxLength = 0;

    /**
     * Viewed length of form field itself. <BR/>
     * Note that if no viewLength is given or it is 0 then
     * the default viewLength of the datatype will be used.
     */
    public int viewLength = 0;

    /**
     * Form field allowed to be empty?. <BR/>
     * Note that if datatype is compound datatype this constraint
     * will affect both fields.
     */
    public boolean emptyAllowed = true;

    /**
     * Lower bound of form field given as String. <BR/>
     * Lower bound must be given as a string, independent
     * of the given data type.
     * e.g.: Datatypes.DT_INTEGER       "123"
     *       Datatypes.DT_DATE          "1.1.1992"
     *                                  "1.1.92"
     *                                  "01.01.92"
     *       Datatypes.DT_TIME          "12:30"
     *       Datatypes.DT_DATETIME      "1.1.90 10:15"
     * Note that if datatype is a compound datatype (e.g. range types)
     * this constraint will affect both fields
     */
    public String lowerBound = null;

    /**
     * Upper bound of form field given as String. <BR/>
     * Upper bound must be given as a string, independent
     * of the given data type.
     * e.g.: Datatypes.DT_INTEGER       "123"
     *       Datatypes.DT_DATE          "1.1.1992"
     *                                  "1.1.92"
     *                                  "01.01.92"
     *       Datatypes.DT_TIME          "12:30"
     *       Datatypes.DT_DATETIME      "1.1.90 10:15"
     * Note that if datatype is a compound datatype (e.g. range types)
     * this constraint will affect both fields
     */
    public String upperBound = null;


    /**************************************************************************
     * Creates a FormFieldRestriction object. <BR/>
     */
    public FormFieldRestriction ()
    {
        // nothing to do
    } // FormFieldRestriction


    /**************************************************************************
     * Creates a FormFieldRestriction object. <BR/>
     *
     * @param   emptyAllowed    Is the field allowed to be empty?
     */
    public FormFieldRestriction (boolean emptyAllowed)
    {
        this.emptyAllowed = emptyAllowed;
    } // FormFieldRestriction


    /**************************************************************************
     * Creates a FormFieldRestriction object. <BR/>
     *
     * @param   emptyAllowed    Is the field allowed to be empty?
     * @param   maxLength       Maximum field length.
     * @param   viewLength      Displayed field length.
     */
    public FormFieldRestriction (boolean emptyAllowed, int maxLength,
        int viewLength)
    {
        this.emptyAllowed = emptyAllowed;
        this.maxLength = maxLength;
        this.viewLength = viewLength;
    } // FormFieldRestriction


    /**************************************************************************
     * Creates a FormFieldRestriction object. <BR/>
     *
     * @param   emptyAllowed    Is the field allowed to be empty?
     * @param   maxLength       Maximum field length.
     * @param   viewLength      Displayed field length.
     * @param   lowerBound      Lower bound of field value.
     * @param   upperBound      Upper bound of field value.
     */
    public FormFieldRestriction (boolean emptyAllowed, int maxLength,
        int viewLength, String lowerBound, String upperBound)
    {
        this.emptyAllowed = emptyAllowed;
        this.maxLength = maxLength;
        this.viewLength = viewLength;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    } // FormFieldRestriction


    /**************************************************************************
     * Build JavaScript code which will check restrictions. <BR/>
     *
     * @param   env     The current environment
     * 
     * @return  created JavaScript code
     */
    public StringBuffer buildRestrictScriptCode (Environment env)
    {
        // does data type need check? - some don't!
        if (this.dataType == Datatypes.DT_UNKNOWN ||  // unknown data type
            this.dataType == Datatypes.DT_BOOL ||     // boolean -> check box
            this.dataType == Datatypes.DT_RADIO ||    // radio button
            this.dataType == Datatypes.DT_SELECT)     // selection box
        {
            return new StringBuffer ();
        } // if

        // created JavaScript code
        StringBuffer script = new StringBuffer ();

        // is empty allowed
        StringBuffer emptyAllowedScript;

        // check if field allowed to be empty - set script part
        if (this.emptyAllowed)
        {
            emptyAllowedScript = new StringBuffer ("true");
        } // if
        else
        {
            emptyAllowedScript = new StringBuffer ("false");
        } // else

/*
        // check for fields length
        // =< 0  no check needed/wanted
        //  > 0  check for given length
        if (this.maxLength > 0)
        {
            // add to script
            script += "if (top.isLengthLessOrEqual (" + HtmlConstants.JREF_SHEETFORM + this.name +
                ", " + this.maxLength + ")) {";
            // increase number of open brackets {
            numBrackets++;
        } // if
*/

        script.append (this.buildCommonRestrictScriptCode (emptyAllowedScript));

        // only lower bound
        if (this.lowerBound != null && this.upperBound == null)
        {
//             there is already a script code?
            if (script.length () > 0)
            {
                script.append (" && ");
            } // if (script.length() > 0)
            script.append (this.buildLowerRestrictScriptCode (emptyAllowedScript));
        } // if - only lower bound set

        // only upper bound
        if (this.upperBound != null && this.lowerBound == null)
        {
//             there is already a script code?
            if (script.length () > 0)
            {
                script.append (" && ");
            } // if (script.length() > 0)
            script.append (this.buildUpperRestrictScriptCode (emptyAllowedScript));
        } // if - only upper bound set

        // lower and upper bound
        if (this.upperBound != null && this.lowerBound != null)
        {
//             there is already a script code?
            if (script.length () > 0)
            {
                script.append (" && ");
            } // if (script.length() > 0)
            script.append (this.buildLowerUpperRestrictScriptCode (emptyAllowedScript, env));
        } // if - only lower bound set

        // maxLength
        if (this.maxLength > 0)
        {
            // differ fields data types
            switch (this.dataType)
            {
                case Datatypes.DT_DESCRIPTION:
                    // add script:
                    if (script.length () > 0) // there is already a script code?
                    {
                        // HACK: add the code which is necessary to concatenate
                        // the two partial scripts to a whole one:
                        script.append (" || !");
                    } // if
                    // add the new script itself:
                    script.append ("top.iLLE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                             ", " + this.maxLength + ")");
                    break;

                default:
                    // nothing to do
            } //switch - dataType
        } // if - maxLength


        // exit method
        return script;
    } // buildRestrictScriptCode


    /**************************************************************************
     * Build JavaScript code which will check common restrictions. <BR/>
     *
     * @param   emptyAllowedScript  Script for checking if empty value is
     *                              allowed.
     *
     * @return  created JavaScript code
     */
    public StringBuffer buildCommonRestrictScriptCode (
                                                       StringBuffer emptyAllowedScript)
    {
        // created JavaScript code
        StringBuffer script = new StringBuffer ();

        // differ fields data types
        switch (this.dataType)
        {
            case Datatypes.DT_INTEGER:
            case Datatypes.DT_SEARCHINTEGER:
                script.append ("top.iI (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_TEXT:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_MONEY:
            case Datatypes.DT_SEARCHMONEY:
                script.append ("top.iM (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_TEXTAREA:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATE:
                script.append (HtmlConstants.FV_ISDATE + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_TIME:
                script.append (HtmlConstants.FV_ISTIME + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATETIME:
                // ATTENTION: this type encloses two form fields.
                // one holds the date value, the other the time value.
                // the java script evaluation code for this type will be called
                // (onBlur) after leaving the time field.
                // the check if the content of the date field is a valid input
                // was already added (in ibsObject) - because of this: only the
                // time-valid-check is necessary here.
                // notice: the _t prefix was added because this is the time part
                // of the datetime field (s).
                script.append (HtmlConstants.FV_ISTIME + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_TIME + ", " +
                    emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHDATETIME:
                // ATTENTION: this type encloses two form fields.
                // one holds the date value, the other the time value.
                // the java script evaluation code for this type will be called
                // (onBlur) after leaving the time field.
                // the check if the content of the date field is a valid input
                // was already added (in ibsObject) - because of this: only the
                // time-valid-check is necessary here.
                // notice: the _t prefix was added because this is the time part
                // of the datetime field (s).
                script.append (HtmlConstants.FV_ISTIME + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_TIME + ", " +
                    emptyAllowedScript + ")");
                break;
            case Datatypes.DT_IMAGE:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_FILE:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_NAME:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_PASSWORD:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_USER:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DESCRIPTION:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHTEXT:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHTEXT_EXT:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHDATE:
                script.append (HtmlConstants.FV_ISDATE + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_LINK:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_EMAIL:
                script.append ("top.iEm (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_URL:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_NUMBER:
            case Datatypes.DT_SEARCHNUMBER:
                script.append ("top.iNu (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_HTMLTEXT:
                script.append ("top.iNE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHTEXTFUNCTION:
                script.append (HtmlConstants.FV_ISTEXT + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + emptyAllowedScript + ")");
                break;
            default:
                // unknown data type - do nothing
        } // switch - differ data types

        // exit method
        return script;
    } // buildCommonRestrictScriptCode


    /**************************************************************************
     * Build JavaScript code which will check lower bound restrictions. <BR/>
     *
     * @param   emptyAllowedScript  Script for checking if empty value is
     *                              allowed.
     *
     * @return  created JavaScript code
     */
    public StringBuffer buildLowerRestrictScriptCode (
                                                      StringBuffer emptyAllowedScript)
    {
        // created JavaScript code
        StringBuffer script = new StringBuffer ();

        // help variables for DATETIME and  SEARCHDATETIME
        String dateStr = "";
        String timeStr = "";
        StringTokenizer st = null;

        // differ data types where bounds are possible
        switch (this.dataType)
        {
            case Datatypes.DT_INTEGER:
                // add script
                script.append ("top.iIGE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.lowerBound + ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATE:
                // add script
                script.append ("top.iDA (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", '" + this.lowerBound + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_TIME:
                // add script
                script.append ("top.iTA (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", '" + this.lowerBound + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATETIME:
                // init date and time strings
                dateStr = "";
                timeStr = "";
                // split lower bound string in date and time part
                st = new StringTokenizer (this.lowerBound, " ");
                // get date part
                if (st.hasMoreTokens ())
                {
                    dateStr = st.nextToken ();
                } // if
                if (st.hasMoreTokens ())
                {
                    timeStr = st.nextToken ();
                } // if
                // add script
                script.append (HtmlConstants.FV_ISDATETIMEAFTER + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_DATE + ", '" +
                    dateStr + "', " + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_TIME + ", '" +
                    timeStr + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHDATETIME:
                // init date and time strings
                dateStr = "";
                timeStr = "";
                // split lower bound string in date and time part
                st = new StringTokenizer (this.lowerBound, " ");
                // get date part
                if (st.hasMoreTokens ())
                {
                    dateStr = st.nextToken ();
                } // if
                if (st.hasMoreTokens ())
                {
                    timeStr = st.nextToken ();
                } // if
                // add script
                script.append (HtmlConstants.FV_ISDATETIMEAFTER +
                    HtmlConstants.JREF_SHEETFORM + this.name +
                    FormFieldConstants.POSTFIX_DATE + ", '" + dateStr +
                    "', " + HtmlConstants.JREF_SHEETFORM + this.name +
                    FormFieldConstants.POSTFIX_TIME + ", '" + timeStr +
                    "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_MONEY:
                // add script
                script.append ("top.iMGE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.lowerBound + ", " + emptyAllowedScript + ")");
                break;
            default:
                // do nothing
        } // switch - dataType

        // exit method
        return script;
    } // buildLowerRestrictScriptCode


    /**************************************************************************
     * Build JavaScript code which will check upper bound restrictions. <BR/>
     *
     * @param   emptyAllowedScript  Script for checking if empty value is
     *                              allowed.
     *
     * @return  created JavaScript code
     */
    public StringBuffer buildUpperRestrictScriptCode (
                                                      StringBuffer emptyAllowedScript)
    {
        // created JavaScript code
        StringBuffer script = new StringBuffer ();

        // help variables for DATETIME and  SEARCHDATETIME
        String dateStr = "";
        String timeStr = "";
        StringTokenizer st = null;

        // differ data types where bounds are possible
        switch (this.dataType)
        {
            case Datatypes.DT_INTEGER:
                // add script
                script.append ("top.iILE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.upperBound + ", " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATE:
                // add script
                script.append ("top.iDB (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", '" + this.upperBound + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_TIME:
                // add script
                script.append ("top.iTB (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", '" + this.upperBound + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATETIME:
                // init date and time strings
                dateStr = "";
                timeStr = "";
                // split lower bound string in date and time part
                st = new StringTokenizer (this.upperBound, " ");
                // get date part
                if (st.hasMoreTokens ())
                {
                    dateStr = st.nextToken ();
                } // if
                if (st.hasMoreTokens ())
                {
                    timeStr = st.nextToken ();
                } // if
                // add script
                script.append (HtmlConstants.FV_ISDATETIMEBEFORE + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_DATE + ", '" +
                    dateStr + "', " + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_TIME + ", '" +
                    timeStr + "', " + emptyAllowedScript + ")");
                break;
            case Datatypes.DT_SEARCHDATETIME:
                // init date and time strings
                dateStr = "";
                timeStr = "";
                // split lower bound string in date and time part
                st = new StringTokenizer (this.upperBound, " ");
                // get date part
                if (st.hasMoreTokens ())
                {
                    dateStr = st.nextToken ();
                } // if
                if (st.hasMoreTokens ())
                {
                    timeStr = st.nextToken ();
                } // if
                // add script
                script.append (HtmlConstants.FV_ISDATETIMEBEFORE + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_DATE + ", '" +
                    dateStr + "', " + HtmlConstants.JREF_SHEETFORM +
                    this.name + FormFieldConstants.POSTFIX_TIME + ", '" +
                    timeStr + "', " + emptyAllowedScript + ")");
                break;

            case Datatypes.DT_MONEY:
                // add script
                script.append ("top.iMLE (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.upperBound + ", " + emptyAllowedScript + ")");
                break;
            default:
                // do nothing
        } // switch - dataType

        // exit method
        return script;
    } // buildUpperRestrictScriptCode


    /**************************************************************************
     * Build JavaScript code which will check lower and upper bound
     * restrictions. <BR/>
     *
     * @param   emptyAllowedScript  Script for checking if empty value is
     *                              allowed.
     * @param   env     The current environment
     *
     * @return  created JavaScript code
     */
    public StringBuffer buildLowerUpperRestrictScriptCode (StringBuffer emptyAllowedScript, Environment env)
    {
        // created JavaScript code
        StringBuffer script = new StringBuffer ();

        // differ data types where bounds are possible
        switch (this.dataType)
        {
            case Datatypes.DT_INTEGER:
                // add script
                script.append ("top.iIR (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.lowerBound + ", " + this.upperBound + ", " +
                    emptyAllowedScript + ")");
                break;
            case Datatypes.DT_DATE:
                // add script
                script.append ("alert ('Datatypes.DT_DATE " +
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_FORMVALIDATIONRANGE_NOTIMPL, env) 
                    + "');");
                break;
            case Datatypes.DT_TIME:
                // add script
                script.append ("alert ('Datatypes.DT_TIME " +
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_FORMVALIDATIONRANGE_NOTIMPL, env)
                    + "');");
                break;
            case Datatypes.DT_DATETIME:
                // add script
                script.append ("alert ('Datatypes.DT_DATETIME " + 
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_FORMVALIDATIONRANGE_NOTIMPL, env)
                    + "');");
                break;
            case Datatypes.DT_SEARCHDATETIME:
                // add script
                script.append ("alert ('Datatypes.DT_SEARCHDATETIME " +
                    MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_FORMVALIDATIONRANGE_NOTIMPL, env)
                    + "');");
                break;
            case Datatypes.DT_MONEY:
                // add script
                script.append ("top.iMGR (" + HtmlConstants.JREF_SHEETFORM + this.name +
                    ", " + this.lowerBound + ", " + this.upperBound + ", " +
                    emptyAllowedScript + ")");
                break;
            default:
                // do nothing
        } // switch - dataType

        // exit method
        return script;
    } // buildLowerUpperRestrictScriptCode

} // FormFieldRestriction
