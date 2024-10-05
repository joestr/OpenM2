/*
 * Class: UpdateStatement.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents an UPDATE query. <BR/>
 * Format:
 * <PRE>
 *      UPDATE    tableName
 *      SET       setClause
 *      [WHERE    whereClause]
 * </PRE>
 *
 * @version     $Id: UpdateStatement.java,v 1.9 2009/12/01 14:51:03 btatzmann Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public class UpdateStatement extends SQLManipulationStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UpdateStatement.java,v 1.9 2009/12/01 14:51:03 btatzmann Exp $";


    /**
     * The table name of the query. <BR/>
     */
    private StringBuilder p_tableName = null;

    /**
     * The SET clause of the query. <BR/>
     */
    private StringBuilder p_set = null;

    /**
     * The WHERE clause of the query. <BR/>
     */
    private StringBuilder p_where = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a UpdateStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   set         SET part of the query.
     * @param   where       WHERE part of the query.
     */
    public UpdateStatement (StringBuilder tableName, StringBuilder set,
                            StringBuilder where)
    {
        // set the properties:
        this.p_tableName = tableName;
        this.p_set = set;
        this.p_where = where;

        // this is an action query:
        this.p_isAction = true;
    } // UpdateStatement


    /**************************************************************************
     * Creates a UpdateStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   set         SET part of the query.
     * @param   where       WHERE part of the query.
     *
     * @throws  NullPointerException
     *          The table name was <CODE>null</CODE>.
     */
    public UpdateStatement (String tableName, String set, String where)
        throws NullPointerException
    {
        // set the properties:
        this.p_tableName = new StringBuilder (tableName);
        this.p_set = set == null ? null : new StringBuilder (set);
        this.p_where = where == null ? null : new StringBuilder (where);

        // this is an action query:
        this.p_isAction = true;
    } // UpdateStatement



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the StringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query. <BR/>
     * It calls {@link #toStringBuilder () toStringBuilder} and performs a check
     * if the query is valid.
     *
     * @return  The generated StringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @throws  DBQueryException
     *          This exception is thrown if not all necessary parts of the
     *          query are set or there is another problem with the query.
     */
    @Override
    public StringBuilder toValidStringBuilder () throws DBQueryException
    {
        StringBuilder retVal = null;     // the return value

        // get the query:
        retVal = this.toStringBuilder ();

        // check if all necessary query parts have been set:
        if (this.p_tableName == null || this.p_tableName.length () == 0 ||
            this.p_set == null || this.p_set.length () == 0)
        {
            // throw corresponding exception:
            throw new DBQueryException (
                "Missing table name or SET part within UPDATE query: " + retVal);
        } // if

        // return the result:
        return retVal;
    } // toValidStringBuilder


    /**************************************************************************
     * Get the StringBuilder representation of the query. <BR/>
     * Especially this method concatenates all parts of the query.
     *
     * @return  The generated StringBuilder or
     *          <CODE>""</CODE> if the query is empty or
     *          <CODE>null</CODE> if there occurred an error.
     */
    @Override
    public StringBuilder toStringBuilder ()
    {
        StringBuilder retVal = new StringBuilder (); // the return value

        // construct the query and return it:
        retVal
            .append (SQLQueryConstants.QL_UPDATE)
            .append (this.p_tableName)
            .append (SQLQueryConstants.QL_SET)
            .append (this.p_set);
        if (this.p_where != null)
        {
            retVal
                .append (SQLQueryConstants.QL_WHERE)
                .append (this.p_where);
        } // if

        // return the result:
        return retVal;
    } // toStringBuilder


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   set     Additional SET part of query.
     * @param   where   Additional WHERE part of the query.
     *
     * @see #add (StringBuilder, StringBuilder)
     */
    public void add (String set, String where)
    {
        // call common method:
        this.add (
            set == null ? null : new StringBuilder (set),
            where == null ? null : new StringBuilder (where));
    } // add
    
    
    /**************************************************************************
     * Add a unicode String to the set section of the update statement. <BR/>
     *
     * @param   setField    The field name.
     * @param   setValue    The value to update the field to.
     *
     * @see #add (StringBuilder, StringBuilder)
     */
    public void addUnicodeStringToSet (String setField, String setValue)
    {
        if (setField == null || setValue == null)
        {
            return;
        } // if
        
        // call common method:
        this.add (
            new StringBuilder (setField).
                append ("=").
                append (SQLHelpers.getUnicodeString (setValue)),
            null);
    } // addUnicodeStringToSet
    
    
    /**************************************************************************
     * Add a unicode Text to the set section of the update statement. <BR/>
     *
     * @param   setField    The field name.
     * @param   setValue    The value to update the field to.
     *
     * @see #add (StringBuilder, StringBuilder)
     */
    public void addUnicodeTextToSet (String setField, String setValue)
    {
        if (setField == null || setValue == null)
        {
            return;
        } // if
        
        // call common method:
        this.add (
            new StringBuilder (setField).
                append ("=").
                append (SQLHelpers.getUnicodeString (SQLHelpers.asciiToDb (setValue))),
            null);
    } // addUnicodeTextToSet


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   set     Additional SET part of query.
     *                  If the SET part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  SET part.
     * @param   where   Additional WHERE part of the query.
     *                  If the WHERE part already contains something
     *                  <CODE>" "</CODE> is appended before the additional
     *                  WHERE part.
     */
    public void add (StringBuilder set, StringBuilder where)
    {
        // add the strings:
        if (set != null)
        {
            if (this.p_set != null)
            {
                this.p_set
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (set);
            } // if
            else
            {
                this.p_set = set;
            } // else
        } // if
        if (where != null)
        {
            if (this.p_where != null)
            {
                this.p_where
                    .append (SQLQueryConstants.QL_SPACE)
                    .append (where);
            } // if
            else
            {
                this.p_where = where;
            } // else
        } // if
    } // add


    /**************************************************************************
     * Extend the SET clause. <BR/>
     * This method extends the set clause with a new statement.
     * The statement has the form "&lt;attribute> = &lt;value>".
     *
     * @param   attribute   The attribute to be set.
     * @param   value       The value for the attribute.
     *
     * @see #extendSet (StringBuilder)
     */
    public void extendSet (String attribute, String value)
    {
        // call common method:
        this.extendSet (new StringBuilder ().append (attribute),
                        new StringBuilder ().append (value));
    } // extendSet


    /**************************************************************************
     * Extend the SET clause. <BR/>
     * This method extends the set clause with a new statement.
     * The statement has the form "&lt;attribute> = &lt;value>".
     *
     * @param   attribute   The attribute to be set.
     * @param   value       The value for the attribute.
     *
     * @see #extendSet (StringBuilder)
     */
    public void extendSet (StringBuilder attribute, StringBuilder value)
    {
        // create the assignment string and add it to the SET clause:
        this.extendSet (new StringBuilder ()
            .append (attribute)
            .append (SQLQueryConstants.QL_ASSIGN)
            .append (value));
    } // extendSet


    /**************************************************************************
     * Extend the SET clause. <BR/>
     * This method extends the set clause with the new String. If the SET
     * clause already has a not empty content the separator <CODE>","</CODE>
     * is inserted before appending the new String. <BR/>
     * If the String is empty just <CODE>","</CODE> is concatenated if
     * necessary.
     *
     * @param   set     The Stirng to append to the SET clause.
     */
    public void extendSet (StringBuilder set)
    {
        // check if we have to append something:
        // important: an empty String means just to append the "," keyword
        if (set != null)
        {
            // check if the SET already has a content:
            if (this.p_set == null)
            {
                // initialize the SET clause and add the new string:
                this.p_set = set;
            } // if
            else if (this.p_set.length () > 0)
            {
                // append the "," and the new string to the SET clause:
                this.p_set.append (SQLQueryConstants.QL_COMMA).append (set);
            } // else if
        } // if
    } // extendSet


    /**************************************************************************
     * Extend the WHERE clause. <BR/>
     * This method extends the where clause with the new String. If the WHERE
     * clause already has a not empty content the keyword <CODE>" AND "</CODE>
     * (with spaces) is inserted before appending the new String. <BR/>
     * If the String is empty just <CODE>" AND "</CODE> is concatenated if
     * necessary.
     *
     * @param   where   The Stirng to append to the WHERE clause.
     */
    public void extendWhere (StringBuilder where)
    {
        // call common method:
        this.p_where = this.extendWhere (this.p_where, where);
    } // extendWhere

} // class UpdateStatement
