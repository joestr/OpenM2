/******************************************************************************
 *
 * The ibs_ASCIITranslator_01 table. <BR>
 *
 * @version     2.2.0001, 01.03.2001
 *
 * @author      Bernd Buchegger    (BB)  20010301
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_ASCIITranslator_01
(
    oid                 RAW(8)          NOT NULL,
    separator           VARCHAR2(15),
    escapeSeparator     VARCHAR2(15),
    isIncludeMetadata   INTEGER,
    isIncludeHeader     INTEGER    
) /*TABLESPACE*/;

EXIT;

