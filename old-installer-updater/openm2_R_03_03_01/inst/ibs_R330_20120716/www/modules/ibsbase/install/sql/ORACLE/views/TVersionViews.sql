/******************************************************************************
 * All views regarding the TVersion table. <BR>
 *
 * @version     2.21.0002, 25.06.2002 KR
 *
 * @author      Rahul Soni (RS)  000517
 ******************************************************************************
 */

/******************************************************************************
 * Get the class names for all versions of all types. <BR>
 */
-- create the new view:
CREATE OR REPLACE VIEW v_TVersion$content
AS
    SELECT  t.id AS typeId, t.name AS typeName, t.code AS typeCode,
            tv.id AS id, tv.className, tv.superTVersionId AS superTVersionId,
            tv.posNoPath AS posNoPath, dt.oid AS oid
    FROM    ibs_Type t, ibs_TVersion tv, ibs_DocumentTemplate_01 dt
    WHERE   tv.typeId = t.id
        AND tv.id = dt.tVersionId(+)
;
-- v_TVersion$content
show errors;

EXIT;
