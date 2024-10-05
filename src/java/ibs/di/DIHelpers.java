/*
 * Class: DIHelpers.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.type.Type;
import ibs.bo.type.TypeContainer;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.filter.Filter;
import ibs.di.filter.m2XMLFilter;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBQueryException;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * The Helpers classes include some useful methods that are used by other
 * data interchange classes.
 *
 * @version     $Id: DIHelpers.java,v 1.49 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990308
 ******************************************************************************
 */
public abstract class DIHelpers
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIHelpers.java,v 1.49 2013/01/18 10:38:17 rburgermann Exp $";


    /**
     * String containing the valid characters from field names in forms.
     */
    private static final String VALIDCHARS =
        "_ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a selection box with a list of files from a certain
     * path using the XML file filter. <BR/>
     *
     * @param   fieldName       name of selection box
     * @param   filePath        path to look for xml files
     * @param   activeName      name of file to mark as active is applicable
     * @param   addEmptyOption  flag to add an empty option at the beginning
     * @param   multipleAllowed flag to allow multiple selection box.
     * @param   evn             The current environment
     *
     * @return  A GroupElement object that holds the SelectionElement object
     *          with the Selection box ot a TextElement when no files could
     *          have been found.
     */
    public static final GroupElement createFileSelectionBox (
                                                             String fieldName,
                                                             String filePath,
                                                             String activeName,
                                                             boolean addEmptyOption,
                                                             boolean multipleAllowed,
                                                             Environment env)
    {
        SelectElement sel;
        String[] filenames;
        GroupElement gel = new GroupElement ();
        int length;

        // get the filenames from the directory
        filenames = FileHelpers.getFilesArray (filePath, new XMLFileFilter ());
        // check if we got the files
        if (filenames != null)
        {
            length = filenames.length;
            if (length > 0)
            {
                // create the selection box:
                sel = new SelectElement (fieldName, multipleAllowed);
                // set number of lines to be displayed in the selection box
                sel.size = 5;
                // go through the filenames and add them as option to the selection list
                for (int i = 0; i < length; i++)
                {
                    // check whether to add an empty option
                    if (addEmptyOption && i == 0)
                    {
                        sel.addOption ("", "");
                    } // if

                    // check if to set the option activated
                    if (activeName != null && activeName.equals (filenames[i]))
                    {
                        sel.addOption (filenames[i], filenames[i], true);
                    } // if (activeName != null && activeName.equals (filenames[i]))
                    else    // option not selected
                    {
                        sel.addOption (filenames[i], filenames[i]);
                    } // else option not selected
                } // for (int i = 0; i < length; i++)
                gel.addElement (sel);
            } // if (lenght > 0)
            else    // no files found
            {
                gel.addElement (new TextElement (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOFILESFOUND, env)));
            } // else no files found
        } // if (dir.isDirectory ())
        else // path is not valid
        {
            gel.addElement (new TextElement (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALIDPATH, env) + filePath));
        } // else path is not valid
        return gel;
    } // createFileSelectionBox


    /**************************************************************************
     * create an selection box with a list of importfiles. <BR/>
     *
     * @param   fieldName       name of selection box
     * @param   fileNames       an array containing the filenames
     * @param   activeName      name of file to mark as active is applicable
     * @param   addEmptyOption  flag to add an empty option at the beginning
     * @param   multipleAllowed flag to allow multiple selection box.
     * @param   evn             The current Environment
     *
     * @return  A GroupElement object that holds the SelectionElement object
     *          with the Selection box ot a TextElement when no files could
     *          have been found.
     */
    public static final GroupElement createFileSelectionBox (
                                                             String fieldName,
                                                             String[] fileNames,
                                                             String activeName,
                                                             boolean addEmptyOption,
                                                             boolean multipleAllowed,
                                                             Environment env)
    {
        SelectElement sel;
        GroupElement gel = new GroupElement ();
        int length;

        // check if we got the files
        if (fileNames != null)
        {
            length = fileNames.length;
            if (length > 0)
            {
                // create the selection box
                sel = new SelectElement (fieldName, multipleAllowed);
                // set number of lines to be displayed in the selection box
                sel.size = 5;
                // go through the filenames and add them as option to the selection list
                for (int i = 0; i < length; i++)
                {
                    // check whether to add an empty option
                    if (addEmptyOption && i == 0)
                    {
                        sel.addOption ("", "");
                    } // if

                    // check if to set the option activated
                    if (activeName != null && activeName.equals (fileNames[i]))
                    {
                        sel.addOption (fileNames[i], fileNames[i], true);
                    } //if
                    else
                    {
                        sel.addOption (fileNames[i], fileNames[i]);
                    } // else
                }  // for
                gel.addElement (sel);
            } // if (lenght > 0)
            else    // no files found
            {
                gel.addElement (new TextElement ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOFILESFOUND, env)));
            } // else no files found
        } //if (dir.isDirectory ())
        else // path is not valid
        {
            gel.addElement (new TextElement ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NOFILESFOUND, env)));
        } // else path is not valid
        return gel;
    } // createFileSelectionBox


    /***************************************************************************
     * Returns the given value list as
     * <code>MULTISELECTION_VALUE_SAPERATOR</code> seperated string.
     *
     * @param valueList valueList which should be returned as seperated string
     * @return <code>MULTISELECTION_VALUE_SAPERATOR</code> seperated string
     */
    public static final String getMultipleSelectionValue (String[] valueList)
    {
        if (valueList == null)
        {
            return null;
        } // if

        // set the value by concatenating the valueList if the field is a multi
        // selection field
        StringBuilder concatenatedString = new StringBuilder (8000);

        for (int i = 0; i < valueList.length; i++)
        {
            concatenatedString.append (valueList[i]);
            if (i != (valueList.length - 1))
            {
                concatenatedString
                    .append (BOConstants.MULTISELECTION_VALUE_SAPERATOR);
            } // if
        } // for i

        return concatenatedString.toString ();
    } // getMultipleSelectionValue


    /**************************************************************************
     * Returns the sub tags of the template.
     *
     * @param   template    The template in which to search for the field's
     *                      sub tags.
     * @param   field       The name of the field in the template.
     *
     * @return  A vector which contains the subTags of the field or
     *          <CODE>null</CODE> otherwise.
     */
    public static final Vector<?> getTemplateSubTags (DocumentTemplate_01 template,
                                          String field)
    {
        DataElement templateDataElement = null;
        ValueDataElement valueDataElement = null;

        if (template != null)
        {
            templateDataElement = template.getTemplateDataElement ();
            if (templateDataElement != null)
            {
                valueDataElement = templateDataElement.getValueElement (field);
                if (valueDataElement != null)
                {
                    return valueDataElement.p_subTags;
                } // if (valueDataElement != null)
            } // if (templateDataElement != null)
        } // if (template != null)

        // return null to indicate that we did not find any data
        return null;
    } // getTemplateSubTags


    /**************************************************************************
     * Find the templates for a list of object type codes. <BR/>
     *
     * @param   typeCache       The type cache in which to search for the types.
     * @param   typeCodeList    A comma-separated list of type codes for which
     *                          the templates shall be found.
     *
     * @return  The resulting templates.
     *          If no templates were found the vector is empty.
     *          If for any type no template is defined the corresponding
     *          position in the vector is <CODE>null</CODE>.
     *
     * @throws  TypeNotFoundException
     *          One of the defined types was not found.
     */
    @SuppressWarnings("unchecked")
    protected static Vector<DocumentTemplate_01> findTemplates (
                                                                TypeContainer typeCache,
                                                                String typeCodeList)
        throws TypeNotFoundException
    {
        // find the templates and return the result:
        return (Vector) typeCache.findTemplates (typeCodeList);
    } // findTemplates


    /**************************************************************************
     * Replaces all critical characters from a string. This has to be done for
     * all field names to be used in online forms. The string itself will be
     * converted to uppercase and a '_' will be added to the beginning of the
     * string. <BR/>
     *
     * @param   str     The string to be transformed.
     *
     * @return  The string without the critical characters.
     */
    public static final String replaceCriticalCharacters (String str)
    {
        if (str == null)
        {
            return null;
        } // if

        // the result string starts with a '_'
        StringBuilder s = new StringBuilder ("_");
        // convert it to uppercase
        s.append (str.toUpperCase ());

        // replace all critical characters by a '_'
        int len = s.length ();
        for (int i = 0; i < len; i++)
        {
            char c = s.charAt (i);
            switch (c)
            {
                case 'Ö':
                    s.setCharAt (i, 'O');
                    break;
                case 'Ä':
                    s.setCharAt (i, 'A');
                    break;
                case 'Ü':
                    s.setCharAt (i, 'U');
                    break;
                case 'ß':
                    s.setCharAt (i, 'S');
                    break;
                default:
                    // replace non invalid characters with 'X'
                    if (DIHelpers.VALIDCHARS.indexOf (c) < 0)
                    {
                        s.setCharAt (i, 'X');
                    } // if (p_validChars.indexOf (c) < 0)
            } // switch (c)
        } // for i
        return s.toString ();
    } // replaceCriticalCharacters


    /***************************************************************************
     * This method transforms a string into a valid argument that can be used in
     * a form. <BR/>
     *
     * @param field The namme of a field to be transformed into an argument.
     *
     * @return A valid argument.
     */
    public static final String createArgument (String field)
    {
        // replace all characters that could be critical when used in a form
        return DIHelpers.replaceCriticalCharacters (field);
    } // ceateArgument


    /**************************************************************************
     * method description. <BR/>
     *
     * @param   path    ???
     *
     * @return  ???
     */
    public static String getOidFromPath (String path)
    {
        return DIHelpers.getLastToken (path, "/\\");
    } // getOidFromPath


    /**************************************************************************
     * Gets all tokens in from a string where the tokens are separated
     * by a certain delimiter. <BR/>
     *
     * @param   strList     The string to get the tokens from.
     * @param   delimiter   The delimiter.
     *
     * @return  An array of strings containing the tokens.
     */
    public static String[] getTokens (String strList, String delimiter)
    {
        if (strList == null || strList.length () == 0)
        {
            return null;
        } // if (strList == null)

        StringTokenizer tokenizer = new StringTokenizer (strList, delimiter);
        int size = tokenizer.countTokens ();
        String[] tokenList = new String[size];
        int i = 0;
        try
        {
            while (tokenizer.hasMoreTokens ())
            {
                tokenList[i++] = tokenizer.nextToken ();
            } // while (tokenizer.hasMoreTokens ())
        } // try
        catch (NoSuchElementException e)
        {
            return tokenList;
        } // catch

        return tokenList;
    } // getTokens


    /**************************************************************************
     * This method gets the first token in a list of string tokens.
     * The strings must be separated by a certain delimiter. <BR/>
     *
     * @param strList       a string containing the list of values
     * @param delimiter     the delimiter
     *
     * @return the first string in the tokenlist or null if the list is empty
     */
    public static String getFirstToken (String strList, String delimiter)
    {
        if (strList == null)
        {
            return null;
        } // if (strList == null)

        StringTokenizer tokenizer = new StringTokenizer (strList, delimiter);
        try
        {
            while (tokenizer.hasMoreTokens ())
            {
                return tokenizer.nextToken ();
            } // while (tokenizer.hasMoreTokens ())
        } // try
        catch (NoSuchElementException e)
        {
            return null;
        } // catch

        return null;
    } // getFirstToken


    /**************************************************************************
     * This method gets the last token in a list of string tokens.
     * The strings must be separated by a certain delimiter. <BR/>
     *
     * @param   strList     A string containing the list of values.
     * @param   delimiter   The delimiter.
     *
     * @return  The last string in the tokenlist or "" if the list is empty.
     */
    public static String getLastToken (String strList, String delimiter)
    {
        String[] tokens;

        tokens = DIHelpers.getTokens (strList, delimiter);
        if (tokens != null && tokens.length > 0)
        {
            return tokens[tokens.length - 1];
        } // if

        return null;
    } // getLastToken


    /**************************************************************************
     * The methods constructs a token string. A tokens string is composed by
     * a set of strings separated by a specific delimiter. <BR/>
     *
     * @param tokens                an array of tokens to construct the string from
     * @param delimiter             the delimiter
     * @param addDelimAtBeginning   flag to add the delimiter at the beginning
     * @param addDelimAtEnd         flag to add the delimiter at the end
     *
     * @return  The token string or null if there have been no tokens to construct
     *          the string from.
     *
     * @deprecated  This method seems to be never used.
     */
    @Deprecated
    public static String tokensToString (String[] tokens, String delimiter,
                                         boolean addDelimAtBeginning,
                                         boolean addDelimAtEnd)
    {
        String str = "";

        // check if tokens are null
        if (tokens == null)
        {
            return null;
        } // if

        // check if we need to add the delimiter at the beginning
        if (addDelimAtBeginning)
        {
            str += delimiter;
        } // if

        // loop through the tokens and add it to the output string
        for (int i = 0; i < tokens.length; i++)
        {
            str += tokens[i];
            // check if we already reached the end of the tokens
            if (i < (tokens.length - 1))
            {
                str += delimiter;
            } // if (i < strings.lenght - 1)
            else // reached end of tokens
            {
                // check if we have to add a delimiter at the end
                if (addDelimAtEnd)
                {
                    str += delimiter;
                } // if (addDelimAtEnd)
            } // reached end of tokens
        } // for (int i = 0; i < strings.length ; i++)
        return str;
    } // tokensToString


    /**************************************************************************
     * This method sets a specific token from its origin location
     * at the beginning of a list of tokens.
     * The tokens are separated by delimiter. <BR/>
     *
     * @param   strValue    the string value to set at the beginning of the list
     * @param   tokenList   a string containing the list of values
     * @param   delimiter   the delimiter character the token list is separated with
     *
     * @return a string with the switched token list
     */
    public static String switchFirstToken (String strValue, String tokenList,
                                           String delimiter)
    {
        StringTokenizer tokenizer = new StringTokenizer (tokenList, delimiter);
        String token;
        String newList = strValue;

        try
        {
            while (tokenizer.hasMoreTokens ())
            {
                token = tokenizer.nextToken ();
                if (!strValue.equals (token))
                {
                    newList += delimiter + token;
                } // if
            } // while (tokenizer.hasMoreTokens ())
        } // try
        catch (NoSuchElementException e)
        {
            return newList;
        } // catch

        return newList;
    } // switchFirstToken


    /**************************************************************************
     * Creates a formatted date string. This method is used mainly by classes
     * that need to add a date to the filename. <BR/>
     *
     * @return a formatted date string (YYYY-MM-DD)
     */
    public static String getDateString ()
    {
        Date date = DateTimeHelpers.getCurAbsDate ();
        String dateStr = DateTimeHelpers.dateToString (date);

        dateStr = StringHelpers.replace (dateStr, ".", "-");
        return dateStr;
    }   // getDateString


    /**************************************************************************
     * Creates an selection box with all filters available. <BR/>
     *
     * @param   fieldName       name of selection box
     * @param   activeFilterId  if of the filter to be marked selected
     *
     * @return  A GroupElement object that holds a SelectionElement object with
     *          the filter selection box.
     */
