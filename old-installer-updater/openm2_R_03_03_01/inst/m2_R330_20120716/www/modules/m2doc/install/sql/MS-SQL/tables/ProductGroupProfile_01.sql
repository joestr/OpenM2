/******************************************************************************
 * This table holds the profile of a product group. <BR>
 *
 * @version     $Id: ProductGroupProfile_01.sql,v 1.3 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Bernhard Walter (BW) 981221
 ******************************************************************************
 */

CREATE TABLE m2_ProductGroupProfile_01
(
    oid             OBJECTID    NOT NULL PRIMARY KEY
    ,thumbAsImage   BOOL        NOT NULL
    ,code           NAME        NULL
    ,season         NAME        NULL
    ,image          NAME        NULL
    ,thumbnail      NAME        NULL
)
GO
