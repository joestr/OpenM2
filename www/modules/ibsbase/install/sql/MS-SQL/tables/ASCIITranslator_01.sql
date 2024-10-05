/******************************************************************************
 * The ibs_ASCIITranslator_01 table. <BR>
 *
 * @version     $Id: ASCIITranslator_01.sql,v 1.3 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Bernd Buchegger    (BB)  200010301
 ******************************************************************************
 */

CREATE TABLE ibs_ASCIITranslator_01
(
    oid                 OBJECTID        NOT NULL,
    separator           NVARCHAR (15),
    escapeSeparator     NVARCHAR (15),
    isIncludeMetadata   BOOL,
    isIncludeHeader     BOOL
)
GO

