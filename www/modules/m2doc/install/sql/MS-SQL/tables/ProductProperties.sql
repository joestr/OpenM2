/******************************************************************************
 * The m2_ProductProperties_01 table incl. indexes and triggers. <BR>
 * The m2_ProductProperties_01 table contains properties.
 * Products in a catalog may refer to this properties. Usually this table
 * contains information about colors and sizes.
 *
 * @version     $Id: ProductProperties.sql,v 1.3 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter  980908
 ******************************************************************************
 */
CREATE TABLE m2_ProductProperties_01
(
    oid                 OBJECTID      NOT NULL PRIMARY KEY -- oid of object in ibs_object
    ,categoryOid        OBJECTID      NULL                 -- category of the properties
    ,delimiter          NVARCHAR(1)   NULL                 -- delimiter used for properties
    ,values1            NVARCHAR(255) NULL                 -- properties separated by delimiter
    ,values2            NVARCHAR(255) NULL                 -- properties separated by delimiter
    ,values3            NVARCHAR(255) NULL                 -- properties separated by delimiter
    ,values4            NVARCHAR(255) NULL                 -- properties separated by delimiter
)
GO
-- m2_ProductProperties_01
