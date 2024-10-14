/******************************************************************************
 * Stored procedures regarding basic database functions. <BR>
 * These stored procedures are using error handling functionality.
 *
 * @version     $Id: U331002p_Helpers2.sql,v 1.1 2013/01/21 08:14:04 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  001031
 ******************************************************************************
 */

 PRINT 'Starting U331002p_Helpers2.sql'
 GO

/******************************************************************************
 * Converts a String with Value Domain Object OIDs to a String with the Value
 * Domain Object elements values.
 *
 * @input parameters:
 * @param   ai_refStr    The comma separated String with the value domain elements oids.
 *
 * @output parameters:
 * @return  The comma separated String with the values.
 */
-- delete existing function:
--DROP FUNCTION f_stringToByte
EXEC p_dropFunc N'f_getValuesFromOids'
GO

-- create the new function:
CREATE FUNCTION f_getValuesFromOids
(
    -- input parameters:
    @ai_refStr NVARCHAR (4000)
)
RETURNS NVARCHAR (4000)
AS
BEGIN
DECLARE
@l_count INT,
@l_startIndex INT,
@l_refOid_s OBJECTIDSTRING,
@l_refOid OBJECTID,
@ao_valueStr NVARCHAR (4000),
@l_value NVARCHAR (4000)

-- If the String starts with the separator set the start index to 2
IF (substring (@ai_refStr,1,1) = N'|')
BEGIN
SELECT @l_startIndex = 2
END
-- Otherwise to 1
ELSE
BEGIN
SELECT @l_startIndex = 1
END

SELECT @l_count = (len (@ai_refStr) + 1) / 19 - 1
SELECT @ao_valueStr = N''
WHILE (@l_count > -1)
BEGIN
SELECT @l_refOid_s = substring (@ai_refStr, @l_count * 19 + @l_startIndex, 18)
SELECT @l_refOid = dbo.f_stringToByte (@l_refOid_s)
SELECT @l_value = value FROM v_getValueDomain WHERE oid = @l_refOid
SELECT @l_count = @l_count - 1
SELECT @ao_valueStr = N', ' + @l_value + @ao_valueStr
END
SELECT @ao_valueStr = substring (@ao_valueStr, 3, len (@ao_valueStr))
-- return the result:
RETURN (@ao_valueStr)
END
GO
-- f_getValuesFromOids

PRINT 'U331002p_Helpers2.sql finished.'
GO