-------------------------------------------------------------------------------
-- The ibs type version table incl. indexes. <BR>
-- The type version table contains the versions of all currently defined object 
-- types.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TVERSION
(
    ID              INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of type version
    STATE           INTEGER NOT NULL WITH DEFAULT 2,
                                            -- state of the version
    TYPEID          INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of type to which the
                                            -- version belongs
    TVERSIONSEQ     INTEGER NOT NULL WITH DEFAULT 0,
                                            -- sequence number of version
    IDPROPERTY      INTEGER NOT NULL WITH DEFAULT 0,
                                            -- property which represents
                                            -- the id
    ORDERPROPERTY   INTEGER      WITH DEFAULT 0,
                                            -- property used for ordering
    SUPERTVERSIONID INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of actual version of super
                                            -- type
    CODE            VARCHAR (63)  WITH DEFAULT 'undefined',
                                            -- code of the version
    CLASSNAME       VARCHAR (63)  WITH DEFAULT 'undefined',
                                            -- class which implements the
                                            -- business logic of the version
    NEXTOBJECTSEQ   INTEGER NOT NULL WITH DEFAULT 0,
                                            -- sequence number for next
                                            -- object with this version
    POSNO           INTEGER NOT NULL WITH DEFAULT 0,
                                            -- the posNo of the tVersion
    POSNOPATH       VARCHAR (254) NOT NULL WITH DEFAULT '0000',
                                            -- the posNoPath of the tVersion
    DEFAULTTAB      INTEGER NOT NULL WITH DEFAULT 0
                                            -- the default tab of the
                                            -- tVersion
                                            -- (id of ibs_ConsistsOf)
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_TVERSIONID ON IBSDEV1.IBS_TVERSION
    (ID ASC);
CREATE INDEX IBSDEV1.I_TVERSIONSTATE ON IBSDEV1.IBS_TVERSION
    (STATE ASC);
CREATE INDEX IBSDEV1.I_TVER_SUP_TVER_ID ON IBSDEV1.IBS_TVERSION
    (SUPERTVERSIONID ASC);
