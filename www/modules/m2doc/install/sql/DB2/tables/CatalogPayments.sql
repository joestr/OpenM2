-------------------------------------------------------------------------------
-- This table contains information about a payment type in a catalog.<BR>
--
-- @version     $Id: CatalogPayments.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

CREATE TABLE IBSDEV1.M2_CATALOGPAYMENTS
(
    catalogOid          CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
    paymentOid          CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
);
-- m2_CatalogPayments
