/******************************************************************************
 * The ibs TypeName_01 table incl. indexes. <BR>
 * The TypeName table contains all TypeNames of the system
 * visible to the user.
 *
 * @version     1.10.0001, 14.12.1999
 *
 * @author      Keim Christine (CK)  981214
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_TypeName_01
(
    id          ID              NOT NULL UNIQUE,  -- unique id of the typeName
    languageId  ID              NOT NULL,         -- id of the language the value of the variable is in
    name        DESCRIPTION     NOT NULL,         -- name of the variable (same as in java)
    value       NAME            NULL,             -- value of the variable (in a specific language)
    className   FILENAME        NOT NULL          -- name of the classFile the variable will be stored in
)
GO
-- ibs_TypeName_01

