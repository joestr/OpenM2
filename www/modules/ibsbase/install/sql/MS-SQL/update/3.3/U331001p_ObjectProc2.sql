/******************************************************************************
 * All stored procedures regarding the ibs_Object table which have to
 * be inserted to the DB after all the others. <BR>
 *
 * @version     $Id: U331001p_ObjectProc2.sql,v 1.1 2013/01/21 08:15:15 rburgermann Exp $
 *
 * @author      Harald Buzzi (HB)  990723
 ******************************************************************************
 */

 PRINT 'Starting U331001p_ObjectProc2.sql'
 GO

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
    PRINT 'UPDATE: ibs_object / FIELD: containerId ...'
    UPDATE ibs_object
    SET containerId = @ai_newOid
    WHERE containerId = @ai_oldOid

    -- the containerOid2 IS NOT UPDATED through the existing trigger
    -- thus we update all references of containerOid2 here manually
    PRINT 'UPDATE: ibs_object / FIELD: containerOid2 ...'
    UPDATE ibs_object
    SET containerOid2 = @ai_newOid
    WHERE containerOid2 = @ai_oldOid
    
    -- update OIDs of object in the respective tables
    EXEC p_changeObjectReferences @ai_oldOid, @ai_newOid
    
END
GO
-- p_changeOid

PRINT 'U331001p_ObjectProc2.sql finished.'
GO