/******************************************************************************
 * The ibs user table incl. indexes. <BR>
 * The user table contains all currently existing users.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Klaus Reimüller (KR)  980528
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_User
(   -- <domainId>100{21*0|1}
    id          USERID          NOT NULL PRIMARY KEY, 
    -- object id of user
    oid         OBJECTID        NOT NULL, 
    -- state of object
    state       STATE           NOT NULL, 
    -- domain where the user resides
    domainId    DOMAINID        NOT NULL, 
    -- user name
    name        NAME            NOT NULL, 
    password    NAME            NULL,
    -- full name of user
    fullname    NAME            NULL,     
    admin       BOOL            NOT NULL    DEFAULT (0),
	-- if password has to be changed
	changePwd	BOOL			NOT NULL	DEFAULT (0)
)
GO
-- ibs_User
