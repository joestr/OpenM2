/******************************************************************************
 *
 * The ibs_MenuTab_01 indexes and triggers. <BR>
 * The ibs_MenuTab_01 table contains the values for the m2 object MenuTab_01.
 *
 *
 * @version 1.10.0001
 *
 * @author  Monika Eisenkolb (ME)  011001
 *
 ******************************************************************************
 */
CREATE TABLE ibs_MenuTab_01
(
    oid             OBJECTID        NOT NULL PRIMARY KEY,
    objectOid       OBJECTID,
    description     DESCRIPTION     NOT NULL,
    isPrivate       BOOL,
    priorityKey     INT,
    domainId        INT,
    classFront      DESCRIPTION     NOT NULL,
    classBack       DESCRIPTION     NOT NULL,
    fileName        DESCRIPTION     NOT NULL,
    levelStep       INT             NOT NULL,
    levelStepMax    INT             NOT NULL
)
GO
