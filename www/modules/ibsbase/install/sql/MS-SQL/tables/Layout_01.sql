/******************************************************************************
 * The ibs_Layout_01 indexes and triggers. <BR>
 *
 * @version     $Id: Layout_01.sql,v 1.3 2005/08/22 15:24:50 klaus Exp $
 *
 * @author      ??? (??)  980513
 ******************************************************************************
 */
CREATE TABLE ibs_Layout_01
(
    oid             OBJECTID        NOT NULL UNIQUE,
    name            NAME            NOT NULL,
    domainId        DOMAINID        NOT NULL,
    isDefault       BOOL            NOT NULL DEFAULT (0)
                                        -- is this the default layout for the
                                        -- domain?
)
GO
-- ibs_Layout_01
 