-------------------------------------------------------------------------------
-- Table for dynamic Search - Queries
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_QUERYCREATOR_01
(
    QUERYTYPE       INTEGER,            
                                        -- bit pattern to restrict where query
                                        -- could be used
    GROUPBYSTRING   VARCHAR (255),       
                                        -- groupBy string of sql-query without
                                        -- String 'GROUP BY'
    ORDERBYSTRING   VARCHAR (255),       
                                        -- orderBy string of sql-query without
                                        -- String 'ORDER BY'
    RESULTCOUNTER   INTEGER,            
                                        -- number of maximum results
    ENABLEDEBUG     INTEGER NOT NULL,   
                                        -- show query when executing
    OID             CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                        -- oid of object in ibs_object
    SELECTSTRING    CLOB(2047M) NOT NULL,
                                        -- select string of sql-query without
                                        -- String 'SELECT'
    FROMSTRING      CLOB(2047M) NOT NULL,
                                        -- from string of sql-query without
                                        -- String 'FROM'
    WHERESTRING     CLOB(2047M) NOT NULL,
                                        -- where string of sql-query without
                                        -- String 'WHERE'
    COLUMNHEADERS   CLOB(2047M) NOT NULL,
                                        -- headers to be displayed in result
                                        -- - seperated by carrige return, 
                                        -- line feed
    QUERYATTRFORHEADERS CLOB(2047M) NOT NULL,
                                        -- mapping between headers and
                                        -- attributes - seperated by carrige
                                        -- return, line feed
    QUERYATTRTYPESFORHEADERS CLOB(2047M) NOT NULL,
                                        -- SQL-Datatypes of queryAttributes
                                        -- which contents are displayed in
                                        -- resultlist.
    SEARCHFIELDTOKENS CLOB(2047M) NOT NULL,
                                        -- tokens for searchfields (this tokens
                                        -- are displayed in searchform)
    QUERYATTRFORFIELDS CLOB(2047M) NOT NULL,
                                        -- mapping between searchfieldtokens and
                                        -- queryAttributes
    QUERYATTRTYPESFORFIELDS CLOB(2047M) NOT NULL
                                        -- SQL-Datatypes of search
                                        -- - queryAttributes
);

-- Primary key:
ALTER TABLE IBSDEV1.IBS_QUERYCREATOR_01 ADD PRIMARY KEY (OID);
