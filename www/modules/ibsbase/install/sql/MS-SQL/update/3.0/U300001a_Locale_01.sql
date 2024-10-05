/******************************************************************************
 * The ibs_Locale_01 table definition. <BR>
 *
 * @version     $Id: U300001a_Locale_01.sql,v 1.1 2010/05/04 07:32:03 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann 20100322
 ******************************************************************************
 */
CREATE TABLE ibs_Locale_01
(
    oid             OBJECTID        NOT NULL UNIQUE,
    language        NVARCHAR (2)    NOT NULL, 
    country         NVARCHAR (2)    NULL,
    isDefault       BOOL            NOT NULL DEFAULT (0)
                                        -- is this the default locale
)
GO
-- ibs_Layout_01
 