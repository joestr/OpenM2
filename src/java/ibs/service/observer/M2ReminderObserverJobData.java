/*
 * Class: m2ReminderObserverJobData.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.OID;
import ibs.service.observer.M2ParameterObserverJobData;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;


/******************************************************************************
 * m2ReminderObserverJobData does not hold any additional data. It is only
 * implemented to specifiy the tablename and for method: determinUniqueId. <BR/>
 * <BR/>
 * The following parameters of m2ParameterObserverJobdata are used:. <BR/>
 * - paramOid1  oid of business-object. <BR/>
 * - paramOid2  oid of workflow-object (must be empty if not used). <BR/>
 * - param0     string that specifies date duration. <BR/>
 *              ... date value: dd.mm.yyyy [hh:mm]. <BR/>
 *              ... duration values: 4711 m|h|d|w|m|y. <BR/>
 * - param1     name of user to notify. <BR/>
 * - param2     subject of notification. <BR/>
 * - param3     content of notification. <BR/>
 * - param4     description of notification. <BR/>
 * - param5     activity of notification. <BR/>
 * - param6     method (optional)<BR>*
 * <BR/>
 * By default the user given in param1 will be notified about the
 * object speficified in paramOid1. The text of the notification-message given
 * by param2, param3, param4, param5. <BR/>
 * <BR/>
 * If param6 is given instead of a notification the given method will be
 * executed. The executing object is be the object specified in oid1.
 * Signature of method must be:. <BR/>
 * void methodname ([object]) throws Exception. <BR/>
 * The object-parameter is always the m2ReminderObserverJob-object.
 *
 * @version     $Id: M2ReminderObserverJobData.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      HORST PICHLER
 ******************************************************************************
 */
public class M2ReminderObserverJobData extends M2ParameterObserverJobData
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ReminderObserverJobData.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $";

    /**
     * Reminder postfix (for table name and index). <BR/>
     */
    private static final String REMINDER_POSTFIX = "_reminder";


    /**************************************************************************
     * Public constructor. <BR/>
     */
    public M2ReminderObserverJobData ()
    {
        // nothing to do
    } // m2ReminderObserverJobData


    /**************************************************************************
     * Public constructor for a new m2ObserverJobData object.<BR> To use with
     * ObserverLoader.[un]register () or execute ().
     *
     * @param   context     Context of the observer job.
     * @param   className   Class name.
     * @param   name        Name of the job.
     * @param   paramOid1   Oid parameter 1.
     * @param   paramOid2   Oid parameter 2.
     * @param   param0      Parameter 0.
     * @param   param1      Parameter 1.
     * @param   param2      Parameter 2.
     * @param   param3      Parameter 3.
     * @param   param4      Parameter 4.
     * @param   param5      Parameter 5.
     * @param   param6      Parameter 6.
     * @param   param7      Parameter 7.
     * @param   param8      Parameter 8.
     * @param   param9      Parameter 9.
     */
    public M2ReminderObserverJobData (ObserverContext context,
                                      String className, String name,
                                      OID paramOid1, OID paramOid2,
                                      String param0, String param1,
                                      String param2, String param3,
                                      String param4, String param5,
                                      String param6, String param7,
                                      String param8, String param9)
    {
        // call constructor of super class:
        super (context, className, name, paramOid1, paramOid2,
            param0, param1, param2, param3, param4, param5, param6, param7, param8, param9);
    } // m2ReminderObserverJobData


    /**************************************************************************
     * Protected constructor for a new m2ObserverJobData object. <BR/>
     *
     * @param   context     The observer context.
     */
    protected M2ReminderObserverJobData (ObserverContext context)
    {
        // call constructor of super class:
        super (context);
    } // m2ReminderObserverJobData


    /**************************************************************************
     * Returns the unique-query. This query must assure that no job returned is
     * unique: results in one row.
     *
     * @return  The constructed query.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected String createUniquenessQuery () throws ObserverException
    {
        String query =
            " SELECT ext.id " +
            " FROM " + super.p_context.getTableName () + " base, " +
                     this.createTableName () + " ext " +
            " WHERE ext.paramOid1 = " + this.oid1.toStringQu ();

        // distinguish between: uniqueness defined by
        // * worklow: paramOid2
        // * username: param1
        if (this.oid2 != null && !(this.oid2.toStringQu () == null || this.oid2.isEmpty ()))
        {
            query += " AND ext.paramOid2 = " + this.oid2.toStringQu ();
        } // if
        else if (this.param1 != null && this.param1.length () > 0)
        {
            query += " AND ext.param1 = '" + this.param1 + "'";
        } // else if
        else
        {
            throw new ObserverException (this.getClass ().getName () +
                ".determineUniqueIdNonFinished: Uniqueness not determined!" +
                this.toString ());
        } // else

        query +=
            "   AND ext.id = base.id " +
            "   AND base.name = '" + this.getName () + "'" +
            "   AND base.className = '" + this.getClassName () + "'";

        return query;
    } // createUniquityQuery


    /**************************************************************************
     * Creates the table name for additional data.
     *
     * @return  The created table name.
     */
    public String createTableName ()
    {
        return this.getContext ().getTableName () +
            M2ReminderObserverJobData.REMINDER_POSTFIX;
    } // createTableName


    /**************************************************************************
     * Creates the index-prefix-name additional data.
     *
     * @return  The created index prefix.
     */
    public String createIndexPrefix ()
    {
        return this.getContext ().getIndexPrefix () +
            M2ReminderObserverJobData.REMINDER_POSTFIX;
    } // createIndexPrefix

} // m2ReminderObserverJobData
