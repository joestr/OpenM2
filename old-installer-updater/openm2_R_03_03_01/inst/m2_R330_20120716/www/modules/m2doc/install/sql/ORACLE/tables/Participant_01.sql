/*****************************************************************************
 * The m2_Participant_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Participant_01.sql,v 1.5 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  991111
 ******************************************************************************
 */

    -- create new table m2_Participant_01
    CREATE TABLE /*USER*/M2_PARTICIPANT_01
    (
        OID                     RAW (8)         NOT NULL,
        ANNOUNCERID             INTEGER         NOT NULL,
        ANNOUNCERNAME           VARCHAR2 (63)   NOT NULL
    ) /*TABLESPACE*/;
     -- m2_Participant_01

    EXIT;
