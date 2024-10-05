/*
 * Class: SQLQueryConstants.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * This class defines the constants used for sql queries. <BR/>
 *
 * @version     $Id: SQLQueryConstants.java,v 1.2 2007/07/10 18:23:00 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR) 20060416
 ******************************************************************************
 */
public class SQLQueryConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLQueryConstants.java,v 1.2 2007/07/10 18:23:00 kreimueller Exp $";


    /**
     * Query literal: SELECT. <BR/>
     */
    public static final StringBuffer QL_SELECT =
        new StringBuffer ("SELECT ");

    /**
     * Query literal: SELECT DISTINCT. <BR/>
     */
    public static final StringBuffer QL_SELECTDISTINCT =
        new StringBuffer ("SELECT DISTINCT ");

    /**
     * Query literal: FROM. <BR/>
     */
    public static final StringBuffer QL_FROM = new StringBuffer (" FROM ");

    /**
     * Query literal: WHERE. <BR/>
     */
    public static final StringBuffer QL_WHERE = new StringBuffer (" WHERE ");

    /**
     * Query literal: GROUP BY. <BR/>
     */
    public static final StringBuffer QL_GROUPBY =
        new StringBuffer (" GROUP BY ");

    /**
     * Query literal: HAVING. <BR/>
     */
    public static final StringBuffer QL_HAVING =
        new StringBuffer (" HAVING ");

    /**
     * Query literal: ORDER BY. <BR/>
     */
    public static final StringBuffer QL_ORDERBY =
        new StringBuffer (" ORDER BY ");

    /**
     * Query literal: INSERT. <BR/>
     */
    public static final StringBuffer QL_INSERT =
        new StringBuffer ("INSERT INTO ");

    /**
     * Query literal: VALUES. <BR/>
     */
    public static final StringBuffer QL_VALUES =
        new StringBuffer (" VALUES ");

    /**
     * Query literal: UPDATE. <BR/>
     */
    public static final StringBuffer QL_UPDATE =
        new StringBuffer ("UPDATE ");

    /**
     * Query literal: SET. <BR/>
     */
    public static final StringBuffer QL_SET = new StringBuffer (" SET ");

    /**
     * Query literal: DELETE. <BR/>
     */
    public static final StringBuffer QL_DELETE =
        new StringBuffer ("DELETE FROM ");

    /**
     * Query literal: AND. <BR/>
     */
    public static final StringBuffer QL_AND = new StringBuffer (" AND ");

    /**
     * Query literal: OR. <BR/>
     */
    public static final StringBuffer QL_OR = new StringBuffer (" OR ");

    /**
     * Query literal: comma (","). <BR/>
     */
    public static final StringBuffer QL_COMMA = new StringBuffer (",");

    /**
     * Query literal: space (" "). <BR/>
     */
    public static final StringBuffer QL_SPACE = new StringBuffer (" ");

    /**
     * Query literal: assignment ("="). <BR/>
     */
    public static final StringBuffer QL_ASSIGN = new StringBuffer ("=");

    /**
     * Query literal: left parenthesis (" ("). <BR/>
     */
    public static final StringBuffer QL_LEFTPARENTHESIS =
        new StringBuffer (" (");

    /**
     * Query literal: right parenthesis (" ("). <BR/>
     */
    public static final StringBuffer QL_RIGHTPARENTHESIS =
        new StringBuffer (")");

} // class SQLQueryConstants
