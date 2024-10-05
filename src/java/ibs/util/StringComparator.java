/*
 * Class: StringComparator.java
 */

// package:
package ibs.util;

// imports:
import ibs.util.UtilConstants;

import java.util.Comparator;


/******************************************************************************
 * The StringComparator. <BR/>
*
 * @version     $Id: StringComparator.java,v 1.5 2007/07/10 09:24:29 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 030611
 ******************************************************************************
 */
public class StringComparator extends Object implements Comparator<String>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StringComparator.java,v 1.5 2007/07/10 09:24:29 kreimueller Exp $";


    /**
     * Ordering. <BR/>
     */
    private int p_ordering = UtilConstants.ORDER_ASC;


    /**************************************************************************
     * Creates a comparator with default ordering. <BR/>
     */
    public StringComparator ()
    {
        // nothing to do
    } // StringComparator


    /**************************************************************************
     * Creates a comparator with a certain ordering. <BR/>
     *
     * @param   ordering    Ordering for the comparator.
     */
    public StringComparator (int ordering)
    {
        this.p_ordering = ordering;
    } // StringComparator


    /**************************************************************************
     * The compare method from the comparator interface. <BR/>
     *
     * @param   o1  The first object to be compared.
     * @param   o2  The second object to be compared.
     *
     * @return  A negative integer, zero, or a positive integer as the
     *          first argument is less than, equal to, or greater than the
     *          second.
     */
    public int compare (String o1, String o2)
    {
        if (o1 == null && o2 == null)
        {
            return 0;
        } // if

        if (this.p_ordering == UtilConstants.ORDER_DESC) // descending?
        {
            if (o1 == null)
            {
                return 1;
            } // if
            else if (o2 == null)
            {
                return -1;
            } // else if
            else
            {
                return o2.compareTo (o1);
            } // else
        } // if descending

        // default is ascending
        if (o1 == null)
        {
            return -1;
        } // if
        else if (o2 == null)
        {
            return 1;
        } // else if
        else
        {
            return o1.compareTo (o2);
        } // else
    } // compare

} // class StringComparator
