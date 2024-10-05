/******************************************************************************
 * This table contains information about a product group
 * in a catalog. <BR>
 *
 * @version     $Id: ProductGroup_01.sql,v 1.3 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Bernhard Walter (BW) 981221
 ******************************************************************************
 */

CREATE TABLE m2_ProductGroup_01
(
    oid							OBJECTID PRIMARY KEY
    ,productGroupProfileOid		OBJECTID NULL
)
GO
