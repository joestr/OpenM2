-------------------------------------------------------------------------------
-- The Reference table. <BR>
-- This table contains all references which are existing within the system.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_REFERENCE
(
    FIELDNAME       VARCHAR (63) NOT NULL,
                                        -- name of the field which
                                        -- contains the reference
    KIND            INTEGER NOT NULL,   
                                        -- kind of reference
                                        -- 1 ... link (reference object)
                                        -- 2 ... OBJECTREF
                                        -- 3 ... FIELDREF
    REFERENCINGOID  CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                        -- oid of the referencing object
    REFERENCEDOID   CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
                                        -- oid of the referenced object
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_REFERENCEOID ON IBSDEV1.IBS_REFERENCE
    (REFERENCINGOID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_REFERENCEOIDNAME ON IBSDEV1.IBS_REFERENCE
    (REFERENCINGOID ASC, FIELDNAME ASC);
CREATE INDEX IBSDEV1.I_REFERENCEREFOID ON IBSDEV1.IBS_REFERENCE
    (REFERENCEDOID ASC);
