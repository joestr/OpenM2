/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 07.08.2002
 * Time: 18:40:31
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.service.observer.ObserverJobData;

import java.util.Comparator;
import java.util.Date;


/******************************************************************************
 * A comparison function, which imposes a total ordering on a collection of
 * ObserverJobData-objects. This Comparator can be passed to a sort method
 * (such as Collections.sort) to allow precise control over the sort order.
 * Comparators can also be used to control the order of certain data structures
 * (such as TreeSet or TreeMap). <BR/>
 *
 * The order of ObserverJobData-objects is determined by their property
 * 'nextExecution' of type Date.
 *
 * @version     $Id: ObserverJobDataComparator.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      HORST PICHLER, 07.08.2002
 ******************************************************************************
 */
public class ObserverJobDataComparator extends BaseObject implements Comparator<ObserverJobData>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverJobDataComparator.java,v 1.5 2007/07/24 21:27:33 kreimueller Exp $";


    /**************************************************************************
     * Constructor for an ObserverJobDataComparator object. <BR/>
     */
    public ObserverJobDataComparator ()
    {
        // nothing to do
    } // ObserverJobDataComparator


    /**************************************************************************
     * Compares its two arguments for order. Returns a negative integer, zero,
     * or a positive integer as the first argument is less than, equal to, or
     * greater than the second. <BR/>
     *
     * The order of ObserverJobData-objects is determined by their property
     * 'nextExecution' of type Date.

     * @param   oj1     The first object to be compared.
     * @param   oj2     The second object to be compared.
     *
     * @return  A negative integer, zero, or a positive integer as the first
     *          argument is less than, equal to, or greater than the second.
     */
    public int compare (ObserverJobData oj1, ObserverJobData oj2)
    {
        Date oj1Next = null;
        Date oj2Next = null;

        // check null values
        if ((oj1Next = oj1.getNextExecution ()) == null)
        {
            return -1;
        } // if
        if ((oj2Next = oj2.getNextExecution ()) == null)
        {
            return 1;
        } // if

        if (oj1Next.after (oj2Next))
        {
            return -1;
        } // if
        else if (oj1Next.before (oj2Next))
        {
            return 1;
        } // else if

        if (oj1.getId () == oj2.getId ())
        {
            return 0;
        } // if

        return 1;
    } // compare

} // class ObserverJobDataComparator

