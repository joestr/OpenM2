/*
 * Class: FormDataElement.java
 */

// package:
package ibs.tech.http;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.io.AContext;
//KR TODO: unsauber
import ibs.io.UploadException;
import ibs.tech.http.FormDataValue;
import ibs.tech.http.HttpConstants;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Handle the data of a form. <BR/>
 *
 * @version     $Id: FormDataElement.java,v 1.14 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 981002
 ******************************************************************************
 */
public class FormDataElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormDataElement.java,v 1.14 2007/07/24 21:27:02 kreimueller Exp $";


    /**
     * The name of the form data. <BR/>
     */
    public String name = null;

    /**
     * The type of the form data. <BR/>
     */
    public int type = 0;

    /**
     * The value of the form data. <BR/>
     */
    public String value = null;

    /**
     * The size of the content, i.e. the value. <BR/>
     */
    public int size = 0;

    /**
     * Beginning of the value within the raw data string. <BR/>
     */
    public int startPos = 0;

    /**
     * The content type. <BR/>
     */
    public String contentType = null;

    /**
     * The filename if file upload. <BR/>
     */
    public String filename = null;

    /**
     * The destination directory of files. <BR/>
     */
    public String targetDir = "c:\\Temp\\";

    /**
     * Vector containing the multiple values, i.e. values and their sizes and
     * starting positions. <BR/>
     * This property contains the {@link #value value} if the form data
     * element is a list of values (MULTIPLE form data).
     */
    public Vector<FormDataValue> values = null;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;


    /**************************************************************************
     * Create a new FormDataElement instance. <BR/>
     */
    public FormDataElement ()
    {
        // initialize the values vector:
        // create with one element and add 5 elements if there is an overflow.
        this.values = new Vector<FormDataValue> (1, 5);
    } // FormDataElement


    /**************************************************************************
     * Create a new FormDataElement instance. <BR/>
     *
     * @param   value       The (first) value of the element.
     */
    public FormDataElement (FormDataValue value)
    {
        // set this instance's properties:
        this.name = value.name;
        this.type = value.type;
        this.contentType = value.contentType;
        this.filename = value.filename;
        this.value = value.value;
        this.size = value.size;
        this.startPos = value.startPos;

        // initialize the values vector and add the first element:
        // create with one element and add 5 elements if there is an overflow.
        this.values = new Vector<FormDataValue> (1, 5);
        this.values.addElement (value);
    } // FormDataElement


    /**************************************************************************
     * Create a new FormDataElement instance. <BR/>
     *
     * @param   data        The raw form of the data.
     * @param   startPos    The starting position of this form data within the
     *                      stream.
     * @param   cxt         The current context.
     *
     * @throws  UploadException
     *          There occurred an error during uploading the file data.
     */
    public FormDataElement (String data, int startPos, AContext cxt)
        throws UploadException
    {
        FormDataValue value = new FormDataValue (data, startPos, cxt);

        // set this instance's properties:
        this.name = value.name;
        this.type = value.type;
        this.contentType = value.contentType;
        this.filename = value.filename;
        this.value = value.value;
        this.size = value.size;
        this.startPos = value.startPos;

        // initialize the values vector and add the first element:
        // create with one element and add 5 elements if there is an overflow.
        this.values = new Vector<FormDataValue> (1, 5);
        this.values.addElement (value);
    } // FormDataElement


    /**************************************************************************
     * Add a new value to this element. <BR/>
     *
     * @param   value   The value to be added to this element.
     */
    public void put (FormDataValue value)
    {
        // add the element to the values vector:
        this.values.addElement (value);

        if (this.values.size () == 1)   // this is the first value?
        {
            // set this instance's properties:
            this.name = value.name;
            this.type = value.type;
            this.contentType = value.contentType;
            this.filename = value.filename;
            this.value = value.value;
            this.size = value.size;
            this.startPos = value.startPos;
        } // if this is the first value
        else                            // not the first value
        {
            this.type = HttpConstants.T_POSTMULTIPLE; // multiple values
        } // else not the first value
    } // put


    /**************************************************************************
     * Add a new value to this element. <BR/>
     *
     * @param   value   The value to be added to this element.
     */
    public void put (String value)
    {
        // add the element to the values vector:
        this.values.addElement (new FormDataValue (this.name, value, 0, null));

        if (this.values.size () == 1)   // this is the first value?
        {
            // set this instance's properties:
            this.type = HttpConstants.T_POST;
            this.value = value;
            /*
            this.contentType = value.contentType;
            this.filename = value.filename;
            this.size = value.size;
            this.startPos = value.startPos;
            */
        } // if this is the first value
        else                            // not the first value
        {
            this.type = HttpConstants.T_POSTMULTIPLE; // multiple values
        } // else not the first value
    } // put


    /***************************************************************************
     * Sends this form element's values to their corresponding files. <BR/>
     *
     * @param   stream      The stream where the raw data reside.
     * @param   targetDir   Target directory where to write a file within the
     *                      form data.
     *
     * @return  The resulting target file name. This is the target file name
     *          of the last value of this element.
     *
     * @throws  UploadException
     *          There occurred an error during uploading the file data.
     */
    public String writeFile (byte[] stream, String targetDir)
        throws UploadException
    {
        String targetFilename = null;   // the filename of the last value
        FormDataValue temp = null;

        if (targetDir != null)          // there is an explicit target
        {
            // directory set?
            this.targetDir = targetDir; // store the directory
        } // if

        if (this.values != null)
        {
            // loop through all values and write them to their files:
            for (Iterator<FormDataValue> iter = this.values.iterator (); iter.hasNext ();)
            {
                temp = iter.next ();

                if (temp != null && temp.filename != null)
                {
                    targetFilename = temp.writeFile (stream, this.targetDir);
                } // if
            } // for iter
        } // if

        // return the last target file name:
        return targetFilename;
    } // writeFile


    /**************************************************************************
     * Compares two Objects for equality. <BR/>
     *
     * @param   obj     The reference object with which to compare.
     *
     * @return  <CODE>true</CODE> if this object is the same as the obj
     *          argument; <CODE>false</CODE> otherwise.
     */
    public boolean equals (Object obj)
    {
        if (obj instanceof FormDataElement) // FormDataElement object?
        {
            // compare the name of this element with the name of the other one
            // and return the result:
            return this.name.equals (((FormDataElement) obj).name);
        } // if FormDataElement object
        else if (obj instanceof String) // string object?
        {
            // compare the name of this element with the name of the other one
            // and return the result:
            return this.name.equals (obj);
        } // else if string object
        else                            // no compatible type
        {
            return false;               // the object is different
        } // else no compatible type
    } // equals


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // check if a valid hash code was set:
        if (this.p_hashCode == Integer.MIN_VALUE)
        {
            // check if the name is set:
            if (this.name != null)
            {
                // compute the hash code from the name:
                this.p_hashCode = this.name.hashCode ();
            } // if
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode


    /**************************************************************************
     * Gets the path to the written file. <BR/>
     *
     * @return  the path to the written file
     */
    public String getFilePath ()
    {
        String result = null;   // the filename + path of the latest value
        FormDataValue temp = null;

        // loop through all values and write them to their files:
        for (Iterator<FormDataValue> iter = this.values.iterator (); iter.hasNext ();)
        {
            temp = iter.next ();

            if (temp != null && temp.filename != null)
            {
                result = temp.getFilePath ();
            } // if
        } // for iter

        // return the last target file name:
        return result;
    } // getFilePath

} // class FormDataElement
