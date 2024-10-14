/******************************************************************************
 * Task:        ELAK-43 - Update Infrastruktur
 *
 * Description:
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24032u_changePasswordEncoding.sql,v 1.1 2007/09/11 17:35:25 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20070821
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO


-- change the passwords
update ibs_user
set password = substring (password, 0, len (password) - 2)
where password like 'isEnc_%0A'

/*
-- revert the changes
update ibs_user
set password = password + '%0A'
where password like 'isEnc_%'
*/

GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
