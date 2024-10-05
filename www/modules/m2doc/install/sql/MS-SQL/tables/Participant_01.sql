/*****************************************************************************
 * The m2_Participant_01 table. <BR>
 * 
 * @version     $Id: Participant_01.sql,v 1.3 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  991111
 ******************************************************************************
 */
    -- create new table m2_Participant_01
    CREATE TABLE m2_Participant_01
    (
        oid                     OBJECTID        NOT NULL PRIMARY KEY,
        announcerId             ID              NOT NULL,
        announcerName           NAME            NOT NULL
    )
    GO
    -- m2_Participant_01
