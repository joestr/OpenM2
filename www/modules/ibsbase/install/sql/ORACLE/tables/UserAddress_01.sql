/******************************************************************************
 * The M2_TERMIN_01 table incl. indexes. <BR>
 * 
 *
 * @version     2.10.0000, 30.01.2001
 *
 * @author      Ferdinand Koban (FF)  010130
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990804    Code cleaning.
 * <DD>HB 990810    EXIT inserted.
 ******************************************************************************
 */

CREATE TABLE /*USER*/IBS_USERADDRESS_01
(
    -- the Object Identifier of the UserAddress Tab
    OID         RAW (8)         NOT NULL,
    -- the Email Address of the User
    EMAIL       VARCHAR2 (127)   NULL,
    -- the SmSEmail Address of the User
    SMSEMAIL    VARCHAR2 (127)   NULL 
);
-- modify the oid-field with an Hex- default
ALTER TABLE /*USER*/ibs_UserAddress_01 modify ( oid default hextoraw('0000000000000000'));
-- Add a Primary key to the Oid-field
ALTER TABLE IBS_USERADDRESS_01 ADD ( CONSTRAINT PK__IBS_USERADDRESS__OID PRIMARY KEY ( oid ) );

EXIT;
