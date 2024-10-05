/*
 * Class: InsertStatement.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class represents an INSERT query. <BR/>
 * Format 1:
 * <PRE>
 *      INSERT INTO tableName
 *                  [fieldsClause]
 *      VALUES      valuesClause
 * </PRE>
 * Format 2:
 * <PRE>
 *      INSERT INTO tableName
 *                  [fieldsClause]
 *      SELECT      ...
 * </PRE>
 *
 * @version     $Id: InsertStatement.java,v 1.7 2009/12/01 14:51:03 btatzmann Exp $
 *
 * @author      klaus, 25.06.2005
 ******************************************************************************
 */
public class InsertStatement extends SQLManipulationStatement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: InsertStatement.java,v 1.7 2009/12/01 14:51:03 btatzmann Exp $";


    /**
     * The table name of the query. <BR/>
     */
    private StringBuilder p_tableName = null;

    /**
     * The fields clause of the query. <BR/>
     */
    private StringBuilder p_fields = null;

    /**
     * The VALUES clause of the query. <BR/>
     */
    private StringBuilder p_values = null;

    /**
     * The SELECT query for getting the values. <BR/>
     */
    private SelectQuery p_selectQuery = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a InsertStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   fields      The fields to be set.
     * @param   values      VALUES part of the query.
     */
    public InsertStatement (StringBuilder tableName, StringBuilder fields,
                        StringBuilder values)
    {
        // set the properties:
        this.p_tableName = tableName;
        this.p_fields = fields;
        this.p_values = values;

        // this is an action query:
        this.p_isAction = true;
    } // InsertStatement


    /**************************************************************************
     * Creates a InsertStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   fields      The fields to be set.
     * @param   selectQuery The select query.
     */
    public InsertStatement (StringBuilder tableName, StringBuilder fields,
                        SelectQuery selectQuery)
    {
        // set the properties:
        this.p_tableName = tableName;
        this.p_fields = fields;
        this.p_selectQuery = selectQuery;

        // this is an action query:
        this.p_isAction = true;
    } // InsertStatement


    /**************************************************************************
     * Creates a InsertStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   fields      The fields to be set.
     * @param   values      VALUES part of the query.
     *
     * @throws  NullPointerException
     *          The table name was <CODE>null</CODE>.
     */
    public InsertStatement (String tableName, String fields, String values)
        throws NullPointerException
    {
        // set the properties:
        this.p_tableName = new StringBuilder (tableName);
        this.p_fields = fields == null ? null : new StringBuilder (fields);
        this.p_values = values == null ? null : new StringBuilder (values);

        // this is an action query:
        this.p_isAction = true;
    } // InsertStatement


    /**************************************************************************
     * Creates a InsertStatement object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * Not all query parts have to be initialized. If not available just set the
     * value <CODE>null</CODE>. <BR/>
     * IMPORTANT: The <CODE>SELECT</CODE> and the <CODE>FROM</CODE> part are not
     * optional when executing a query.
     *
     * @param   tableName   Name of table on which to perform the query.
     *                      The table name must not be <CODE>null</CODE>!
     * @param   fields      The fields to be set.
     * @param   selectQuery The select query.
     *
     * @throws  NullPointerException
     *          The table name was <CODE>null</CODE>.
     */
    public InsertStatement (String tableName, String fields,
                            SelectQuery selectQuery)
        throws NullPointerException
    {
        // set the properties:
        this.p_tableName = new StringBuilder (tableName);
        this.p_fields = fields == null ? null : new StringBuilder (fields);
        this.p_selectQuery = selectQuery;

        // this is an action query:
        this.p_isAction = true;
    } // InsertStatement



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
        if (this.p_tableName == null ||
            this.p_tableName.length () == 0 ||
            (this.p_selectQuery == null && (this.p_values == null || this.p_values
                .length () == 0)))
        {
            // throw corresponding exception:
            throw new DBQueryException (
                "Missing table name or VALUES/SELECT part within INSERT query: " +
                    retVal);
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
            .append (SQLQueryConstants.QL_INSERT)
            .append (this.p_tableName);
        if (this.p_fields != null)
        {
            retVal
                .append (SQLQueryConstants.QL_LEFTPARENTHESIS)
                .append (this.p_fields)
                .append (SQLQueryConstants.QL_RIGHTPARENTHESIS);
        } // if
        if (this.p_values != null)
        {
            retVal
                .append (SQLQueryConstants.QL_VALUES)
                .append (SQLQueryConstants.QL_LEFTPARENTHESIS)
                .append (this.p_values)
                .append (SQLQueryConstants.QL_RIGHTPARENTHESIS);
        } // if
        else if (this.p_selectQuery != null)
        {
            retVal
                .append (this.p_selectQuery.toStringBuilder ());
        } // else if

        // return the result:
        return retVal;
    } // toStringBuilder


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   fields  Additional fields of query.
     * @param   values  Additional VALUES part of the query.
     *
     * @see #add (StringBuilder, StringBuilder)
     */
    public void add (String fields, String values)
    {
        // call common method:
        this.add (
            fields == null ? null : new StringBuilder (fields),
            values == null ? null : new StringBuilder (values));
    } // add
    
    
    /**************************************************************************
     * Add a string field to the query. <BR/>
     *
     * @param   field  Additional field of query.
     * @param   value  Additional VALUES part for the string.
     *
     * @see #add (StringBuilder, StringBuilder)
     */
    public void addUnicodeString (String field, String value)
    {       
        // call common method:
        this.add (
            field == null ? null : new StringBuilder (field),
            value == null ? null : SQLHelpers.getUnicodeString (value));
    } // addString


    /**************************************************************************
     * Add something to the query. <BR/>
     *
     * @param   fields  Additional fields of query.
     *                  If the fields part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  fields.
     * @param   values  Additional VALUES part of the query.
     *                  If the VALUES part already contains something
     *                  <CODE>","</CODE> is appended before the additional
     *                  VALUES part.
     */
    public void add (StringBuilder fields, StringBuilder values)
    {
        // add the strings:
        if (fields != null && values != null)
        {
            if (this.p_fields != null)
            {
                this.p_fields
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (fields);
            } // if
            else
            {
                this.p_fields = fields;
            } // else
        } // if
        if (values != null)
        {
            if (this.p_values != null)
            {
                this.p_values
                    .append (SQLQueryConstants.QL_COMMA)
                    .append (values);
            } // if
            else
            {
                this.p_values = values;
            } // else
        } // if
    } // add


    /**************************************************************************
     * Set the select query. <BR/>
     *
     * @param   selectQuery The select query to be set.
     */
    public void setSelectQuery (SelectQuery selectQuery)
    {
        // set the value:
        this.p_selectQuery = selectQuery;
    } // setSelectQuery

} // class InsertStatement
