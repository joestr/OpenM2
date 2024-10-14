/******************************************************************************
 * Task:        IBS-29 - Changing the m2Observer classnames leaded to Observer error
 *
 * Description: The m2Observer* classes have been changed to M2Observer*.
 *              This leads to problems in the ObserverService because
 *              the Class names of the m2ReminderObserverJobs are stored within the
 *              database table obs_standard.
 *              It contains "ibs.service.observer.m2ReminderObserverJob" in the className column.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24031u_changeObserverJobsClassName.sql,v 1.1 2007/08/10 09:46:14 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 20070810
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO


-- change the M2ReminderJobObsercer className
update obs_standard
set className = 'ibs.service.observer.M2ReminderObserverJob'
where className = 'ibs.service.observer.m2ReminderObserverJob'

GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
