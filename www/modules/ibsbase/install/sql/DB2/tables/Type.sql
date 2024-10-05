-------------------------------------------------------------------------------
-- The ibs type table incl. indexes. <BR>
-- The type table contains all currently defined object types.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement
CREATE TABLE IBSDEV1.IBS_TYPE
(
    ID              INTEGER     NOT NULL WITH DEFAULT 0,
    OID             CHAR (8)    FOR BIT DATA
                                WITH DEFAULT X'0000000000000000',
                                            -- fictive object id
    STATE           INTEGER     NOT NULL WITH DEFAULT 2,
    NAME            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
    IDPROPERTY      INTEGER     NOT NULL WITH DEFAULT 0,
    SUPERTYPEID     INTEGER     NOT NULL WITH DEFAULT 0,
    MAYCONTAININHERITEDTYPEID INTEGER NOT NULL WITH DEFAULT 0,
    ISCONTAINER     SMALLINT    NOT NULL WITH DEFAULT 0,
    ISINHERITABLE   SMALLINT    NOT NULL WITH DEFAULT 1,
    ISSEARCHABLE    SMALLINT    NOT NULL WITH DEFAULT 1,
    SHOWINMENU      SMALLINT    NOT NULL WITH DEFAULT 0,
    SHOWINNEWS      SMALLINT    NOT NULL WITH DEFAULT 1,
    CODE            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
    NEXTPROPERTYSEQ INTEGER     NOT NULL WITH DEFAULT 0,
    ACTVERSION      INTEGER     WITH DEFAULT 0,
    POSNO           INTEGER     NOT NULL WITH DEFAULT 0,
    DESCRIPTION     VARCHAR (255) WITH DEFAULT 'null',
    ICON            VARCHAR (63) WITH DEFAULT 'undefined',
    VALIDUNTIL      TIMESTAMP   NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
    POSNOPATH       VARCHAR (254) NOT NULL FOR BIT DATA WITH DEFAULT X'00'
);

-- Create index statements
CREATE UNIQUE INDEX IBSDEV1.INDEXTYPEID ON IBSDEV1.IBS_TYPE
    (ID ASC);
CREATE INDEX IBSDEV1.INDEXTYPENAME ON IBSDEV1.IBS_TYPE
    (NAME ASC);
CREATE INDEX IBSDEV1.INDEXTYPESTATE ON IBSDEV1.IBS_TYPE
    (STATE ASC);
