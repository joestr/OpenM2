/******************************************************************************
 * All views regarding a cleancontainer. <BR>
 *
 * @version     1.10.0001, 28.03.2000
 *
 * @author      Keim Christine (CK)  000328
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects which can be cleaned. <BR>
 */
-- delete existing view:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.v_CleanContainer_01$content') 
                AND sysstat & 0xf = 2)
	DROP VIEW #CONFVAR.ibsbase.dbOwner#.v_CleanContainer_01$content
GO

-- create the new view:
CREATE VIEW v_CleanContainer_01$content
AS
    SELECT  * 
    FROM    v_Container$content
    WHERE (DATEDIFF (day, getDate (), validUntil) < 0)
GO
-- v_CleanContainer_01$content
