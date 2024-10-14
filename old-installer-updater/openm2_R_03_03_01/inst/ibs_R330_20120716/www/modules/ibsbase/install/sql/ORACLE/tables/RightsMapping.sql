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
CREATE TABLE /*USER*/ibs_RightsMapping
(
    aliasName   VARCHAR2(63)    NOT NULL,   -- name of rights-alias
    rightName   VARCHAR2(63)    NOT NULL    -- m2-right
);

ALTER TABLE /*USER*/ibs_RightsMapping modify ( aliasName DEFAULT 'UNDEFINED' );
ALTER TABLE /*USER*/ibs_RightsMapping modify ( rightName DEFAULT 'UNDEFINED' );

COMMIT WORK;

EXIT;