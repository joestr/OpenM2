/*
 * Class: ASCIITranslator_01.java
 */

// package:
package ibs.di.trans;

// imports:
//KR TODO: unsauber
import ibs.app.AppMessages;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DITokens;
import ibs.di.DataElement;
import ibs.di.Log_01;
import ibs.di.trans.TranslatorInterface;
import ibs.di.trans.Translator_01;
//KR TODO: unsauber
import ibs.io.IOConstants;
//KR TODO: unsauber
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.FormFieldRestriction;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/******************************************************************************
 * This class represents one object of type ASCIITranslator with version 01. <BR/>
 * An ASCIITranslator takes a ASCII file where elements are separated with a
 * special separator character.
 * The ascii file can container meta information that represent the names of
 * the elements. Spaces in the names will be replaced by an underline character
 * e.g. "first name" will be "first_name".
 * Additionally the ascii file can contain header information.
 * The ascii file will be parsed and an intermediate xml structure will be
 * created.
 * An XSLT transformation will be performed with the associated XSLT file that
 * will generate the final XML file out of the intermediate structure.<P>
 *
 * Example:. <BR/>
 * ASCII File (with meta and header information):
 * <pre>
 * first name;last name  --> meta information for header
 * Bernd;Buchegger       --> header data
 * location;country      --> meta information for body
 * klagenfurt;austria    --> body data
 * frauental;austria     --> body data
 * </pre>
 *
 * resulting intermediate XML structure:
 * <pre>
 * &lt;?xml version=”1.0” encoding=”UTF-8”?&gt;
 * &lt;ASCII VERSION=”1.0”&gt;
 *  &lt;HEADER&gt;
 *    &lt;first_name&gt;Bernd&lt;/first_name&gt;
 *    &lt;last_name&gt;Buchegger&lt;/last_name&gt;
 *  &lt;/HEADER&gt;
 *  &lt;BODY&gt;
 *   &lt;ITEM&gt;
 *    &lt;location&gt;klagenfurt&lt;/location&gt;
 *    &lt;country&gt;austria&lt;/country&gt;
 *   &lt;/ITEM&gt;
 *   &lt;ITEM&gt;
 *    &lt;location&gt;frauental&lt;/location&gt;
 *    &lt;country&gt;austria&lt;/country&gt;
 *   &lt;/ITEM&gt;
 *  &lt;/BODY&gt;
 * &lt;/ASCII&gt;
 * </pre>
 *
 * In case the ASCII file does not contain the names of the elements the
 * following XML structure will be generated:
 * <pre>
 * &lt;?xml version=”1.0” encoding=”UTF-8”?&gt;
 * &lt;ASCII VERSION=”1.0”&gt;
 *  &lt;HEADER&gt;
 *   &lt;H1&gt;Bernd&lt;/H1&gt;
 *   &lt;H2&gt;Buchegger&lt;/H2&gt;
 *  &lt;/HEADER&gt;
 *  &lt;BODY&gt;
 *   &lt;ITEM&gt;
 *    &lt;B1&gt;klagenfurt&lt;/B1&gt;
 *    &lt;B2&gt;austria&lt;/B2&gt;
 *   &lt;/ITEM&gt;
 *   &lt;ITEM&gt;
 *    &lt;B1&gt;frauental&lt;/B1&gt;
 *    &lt;B2&gt;austria&lt;/B2&gt;
 *   &lt;/ITEM&gt;
 *  &lt;/BODY&gt;
 * &lt;/ASCII&gt;
 * </pre>
 *
 * In case the ascii file does not contain a header the resulting intermediate
 * xml structure would be:
 * <pre>
 * &lt;?xml version=”1.0” encoding=”UTF-8”?&gt;
 * &lt;ASCII VERSION=”1.0”&gt;
 *  &lt;BODY&gt;
 *   &lt;ITEM&gt;
 *    &lt;B1&gt;klagenfurt&lt;/B1&gt;
 *    &lt;B2&gt;austria&lt;/B2&gt;
 *   &lt;/ITEM&gt;
 *   &lt;ITEM&gt;
 *    &lt;B1&gt;frauental&lt;/B1&gt;
 *    &lt;B2&gt;austria&lt;/B2&gt;
 *   &lt;/ITEM&gt;
 *  &lt;/BODY&gt;
 * &lt;/ASCII&gt;
 * </pre>
 *
 * This resulting XML structure will be used as input for the associated XSLT file
 * in order to transform it to a XML structure that can be read with an filter. <BR/>
 *
 * @version     $Id: ASCIITranslator_01.java,v 1.27 2013/01/16 16:14:11 btatzmann Exp $
 *
 * @author      Bernd Buchegger(BB), 20010213
 ******************************************************************************
 */
