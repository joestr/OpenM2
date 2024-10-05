/*
 * Class: ISingleSelectObject.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * Object which performs a single select query for getting the data. <BR/>
 *
 * @version     $Id: ISingleSelectObject.java,v 1.2 2007/07/10 18:23:00 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR) 20060425
 ******************************************************************************
 */
public interface ISingleSelectObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ISingleSelectObject.java,v 1.2 2007/07/10 18:23:00 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the data into the object. <BR/>
     *
     * @param   stmt    The statement to be executed.
     */
    public void getRetrieveStatementData (SQLStatement stmt);

} // class ISingleSelectObject
