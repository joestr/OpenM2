/******************************************************************************
 * The Reference table. <BR>
 * This table contains all references which are existing within the system.
 *
 * @version     2.21.0001, 04.06.2002 KR
 *
 * @author      Klaus Reimüller (KR)  020604
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_Reference
(
    referencingOid  RAW (8)     NOT NULL,       -- oid of the referencing object
    fieldName       VARCHAR2 (63) NULL,         -- name of the field which
                                                -- contains the reference
    referencedOid   RAW (8)     NULL,           -- oid of the referenced object
    kind            INTEGER     NOT NULL        -- kind of reference
                                                -- 1 ... link (reference object)
                                                -- 2 ... OBJECTREF
                                                -- 3 ... FIELDREF
)  /*TABLESPACE*/;
-- ibs_Reference

-- set default values:
ALTER TABLE ibs_Reference MODIFY (referencingOid DEFAULT hextoraw ('0000000000000000'));
ALTER TABLE ibs_Reference MODIFY (referencedOid DEFAULT hextoraw ('0000000000000000'));

EXIT;
