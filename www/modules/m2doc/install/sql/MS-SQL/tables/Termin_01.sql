/*****************************************************************************
 * The m2_Termin_01 table incl. indexes. <BR>
 * The m2_Termin_01 table contains the values for the base object Termin_01.
 * 
 * @version     $Id: Termin_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Horst Pichler (HP)  980513
 ******************************************************************************
 */
CREATE TABLE m2_Termin_01
(
    -- unique object id, reference to table ibs_object
    oid         OBJECTID        NOT NULL PRIMARY KEY,
    -- begin of term, date and time
    startDate   DATETIME        NOT NULL,
    -- end of term, date and time
    endDate     DATETIME        NOT NULL,
    -- place where term happens
    place       NVARCHAR (255)  NOT NULL,
    -- does term have participants?
    participants BOOL           NOT NULL,
    -- maximum number of participants
    maxNumParticipants INT      NOT NULL,
    -- viewing of participants list allowed?
    showParticipants BOOL       NOT NULL,
    -- deadline (time) to cancel the announcement
    deadline    DATETIME        NULL,
    -- may term have attachments?
    attachments BOOL            NOT NULL
)
GO
-- m2_Termin_01
