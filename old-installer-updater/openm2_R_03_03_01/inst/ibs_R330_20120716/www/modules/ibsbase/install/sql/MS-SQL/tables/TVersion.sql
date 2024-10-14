/******************************************************************************
 * The ibs type version table incl. indexes. <BR>
 * The type version table contains the versions of all currently defined object 
 * types.
 * 
 * @version     2.10.0003, 17.10.2000
 *
 * @author      Klaus Reimüller (KR)  980416
 ******************************************************************************
 */
CREATE TABLE ibs_TVersion
(
    id              TVERSIONID      NOT NULL PRIMARY KEY, -- id of type version
    state           STATE           NOT NULL,   -- state of the version
    typeId          TYPEID          NOT NULL,   -- id of type to which the
                                                -- version belongs
    tVersionSeq     TVERSIONSEQ     NOT NULL,   -- sequence number of version
    idProperty      PROPERTYID      NOT NULL,   -- property which represents
                                                -- the id
    orderProperty   PROPERTYID      NULL,       -- property used for ordering
    superTVersionId TVERSIONID      NULL,       -- id of actual version of super
                                                -- type
    code            NAME            NULL,       -- code of the version
    className       NAME            NULL,       -- class which implements the
                                                -- business logic of the version
    nextObjectSeq   OBJECTSEQ       NOT NULL,   -- sequence number for next
                                                -- object with this version
    posNo           POSNO           NOT NULL,   -- the posNo of the tVersion
    posNoPath       POSNOPATH_VC    NOT NULL,   -- the posNoPath of the tVersion
    defaultTab      ID              NOT NULL    -- the default tab of the
                                                -- tVersion
                                                -- (id of ibs_ConsistsOf)
)
GO