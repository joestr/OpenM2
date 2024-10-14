/******************************************************************************
 * Jointable between m2_ProductCollection_01 and ProductCollectionValue_01
 *
 * @version     $Id: ProductCollectionQty_01.sql,v 1.3 2003/10/05 02:00:35 klaus Exp $
 *
 * @author      Andreas Jansa (AJ) 990424
 ******************************************************************************
 */
 
CREATE TABLE m2_ProductCollectionQty_01
(
    id              ID          PRIMARY KEY NOT NULL,
    collectionOid   OBJECTID    NULL,
    quantity        INT         NULL
)
GO
