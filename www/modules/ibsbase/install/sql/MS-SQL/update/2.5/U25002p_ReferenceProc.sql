/******************************************************************************
 * All stored procedures regarding the Reference table. <BR>
 *
 * @version     2.21.0001, 17.12.2001
 *
 * @author      Klaus Reimüller (KR)  011217
 ******************************************************************************
 */


/******************************************************************************
 * Create a new reference. <BR>
 * This procedure stores the reference within the references table.
 * If there exists already a corresponding reference (same oid and fieldName)
 * that reference is updated to the new referenced oid. Otherwise a new
 * reference tuple is created.
 *
 * @input parameters:
 * @param   ai_referencingOid   The oid of the referencing object.
 * @param   ai_fieldName        Name of the field which contains the reference.
 * @param   ai_referencedOid    oid of the referenced object.
 * @param   ai_kind             Kind of reference
 *                              1 ... link (reference object)
 *                              2 ... OBJECTREF
 *                              3 ... FIELDREF
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Reference$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Reference$create
(
    -- input parameters:
    @ai_referencingOid      OBJECTID,
    @ai_fieldName           NAME,
    @ai_referencedOid       OBJECTID,
    @ai_kind                INT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_REF_LINK             INT,            -- reference kind: link
    @c_MULTIPLE_LINK		INT,			-- reference kind: multiple

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_referencingOid       OBJECTID,       -- the local referencing oid
    @l_fieldName            NAME            -- the local field name

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000,
    @c_REF_LINK             = 1,
    @c_MULTIPLE_LINK        = 5

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_referencingOid = @ai_referencingOid,
    @l_fieldName = @ai_fieldName

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Reference$create

    -- set the correct referencing oid:
    IF (@l_referencingOid = NULL)       -- not allowed value?
    BEGIN
        -- set default value:
        SELECT  @l_referencingOid = @c_NOOID
    END -- if not allowed value

    -- set the correct fieldName value:
    IF (@ai_kind = @c_REF_LINK)         -- link object?
    BEGIN
        -- ensure that the field name has a correct value:
        SELECT  @l_fieldName = null
    END -- if link object

    -- update the existing reference:
	UPDATE  ibs_Reference
	SET     referencedOid = @ai_referencedOid
	WHERE   referencingOid = @ai_referencingOid
	AND (   kind = @c_REF_LINK
	OR  (   fieldName = @l_fieldName
		AND (   (   kind in (@c_MULTIPLE_LINK)
	            AND referencedOid = @ai_referencedOid
	            )
	        OR  (   kind <> @c_MULTIPLE_LINK)
	        )
	    )
	)

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'update existing reference', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- check if the reference was set:
    IF (@l_rowCount <= 0)               -- reference not existing?
    BEGIN
        -- create a new reference:
        INSERT  INTO ibs_Reference
                (referencingOid, fieldName, referencedOid, kind)
        VALUES  (@ai_referencingOid, @l_fieldName, @ai_referencedOid, @ai_kind)

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            'update existing reference', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler
    END -- if reference not existing

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Reference$create
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Reference$create', @l_error, @l_ePos,
            'ai_kind', @ai_kind,
            'ai_fieldName', @ai_fieldName
GO
-- p_Reference$create


/******************************************************************************
 * Delete an existing reference. <BR>
 * If there exists a tuple within the reference table which corresponds to this
 * oid/fieldName combination it is deleted. If not nothing happens.
 *
 * @input parameters:
 * @param   ai_referencingOid   The oid of the referencing object.
 * @param   ai_fieldName        Name of the field which contains the reference.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Reference$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Reference$delete
(
    -- input parameters:
    @ai_referencingOid      OBJECTID,
    @ai_fieldName           NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_REF_LINK             INT,            -- reference kind: link

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_REF_LINK             = 1

    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Reference$delete

    -- delete the existing reference:
    -- don't consider the fieldName for LINKs.
    DELETE  ibs_Reference
    WHERE   referencingOid = @ai_referencingOid
        AND (   kind = @c_REF_LINK
            OR  fieldName = @ai_fieldName
            )

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        'delete existing reference', @l_ePos OUTPUT, @l_rowCount OUTPUT
    IF (@l_error <> 0 OR @l_rowCount <= 0) -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Reference$delete
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Reference$delete', @l_error, @l_ePos,
            '', '',
            'ai_fieldName', @ai_fieldName
GO
-- p_Reference$delete


/******************************************************************************
 * Delete all references from a specific object. <BR>
 * This procedure deletes all references from an object. This is necessary if
 * the object is deleted.
 *
 * @input parameters:
 * @param   ai_referencingOid   The oid of the referencing object.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Reference$deleteRefcingOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Reference$deleteRefcingOid
(
    -- input parameters:
    @ai_referencingOid      OBJECTID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Reference$deleteRefcingOid

    -- delete the existing references:
    DELETE  ibs_Reference
    WHERE   referencingOid = @ai_referencingOid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'delete all references from object', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Reference$deleteRefcingOid
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Reference$deleteRefcingOid', @l_error, @l_ePos
GO
-- p_Reference$deleteRefcingOid


/******************************************************************************
 * Delete all references to a specific object. <BR>
 * This procedure deletes all references to an object. This is necessary if
 * the object is deleted.
 *
 * @input parameters:
 * @param   ai_referencedOid    The oid of the referenced object.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Reference$deleteRefcedOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_Reference$deleteRefcedOid
(
    -- input parameters:
    @ai_referencedOid       OBJECTID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Reference$deleteRefcedOid

    -- delete the existing references:
    DELETE  ibs_Reference
    WHERE   referencedOid = @ai_referencedOid

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'delete all references to object', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Reference$deleteRefcedOid
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Reference$deleteRefcedOid', @l_error, @l_ePos
GO
-- p_Reference$deleteRefcedOid


/******************************************************************************
 * Delete all references to and from objects of a specific type. <BR>
 * This procedure deletes all references from and to objects which can be
 * identified as being derived from a specific type. This means that both
 * referencingOid and referencedOid are checked. The check is done through
 * getting the tVersionId for each business object out of the ibs_Object table.
 * If a business object is not found its data stay unchanged.
 *
 * @input parameters:
 * @param   ai_tVersionId       The id of the type version whose objects shall
 *                              be deleted.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Reference$deleteTVersion'
GO

-- create the new procedure:
CREATE PROCEDURE p_Reference$deleteTVersion
(
    -- input parameters:
    @ai_tVersionId          TVERSIONID
    -- output parameters:
)
AS
DECLARE
    -- constants:
    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- set a save point for the current transaction:
    SAVE TRANSACTION s_Reference$deleteTVersion

    -- delete references from objects of the regarding type:
    DELETE  ibs_Reference
    WHERE   referencingOid IN
            (SELECT oid
            FROM    ibs_Object
            WHERE   tVersionId = @ai_tVersionId)

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'delete all references from this type''s objects', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- delete references to objects of the regarding type:
    DELETE  ibs_Reference
    WHERE   referencedOid IN
            (SELECT oid
            FROM    ibs_Object
            WHERE   tVersionId = @ai_tVersionId)

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'delete all references to this type''s objects', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    -- terminate the procedure:
    RETURN

exception:                              -- an error occurred
    -- roll back to the save point:
    ROLLBACK TRANSACTION s_Reference$deleteTVersion
    -- log the error:
    EXEC ibs_error.logError 500, 'p_Reference$deleteTVersion', @l_error, @l_ePos,
            'ai_tVersionId', @ai_tVersionId
GO
-- p_Reference$deleteTVersion
