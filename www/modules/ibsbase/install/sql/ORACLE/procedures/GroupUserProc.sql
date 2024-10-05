/******************************************************************************
 * All stored procedures regarding the GroupUser table. <BR>
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Add a new user to a group. <BR>
 *
 * @input parameters:
 * @param   @groupId            Id of the group where the user shall be added.
 * @param   @userId             Id of the user to be added.
 * @param   @group              Id of group, which has to have some rights on 
 *                              the GroupUser object.
 * @param   @rights             Rights of the group.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE PROCEDURE p_GroupUser_01$addUser
(
    -- input parameters:
    groupId        INTEGER,
    userId         INTEGER,
    group_         INTEGER := null,
    rights         INTEGER := null
)
AS
    -- exception values:
    StoO_error     INTEGER;
    StoO_errmsg    VARCHAR2(255);
    -- define local variables:
    id              INTEGER;
    oid             RAW (8);
    tVersionId      INTEGER;
    admin           INTEGER;
    rightsAdmin     INTEGER;

BEGIN
    -- set tVersionId:
    p_GroupUser_01$addUser.tVersionId :=  16842929;    -- tVersionId of type Group_01

    -- set admin user:
    SELECT  id
    INTO    p_GroupUser_01$addUser.admin
    FROM    ibs_User
    WHERE   name = 'Admin';

    -- get rights for admin user:
    SELECT  SUM (id)
    INTO    p_GroupUser_01$addUser.rightsAdmin
    FROM    ibs_Operation;

    -- insert user into group:
    INSERT INTO ibs_GroupUser (groupId, userId)
    VALUES (p_GroupUser_01$addUser.groupId, p_GroupUser_01$addUser.userId);

    -- get id of newly created tuple:
    SELECT  MAX (Id)
    INTO    p_GroupUser_01$addUser.id
    FROM    ibs_GroupUser
    WHERE   groupId = p_GroupUser_01$addUser.groupId
        AND userId = p_GroupUser_01$addUser.userId;

    -- create oid for saving rights:
    p_GroupUser_01$addUser.oid := createOid (p_GroupUser_01$addUser.tVersionId, p_GroupUser_01$addUser.id);

    -- set rights for group:
    IF (p_GroupUser_01$addUser.group_ <> null AND p_GroupUser_01$addUser.rights <> null) -- rights set?
    THEN
        p_Rights$setRights (p_GroupUser_01$addUser.oid, p_GroupUser_01$addUser.group_, 
                    p_GroupUser_01$addUser.rights, 0);
    END IF; -- rights set

    -- set rights for admin:
    p_Rights$setRights (p_GroupUser_01$addUser.oid, p_GroupUser_01$addUser.admin, p_GroupUser_01$addUser.rightsAdmin, 0);

COMMIT WORK;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;       
    ibs_error.log_error ( ibs_error.error, 'p_GroupUser_01$addUser',               
    ', groupId = ' || groupId ||    
    ', userId = ' || userId ||
    ', group_ = ' || group_ ||    
    ', rights = ' || rights ||        
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);      
END p_GroupUser_01$addUser;
/

show errors;

exit;