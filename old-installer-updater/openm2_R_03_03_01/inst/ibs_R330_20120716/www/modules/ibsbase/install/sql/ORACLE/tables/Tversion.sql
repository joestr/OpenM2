/******************************************************************************
 * The ibs type version table incl. indexes. <BR>
 * The type version table contains the versions of all currently defined object 
 * types.
 *
 * @version     2.10.0003, 17.10.2000
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

-- create the table:
CREATE TABLE /*USER*/ibs_TVersion
(
    id              NUMBER (10, 0)  NOT NULL,   -- id of type version
    state           NUMBER (10, 0)  NOT NULL,   -- state of the version
    typeId          NUMBER (10, 0)  NOT NULL,   -- id of type to which the
                                                -- version belongs
    tVersionSeq     NUMBER (10, 0)  NOT NULL,   -- sequence number of version
    idProperty      NUMBER (10, 0)  NOT NULL,   -- property which represents
                                                -- the id
    orderProperty   NUMBER (10, 0),              -- property used for ordering
    superTVersionId NUMBER (10, 0),              -- id of actual version of super
                                                -- type
    code            VARCHAR2 (63),              -- code of the version
    className       VARCHAR2 (63),              -- class which implements the
                                                -- business logic of the version
    nextObjectSeq   NUMBER (10, 0)  NOT NULL,   -- sequence number for next
                                                -- object with this version
    posNo           NUMBER (10, 0)  NOT NULL,   -- the posNo of the tVersion
    posNoPath       VARCHAR2 (254)  NOT NULL,   -- the posNoPath of the tVersion
    defaultTab      INTEGER         NOT NULL    -- the default tab of the
                                                -- tVersion
                                                -- (id of ibs_ConsistsOf)
) /*TABLESPACE*/;

-- set default values:
ALTER TABLE /*USER*/ibs_TVersion MODIFY (ID DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (STATE DEFAULT 2);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (TYPEID DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (TVERSIONSEQ DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (idproperty DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (orderproperty DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (supertversionid DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (code DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_TVersion MODIFY (className DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_TVersion MODIFY (nextobjectseq DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (posNo DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersion MODIFY (posNoPath DEFAULT '0000');
ALTER TABLE /*USER*/ibs_TVersion MODIFY (defaultTab DEFAULT 0);

EXIT;
