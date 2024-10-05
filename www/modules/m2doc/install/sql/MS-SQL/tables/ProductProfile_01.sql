/******************************************************************************
 * . <BR>
 *
 * @version     $Id: ProductProfile_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 981222
 ******************************************************************************
 */

CREATE TABLE m2_ProductProfile_01
(
	oid                 OBJECTID		NOT NULL PRIMARY KEY -- oid of object in ibs_object
    ,categories         NVARCHAR(255)   NULL                 -- category of the properties
)
GO
