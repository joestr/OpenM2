/******************************************************************************
 * All stored procedures regarding the ParticipantContainer_01 object. <BR>
 *
 * @version     $Id: ParticipantContainer_01Proc.sql,v 1.4 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Checks announcements to a given term. <BR>
 *
 * @input parameters:
 * @param   @partContId_s       oid of participant container
 * @param   @userId                users id
 * @output parameter
 * @param   @announced          user already announced?
 * @param   @free               number of free places
 */
CREATE OR REPLACE PROCEDURE p_ParticipantCont_01$chkPart
(
    -- input parameters:
    ai_partContId_s    VARCHAR2,    -- oid of participant container
    ai_userId          INTEGER,              -- users id
    -- output parameter
    ao_announced       OUT     NUMBER,    -- user already announced?
    ao_free            OUT     INTEGER,   -- number of free places
    ao_deadline        OUT     DATE,      -- deadline: unannouncement possible until ..
    ao_startDate       OUT     DATE       -- startDate of term -> announcement possible until ...
)
AS
    -- CONVERSIONS (OBJECTIDSTRING) - all input object ids must be converted
    l_partContId          RAW(8);
    l_oid                 RAW(8);
    -- local variables:
    l_numParticipants     INTEGER;
    l_maxNumParticipants  INTEGER;
    l_count               INTEGER := 0;
BEGIN
    -- convert string to raw (8)
    p_StringToByte (ai_partContId_s, l_partContId);

    -- get oid from referring term
    SELECT  containerId
    INTO    l_oid
    FROM    ibs_Object
    WHERE   oid = l_partContId;

    -- check if term has limited announcements
    SELECT  maxNumParticipants, deadline, startDate
    INTO    l_maxNumParticipants,
            ao_deadline,
            ao_startDate
    FROM    m2_Termin_01
    WHERE   oid = l_oid;

    -- check if deadline is set
    IF ao_deadline IS NULL
    THEN
        -- set deadline to startdate
        ao_deadline := ao_startDate;
    END IF;
    -- check if user is already announced to term
    -- count participants
    SELECT  COUNT(*)
    INTO    l_count 
    FROM    ibs_object o, m2_Participant_01 p
    WHERE   containerId = l_partContId 
    AND     tversionid =  16854529
    AND     o.oid = p.oid
    AND     o.name = p.announcerName    -- name of participant is stored in ibs_object.name
                                        -- name of announcer is stored in m2_Participant_01.announcerName
                                        -- if ibs_obect.name == m2_Participant_01.announcerName
                                        -- then participant object is announcer himself
    AND     p.announcerId = ai_userId
    AND o.state = 2;                    -- only active objects    

    -- set boolean value 'announced' according to users
    -- announcements
    IF (l_count > 0)
    THEN
        -- set 'true'
        ao_announced := -1;
    ELSE
        -- set 'false'
        ao_announced := 0;
    END IF;

    -- has term limited participants?
    IF  (l_maxNumParticipants >= 1)
    THEN
        -- count number of participants for term
        SELECT  COUNT (*)
        INTO    l_count
        FROM    ibs_Object
        WHERE   containerId = l_partContID
        AND     state = 2;
        -- calculate free announcement places
        ao_free := l_maxNumParticipants - l_count;
    ELSE
        -- unlimitied participants
        ao_free := 2000000000;
    END IF;

COMMIT WORK;
    -- exit procedure
    RETURN;

EXCEPTION
    WHEN OTHERS THEN
      ibs_error.log_error ( ibs_error.error, ' p_ParticipantCont_01$chkPart',
      ', partContId_s = ' || ai_partContId_s ||
      ', userId = ' || ai_userId ||
      ', announced = ' || ao_announced ||
      ', free = ' || ao_free ||
      ', deadline = ' || ao_deadline ||
      ', startDate = ' || ao_startDate ||
      ', errorcode = ' || SQLCODE ||
      ', errormessage = ' || SQLERRM);
END p_ParticipantCont_01$chkPart;
/

show errors;

EXIT;