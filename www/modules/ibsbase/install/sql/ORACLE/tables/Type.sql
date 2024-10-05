/******************************************************************************
 * The ibs_Type table incl. indexes. <BR>
 * The type table contains all currently defined object types.
 * 
 * @version     2.10.0002, 11.10.2000
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

-- create the id sequence:
CREATE SEQUENCE typeIdSeq
	INCREMENT BY 16
	START WITH 16842768 -- 0x01010010
	NOMAXVALUE
	NOCYCLE
    NOCACHE;


-- create the table:
CREATE TABLE /*USER*/ibs_Type
(
    id              NUMBER (10,0)   NOT NULL,
    oid             RAW (8),                -- fictive object id
    state           NUMBER (10,0)   NOT NULL,
    name            VARCHAR2 (63)   NOT NULL,
    idProperty      NUMBER (10,0)   NOT NULL,
    superTypeId     NUMBER (10,0),
    mayContainInheritedTypeId NUMBER (10, 0) NOT NULL,
    isContainer     NUMBER (1,0)    NOT NULL,
    isInheritable   NUMBER (1,0),
    isSearchable    NUMBER (1,0),
    showInMenu      NUMBER (1,0),
    showInNews      NUMBER (1,0),
    code            VARCHAR2 (63)   NOT NULL,
    nextPropertySeq NUMBER (10,0)   NOT NULL,
    actVersion      NUMBER (10,0),
    posNo           NUMBER (10,0)   NOT NULL,
    posNoPath       RAW (254)       NOT NULL,
    description     VARCHAR2 (255),
    icon            VARCHAR2 (63),
    validUntil      DATE            NOT NULL
) /*TABLESPACE*/;

-- set default values:
ALTER TABLE /*USER*/ibs_Type  MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (oid DEFAULT hextoraw('0000000000000000'));
ALTER TABLE /*USER*/ibs_Type  MODIFY (state DEFAULT 2);
ALTER TABLE /*USER*/ibs_Type  MODIFY (name DEFAULT  'undefined');
ALTER TABLE /*USER*/ibs_Type  MODIFY (idProperty DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (superTypeId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (mayContainInheritedTypeId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (isContainer DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (isInheritable DEFAULT 1);
ALTER TABLE /*USER*/ibs_Type  MODIFY (isSearchable DEFAULT 1);
ALTER TABLE /*USER*/ibs_Type  MODIFY (showInMenu DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (showInNews DEFAULT 1);
ALTER TABLE /*USER*/ibs_Type  MODIFY (code DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_Type  MODIFY (nextPropertySeq DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (actVersion DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (posNo DEFAULT 0);
ALTER TABLE /*USER*/ibs_Type  MODIFY (posNoPath DEFAULT hextoraw('0000'));
ALTER TABLE /*USER*/ibs_Type  MODIFY (description DEFAULT  null);
ALTER TABLE /*USER*/ibs_Type  MODIFY (icon DEFAULT 'undefined');

exit;
