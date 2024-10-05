/******************************************************************************
 * All views regarding a xml container. <BR>
 *
 * @version     2.21.0001, 25.06.2002 KR
 *
 * @author      Andreas Jansa (AJ)  020304
 ******************************************************************************
 */

/******************************************************************************
 * Get the class names for all versions of all types. <BR>
 */
-- create the new view:
CREATE OR REPLACE VIEW v_XMLContainer_01$content
AS
    -- get all 'Thema'-objects
    SELECT  v.oid AS refOid, v.*
    FROM    v_Container$content v
    WHERE   v.isLink = 0
    UNION ALL
    SELECT  v.linkedObjectId AS refOid, v.*
    FROM    v_Container$content v
    WHERE   v.isLink = 1;
-- v_XMLContainer_01$content
show errors;

EXIT;
