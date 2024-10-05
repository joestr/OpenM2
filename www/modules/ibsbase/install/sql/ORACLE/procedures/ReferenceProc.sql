/******************************************************************************
 * All stored procedures regarding the Reference table. <BR>
 *
 * @version     2.21.0001, 04.06.2002 KR
 *
 * @author      Klaus Reimüller (KR)  020604
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
CREATE OR REPLACE PROCEDURE p_Reference$create
(
    -- input parameters:
    ai_referencingOid       ibs_Reference.referencingOid%TYPE,
    ai_fieldName            ibs_Reference.fieldName%TYPE,
    ai_referencedOid        ibs_Reference.referencedOid%TYPE,
    ai_kind                 ibs_Reference.kind%TYPE
    -- output parameters:
)
AS
    -- constants:
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_REF_LINK              ibs_Reference.kind%TYPE := 1;-- reference kind: link
    c_MULTIPLE_LINK         ibs_Reference.kind%TYPE := 5;-- reference kind: multiple

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_referencingOid        ibs_Reference.referencingOid%TYPE := ai_referencingOid;
                                            -- the local referencing oid
    l_fieldName             ibs_Reference.fieldName%TYPE := ai_fieldName;
                                            -- the local field name

-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Reference$create;

    -- set the correct referencing oid:
    IF (l_referencingOid = NULL)        -- not allowed value?
    THEN
        -- set default value:
        l_referencingOid := c_NOOID;
    END IF; -- if not allowed value

    -- set the correct fieldName value:
    IF (ai_kind = c_REF_LINK)           -- link object?
    THEN
        -- ensure that the field name has a correct value:
        l_fieldName := null;
    END IF; -- if link object

    BEGIN
        -- update the existing reference:
        UPDATE  ibs_Reference
        SET     referencedOid = ai_referencedOid
        WHERE   referencingOid = ai_referencingOid
		AND (   kind = c_REF_LINK
		OR  (   fieldName = l_fieldName
			AND (   (   kind in (c_MULTIPLE_LINK)
		            AND referencedOid = ai_referencedOid
		            )
		        OR  (   kind <> c_MULTIPLE_LINK)
		        )
		    )
		);

        l_rowCount := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'UPDATE existing reference';
            RAISE;                      -- call common exception handler
    END;

    -- check if the reference was set:
    IF (l_rowCount <= 0)                -- reference not existing?
    THEN
        BEGIN
            -- create a new reference:
            INSERT  INTO ibs_Reference
                    (referencingOid, fieldName, referencedOid, kind)
            VALUES  (ai_referencingOid, l_fieldName, ai_referencedOid, ai_kind);
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'INSERT reference';
                RAISE;                  -- call common exception handler
        END;
    END IF; -- if reference not existing

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Reference$create; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_referencingOid = ' || ai_referencingOid || ', ' ||
            ', ai_kind = ' || ai_kind || ', ' ||
            ', ai_referencedOid = ' || ai_referencedOid || ', ' ||
            ', ai_fieldName = ' || ai_fieldName || ', ' ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Reference$create', l_eText);
END p_Reference$create;
/

show errors;


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
-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Reference$delete
(
    -- input parameters:
    ai_referencingOid       ibs_Reference.referencingOid%TYPE,
    ai_fieldName            ibs_Reference.fieldName%TYPE
    -- output parameters:
)
AS
    -- constants:
    c_REF_LINK              ibs_Reference.kind%TYPE := 1;-- reference kind: link

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows

-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Reference$delete;

    BEGIN
        -- delete the existing reference:
        -- don't consider the fieldName for LINKs.
        DELETE  ibs_Reference
        WHERE   referencingOid = ai_referencingOid
            AND (   kind = c_REF_LINK
                OR  fieldName = ai_fieldName
                );
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'delete existing reference';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Reference$delete; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_referencingOid = ' || ai_referencingOid || ', ' ||
            ', ai_fieldName = ' || ai_fieldName || ', ' ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Reference$delete',l_eText);
END p_Reference$delete;
/

show errors;


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
CREATE OR REPLACE PROCEDURE p_Reference$deleteRefcingOid
(
    -- input parameters:
    ai_referencingOid       ibs_Reference.referencingOid%TYPE
    -- output parameters:
)
AS
    -- constants:

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text


-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Reference$deleteRefcingOid;

    BEGIN
        -- delete the existing references:
        DELETE  ibs_Reference
        WHERE   referencingOid = ai_referencingOid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'delete all references from object';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Reference$deleteRefcingOid; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_referencingOid = ' || ai_referencingOid || ', ' ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Reference$deleteRefcingOid',l_eText);
END p_Reference$deleteRefcingOid;
/

show errors;


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
CREATE OR REPLACE PROCEDURE p_Reference$deleteRefcedOid
(
    -- input parameters:
    ai_referencedOid        ibs_Reference.referencedOid%TYPE
    -- output parameters:
)
AS
    -- constants:

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text


-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Reference$deleteRefcedOid;

    BEGIN
        -- delete the existing references:
        DELETE  ibs_Reference
        WHERE   referencedOid = ai_referencedOid;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'delete all references to object';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Reference$deleteRefcedOid; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_referencedOid = ' || ai_referencedOid || ', ' ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Reference$deleteRefcedOid',l_eText);
END p_Reference$deleteRefcedOid;
/

show errors;


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
CREATE OR REPLACE PROCEDURE p_Reference$deleteTVersion
(
    -- input parameters:
    ai_tVersionId           ibs_Object.tVersionId%TYPE
    -- output parameters:
)
AS
    -- constants:

    -- local variables:
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text


-- body:
BEGIN
    -- set a save point for the current transaction:
    SAVEPOINT s_Reference$deleteTVersion;

    BEGIN
        -- delete references from objects of the regarding type:
        DELETE  ibs_Reference
        WHERE   referencingOid IN
                (   SELECT  oid
                    FROM    ibs_Object
                    WHERE   tVersionId = ai_tVersionId
                );
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'delete all references from this type''s objects';
            RAISE;                      -- call common exception handler
    END;

    BEGIN
        -- delete references to objects of the regarding type:
        DELETE  ibs_Reference
        WHERE   referencedOid IN
                (   SELECT  oid
                    FROM    ibs_Object
                    WHERE   tVersionId = ai_tVersionId
                );
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'delete all references to this type''s objects';
            RAISE;                      -- call common exception handler
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Reference$deleteTVersion; -- undo changes
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_tVersionId = ' || ai_tVersionId || ', ' ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Reference$deleteTVersion',l_eText);
END p_Reference$deleteTVersion;
/

show errors;

EXIT;
