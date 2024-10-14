if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.accessStat') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.accessStat
GO
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.dayAccesses') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.dayAccesses
GO
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.lastDayAccesses') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.lastDayAccesses
GO
if exists (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.lastHourAccesses') and sysstat & 0xf = 4)
	drop procedure #CONFVAR.ibsbase.dbOwner#.lastHourAccesses
GO



CREATE PROCEDURE accessStat
AS
print ''
print ''
print 'Benutzer mit 0 Zugriffen auf das System'
print ''

SELECT CONVERT (VARCHAR (20), u.fullname) AS Benutzer
FROM ibs_User u
WHERE u.id NOT IN (SELECT userid FROM ibs_ObjectRead)
order by u.fullname

print ''
print ''
print  'Anzahl Zugriffe eines Benutzers sortiert nach Benutzername'
print ''

SELECT CONVERT (VARCHAR (20), u.fullname) AS Benutzer, count(r.userid) as Zugriffe
FROM ibs_ObjectRead r, ibs_User u
WHERE r.userid = u.id
group by u.fullname
order by u.fullname

print ''
print ''
print 'Anzahl Zugriffe eines Benutzers sortiert nach Anzahl Zugriffe'
print ''

SELECT CONVERT (VARCHAR (20), u.fullname) AS Benutzer, count(r.userid) as Zugriffe
FROM ibs_ObjectRead r, ibs_User u
WHERE r.userid = u.id
group by u.fullname
order by Zugriffe DESC
GO


CREATE PROCEDURE dayAccesses
(
    -- input parameters:
    @date               DATETIME
)
AS
SELECT CONVERT (VARCHAR (20), o.name) AS domain, CONVERT (VARCHAR (20), u.name) AS username, r.*
FROM ibs_ObjectRead r, ibs_User u, ibs_Domain_01 d, ibs_Object o
WHERE r.lastRead >= @date
AND r.lastRead <= DATEADD (day, 1, @date)
AND r.userid = u.id
AND d.id = u.domainId
AND d.oid = o.oid
ORDER BY r.lastRead
GO


CREATE PROCEDURE lastDayAccesses
AS
PRINT 'Zugriffe des letzten Tages, der letzte zuerst:'

SELECT  CONVERT (VARCHAR (15), od.name) AS Domäne, CONVERT (VARCHAR (20), o.name) AS Objekt, CONVERT (VARCHAR (15), u.name) AS Benutzer, r.lastRead AS Datum
FROM    ibs_ObjectRead r, ibs_User u, ibs_Domain_01 d, ibs_Object od, ibs_Object o
WHERE   r.lastRead >= DATEADD (day, -1, getDate ())
    AND r.userid = u.id
    AND d.id = u.domainId
    AND d.oid = od.oid
    AND r.oid = o.oid
ORDER BY r.lastRead DESC
GO


CREATE PROCEDURE lastHourAccesses
AS
PRINT 'Zugriffe der letzten Stunde, der letzte zuerst:'

SELECT  CONVERT (VARCHAR (15), od.name) AS Domäne, CONVERT (VARCHAR (20), o.name) AS Objekt, CONVERT (VARCHAR (15), u.name) AS Benutzer, r.lastRead AS Datum

FROM    ibs_ObjectRead r, ibs_User u, ibs_Domain_01 d, ibs_Object od, ibs_Object o
WHERE   r.lastRead >= DATEADD (minute, -60, getDate ())
    AND r.userid = u.id
    AND d.id = u.domainId
    AND d.oid = od.oid
    AND r.oid = o.oid
ORDER BY r.lastRead DESC
GO

