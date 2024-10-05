/**
 * Class: AConfiguration
 */

// package:
package ibs.service.conf;

// imports:
import ibs.BaseObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;


/******************************************************************************
 * .
 *
 * @version     $Id: AConfigurationContainer.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Bernd Martin (BM) Oct 22, 2001
 ******************************************************************************
 */
public abstract class AConfigurationContainer extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AConfigurationContainer.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * a property collection all errors found in the configuration
     */
    protected StringBuffer errors = null;


    /**************************************************************************
     * Get the value of a specific field. <BR/>
     *
     * @param   f       The field.
     *
     * @return  The value of the field as object.
     *
     * @throws  IllegalAccessException
     *          There is no access to the field.
     */
    protected abstract Object getFieldValue (Field f)
        throws IllegalAccessException;


    /**************************************************************************
     * Get all fields which are declared within this container. <BR/>
     *
     * @return  The fields.
     */
    protected abstract Field[] getDeclaredFields ();


    /**************************************************************************
     * ???
     *
     * @param   message     The message string to add to.
     */
    protected void addErrorMessage (String message)
    {
        if (this.errors == null)
        {
            this.errors = new StringBuffer ();
        } // if init stringbuffer not done yet

        this.errors.append (message + "\n");
    } // addErrorMessage


    /**************************************************************************
     * Return the string representation of the Configuration. <BR/>
     * This method just concatenates the most important configuration properties
     * and creates a String out of them.
     *
     * @return  String representation of Configuration.
     */
    public String toString ()
    {
        Field[] fields = this.getDeclaredFields ();
                                        // the fields of the class
        String retVal = "";             // return value of the method
        String name = "";               // the name of the actual field
        int nameLength = 25;            // normal length of name
                                        // (for formatting)

        try
        {
            // loop through all fields of the current class and return these
            // ones which are allowed to be displayed:
            for (int i = 0; i < Array.getLength (fields); i++)
            {
                // get the field modifiers:
//                modifiers = fields[i].getModifiers ();

                // check if the field shall be displayed:
                // display just public fields which are not static.
                if (!fields[i].getName ().equalsIgnoreCase ("dbConf") &&
                    !fields[i].getName ().equalsIgnoreCase ("tracerConf"))
                {
                    name = fields[i].getName () + ": ";
                } // if not subrecord

                // add the field to the return value:
                //name = fields[i].getName () + ": ";
                name +=
                    "                                                  ".substring (0, nameLength - name.length ());
                retVal += name + this.getFieldValue (fields[i]) + "\n";
            } // for i
        } // try
        catch (IllegalAccessException e)
        {
            e.printStackTrace ();
            // the specified object is not an instance of the class or interface
            // declaring the underlying field (or a subclass or implementor
            // thereof)
        } // catch
        catch (IllegalArgumentException e)
        {
            e.printStackTrace ();
            // the specified object is not an instance of the class or interface
            // declaring the underlying field (or a subclass or implementor
            // thereof)
        } // catch
        catch (NullPointerException e)
        {
            e.printStackTrace ();
            // the specified object is null and the field is an instance field
        } // catch
        catch (ExceptionInInitializerError e)
        {
            // the initialization provoked by this method fails
        } // catch

        return retVal;                  // return the computed value
    } // toString

} // AConfiguration
