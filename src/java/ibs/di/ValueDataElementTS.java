/*
 * Class: ValueDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.AppMessages;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.di.filter.m2XMLFilter;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MlInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.obj.query.QueryArguments;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryCreator_01;
import ibs.obj.query.QueryExceptions;
import ibs.obj.query.QueryHelpers;
import ibs.obj.query.QueryNotFoundException;
import ibs.obj.query.QueryPool;
import ibs.service.user.User;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.util.Base64;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.StringHelpers;
import ibs.util.crypto.EncryptionManager;
import ibs.util.file.FileHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class holds the type-specific methods of ValueDataElement.
 * These methods should be implemented in sub classes of ValueDataElement.
 *
 * @version     $Id: ValueDataElementTS.java,v 1.21 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 17.07.2009
 * @since       3.0.0
 ******************************************************************************
 */
public abstract class ValueDataElementTS extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ValueDataElementTS.java,v 1.21 2013/01/15 14:48:28 rburgermann Exp $";



    /***************************************************************************
     * Read the data element values from the User input. <BR/>
     *
     * @param   vie         The value data element.
     * @param   fieldArgument The argument name of the field.
     * @param   app         The application context.
     * @param   sess        The current session context.
     * @param   env         Environment for getting input and generating output.
     * @param   path        Path for data files.
     * @param   viewMode    The view mode for the object.
     */
    public static void getParameters (ValueDataElement vie, String fieldArgument,
                                      ApplicationInfo app, SessionInfo sess,
                                      Environment env, String path, int viewMode)
    {
        // call type-specific version of the method:
        ValueDataElementTS.getParametersTS (vie, fieldArgument, app, sess, env,
            path, viewMode);
    } // getParameters


    /***************************************************************************
     * Read the data element values from the user input. <BR/>
     * Type specific version of this method.
     *
     * @param   vie         The value data element.
     * @param   fieldArgument The argument name of the field.
     * @param   app         The application context.
     * @param   sess        The current session context.
     * @param   env         Environment for getting input and generating output.
     * @param   path        Path for data files.
     * @param   viewMode    The view mode for the object.
     */
    private static void getParametersTS (ValueDataElement vie,
                                         String fieldArgument,
                                         ApplicationInfo app, SessionInfo sess,
                                         Environment env, String path,
                                         int viewMode)
    {
/* KR currently not necessary
trace ("--- START getDataElementParameters ---");
*/
        String str;
        String strValue;
        String strOptionValues;
        int intValue;
        float floatValue;
        Date dateValue;

        // differentiate between the various datatypes
        if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_BOOLEAN))
        {
            intValue = env.getBoolParam (fieldArgument);
            if (intValue >= IOConstants.BOOLPARAM_FALSE)
            {
                vie.setOldValue ("" +
                    (intValue == IOConstants.BOOLPARAM_TRUE));
            } // if
        } // if
        // datetime
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_DATETIME))
        {
            str = env.getParam (fieldArgument + "_d");
            if (str != null)
            {
                if (str.length () > 0)
                {
                    dateValue = env.getDateTimeParam (fieldArgument);
                    vie.setOldValue (DateTimeHelpers
                        .dateTimeToString (dateValue));
                } // if
                else
                {
                    vie.setOldValue ("");
                } // else
            } // if
        } // if
        // date
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_DATE))
        {
            str = env.getParam (fieldArgument);
            if (str != null)
            {
                if (str.length () > 0)
                {
                    dateValue = env.getDateParam (fieldArgument);
                    vie.setOldValue (DateTimeHelpers
                        .dateToString (dateValue));
                } // if
                else
                {
                    vie.setOldValue ("");
                } // else
            } // if
        } // if
        // time
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_TIME))
        {
            str = env.getParam (fieldArgument);
            if (str != null)
            {
                if (str.length () > 0)
                {
                    dateValue = env.getTimeParam (fieldArgument);
                    vie.setOldValue (DateTimeHelpers
                        .timeToString (dateValue));
                } // if
                else
                {
                    vie.setOldValue ("");
                } // else
            } // if
        } // if
        // file || image
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FILE) ||
            vie.type.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
        {
            if ((strValue = BOHelpers.getFileParamBO (fieldArgument, env)) != null)
            {
                vie.setOldValue (strValue);
                vie.p_size = FileHelpers.getFileSize (path, vie.value);
                vie.setFileFlag (true);
            } // if (strValue != null)
            else
            // no new file set
            {
                str = env.getStringParam (fieldArgument +
                    DIArguments.ARG_FILE_EXTENSION);

                // check if the file has been deleted
                if (str != null && str.isEmpty ())
                {
                    vie.setOldValue ("");
                    vie.setFileFlag (false);
                } // if
            } // else no new file set
        } // if
        // integer
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_INT))
        {
            // check if the int value exists
            str = env.getParam (fieldArgument);
            if (str != null)
            {
                if (str.trim ().length () > 0)
                {
                    intValue = env.getIntParam (fieldArgument);
                    vie.setOldValue (Integer.toString (intValue));
                } // if
                else
                {
                    // clear the field:
                    vie.setOldValue ("");
                } // else if
            } // if
        } // if
        // float
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FLOAT))
        {
            // check if the float value exists:
            str = env.getParam (fieldArgument);
            if (str != null)
            {
                if (str.trim ().length () > 0)
                {
                    floatValue = env.getFloatParam (fieldArgument);
                    // set the float value
                    vie.setOldValue (Float.toString (floatValue));
                } // if
                else
                {
                    // clear the field
                    vie.setOldValue ("");
                } // else if
            } // if
        } // if
        // option
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_OPTION))
        {
            strValue = env.getStringParam (fieldArgument);
            if (strValue != null)
            {
                strOptionValues =
                    env.getStringParam (fieldArgument +
                        DIArguments.ARG_OPTION_EXTENSION);
                vie.setOldValue (DIHelpers.switchFirstToken (strValue,
                    strOptionValues, DIConstants.OPTION_DELIMITER));
            } // if
        } // if
        // separator:
        else if (vie.type
            .equalsIgnoreCase (DIConstants.VTYPE_SEPARATOR))
        {
            // BB HINT: separator do not have any values!!! they are
            // used for
            // pure layout purposes!!!
            vie.setOldValue ("");
        } // if
        // remark:
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_REMARK))
        {
            // BB HINT: the value of a remark field can not be changed
        } // if
        // button:
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_BUTTON))
        {
            // BB HINT: the value of a button field can not be changed
        } // if
        // EXPORTFILTER
        else if (vie.type
            .equalsIgnoreCase (DIConstants.VTYPE_EXPORTFILTER))
        {
/* KR currently not necessary
trace ("Exportfilter " + DIConstants.VTYPE_EXPORTFILTER);
*/
            // get the fieldname out of the environment
            intValue = env.getIntParam (fieldArgument);
/* KR currently not necessary
trace ("fieldArgument " + fieldArgument);
*/
            if (intValue != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                // write the filterId to the value of the DataElement
                vie.setOldValue ("" + intValue);
            } // if
        } // else if
        // IMPORTFILTER
        else if (vie.type
            .equalsIgnoreCase (DIConstants.VTYPE_IMPORTFILTER))
        {
            // get the fieldname out of the environment
            intValue = env.getIntParam (fieldArgument);
            if (intValue != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                // write the filterId to the value of the DataElement
                vie.setOldValue ("" + intValue);
            } // if
        } // else if
        // SELECTION
        else if (vie.type
            .equalsIgnoreCase (DIConstants.VTYPE_SELECTION))
        {
/* KR currently not necessary
trace ("vie.value : " + vie.value); trace ("vie.searchroot : " + vie.searchRoot);
trace ("vie.searchrecursive : " + vie.searchRecursive);
trace ("vie.typefilter : " + vie.typeFilter);
*/
            strValue = env.getStringParam (fieldArgument);
            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if if (strValue != null)
            // get the parameters for the typeFilter
            str = env.getStringParam (fieldArgument +
                DIConstants.SEL_TYPEFILTER);
            if (str != null)
            {
                vie.typeFilter = str;
            } // if if (str != null)
            // get the parameters for the searchroot
            str = env.getStringParam (fieldArgument +
                DIConstants.SEL_SEARCHROOT);
            if (str != null)
            {
                vie.searchRoot = str;
            } // if if (str != null)
            // get the parameters for searchrecursive
            str = env.getStringParam (fieldArgument +
                DIConstants.SEL_SEARCHRECURSIVE);
            if (str != null)
            {
                vie.searchRecursive = str;
            } // if if (str != null)
        } // if
        // selectionbox that is filled with query data
        else if (vie.type
            .startsWith (DIConstants.VTYPE_QUERYSELECTIONBOX))
        {
            if (vie.type
                .equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOX))
            {
                str = env.getStringParam (fieldArgument +
                    DIConstants.ATTR_MULTISELECTION);
                if (str != null)
                {
                    vie.multiSelection = str;
                } // if
            } // if (vie.type.equalsIgnoreCase
                // (DIConstants.VTYPE_QUERYSELECTIONBOX))

            if (vie.multiSelection != null &&
                vie.multiSelection
                    .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
            {
                strValue = DIHelpers.getMultipleSelectionValue (
                    env.getMultipleParam (fieldArgument));
            } // if multiSelection=true
            else
            // multiSelection=false
            {
                strValue = env.getStringParam (fieldArgument);
            } // else //multiSelection=false

            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_QUERYNAME);
            if (str != null)
            {
                vie.queryName = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_OPTIONS);
            if (str != null)
            {
                vie.options = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_EMPTYOPTION);
            if (str != null)
            {
                vie.emptyOption = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REFRESH);
            if (str != null)
            {
                vie.refresh = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_NO_COLUMNS);
            if (str != null)
            {
                vie.noColumns = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_MULTISELECTION);
            if (str != null)
            {
                vie.multiSelection = str;
            } // if

        } // if
        // selectionbox that is filled with query data
        else if (vie.type.startsWith (DIConstants.VTYPE_SELECTIONBOX))
        {
            if (vie.type
                .equalsIgnoreCase (DIConstants.VTYPE_SELECTIONBOX))
            {
                str = env.getStringParam (fieldArgument +
                    DIConstants.ATTR_MULTISELECTION);
                if (str != null)
                {
                    vie.multiSelection = str;
                } // if
            } // if (vie.type.equalsIgnoreCase
                // (DIConstants.VTYPE_SELECTIONBOX))

            if (vie.multiSelection != null &&
                vie.multiSelection
                    .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
            {
                strValue = DIHelpers.getMultipleSelectionValue (
                    env.getMultipleParam (fieldArgument));
            } // if multiSelection=true
            else
            // multiSelection=false
            {
                strValue = env.getStringParam (fieldArgument);
            } // else //multiSelection=false

            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_OPTIONS);
            if (str != null)
            {
                vie.options = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_VIEWTYPE);
            if (str != null)
            {
                vie.viewType = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_NO_COLUMNS);
            if (str != null)
            {
                vie.noColumns = str;
            } // if

        } // if
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF))
        {
            // if there is an objectrefvalue, there has to be a second
            // frame
            // in the changeform
            // vie.showChangeFormAsFrameset = true;
            strValue = env.getStringParam (fieldArgument +
                BOArguments.ARG_OID_EXTENSION);

            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if if (strValue != null)
        } // if
        else if (vie.type
            .equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF))
        {
            // if there is an objectrefvalue, there has to be a second
            // frame
            // in the changeform
            // vie.showChangeFormAsFrameset = true;
            strValue = env.getStringParam (fieldArgument +
                BOArguments.ARG_OID_EXTENSION);
            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if if (strValue != null)
            strValue = env.getStringParam (fieldArgument);
            if (strValue != null)
            {
                vie.value += DIConstants.OPTION_DELIMITER + strValue;
            } // if if (strValue != null)
            if (vie.typeFilter != null)
            {
                strValue = env.getStringParam (vie.typeFilter);
                if (strValue != null)
                {
                    vie.typeFilter = strValue;
                } // if if (strValue != null)
            } // if
            if (vie.searchRoot != null)
            {
                strValue = env.getStringParam (vie.searchRoot);
                if (strValue != null)
                {
                    vie.searchRoot = strValue;
                } // if if (strValue != null)
            } // if
            if (vie.searchRootIdDomain != null)
            {
                strValue = env.getStringParam (vie.searchRootIdDomain);
                if (strValue != null)
                {
                    vie.searchRootIdDomain = strValue;
                } // if if (strValue != null)
            } // if
            if (vie.searchRootId != null)
            {
                strValue = env.getStringParam (vie.searchRootId);
                if (strValue != null)
                {
                    vie.searchRootId = strValue;
                } // if if (strValue != null)
            } // if
            if (vie.searchRecursive != null)
            {
                strValue = env.getStringParam (vie.searchRecursive);
                if (strValue != null)
                {
                    vie.searchRecursive = strValue;
                } // if if (strValue != null)
            } // if
        } // if
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD))
        {
            str = env.getStringParam (fieldArgument);

            if (str != null && str.length () > 0)
            // if password is not null and not ""
            {
                vie.setOldValue (str);
            } // if password is not null and not ""
        } // if
        else if (vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
        {
            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_MULTISELECTION);
            if (str != null)
            {
                vie.multiSelection = str;
            } // if

            // get the current set view mode
            int currentViewMode = viewMode;

            // check current display mode
            // on view mode same behavior as FIELDREF
            if (currentViewMode == XMLViewer_01.VIEWMODE_SHOW)
            {
                strValue = env.getStringParam (fieldArgument +
                    BOArguments.ARG_OID_EXTENSION);

                // get value
                if (strValue != null)
                {
                    vie.setOldValue (strValue);
                } // if
            } // if
            else
            {
                if (vie.multiSelection != null &&
                    vie.multiSelection
                        .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
                {
                    strValue = DIHelpers.getMultipleSelectionValue (
                        env.getMultipleParam (fieldArgument));
                } // if multiSelection=true
                else
                // multiSelection=false
                {
                    strValue = env.getStringParam (fieldArgument);
                } // else //multiSelection=false

                // get value
                if (strValue != null)
                {
                    vie.setOldValue (strValue);
                } // if
            } // else if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_VIEWTYPE);
            if (str != null)
            {
                vie.viewType = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_NO_COLUMNS);
            if (str != null)
            {
                vie.noColumns = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_EMPTYOPTION);
            if (str != null)
            {
                vie.emptyOption = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REFRESH);
            if (str != null)
            {
                vie.refresh = str;
            } // if
        } // if

        // reminder:
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_REMINDER))
        {
            // drop backup of old value:
            vie.p_oldReminder = null;
            // create backup of the value:
            vie.p_oldReminder = new ValueDataElement (vie);

            // get argument values:
            str = env.getParam (fieldArgument);
            if (str != null)
            {
                if (str.length () > 0)
                {
                    dateValue = env.getDateParam (fieldArgument);
                    vie.setOldValue (DateTimeHelpers
                        .dateToString (dateValue));
                } // if
                else
                {
                    vie.setOldValue ("");
                } // else
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_DISPLAY);
            if (str != null)
            {
                vie.p_displayType = str;
            } // if

            // get data for reminder 1:
            intValue = env.getIntParam (fieldArgument +
                DIConstants.ATTR_REMIND1DAYS);
            if (intValue >= 0)
            {
                vie.p_remind1Days = intValue;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND1TEXT);
            if (str != null)
            {
                vie.p_remind1Text = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND1RECIP);
            if (str != null)
            {
                vie.p_remind1Recip = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND1RECIPQUERY);
            if (str != null)
            {
                vie.p_remind1RecipQuery = str;
            } // if

            // get data for reminder 2:
            intValue = env.getIntParam (fieldArgument +
                DIConstants.ATTR_REMIND2DAYS);
            if (intValue >= 0)
            {
                vie.p_remind2Days = intValue;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND2TEXT);
            if (str != null)
            {
                vie.p_remind2Text = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND2RECIP);
            if (str != null)
            {
                vie.p_remind2Recip = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_REMIND2RECIPQUERY);
            if (str != null)
            {
                vie.p_remind2RecipQuery = str;
            } // if

            // get data for escalation:
            intValue = env.getIntParam (fieldArgument +
                DIConstants.ATTR_ESCALATEDAYS);
            if (intValue >= 0)
            {
                vie.p_escalateDays = intValue;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_ESCALATETEXT);
            if (str != null)
            {
                vie.p_escalateText = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_ESCALATERECIP);
            if (str != null)
            {
                vie.p_escalateRecip = str;
            } // if

            str = env.getStringParam (fieldArgument +
                DIConstants.ATTR_ESCALATERECIPQUERY);
            if (str != null)
            {
                vie.p_escalateRecipQuery = str;
            } // if
        } // else if

        // everything else
        else
        {
            strValue = env.getStringParam (fieldArgument);

            if (strValue != null)
            {
                vie.setOldValue (strValue);
            } // if if (strValue != null)
        } // else any other type
    } // getParametersTS


    /***************************************************************************
     * Create a value for the dom tree and add it directly to the tree. <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the value is directly added.
     *
     * @param   obj         The object for which to create the dom tree.
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   vie         The data element representing the data of the value.
     * @param   objOid      The oid of the object.
     * @param   fieldName   Name of field.
     * @param   arg         The argument.
     * @param   argName     Name of argument for form fields.
     * @param   viewMode    The specific view mode.
     * @param   app         Application info object.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    public static void createDomTreeValueNode (XMLViewer_01 obj,
                                               Document doc, Node values,
                                               ValueDataElement vie,
                                               OID objOid, String fieldName,
                                               String arg, String argName,
                                               int viewMode,
                                               ApplicationInfo app,
                                               SessionInfo sess, User user,
                                               Environment env)
    {
        // call type-specific version of the method:
        ValueDataElementTS.createDomTreeValueNodeTS (obj, doc, values, vie,
            objOid, fieldName, arg, argName, viewMode, app, sess, user, env);
    } // createDomTreeValueNode


    /***************************************************************************
     * Create a value for the dom tree and add it directly to the tree. <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the value is directly added.
     * Type specific version of this method.
     *
     * @param   obj         The object for which to create the dom tree.
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   vie         The data element representing the data of the value.
     * @param   objOid      The oid of the object.
     * @param   fieldName   Name of field.
     * @param   arg         The argument.
     * @param   argName     Name of argument for form fields.
     * @param   viewMode    The specific view mode.
     * @param   app         Application info object.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    public static void createDomTreeValueNodeTS (XMLViewer_01 obj,
                                                 Document doc, Node values,
                                                 ValueDataElement vie,
                                                 OID objOid, String fieldName,
                                                 String arg, String argName,
                                                 int viewMode,
                                                 ApplicationInfo app,
                                                 SessionInfo sess, User user,
                                                 Environment env)
    {
        OID searchStart = null;
        boolean searchRecursive = false;

        // <VALUE FIELD="" TYPE=""></VALUE>
        Element value = doc.createElement (DIConstants.ELEM_VALUE);
        value.setAttribute (DIConstants.ATTR_FIELD, fieldName);
        value.setAttribute (DIConstants.ATTR_TYPE, vie.type);

        // set the input field name (replace all critical characters)
        value.setAttribute ("INPUT", argName);

        // set the multilang info
        MlInfo mlInfo = vie.getMlInfo (env);
        value.setAttribute (DIConstants.ATTR_NAME, mlInfo.getName ());
        value.setAttribute (DIConstants.ATTR_DESCRIPTION, mlInfo.getDescription ());
        value.setAttribute (DIConstants.ATTR_UNIT, mlInfo.getUnit ());
        
        // set correct mandatory value
        if (vie.mandatory != null &&
            vie.mandatory.equalsIgnoreCase (DIConstants.ATTRVAL_YES))
        {
            value.setAttribute (DIConstants.ATTR_MANDATORY,
                DIConstants.ATTRVAL_YES);
        } // if
        else
        // not mandatory
        {
            value.setAttribute (DIConstants.ATTR_MANDATORY,
                DIConstants.ATTRVAL_NO);
        } // else not mandatory

        // set correct readonly value:
        if (vie.p_readonly != null &&
            vie.p_readonly.equalsIgnoreCase (DIConstants.ATTRVAL_YES))
        {
            value.setAttribute (DIConstants.ATTR_READONLY,
                DIConstants.ATTRVAL_YES);
        } // if
        else
        // not readonly
        {
            value.setAttribute (DIConstants.ATTR_READONLY,
                DIConstants.ATTRVAL_NO);
        } // else not readonly

        // the value is tested several times. it is b
        String valueStr = vie.value;
        if (valueStr == null)
        {
            valueStr = "";
        } // if

        // is query required for value?
        boolean requireQuery = true;

        // if this is an LINK value add the protocol prefix attribute
        if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_URL))
        {
            String url = vie.value.toLowerCase ();
            String protocol = null;

            int pos = url.indexOf ("://");
            if (pos >= 0)
            {
                protocol = url.substring (0, pos);
            } // if

            if (protocol != null)
            {
                value.setAttribute ("PROTOCOL", protocol);
            } // if
        } // if

        // if this is an OBJECTREF value there have to be 3 more attributes
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF))
        {
            if (vie.typeFilter != null)
            {
                obj.searchTypes = vie.typeFilter;
            } // if
            // check if a search root is defined via EXTKEY
            if (vie.searchRootIdDomain != null && !vie.searchRootIdDomain.isEmpty () &&
                    vie.searchRootId != null && !vie.searchRootId.isEmpty ())
            {
                searchStart = BOHelpers.getOidByExtKey (vie.searchRootIdDomain, vie.searchRootId, env);                
            } // if
            // check if a search root is defined via path
            else if (vie.searchRoot != null)
            {
                searchStart = BOHelpers.resolveObjectPath (vie.searchRoot,
                    obj.containerId, obj, env);
            } // if
            if (vie.searchRecursive != null)
            {
                searchRecursive = vie.searchRecursive
                    .equalsIgnoreCase ("YES");
            } // if

            // searchtext - field for fullname
            String fieldname = arg;
            String url = null;

            url = IOHelpers.getBaseUrl (env) +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                    AppFunctions.FCT_SHOWOBJECTCONTENT) +
                HttpArguments.createArg (BOArguments.ARG_OID, (new OID (
                    BOHelpers.getTypeCache ().getTVersionId (
                        TypeConstants.TC_ObjectSearchContainer), 0))
                    .toString ()) +
                HttpArguments.createArg (BOArguments.ARG_CALLINGOID, "" +
                    objOid) +
                HttpArguments
                    .createArg (BOArguments.ARG_TYPE, obj.searchTypes) +
                HttpArguments.createArg (BOArguments.ARG_RECURSIVE,
                    searchRecursive) +
                HttpArguments.createArg (BOArguments.ARG_CONTAINERID, "" +
                    searchStart) +
                HttpArguments.createArg (BOArguments.ARG_SHOWLINK,
                    BOConstants.SHOWSEARCHEDOBJECTS) +
                HttpArguments.createArg (BOArguments.ARG_FIELDNAME, fieldname);

            value
                .setAttribute (DIConstants.ATTR_TYPECODEFILTER, vie.typeFilter);
            value.setAttribute (DIConstants.ATTR_SEARCHRECURSIVE,
                vie.searchRecursive);
            value.setAttribute (DIConstants.ATTR_SEARCHROOT, vie.searchRoot);
            value.setAttribute (DIConstants.ATTR_SEARCHROOTIDDOMAIN, vie.searchRootIdDomain);
            value.setAttribute (DIConstants.ATTR_SEARCHROOTID, vie.searchRootId);
            value.setAttribute (DIConstants.ATTR_URL, url);
        } // else if

        // <FILE>
        // add url to file as attribute to value
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FILE) ||
            vie.type.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
        {
            // show name of file without OID !!!
            value.setAttribute (DIConstants.ATTR_URL, objOid.toString () +
                "/" + vie.value);
            value.setAttribute (DIConstants.ATTR_SIZE, "" + vie.p_size);
        } // else if

        // <QUERY>
        // show value if type is not QUERY
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_QUERY))
        {
            // TODO: this is a test. try to cache a query field
            // get the cached query result node of the query field
            Node queryResultNode = vie.getQueryResultNode ();
            // check if we got a result?
            if (queryResultNode == null)
            {
                // resolve the query and add the data
                QueryHelpers.addQueryData (obj, value, objOid, vie.field,
                    vie.queryName,
                    DIHelpers.getTemplateSubTags (
                        (DocumentTemplate_01) obj.typeObj.getTemplate (),
                        vie.field), env);
                // cache the query node
                vie.setQueryResultNode (value);
            } // if
            else
            // include the cached query node
            {
                value = (Element) doc.importNode (queryResultNode, true);
            } // else include the cached query node
        } // else if

        // <QUERYSELECTIONBOX>
        else if (vie.type.startsWith (DIConstants.VTYPE_QUERYSELECTIONBOX))
        {
            // set custom vie. type value
            if (vie.viewType != null &&
                vie.viewType.equalsIgnoreCase (DIConstants.ATTRVAL_CHECKLIST))
            {
                value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                    DIConstants.ATTRVAL_CHECKLIST);

                // set the number of columns value
                if (vie.noColumns != null)
                {
                    value.setAttribute (DIConstants.ATTR_NO_COLUMNS,
                        vie.noColumns);
                } // if

            } // else if
            else
            {
                value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                    DIConstants.ATTRVAL_SELECTIONBOX);
            } // if

            if (vie.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX))
            {
                // set correct multiselection value
                if (vie.multiSelection != null &&
                    vie.multiSelection
                        .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                        DIConstants.ATTRVAL_YES);
                } // if
                else
                // not multiselection
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                        DIConstants.ATTRVAL_NO);
                } // else not multiselection
            } // if (vie.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX))

            ValueDataElementTS.addSelectionData (value, viewMode, vie);
        } // else if

        // <SELECTIONBOX>
        else if (vie.type.startsWith (DIConstants.VTYPE_SELECTIONBOX))
        {
            // set custom view type value
            if (vie.viewType != null &&
                vie.viewType.equalsIgnoreCase (DIConstants.ATTRVAL_CHECKLIST))
            {
                value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                    DIConstants.ATTRVAL_CHECKLIST);

                // set the number of columns value
                if (vie.noColumns != null)
                {
                    value.setAttribute (DIConstants.ATTR_NO_COLUMNS,
                        vie.noColumns);
                } // if

            } // else if
            else
            {
                value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                    DIConstants.ATTRVAL_SELECTIONBOX);
            } // if

            if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_SELECTIONBOX))
            {
                // set correct multiselection value
                if (vie.multiSelection != null &&
                    vie.multiSelection
                        .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                        DIConstants.ATTRVAL_YES);
                } // if
                else
                // not multiselection
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                        DIConstants.ATTRVAL_NO);
                } // else not multiselection
            } // if

            ValueDataElementTS.addSelectionData (value, viewMode, vie);
        } // else if

        // <HTMLTEXT>
        // In a xsl stylesheet the output of html text
        // is not so easy becouse the xsl processor escapes the meta characters
        // (like '<' '>') in the tag content.
        // To avoid this output escaping the html text must be embedded
        // in a <SCRIPT> tag and processed with JavaScript commands.
        // For this reason all linefeeds the html text must be replaced with
        // spaces.
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_HTMLTEXT))
        {
            if (viewMode == XMLViewer_01.VIEWMODE_SHOW)
            {
                // replace all linefeeds with a space
                // escape the single quote character (') with a leading '\'
                StringBuffer buf = new StringBuffer ();
                int len = valueStr.length ();
                for (int i = 0; i < len; i++)
                {
                    char c = valueStr.charAt (i);
                    switch (c)
                    {
                        case '\n':
                        case '\r':
                            c = ' ';
                            break;
                        case '\\':
                        case '\'':
                            buf.append ('\\');
                        default:
                            // nothing to do
                    } // switch (c)
                    buf.append (c);
                } // for i
                valueStr = buf.toString ();
            } // if
        } // else if

        // if not null set the content string of the value element
        if (valueStr != null)
        {
            value.appendChild (doc.createTextNode (valueStr));
        } // if

        // <LONGTEXT>
        // The content of a LONGTEXT field is not added as a single text node
        // but it is separated in single lines and each line is added as a
        // separate
        // node element.
        if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_LONGTEXT))
        {
            // ATTENTION!!
            // The line separation is only done for the VIEW and EDIT mode.
            // For the TRANSFORM mode this should not be done!
            if (viewMode == XMLViewer_01.VIEWMODE_SHOW ||
                viewMode == XMLViewer_01.VIEWMODE_EDIT)
            {
                String[] lines = StringHelpers.stringToStringArray (valueStr,
                    '\n', true);
                // loop through the lines:
                for (int i = 0; i < lines.length; i++)
                {
                    Element line = doc.createElement (DIConstants.ELEM_LINE);
                    if (lines[i] != null)
                    {
                        line.appendChild (doc.createTextNode (lines[i]));
                    } // if
                    value.appendChild (line);
                } // for
            } // if
        } // if

        // <FIELDREF>
        else if (vie.type.startsWith (DIConstants.VTYPE_FIELDREF))
        {
            ValueDataElementTS.addFieldRefData (value, viewMode, vie,
                requireQuery, app, sess, user, env);
        } // else if

        // <VALUEDOMAIN>
        // ValueDomain has the same view as FieldRef on the view mode
        // and on the edit mode the same as queryselectionbox,
        // except displaying and saving oid instead of text
        else if (vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
        {
            // set context value:
            if (vie.p_context != null)
            {
                // set attribute context, specifies the type of ValueDomains
                // which should be shown. This field is required for
                // querying value domain elements
                value.setAttribute (DIConstants.ATTR_CONTEXT, vie.p_context);
            } // if

            // set correct multiselection value
            if (vie.multiSelection != null &&
                vie.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES))
            {
                value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                    DIConstants.ATTRVAL_YES);
            } // if
            else
            // not multiselection
            {
                value.setAttribute (DIConstants.ATTR_MULTISELECTION,
                    DIConstants.ATTRVAL_NO);
            } // else not multiselection

            // check which mode should be displayed
            if (viewMode == XMLViewer_01.VIEWMODE_EDIT || viewMode == XMLContainer_01.VIEWMODE_CONTENTEDIT)
            {
                // set custom view type value
                if (vie.viewType != null &&
                    vie.viewType
                        .equalsIgnoreCase (DIConstants.ATTRVAL_CHECKLIST))
                {
                    value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                        DIConstants.ATTRVAL_CHECKLIST);

                    // set the number of columns value
                    if (vie.noColumns != null)
                    {
                        value.setAttribute (DIConstants.ATTR_NO_COLUMNS,
                            vie.noColumns);
                    } // if

                } // else if
                else
                {
                    value.setAttribute (DIConstants.ATTR_VIEWTYPE,
                        DIConstants.ATTRVAL_SELECTIONBOX);
                } // if

                // display ValueDomain as selection box
                ValueDataElementTS.addValueDomainSelectionData (value,
                    viewMode, vie, app, sess, user, env);
            } // if
            else if (viewMode == XMLViewer_01.VIEWMODE_SHOW)
            {
                // display ValueDomain
                ValueDataElementTS.addValueDomainViewData (value, viewMode,
                    vie, app, sess, user, env);
            } // else if
        } // else if

        // <PASSWORD>
        else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD))
        {
            // remove all child nodes and add only "" to keep the
            // password out of the dom tree:
            if (value.hasChildNodes ())
            {
                NodeList nodes = value.getChildNodes ();

                for (int i = 0; i < nodes.getLength (); i++)
                {
                    value.removeChild (nodes.item (i));
                } // for i
            } // if

            value.appendChild (doc.createTextNode (""));
        } // else if

        // <REMINDER>
        else if (vie.type.startsWith (DIConstants.VTYPE_REMINDER))
        {
            try
            {
                Element recipNode = null;
                // display type:
                value
                    .setAttribute (DIConstants.ATTR_DISPLAY, vie.p_displayType);

                // reminder 1 parameters:
                value.setAttribute (DIConstants.ATTR_REMIND1DAYS, Integer
                    .toString (vie.p_remind1Days));
                value.setAttribute (DIConstants.ATTR_REMIND1TEXT,
                    vie.p_remind1Text);
                value.setAttribute (DIConstants.ATTR_REMIND1RECIP,
                    vie.p_remind1Recip);
                // create recipient node and add it to the reminder:
                recipNode = doc
                    .createElement (DIConstants.ATTR_REMIND1RECIPQUERY);
                // resolve the groups query and add the data:
                QueryHelpers.addQueryData (obj, recipNode, objOid,
                    vie.field, vie.p_remind1RecipQuery, null, env);
                value.appendChild (recipNode);

                // reminder 2 parameters:
                value.setAttribute (DIConstants.ATTR_REMIND2DAYS, Integer
                    .toString (vie.p_remind2Days));
                value.setAttribute (DIConstants.ATTR_REMIND2TEXT,
                    vie.p_remind2Text);
                value.setAttribute (DIConstants.ATTR_REMIND2RECIP,
                    vie.p_remind2Recip);
                // create recipient node and add it to the reminder:
                recipNode = doc
                    .createElement (DIConstants.ATTR_REMIND2RECIPQUERY);
                // resolve the groups query and add the data:
                QueryHelpers.addQueryData (obj, recipNode, objOid,
                    vie.field, vie.p_remind2RecipQuery, null, env);
                value.appendChild (recipNode);

                // escalation parameters:
                value.setAttribute (DIConstants.ATTR_ESCALATEDAYS, Integer
                    .toString (vie.p_escalateDays));
                value.setAttribute (DIConstants.ATTR_ESCALATETEXT,
                    vie.p_escalateText);
                value.setAttribute (DIConstants.ATTR_ESCALATERECIP,
                    vie.p_escalateRecip);
                // create recipient node and add it to the reminder:
                recipNode = doc
                    .createElement (DIConstants.ATTR_ESCALATERECIPQUERY);
                // resolve the groups query and add the data:
                QueryHelpers.addQueryData (obj, recipNode, objOid,
                    vie.field, vie.p_escalateRecipQuery, null, env);
                value.appendChild (recipNode);

                // add value content:
                Element valNode = doc.createElement (DIConstants.ELEM_VALUE);
                valNode.appendChild (doc.createTextNode (valueStr));
                value.appendChild (valNode);
            } // try
            catch (DOMException e)
            {
                // display error message:
                IOHelpers.showMessage (e, app, sess, env, true);
            } // catch
        } // else if

        // append the value node to the values:
        values.appendChild (value);

        // variables to be returned:
        // searchTypes
        // searchStart
        // searchRecursive
    } // createDomTreeValueNodeTS


    /***************************************************************************
     * adds a selectionbox which is filled with query data to the dom tree. A
     * marked item is preselected. used for the types QUERYSELECTIONBOX,
     * QUERYSELECTIONBOXNUM, QUERYSELECTIONBOXINT, SELECTIONBOX,
     * SELECTIONBOXNUM, SELECTIONBOXINT
     *
     * the domtree-part for a queryselection looks like this:
     *
     * <pre>
     *      ...........
     *      &lt;VALUES&gt;
     *          ......
     *          &lt;VALUE FIELD=&quot;x&quot; TYPE=&quot;SELECTIONBOX&quot; OPTIONS=&quot; ,eins,zwei,drei&quot;&gt;
     *              eins
     *              &lt;OPTION SELECTED=&quot;1&quot;&gt;eins&lt;/OPTION&gt;
     *              &lt;OPTION&gt;zwei&lt;/OPTION&gt;
     *              ...
     *          &lt;/VALUE&gt;
     *          &lt;VALUE FIELD=&quot;x&quot; TYPE=&quot;QUERYSELECTIONBOX&quot; QUERYNAME=&quot;x&quot;
     *              OPTIONS=&quot;x,y,z&quot; EMPTYOPTION=&quot;YES&quot; REFRESH=&quot;YES&quot;&gt;
     *              x
     *              &lt;OPTION SELECTED=&quot;1&quot;&gt;x&lt;/OPTION&gt;
     *              &lt;OPTION&gt;y&lt;/OPTION&gt;
     *              ......
     *          &lt;/VALUE&gt;
     *          ......
     *      &lt;/VALUES&gt;
     * </pre>
     *
     * @param   querySelectionValueNode ???
     * @param   viewMode    ???
     * @param   vie         The data element representing the data of the value.
     */
    private static void addSelectionData (Node querySelectionValueNode, int viewMode,
                                   ValueDataElement vie)
    {
        Document doc = querySelectionValueNode.getOwnerDocument ();
        String token = null;
        String[] tokens = DIHelpers.getTokens (vie.options,
            DIConstants.OPTION_DELIMITER);

        Set<String> valueSet = null;

        boolean multiSelection = vie.multiSelection != null &&
            vie.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

        if (multiSelection)
        {
            valueSet = ValueDataElementTS.getMultipleSelectionSet (vie.value);
        } // if

        // instanciation of all possible node objects
        Element optionNode = null;

        // do we have any options available?
        if (tokens != null)
        {
            // add all values of the selectionlist
            int i; // counter
            for (i = 0; i < tokens.length; i++)
            {
                token = StringHelpers.replace (tokens[i],
                    AppConstants.UC_COMMA, ",");

                // create option node
                optionNode = doc.createElement ("OPTION");

                if ((multiSelection && valueSet.contains (token)) ||
                    (!multiSelection && (vie.value.equals (token) ||
                    // IBS-89: Empty selection box options are represented with
                    // " ".
                    // If this value is cached in XML and retrieved later on
                    // null is returned from the tag holding
                    // this string as value. The same as for "". So the value is
                    // set to "".
                    // For those cases it is checked here if the current options
                    // value is " " and the stored data is
                    // "" to the set the preselected flag also in those cases.
                    (vie.value.isEmpty () && token.equals (" ")))))
                {
                    optionNode.setAttribute (DIConstants.ATTR_SELECTED, "1");
                } // if

                // add the token
                optionNode.appendChild (doc.createTextNode (token));
                // add the option node
                querySelectionValueNode.appendChild (optionNode);
            } // for i
        } // if
    } // addSelectionData


    /***************************************************************************
     * Returns the <code>MULTISELECTION_VALUE_SAPERATOR</code> separated
     * string as set of set of Strings.
     *
     * @param values <code>MULTISELECTION_VALUE_SAPERATOR</code> separated
     *            string
     * @return set of Strings
     */
    private static Set<String> getMultipleSelectionSet (String values)
    {
        Set<String> valueSet = new HashSet<String> ();

        StringTokenizer tok = new StringTokenizer (values,
            BOConstants.MULTISELECTION_VALUE_SAPERATOR);

        while (tok.hasMoreTokens ())
        {
            valueSet.add (tok.nextToken ());
        } // while

        return valueSet;
    } // getMultipleSelectionSet


    /***************************************************************************
     * adds a selectionbox which is filled with query data to the dom tree. A
     * marked item is preselected. used for the types VALUEDOMAIN.
     *
     * the domtree-part for a value domain in edit mode looks like this:
     * ........... <VALUES> <VALUE FIELD="x" TYPE="VALUEDOMAIN" QUERYNAME="x"
     * EMPTYOPTION="YES" REFRESH="YES"> x <OPTION SELECTED="1">x</OPTION>
     * <OPTION>y</OPTION> ...... </VALUE> ...... </VALUES>
     *
     * @param   querySelectionValueNode ???
     * @param   viewMode    ???
     * @param   vie         The data element representing the data of the value.
     * @param   app         Application info object.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    private static void addValueDomainSelectionData (
                                                     Node querySelectionValueNode,
                                                     int viewMode,
                                                     ValueDataElement vie,
                                                     ApplicationInfo app,
                                                     SessionInfo sess,
                                                     User user, Environment env)
    {
        Document doc = querySelectionValueNode.getOwnerDocument ();
        String oid = null;
        String element = null;        
        String description = null;

        // instanciation of all possible node objects
        Element optionNode = null;

        // do we have any options available?
        if (vie.values != null)
        {
            Vector<BusinessObjectInfo> valueDomainElements = 
                BOHelpers.findObjects (vie.values, env, true);
            
            // Iterate over all value domain elements
            Iterator<BusinessObjectInfo> valueDomainElementsIter = valueDomainElements.iterator ();
            while (valueDomainElementsIter.hasNext ())
            {
                BusinessObjectInfo valueDomainElement = valueDomainElementsIter.next ();
    
                // create option node
                optionNode = doc.createElement (DIConstants.ELEM_OPTION);
    
                // get the oid for the value domain element
                // A null object should only occur on the first position when 
                // the empty option is set to YES
                if (valueDomainElement != null)
                {
                    oid = valueDomainElement.getOid ().toString ();
                }
                else
                {
                    oid = OID.EMPTYOID;
                }
    
                // set the oid to the options node
                optionNode.setAttribute (DIConstants.ELEM_VALUE, oid);
                
                // get the description for the value domain element
                // A null object should only occur on the first position when 
                // the empty option is set to YES
                if (valueDomainElement != null)
                {
                    description = valueDomainElement.getDescription ();
                }
                else
                {
                    description = "";
                }
    
                // set the description to the options node
                if (description != null && !description.equals (" "))
                {
                    optionNode.setAttribute (DIConstants.ELEM_DESCRIPTION,
                        description);
                } // if 
    
                // multiSelection
                if (vie.multiSelection != null &&
                    vie.multiSelection
                        .equalsIgnoreCase (DIConstants.ATTRVAL_YES))
                {
                    if (ValueDataElementTS.getMultipleSelectionSet (vie.value).contains (oid))
                    {
                        optionNode
                            .setAttribute (DIConstants.ATTR_SELECTED, "1");
                    } // if
                } // if
                else
                {
                    if (vie.value != null && vie.value.equals (oid))
                    {
                        optionNode
                            .setAttribute (DIConstants.ATTR_SELECTED, "1");
                    } // if
                } // else
    
                // get the name for the value domain element 
                // A null object should only occur on the first position when 
                // the empty option is set to YES
                if (valueDomainElement != null)
                {
                    element = valueDomainElement.getName ();
                }
                else
                {
                    element = " ";
                }
                
                // add the token
                optionNode.appendChild (doc.createTextNode (element));
                // add the option node
                querySelectionValueNode.appendChild (optionNode);
            } // while
        } // if
    } // addValueDomainSelectionData


    /***************************************************************************
     * adds a selectionbox which is filled with query searchfields with type
     * STRING to the dom tree. A marked item is preselected. used for the types
     * QUERYSELECTIONBOX, QUERYSELECTIONBOXNUM, QUERYSELECTIONBOXINT,
     * SELECTIONBOX, SELECTIONBOXNUM, SELECTIONBOXINT
     *
     * the domtree-part for a queryselection looks like this: ........... <VALUE
     * FIELD="Field" INPUT="_FIELD" TYPE="FIELDREF" QUERYNAME="searchQuery">
     * 0x010104032000233 <OPTIONS> <OPTION VALUE="fie1" SELECTED="1">Name</OPTION>
     * <OPTION VALUE="fie2">Beschreibung</OPTION> </OPTIONS> <FIELDS> <SYSFIELD
     * NAME="Name" TOKEN="Name" INPUT="_NAME">[value]</SYSFIELD> <FIELD
     * NAME="xxx" TOKEN="xxx" INPUT="_XXX">[value]</FIELD> </FIELDS> </VALUE>
     *
     *
     * @param   valueNode   Element represent current value node.
     * @param   viewMode    Integer represent current mode.
     * @param   vde         The data element representing the data of the value.
     * @param   requireQuery Boolean flag which is <CODE>true</CODE>, if a
     *                      query is required.
     * @param   app         Application info object.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    protected static void addFieldRefData (Element valueNode, int viewMode,
                                           ValueDataElement vde,
                                           boolean requireQuery,
                                           ApplicationInfo app,
                                           SessionInfo sess, User user,
                                           Environment env)
    {
        Document doc = valueNode.getOwnerDocument ();
        OID refOid = null; // oid of referenced Object

        // AJ remark !!!!!!! not only XMLViewer should be referenced objects
        XMLViewer_01 refObj = null; // referenced Object
        // AJ remark !!!!!!! not only XMLViewer should be referenced objects

        // append options for search
        // Tag <OPTIONS>
        Element optionsNode = doc.createElement (DIConstants.ELEM_OPTIONS);
        // Tag <OPTION>
        Element optionNode = null;

        // check if query is required for this value
        if (requireQuery)
        {
            // set queryName for attribute QUERYNAME
            valueNode.setAttribute (DIConstants.ATTR_QUERYNAME, vde.queryName);

            // get query data
            try
            {
                // try to get query via name out of querypool
                QueryCreator_01 qc = ((QueryPool) app.queryPool).fetch (
                    vde.queryName, user.domain);
                // go through all searchfields
                int searchFieldCount = qc.getSearchFieldCount ();
                // add searchfield with type STRING to OPTIONS Tag
                for (int i = 0; i < searchFieldCount; i++)
                {
                    if (!QueryConstants.FIELDTYPE_STRING.equals (qc
                        .getFieldType (i)))
                    {
                        continue;
                    } // if

                    // create new Node OPTION
                    optionNode = doc.createElement (DIConstants.ELEM_OPTION);
                    // argument for OPTION
                    optionNode.setAttribute (DIConstants.ATTR_VALUE,
                        QueryArguments.ARG_SEARCHFIELD + i);
                    // TODO RB: Do we need to get the multilang name from the
                    //          Query Creator with .getMlFieldName (i) here??
                    // add value content
                    optionNode.appendChild (doc.createTextNode (qc
                        .getFieldName (i)));
                    optionsNode.appendChild (optionNode);
                } // for i
                valueNode.appendChild (optionsNode);
            } // try
            catch (QueryNotFoundException e)
            {
                // show exception if query was not found
                String exc =  
                    MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                        QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_NAME,
                        new String[] {vde.queryName}, env);

                IOHelpers.showMessage ("XMLViewer_01.addFieldRefData: ERROR " +
                    exc, app, sess, env);
            } // catch
        } // if

        // get object of referenced oid if there is one oid
        try
        {
        	// check if a value has been set
        	if (vde.value != null)
        	{
                refOid = new OID (
                        // 20090911 BT/BB Hack:
                        // The incorrect 0xF0F0F0F0F0F0F0F0 can occurs within
                        // the database for value domain and fieldref fields.
                        // see IBS-254
                        vde.value.equals("0xF0F0F0F0F0F0F0F0") ? "" : vde.value);
                
                // check if the oid is valid:
                if (!refOid.isEmptyInDomain ()) // oid is valid?
                {
                    // try to get object via oid:
                    refObj = (XMLViewer_01)
                        BOHelpers.getObject (refOid, env, false, false, false);
                } // if oid is valid        		
        	} // if (vde.value != null)
        	else // no value set
        	{
        		refOid = OID.getEmptyOid ();
        	} // else no value set
        } // try
        catch (IncorrectOidException e)
        {
            // debug ("IncorrectOidException");
            // create empty oid
            refOid = OID.getEmptyOid ();
        } // catch

        /*
         * try { refObj = (XMLViewer_01) vie.getObjectCache ().fetchObject
         * (refOid, vie.user, vie.sess, vie.env, false); } // try catch
         * (ObjectNotFoundException e) { //debug ("ObjectNotFoundException"); } //
         * catch catch (TypeNotFoundException e) { //debug
         * ("TypeNotFoundException"); } // catch catch
         * (ObjectClassNotFoundException e) { //debug
         * ("ObjectClassNotFoundException "); } // catch catch
         * (ObjectInitializeException e) { //debug ("ObjectInitializeException
         * "); } // catch
         */

        // instanciation of all possible node objects
        // Tag <FIELDS>
        Element fieldsNode = doc.createElement (DIConstants.ELEM_FIELDS);
        // Tag <FIELD> or <SYSFIELD>
        Element fieldNode = null;
        // value for fieldNode
        String fieldValue = null;

        for (int i = 0; vde.p_subTags != null && i < vde.p_subTags.size (); i++)
        {
            // reset fieldValue
            fieldValue = "";

            ReferencedObjectInfo fri = (ReferencedObjectInfo) vde.p_subTags
                .elementAt (i);
            // is it system field?
            if (fri.isSysField ())
            {
                fieldNode = doc.createElement (DIConstants.ELEM_SYSFIELD);
                // get the value for the sysfield
                fieldValue = DIHelpers
                    .getSysFieldValue (fri.getName (), refObj);
            } // if <SYSFIELD>
            else
            {
                fieldNode = doc.createElement (DIConstants.ELEM_FIELD);
                if (refObj == null)
                {
                    fieldValue = "";
                } // if
                else
                {
                    DataElement de = refObj.getDataElement ();
                    ValueDataElement vd = de.getValueElement (fri.getName ());
                    if (vd != null)
                    {
                        fieldValue = vd.value;
                    } // if field found
                } // else
            } // else <FIELD>
            // set NAME
            fieldNode.setAttribute (DIConstants.ATTR_NAME, fri.getName ());
            
            // retrieve the multilang info
            MlInfo mlInfo = fri.getMultilangToken (env);
            
            // set TOKEN and DESCRITPION
            fieldNode.setAttribute (DIConstants.ATTR_TOKEN, mlInfo.getName ());
            fieldNode.setAttribute (DIConstants.ATTR_DESCRIPTION, mlInfo.getDescription ());

            // add value content
            fieldNode.appendChild (doc.createTextNode (fieldValue));
            // add the field node
            fieldsNode.appendChild (fieldNode);
        } // for i

        valueNode.appendChild (fieldsNode);
    } // addFieldRefData


    /***************************************************************************
     * adds a the data for the value domain fields in view mode used for the
     * types VALUEDOMAIN
     *
     * the domtree-part for a value domain in view mode looks like this:
     * ........... <VALUE CONTEXT="Usage" FIELD="Usage" INPUT="_USAGE"
     * TYPE="VALUEDOMAIN" MULTISELECTION="NO" UNIT=""> 0x01010621000F98F0
     * <FIELDS> <SYSFIELD NAME="Name" TOKEN="Name" INPUT="_NAME"> <FIELDVALUE
     * ID="0x01010621000F98F0">[value]</FIELDVALUE> </SYSFIELD> <FIELD
     * NAME="xxx" TOKEN="xxx" INPUT="_XXX"> <FIELDVALUE
     * ID="0x01010621000F98F0">[value]</FIELDVALUE> </FIELD> </FIELDS> </VALUE>
     *
     * or if multiselection=true
     *
     * ........... <VALUE CONTEXT="Usage" FIELD="Usage" INPUT="_USAGE"
     * TYPE="VALUEDOMAIN" MULTISELECTION="NO" UNIT="">
     * 0x01010621000F98F0|0x01010621000F98F2 <FIELDS> <SYSFIELD NAME="Name"
     * TOKEN="Name" INPUT="_NAME"> <FIELDVALUE ID="0x01010621000F98F0">[value]</FIELDVALUE>
     * <FIELDVALUE ID="0x01010621000F98F2">[value]</FIELDVALUE> </SYSFIELD>
     * <FIELD NAME="xxx" TOKEN="xxx" INPUT="_XXX"> <FIELDVALUE
     * ID="0x01010621000F98F0">[value]</FIELDVALUE> <FIELDVALUE
     * ID="0x01010621000F98F2">[value]</FIELDVALUE> </FIELD> </FIELDS> </VALUE>
     *
     * @param   valueNode   Element represent current value node
     * @param   viewMode    Integer represent current mode
     * @param   vie         The data element representing the data of the value.
     * @param   app         Application info object.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    protected static void addValueDomainViewData (Element valueNode,
                                                  int viewMode,
                                                  ValueDataElement vie,
                                                  ApplicationInfo app,
                                                  SessionInfo sess, User user,
                                                  Environment env)
    {
        Document doc = valueNode.getOwnerDocument ();
        OID refOid = null; // oid of referenced Object

        // AJ remark !!!!!!! not only XMLViewer should be referenced objects
        List<XMLViewer_01> refObjList = new ArrayList<XMLViewer_01> (); // referenced
                                                                        // Object
        // AJ remark !!!!!!! not only XMLViewer should be referenced objects

        // multi selection
        boolean multiSelection = vie.multiSelection != null &&
            vie.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

        // get object of referenced oid if there is one oid
        try
        {
            if (!multiSelection)
            {            	
            	// any value set?
            	if (vie.value != null)
            	{
                    refOid = new OID (
                            // 20090911 BT/BB Hack:
                            // The incorrect 0xF0F0F0F0F0F0F0F0 can occurs within
                            // the database for value domain and fieldref fields.
                            // see IBS-254
                            vie.value.equals("0xF0F0F0F0F0F0F0F0") ? "" : vie.value);
                    
                    // check if the oid is valid:
                    if (!refOid.isEmptyInDomain ()) // oid is valid?
                    {
                        // try to get object via oid:
                        XMLViewer_01 refObj = (XMLViewer_01)
                            BOHelpers.getObject (refOid, env, false, false, false);
                        refObjList.add (refObj);
                    } // if oid is valid            		
            	} // if (vie.value != null
            	else // no value set
            	{
            		refOid = OID.getEmptyOid ();
            	} // else no value set
            } // if !multiSelection
            else
            // multiSelection
            {
                Set<String> valueSet =
                    ValueDataElementTS.getMultipleSelectionSet (vie.value);
                Iterator<String> it = valueSet.iterator ();

                while (it.hasNext ())
                {
                    String value = it.next ();
                    refOid = new OID (
                            // 20090911 BT/BB Hack:
                            // The incorrect 0xF0F0F0F0F0F0F0F0 can occurs within
                            // the database for value domain and fieldref fields.
                            // see IBS-254
                            value.equals("0xF0F0F0F0F0F0F0F0") ? "" : value);

                    // check if the oid is valid:
                    if (!refOid.isEmptyInDomain ()) // oid is valid?
                    {
                        // try to get object via oid:
                        XMLViewer_01 refObj = (XMLViewer_01) BOHelpers
                            .getObject (refOid, env, false, false, false);
                        refObjList.add (refObj);
                    } // if oid is valid
                } // while
            } // else multiSelection
        } // try
        catch (IncorrectOidException e)
        {
            // debug ("IncorrectOidException");
            // create empty oid
            refOid = OID.getEmptyOid ();
        } // catch

        // instanciation of all possible node objects
        // Tag <FIELDS>
        Element fieldsNode = doc.createElement (DIConstants.ELEM_FIELDS);
        // Tag <FIELD> or <SYSFIELD>
        Element fieldNode = null;

        for (int i = 0; vie.p_subTags != null && i < vie.p_subTags.size (); i++)
        {
            ReferencedObjectInfo fri = (ReferencedObjectInfo) vie.p_subTags
                .elementAt (i);
            // is it system field?
            if (fri.isSysField ())
            {
                fieldNode = doc.createElement (DIConstants.ELEM_SYSFIELD);

                // do multi selection handling
                for (int j = 0; j < refObjList.size (); j++)
                {
                    // get the value for the sysfield
                    String value = DIHelpers.getSysFieldValue (fri.getName (),
                        refObjList.get (j));

                    Element fieldValueNode = doc
                        .createElement (DIConstants.ELEM_FIELD_VALUE);

                    // add value content
                    fieldValueNode.appendChild (doc.createTextNode (value));

                    // set the id
                    fieldValueNode.setAttribute (DIConstants.ATTR_ID,
                        refObjList.get (j).oid.toString ());

                    // set the description to the options node
                    if (refObjList.get (j).dataElement.description != null)
                    {
                        fieldValueNode.setAttribute (
                            DIConstants.ELEM_DESCRIPTION,
                            refObjList.get (j).dataElement.description);
                    } // if description not null

                    // add the fieldValueNode
                    fieldNode.appendChild (fieldValueNode);
                } // for do multi selection handling
            } // if <SYSFIELD>
            else
            {
                fieldNode = doc.createElement (DIConstants.ELEM_FIELD);
                if (refObjList.size () != 0)
                {
                    // do multi selection handling
                    for (int j = 0; j < refObjList.size (); j++)
                    {
                        // BB TODO: a change in XMLViewer_01.getDataElement ()
                        // does
                        // only read the dataElement from the xmldata file
                        // in case the object is not in the cache
                        DataElement de = refObjList.get (j).getDataElement ();
                        ValueDataElement vd =
                            de.getValueElement (fri.getName ());
                        if (vd != null)
                        {
                            Element fieldValueNode = doc
                                .createElement (DIConstants.ELEM_FIELD_VALUE);

                            // add value content
                            fieldValueNode.appendChild (doc
                                .createTextNode (vd.value));

                            // set the id
                            fieldValueNode.setAttribute (DIConstants.ATTR_ID,
                                refObjList.get (j).oid.toString ());

                            // set the description to the options node
                            if (refObjList.get (j).dataElement.description != null)
                            {
                                fieldValueNode.setAttribute (
                                    DIConstants.ELEM_DESCRIPTION, refObjList
                                        .get (j).dataElement.description);
                            } // if description not null

                            // add the fieldValueNode
                            fieldNode.appendChild (fieldValueNode);
                        } // if field found
                    } // for do multi selection handling
                } // if
            } // else <FIELD>
            // set NAME
            fieldNode.setAttribute (DIConstants.ATTR_NAME, fri.getName ());
            
            // retrieve the multilang info
            MlInfo mlInfo = fri.getMultilangToken (env);
            
            // set TOKEN and DESCRITPION
            fieldNode.setAttribute (DIConstants.ATTR_TOKEN, mlInfo.getName ());
            fieldNode.setAttribute (DIConstants.ATTR_DESCRIPTION, mlInfo.getDescription ());

            // add the field node
            fieldsNode.appendChild (fieldNode);
        } // for i

        valueNode.appendChild (fieldsNode);
    } // addValueDomainViewData


    /**************************************************************************
     * Reads the value data from an import value. <BR/>
     *
     * @param   importValue The value which is imported.
     * @param   dataElement Data element where the value shall be imported.
     * @param   app         The global application info.
     * @param   sess        Session info object.
     * @param   user        The current user info.
     * @param   env         The current environment info.
     */
    protected static void readValueImportData (ValueDataElement importValue,
                                               DataElement dataElement,
                                               ApplicationInfo app,
                                               SessionInfo sess, User user,
                                               Environment env)
    {
        // get the corresponding value from the object dataelement
        ValueDataElement vie =
            dataElement.getValueElement (importValue.field);

        // get the type of the imported value
        String importType = importValue.type;

        // check if the value exists:
        if (vie == null)
        {
            // the value has not been found
            env.write ("<DIV ALIGN=\"LEFT\"><LI><B>" +   
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_WARNING, env) + "</B>: " +
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_INVALID_IMPORT_FIELD, env) + ": '" +
                importValue.field + "'</DIV>");
        } //  if
        // and the type is correct
        else if (importType == null || !vie.type.equalsIgnoreCase (importType))
        {
            // type setting is not correct
            env.write ("<DIV ALIGN=\"LEFT\"><LI><B>" +   
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_WARNING, env) + "</B>: " +
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_INVALID_IMPORT_FIELDTYPE, env) + ": '" +
                importType + "' <-> '" + vie.type + "'</DIV>");
        } // else if
        else    // value found
        {
            // get the necessary paths:
            String filePath = BOHelpers.getFilePath (dataElement.oid);
            String imagePath = BOHelpers.getImagePath (dataElement.oid);

            // The meta information present in the imported dataelement
            // is ignored with the exception that for values of type
            // QUERYSELECTIONBOX the options (attribute OPTIONS) are taken
            // from the import dataelement.
            // BB 20060323: for what reason? Values are read from the local DB?

            // assign the import value
            vie.value = importValue.value;

            // for values of type FILE or IMAGE the attached files must
            // be imported too.
            if (importType.equalsIgnoreCase (DIConstants.VTYPE_FILE))
            {
                // check if encoding == BASE64
                if (importValue.p_encoding != null &&
                    importValue.p_encoding.equals (DIConstants.ENCODING_BASE64))
                {
                    ValueDataElementTS.handleBase64EncodedFile (vie,
                        importValue, dataElement, filePath, env);
                } // encoding == BASE64
                else // encoding != BASE64
                {
                    // set the file we want to import
                    // ATTENTION!! this action must be performed with the IMPORTED
                    // dataelement (see class ObjectFactory)!
                    dataElement.addFile (vie.field, filePath,
                        vie.value, vie.p_size);
                } // else encoding != BASE64
                
                // a file is attached to the object, so the hasFile flag has to be set in
                // the ValueDataElement
                vie.setFileFlag(true);
                
            } // if field of type VTYPE_FILE

            else if (importType.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
            {
                // set the image file we want to import
                // ATTENTION!! this action must be performed with the IMPORTED
                // dataelement (see class ObjectFactory)!
                dataElement.addFile (vie.field, imagePath,
                    vie.value, vie.p_size);
                
                // set the hasFile flag
                vie.setFileFlag(true);
            } // else if field of type VTYPE_IMAGE

            // for value of type password
            else if (importType.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD))
            {
                // decrypt the password:
                vie.value = EncryptionManager.decrypt (vie.value);
            } // else if field of type VTYPE_PASSWORD

            // for values of type NUMBER/FLOAT/DOUBLE the number format is adjusted
            else if (importType.equalsIgnoreCase (DIConstants.VTYPE_NUMBER) ||
                     importType.equalsIgnoreCase (DIConstants.VTYPE_FLOAT) ||
                     importType.equalsIgnoreCase (DIConstants.VTYPE_DOUBLE))
            {
                // replace any "," by a "." in order to ensure a correct value
                vie.value = vie.value.replace (',', '.');
            } // else if field of type VTYPE_NUMBER or VTYPE_FLOAT or VTYPE_DOUBLE

            else if (importType.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOX) ||
                     importType.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOXINT) ||
                     importType.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOXNUM))
            {
                // for QUERYSELECTION fields with the REFRESH option not set
                // the options are taken from the imported dataelement
                if (vie.refresh != null &&
                	!vie.refresh.equals (DIConstants.ATTRVAL_ALWAYS) &&
                    !DataElement.resolveBooleanValue (vie.refresh))
                {
                    if (importValue.options != null &&
                        importValue.options.length () > 0)
                    {
                        vie.options = importValue.options;
                    } // if import options are valid
                } // if no refresh
            } // else if field of type QUERYSELECTION

            // for reference types it is possible that the reference is not
            // already resolved and has to be resolved now
            else if ((importType.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF) ||
                    importType.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF) ||
                    importType.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN)) &&
                    importValue.p_domain != null)
            {
                // get the oid with respect to the kind of reference:
                OID oid = null;
                if (importValue.p_domain.equals (DIConstants.PATH_IDDOMAIN))
                {
                    // get oid for PATH reference:
                    oid = BOHelpers.resolveObjectPath (importValue.value, null, env);
                } // if
                else
                {
                    // get oid for EXTKEY reference:
                    oid = BOHelpers.getOidByExtKey (importValue.p_domain, importValue.value, env);
                } // else

                // check if the oid was found:
                if (oid != null)
                {
                    vie.value = m2XMLFilter.getRefFieldContentFromOid (oid, importType, env).toString ();
                } // if
            } // else if field of type REFERENCE

            // for values of type REMINDER the additional attributes have
            // to be read
            else if (importType.equalsIgnoreCase (DIConstants.VTYPE_REMINDER))
            {
                // take the display type from the imported data element:
                if (importValue.p_displayType != null &&
                    importValue.p_displayType.length () > 0)
                {
                    vie.p_displayType = importValue.p_displayType;
                } // if

                // take the reminder 1 options from the imported data elem.:
                if (importValue.p_remind1Days >= 0)
                {
                    vie.p_remind1Days = importValue.p_remind1Days;
                    vie.p_remind1Recip = importValue.p_remind1Recip;
                    vie.p_remind1RecipQuery =
                        importValue.p_remind1RecipQuery;
                    vie.p_remind1Text = importValue.p_remind1Text;
                } // if

                // take the reminder 2 options from the imported data elem.:
                if (importValue.p_remind2Days >= 0)
                {
                    vie.p_remind2Days = importValue.p_remind2Days;
                    vie.p_remind2Recip = importValue.p_remind2Recip;
                    vie.p_remind2RecipQuery =
                        importValue.p_remind2RecipQuery;
                    vie.p_remind2Text = importValue.p_remind2Text;
                } // if

                // take the escalation options from the imported data elem.:
                if (importValue.p_escalateDays >= 0)
                {
                    vie.p_escalateDays = importValue.p_escalateDays;
                    vie.p_escalateRecip =
                        importValue.p_escalateRecip;
                    vie.p_escalateRecipQuery =
                        importValue.p_escalateRecipQuery;
                    vie.p_escalateText = importValue.p_escalateText;
                } // if
            } // else if field of type REMINDER
        } // if value found
    } // readImportData


    /**************************************************************************
     * Handles a value data element of type file with ENCODING="BASE64".
     *
     * @param   vie         The data element representing the data of the value.
     * @param   importValue The Value Data Element.
     * @param   dataElement Data element where the value shall be imported.
     * @param   filePath    Path for attached files.
     * @param   env         The current environment info.
     */
    private static void handleBase64EncodedFile (ValueDataElement vie,
                                                 ValueDataElement importValue,
                                                 DataElement dataElement,
                                                 String filePath,
                                                 Environment env)
    {
        // Set the extension
        String extension;

        // check if the extension is not set
        if (importValue.p_extension == null)
        {
            // check if the content type is set
            if (importValue.p_contentType == null)
            {
                extension = DIConstants.BASE64_DEF_EXTENSION;
            } // if content type is set
            else
            {
                // map the content type to the extension
                if (importValue.p_contentType
                    .equals (DIConstants.MIMETYPE_APPLICATION_PDF))
                {
                    extension = DIConstants.FILEEXTENSION_PDF;
                } // if pdf
                else
                {
                    // default extension
                    extension = DIConstants.BASE64_DEF_EXTENSION;
                } // else
            } // else
        } // if extension is not set
        else
        {
            extension = importValue.p_extension;
        } // else extension is set

        // Decode the BASE64 encoded file
        byte[] decodedData = Base64.decode (vie.value);

        // Set the filename
        String filename = (importValue.p_filename == null) ?
            DIConstants.BASE64_DEF_FILENAME : importValue.p_filename;

        if (decodedData.length == 0)
        {
            IOHelpers.showMessage (
                "XMLViewer_01.readImportData: BASE64 decoded file data is empty.",
                env);
        } // if decodedData length is 0

        // write the file
        filename = FileHelpers.writeFile (decodedData, filename, extension,
            filePath, importValue.p_filename != null);

        if (filename != null)
        {
            importValue.value = filename;
            vie.value = filename;

            dataElement.addFile (vie.field, filePath,
                filename,
                FileHelpers.getFileSize (filePath, filename));
        } // if file could be written
        else
        {
            IOHelpers.showMessage (
                "XMLViewer_01.readImportData: BASE64 encoded file could not be written.",
                env);
        } // else
    } // handleBase64EncodedFile

    
    /**************************************************************************
     * Get the oid of an value domain object with a given context and a given
     * value.
     *
     * @param vdContext the value domain context
     * @param vdValue	the value domain value    
     * @param env		the environment 
     *
     * @return the oid of the value domain in case it could have been found
     *         or null otherwise
     */
    public static OID getVDOidFromValue (String vdContext, 
    									 String vdValue,
    									 Environment env)
    {
    	    	        	        
    	Vector<BusinessObjectInfo> resultVDs = getVDFromValues (vdContext, 
    			new String [] {vdValue}, env);        
    	// TODO: Note that the result could be more then one element
    	// How shall we handle that case?
        if (resultVDs != null && resultVDs.size() > 0)
        {
        	return resultVDs.get(0).p_oid;        	
        } // if (resultVDs != null && resultVDs.size() > 0)
        else	// no result found
        {
        	return null;
        } // else no result found
    } // getVDOidFromValue


    /**************************************************************************
     * Get the value of an value domain object with a given context and a given
     * oid.
     *
     * @param vdContext the value domain context
     * @param vdOid		the oid of the value domain    
     * @param env		the environment 
     *
     * @return the oid of the value domain in case it could have been found
     *         or null otherwise
     */
    public static String getVDValueFromOid (String vdContext, 
    									 	OID vdOid,
    									 	Environment env)
    {
    	    	        	        
    	Vector<BusinessObjectInfo> resultVDs = getVDFromOids (vdContext,     	
    			new String [] {vdOid.toString()}, env);        
    	// TODO: Note that the result could be more then one element
    	// How shall we handle that case?
        if (resultVDs != null && resultVDs.size() > 0)
        {
        	return resultVDs.get(0).p_name;        	
        } // if (resultVDs != null && resultVDs.size() > 0)
        else	// no result found
        {
        	return null;
        } // else no result found
    } // getVDValueFromOid
    
    
    /**************************************************************************
     * Get the value domain objects with a given context 
     * and matching a string array containing the value(=name) 
     * of the value domains.
     *
     * @param vdContext the value domain context
     * @param vdValues	the array containing the oids to resolve
     * @param env		the environment 
     *
     * @return value domain objects as vector of BusinessObjectInfo or 
     * 		   <code>null</code> otherwise
     */
    public static Vector<BusinessObjectInfo> getVDFromValues (String vdContext, 
    										  				  String [] vdValues,
    										  				  Environment env)
    {
        // CONTRAINTS: input values must not be null
        if (vdContext == null || vdValues == null || 
        	vdValues == null || vdValues.length == 0)
        {
        	return null;
        } // if (vdContext == null || vdValue == null)
        	
        String nameFilter = StringHelpers.stringArrayToString (vdValues, 
        		new StringBuffer (","), new StringBuffer ("'"));        
        
        // create the SQL String to select the value domain
        SelectQuery query = new SelectQuery (        
	         new StringBuilder()
	         .append (" ovdelem.oid, ovdelem.name, ovdelem.typename,")
	         .append (" ovdelem.state, ovdelem.description"),
             new StringBuilder ()
	            .append (" ibs_Object ovdelem, dbm_valuedomainelem vdelem, ")
	            .append (" ibs_Object ovd "),
            new StringBuilder ()
	            .append (" ovdelem.oid = vdelem.oid ")
	            // value domain element
	            .append (" AND    ovdelem.state = ") .append (States.ST_ACTIVE)
	            // value domain ("the context")
	            .append (" AND    ovdelem.containerId = ovd.oid ") 
	            .append (" AND    ovd.name = '").append (vdContext).append ("'"),
            null, null, null);
        
        // add the oid filter
        query.extendWhere (SQLHelpers.createQueryFilter ("ovdelem.name", 
        		new StringBuilder (nameFilter)));

    	return BOHelpers.findObjects (query, env);        
    } // getVDFromValues

    
    /**************************************************************************
     * Get the value domain objects with a given context 
     * and matching a string array containing the OIDs of the value domains.
     *
     * @param vdContext the value domain context
     * @param vdOids	the array containing the oids to resolve
     * @param env		the environment 
     *
     * @return value domain objects as vector of BusinessObjectInfo or 
     * 		   <code>null</code> otherwise
     */
    public static Vector<BusinessObjectInfo> getVDFromOids (String vdContext, 
    										  				String [] vdOids,
    										  				Environment env)
    {
        // CONTRAINTS: input values must not be null
        if (vdContext == null || vdOids == null || 
        	vdOids == null || vdOids.length == 0)
        {
        	return null;
        } // if (vdContext == null || vdValue == null)
        	
        String oidFilter = StringHelpers.stringArrayToString (vdOids, 
        		new StringBuffer (","), null);        
        
        // create the SQL String to select the value domain
        SelectQuery query = new SelectQuery (        
	         new StringBuilder()
	         .append (" ovdelem.oid, ovdelem.name, ovdelem.typename,")
	         .append (" ovdelem.state, ovdelem.description"),
             new StringBuilder ()
	            .append (" ibs_Object ovdelem, dbm_valuedomainelem vdelem, ")
	            .append (" ibs_Object ovd "),
            new StringBuilder ()
	            .append (" ovdelem.oid = vdelem.oid ")
	            // value domain element
	            .append (" AND    ovdelem.state = ") .append (States.ST_ACTIVE)
	            // value domain ("the context")
	            .append (" AND    ovdelem.containerId = ovd.oid ") 
	            .append (" AND    ovd.name = '").append (vdContext).append ("'"),
            null, null, null);
        // add the oid filter
        query.extendWhere (SQLHelpers.createQueryFilter ("ovdelem.oid", 
        		new StringBuilder (oidFilter)));

    	return BOHelpers.findObjects (query, env);       
    } // getVDFromOids
    

    /**************************************************************************
     * Get the string for a value domain containing the oids of the value domain
     * separated by the BOConstants.MULTISELECTION_VALUE_SEPARATOR. 
     *
     * @param vdOids	the array containing the value domain oids
     *
     * @return the string with the value domain oids 
     * 		   or <code>null</code> otherwise
     */
    public static String getVDStringFromOids (String [] vdOids)
    {
    	return StringHelpers.stringArrayToString(vdOids, BOConstants.MULTISELECTION_VALUE_SAPERATOR);    
    } // getVDStringFromOids

    
    /**************************************************************************
     * Get the string for a value domain containing the oids of the value domain
     * separated by the BOConstants.MULTISELECTION_VALUE_SEPARATOR. 
     *
     * @param vdContext the value domain context
     * @param vdValues	the array containing the oids to resolve
     * @param env		the environment 
     *
     * @return the string with the value domain oids 
     * 		   or <code>null</code> otherwise
     */
    public static String getVDStringFromValues (String vdContext, 
												String [] vdValues,
												Environment env)
    {
    	Vector<BusinessObjectInfo> resultVDs = 
    		getVDFromValues (vdContext, vdValues, env);        
    	// TODO: Note that the result could be more then one element
    	// How shall we handle that case?
        if (resultVDs != null && resultVDs.size() > 0)
        {
        	StringBuffer result = new StringBuffer();
        	StringBuffer separator = 
        		new StringBuffer(BOConstants.MULTISELECTION_VALUE_SAPERATOR);
        	BusinessObjectInfo valueDomain;
        	Iterator<BusinessObjectInfo> iter = resultVDs.iterator();
            while (iter.hasNext ())
            {
            	valueDomain = iter.next ();            	
            	result.append (valueDomain.p_oid.toString());
            	// append the separator?
            	if (iter.hasNext ())
            	{
            		result.append (separator);
            	} // if (iter.hasNext ())
            } // while        	
        	return result.toString ();        	
        } // if (resultVDs != null && resultVDs.size() > 0)
        else	// no result found
        {
        	return null;
        } // else no result found
    } // getVDStringFromValues


    /***************************************************************************
     * Retrieves the multilang info (name, description) for the provided
     * reference field and adds it to the field.
     * 
     * @param typeCode  The type code of the type containing the field
     * @param vde       The value data element holding the reference field  
     * @param locales   The locales to retrieve the multilang info for
     * @param env       The environment
     */
    public static void addRefFieldMlInfo (String typeCode, ValueDataElement vde,
            Collection<Locale_01> locales, Environment env)
    {
        // check if the field has no subtags
        if (vde.p_subTags == null || vde.p_subTags.size () <= 0)
        {
            return;
        } // if
        
        // retrieve the base lookup key for value data elements
        String vdeBaseLookupKey = MultilingualTextProvider.
                getFormtemplateVdeBaseLookupKey (typeCode, vde.field);
        
        // iterate through the subtags
        Iterator<?> it = vde.p_subTags.iterator ();
        while (it.hasNext ())
        {
            // retrieve the next subtag
            Object subtagI = (Object) it.next ();
            
            // check if the object is of type ReferencedObjectInfo
            if (subtagI instanceof ReferencedObjectInfo)
            {
                ReferencedObjectInfo refInfo = (ReferencedObjectInfo) subtagI;
                
                String refTypePrefix = null;

                // (1) Generic lookup:
                // retrieve the lookup for fieldref fields
                if (vde.type.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF))
                {
                    refTypePrefix = MultilangConstants.LOOKUP_KEY_FIELDREF_PREFIX;
                } // if
                else if (vde.type.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN))
                {
                    refTypePrefix = MultilangConstants.LOOKUP_KEY_VALUEDOMAIN_PREFIX;
                } // else
                               
                // retrieve the lookup key
                String baseLookupKey = MultilingualTextProvider.
                    getRefFieldLookupKey (vdeBaseLookupKey, refTypePrefix,
                            refInfo.isSysField (), refInfo.getName ());
                
                // retrieve the ml tokens
                boolean completed = refInfo.setMlTokens (baseLookupKey, locales, env);
                
                // not completed?
                if (!completed)
                {
                    // (2) Use token
                    if (refInfo.getToken () != null && !refInfo.getToken ().isEmpty ())
                    {
                        refInfo.setMultilangTokenForLocales (new MlInfo (refInfo.getToken ()), locales, env);
                        
                        completed = true;
                    } // if
                    // (3) Use referenced field info - only possible for value domains
                    // For fieldref no info about the referenced type available.
                    // The reference is determined by the query at the moment.
                    else if (!refInfo.isSysField () &&
                            vde.type.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN))
                    {
                        // retrieve the value domain type
                        Type valueDomainType = ((ObjectPool) env.getApplicationInfo ().cache).
                            getTypeContainer ().findType (TypeConstants.TC_ValueDomainElement);
                        
                        // retrieve the multilang field info from the value domain
                        Map<String, MlInfo> mlInfos = valueDomainType.getMultilangFieldInfo (refInfo.getName ());
                        
                        // check if something has been found
                        if (mlInfos != null)
                        {
                            // link the referenced object info ML names to
                            // the Value Data Element Names 
                            refInfo.setMultilangTokens (mlInfos);
                            
                            completed = true;
                        } // if
                    } // else

                    // (4) Use name
                    if (!completed)
                    {
                        // set the multilang token
                        refInfo.setMultilangTokenForLocales (null, locales, env);
                    } // else
                } // if
            } // if
        } // while
    } // addRefFieldMlInfo
    
    
    /**
     * Fills the options data for query selection boxes and value domain fields.
     *
     * @param xmlViewer The xmlViewer object.
     * @param vie       The value data element.
     */
    public static void fillOptionsData (XMLViewer_01 xmlViewer, ValueDataElement vie)
    {
        // check if we the object has CREATED state
        // in that case the query fields shall not be evaluated
        // because it has already been done within the
        // performSetFormDefaults () method
        // else refresh the options for a queryselectionbox
        if (xmlViewer.state != States.ST_CREATED &&
            vie.type.startsWith (DIConstants.VTYPE_QUERYSELECTIONBOX))
        {                   
            // refresh the options attribute
            if (vie.refresh == null ||
                vie.refresh.equals (DIConstants.ATTRVAL_ALWAYS) ||
                DataElement.resolveBooleanValue (vie.refresh))
            {
                xmlViewer.fillOptionsWithQueryData (vie);
            } // if

        } // if

        // check if we the object has CREATED state
        // in that case the query fields shall not be evaluated
        // because it has already been done within the
        // performSetFormDefaults () method
        // else refresh the options for a value domain selection box
        if (xmlViewer.state != States.ST_CREATED &&
            vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
        {
            // refresh the values attribute
//            TODO: VALUEDOMAIN - Decide which caching handling to use!
            //                    if (vie.refresh.equals (DIConstants.ATTRVAL_ALWAYS) ||
//                DataElement.resolveBooleanValue (vie.refresh))
//            {
            xmlViewer.fillValueDomainOptionsWithQueryData (vie);
//            } // if
        } // if
    } // addOptionsData

    /**************************************************************************
     * Get data for the given linkfields out of the result set. <BR/>
     *
     * @param   linkFields      Set of linkfields which should be checked.
     * @param   action          The action for the database connection.
     * @param   env             The current environment. 
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    static public Vector<ValueDataElement> getAllDBValues ( 
        Vector<ValueDataElement> linkFields, SQLAction action, Environment env)
        throws DBError
    {
        Vector<ValueDataElement> values = new Vector<ValueDataElement>();

        Iterator<ValueDataElement> linkFieldIter = linkFields.iterator ();
        while (linkFieldIter.hasNext ())
        {
            ValueDataElement linkField = linkFieldIter.next ();
            values.add (getDBValue(action, linkField, env));
        } // while
        
        return values;
    } // getAllDBValues

    
    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result out of DB. <BR/>
     *
     * @param   action          The action for the database connection.
     * @param   linkField       Name of the linkfield for which the data should
     *                          be extracted out of the result set.
     * @param   env             The current environment                          
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    static public ValueDataElement getDBValue (SQLAction action, 
        ValueDataElement linkField, Environment env)
        throws DBError
    {
        // create a new ValueDataElement and copy the necessary data
        ValueDataElement value = new ValueDataElement (linkField);
        
        // The following part is an excerpt from XMLContainer_01.getContainerElementDataDB 
        boolean multiSelection = value.multiSelection != null &&
            value.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

        // DB - STRINGS
        if (DIConstants.VTYPE_CHAR.equals (value.type) ||
            DIConstants.VTYPE_FILE.equals (value.type) ||
            DIConstants.VTYPE_URL.equals (value.type) ||
            DIConstants.VTYPE_EMAIL.equals (value.type) ||
            DIConstants.VTYPE_IMAGE.equals (value.type) ||
            DIConstants.VTYPE_OBJECTREF.equals (value.type) ||
            DIConstants.VTYPE_QUERYSELECTIONBOX.equals (value.type) ||
            DIConstants.VTYPE_SELECTIONBOX.equals (value.type) ||
            DIConstants.VTYPE_LONGTEXT.equals (value.type) ||
            DIConstants.VTYPE_TEXT.equals (value.type) ||
            (DIConstants.VTYPE_VALUEDOMAIN.startsWith (value.type) && multiSelection))
        {
            value.value = SQLHelpers.dbToAscii (
                action.getString (value.mappingField));
        } // if
        // DB - BOOLEAN
        else if (DIConstants.VTYPE_BOOLEAN.equals (value.type))
        {
            value.value = (action.getBoolean (value.mappingField)) ?
                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_BOOLTRUE, env) :
                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                    AppMessages.ML_MSG_BOOLFALSE, env);
        } // else if
        // DATE
        else if (DIConstants.VTYPE_DATE.equals (value.type))
        {
            // get value for this column in current line of resultset
            Date d = action.getDate (value.mappingField);
            // check if there is a value set for this column
            if (action.wasNull () || d == null)
            {
                value.value = "";
            } // if
            else
            {
                // convert date to string
                value.value = DateTimeHelpers.dateToString (d);
            } // else
        } // else if
        else if (DIConstants.VTYPE_DATETIME.equals (value.type))
        {
            // get value for this column in current line of resultset
            Date d = action.getDate (value.mappingField);
            // check if there is a value set for this column
            if (action.wasNull () || d == null)
            {
                value.value = "";
            } // if
            else
            {
                // convert date to string
                value.value = DateTimeHelpers.dateTimeToString (d);
            } // else
        } // else if
        else if (DIConstants.VTYPE_TIME.equals (value.type))
        {
            // get value for this column in current line of resultset
            Date d = action.getDate (value.mappingField);
            // check if there is a value set for this column
            if (action.wasNull () || d == null)
            {
                value.value = "";
            } // if
            else
            {
                // convert date to string
                value.value = DateTimeHelpers.timeToString (d);
            } // else
        } // else if
        // INTEGER
        else if (DIConstants.VTYPE_INT.equals (value.type) ||
            DIConstants.VTYPE_QUERYSELECTIONBOXINT.equals (value.type) ||
            DIConstants.VTYPE_SELECTIONBOXINT.equals (value.type))
        {
            value.value = "" + action.getInt (value.mappingField);
        } // else if
        // DB-NUMBER
        else if (DIConstants.VTYPE_FLOAT.equals (value.type) ||
            DIConstants.VTYPE_DOUBLE.equals (value.type) ||
            DIConstants.VTYPE_NUMBER.equals (value.type) ||
            DIConstants.VTYPE_QUERYSELECTIONBOXNUM.equals (value.type))

        {
            Float fValue = new Float (
                action.getFloat (value.mappingField));

            value.value = fValue.toString ();
        } // DB-NUMBER
        // MONEY
        else if (DIConstants.VTYPE_MONEY.equals (value.type))
        {
            long money = action.getCurrency (value.mappingField);
            // check if there is a value set for this column
            if (action.wasNull ())
            {
                value.value = "";
            } // if
            else
            {
                // convert money to string
                value.value = Helpers.moneyToString (money);
            } // else
        } // else if
        else if (DIConstants.VTYPE_FIELDREF.equals (value.type) ||
                (DIConstants.VTYPE_VALUEDOMAIN.startsWith (value.type) && !multiSelection))
        {
            // get value for this column in current line of resultset
            OID localObjId =
                SQLHelpers.getQuOidValue (action, value.mappingField);

            // check if there is a value set for this column
            if (action.wasNull () || localObjId == null ||
                localObjId.isEmpty ())
            {
                value.value = "";
            } // if
            else
            {
                // convert oid and value of fieldref to string
                value.value = "" + localObjId +
                    DIConstants.OPTION_DELIMITER +
                    action.getString (
                        value.mappingField + "_VALUE");
            } // else
        } // else if
        // REMINDER
        else if (DIConstants.VTYPE_REMINDER.equals (value.type))
        {
            // get value for this column in current line of resultset
            Date d = action.getDate (value.mappingField);
            // check if there is a value set for this column
            if (action.wasNull () || d == null)
            {
                value.value = "";
            } // if
            else
            {
                // convert date to string:
                value.value = DateTimeHelpers.dateToString (d);
            } // else
        } // else if
        else
        {
            value.value = "type [" + value.type + "] not possible";
        } // else

        return value;
    } // getDBValue

} // class ValueDataElementTS