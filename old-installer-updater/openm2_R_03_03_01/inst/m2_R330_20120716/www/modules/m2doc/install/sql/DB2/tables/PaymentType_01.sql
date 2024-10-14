-------------------------------------------------------------------------------
-- The M2_PAYMENTTYPE_01 table. <BR>
-- This table contains information about a payment type in a catalog.<BR>
--
-- @version     $Id: PaymentType_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_PAYMENTTYPE_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    PAYMENTTYPEID       INTEGER,
    NAME                VARCHAR (63)
);
