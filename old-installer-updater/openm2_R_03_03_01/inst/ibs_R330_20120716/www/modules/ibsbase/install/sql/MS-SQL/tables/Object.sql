/******************************************************************************
 * The ibs object table incl. indexes. <BR>
 * The object table contains all currently existing system objects.
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */
CREATE TABLE ibs_Object
(
    id              ID              NOT NULL UNIQUE,
    oid             OBJECTID        NOT NULL,
    state           STATE           NOT NULL,
    tVersionId      TVERSIONID      NOT NULL,
    typeCode        NAME            NOT NULL DEFAULT ('UNKNOWN'),    
    typeName        NAME            NOT NULL DEFAULT ('UNKNOWN'),
    isContainer     BOOL            NOT NULL DEFAULT (0),
    name            NAME            NOT NULL,
    containerId     OBJECTID        NOT NULL,
    containerKind   INT             NOT NULL,
    containerOid2   OBJECTID        NOT NULL,   -- oid of container being two
                                                -- levels above
                                                -- (container of the container)
    isLink          BOOL            NOT NULL DEFAULT (0),
    linkedObjectId  OBJECTID        NOT NULL,
    showInMenu      BOOL            NOT NULL DEFAULT (0),
    flags           INT             NOT NULL DEFAULT (0),
                                        -- flags for some properties of the obj:
                                        -- Bit 0 ... hasMasterObject
                                        -- Bit 1 ... ...
    owner           USERID          NOT NULL,
    oLevel          INT             NOT NULL DEFAULT (0),
    posNo           POSNO           NOT NULL,
    posNoPath       POSNOPATH_VC    NOT NULL,
    creationDate    DATETIME        NOT NULL DEFAULT (getDate ()),
    creator         USERID          NOT NULL,
    lastChanged     DATETIME        NOT NULL DEFAULT (getDate ()),
    changer         USERID          NOT NULL,
    validUntil      DATETIME        NULL,
    description     DESCRIPTION     NULL,
    icon            NAME            NULL,
    processState    INT             NOT NULL DEFAULT (0),
    rKey            ID              NULL,
    consistsOfId    ID              NOT NULL
                                        -- unique tab id if the object
                                        -- represents a tab
)
GO
-- ibs_Object
