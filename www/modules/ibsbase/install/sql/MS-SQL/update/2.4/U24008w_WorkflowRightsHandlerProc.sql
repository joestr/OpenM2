/******************************************************************************
 * All stored procedures regarding the workflow service. <BR>
 *
 * @version     $Id: U24008w_WorkflowRightsHandlerProc.sql,v 1.1 2005/03/04 22:05:50 klaus Exp $
 *
 * @author      Horst Pichler, 5.10.2000
 ******************************************************************************
 */

/******************************************************************************
 * Set the rights for a given user/group on given object handled by the 
 * workflow. <BR> This procedure is only a wrapper to call p_Rights$setRights 
 * from JAVA.
 * 
 * @input parameters:
 * @param   @ai_oid_s           ID of the object to be deleted.
 * @param   @ai_userId          ID of the user who is deleting the object.
 * @param   @ai_rights          New rights (if 0 rights entry will be deleted)
 * @param   @ai_rec             Set rights recursive?
 *                              (1 true, 0 false).
 *
 * @output parameters:          problem: called procedure provides no error-level
 *
 */
-- delete existing procedure: 
EXEC p_dropProc 'p_Workflow$setRights'
GO

CREATE PROCEDURE p_Workflow$setRights
(
    -- common input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_rPersonId       INT,
    @ai_rights          RIGHTS = 0,
    @ai_rec             BOOL = 0
)
AS
-- constants:
            
-- local variables:
DECLARE 
    @l_oid              OBJECTID       
            
-- initialize constants:

-- initialize local variables:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT
    
-- body:
    BEGIN TRANSACTION
        EXEC  p_Rights$setRights @l_oid, @ai_rPersonId, @ai_rights, @ai_rec
    COMMIT TRANSACTION

    -- return the state value:
    RETURN
GO
-- p_Workflow$setRights



/**************************************************************************
 * Set the rights-key of the object with the given targetOid (incl. sub-objects) 
 * to the rightskey of the object with the given sourceOid.<BR>
 * <BR>
 *
 * @input parameters:
 * @param   targetOid   oid of the object for which rights-key will be changed
 *                      (incl. sub-objects)
 * @param   sourceOid    oid of the object of which rights-key will be copied
 *
 * @returns true if ok; false if an error occured
 *
 */
-- delete existing procedure: 
EXEC p_dropProc 'p_Workflow$copyRightsRec'
GO

CREATE PROCEDURE p_Workflow$copyRightsRec
(
    -- common input parameters:
    @ai_targetOid_s         OBJECTIDSTRING,
    @ai_sourceOid_s         OBJECTIDSTRING
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,
    @c_EMPTYPOSNOPATH       VARCHAR(4),

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_targetOid            OBJECTID,
    @l_sourceOid            OBJECTID,
    @l_rKey                 INT,
    @l_posNoPath            POSNOPATH_VC

SELECT
    -- assign constants:
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_EMPTYPOSNOPATH       = '0000',

    -- initialize local variables:
    @l_retValue             = @c_ALL_RIGHT,
    @l_targetOid            = @c_NOOID,
    @l_sourceOid            = @c_NOOID,
    @l_rKey                 = 0,
    @l_posNoPath            = @c_EMPTYPOSNOPATH

-- body:
    -- convert oids
    EXEC p_stringToByte @ai_targetOid_s, @l_targetOid OUTPUT
    EXEC p_stringToByte @ai_sourceOid_s, @l_sourceOid OUTPUT

/* KR performance tuning: not longer necessary
    -- get the rkey of the 2nd object
    SELECT @l_rKey = rKey, @l_sourceOwner = owner
    FROM   ibs_Object
    WHERE  oid = @l_sourceOid

    -- existence check
    IF (@@ROWCOUNT = 0)
        RETURN @c_NOT_OK
    
    -- get posnopath of 1st object
    SELECT @l_posNoPath = posNoPath, @l_targetOwner = owner
    FROM   ibs_Object
    WHERE  oid = @l_targetOid
    
    -- existence check
    IF (@@ROWCOUNT = 0)
        RETURN @c_NOT_OK
*/

    --
    -- change rkeys
    --
    -- 1. set new rKey for first object + sub-objects
    BEGIN TRANSACTION
    EXEC @l_retValue = p_Rights$propagateRightsRec @l_sourceOid, @l_targetOid
    COMMIT TRANSACTION

    -- return the result:
    RETURN @l_retValue

/* KR performance tuning: This is not longer valid
 * because the owner is now regarded within the rKey
    UPDATE ibs_Object
    SET    rKey = @l_rKey
    WHERE  posNoPath LIKE @l_posNoPath + '%'
    -- check for errors
    IF (@@ERROR <> 0)               -- an error occurred?
        RETURN @c_NOT_OK
*/
        
/* KR performance tuning: Already done in Trigger
    --
    -- 2. set rkey for linked-objects (object + sub-objects)
    UPDATE ibs_Object
    SET    rKey = @l_rKey
    WHERE  linkedObjectId IN
         (SELECT oid
          FROM   ibs_Object
          WHERE  posNoPath LIKE @l_posNoPath + '%')
... KR performance tuning */

/* KR performance tuning: not longer necessary
    -- check for errors
    IF (@@ERROR <> 0)               -- an error occurred?
        RETURN @c_NOT_OK

    -- no error occurred; exit
    RETURN @c_ALL_RIGHT
*/
GO
-- p_Workflow$copyRightsRec
