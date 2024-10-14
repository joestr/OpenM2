/******************************************************************************
 * All views regarding the TVersion table. <BR>
 *
 * @version     2.21.0003, 25.02.2002
 *
 * @author      Rahul Soni (RS)  000517
 ******************************************************************************
 */

/******************************************************************************
 * Get the version information of all types. <BR>
 */
-- delete existing view:
EXEC p_dropView 'v_TVersion$content'
GO

-- create the new view:
CREATE VIEW v_TVersion$content
AS
    SELECT  t.id AS typeId, t.name AS typeName, t.code AS typeCode,
            t.icon AS icon,
            tv.id AS id, tv.className, tv.superTVersionId AS superTVersionId,
            tv.posNoPath AS posNoPath, dt.oid AS oid
    FROM    ibs_Type t
            INNER JOIN ibs_tVersion tv ON tv.typeId = t.id
            LEFT OUTER JOIN 
            (
                SELECT  o.oid, dt.tVersionId
                FROM    ibs_Object o, ibs_DocumentTemplate_01 dt
                WHERE   o.oid = dt.oid
                    AND o.state = 2
            ) dt
            ON tv.id = dt.tVersionId
GO
-- v_TVersion$content
