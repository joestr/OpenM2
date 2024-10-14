/******************************************************************************
 *  Create table M2_PRICECODEVALUES_01 and indices . <BR>
 * 
 * @version     $Id: PriceCodeValues_01.sql,v 1.3 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990826
 ******************************************************************************
 */

CREATE TABLE /*USER*/M2_PRICECODEVALUES_01
(
    PRICEOID RAW (8) NOT NULL,
    CATEGORYOID RAW (8) NOT NULL,
    VALIDFORALLVALUES NUMBER (1,0) NOT NULL,
    CODEVALUES VARCHAR2 (255)  
) /*TABLESPACE*/;

exit;
