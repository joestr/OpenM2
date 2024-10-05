/******************************************************************************
 * The m2_ProfileCategory_01 table incl. indexes and triggers. <BR>
 * The m2_ProfileCategory_01 is the connection between the productprofile
 * and the code categories.
 *
 * @version     $Id: ProfileCategory_01.sql,v 1.2 2003/10/31 00:13:06 klaus Exp $
 *
 * @author      Bernhard Walter  981215
 ******************************************************************************
 */
CREATE TABLE m2_ProfileCategory_01
(
    productProfileOid   OBJECTID     NOT NULL,       -- oid of object in ibs_object
    categoryOid         OBJECTID     NOT NULL		 -- the categories of codes 
)
GO
-- m2_ProfileCategory_01

