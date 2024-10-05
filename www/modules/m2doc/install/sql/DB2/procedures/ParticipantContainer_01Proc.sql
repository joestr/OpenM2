--------------------------------------------------------------------------------
-- All stored procedures regarding the ParticipantContainer_01 object. <BR>
--
-- @version     $Id: ParticipantContainer_01Proc.sql,v 1.4 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Checks announcements to a given term. <BR>
--
-- @input parameters:
-- @param   @partContId_s       oid of participant container
-- @param   @userId             users id
-- @output parameter
-- @param   @announced          user already announced?
-- @param   @free               number of free places
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_ParticipantCont_01$chkPart');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_ParticipantCont_01$chkPart(
    -- input parameters:
    IN  ai_partContId_s     VARCHAR (18),    -- oid of participant container
    IN  ai_userId           INT,            -- users id
    -- output parameter
    OUT ao_announced        SMALLINT,       -- user already announced?
    OUT ao_free             INT,            -- number of free places
    OUT ao_deadline         TIMESTAMP,      -- deadline: unannouncement possible until ..
    OUT ao_startDate        TIMESTAMP       -- startDate of term -> announcement possible until ...
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    ---------------------------------------------------------------------------
    -- conversionS (VARCHAR (18)) - all input object ids must be converted
    DECLARE l_partContId    CHAR (8) FOR BIT DATA;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------
    -- LOCAL VARIABLES
    DECLARE l_numParticipants INT;
    DECLARE l_maxNumParticipants INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_partContId_s, l_partContId);
  
-- body:
    -- get oid from referring term
    SELECT containerId
    INTO l_oid
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_partContId;
  
    -- check if term has limited announcements
    SELECT maxNumParticipants, deadline, startDate
    INTO l_maxNumParticipants, ao_deadline, ao_startDate
    FROM IBSDEV1.m2_Termin_01
    WHERE oid = l_oid;
  
    -- check if deadline is set
    IF ao_deadline IS NULL THEN 
        -- set deadline to startdate
        SET ao_deadline = ao_startDate;
    END IF;
    -- check if user is already announced to term
    -- count participants
    SELECT COUNT(*) 
    INTO l_rowcount
    FROM   (
                    SELECT o.oid 
                    FROM IBSDEV1.ibs_object o, IBSDEV1.m2_Participant_01 p
                    WHERE containerId = l_partContId
                        AND tversionid = 16854529
                        AND o.oid = p.oid
                        AND o.name = p.announcerName
                        AND p.announcerId = ai_userId
                        AND o.state = 2
                ) AS temp_table2;
    -- set boolean value 'announced' according to users
    -- announcements
    IF l_rowcount > 0 THEN 
        -- set 'true'
        SET ao_announced = -1;
    ELSE 
        -- set 'false'
        SET ao_announced = 0;
    END IF;
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
    IF l_maxNumParticipants >= 1 THEN 
        SELECT COUNT(*) 
        INTO l_rowcount
        FROM   (
                        SELECT oid 
                        FROM IBSDEV1.ibs_Object
                        WHERE containerId = l_partContID 
                            AND state = 2
                    ) AS temp_table3;
        -- calculate free announcement places
        SET ao_free = l_maxNumParticipants - l_rowcount;
    ELSE 
        -- unlimitied participants
        SET ao_free = 2000000000;
        RETURN 0;
    END IF;
    -- exit procedure
    RETURN 0;
END;
-- p_ParticipantCont_01$chkPart