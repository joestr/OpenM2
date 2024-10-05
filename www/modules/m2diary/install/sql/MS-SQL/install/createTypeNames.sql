/******************************************************************************
 * All typenames within m2 <BR>
 *
 * @version     $Id: createTypeNames.sql,v 1.1 2010/04/15 10:40:54 rburgermann Exp $
 *
 * @author      autogenerated by m2 MultiLang
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

EXEC p_TypeName_01$new 0,'TN_Termin_01', 'appointment', 'm2.diary.DiaryTypeConstants'
EXEC p_TypeName_01$new 0,'TN_TerminplanContainer_01', 'schedule container', 'm2.diary.DiaryTypeConstants'
EXEC p_TypeName_01$new 0,'TN_Terminplan_01', 'schedule', 'm2.diary.DiaryTypeConstants'
EXEC p_TypeName_01$new 0,'TN_OverlapContainer_01', 'overlap container', 'm2.diary.DiaryTypeConstants'
EXEC p_TypeName_01$new 0,'TN_ParticipantContainer_01', 'participant container', 'm2.diary.DiaryTypeConstants'
EXEC p_TypeName_01$new 0,'TN_Participant_01', 'participant', 'm2.diary.DiaryTypeConstants'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
