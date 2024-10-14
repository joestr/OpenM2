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

-- create the sequence:
CREATE SEQUENCE objectDescIdSeq
                INCREMENT BY 1
                START WITH 1
                NOMAXVALUE
                NOCYCLE
                CACHE 20;

-- create the table:
CREATE TABLE /*USER*/ibs_ObjectDesc_01
(
    id          NUMBER (10, 0)  NOT NULL,   -- unique id of the object
    languageId  NUMBER (10, 0)  NOT NULL,   -- id of the language
    name        VARCHAR2 (63)   NOT NULL,   -- the unique name of the object
    objName     VARCHAR2 (63)   NOT NULL,   -- name of the business object
    objDesc     VARCHAR2 (255)  NULL,       -- description of the BO
    className   VARCHAR2 (255)  NOT NULL    -- name of the classFile the variables will be stored in
) /*TABLESPACE*/;

-- default values:
ALTER TABLE /*USER*/ibs_ObjectDesc_01 MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/ibs_ObjectDesc_01 MODIFY (languageId DEFAULT 0);

EXIT;
