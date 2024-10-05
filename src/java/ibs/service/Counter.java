/*
 * Class: Counter.java
 */

// package:
package ibs.service;

// imports:
//KR TODO: unsauber
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;


/******************************************************************************
 * The Counter class to manage different counters to be used in forms. <BR/>
 *
 * @version     $Id: Counter.java,v 1.8 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Andreas Jansa (BW) 010827
 ******************************************************************************
 */
public class Counter extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Counter.java,v 1.8 2013/01/16 16:14:13 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class Counter. <BR/>
     */
    public Counter ()
    {
        // nothing to do
    } // Counter


    /**
     * Procedure get next count of one counter. <BR/>
     */
    private static final String PROC_GETNEXT =
        "p_Counter$getNext";

    /**
     * Procedure reset a counter. <BR/>
     */
    private static final String PROC_RESET =
        "p_Counter$reset";


    /**************************************************************************
     * getNext count of specific counter. <BR/>
     *
     * @param   counterName     the name of the counter to get next count from
     *
     * @return  the currentCount + 1 of the specified counter
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public int getNext (String counterName) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                Counter.PROC_GETNEXT,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // counterName
        sp.addInParameter (ParameterConstants.TYPE_STRING, counterName);

        // nextCount
        Parameter nextCount = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        return nextCount.getValueInteger ();
    } // getNext


    /**************************************************************************
     * getNext count of specific counter. <BR/>
     *
     * @param   counterName     the name of the counter to get next count from
     * @param   formatString    the format for the returned String
     *              every # is replaced with a number, leading # are replaced
     *              with 0. escapecharacter for # is \.
     *
     * @return  the currentCount + 1 of the specified counter formated like
     *          the given format in formatString
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public String getNextFormat (String counterName, String formatString)
        throws NoAccessException
    {
        int count = this.getNext (counterName);
        String countStr = Integer.toString (count);
            // contains the currect count as a String
        StringBuffer escapedFormatStr = new StringBuffer ();
            // contains only the escaped chars of formatStr
        StringBuffer unEscapedFormatStr = new StringBuffer ();
            // contains only the unescaped chars of formatStr
        StringBuffer countResultStr = new StringBuffer ();
        StringBuffer resultStr = new StringBuffer ();


        // seperate escaped chars from unescaped chars in formatString
        for (int i = 0; i < formatString.length (); i++)
        {
            if (formatString.charAt (i) == '\\' &&
                i < formatString.length () - 1 &&
                (formatString.charAt (i + 1) == '#' || formatString
                    .charAt (i + 1) == '\\'))
            {
                escapedFormatStr.append (formatString.charAt (++i));
            } // if
            else if (formatString.charAt (i) != '\\')
                // check if there is an unescaped \ in the formatString
            {
                escapedFormatStr.append ('_');
                unEscapedFormatStr.append (formatString.charAt (i));
            } // else

        } // for


        // fill counterStr into unEscapedFormatStr
        int j = countStr.length () - 1;

        for (int i = unEscapedFormatStr.length () - 1; i >= 0; i--)
        {
            // replace the character # with the current count
            if (unEscapedFormatStr.charAt (i) == '#')
            {
                // check if the # should be fullfilled with "0"
                if (j >= 0)
                {
                    countResultStr.insert (0, countStr.charAt (j--));
                } // if
                else
                {
                    countResultStr.insert (0, '0');
                } // else
            } // if
            else
            {
                countResultStr.insert (0, unEscapedFormatStr.charAt (i));
            } // else
        } // for


        // join escaped chars of formatString with the result
        // of fullfilling the unEscapedFormatStr with the current count
        j = 0;
        for (int i = 0; i < escapedFormatStr.length (); i++)
        {
            if (escapedFormatStr.charAt (i) != '_')
            {
                resultStr.append (escapedFormatStr.charAt (i));
            } // if
            else
            {
                resultStr.append (countResultStr.charAt (j++));
            } // else
        } // for

        return resultStr.toString ();
    } // getNext


    /**************************************************************************
     * set specified counter to 0. <BR/>
     *
     * @param   counterName     the name of the counter to be reseted
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public void reset (String counterName) throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                Counter.PROC_RESET,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // counterName
        sp.addInParameter (ParameterConstants.TYPE_STRING, counterName);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);
    } // getNext

} // class Counter