/*
    public static GroupElement createFilterSelectionBox
        (String fieldName, int activeFilterId)
    {
        SelectElement sel;
        GroupElement gel = new GroupElement ();

        // create the selection box
        sel = new SelectElement (fieldName, false);
        // set number of lines to be displayed in the selection box
        sel.size = 1;
        // go through the filenames and add them as option to the selection list
        for (int i = 0; i < DIConstants.FILTER_NAMES.length; i++)
        {
            // check if to set the option activated
            if (i == activeFilterId)
            {
                sel.addOption (DIConstants.FILTER_NAMES[i], "" + i, true);
            } //if
            else
            {
                sel.addOption (DIConstants.FILTER_NAMES[i], "" + i);
            } //else
        }  //for
        gel.addElement (sel);
        return gel;
    } // createFilterSelectionBox
*/


    /**************************************************************************
     * Creates an selection box with all import filters available. <BR/>
     *
     * @param   fieldName       name of selection box
     * @param   activeFilterId  id of the filter to be marked selected
     * @param   isAddEmptyOption Shall the empty option be added to the
     *                          selection box?
     * @param   env             The current environment
     *
     * @return  A GroupElement object that holds a SelectionElement object with
     *          the filter selection box.
     */
    public static GroupElement createImportFilterSelectionBox (
                                                               String fieldName,
                                                               int activeFilterId,
                                                               boolean isAddEmptyOption,
                                                               Environment env)
    {
        SelectElement sel;
        GroupElement group = new GroupElement ();

        // create the selection box
        sel = new SelectElement (fieldName, false);
        // set number of lines to be displayed in the selection box
        sel.size = 1;

        // check if we have to add an empty option
        if (isAddEmptyOption)
        {
            sel.addOption ("", "" + DIConstants.FILTER_NOFILTERID);
        } // if (isAddEmptyOption)

        // go through the filternames and add them as option to the
        // selection list
        for (int i = 0; i < DIConstants.IMPORTFILTER_NAMES.length; i++)
        {
            // check if this is the preselected option
            if (i == activeFilterId)
            {
                sel.addOption (
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DIConstants.IMPORTFILTER_NAMES[i], env),
                    "" + i, true);
            } // if (i == activeFilterId)
            else    // not preselected
            {
                sel.addOption (
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DIConstants.IMPORTFILTER_NAMES[i], env),
                    "" + i);
            } // else not preselected
        } // for (int i = 0; i < DIConstants.IMPORTFILTER_NAMES.length; i++)
        group.addElement (sel);

        return group;
    } // createImportFilterSelectionBox


    /**************************************************************************
     * Creates an selection box with all export filters available. <BR/>
     *
     * @param   fieldName           Name of selection box.
     * @param   activeFilterId      Id of the filter to be marked selected.
     * @param   isAddEmptyOption    <CODE>true</CODE> if an empty option shall
     *                              be added to the selection box.
     * @param   env                 The current environment
     *
     * @return  A GroupElement object that holds a SelectionElement object with
     *          the filter selection box.
     */
    public static GroupElement createExportFilterSelectionBox (
                                                               String fieldName,
                                                               int activeFilterId,
                                                               boolean isAddEmptyOption,
                                                               Environment env)
    {
        SelectElement sel;
        GroupElement group = new GroupElement ();

        // create the selection box
        sel = new SelectElement (fieldName, false);
        // set number of lines to be displayed in the selection box
        sel.size = 1;
        // check if we have to add an empty option
        if (isAddEmptyOption)
        {
            sel.addOption ("", "" + DIConstants.FILTER_NOFILTERID);
        } // if (isAddEmptyOption)
        // go through the filternames and add them as option to the selection list
        for (int i = 0; i < DIConstants.EXPORTFILTER_NAMES.length; i++)
        {
            // check if this is the preselected option
            if (i == activeFilterId)
            {
                sel.addOption (
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DIConstants.EXPORTFILTER_NAMES[i], env), 
                    "" + i, true);
            } // if (i == activeFilterId)
            else    // not preselected
            {
                sel.addOption (
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DIConstants.EXPORTFILTER_NAMES[i], env), 
                    "" + i);
            } // else not preselected
        } // for (int i = 0; i < DIConstants.EXPORTFILTER_NAMES.length; i++)
        group.addElement (sel);
        return group;
    } // createExportFilterSelectionBox


    /**************************************************************************
     * This method create the filter with will be used for the import.
     *
     * @param   filterId    Id of the filter with will be used for the Import
     * @param   env         The current environment
     *
     * @return  The filter.
     */
    public static Filter getImportFilter (int filterId, Environment env)
    {
        Filter filter = null;
        // check if the filter id is valid
        if (filterId < 0 || filterId >= DIConstants.IMPORTFILTER_CLASSES.length)
        {
            return null;
        } // if

        try
        {
            // create a filter out of the filter id and the classname
            filter = (Filter) Class.forName (DIConstants.IMPORTFILTER_CLASSES
                                             [filterId]).newInstance ();
        } // try
        catch (java.lang.IllegalAccessException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.IllegalAccessException e)
        catch (java.lang.InstantiationException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.InstantiationException e)
        catch (java.lang.ClassNotFoundException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.ClassNotFoundException)
        return filter;
    } // getImportFilter


    /**************************************************************************
     * This method create the filter which will be used for the export. <BR/>
     *
     * @param   filterId    Id of the filter with will be used for the export
     * @param   env         The current environment
     *
     * @return  The filter.
     */
    public static Filter getExportFilter (int filterId, Environment env)
    {
        Filter filter = null;
        // check if the filter id is valid
        if (filterId < 0 || filterId >= DIConstants.EXPORTFILTER_CLASSES.length)
        {
            return null;
        } // if

        try
        {
            // create a filter out of the filterId with the classname
            filter = (Filter) Class.forName (DIConstants.EXPORTFILTER_CLASSES
                                             [filterId]).newInstance ();
        } // try
        catch (java.lang.IllegalAccessException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.IllegalAccessException e)
        catch (java.lang.InstantiationException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.InstantiationException e)
        catch (java.lang.ClassNotFoundException e)
        {
            System.out.print ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EXPORTFAILED, env));
        } // catch (java.lang.ClassNotFoundException)
        return filter;
    } // getExportFilter


    /**************************************************************************
     * Get table and attribute for first FIELD in reference object field. <BR/>
     *
     * @param   vde         Definition of Value in mayContainType which should
     *                      be shown in content.
     * @param   attribute   StringBuilder to be set to attribute
     * @param   table       The table to be used.
     * @param   env         Environment for getting input and generating output.
     *
     * @return true if data for FIELD in tag FIELDS exist in mayContainType and
     *         if attribute and table were set.
     */
    private static boolean setReferencedObjectData (ValueDataElement vde,
                                                    StringBuilder attribute,
                                                    StringBuilder table,
         Environment env)
    {
        IOHelpers.debug ("DIHelpers.setReferencedObjectData (" + vde + ", " + attribute + ", " + table + ")", env);
        // get first FIELDS element in referenced object field VALUE
        ReferencedObjectInfo fri = (vde.p_subTags != null) ? (ReferencedObjectInfo) vde.p_subTags
            .elementAt (0) : null;

        // if there is no FIELDS tag in referenced object field
        if (fri == null)
        {
            return false;
        } // if

        // if RefInfo = SYSFIELD
        if (fri.isSysField ())
        {
            // is field is sysfield, dat data is stored in ibs_Object
            table.append ("(SELECT oid, name, description, validUntil")
                 .append (" FROM ibs_Object)");

            // Change handling to use hardcoded values from DIConstants instead
            // of multilang tokens for the compare. Due to german and english names
            // are different Name <> name we set the given name to lowercase this
            // should allow to correct comparison for name. For description and
            // valid until we provide a second compare with the german names for
            // backwards compatibility to old systems!
            if (DIConstants.TOK_NAME.equals (fri.getName ().toLowerCase ()))
            {
                attribute.append ("name");
            } // if
            else if (DIConstants.TOK_DESCRIPTION.equals (fri.getName ().toLowerCase ()) ||
                     DIConstants.TOK_DESCRIPTION_DE.equals (fri.getName ().toLowerCase ()))
            {
                attribute.append ("description");
            } // else if
            else if (DIConstants.TOK_VALIDUNTIL.equals (fri.getName ().toLowerCase ()) ||
                     DIConstants.TOK_VALIDUNTIL_DE.equals (fri.getName ().toLowerCase ()))
            {
                attribute.append ("validUntil");
            } // else if
        } // if
        else
            // else FIELD
        {
            // get DocumentTemplate to get DataElement of refernced type
            DocumentTemplate_01 dt  = null;

            // first try to get type via typeFilter
            Type t = BOHelpers.getTypeCache ().find (vde.typeFilter);

            // if type with given typefilter exist
            if (t != null)
            {
                dt = (DocumentTemplate_01) t.getTemplate ();
            } // if
            else
            // if type does not exist - show error
            {
                IOHelpers.showMessage ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_INVALIDTYPECODE, env)
                    + vde.typeFilter, env);
            } // else


            // if type has a document template
            if (dt == null)
            {
                return false;
            } // if

            // get dataelement from documenttemplate and set table and
            // attribute for referenced object field
            DataElement de = dt.getTemplateDataElement ();
            table .append (de.tableName);

            ValueDataElement vd = de.getValueElement (fri.getName ());
            attribute.append (vd.mappingField);
        } // else

        // everything was all right:
        return true;
    } // setRefData


    /**************************************************************************
     * Create a query for getting the content. <BR/>
     * The query is finished with one of the SQL keywords <CODE>"WHERE"</CODE>
     * or  <CODE>"AND"</CODE> for directly concatenating further conditions.
     *
     * @param   childValues         Vector with ValueDataElements of childtype
     *                              which should be shown in this container.
     * @param   viewContent         The content view to be used.
     * @param   contentTypeTable    Table which is used for contentType (if
     *                              only one exists).
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     *
     * @return  The query string or
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @deprecated  KR 20090904 Use
     *              {@link #createCommonContentQuery(Vector, String, String, Environment)}
     *              instead.
     */
    @Deprecated
    public static StringBuffer createCommonContentQuery (
                                         Vector<ValueDataElement> childValues,
                                         String viewContent,
                                         String contentTypeTable,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        // call common method:
        return new StringBuffer ().append (DIHelpers.createCommonContentQuery (
            childValues, viewContent, contentTypeTable, env));
    } // createCommonContentQuery


    /**************************************************************************
     * Create a query for getting the content. <BR/>
     * The query is finished with one of the SQL keywords <CODE>"WHERE"</CODE>
     * or  <CODE>"AND"</CODE> for directly concatenating further conditions.
     *
     * @param   childValues         Vector with ValueDataElements of childtype
     *                              which should be shown in this container.
     * @param   viewContent         The content view to be used.
     * @param   contentTypeTable    Table which is used for contentType (if
     *                              only one exists).
     * @param   env                 Environment for getting input and generating output.
     *
     * @return  The query string or
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static StringBuilder createCommonContentQuery (
                                                          Vector<ValueDataElement> childValues,
                                                          String viewContent,
                                                          String contentTypeTable,
                                                          Environment env)
    {
        SelectQuery query;              // the query

        // construct the query:
        query = DIHelpers.createCommonContentSQLQuery (childValues,
            viewContent, contentTypeTable, env);

        // enable direct concatenation of next condition:
        query.extendWhere (new StringBuilder ());

        try
        {
            // convert the query to a StringBuilder and return the result:
            return query.toValidStringBuilder ();
        } // try
        catch (DBQueryException e)
        {
            IOHelpers.showMessage (e, env, true);
        } // catch

        return null;
    } // createCommonContentQuery


    /**************************************************************************
     * Create a query for getting the content. <BR/>
     * The query is finished with the SQL keyword <CODE>"AND"</CODE> for
     * directly concatenating other conditions.
     *
     * @param   childValues         Vector with ValueDataElements of childtype
     *                              which should be shown in this container.
     * @param   viewContent         The content view to be used.
     * @param   contentTypeTable    Table which is used for contentType (if
     *                              only one exists).
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     *
     * @return  The query object.
     *
     * @deprecated  KR 20090904 Use
     *              {@link #createCommonContentSQLQuery(Vector, String, String, Environment)}
     *              instead.
     */
    public static SelectQuery createCommonContentSQLQuery (
                                           Vector<ValueDataElement> childValues,
                                           String viewContent,
                                           String contentTypeTable,
                                           ApplicationInfo app,
                                           SessionInfo sess,
                                           Environment env)
    {
        // call common method and return the result:
        return DIHelpers.createCommonContentSQLQuery (
            childValues, viewContent, contentTypeTable, env);
    } // createCommonContentSQLQuery


    /**************************************************************************
     * Create a query for getting the content. <BR/>
     * The query is finished with the SQL keyword <CODE>"AND"</CODE> for
     * directly concatenating other conditions.
     *
     * @param   childValues         Vector with ValueDataElements of childtype
     *                              which should be shown in this container.
     * @param   viewContent         The content view to be used.
     * @param   contentTypeTable    Table which is used for contentType (if
     *                              only one exists).
     * @param   env                 Environment for getting input and generating output.
     *
     * @return  The query object.
     */
    public static SelectQuery createCommonContentSQLQuery (
                                           Vector<ValueDataElement> childValues,
                                           String viewContent,
                                           String contentTypeTable,
                                           Environment env)
    {
        SelectQuery query;              // the query
        StringBuilder selectStr = null;  // the SELECT clause
        StringBuilder fromStr = null;    // the FROM clause
        StringBuilder whereStr = null;   // the WHERE clause
        StringBuilder refFrom = new StringBuilder ();
        StringBuilder refWhere = new StringBuilder ();
        StringBuilder refAttribute = new StringBuilder ();
        StringBuilder refTable = new StringBuilder ();
        StringBuilder refTableAlias;
        ValueDataElement value = null;

        // SELECT
        selectStr = new StringBuilder ()
            .append (" v.oid, v.state, v.name AS name, v.typeName,")
            .append (" v.typeCode, v.isLink, v.linkedObjectId,")
            .append (" v.owner, v.ownerName, v.ownerOid, v.ownerFullname,")
            .append (" v.lastChanged AS lastChanged, v.isNew, v.icon,")
            .append (" v.description, v.flags, v.processState,")
            .append (" v.creationDate, v.validUntil");

        // check if there is a table for the content type set:
        // if not it does not make sense to get the child values.
        // if the table is ibs_Object and the child values are empty
        // this also does not make sense, because the container content already
        // contains the necessary attributes of ibs_Object.
        if (contentTypeTable != null &&
            !(contentTypeTable.trim ().isEmpty ()) &&
            !(contentTypeTable.equalsIgnoreCase ("ibs_Object") &&
              (childValues == null || childValues.size () == 0)))
                                        // content type table defined?
        {
            // add type specific attributes to the content query:
            for (int i = 0;
                 childValues != null && i < childValues.size ();
                 i++)
            {
                // get the value
                value = childValues.elementAt (i);
                // check if we got a valid value at this position
                // can be null if there is an wrong columnname in the
                // container-typedefinition
                if (value == null)
                {
                    continue;
                } // if (value == null)

                // extended column. Extended columns will be added
                // by an extension query.
                if (!value.p_isExtendedColumn)
                {
                    // check if current field is db mapped and is not an
                    if (value.mappingField != null) // db mapped field?
                    {
                        // For MSSQL Server we still use the old TEXT datatype
                        // for HTML and LONGTEXT fields
                        // these fields must be converted to NVARCHAR first
                        // We use NVARCHAR (255) for this purpose
                        if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL &&
                            (value.type.equals (DIConstants.VTYPE_HTMLTEXT) ||
                             value.type.equals (DIConstants.VTYPE_LONGTEXT)))
                        {
                            selectStr.append (", CONVERT (NVARCHAR (255), dbm.")
                                .append (value.mappingField)
                                .append (") AS ")
                                .append (value.mappingField);
                        } // if (SQLConstants.DB_TYPE == SQLConstants.DB_MSSQL &&
                        else    // standard field
                        {
                            selectStr.append (", dbm.").append (value.mappingField);
                        } // else standard field
                    } // if db mapped field
                    else                    // current field is not db mapped
                    {
                        IOHelpers.showMessage ( 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_MISSINGDBMAPPING, env)
                            + value.field, env);
                    } // else current field is not db mapped
                } // if (!value.p_isExtendedColumn)

                boolean multiSelection = value.multiSelection != null &&
                    value.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

                // if current value is referenced object field value add attributes
                // and tables for referenced object-field
                if (DIConstants.VTYPE_FIELDREF.equals (value.type) ||
                        (DIConstants.VTYPE_VALUEDOMAIN.equals (value.type) && !multiSelection))
                {
                    // re-initialize referenced object field variables:
                    refAttribute = new StringBuilder ();
                    refTable = new StringBuilder ();

                    // try to set attribute for SELECT ad table for FROM
                    // for referenced object field
                    if (DIHelpers.setReferencedObjectData (value, refAttribute,
                                         refTable, env))
                    {
                        refTableAlias = new StringBuilder ("d").append (i);

                        selectStr
                            .append (", ").append (refTableAlias).append (".")
                            .append (refAttribute).append (" AS ")
                            .append (value.mappingField).append ("_VALUE");

                        SQLHelpers.getLeftOuterJoin (
                            refTable, refTableAlias,
                            new StringBuilder ("dbm.").append (value.mappingField)
                                .append (" = ")
                                .append (refTableAlias).append (".oid"),
                            new StringBuilder ("AND"),
                            refFrom, refWhere);
                    } // if referenced object field attributes and tables could be set
                } // if (DIConstants.VTYPE_FIELDREF.equals (value.type)||
                //DIConstants.VTYPE_VALUEDOMAIN.equals (value.type))
            } // for - go through all content attributes

            fromStr = new StringBuilder ()
                .append (viewContent).append (" v")
                .append (", ").append (contentTypeTable).append (" dbm")
                .append (refFrom);
            whereStr = new StringBuilder ()
                .append ("dbm.oid = v.oid ").append (refWhere);
        } // if content type table defined
        else                            // no content type table
        {
            // FROM
            fromStr = new StringBuilder ()
                .append (viewContent).append (" v");
        } // else no content type table

        // construct the query object and return the result:
        query = new SelectQuery (selectStr, fromStr, whereStr, null, null, null);
        query.setUseDistinct (true);
        return query;
    } // createCommonContentSQLQuery


    /**************************************************************************
     * Compares two strings with proper handling of null values.<BR/>
     *
     * @param str1  first string to compare
     * @param str2  second string to compare with
     *
     * @return  <CODE>true</CODE> if both string are equal or both are
     *          <CODE>null</CODE>,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean compareStr (String str1, String str2)
    {
        if (str1 == null && str2 == null)
        {
            return true;
        } // if
        else if (str1 == null && str2 != null)
        {
            return false;
        } // else if
        else
        {
            return str1.equals (str2);
        } // else
    } // compareStr


    /**************************************************************************
     * Get the system field value from a given business object referenced
     * by a fieldName. <BR/>
     *
     * @param   fieldName   the name of the system field
     * @param   obj         the object to read the data from
     *
     * @return the system field value from the business object
     */
    public static String getSysFieldValue (String fieldName, BusinessObject obj)
    {
        if (obj == null)
        {
            return "";
        } // if

        if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        {
            return obj.name;
        } // if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        {
            return obj.typeName;
        } // else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        {
            return obj.description;
        } // else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.creationDate);
        } // else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_LASTCHANGED.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.lastChanged);
        } // else if (DIConstants.SYSFIELD_CHANGEDDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        {
            return obj.changer != null ? obj.changer.fullname : "";
        } // else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        {
            return obj.creator != null ? obj.creator.fullname : "";
        } // else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        {
            return obj.owner != null ? obj.owner.fullname : "";
        } // else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.validUntil);
        } // else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        else // invalid SYSFIELD!!!
        {
            return "-- INVALID SYSFIELDNAME '" + fieldName + "' ---";
        } // invalid SYSFIELD!!!
    } // getSysFieldValue


    /**************************************************************************
     * Get the system field value from a given container element referenced
     * by a fieldName. <BR/>
     *
     * @param   fieldName   the name of the system field
     * @param   obj         the container element to read the data from
     *
     * @return the system field value from the business object
     */
    public static String getSysFieldValue (String fieldName, ContainerElement obj)
    {
        if (obj == null)
        {
            return "";
        } // if

        if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        {
            return obj.name;
        } // if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        {
            return obj.typeName;
        } // else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        {
            return obj.description;
        } // else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.creationDate);
        } // else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_LASTCHANGED.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.lastChanged);
        } // else if (DIConstants.SYSFIELD_CHANGEDDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        {
            return obj.changer != null ? obj.changer.fullname : "";
        } // else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        {
            return obj.creator != null ? obj.creator.fullname : "";
        } // else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        {
            return obj.owner != null ? obj.owner.fullname : "";
        } // else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        {
            return DateTimeHelpers.dateToString (obj.validUntil);
        } // else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        else // invalid SYSFIELD!!!
        {
            return "-- INVALID SYSFIELDNAME '" + fieldName + "' ---";
        } // invalid SYSFIELD!!!
    } // getSysFieldValue


    /**************************************************************************
     * Get the associated token for a system field name.<BR/>
     * by a fieldName. <BR/>
     *
     * @param   fieldName   the name of the system field
     * @param   locale      the locale to retrieve the token for
     * @param   env         the environment
     *
     * @return the associated token
     */
    public static String getSysFieldToken (String fieldName, Locale_01 locale, Environment env)
    {
        String retValue = "";
        
        if (fieldName == null)
        {
            return "";
        } // if
        
        if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_NAME;
        } // if (DIConstants.SYSFIELD_NAME.equals (fieldName))
        else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_TYPENAME;
        } // else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_DESCRIPTION;
        } // else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_CREATIONDATE;
        } // else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_LASTCHANGED.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_LASTCHANGED;
        } // else if (DIConstants.SYSFIELD_CHANGEDDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_CHANGER;
        } // else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_CREATOR;
        } // else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_OWNER;
        } // else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        {
            retValue =  BOTokens.ML_VALIDUNTIL;
        } // else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        else // invalid SYSFIELD!!!
        {
            return "-- INVALID SYSFIELDNAME '" + fieldName + "' ---";
        } // invalid SYSFIELD!!!
        
        return MultilingualTextProvider.getTextForLocale (BOTokens.TOK_BUNDLE, retValue, locale, env);
    } // getSysFieldToken


    /**************************************************************************
     * Get the associated database field for a system field name.<BR/>
     * by a fieldName. <BR/>
     *
     * @param   fieldName   the name of the system field
     *
     * @return the associated token
     */
    public static String getSysFieldDBField (String fieldName)
    {
        if (fieldName == null)
        {
            return null;
        } // if

        if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        {
            return "name";
        } // if (DIConstants.SYSFIELD_NAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        {
            return "typeName";
        } // else if (DIConstants.SYSFIELD_TYPENAME.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        {
            return "description";
        } // else if (DIConstants.SYSFIELD_DESCRIPTION.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        {
            return "creationDate";
        } // else if (DIConstants.SYSFIELD_CREATIONDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_LASTCHANGED.equalsIgnoreCase (fieldName))
        {
            return "lastChanged";
        } // else if (DIConstants.SYSFIELD_CHANGEDDATE.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        {
            return "ownerFullname";
        } // else if (DIConstants.SYSFIELD_OWNER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        {
            return "validUntil";
        } // else if (DIConstants.SYSFIELD_VALIDUNTIL.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        {
            // not supported!!!
            return null;
        } // else if (DIConstants.SYSFIELD_CHANGER.equalsIgnoreCase (fieldName))
        else if (DIConstants.SYSFIELD_CREATOR.equalsIgnoreCase (fieldName))
        {
            // not supported!!!
            return null;
        } // else if (DIConstants.SYSFIELD_CREATOR.equals (fieldName))
        else // invalid SYSFIELD!!!
        {
            return null;
        } // invalid SYSFIELD!!!
    } // getSysFieldToken


    /**************************************************************************
     * Print an "elapsed time" output showing a message with the ending date
     * and the calculated time and the average process time per object
     * if greater then 0. <BR/>
     *
     * @param   env         The current environment.
     * @param   msg         The message to be printed showing the ending date.
     * @param   startDate   The starting date.
     * @param   endDate     The ending date.
     * @param   objCounter  The number of objects processed.
     */
    public static void showElapsedTime (Environment env, String msg,
                                        Date startDate, Date endDate, int objCounter)
    {
        // should a message be printed with the enddate?
        if (msg != null)
        {
            env.write (IE302.TAG_NEWLINE + msg + " at " +
                DateTimeHelpers.dateTimeToString (endDate) + ".");
        } // if (msg != null)
        env.write (IE302.TAG_NEWLINE + "Total elapsed time: ");
        long milliseconds = endDate.getTime () - startDate.getTime ();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds -= minutes * 60;
        env.write (minutes + " minute(s) " + seconds + " second(s).");
        if (objCounter > 0)
        {
            env.write (IE302.TAG_NEWLINE + objCounter + " object(s) processed.");
            env.write (IE302.TAG_NEWLINE + "Average time per object: " +
                (milliseconds / objCounter) + " milliseconds.");
        } // if (objCounter > 0)
        env.write (IE302.TAG_NEWLINE);
    } // showElapsedTime


    /**************************************************************************
     * Print an error message. <BR/>
     *
     * @param   env         The current environment.
     * @param   msg         The message to be printed showing the ending date.
     */
    public static void showError (Environment env, String msg)
    {
        // report the error:
        env.write ("<B>ERROR</B>: " + msg +
            IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
    } // showError


    /**************************************************************************
     * Returns the default filter in case no filter has been set yet. <BR/>
     *
     * @param   isTemplateImport    Configure the filter to read the
     *                              extended attributes for template files.
     * @param   env                 The current environment.
     *
     * @return the filter object
     */
    private static m2XMLFilter getFilter (boolean isTemplateImport,
                                          Environment env)
    {
        // initialize the exportFilter Object
        m2XMLFilter filter = new m2XMLFilter ();
        // set the environment
        filter.initObject (OID.getEmptyOid (), env.getUserInfo ().getUser (),
            env, env.getSessionInfo (), env.getApplicationInfo ());
        filter.isTemplateImport = isTemplateImport;
        // disable the writing of query field results cause it is not neccessary
        // and causes bad performance
        filter.setExportQueryResults (false);
        return filter;
    } // getFilter


    /**************************************************************************
     * Reads from an XML Viewer Data file and creates a dataElement
     * from the data read. <BR/>
     * The difference between a XMLViewer data file and a template data files
     * is that template file can hold additional attributes for the db-mapping.
     * If the parameter 'isTemplateFile' is true the method reads also
     * the mapping attributes.
     *
     * @param path              file path to read from
     * @param fileName          filename of the file to read from
     * @param isTemplateFile    true if the xml file is a template
     * @param   env             The current environment.
     *
     * @return  an dataElement object that holds the data or null if file
     *          could not be processed
     */
    private static DataElement readXMLDataFile (String path, String fileName,
                                                boolean isTemplateFile,
                                                Environment env)
    {
        // create an importFilter
        Filter importFilter = DIHelpers.getFilter (isTemplateFile, env);
        // set the path and the name in the importFiler
        importFilter.setPath (path);
        importFilter.setFileName (fileName);
        // init the importFilter and check if successful
        if (importFilter.init ())
        {
            // check if the viewerfile stores data
            if (importFilter.hasMoreObjects ())
            {
                return importFilter.nextObject ();
            } // if

            return null;
        } // if

        // init was not successfull
        return null;
    } // readXMLDataFile


    /**************************************************************************
     * Creates a xml document out of an dataElement and writes it to a
     * destination path. <BR/>
     * This method is used by the XMLViewer Object.
     *
     * @param   dataElement The dataElement to create the xml document from.
     * @param   template    The template object.
     * @param   fileName    The file name.
     * @param   path        The destination path to write the file to.
     * @param   env         The current environment.
     *
     * @return  <CODE>true</CODE> if everything was o.k., <CODE>false</CODE>
     *          otherwise.
     */
    public static boolean writeDataFile (DataElement dataElement,
                                         DocumentTemplate_01 template,
                                         String fileName, String path,
                                         Environment env)
    {
        // get the exportFilter Object
        m2XMLFilter exportFilter = DIHelpers.getFilter (false, env);
        exportFilter.setFileName (fileName);
        exportFilter.setPath (path);
        exportFilter.setDocumentTemplate (template);

        // create the directory for the XML viewer file
        // if needed
        if (!FileHelpers.exists (path))
        {
            FileHelpers.makeDir (path);
        } // if
        // try to initialize the export
        if (!exportFilter.create (dataElement))
        {
            return false;
        } // if

        return exportFilter.write ();
    } // writeDataFile


    /**************************************************************************
     * Reads from an XML Viewer Data file and creates a dataElement
     * from the data read. <BR/>
     * The difference between a XMLViewer data file and a template data files
     * is that template files can hold additional attributes for the db-mapping.
     * ATTENTION! This method IGNORES the mapping attributes in the xml file!
     *
     * @param   path        File path to read from.
     * @param   fileName    File name of the file to read from.
     * @param   env         The current environment.
     *
     * @return  A DataElement object that holds the data or
     *          <CODE>null</CODE> if the file could not be processed.
     */
    public static DataElement readDataFile (String path, String fileName,
                                            Environment env)
    {
        return DIHelpers.readXMLDataFile (path, fileName, false, env);
    } // readDataFile


    /**************************************************************************
     * Reads from an Document template Data file and creates a dataElement
     * from the data read. <BR/>
     * The difference between a XMLViewer data file and a template data files
     * is that template file can hold additional attributes for the db-mapping.
     * This method read also this attributes.
     *
     * @param   path        File path to read from.
     * @param   fileName    File name of the file to read from.
     * @param   env         The current environment.
     *
     * @return  A DataElement object that holds the data or
     *          <CODE>null</CODE> if the file could not be processed.
     */
    public static DataElement readTemplateDataFile (String path,
                                                    String fileName,
                                                    Environment env)
    {
        return DIHelpers.readXMLDataFile (path, fileName, true, env);
    } // readTemplateDataFile



    /**************************************************************************
     * Get the data element for an object. <BR/>
     * This method overwrites the same method in BOHelpers. It is a specific
     * implementation for XMLViewer_01 objects. <BR/>
     * For non-XMLViewer_01 objects the method within BOHelpers is called
     * automatically.
     *
     * @param   oid     The oid of the object for which to get the data element.
     * @param   env     The current environment.
     *
     * @return  The data element.
     *          <CODE>null</CODE> if no data element exists.
     */
    public static DataElement getObjectDataElement (OID oid, Environment env)
    {
        // the resulting data element:
        DataElement dataElement = null;
        // get the business object out of the cache:
        BusinessObject obj = BOHelpers.getObject (oid, env, false);

        // check if an object was found:
        if (obj != null)
        {
            // check if this is a XMLViewer_01 object:
            if (obj instanceof XMLViewer_01)
            {
                // get the data element out of the object:
                dataElement = ((XMLViewer_01) obj).getDataElement ();
            } // if
            else
            {
                // for standard objects call the corresponding method in
                // BOHelpers:
                dataElement = BOHelpers.getObjectDataElement (oid, env);
            } // else
        } // if

        // return the result:
        return dataElement;
    } // getObjectDataElement


    /**************************************************************************
     * Produces HTML output to show a DOM tree with buttons to hide and show it.
     * Displays a link to a document template if given. <BR>
     *
     * @param   env     	The current environment.
     * @param   domString	the dom string
     * @param   template	the template object 
     *
     */
    public static void showDOMInfo (Environment env, 
    								String domString, 
    								DocumentTemplate_01 template)
    {    	

    	// any template set?
    	if (template != null)
    	{
            IOHelpers.showStructure (env, domString, 
            		"hide DOM", "show DOM", 
            		"open " + template.getObjectTypeCode(),
            		template.oid);    		
    	} // if (template != null)
    	else  // no template set
    	{
            IOHelpers.showStructure (env, domString, 
            		"hide DOM", "show DOM", 
            		null, null);
    	} // else no template set
    } // showDOMInfo    
    
    
    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor is just to ensure that there is no default constructor
     * generated during compilation. <BR/>
     */
    private DIHelpers ()
    {
        // nothing to do
    } // DIHelpers

} // class DIHelpers
