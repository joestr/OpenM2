/******************************************************************************
 * All views regarding a cleancontainer. <BR>
 *
 * @version     1.10.0001, 28.03.2000
 *
 * @author     Christine Keim (CK)  000328
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * Gets the data of the objects to clean (not valid anymore). <BR>
 */
CREATE OR REPLACE VIEW v_CleanContainer_01$content
AS 
    SELECT  * 
    FROM    v_Container$content 
    WHERE   ((SYSDATE - validUntil) > 0)
;
-- v_CleanContainer_01$content

EXIT;
