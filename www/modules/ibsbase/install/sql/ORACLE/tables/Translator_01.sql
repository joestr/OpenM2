/******************************************************************************
 * The ibs_Translator_01 table. <BR>
 *
 * @version     2.21.0001, 04.06.2002 KR
 *
 * @author      Klaus Reimüller (KR)  020604
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_Translator_01
(
    oid             RAW (8)         NOT NULL, -- oid of the object
    extension       VARCHAR2 (15)   DEFAULT ('xml') -- extension of generated
                                                -- output file
)  /*TABLESPACE*/;
-- ibs_Translator_01

-- set default values:
ALTER TABLE ibs_Translator_01 MODIFY (oid DEFAULT hextoraw ('0000000000000000'));
ALTER TABLE ibs_Translator_01 MODIFY (extension DEFAULT ('xml'));

EXIT;
