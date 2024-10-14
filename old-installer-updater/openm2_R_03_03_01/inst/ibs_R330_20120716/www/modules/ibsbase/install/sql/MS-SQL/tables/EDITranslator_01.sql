/******************************************************************************
 * The ibs_EDITranslator_01 table. <BR>
 *
 * @version     2.21.0001, 27.02.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020227
  ******************************************************************************
 */
CREATE TABLE ibs_EDITranslator_01
(
    oid                 OBJECTID        NOT NULL, -- oid of the object
    filterFile          FILENAME,               -- name of the filter file
    formatFile          FILENAME                -- name of the format file
)
GO

