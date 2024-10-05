/******************************************************************************
 * All objectdescs within m2 <BR>
 *
 * @version     $Id: createObjectDesc.sql,v 1.1 2010/04/15 10:38:38 rburgermann Exp $
 *
 * @author      autogenerated by m2 MultiLang
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_ObjectDesc_01$new 0,'OD_domDates', 'appointments', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_domDatCommon', 'common', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_domDatAdvertising', 'advertising plan', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_domDatEvents', 'events', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_domDatTraining', 'training', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabDay', 'day', 'Day view of the diary.', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabDiaries', 'schedules', '', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabMonth', 'month', 'Month overview.', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabQuarter', 'quarter', 'Quarter overview of the diary.', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabWeek', 'week', 'Week overview of the diary.', 'ibs.bo.ObjectDesc'
EXEC p_ObjectDesc_01$new 0,'OD_tabParticipants', 'participants', 'Participants which are registered for the date.', 'ibs.bo.ObjectDesc'

GO
-- show count messages again:
SET NOCOUNT OFF
GO
