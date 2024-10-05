/******************************************************************************
 * The ibs_ObjectDesc_01 table incl. indexes. <BR>
 * The ObjectDesc table contains the names and descriptions of standard 
 * business objects.
 *
 * @version     2.00.0001, 29.02.2000
 *
 * @author      Klaus Reimüller (KR)  000229
 ******************************************************************************
 */
CREATE TABLE ibs_ObjectDesc_01
(
    id          ID              NOT NULL UNIQUE, -- unique id of the object
    languageId  ID              NOT NULL,   -- id of the language
    name        NAME            NOT NULL UNIQUE, -- the unique name of the object
    objName     NAME            NOT NULL,   -- name of the business object
    objDesc     DESCRIPTION     NULL,       -- description of the BO
    className   FILENAME        NOT NULL    -- name of the classFile the variables will be stored in
)
GO
-- ibs_ObjectDesc_01
