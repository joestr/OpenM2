/******************************************************************************
 * Delete tokens that are not in use. <BR>
 *
 * @version     $Id: U24033u_deleteTokens.sql,v 1.1 2007/09/14 11:02:00 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20070913
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete old tokens:
DELETE  ibs_Token_01
WHERE   languageId = 0
    AND classname = 'm2.diary.DiaryTokens'
    AND name IN ('DV_WEEKDAY_','DV_WEEKDAY_SHORT_')

GO

-- show count messages again:
SET NOCOUNT OFF
GO

