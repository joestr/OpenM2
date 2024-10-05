/******************************************************************************
 * The ibs_Checkout_01 table incl. indexes. <BR>
 * The Checkout table contains the information which objects are checked out 
 * by which user.
 *
 * @version     1.10.0001, 20.02.2000
 *
 * @author      Keim Christine (CK)  000220
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_Checkout_01
(
    oid         OBJECTID        NOT NULL,         --  oid of the object that is checked out
    userId      USERID          NOT NULL,         --  id of the user that checked out the object
    checkout    DATETIME        NOT NULL          --  the date on which the object was checked out by the user
)
GO
-- ibs_Checkout_01
