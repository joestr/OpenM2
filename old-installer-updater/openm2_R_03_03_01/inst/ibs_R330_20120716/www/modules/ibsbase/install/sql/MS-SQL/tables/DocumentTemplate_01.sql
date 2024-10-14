/******************************************************************************
 *
 * The ibs_DocumentTemplate_01 table contains the values for the object
 * DocumentTemplate_01.
 * 
 *
 * @version         1.10.0001, 04.08.1998
 *
 * @author      Michael Steiner (MS)  001004
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_DocumentTemplate_01
(
    oid                 OBJECTID        NOT NULL UNIQUE,
    objectType          NAME,
    objectSuperType     NAME,
    typeName            NAME,
    className           NAME,
    iconName            NAME,
    typeID              TYPEID,
    tVersionID          TVERSIONID,
    mayExistIn          NVARCHAR (255),
    isContainerType     BOOL            DEFAULT (0),
    mayContain          NVARCHAR (255),
    isSearchable        BOOL            DEFAULT (0),
    isInheritable       BOOL            DEFAULT (0),
    showInMenu          BOOL            DEFAULT (0),
    showInNews          BOOL            DEFAULT (0),
    systemDisplayMode   INT             DEFAULT (0),
    dbMapped            BOOL            DEFAULT (0),
    tableName           NVARCHAR (30),
    procCopy            NVARCHAR (30),
    mappingInfo         NTEXT,
    workflowTemplateOid OBJECTID,
    attachmentCopy      NVARCHAR (30),
    logDirectory        FILENAME,
    showDOMTree         BOOL
)
GO
-- ibs_DocumentTemplate_01
