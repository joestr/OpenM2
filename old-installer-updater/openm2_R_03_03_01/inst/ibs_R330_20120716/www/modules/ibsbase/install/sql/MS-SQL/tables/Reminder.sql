/******************************************************************************
 * The ibs_Reminder table contains the values for fields of type REMINDER.
 *
 * @version     $Id: Reminder.sql,v 1.1 2012/07/04 12:18:45 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT)  20120702
 ******************************************************************************
 */
CREATE TABLE ibs_Reminder
(
    oid                 OBJECTID        NOT NULL,
    fieldDbName         NAME            NOT NULL DEFAULT ('UNKNOWN'),
    reminderDate		DATETIME		NULL,
    remind1Days         INT             NOT NULL DEFAULT (0),
    remind1Text         NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN'),
    remind1Recip        NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN'),
    remind2Days         INT             NOT NULL DEFAULT (0),
    remind2Text         NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN'),
    remind2Recip        NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN'),
    escalateDays        INT             NOT NULL DEFAULT (0),
    escalateText        NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN'),
    escalateRecip       NVARCHAR(255)   NOT NULL DEFAULT ('UNKNOWN')
)
GO
-- ibs_Reminder