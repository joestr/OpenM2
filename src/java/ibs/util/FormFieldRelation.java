/*
 * Class: FormFieldRelation.java
 */

// package:
package ibs.util;

// imports:
//KR TODO: unsauber
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.BOMessages;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.util.UtilConstants;


/******************************************************************************
 * With this class it is possible to define relations between two fields
 * of one form. <BR> This relations are expressed as operators (equal, greater,
 * ...). The realizations will be translated to JavaScript code. <BR/>
 * Attention-Attention: The created JavaScript code contains top.* references
 * to the top frame of the browser, because all functions concerning type
 * or range checks are held there!.
 * You will often encounter the term 'compound datatypes'. Compound datatypes
 * will be displayed in two fields, e.g. Datatypes.DT_DATETIME, Datatypes.DT_INTEGERRANGE.
 *
 * @version     $Id: FormFieldRelation.java,v 1.16 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author          Horst Pichler (HP) 980508
 ******************************************************************************
 */
public class FormFieldRelation extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormFieldRelation.java,v 1.16 2010/04/07 13:37:04 rburgermann Exp $";


    /**
     * Name of the the first form field. <BR/>
     */
    public String field1 = null;

    /**
     * Viewed name of the first form field. <BR/>
     */
    public String name1 = null;

    /**
     * Name of the the second form field. <BR/>
     */
    public String field2 = null;

    /**
     * Viewed name of the second form field. <BR/>
     */
    public String name2 = null;

    /**
     * Datatype of form field. <BR/>
     * Note that following datatypes (compound) include two seperate form
     * fields.
     *      - {@link ibs.bo.Datatypes#DT_DATETIME Datatypes.DT_DATETIME}
     *      - {@link ibs.bo.Datatypes#DT_DATETIME Datatypes.DT_DATETIME}
     *      - {@link ibs.bo.Datatypes#DT_DATERANGE Datatypes.DT_DATERANGE}
     *      - {@link ibs.bo.Datatypes#DT_TIMERANGE Datatypes.DT_TIMERANGE}
     */
    public int dataType = Datatypes.DT_UNKNOWN;

    /**
     * Wanted relation between the two fields. <BR/>
     */
    public int relationOperator = UtilConstants.FF_REL_UNKNOWN;

    /**
     * End of javascript call for form field relation check. <BR/>
     */
    private static final String FFCHECK_END = "')) return false;";


    /**************************************************************************
     * Constructor. <BR/>
     */
    public FormFieldRelation ()
    {
        // nothing to do
    } // FormFieldRelation


    /**************************************************************************
     * Creates a FormFieldRelation object. <BR/>
     *
     * @param   dataType            The data type of the relation.
     * @param   field1              First field.
     * @param   name1               Name of first field.
     * @param   field2              Second field.
     * @param   name2               Name of second field.
     * @param   relationOperator    Operator to be performed in relation.
     */
    public FormFieldRelation (int dataType, String field1, String name1,
        String field2, String name2, int relationOperator)
    {
        this.dataType = dataType;
        this.field1 = field1;
        this.name1 = name1;
        this.field2 = field2;
        this.name2 = name2;
        this.relationOperator = relationOperator;
    } // FormFieldRelation


    /**************************************************************************
     * Build JavaScript code which will check relations on form-
     * submission. <BR/>
     * For each relation a script code like:
     *
     *   if (top.function (..., ...)) return false;
     *
     * will be generated. At the end all if-stmts will be combined in one
     * function wich will be called on submit.
     * this function will (e.g.) look like this:
     *
     * <PRE>
     * function submitAllowed ()
     * {
     *     // check all restrictions/relations
     *     if (top.function (..., ...)) return false;
     *     if (!top.otherFunction (..., ...)) return false;
     *     ...
     *     ...
     *     return true;
     * } // submitAllowed
     * </PRE>
     *
     * @param   env          The current environment
     * 
     * @return  created      JavaScript code if-statement
     */
    public String buildRelationScriptCode (Environment env)
    {
        // does datatype need check? - some don't!
        if (this.dataType == Datatypes.DT_UNKNOWN ||  // unknown data type
            this.dataType == Datatypes.DT_BOOL ||     // boolean -> check box
            this.dataType == Datatypes.DT_RADIO ||    // radio button
            this.dataType == Datatypes.DT_SELECT)     // selection box
        {
            return "";
        } // if

        // created JavaScript code
        String script = "";
        // used operator (==, !=, <, ...)
        String operator = "";

        try
        {
            // differ operators
            switch (this.relationOperator)
            {
                case UtilConstants.FF_REL_EQUAL:
                    operator = "==";
                    break;
                case UtilConstants.FF_REL_EQUALIGNORECASE:
                    script =
                        "if (!top.cTxI (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2 + ", '" +
                        this.name2 + "', '==')) return false;";
                    return script;
                case UtilConstants.FF_REL_NOTEQUAL:
                    operator = "!=";
                    break;
                case UtilConstants.FF_REL_GREATER:
                    operator = ">";
                    break;
                case UtilConstants.FF_REL_LOWER:
                    operator = "<";
                    break;
                case UtilConstants.FF_REL_GREATEREQUAL:
                    operator = ">=";
                    break;
                case UtilConstants.FF_REL_LOWEREQUAL:
                    operator = "<=";
                    break;
                default:
                    // unknown data type - return javascript warning
                    throw new GeneralException ("unknown function");
            } // switch

            // differ datatypes - set compare function with given operator
            switch (this.dataType)
            {
                // all text based form fields
                case Datatypes.DT_TEXT:
                case Datatypes.DT_TEXTAREA:
                case Datatypes.DT_IMAGE:
                case Datatypes.DT_FILE:
                case Datatypes.DT_NAME:
                case Datatypes.DT_PASSWORD:
                case Datatypes.DT_USER:
                case Datatypes.DT_DESCRIPTION:
                case Datatypes.DT_SEARCHTEXT:
                case Datatypes.DT_SEARCHTEXT_EXT:
                case Datatypes.DT_SEARCHDATE:
                case Datatypes.DT_SEARCHNUMBER:
                case Datatypes.DT_LINK:
                case Datatypes.DT_EMAIL:
                case Datatypes.DT_URL:
                    // add script - used function:
                    // compareText (field1, name1, field2, name2, operator)
                    script =
                        "if (!top.cTx (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2 + ", '" +
                        this.name2 + "', '" + operator + FormFieldRelation.FFCHECK_END;
                    break;

                case Datatypes.DT_INTEGER:
                case Datatypes.DT_SEARCHINTEGER:
                    // add script - used function:
                    // compareInt (field1, name1, field2, name2, operator)
                    script =
                        "if (!top.cI (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2 + ", '" +
                        this.name2 + "', '" + operator + FormFieldRelation.FFCHECK_END;

                    break;
                case Datatypes.DT_DATE:
                    // add script - used function:
                    // compareDate (field1, name1, field2, name2, operator)
                    script =
                        "if (!top.cD (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2  + ", '" +
                        this.name2 + "', '" + operator + FormFieldRelation.FFCHECK_END;
                    break;
                case Datatypes.DT_TIME:
                    // add script - used function:
                    // compareTime (field1, name1, field2, name2, operator)
                    script =
                        "if (!top.cT (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2 + ", '" +
                        this.name2 + "', '" + operator + FormFieldRelation.FFCHECK_END;
                    break;
                case Datatypes.DT_DATETIME:
                    // add script - used function:
                    // compareDateTime (datefield1, timefield1, name1,
                    //     datefield2, timefield2, operator)
                    script =
                        "if (!top.cDT (" + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + FormFieldConstants.POSTFIX_DATE + ", " + HtmlConstants.JREF_SHEETFORM +
                        this.field1 + FormFieldConstants.POSTFIX_TIME + ", '" + this.name1 +
                        "', " + HtmlConstants.JREF_SHEETFORM + this.field2 + FormFieldConstants.POSTFIX_DATE + ", " +
                        HtmlConstants.JREF_SHEETFORM + this.field2 + FormFieldConstants.POSTFIX_TIME + ", '" +
                        this.name2 + "', '" + operator + FormFieldRelation.FFCHECK_END;
                    break;
                default:
                    // unknown data type - do nothing
                    throw new GeneralException ("unknown data type");
            } // switch
        } // try
        catch (GeneralException e)
        {
            // unknown data type - do nothing
            script = "alert (\"" + MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOTIMPLEMENTED, env) + " Function: " +
                this.relationOperator + " * Datatype: " + this.dataType + "\");";
        } // catch GeneralException

        // exit method
        return script;
    } // buildRelationScriptCode

} // FormFieldRelation
