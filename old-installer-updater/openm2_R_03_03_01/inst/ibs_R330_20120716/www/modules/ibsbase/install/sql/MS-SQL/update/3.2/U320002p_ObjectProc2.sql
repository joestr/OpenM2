/******************************************************************************
 * All stored procedures regarding the ibs_Object table which have to
 * be inserted to the DB after all the others. <BR>
 *
 * @version     $Id: U320002p_ObjectProc2.sql,v 1.1 2012/04/24 16:08:34 rburgermann Exp $
 *
 * @author      Harald Buzzi (HB)  990723
 ******************************************************************************
 */

/******************************************************************************
 * Update all referenced OIDs within the table ibs_reference. <BR>
 * This method ONLY handles DocumentTemplates. <BR>
 * References without a document templates must be migrated manually. <BR>
 *  
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeIbsTableReferences'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeIbsTableReferences
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
-- local variables
DECLARE
    @l_tableName            sysname,
      @l_columnName           sysname,
    @sql                    nvarchar(4000),
    @ai_oldOid_s            OBJECTIDSTRING,
    @ai_newOid_s            OBJECTIDSTRING

BEGIN

    -- Update all referencedOid fields in all document template tables
    DECLARE selectCursor2 CURSOR FOR
    SELECT col.TABLE_NAME, 
           col.COLUMN_NAME
    FROM ibs_documenttemplate_01 doct,
         ibs_object o,
         ibs_reference ref,
         INFORMATION_SCHEMA.COLUMN_DOMAIN_USAGE col
    WHERE doct.tVersionID = o.tversionId
    AND   o.oid = ref.referencingOid
    AND   ref.referencedOid = @ai_oldOid
    AND   doct.tableName = col.TABLE_NAME
    AND   col.DOMAIN_NAME = 'OBJECTID'
    AND   col.COLUMN_NAME != 'oid'
    
    -- open the cursor:
    OPEN    selectCursor2

    -- get the first object:
    FETCH NEXT FROM selectCursor2 INTO @l_tableName, @l_columnName

    -- loop through all objects:
    WHILE (@@FETCH_STATUS <> -1)
                                       -- another object found?
    BEGIN
        
        PRINT 'UPDATE: referencedOid in ' + @l_tableName + ' / FIELD: ' + @l_columnName
    -- convert oids to strings:
    EXEC p_byteToString @ai_oldOid, @ai_oldOid_s OUTPUT
    EXEC p_byteToString @ai_newOid, @ai_newOid_s OUTPUT

    SET @sql = 'UPDATE ' + @l_tableName +
               ' SET ' + @l_columnName + ' = ' + @ai_newOid_s +
               ' WHERE ' + @l_columnName + ' = ' + @ai_oldOid_s
  
        EXEC sp_executesql @sql
    
        -- get next tuple:
        FETCH NEXT FROM selectCursor2 INTO @l_tableName, @l_columnName

    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE selectCursor2
    DEALLOCATE selectCursor2

    -- update referencedOid of object within ibs_reference 
    PRINT 'UPDATE: ibs_reference / FIELD: referencedOid ...'
    UPDATE ibs_reference
    SET referencedOid = @ai_newOid
    WHERE referencedOid = @ai_oldOid

    -- update referencingOid of object within ibs_reference 
    PRINT 'UPDATE: ibs_reference / FIELD: referencingOid ...'
    UPDATE ibs_reference
    SET referencingOid = @ai_newOid
    WHERE referencingOid = @ai_oldOid
    
END
GO
-- p_changeIbsTableReferences


/******************************************************************************
 * Update all references OIDs of an object with a new OID. <BR>
 *  
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeObjectReferences'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeObjectReferences
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
BEGIN
    -- update linkedObjectId of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: linkedObjectId ...'
    UPDATE ibs_object
    SET linkedObjectId = @ai_newOid
    WHERE linkedObjectId = @ai_oldOid

    -- update distributeId of object within ibs_sentObject_01 
    PRINT 'UPDATE: ibs_sentObject_01 / FIELD: distributeId ...'
    UPDATE ibs_sentObject_01
    SET distributeId = @ai_newOid
    WHERE distributeId = @ai_oldOid

    -- update distributedId of object within ibs_receivedObject_01 
    PRINT 'UPDATE: ibs_sentObject_01 / FIELD: distributedId ...'
    UPDATE ibs_receivedObject_01
    SET distributedId = @ai_newOid
    WHERE distributedId = @ai_oldOid

    -- update paramOid1 of object within obs_standard_reminder 
    PRINT 'UPDATE: obs_standard_reminder / FIELD: paramOid1 ...'
    UPDATE obs_standard_reminder
    SET paramOid1 = @ai_newOid
    WHERE paramOid1 = @ai_oldOid

    -- update paramOid2 of object within obs_standard_reminder 
    PRINT 'UPDATE: obs_standard_reminder / FIELD: paramOid2 ...'
    UPDATE obs_standard_reminder
    SET paramOid2 = @ai_newOid
    WHERE paramOid2 = @ai_oldOid

    -- update oid of object within ibs_protocol_01 
    PRINT 'UPDATE: ibs_protocol_01 / FIELD: oid ...'
    UPDATE ibs_protocol_01
    SET oid = @ai_newOid
    WHERE oid = @ai_oldOid

    -- update containerId of object within ibs_protocol_01 
    PRINT 'UPDATE: ibs_protocol_01 / FIELD: containerId ...'
    UPDATE ibs_protocol_01
    SET containerId = @ai_newOid
    WHERE containerId = @ai_oldOid

    -- SPECIAL HANDLING to INFORM user for MANUAL changes
    EXEC p_changeIbsTableReferences @ai_oldOid, @ai_newOid

END
GO
-- p_changeObjectReferences


/******************************************************************************
 * Update an object with a new OID. <BR>
 * This includes the update of all referenced objects of this object. <BR>
 * 
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeOid
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
BEGIN
    -- update oid of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: oid ...'
    UPDATE ibs_object
    SET oid = @ai_newOid
    WHERE oid = @ai_oldOid

    -- update containerid of object within ibs_object 
    -- the containerOid2 will be updated through the existing trigger
    PRINT 'UPDATE: ibs_object / FIELD: containerId ...'
    UPDATE ibs_object
    SET containerId = @ai_newOid
    WHERE containerId = @ai_oldOid

    -- update OIDs of object in the respective tables
    EXEC p_changeObjectReferences @ai_oldOid, @ai_newOid
    
END
GO
-- p_changeOid


/******************************************************************************
 * Update the type (tversion, typcode, typename) of an object. <BR>
 * This updates the necessary update of the OID of the object too. <BR>
 * 
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newTVersionId    TVersionId of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeType'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeType
(
    -- input parameters:
    @ai_oldOid               OBJECTID,
    @ai_tVersionId           TVERSIONID
)
AS
-- local variables
DECLARE
    @l_newOid               OBJECTID,           -- new generate OID
    @l_typeName             NAME,           -- the name of the type
    @l_typeCode             NAME,           -- the code of the type
    @l_id                   int             -- the code of the type
    
BEGIN
    -- get the typecode and typename from the ibs_type table
    SELECT @l_typecode = t.code,
           @l_typename = t.name
    FROM   ibs_tversion tv, ibs_type t
    WHERE  tv.typeId = t.id
    AND    tv.id = @ai_tVersionId

    -- get the id of the given object
    SELECT @l_id = o.id
    FROM   ibs_object o
    WHERE  o.oid = @ai_oldOid

    -- create new OID for the new object
    EXEC p_createOid @ai_tVersionId, @l_id, @l_newOid OUTPUT
    
    -- update OIDs of object in the respective tables
    EXEC p_changeOid @ai_oldOid, @l_newOid
    
    -- update tversionId of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: tversionId ...'
    UPDATE ibs_object
    SET tversionId = @ai_tVersionId
    WHERE oid = @l_newOid

    -- update typecode, typename of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: typeName, typeCode ...'
    UPDATE ibs_object
    SET typeName = @l_typeName, 
        typeCode = @l_typeCode
    WHERE oid = @l_newOid
END
GO
-- p_changeType