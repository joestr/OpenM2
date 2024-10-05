-------------------------------------------------------------------------------
-- The ibs_Checkout_01 table incl. indexes. <BR>
-- The Checkout table contains the information which objects are checked out 
-- by which user.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_CHECKOUT_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            --  oid of the object that is
                                            --  checked out
    USERID              INTEGER NOT NULL,   --  id of the user that checked out
                                            --  the object
    CHECKOUT            TIMESTAMP           --  the date on which the object was
                                            --  checked out by the user
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_CHECKOUTOID ON IBSDEV1.IBS_CHECKOUT_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_CHECKOUTUSERID ON IBSDEV1.IBS_CHECKOUT_01
    (USERID ASC);
