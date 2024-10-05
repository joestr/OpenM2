/******************************************************************************
 * The ibs_Translator_01 table. <BR>
 *
 * @version     2.21.0001, 28.02.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020228
 ******************************************************************************
 */
CREATE TABLE ibs_Translator_01
(
    oid                 OBJECTID        NOT NULL, -- oid of the object
    extension           NVARCHAR (15) DEFAULT ('xml') -- extension of generated
                                                -- output file
)
GO

