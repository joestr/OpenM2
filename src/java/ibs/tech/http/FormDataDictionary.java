/*
 * Class: FormDataDictionary.java
 */

// package:
package ibs.tech.http;

// imports:
import ibs.app.AppConstants;
//KR TODO: unsauber
import ibs.io.UploadException;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;


/******************************************************************************
 * Supports lookup of items in a collection of FormDataElements. <BR/>
 *
 * @version     $Id: FormDataDictionary.java,v 1.10 2007/07/10 19:25:59 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 981008
 ******************************************************************************
 */
public class FormDataDictionary extends Dictionary<Object, FormDataElement>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormDataDictionary.java,v 1.10 2007/07/10 19:25:59 kreimueller Exp $";


    /**
     * A vector containing all form data elements. <BR/>
     */
    private Vector<FormDataElement> formDataElements = null;


    /**
     * The data stream with the raw data of the fields. <BR/>
     */
    private byte[] bStream = null;


    /**************************************************************************
     * Constructor of this class. <BR/>
     */
    public FormDataDictionary ()
    {
        this.formDataElements = new Vector<FormDataElement> (50, 10);
    } // FormDataDictionary


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   bStream The byte stream containing the raw data of the form
     *                  elements.
     */
    public FormDataDictionary (byte[] bStream)
    {
        this.bStream = bStream;
        this.formDataElements = new Vector<FormDataElement> (50, 10);
    } // FormDataDictionary


    /**************************************************************************
     * Get one item out of the dictionary determined through its name. <BR/>
     *
     * @param   name    Name of the item.
     *
     * @return  The form data corresponding to the name or <CODE>null</CODE>
     *          if not found.
     */
    public FormDataElement getItem (String name)
    {
        int pos;                        // the position of the searched item
        FormDataElement nameDataElement = new FormDataElement ();
        nameDataElement.name = name;
        // search for the item:
/*
        for (pos = 0;
             pos < formDataElements.size () &&
                 !name.equals (((FormDataElement) formDataElements.elementAt (pos)).name);
             pos++);

        if (pos < formDataElements.size ())
            return ((FormDataElement) formDataElements.elementAt (pos));
        else                            // item not found?
            return null;                // return corresponding value
*/

        if ((pos = this.formDataElements.indexOf (nameDataElement)) == -1)
                                        // item not found?
        {
            return null;                // return corresponding value
        } // if item not found

        // item was found
        // get the item from the vector and return it:
        return this.formDataElements.elementAt (pos);
    } // getItem


    /**************************************************************************
     * Write one item to the dictionary. <BR/>
     *
     * @param   value   The item.
     */
    public void putItem (FormDataElement value)
    {
        // put the new item into the vector:
        this.formDataElements.addElement (value);
    } // putItem


    /**************************************************************************
     * Write one value to the dictionary. <BR/>
     * This method checks if there is already an item with the same name here.
     * In this case the value is added to the item, otherwise a new item is
     * created which contains just the new value. <BR/>
     *
     * @param   value   The value.
     *
     * @throws  NullPointerException
     *          A NullPointerException when accessing the value. So the value
     *          must be <CODE>null</CODE>.
     */
    public void putValue (FormDataValue value) throws NullPointerException
    {
        if (value == null)              // no value set?
        {
            throw new NullPointerException (); // this is not allowed!
        } // if no value set

        // search for an item with the same name:
        FormDataElement item = this.getItem (value.name);

        if (item == null)               // no item found?
        {
            // create a new item containing just the value and add it to the
            // other elements:
            item = new FormDataElement (value);
            this.putItem (item);
        } // if no item found
        else                            // the item was found?
        {
            // add the value to the item:
            item.put (value);
        } // else the item was found

    } // putValue


    /**************************************************************************
     * Returns the value to which the key is mapped in this dictionary as a
     * String. <BR/>
     * If the value is a file the file is stored to the file system and the
     * file name is returned.
     * The target directory for this operation is gotten from another parameter
     * within the form which has the name
     * <CODE>"&lt;thisParamName&gt;{@link ibs.app.AppConstants#DT_FILE_PATH_EXT AppConstants.DT_FILE_PATH_EXT}"</CODE>.
     * If this parameter doesn't exist the default path (the temp directory) is
     * used. <BR/>
     *
     * @param   key     A key in this dictionary.
     *
     * @return  The value to which the key is mapped in this dictionary or
     *          <CODE>null</CODE> if the key has no mapping.
     */
    public String getString (String key)
    {
        String result = null;           // the result value
        FormDataElement formDataElement = this.getItem (key);
                                        // get the form data element
        if (formDataElement != null)    // the element was found?
        {
            if (formDataElement.type == HttpConstants.T_FILE)
                                        // this is a file which must be
                                        // uploaded?
            {
/*
                // try to get the target directory from the form:
                String targetDir =
                    getString (key + AppConstants.DT_FILE_PATH_EXT);

                // save the file into the target directory and get the target
                // filename as result:
                result = formDataElement.writeFile (bStream, targetDir);
*/
                result = null;
            } // if this is a file which must be uploaded
            else                        // any other type of parameter
            {
                result = formDataElement.value; // create the result
            } // else any other type of parameter
        } // if the element was found
        return result;                  // return the result
    } // getString


    /**************************************************************************
     * Returns the value to which the key is mapped in this dictionary as a
     * String. <BR/>
     *
     * @param   key     A key in this dictionary.
     *
     * @return  The value to which the key is mapped in this dictionary or
     *          <CODE>null</CODE> if the key has no mapping.
     */
    public String[] getMultipleString (String key)
    {
        String[] result = null;           // the result value

        FormDataElement formDataElement = this.getItem (key);
                                        // get the form data element
        if (formDataElement != null)    // the element was found?
        {
            if (formDataElement.values != null)
            {
                int size = formDataElement.values.size ();
                result = new String [size];
                for (int i = 0; i < size; i++)
                {
                    Object o = formDataElement.values.elementAt (i);
                    if (o instanceof String)
                    {
                        result[i] = (String) o;
                    } // if
                    else
                    {
                        result[i] = ((FormDataValue) o).value; // create the result
                    } // else
                } // for i
            } // if
        } // if the element was found
        return result;                  // return the result
    } // getString


    /**************************************************************************
     * Returns the file to which the key is mapped in this dictionary as a
     * String. <BR/>
     * The file is saved to the file system and the filename is returned. <BR/>
     *
     * @param   key         A key in this dictionary.
     * @param   targetDir   The directory where the file shall be placed.
     *                      If this value is <CODE>null</CODE> the target
     *                      directory is derived like in
     *                      <A HREF="#getString">getString</A>.
     *
     * @return  The value to which the key is mapped in this dictionary or
     *          <CODE>null</CODE> if the key has no mapping.
     *
     * @throws  UploadException
     *          An exception occurred during file upload.
     */
    public String getFileString (String key, String targetDir)
        throws UploadException
    {
        String targetDirLocal = targetDir; // variable for local assignments
        String result = null;           // the result value
        FormDataElement formDataElement = this.getItem (key);
                                        // get the form data element
        if (formDataElement != null &&
            formDataElement.type == HttpConstants.T_FILE)
                                        // the element was found and is a file?
        {
            if (targetDirLocal == null) // no target directory set?
            {
                // try to get the target directory from the form:
                targetDirLocal = this.getString (key + AppConstants.DT_FILE_PATH_EXT);
            } // if no target directory set

            // save the file into the target directory and get the target
            // filename as result:
            result = formDataElement.writeFile (this.bStream, targetDirLocal);
        } // if the element was found and is a file

        return result;                  // return the result
    } // getFileString


    /**************************************************************************
     * Returns an enumeration of the elements within the dictionary. <BR/>
     *
     * @return  The enumeration of elements within this dictionary.
     */
    public Enumeration<FormDataElement> getEnumeration ()
    {
        return this.formDataElements.elements ();
    } // getEnumeration


    ///////////////////////////////////////////////////////////////////////////
    // external (public) java.util.Dictionary class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Returns the number of keys in this dictionary. <BR/>
     *
     * @return  The number of keys in this dictionary.
     */
    public int size ()
    {
        // get the number of elements and return it:
        return this.formDataElements.size ();
    } // size


    /**************************************************************************
     * Tests if this dictionary maps no keys to value. <BR/>
     *
     * @return  <CODE>true</CODE> if this dictionary maps no keys to values;
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isEmpty ()
    {
        // check if the dictionary is empty and return the result:
        return this.formDataElements.size () == 0;
    } // isEmpty


    /**************************************************************************
     * Returns an enumeration of the keys in this dictionary. <BR/>
     *
     * @return  An enumeration of the keys in this dictionary.
     *
     * @exception   HttpComponentException
     *              The operation could not be performed correctly.
     *              This value is always returned since this method is not
     *              supported yet.
     */
    public Enumeration<Object> keys () throws HttpComponentException
    {
        throw new HttpComponentException ();
//        return new Enumeration (formDataElements.elements ());
    } // keys


    /**************************************************************************
     * Returns an enumeration of the values in this dictionary.
     * The Enumeration methods on the returned object can be used to fetch the
     * elements sequentially. <BR/>
     *
     * @return  An enumeration of the values in this dictionary.
     *
     * @exception   HttpComponentException
     *              The operation could not be performed correctly.
     *              This value is always returned since this method is not
     *              supported yet.
     */
    public Enumeration<FormDataElement> elements () throws HttpComponentException
    {
        throw new HttpComponentException ();
//        return new Enumeration (formDataElements.elements ());
    } // elements


    /**************************************************************************
     * Returns the value to which the key is mapped in this dictionary. <BR/>
     *
     * @param   key     A key in this dictionary.
     *                  <CODE>null</CODE> if the key is not mapped to any value
     *                  in this dictionary.
     *
     * @return  The value to which the key is mapped in this dictionary or null
     *          if the key has no mapping.
     *
     * @exception   HttpComponentException
     *              The key is not of type String or of type Variant with
     *              variant type string.
     */
    public FormDataElement get (Object key) throws HttpComponentException
    {
        if (key instanceof String)      // the key is a string?
        {
            // get item with string key and return it:
            return this.getItem ((String) key);
        } // if the key is a string
        else if (key instanceof FormDataElement)
                                        // the key is a form data?
        {
            // get name of form data out of the key and return the
            // corresponding item:
            return this.getItem (((FormDataElement) key).value);
        } // else if the key is a variant

        // no valid type of the key => throw corresponding exception:
        throw new HttpComponentException (
            HttpLocalizedStrings.ASP_E_NON_STRING_DICT_KEY);
    } // get


    /**************************************************************************
     * Maps the specified key to the specified value in this dictionary.
     * Neither the key nor the value can be <CODE>null</CODE>. <BR/>
     * The value can be retrieved by calling the get method with a key that is
     * equal to the original key. <BR/>
     *
     * @param   key     The hash table key.
     * @param   value   The value of the object.
     *
     * @return  The previous value to which the key was mapped in this
     *          dictionary, or null if the key did not have a previous mapping.
     *
     * @exception   HttpComponentException
     *              The key is not of type String or of type Variant with
     *              variant type string.
     */
    public FormDataElement put (Object key, FormDataElement value)
        throws HttpComponentException
    {
        String strKey = null;           // string representation of key

        if (key instanceof String)      // the key is a string?
        {
            strKey = (String) key;      // set string representation
        } // if the key is a string
        else if (key instanceof FormDataElement) // the key is a form data?
        {
            strKey = ((FormDataElement) key).name; // get the name as key
        } // else if the key is a variant
        else                            // no valid type of the key
        {
            // throw corresponding exception:
            throw new HttpComponentException (
                HttpLocalizedStrings.ASP_E_NON_STRING_DICT_KEY);
        } // else no valid type of the key

        // get the object with the same key before inserting the new object:
        FormDataElement objPrevious = this.getItem (strKey);
        // set the new object:
        this.putItem (value);

        return objPrevious;             // return the previous object
    } // put


    /**************************************************************************
     * Removes the key (and its corresponding value) from this dictionary.
     * This method does nothing if the key is not in this dictionary. <BR/>
     *
     * @param   key     The key of the object to be removed.
     *
     * @return  The value to which the key had been mapped in this dictionary,
     *          or null if the key did not have a mapping.
     *
     * @exception   HttpComponentException
     *              The operation could not be performed correctly.
     *              This value is always returned since the remove operation
     *              is not implemented yet.
     */
    public FormDataElement remove (Object key) throws HttpComponentException
    {
        FormDataElement obj = this.get (key); // store the item
        this.formDataElements.removeElement (key); // remove it
        return obj;                     // return the stored item
    } // remove


    /**************************************************************************
     * Gets the path to the written file. <BR/>
     *
     * @param   key     The hash table key.
     *
     * @return  the path to the written file
     */
    public String getFilePath (String key)
    {
        String result = null;           // the result value
        FormDataElement formDataElement = this.getItem (key);
                                        // get the form data element
        if (formDataElement != null &&
            formDataElement.type == HttpConstants.T_FILE)
                                        // the element was found and is a file?
        {
            result = formDataElement.getFilePath ();
        } // if the element was found and is a file

        return result;                  // return the result
    } // getFilePath

} // class FormDataDictionary
