/******************************************************************************
 * The ibs type table incl. indexes. <BR>
 * The type table contains all currently defined object types.
 *
 * @version     2.10.0002, 11.10.2000
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */
CREATE TABLE ibs_Type
(
    id              TYPEID          NOT NULL PRIMARY KEY,
    oid             OBJECTID        NULL,   -- fictive object id
    state           STATE           NOT NULL,
    name            NAME            NOT NULL,
    idProperty      PROPERTYSEQ     NOT NULL,
    superTypeId     TYPEID          NULL,
    mayContainInheritedTypeId TYPEID NOT NULL,
    isContainer     BOOL            NOT NULL DEFAULT (0),
    isInheritable   BOOL            NOT NULL DEFAULT (1),
    isSearchable    BOOL            NOT NULL DEFAULT (1),
    showInMenu      BOOL            NOT NULL DEFAULT (0),
    showInNews      BOOL            NOT NULL DEFAULT (1),
    code            NAME            NOT NULL,
    nextPropertySeq PROPERTYSEQ NOT NULL,
    actVersion      TVERSIONID      NULL,
    posNo           POSNO           NOT NULL,
    posNoPath       POSNOPATH       NOT NULL,
    description     DESCRIPTION     NULL,
    icon            NAME            NULL,
    validUntil      DATETIME        NOT NULL DEFAULT (getDate ())
)
GO