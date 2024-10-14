/******************************************************************************
 * The ibs_DocumentTemplate_01 table contains the values for the object
 * DocumentTemplate_01.
 * 
 * @version     2.21.0008, 25.06.2002 KR
 *
 * @author      Michael Steiner (MS)  001004
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_DocumentTemplate_01
(
    oid                 RAW (8)         NOT NULL UNIQUE,
    objectType          VARCHAR2 (63),
    objectSuperType     VARCHAR2 (63),
    typeName            VARCHAR2 (63),
    className           VARCHAR2 (63),
    iconName            VARCHAR2 (63),
    typeID              INTEGER         DEFAULT (0),
    tVersionID          INTEGER         DEFAULT (0),
    mayExistIn          VARCHAR2 (255),
    isContainerType     NUMBER (1)      DEFAULT (0),
    mayContain          VARCHAR2 (255),
    isSearchable        NUMBER (1)      DEFAULT (0),
    isInheritable       NUMBER (1)      DEFAULT (0),
    showInMenu          NUMBER (1)      DEFAULT (0),
    showInNews          NUMBER (1)      DEFAULT (0),
    systemDisplayMode   INTEGER         DEFAULT (0),
    dbMapped            NUMBER (1)      DEFAULT (0),
    tableName           VARCHAR (30),
    procCopy            VARCHAR (30),
    mappingInfo         CLOB,
    workflowTemplateOid RAW (8),
    attachmentCopy      VARCHAR2 (30),
    logDirectory        VARCHAR2 (255),
    showDOMTree         NUMBER (1)
) /*TABLESPACE*/;
-- ibs_DocumentTemplate_01

EXIT;
