/******************************************************************************
 * The IBS_DB_ERRORS table incl. indexes. <BR>
 * dummyscript for ORACLE/MSSQL - installscriptcompatibility - it's needed
 * for NT/ORACLE installation and maybe there will be an errorhandling
 * on MSSQL to meanwhile ??? <BR>
 *
 * @version     2.00.0000, 13.04.2000
 *
 * @author      Andreas Jansa (AJ)  000413
 ******************************************************************************
 */

/****** Object:  Login ibs_error    Script Date: 12.10.2000 20:33:48 ******/
if not exists (select * from master..syslogins where name = 'ibs_error')
BEGIN
	declare @logindb NVARCHAR(30), @loginlang NVARCHAR(30) select @logindb = N'master', @loginlang = null
	if @logindb is null or not exists (select * from master..sysdatabases where name = @logindb)
		select @logindb = N'master'
	if @loginlang is null or (not exists (select * from master..syslanguages where name = @loginlang) and @loginlang <> N'us_english')
		select @loginlang = @@language
	exec sp_addlogin N'ibs_error', N'ibs_error#+', @logindb, @loginlang
END
GO





/****** Object:  User ibs_error    Script Date: 12.10.2000 20:33:48 ******/
if not exists (select * from sysusers where name = 'ibs_error' and uid < 16382)
	EXEC sp_adduser 'ibs_error', 'ibs_error', 'public'
GO




CREATE TABLE ibs_error.ibs_db_errors2
(
    errorType       INTEGER         NOT NULL,   -- type of error
    errorNo         INTEGER,                    -- number of error
    errorDate       DATETIME        NOT NULL,   -- date when the error occurred
    errorProc       NVARCHAR (254)  NOT NULL,   -- procedure where the error
                                                -- occurred
    errorPos        NVARCHAR (255),             -- position of error within proc
    errorDesc       NTEXT                       -- description of error
)
GO
-- ibs_error.ibs_db_errors
