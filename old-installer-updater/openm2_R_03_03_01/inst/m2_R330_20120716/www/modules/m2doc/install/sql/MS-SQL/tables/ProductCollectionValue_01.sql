/******************************************************************************
 * Contents the different values within one collection
 *
 * @version     $Id: ProductCollectionValue_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ) 990424
 ******************************************************************************
 */
 
CREATE TABLE m2_ProductCollectionValue_01
(
    id              ID              NOT NULL,
    categoryOid     OBJECTID        NULL,
    value           NVARCHAR(255)   NULL
)
GO
