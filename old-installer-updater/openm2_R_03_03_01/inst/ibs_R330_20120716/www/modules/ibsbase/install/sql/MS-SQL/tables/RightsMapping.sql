/******************************************************************************
 *
 * The ibs_RightsMapping table. <BR>
 *
 * @version         2.50.0001, 1.11.2000
 *
 * @author      Horst Pichler (HP)  01112000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_RightsMapping
(
    aliasName   NAME    NOT NULL,   -- name of rights-alias
    rightName   NAME    NOT NULL    -- m2-right
)
GO