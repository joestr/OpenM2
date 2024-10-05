-------------------------------------------------------------------------------
-- The ibs_ObjectDesc_01 table incl. indexes. <BR>
-- The ObjectDesc table contains the names and descriptions of standard 
-- business objects.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_OBJECTDESC_01
(
    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),
                                        -- unique id of the object
    LANGUAGEID      INTEGER NOT NULL WITH DEFAULT 0,
                                        -- id of the language
    NAME            VARCHAR (63) NOT NULL,
                                        -- the unique name of the object
    OBJNAME         VARCHAR (63) NOT NULL,
                                        -- name of the business object
    OBJDESC         VARCHAR (255),       
					-- description of the BO
    CLASSNAME       VARCHAR (255) NOT NULL
                                        -- name of the classFile the variables
                                        --  will be stored in
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_OBJ_DESCCLASSNAM ON IBSDEV1.IBS_OBJECTDESC_01
    (CLASSNAME ASC);
CREATE INDEX IBSDEV1.I_OBJ_DESCLANG_ID ON IBSDEV1.IBS_OBJECTDESC_01
    (LANGUAGEID ASC);
CREATE INDEX IBSDEV1.I_OBJ_DESCNAME ON IBSDEV1.IBS_OBJECTDESC_01
    (NAME ASC);
