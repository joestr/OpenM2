-------------------------------------------------------------------------------
-- The ibs Exception_01 table incl. indexes. <BR>
-- The exception table contains all ExceptionMessages of the system
-- visible to the user.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)  020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_EXCEPTION_01
(
   ID               INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),
                                            -- unique id of the exception
   LANGUAGEID       INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of the language the value of 
                                            -- the variable is in
   NAME             VARCHAR (63),            
                                            -- name of the variable (same as in
                                            -- java)
   VALUE            VARCHAR (2048),          
                                            -- value of the variable (in a 
                                            -- specific language)
   CLASSNAME        VARCHAR (255)            
                                            -- name of the classFile the 
                                            --  variable will be stored in
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_EXCEPTCLASSNAME ON IBSDEV1.IBS_EXCEPTION_01
    (CLASSNAME ASC);
CREATE INDEX IBSDEV1.I_EXCEPTIONID ON IBSDEV1.IBS_EXCEPTION_01
    (ID ASC);
CREATE INDEX IBSDEV1.I_EXCEPTIONNAME ON IBSDEV1.IBS_EXCEPTION_01
    (NAME ASC);
