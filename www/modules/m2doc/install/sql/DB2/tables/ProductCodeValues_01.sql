-------------------------------------------------------------------------------
-- This table holds the code values defined for a product. <BR>
--
-- @version     $Id: ProductCodeValues_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTCODEVALUES_01
(
    CODEVALUES          VARCHAR (255),
    PRODUCTOID          CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    CATEGORYOID         CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PREDEFINEDCODEOID   CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_PROD_VAL_01OID ON 
    IBSDEV1.M2_PRODUCTCODEVALUES_01 (PRODUCTOID ASC, CATEGORYOID ASC);
