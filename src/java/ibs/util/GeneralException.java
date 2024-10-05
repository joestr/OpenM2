/*
 * Class: GeneralException.java
 */

// package:
package ibs.util;

// imports:
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;


/******************************************************************************
 * This class implements the error handler. <BR/>
 * An Error mapping file is used to get meaningful error messages according to
 * the rather abstract error codes returned by implementation specific
 * classes.<P>
 * Note that errors could be chained - this is represented by a vector
 * containing all errors that happened on the way to and from the database.
 *
 * @version     $Id: GeneralException.java,v 1.10 2007/07/31 19:14:00 kreimueller Exp $
 *
 * @author      Manfred Rieder (MR) 970811
 ******************************************************************************
 */
public class GeneralException extends Throwable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: GeneralException.java,v 1.10 2007/07/31 19:14:00 kreimueller Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = 2234670187861398927L;


    /**
     * The name of the file containing the error codes to be used within this
     * application. This file has to be in the same directory.
     */
    private static final String FILENAME = "Exceptions.txt";

    /**
     *
     */
    private Vector<String> errorlist;

    /**
     *
     */
    private Vector<String[]> filelist;


    /**************************************************************************
     * Create a new exception. <BR/>
     * This method calls the constructor of the super class and initializes all
     * properties of this class.
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public GeneralException (String name)
    {
        // call constructor of super class:
        super (name);

        // initialize this class's properties:
        this.initialize ();
    } // GeneralException


    /**************************************************************************
     * Constructs a new throwable with the specified cause and a detail
     * message of <tt> (cause==null ? null : cause.toString ())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for throwables that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * <p>The {@link #fillInStackTrace ()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param   cause   The cause (which is saved for later retrieval by the
     *                  {@link #getCause ()} method).  (A <tt>null</tt> value is
     *                  permitted, and indicates that the cause is nonexistent
     *                  or unknown.)
     */
    public GeneralException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);

        // initialize this class's properties:
        this.initialize ();
    } // GeneralException


    /**************************************************************************
     * Constructs a new throwable with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this throwable's detail message.
     *
     * <p>The {@link #fillInStackTrace ()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     * @param   cause   The cause (which is saved for later retrieval by the
     *                  {@link #getCause ()} method).  (A <tt>null</tt> value is
     *                  permitted, and indicates that the cause is nonexistent
     *                  or unknown.)
     */
    public GeneralException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);

        // initialize this class's properties:
        this.initialize ();
    } // GeneralException


    /**************************************************************************
     * Initialize the classes' properties. <BR/>
     */
    public void initialize ()
    {
        // initialize this class's properties:
        this.errorlist = new Vector<String> ();
        this.filelist = new Vector<String[]> ();
        String[] errorArr = {"", ""};

        // load the error code from a file stored in the file system:
        try
        {
            try
            {
                // open filestream handlers
                FileInputStream fileIn =
                    new FileInputStream (GeneralException.FILENAME);
                //DataInputStream dataIn = new DataInputStream (fileIn);
                BufferedReader dataIn = new BufferedReader (new InputStreamReader (fileIn));
                // search for the errorcode
                while (errorArr[0] != null)
                {
//! Warning:
//! 'String readLine ()' has been deprecated by the author of 'java.io.DataInputStream'
                    errorArr[0] = dataIn.readLine ();
                    errorArr[1] = dataIn.readLine ();
                    if ((errorArr[0] != null) && (errorArr[1] != null))
                    {
                        this.filelist.addElement (errorArr);
                    } // if
                } // while

            } // try
            catch (IOException e)
            {
                // end of file reached or reader disappeared
            } // catch
        } // try
        catch (Exception e)
        {
            // nothing to do
        } // catch
    } // initialize


    /**************************************************************************
     * Get and remove the first error from the errorset. <BR/>
     * Look for an error description in the errors file, which has to be in the
     * same directory.
     *
     * @return  The owner object.
     */
    public String getError ()
    {
        String errorStr = "";
        String fileStr = "";
        String retStr = "";

        try
        {
            errorStr = this.errorlist.firstElement ();
            retStr = "\n* ErrorCode: " + errorStr;
            for (int i = 0; i < this.filelist.size (); i++)
            {
                fileStr = this.filelist.firstElement ()[0];
                if (!errorStr.equals (fileStr))
                {
                    retStr += "Description: " +
                        this.filelist.firstElement ()[1] +
                        "\n";
                    break;
                } // if
            } // for
            this.errorlist.removeElementAt (0);
        } // try
        catch (Exception e)
        {
            retStr = "" + e;
        } // catch

        return retStr;
    } // getError


    /**************************************************************************
     * Add an error to the errorset. <BR/>
     *
     * @param   err     Error to be added to the errorset.
     */
    public void addError (String err)
    {
        this.errorlist.addElement (err); // add the new error to the errorset
    } // addError


    /**************************************************************************
     * Check whether the errorset is empty or not. <BR/>
     *
     * @return  true if it is empty.
     */
    public boolean isEmpty ()
    {
        // check if the errorlist is empty and return the result:
        return this.errorlist.isEmpty ();
    } // isEmpty

} // class GeneralException
