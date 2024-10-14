/******************************************************************************
 * All views regarding a xml-container. <BR>
 *
 * @version     2.3.0001, 04.03.2002
 *
 * @author      Andreas Jansa (AJ)  020304
 *
 ******************************************************************************
 */

-- delete existing view:
EXEC p_dropView 'v_XMLContainer_01$content'
GO

-- create the new view:
CREATE VIEW v_XMLContainer_01$content
AS
    -- get all 'Thema'-objects
    SELECT  v.oid AS refOid, v.*
    FROM    v_Container$content v
    WHERE   v.isLink = 0
    UNION ALL
    SELECT  v.linkedObjectId AS refOid, v.*
    FROM    v_Container$content v
    WHERE   v.isLink = 1

-- v_XMLContainer_01$content