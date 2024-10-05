/******************************************************************************
 * The M2_PRODUCTCODEVALUES_01 table incl. indexes. <BR>
 * 
 * @version     $Id: ProductCodeValues_01.sql,v 1.4 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PRODUCTCODEVALUES_01
(
    PRODUCTOID RAW (8) NOT NULL,
    CATEGORYOID RAW (8) NOT NULL,
    PREDEFINEDCODEOID RAW (8),
    CODEVALUES VARCHAR2 (255)
) /*TABLESPACE*/;


EXIT;
