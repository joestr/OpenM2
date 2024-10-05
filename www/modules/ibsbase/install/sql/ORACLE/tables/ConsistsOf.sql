/******************************************************************************
 * The ibs consistsOf table incl. indexes. <BR>
 * The consistsOf table contains the dependencies between types regarding tabs.
 *
 * @version     2.10.0001, 22.01.2001    
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

CREATE SEQUENCE consistsOfIdSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
    NOCACHE;


CREATE TABLE ibs_ConsistsOf
(
    id              NUMBER (10,0)   NOT NULL,   -- unique id of the tab
    oid             RAW (8)         NULL,       -- unique object id
                                                -- (for later use)
    tVersionId      INTEGER         NOT NULL,   -- tVersionId of the tab
    tabId           INTEGER         NOT NULL,   -- id of the tab in table
                                                -- ibs_Tab
    priority        INTEGER         NOT NULL,   -- priority of the tab
    rights          INTEGER         NOT NULL,   -- necessary rights to show the
                                                -- tab
    inheritedFrom   INTEGER         NOT NULL    -- id of type version from which 
                                                -- this tuple was inherited
 );

ALTER TABLE ibs_ConsistsOf  MODIFY (ID DEFAULT 0);
ALTER TABLE ibs_ConsistsOf  MODIFY (oid DEFAULT hextoraw ('0000000000000000'));
ALTER TABLE ibs_ConsistsOf  MODIFY (tVersionId DEFAULT 0);
ALTER TABLE ibs_ConsistsOf  MODIFY (tabId DEFAULT 0);
ALTER TABLE ibs_ConsistsOf  MODIFY (rights DEFAULT 0);
ALTER TABLE ibs_ConsistsOf  MODIFY (inheritedFrom DEFAULT 0);

exit;