public class ASCIITranslator_01 extends Translator_01
    implements TranslatorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ASCIITranslator_01.java,v 1.27 2013/01/16 16:14:11 btatzmann Exp $";


    /**
     * The separator character within the ASCII file. <BR/>
     */
    private String p_separator = ";";

    /**
     * The escape sequence for the separator character. <BR/>
     */
    private String p_escapeSeparator = "";

    /**
     * Option if the ascii file includes meta information. <BR/>
     */
    private boolean p_isIncludeMetadata = false;

    /**
     * Option if the ascii file includes header information. <BR/>
     */
    private boolean p_isIncludeHeader = false;

    /**
     * Option to write the generated intermediate file into the
     * filesystem. <BR/>
     */
    private boolean p_isWriteIntermediate = true;

    /**
     * Field name: separator. <BR/>
     */
    private static final String FIELD_SEPARATOR = "separator";
    /**
     * Field name: escape separator. <BR/>
     */
    private static final String FIELD_ESCAPESEPARATOR = "escapeSeparator";
    /**
     * Field name: isIncludeMetadata. <BR/>
     */
    private static final String FIELD_ISINCLUDEMETADATA = "isIncludeMetadata";
    /**
     * Field name: isIncludeHeader. <BR/>
     */
    private static final String FIELD_ISINCLUDEHEADER = "isIncludeHeader";

    /**************************************************************************
     * Creates an ASCIITranslator_01 Object. <BR/>
     */
    public ASCIITranslator_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Translator


    /**************************************************************************
     * Creates an ASCIITranslator_01 Object. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ASCIITranslator_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Translator


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set specific procedures:
        this.procCreate = "p_ASCIITranslator_01$create";
        this.procRetrieve = "p_ASCIITranslator_01$retrieve";
        this.procChange = "p_ASCIITranslator_01$change";
//        this.procDelete = "p_ASCIITranslator_01$delete";
//        this.procDeleteRec = "p_ASCIITranslator_01$delete";

        // set db table name:
        this.tableName = "ibs_ASCIITranslator_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters += 4;
        this.specificChangeParameters += 4;

        // initialize specific properties:
    } // initClassSpecifics


    /**************************************************************************
     * Read the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;

        super.getParameters ();

        // separator
        if ((str = this.env.getStringParam (DIArguments.ARG_SEPARATOR)) != null)
        {
            this.p_separator = str;
        } // if

        // escape separator
        if ((str = this.env.getStringParam (DIArguments.ARG_ESCAPESEPARATOR)) != null)
        {
            this.p_escapeSeparator = str;
        } // if

        // isIncludeMetadata
        if ((num = this.env.getBoolParam (DIArguments.ARG_ISINCLUDEMETADATA)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.p_isIncludeMetadata = num == IOConstants.BOOLPARAM_TRUE;
        } // if ((num = env.getBoolParam (AppArguments.ARG_ISINCLUDEMETADATA)) >= IOConstants.BOOLPARAM_FALSE)

        // isIncludeMetadata
        if ((num = this.env.getBoolParam (DIArguments.ARG_ISINCLUDEHEADER)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.p_isIncludeHeader = num == IOConstants.BOOLPARAM_TRUE;
        } // if ((num = env.getBoolParam (AppArguments.ARG_ISINCLUDEHEADER)) >= IOConstants.BOOLPARAM_FALSE)
    } // getParameters


    /**************************************************************************
     * Compute the full path and name of the xml source file. <BR/>
     *
     * @param   path        The path of the xml source file.
     * @param   fileName    The name of the xml source file.
     *
     * @return  The full path with file name.
     */
    public String computeXmlSourceFileName (String path, String fileName)
    {
        // compute the new file name and return it:
        return FileHelpers.getUniqueFileName (path, "i_" + fileName + ".xml");
    } // computeXmlSourceFileName


    /**************************************************************************
     * Get the source to be translated. <BR/>
     * The ASCII translator handles ASCII text files so the input file will
     * be translated into an intermediate xml file that can be used for the
     * XSLT translation process. <BR/>
     *
     * @param   sourceFile  The path and name of the source file.
     * @param   xmlFile     The path and file of the destination file.
     * @param   log         The log to write translation messages to.
     *
     * @return  The intermediate xml document or <CODE>null</CODE> if it could
     *          not be created.
     */
    protected Source getSource (String sourceFile, String xmlFile, Log_01 log)
    {
        Source retVal = null;           // the return value
        Document doc = null;            // the xml document instance

        // create the intermediate XML structure
        // and set the generated xml DOM in the this.p_doc property
        doc = this.getIntermediateXml (sourceFile);

        // check if we could get an xml document instance:
        if (doc != null)                // translation to xml successful?
        {
            // check if we have to write the intermediate xml structure into a file
            if (this.p_isWriteIntermediate)
            {
                // write the intermediate file to the filesystem:
                this.write (doc, xmlFile);
            } // if (this.p_isWriteIntermediate)

            // set return value of method:
            retVal = new DOMSource (doc);
        } // if translation to xml successful

        // return the computed xml tree:
        return retVal;
    } // getSource


    /**************************************************************************
     * Creates the intermediate XML structure out of the file. <BR/>
     *
     * @param   sourceFile      the name of the source file
     *
     * @return  The intermediate xml structure or <CODE>null</CODE> if there was
     *          an error when trying to create it.
     */
    private Document getIntermediateXml (String sourceFile)
    {
        File file;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String line = "";
        String [] tokens = null;
        String [] headerMetadata = null;
        String [] bodyMetadata = null;
        boolean isFirstLine = true;
        boolean isHeader = true;
        boolean isFirstData = true;
        Document doc = null; //         the document
        Node body = null;
        Element root = null;

        try
        {
            // create the document:
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            return doc;
        } // catch

        // create the document root: <ASCII VERSION="1.0">
        root = doc.createElement ("ASCII");
        root.setAttribute ("VERSION", "1.0");
        // add the head to the xml document
        doc.appendChild (root);

        // do we have to read header data?
        isHeader = this.p_isIncludeHeader;

        // check the separator for special characters
        this.p_separator = this.checkSeparator (this.p_separator);

        // open the file
        try
        {
            file = new File (sourceFile);
            fileReader = new FileReader (file);
            bufferedReader = new BufferedReader (fileReader);

            // read the input
            while ((line = bufferedReader.readLine ()) != null)
            {
                // check if line is not empty
                // empty lines will be discarded
                if (line.length () > 0)
                {
                    // getTokens is doing the same!!!
                    // tokens = DIHelpers.getTokens (line, this.p_separator);
                    tokens = this.getTokens (line, this.p_separator);
                    // do we have to read the header?
                    if (this.p_isIncludeHeader && isHeader)
                    {
                        // is it a line that includes header metadata
                        if (this.p_isIncludeMetadata && isFirstLine)
                        {
                            headerMetadata = this.checkMetadata (tokens, "H");
                            isFirstLine = false;
                        } // if (this.p_isIncludeMetadata && isFirstLine)
                        else            // header data line found
                        {
                            // create the header structure
                            this.createHeader (doc, root, tokens, headerMetadata);
                            isHeader = false;
                            isFirstLine = true;
                        } // header data line found
                    } // if (this.p_isIncludeHeader && isHeader)
                    else                // process body lines
                    {
                        // is it a line that includes header metadata
                        if (this.p_isIncludeMetadata && isFirstLine)
                        {
                            bodyMetadata = this.checkMetadata (tokens, "B");
                            isFirstLine = false;
                        } // if (this.p_isIncludeMetadata && isFirstLine)
                        else            // header data line found
                        {
                            // is it the first data line?
                            if (isFirstData)
                            {
                                body = this.createBody (doc, root, tokens,
                                    bodyMetadata);
                                isFirstData = false;
                            } // if (isFirstData)
                            else        // create a body item
                            {
                                this.createBodyItem (doc, body, tokens,
                                    bodyMetadata);
                            } // create a body item
                        } // header data line found
                    } // else process body lines
                } // if (! line.length () == 0)
                else
                {
                    // line ignored
                } // else
            } // while ((line = bufferedReader.readLine ()) != null)
        } // try
        catch (IOException e)
        {
            IOHelpers.showMessage ("Could not read from original file.",
                e, this.app, this.sess, this.env, true);
            doc = null;
        } // catch
        finally
        {
            try
            {
                // close reader:
                if (bufferedReader != null)
                {
                    bufferedReader.close ();
                } // if
                else if (fileReader != null)
                {
                    fileReader.close ();
                } // else if
            } // try
            catch (IOException e)
            {
                IOHelpers.showMessage ("Could not close file reader.",
                    e, this.app, this.sess, this.env, true);
            } // catch
        } // final

        // return the xml structure:
        return doc;
    } // getIntermediateXml


    /**************************************************************************
     * Create header structure. <BR/>
     *
     * @param   doc         The complete structure where the header shall be
     *                      added.
     * @param   root        The xml node to add the header structure to.
     * @param   data        A string array containing the data.
     * @param   metadata    A string array containing the metadata.
     */
    private void createHeader (Document doc, Node root, String[] data,
                               String[] metadata)
    {
        Node node;
        Node header;
        Text text;
        String nodeName;
        String nodeValue;

        header = doc.createElement ("HEADER");
        root.appendChild (header);

        // loop through the data array and create the header nodes
        for (int i = 0; i < data.length; i++)
        {
            // set the node name
            if (metadata != null && i < metadata.length)
            {
                nodeName = metadata [i];
            } // if
            else
            {
                nodeName = "H" + i;
            } // else
            // create the node
            node = doc.createElement (nodeName);
            header.appendChild (node);
            // set the node value
            if (data [i] != null)
            {
                // restore the separator character
                nodeValue = this.restoreSeparator (data [i]);
                text = doc.createTextNode (nodeValue);
                node.appendChild (text);
            } // if (data [i] != null)
        } // for (int i = 0; i < data.length (); i++)
    } // createHeader


    /**************************************************************************
     * Create the body structure. <BR/>
     *
     * @param   doc         The complete structure where the body shall be
     *                      added.
     * @param   root        The xml node to add the item structure to.
     * @param   data        A string array containing the data.
     * @param   metadata    A string array containing the metadata.
     *
     * @return  The body node to add the item nodes to.
     */
    private Node createBody (Document doc, Node root, String[] data,
                             String[] metadata)
    {
        Node body;

        body = doc.createElement ("BODY");
        root.appendChild (body);
        // create the body item
        this.createBodyItem (doc, body, data, metadata);
        // return the body node in order to enable adding item nodes to it
        return body;
    } // createBodyItem



    /**************************************************************************
     * Create a body item structure. <BR/>
     *
     * @param   doc         The complete structure where the body item shall be
     *                      added.
     * @param   root        The xml node to add the item structure to.
     * @param   data        A string array containing the data.
     * @param   metadata    A string array containing the metadata.
     */
    private void createBodyItem (Document doc, Node root, String[] data,
                                 String[] metadata)
    {
        Node node;
        Node item;
        Text text;
        String nodeName;
        String nodeValue;

        item = doc.createElement ("ITEM");
        root.appendChild (item);

        // loop through the data array and create the header nodes
        for (int i = 0; i < data.length; i++)
        {
            // set the node name
            if (metadata != null  && i < metadata.length)
            {
                nodeName = metadata [i];
            } // if
            else
            {
                nodeName = "B" + i;
            } // else
            // create the node
            node = doc.createElement (nodeName);
            item.appendChild (node);
            // set the node value
            if (data [i] != null)
            {
                // restore the separator character
                nodeValue = this.restoreSeparator (data [i]);
                text = doc.createTextNode (nodeValue);
                node.appendChild (text);
            } // if (data [i] != null)
        } // for (int i = 0; i < data.length (); i++)
    } // createBodyItem


    /**************************************************************************
     * Checks if delimiter has been set to a special character like
     * tabulator. in that case we have to set the escape character
     * otherwise it will not be recognized and can cause errors. <BR/>
     *
     * @param   separator   the separator/delimiter string
     *
     * @return the modified separator
     */
    private String checkSeparator (String separator)
    {
        String separatorLocal = separator; // variable for local assignments

        // check for tabulator
        if (separatorLocal.equals ("\\t"))
        {
            separatorLocal = "\t";
        } // if
        // return the separator
        return separatorLocal;
    } // checkSeparator


    /**************************************************************************
     * Restores separator characters in a string by replacing a separator
     * escape sequence by the separator character. This will only be done in
     * case an escape sequence has been set. <BR/>
     *
     * @param   value   the value string to restore the separator characters in
     *
     * @return the value with restored separator characters
     */
    private String restoreSeparator (String value)
    {
        // check if an separator escape sequence has been set
        // if yes try a replacement
        if (this.p_escapeSeparator != null &&
            this.p_escapeSeparator.trim ().length () > 0)
        {
            return StringHelpers.replace (value, this.p_escapeSeparator,
                this.p_separator);
        } // if

        return value;
    } // restoreSeparator


    /**************************************************************************
     * Gets all tokens in from a string where the tokens are separated
     * by a certain delimiter. <BR/>
     *
     * @param   tokenStr    the string to get the tokens from
     * @param   delimiter   the delimiter
     *
     * @return an array of string containing the tokens
     */
    private String[] getTokens (String tokenStr, String delimiter)
    {
        Vector<String> tokenVector = null;
        String [] tokens;
        int lastIndex = 0;
        int index = 0;
        int delimiterLength;
        String token;

        // check if we got a non empty string to tokenize
        if (tokenStr == null)
        {
            return null;
        } // if (strList == null)
        else if (tokenStr.length () == 0)
        {
            return new String [] {""};
        } // else if (tokenStr.length () == 0)
        else    // a string with tokens is available
        {
            tokenVector = new Vector<String> ();
            // remember the length of the delimiter in case it
            // is more then 1 character
            delimiterLength = delimiter.length ();
            // tokenize the tokenStr
            while (index != -1)
            {
                // find the position of the delimiter
                index = tokenStr.indexOf (delimiter, lastIndex);
                // cut out the token from the tokenStr
                if (index != -1)
                {
                    token = tokenStr.substring (lastIndex, index);
                } // if
                else
                {
                    token = tokenStr.substring (lastIndex);
                } // else
                // check if we got a null value
                // check if the token equals the delimiter
                // this is the case when 2 tokens follow each other
                // or a delimiter was at the end
                // in that case the result must be ""
                if (token.equals (delimiter))
                {
                    token = "";
                } // if
                // add the token to the token vector
                tokenVector.addElement (token);
                // increment the position where to substring the next token
                lastIndex = index + delimiterLength;
            } // while (index != -1)
            // create the resulting string array:
            tokens = new String [tokenVector.size ()];
            tokens = tokenVector.toArray (tokens);
            // return the string array with the tokens
            return tokens;
        } // a string with tokens is available
    } // getTokens


    /**************************************************************************
     * This method checks all tokens in a metadata string array in order to
     * ensure valid tagnames. <BR/>
     * This means that any spaces, "&", ";", "<",">" and tabstobs "\t"
     * will be replaces  by "_" characters and the escape separator
     * sequence is tried to be restored. <BR/>
     *
     * @param   metadata        a string array containing metadata tokens
     * @param   prefixUndefined the prefix for undefined entries that resulted
     *                          in an empty string
     *
     * @return the valid metadata string array
     */
    private String [] checkMetadata (String [] metadata, String prefixUndefined)
    {
        char c;
        char [] charArray;

        // check if we got any metadata
        if (metadata != null)
        {
            // loop through the metadata string array
            for (int i = 0; i < metadata.length; i++)
            {
                metadata [i] = this.restoreSeparator (metadata [i]);
                // convert to char array
                charArray = metadata [i].toCharArray ();
                // loop through the characters
                for (int j = 0; j < charArray.length; j++)
                {
                    // get the character
                    c = charArray [j];
                    // check if the character is out of the a..zA..Z range
                    // TODO: add numbers to allowed characters
                    if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                          (c >= '0' && c <= '9')))
                    {
                        // replace the character by a "_" that will be removed afterwards
                        charArray [j] = '_';
                    } // if (!(c >= 'a' && c <= 'z') || !(c >= 'A' && c <= 'Z'))
                } // for (j = 0; j < charArray.length; j++)
                // copy the char array back into the string
                metadata [i] = new String (charArray);
                // replace any _ by ''
                metadata [i] = StringHelpers.replace (metadata [i], "_", "");

                // check if the metadata is not empty:
                if (metadata [i].length () == 0)
                {
                    // in case the resulting metadata name is empty
                    // we generate a default value with the format "_<index>"
                    metadata [i] = prefixUndefined + i;
                } // if (metadata [i].length () == 0)
            } // for (int i = 0; i < metadata.length (); i++)
        } // if (metadata != null)
        // return a valid metadata string array
        return metadata;
    } // checkMetadata


    /**************************************************************************
     * Write a XML document instance to a file. <BR/>
     *
     * @param   doc         The complete structure to be written to the file.
     * @param   filePath    The path + file name to write the output file to.
     */
    public void write (Document doc, String filePath)
    {
        FileOutputStream fileOutputStream = null;
                                        // the stream to write the data to

        // check if we have a document to write to the file:
        if (doc != null)                // document exists?
        {
            try
            {
                // open file output stream:
                fileOutputStream = new FileOutputStream (filePath);
                // serialize the dom tree:
                DOMHandler.serializeDOM (doc, fileOutputStream, true, null);
            } // try
            catch (FileNotFoundException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch IOException
            finally
            {
                // close the streams:
                if (fileOutputStream != null) // output stream was opened?
                {
                    try
                    {
                        // close  the stream:
                        fileOutputStream.close ();
                    } // try
                    catch (IOException e)
                    {
                        IOHelpers.showMessage (
                            "write: could not close the fileOutputStream",
                            e, this.app, this.sess, this.env, true);
                    } // catch IOException
                } // if output stream was opened
            } // finally
        } // if document exists
        else                            // document does not exist
        {
            IOHelpers.printMessage ("write: no document to write!");
        } // else document does not exist
    } // write


    /**************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // display the properties for import/export connector
        this.showProperty (table, DIArguments.ARG_SEPARATOR, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SEPARATOR, env),
            Datatypes.DT_TEXT, this.p_separator);
        this.showProperty (table, DIArguments.ARG_ESCAPESEPARATOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ESCAPESEPARATOR, env),
            Datatypes.DT_TEXT, this.p_escapeSeparator);
        this.showProperty (table, DIArguments.ARG_ISINCLUDEMETADATA,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEMETADATA, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeMetadata);
        this.showProperty (table, DIArguments.ARG_ISINCLUDEHEADER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEHEADER, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeHeader);
    } //  showProperties


    /**************************************************************************
     * Represent the properties of a Connector_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        super.showFormProperties (table);
        // display the properties for import/export connector
        // restriction for separator: empty entries allowed, max length = 15
        this.formFieldRestriction =
            new FormFieldRestriction (false, 15, 0);
        this.showFormProperty (table, DIArguments.ARG_SEPARATOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SEPARATOR, env),
            Datatypes.DT_TEXT, this.p_separator);
        // restriction for escape separator: empty entries allowed, max length = 15
        this.formFieldRestriction =
            new FormFieldRestriction (true, 15, 0);
        this.showFormProperty (table, DIArguments.ARG_ESCAPESEPARATOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ESCAPESEPARATOR, env),
            Datatypes.DT_TEXT, this.p_escapeSeparator);
        this.showFormProperty (table, DIArguments.ARG_ISINCLUDEMETADATA,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEMETADATA, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeMetadata);
        this.showFormProperty (table, DIArguments.ARG_ISINCLUDEHEADER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEHEADER, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeHeader);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the translator. <BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);
        // display the properties for import/export connector
        this.showProperty (table, DIArguments.ARG_SEPARATOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SEPARATOR, env),
            Datatypes.DT_TEXT, this.p_separator);
        this.showProperty (table, DIArguments.ARG_ESCAPESEPARATOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ESCAPESEPARATOR, env),
            Datatypes.DT_TEXT, this.p_escapeSeparator);
        this.showProperty (table, DIArguments.ARG_ISINCLUDEMETADATA,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEMETADATA, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeMetadata);
        this.showProperty (table, DIArguments.ARG_ISINCLUDEHEADER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISINCLUDEHEADER, env),
            Datatypes.DT_BOOL, "" + this.p_isIncludeHeader);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the translator to a log. <BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add Translator specific settings
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SEPARATOR, env) + ": " +
            this.p_separator, false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ESCAPESEPARATOR, env) + ": " +
            this.p_escapeSeparator, false);
        // show the isIncludeMetadata value
        if (this.p_isIncludeMetadata)
        {
            log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISINCLUDEMETADATA, env) + ": " +
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE, 
                    AppMessages.ML_MSG_BOOLTRUE, env));
        } // if
        else
        {
            log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISINCLUDEMETADATA, env) + ": " +
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE, 
                    AppMessages.ML_MSG_BOOLFALSE, env));
        } // else
        // show the isIncludeHeader value
        if (this.p_isIncludeHeader)
        {
            log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISINCLUDEHEADER, env) + ": " +
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE, 
                    AppMessages.ML_MSG_BOOLTRUE, env));
        } // if
        else
        {
            log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISINCLUDEHEADER, env) + ": " +
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE, 
                    AppMessages.ML_MSG_BOOLFALSE, env));
        } // else
    } // addSettingsToLog


    /**************************************************************************
     * Reads the object data from a dataElement. <BR/>
     *
     * @param   dataElement The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists (ASCIITranslator_01.FIELD_SEPARATOR))
        {
            this.p_separator = dataElement
                .getImportStringValue (ASCIITranslator_01.FIELD_SEPARATOR);
        } // if
        if (dataElement.exists (ASCIITranslator_01.FIELD_ESCAPESEPARATOR))
        {
            this.p_escapeSeparator = dataElement
                .getImportStringValue (ASCIITranslator_01.FIELD_ESCAPESEPARATOR);
        } // if
        if (dataElement.exists (ASCIITranslator_01.FIELD_ISINCLUDEMETADATA))
        {
            this.p_isIncludeMetadata = dataElement
                .getImportBooleanValue (ASCIITranslator_01.FIELD_ISINCLUDEMETADATA);
        } // if
        if (dataElement.exists (ASCIITranslator_01.FIELD_ISINCLUDEHEADER))
        {
            this.p_isIncludeHeader = dataElement
                .getImportBooleanValue (ASCIITranslator_01.FIELD_ISINCLUDEHEADER);
        } // if
    } // readImportData


    /**************************************************************************
     * Writes the object data into a dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the connector specific values
        dataElement.setExportValue (ASCIITranslator_01.FIELD_SEPARATOR,
            this.p_separator);
        dataElement.setExportValue (ASCIITranslator_01.FIELD_ESCAPESEPARATOR,
            this.p_escapeSeparator);
        dataElement.setExportValue (ASCIITranslator_01.FIELD_ISINCLUDEMETADATA,
            this.p_isIncludeMetadata);
        dataElement.setExportValue (ASCIITranslator_01.FIELD_ISINCLUDEHEADER,
            this.p_isIncludeHeader);
    } // writeExportData


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // initialize params index
        super.setSpecificChangeParameters (sp);

        // set the specific parameters:
        // separator:
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_separator);
        // escapeSeparator:
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_escapeSeparator);
        // isIncludeMetadata:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.p_isIncludeMetadata);
        // isIncludeHeader:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.p_isIncludeHeader);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        // initialize params index
        int i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:
        // separator:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // escapeSeparator:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // isIncludeMetadata:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // isIncludeHeader
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                      procedure.
     * @param lastIndex     The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        // read the parameter in the super class which
        // will be attachment_01
        super.getSpecificRetrieveParameters (params, lastIndex);

        // initialize params index
        // Attachment_01 reads 7 parameters in its getSpecificRetrieveParameters
        // method, Translator_01 reads 1 parameter, therefore we have to add 8
        // to the params index
        int i = lastIndex + 8;

        // get the specific parameters:
        this.p_separator            = params[++i].getValueString ();
        this.p_escapeSeparator      = params[++i].getValueString ();
        this.p_isIncludeMetadata    = params[++i].getValueBoolean ();
        this.p_isIncludeHeader      = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters

} // class ASCIITranslator_01
