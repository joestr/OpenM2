/******************************************************************************
 * The ibs address table incl. indexes. <BR>
 * The address table contains all currently existing addresses.
 * 
 * @version     $Id: Address_01.sql,v 1.5 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Christine Keim (CK)  9800603
 ******************************************************************************
 */
CREATE TABLE m2_Address_01 
(
    oid         OBJECTID         NOT NULL UNIQUE,
    street      NVARCHAR(63)     NOT NULL,
    zip         NVARCHAR(15)     NOT NULL,
    town        NVARCHAR(63)     NOT NULL,
    mailbox     NVARCHAR(15)     NOT NULL,
    country     NVARCHAR(31)     NOT NULL,
    tel         NVARCHAR(63)     NOT NULL,
    fax         NVARCHAR(63)     NOT NULL,
    email       NVARCHAR(127)    NOT NULL,
    homepage    NVARCHAR(255)    NOT NULL
)
GO
-- m2_Address_01
