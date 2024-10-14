-------------------------------------------------------------------------------
-- Currently this table doesn't hold any special values. For future
-- extension it was established tough (prices for collections etc.). <BR>
--
-- @version     $Id: ProductCollection_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PRODUCTCOLLECTION_01
(
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
    COST            INTEGER,
    COSTCURRENCY    VARCHAR (5),
    TOTALQUANTITY   INTEGER,
    VALIDFROM       TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    NRCODES         INTEGER,
    CATEGORYOIDX    CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
    CATEGORYOIDY    CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_PRODUCTCOLLECTION_01 ADD PRIMARY KEY (OID);
