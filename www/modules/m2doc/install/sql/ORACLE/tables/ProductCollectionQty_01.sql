/******************************************************************************
 * The M2_PRODUCTCOLLECTIONQTY_01 table incl. indexes. <BR>
 * 
 * @version     $Id: ProductCollectionQty_01.sql,v 1.4 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTCOLLECTIONQTY_01
(
    ID NUMBER (10,0) NOT NULL,
    COLLECTIONOID RAW (8),
    QUANTITY NUMBER (10,0)  
) /*TABLESPACE*/;

ALTER TABLE /*USER*/M2_PRODUCTCOLLECTIONQTY_01  MODIFY (ID DEFAULT  0);

EXIT;
