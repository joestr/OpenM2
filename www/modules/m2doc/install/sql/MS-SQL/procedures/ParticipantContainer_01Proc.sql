/******************************************************************************
 * All stored procedures regarding the ParticipantContainer_01 object. <BR>
 *
 * @version     $Id: ParticipantContainer_01Proc.sql,v 1.5 2006/01/19 15:56:46 klreimue Exp $
 *
 * @author      Horst Pichler   (HP)  98xxxx
 ******************************************************************************
 */


/******************************************************************************
 * Checks announcements to a given term. <BR>
 *
 * @input parameters:
 * @param   @partContId_s       oid of participant container
 * @param   @userId             users id
 * @output parameter
 * @param   @announced          user already announced?
 * @param   @free               number of free places
 */
IF EXISTS (SELECT * FROM sysobjects WHERE id = object_id('p_ParticipantCont_01$chkPart') AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_ParticipantCont_01$chkPart
GO

-- create the new procedure:
CREATE PROCEDURE p_ParticipantCont_01$chkPart
(
    -- input parameters:
    @partContId_s OBJECTIDSTRING,    -- oid of participant container
    @userId       USERID,            -- users id
    -- output parameter
    @announced  BOOL      OUTPUT,    -- user already announced?
    @free       INT       OUTPUT,    -- number of free places
    @deadline   DATETIME  OUTPUT,    -- deadline: unannouncement possible until ..
    @startDate  DATETIME  OUTPUT     -- startDate of term -> announcement possible until ...
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @partContId     OBJECTID
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @partContId_s, @partContId OUTPUT

    ---------------------------------------------------------------------------
    -- LOCAL VARIABLES
    DECLARE @numParticipants    INT
    DECLARE @maxNumParticipants INT

    ---------------------------------------------------------------------------
    -- START

    -- get oid from referring term
    SELECT @oid = containerId
    FROM ibs_Object
    WHERE oid = @partContId

    -- check if term has limited announcements
    SELECT @maxNumParticipants = maxNumParticipants, @deadline = deadline,
           @startDate = startDate
    FROM m2_Termin_01
    WHERE oid = @oid

    -- check if deadline is set
    IF @deadline IS NULL
        -- set deadline to startdate
        SELECT @deadline = @startDate

    -- check if user is already announced to term
    -- count participants
    SELECT o.oid 
    FROM ibs_object o, m2_Participant_01 p
    WHERE containerId = @partContId 
    AND tversionid = 0x01012E01
    AND o.oid = p.oid
    AND o.name = p.announcerName        -- name of participant is stored in ibs_object.name
                                        -- name of announcer is stored in m2_Participant.announcerName
                                        -- if ibs_obect.name == m2_Participant.announcerName
                                        -- then participant object is announcer himself
    AND p.announcerId = @userId
    AND o.state = 2                     -- only active objects

    -- set boolean value 'announced' according to users
    -- announcements
    IF @@ROWCOUNT > 0
    BEGIN
        -- set 'true'
        SELECT @announced = -1
    END
    ELSE
    BEGIN
        -- set 'false'
        SELECT @announced = 0
    END

/*
    -- get oid from referring term
    SELECT @oid = containerId
    FROM ibs_Object
    WHERE oid = @partContId 

    -- check if term has limited announcements
    SELECT @maxNumParticipants = maxNumParticipants
    FROM m2_Termin_01
    WHERE oid = @oid
*/

    -- has term limited participants?
    IF   @maxNumParticipants >= 1
    BEGIN
        -- count number of participants for term
        SELECT oid
        FROM ibs_Object
        WHERE containerId = @partContID
        AND state = 2
        -- calculate free announcement places
        SELECT @free = @maxNumParticipants - @@ROWCOUNT
    END
    ELSE
    BEGIN
        -- unlimitied participants
        SELECT @free = 2000000000      
        RETURN
    END

    -- exit procedure
    RETURN
GO


