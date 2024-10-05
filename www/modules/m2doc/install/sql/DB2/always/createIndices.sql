--------------------------------------------------------------------------------
-- create all indices of the whole base system. <BR>
--
-- @version     $Id: createIndices.sql,v 1.7 2003/10/31 16:29:01 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
--------------------------------------------------------------------------------
-- get and drop all indixes where dropping is possible
-- the following indixes can not be dropped:
-- * index created by 'primary key' column constraint
-- * unique index created by 'unique' column constraint
-- * index on text or image structures
-- * indexes on a non-clustered table itself
--
-- only m2 indexes will be selected and dropped
-- prefixes: M2_ MAD_ SP_
--
CALL IBSDEV1.p_dropProc ('pim2_createIndices');

CREATE PROCEDURE IBSDEV1.pim2_createIndices ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE indexName VARCHAR (64);
    DECLARE tableName VARCHAR (64);
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE l_sqlstatus INT;
    DECLARE l_SQL VARCHAR (255);

  DECLARE cursorAllIndexes CURSOR WITH HOLD FOR 

        SELECT i.name, o.name 
        FROM QSYS2.SYSINDEXES i, QSYS2.SYSTABLES o
        WHERE o.type = 'T' 
          AND o.NAME = i.TBNAME             -- get tables for indixes
          AND (
                (o.name LIKE 'M2_%')        -- get only indixes on m2 tables
             OR (o.name LIKE 'MAD_%')
             OR (o.name LIKE 'SP_%')
              ) 
          AND (i.name NOT LIKE 'PK__%')     -- primary key constraint
          AND (i.name NOT LIKE 'UQ__%')     -- unique constraint
          AND (i.name NOT LIKE '_WA_%');    -- unknown???
--          AND  i.indid <> 255             -- without indexes for text or image
                                            -- data
--          AND i.indid <> 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

        OPEN cursorAllIndexes;
    
    -- get the first index:
  SET l_sqlcode = 0;
  FETCH FROM cursorAllIndexes INTO indexName, tableName;
  SET l_sqlstatus = l_sqlcode;
    
    WHILE l_sqlstatus <> 100 DO
    
    -- another index found?
    -- Because @@FETCH_STATUS may have one of the three values
    -- -2, -1, or 0 all of these cases must be checked.
    -- In this case the tuple is skipped if it was deleted
    -- during the execution of this procedure.
     IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 

          -- drop index:
          SET l_SQL = 'DROP INDEX ' || tableName || '.' || indexName;
          EXECUTE IMMEDIATE l_SQL;
      END IF;

      SET l_sqlcode = 0;
      FETCH FROM cursorAllIndexes INTO indexName, tableName;
      SET l_sqlstatus = l_sqlcode;
  END WHILE;

  -- loop through all found indexes:
    -- if
    
    -- get the next index:
  
    -- while another index found
    
    -- close cursor:
    
    -- remove cursor from system:
    RETURN 0;
END;




--
-- CREATE ALL INDEXES
--
CALL IBSDEV1.p_dropProc ('pim2_createIndices2');

CREATE PROCEDURE IBSDEV1.pim2_createIndices2 ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- 
-- MODULE: M2
--

    -- M2_Article_01
    CREATE INDEX IBSDEV1.I_ArticleDiscussionId ON IBSDEV1.m2_Article_01 (discussionId);

    -- M2_PRODUCT_01
    CREATE INDEX IBSDEV1.I_ProductProductNo ON IBSDEV1.m2_Product_01 (productNo);

    -- M2_PRODUCTCODEVALUES_01
    CREATE INDEX IBSDEV1.I_ProductCValues_01Oid ON IBSDEV1.m2_ProductCodeValues_01 (productOid, categoryOid);

    -- M2_PARTICIPANT_01
    CREATE UNIQUE INDEX IBSDEV1.I_ParticipantOid ON IBSDEV1.m2_Participant_01 (oid);

    -- M2_SHOPPINGCARTENTRY_01
    CREATE UNIQUE INDEX IBSDEV1.I_CEntry_01Oid ON IBSDEV1.m2_ShoppingCartEntry_01 (oid);

    -- M2_TERMIN_01
    CREATE INDEX IBSDEV1.I_TerminStartDate ON IBSDEV1.m2_Termin_01 (startDate);
    CREATE INDEX IBSDEV1.I_TerminEndDate ON IBSDEV1.m2_Termin_01 (endDate);
END;


-- execute procedures:
CALL IBSDEV1.pim2_createIndices;
CALL IBSDEV1.pim2_createIndices2;

-- delete procedures:
CALL IBSDEV1.p_dropProc ('pim2_createIndices');
CALL IBSDEV1.p_dropProc ('pim2_createIndices2');
