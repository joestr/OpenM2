/******************************************************************************
 * The ibs_EDITranslator_01 table. <BR>
 *
 * @version     2.21.0001, 25.06.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020625
  ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_EDITranslator_01
(
    oid                 RAW (8) UNIQUE  NOT NULL, -- oid of the object
    filterFile          VARCHAR2 (255),         -- name of the filter file
    formatFile          VARCHAR2 (255)          -- name of the format file
)  /*TABLESPACE*/;
-- ibs_EDITranslator_01

-- set default values:
ALTER TABLE ibs_EDITranslator_01 MODIFY (oid DEFAULT hextoraw ('0000000000000000'));


EXIT;
