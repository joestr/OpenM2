/******************************************************************************
 * The m2_Catalog_01 table incl. indexes and triggers. <BR>
 * The m2_Catalog_01 table contains the values for the base object Catalog_01.
 *
 * @version     $Id: Catalog_01.sql,v 1.5 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Rupert Thurner (RT)  980521
 ******************************************************************************
 */
CREATE TABLE m2_Catalog_01
(
    oid         OBJECTID PRIMARY KEY,
    companyOid  OBJECTID NULL,
    ordResp     OBJECTID    NULL,   -- The responsible User, Group or
                                    -- Person for orderings
    ordRespMed  OBJECTID    NULL,   -- The medium the ordering is send 
                                    -- through
    contResp    OBJECTID    NULL,   -- The responsible User, Group or
                                    -- Person for the contents of this
                                    -- cataolog
    contRespMed OBJECTID    NULL,   -- The medium the content responsible is 
                                    -- reached      
    locked      BOOL NOT NULL default (0),       -- default not locked
    description1 DESCRIPTION NULL,
    description2 DESCRIPTION NULL,
    isOrderExport BOOL      NOT NULL default (0),       -- default do not use export order
    connectorOid  OBJECTID  NULL,
    translatorOid OBJECTID  NULL,
    filterId      INT       NULL, 
     -- notify orderresponsible by email  if he gets a new order
    notifyByEmail   BOOL    NOT NULL, 
    subject         DESCRIPTION NULL, -- subject of email
    content         DESCRIPTION NULL, -- content of email       
)
GO
-- m2_Catalog_01

-- unique key indices:

-- access indices:
