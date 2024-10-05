/******************************************************************************
 * The ibs UserProfile table incl. indexes. <BR>
 * The UserProfile table contains all currently existing user UserProfiles.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Bernd Buchegger (BB)  980709
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_UserProfile
(
    oid                     OBJECTID        NOT NULL,
    userId                  USERID          NOT NULL PRIMARY KEY,
    newsTimeLimit           INT             NULL DEFAULT 5,
    newsShowOnlyUnread      BOOL            NOT NULL,
    outboxUseTimeLimit      BOOL            NOT NULL,
    outboxTimeLimit         INT             NULL,
    outboxUseTimeFrame      BOOL            NOT NULL,
    outboxTimeFrameFrom     DATETIME        NULL,
    outboxTimeFrameTo       DATETIME        NULL,
    showExtendedAttributes  BOOL            NOT NULL,
    showFilesInWindows      BOOL            NOT NULL,
    lastLogin               DATETIME        NULL,
    layoutId                OBJECTID        NULL,
    showRef                 BOOL            NOT NULL,
    showExtendedRights      BOOL            NOT NULL,
    saveProfile             BOOL            NOT NULL,
    notificationKind        INT             NULL,
    sendSms                 BOOL            NOT NULL,
    addWeblink              BOOL            NOT NULL,
    localeId                OBJECTID        NULL 
)
GO
-- ibs_UserProfile
