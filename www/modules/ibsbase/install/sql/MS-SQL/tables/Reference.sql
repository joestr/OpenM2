/******************************************************************************
 * The Reference table. <BR>
 * This table contains all references which are existing within the system.
 *
 * @version     2.21.0001, 17.12.2001
 *
 * @author      Klaus Reimüller (KR)  011217
 ******************************************************************************
 */
CREATE TABLE ibs_Reference
(
    referencingOid  OBJECTID    NOT NULL,       -- oid of the referencing object
    fieldName       NAME        NULL,           -- name of the field which
                                                -- contains the reference
    referencedOid   OBJECTID    NULL,           -- oid of the referenced object
    kind            INT         NOT NULL        -- kind of reference
                                                -- 1 ... link (reference object)
                                                -- 2 ... OBJECTREF
                                                -- 3 ... FIELDREF
)
GO
-- ibs_Reference
