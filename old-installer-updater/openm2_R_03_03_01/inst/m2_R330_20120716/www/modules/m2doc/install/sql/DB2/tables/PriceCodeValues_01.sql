-------------------------------------------------------------------------------
-- This table contains tuples which define the relationship between
-- a price and the code values of a product (colors, sizes, etc.). <BR>
--
-- @version     $Id: PriceCodeValues_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRICECODEVALUES_01
(
    VALIDFORALLVALUES SMALLINT  NOT NULL,
    CODEVALUES      VARCHAR (255),
    PRICEOID        CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    CATEGORYOID     CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);
