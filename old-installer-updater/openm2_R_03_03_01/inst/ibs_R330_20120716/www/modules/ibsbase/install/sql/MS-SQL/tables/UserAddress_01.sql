/******************************************************************************
 * The ibs UserProfile table incl. indexes. <BR>
 * The UserProfile table contains all currently existing user UserProfiles.
 *
 * @version     1.10.0001, 16.01.2001
 *
 * @author      Ferdinand Koban (FF)  010116
 *
 * <DT><B>Updates:</B>
 * <DD>MS 010116    Code cleaning.
 ******************************************************************************
 */
SET NOCOUNT ON
GO

CREATE TABLE ibs_UserAddress_01
(
    -- Object Identifier of Tab UserAddress
    oid         OBJECTID    NOT NULL UNIQUE,
    -- Field Email contens the email address of a user
    email       EMAIL       NULL,
    -- Field smsemail contens the sms-gateway emailaddress of a user           
    smsemail    EMAIL       NULL
)
GO
-- turns the off the count of the rows
SET NOCOUNT OFF
GO
