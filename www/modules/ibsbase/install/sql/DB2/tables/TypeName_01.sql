-------------------------------------------------------------------------------
-- The ibs TypeName_01 table incl. indexes. <BR>
-- The TypeName table contains all TypeNames of the system
-- visible to the user.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TYPENAME_01
(
    ID              INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20),
                                            -- unique id of the typeName
    LANGUAGEID      INTEGER NOT NULL WITH DEFAULT 0, 
                                            -- id of the language the value 
                                            -- of the variable is in
    NAME            VARCHAR (63) NOT NULL,   
                                            -- name of the variable 
                                            -- (same as in java)
    VALUE           VARCHAR (255),           
                                            -- value of the variable 
                                            -- (in a specific language)
    CLASSNAME       VARCHAR (255) NOT NULL   
                                            -- name of the classFile 
                                            -- the variable will be stored in
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_TYPENAMECLASSNAM ON IBSDEV1.IBS_TYPENAME_01
    (CLASSNAME ASC);
CREATE INDEX IBSDEV1.I_TYPENAMELANG_ID ON IBSDEV1.IBS_TYPENAME_01
    (ID ASC);
CREATE INDEX IBSDEV1.I_TYPENAMENAME ON IBSDEV1.IBS_TYPENAME_01
    (NAME ASC);
