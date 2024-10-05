/*
 * Class: TermElement.java
 */

// package:
package m2.diary;


// imports:
import ibs.BaseObject;
import ibs.bo.OID;

import java.util.Date;


/******************************************************************************
 * This class represents one Term. <BR/>
 * Used in Termin_01.
 *
 * @version     $Id: TermElement.java,v 1.4 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 980424
 ******************************************************************************
 */
public class TermElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TermElement.java,v 1.4 2007/07/23 08:21:36 kreimueller Exp $";


    /**
     * . <BR/>
     */
    public OID oid;
    /**
     * . <BR/>
     */
    public String name;
    /**
     * . <BR/>
     */
    public Date startDate;
    /**
     * . <BR/>
     */
    public Date endDate;
    /**
     * . <BR/>
     */
    public String place;
    /**
     * . <BR/>
     */
    public int year;
    /**
     * . <BR/>
     */
    public boolean participants;
    /**
     * . <BR/>
     */
    public int numParticipants;

    /**
     * Number of days - needed for multi-day-terms. <BR/>
     */
    public int numDays;

} // TermElement
