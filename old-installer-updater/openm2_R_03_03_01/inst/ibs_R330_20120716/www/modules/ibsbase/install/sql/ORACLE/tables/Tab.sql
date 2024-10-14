/******************************************************************************
 * The ibs Tab table incl. indexes. <BR>
 * This table contains all tabs which are available throughout the system.
 *
 * @version     2.21.0004, 25.06.2002 KR
 *
 * @author      Mario Oberdorfer (MO)  010122
 ******************************************************************************
 */

CREATE SEQUENCE tabIdSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
	CACHE 20;


CREATE TABLE /*USER*/ibs_Tab
(
    id              NUMBER (10,0)   NOT NULL,   -- unique id of the tab
    oid             RAW (8)         NULL,       -- object id (for later use) 
    domainId        INTEGER         NOT NULL,   -- valid domain of tab
    code            VARCHAR2 (63)   NOT NULL,   -- definite description of tab                                            
    kind            INTEGER         NOT NULL,   -- kind of tab
    tVersionId      INTEGER         NULL,       -- id of type version
    fct             INTEGER         NOT NULL,   -- function of tab
    priority        INTEGER         NOT NULL,   -- priority of tab
    multilangKey    VARCHAR2 (63)   NOT NULL,   -- key for storing the
                                                -- multilang values of this tab
    rights          INTEGER         NOT NULL,   -- necessary rights to show the
                                                -- tab
    class           VARCHAR2 (255)  NULL        -- class to show the view tab
 ) /*TABLESPACE*/;
 
-- set default values:
ALTER TABLE /*USER*/ibs_Tab  MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (oid DEFAULT hexToRaw ('0000000000000000'));
ALTER TABLE /*USER*/ibs_Tab  MODIFY (domainId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (code DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_Tab  MODIFY (kind DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (tVersionId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (fct DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (priority DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (multilangKey DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_Tab  MODIFY (rights DEFAULT 0);
ALTER TABLE /*USER*/ibs_Tab  MODIFY (class DEFAULT 'undefined');

EXIT;
