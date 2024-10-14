/******************************************************************************
 * The IBS_OBJECT table incl. indexes. <BR>
 * 
 * @version     $Id: ProductCollectionValue_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTCOLLECTIONVALUE_01
(
    ID NUMBER (10,0) NOT NULL,
    CATEGORYOID RAW (8),
    VALUE VARCHAR2 (255)
) /*TABLESPACE*/;

ALTER TABLE /*USER*/M2_PRODUCTCOLLECTIONVALUE_01  MODIFY (ID DEFAULT  0);

EXIT;
