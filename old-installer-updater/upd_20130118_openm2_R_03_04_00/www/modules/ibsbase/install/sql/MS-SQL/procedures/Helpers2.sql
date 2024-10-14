/******************************************************************************
 * Stored procedures regarding basic database functions. <BR>
 * These stored procedures are using error handling functionality.
 *
 * @version     $Id: Helpers2.sql,v 1.13 2012/11/14 08:00:49 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR)  001031
 ******************************************************************************
 */


/******************************************************************************
 * Converts a string representation of an object id to its binary 
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            The value to be converted to byte.
 *
 * @output parameters:
 * @param   ao_oid              The byte value of the oid.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_stringToByte'
GO

-- create the new procedure:
CREATE PROCEDURE p_stringToByte
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    -- output parameters:
    @ao_oid                 OBJECTID OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID        -- default value for no defined oid

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

-- body:
    -- The characters are converted to LOWER instead of UPPER.
    -- Because of this there is enough distance in the ASCII code between
    -- 0..9 and a..f to be able to use a modulo operation for converting
    -- the letters to numbers. Therefore no multiple SUBSTRING operation
    -- is necessary (as was in an earlier solution).
    SELECT @ai_oid_s = LOWER (@ai_oid_s)
    SELECT @ao_oid =
        (   CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 3, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 4, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 5, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 6, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 7, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 8, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 9, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 10, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 11, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 12, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 13, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 14, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 15, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 16, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 17, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 18, 1))) - 48) % 39)
            )
        )

    -- ensure that the return value is not null:
    IF (@ao_oid IS NULL)
        SELECT  @ao_oid = @c_NOOID
GO
-- stringToByte


/******************************************************************************
 * Converts a string representation of an object id to its binary 
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            The value to be converted to byte.
 *
 * @output parameters:
 * @return  The byte value of the oid.
 */
-- delete existing function:
--DROP FUNCTION f_stringToByte
EXEC p_dropFunc N'f_stringToByte'
GO

-- create the new function:
CREATE FUNCTION f_stringToByte
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING
)
RETURNS OBJECTID
AS
BEGIN
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @ao_oid                 OBJECTID

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

-- body:
    -- The characters are converted to LOWER instead of UPPER.
    -- Because of this there is enough distance in the ASCII code between
    -- 0..9 and a..f to be able to use a modulo operation for converting
    -- the letters to numbers. Therefore no multiple SUBSTRING operation
    -- is necessary (as was in an earlier solution).
    SELECT @ai_oid_s = LOWER (@ai_oid_s)
    SELECT @ao_oid =
        (   CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 3, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 4, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 5, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 6, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 7, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 8, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 9, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 10, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 11, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 12, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 13, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 14, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 15, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 16, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@ai_oid_s, 17, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@ai_oid_s, 18, 1))) - 48) % 39)
            )
        )

    -- ensure that the return value is not null:
    IF (@ao_oid IS NULL)
        SELECT  @ao_oid = @c_NOOID

    RETURN (@ao_oid)
END
GO
-- f_stringToByte

/******************************************************************************
 * Converts a binary representation of an object id to its string
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid              The byte value to be converted to string.
 *
 * @output parameters:
 * @return  The string value of the oid.
 */
-- delete existing function:
EXEC p_dropFunc N'f_byteToString'
GO

-- create the new function:
CREATE FUNCTION f_byteToString
(
    -- input parameters:
    @ai_oid                 OBJECTID
)
RETURNS OBJECTIDSTRING
AS
BEGIN
DECLARE
    -- constants:
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @ao_oid_s               OBJECTIDSTRING  -- the return value

    -- assign constants:
SELECT
    @c_NOOID_s              = '0x0000000000000000'

-- body:
    SELECT @ao_oid_s = 
        '0x' +
        CHAR (SUBSTRING (@ai_oid, 1, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 1, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 1, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 1, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 2, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 2, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 2, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 2, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 3, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 3, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 3, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 3, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 4, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 4, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 4, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 4, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 5, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 5, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 5, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 5, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 6, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 6, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 6, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 6, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 7, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 7, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 7, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 7, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 8, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 8, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 8, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 8, 1) % 16 / 10 * 7)

    -- ensure that the return value is not null:
    IF (@ao_oid_s IS NULL)
        SELECT  @ao_oid_s = @c_NOOID_s

    -- return the result:
    RETURN (@ao_oid_s)
END
GO
-- f_byteToString


/******************************************************************************
 * Extracts the oid out of an objectref value ("<oid>,<name>") and converts it
 * to its binary representation. <BR>
 *
 * @input parameters:
 * @param   ai_objectref    The objectref value.
 *
 * @output parameters:
 * @return  The byte value of the oid.
 */
-- delete existing function:
--DROP FUNCTION f_stringToByte
EXEC p_dropFunc N'f_getOidFromObjectRef'
GO

-- create the new function:
CREATE FUNCTION f_getOidFromObjectRef
(
    -- input parameters:
    @ai_objectref           NVARCHAR (255)
)
RETURNS OBJECTID
AS
BEGIN
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @l_oid_s                OBJECTIDSTRING,
    @ao_oid                 OBJECTID

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

-- body:
    -- The characters are converted to LOWER instead of UPPER.
    -- Because of this there is enough distance in the ASCII code between
    -- 0..9 and a..f to be able to use a modulo operation for converting
    -- the letters to numbers. Therefore no multiple SUBSTRING operation
    -- is necessary (as was in an earlier solution).
    SELECT @l_oid_s = LOWER (substring (@ai_objectref, 1, 18))
    SELECT @ao_oid =
        (   CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 3, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 4, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 5, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 6, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 7, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 8, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 9, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 10, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 11, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 12, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 13, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 14, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 15, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 16, 1))) - 48) % 39)
            )
            +
            CONVERT (binary (1),
                (((ASCII (SUBSTRING (@l_oid_s, 17, 1))) - 48) % 39) * 16 +
                (((ASCII (SUBSTRING (@l_oid_s, 18, 1))) - 48) % 39)
            )
        )

    -- ensure that the return value is not null:
    IF (@ao_oid IS NULL)
        SELECT  @ao_oid = @c_NOOID

    RETURN (@ao_oid)
END
GO
-- f_getOidFromObjectRef

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


/******************************************************************************
 * Converts a binary representation of an object id to its string
 * representation. <BR>
 *
 * @input parameters:
 * @param   ai_oid              The byte value to be converted to string.
 *
 * @output parameters:
 * @param   ao_oid_s            The string value of the oid.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_byteToString'
GO

-- create the new procedure:
CREATE PROCEDURE p_byteToString
(
    -- input parameters:
    @ai_oid                 OBJECTID,
    -- output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID_s              OBJECTIDSTRING  -- no oid as string

    -- assign constants:
SELECT
    @c_NOOID_s              = '0x0000000000000000'

-- body:
    SELECT @ao_oid_s = 
        '0x' +
        CHAR (SUBSTRING (@ai_oid, 1, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 1, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 1, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 1, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 2, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 2, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 2, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 2, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 3, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 3, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 3, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 3, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 4, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 4, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 4, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 4, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 5, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 5, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 5, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 5, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 6, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 6, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 6, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 6, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 7, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 7, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 7, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 7, 1) % 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 8, 1) / 16 + 48 + 
              SUBSTRING (@ai_oid, 8, 1) / 16 / 10 * 7) +
        CHAR (SUBSTRING (@ai_oid, 8, 1) % 16 + 48 + 
              SUBSTRING (@ai_oid, 8, 1) % 16 / 10 * 7)

    -- ensure that the return value is not null:
    IF (@ao_oid_s IS NULL)
        SELECT  @ao_oid_s = @c_NOOID_s
GO
-- byteToString


/******************************************************************************
 * Compute the length of a string. <BR>
 *
 * @input parameters:
 * @param   ai_string           String whose length shall be computed.
 *
 * @output parameters:
 * @return  The length of the String or 0 if the String is NULL.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_stringLength'
GO

-- create the new procedure:
CREATE PROCEDURE p_stringLength
( 
    -- input parameters: 
    @ai_string              NVARCHAR (4000)
    -- input output parameters:
) 
AS
DECLARE
    -- local variables: 
    @l_retValue             INTEGER         -- return value of function

    -- initialize local variables:
SELECT
    @l_retValue = 0

-- body:
    -- compute the length:
    SELECT  @l_retValue = LEN (@ai_string)

    -- return the result:
    RETURN  @l_retValue
GO
-- p_stringLength


/******************************************************************************
 * Add a string to a long string. <BR>
 * The long string is represented through a set of string variables. These
 * variables must be concatenated to get the whole string.
 * The procedure first searches for the currently last filled position of the
 * string and then concatenates the separator and the new string a this
 * position.
 * If the long string is empty the separator is not used.
 *
 * @input parameters:
 * @param   ai_string           String which shall be concatenated.
 * @param   ai_sep              Separator string to be used between the previous
 *                              and the current value.
 * @param   aio_length          The length of the full string.
 * @param   aio_str             Content of the string.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_addString'
GO

-- create the new procedure:
CREATE PROCEDURE p_addString
( 
    -- input parameters: 
    @ai_string              NVARCHAR (4000),
    @ai_sep                 VARCHAR (10),
    -- input output parameters:
    @aio_length             INTEGER OUTPUT,
    @aio_str                NVARCHAR (4000) OUTPUT
) 
AS
DECLARE
    -- constants: 
    @c_maxLength            INT,            -- max. length
 
    -- local variables: 
    @l_length               INTEGER,        -- length of the string to be
                                            -- concatenated
    @l_concatenationString  NVARCHAR (4000) -- full string for concatenation


    -- assign constants:
SELECT
    @c_maxLength            = 4000

    -- initialize local variables:
SELECT
    @l_concatenationString = @ai_sep + @ai_string

-- body:
    IF (@aio_length = 0)                -- string is empty?
    BEGIN
        -- get the length of the string to be concatenated:
        SELECT  @l_length = LEN (@ai_string)

        -- set the new first part of the string:
        SELECT  @aio_str = @ai_string
    END -- if string is empty
    ELSE                                -- string not empty
    BEGIN
        -- get the length of the string to be concatenated:
        SELECT  @l_length = LEN (@l_concatenationString)

        IF (@aio_length <= @c_maxLength) -- string not full?
        BEGIN
            SELECT  @aio_str = @aio_str +
                        SUBSTRING (@l_concatenationString, 1,
                                   @c_maxLength - @aio_length)
        END -- if string not full
        ELSE                            -- the string is already full
        BEGIN
            -- nothing to concatenate:
            SELECT  @l_length = 0
        END -- else the string is already full
    END -- else string not empty

    -- compute the new length of the whole string:
    SELECT  @aio_length = @aio_length + @l_length
GO
-- p_addString


/******************************************************************************
 * Display a long string. <BR>
 * The long string is represented through a set of string variables. These
 * variables must be concatenated to get the whole string.
 *
 * @input parameters:
 * @param   ai_title            Title to be displayed before the string.
 * @param   ai_length           The length of the full string.
 * @param   ai_string           Content of the string.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_displayString'
GO

-- create the new procedure:
CREATE PROCEDURE p_displayString
( 
    -- input parameters: 
    @ai_title               NVARCHAR (255),
    @ai_length              INTEGER,
    @ai_string              NVARCHAR (4000)
) 
AS
-- body:
    -- display the title:
    PRINT   @ai_title

    -- display the string if it is not empty:
    IF (@ai_length > 0)
        PRINT   @ai_string
GO
-- p_displayString


/******************************************************************************
 * Change scheme of a table. <BR>
 * This procedure compares the original table and a table containing the new
 * scheme. It then copies each attribute value of the original table to the
 * new table which also exists in the new table. Attributes which do not exist
 * in the new table are omitted. <BR>
 * For each attribute which exists just in the new table and not in the old one
 * there must be a default value within the set of parameters. If such an
 * attribute is not mentioned the whole process is cancelled. Each attribute
 * value which represents a string must also contain quotes. Otherwise a syntax
 * error will be raised.
 * E.g.: ai_attrName1 = 'name', ai_attrValue1 = '''myName'''
 *       ai_attrName2 = 'id', ai_attrValue2 = '78'
 * For SQL Server the ai_attrNameX and ai_attrValueX parameters are optional.
 * <BR>
 * After the copy process is finished the old table is deleted and the new
 * table is renamed to the name of the original table. <BR>
 * The procedure contains a TRANSACTION block, so it is not allowed to call it
 * from within another TRANSACTION block. <BR>
 * ATTENTION: Ensure that each trigger and procedure working on this table
 * are newly created.
 *
 * @input parameters:
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_tableName        Name of the table to be changed.
 * @param   ai_tempTableName    The temporary name of the table containing the
 *                              new structure.
 * @param   ai_attrName1        Name of attribute to be added.
 * @param   ai_attrValue1       Default value of attribute to be added.
 * @param   ai_attrName2        Name of attribute to be added.
 * @param   ai_attrValue2       Default value of attribute to be added.
 * @param   ai_attrName3        Name of attribute to be added.
 * @param   ai_attrValue3       Default value of attribute to be added.
 * @param   ai_attrName4        Name of attribute to be added.
 * @param   ai_attrValue4       Default value of attribute to be added.
 * @param   ai_attrName5        Name of attribute to be added.
 * @param   ai_attrValue5       Default value of attribute to be added.
 * @param   ai_attrName6        Name of attribute to be added.
 * @param   ai_attrValue6       Default value of attribute to be added.
 * @param   ai_attrName7        Name of attribute to be added.
 * @param   ai_attrValue7       Default value of attribute to be added.
 * @param   ai_attrName8        Name of attribute to be added.
 * @param   ai_attrValue8       Default value of attribute to be added.
 * @param   ai_attrName9        Name of attribute to be added.
 * @param   ai_attrValue9       Default value of attribute to be added.
 * @param   ai_attrName10       Name of attribute to be added.
 * @param   ai_attrValue10      Default value of attribute to be added.
 *
 * @output parameters:
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeTable
( 
    -- input parameters: 
    @ai_context             NVARCHAR (255),
    @ai_tableName           NVARCHAR (30),
    @ai_tempTableName       NVARCHAR (30),
    @ai_attrName1           NVARCHAR (30) = N'',
    @ai_attrValue1          NVARCHAR (255) = N'',
    @ai_attrName2           NVARCHAR (30) = N'',
    @ai_attrValue2          NVARCHAR (255) = N'',
    @ai_attrName3           NVARCHAR (30) = N'',
    @ai_attrValue3          NVARCHAR (255) = N'',
    @ai_attrName4           NVARCHAR (30) = N'',
    @ai_attrValue4          NVARCHAR (255) = N'',
    @ai_attrName5           NVARCHAR (30) = N'',
    @ai_attrValue5          NVARCHAR (255) = N'',
    @ai_attrName6           NVARCHAR (30) = N'',
    @ai_attrValue6          NVARCHAR (255) = N'',
    @ai_attrName7           NVARCHAR (30) = N'',
    @ai_attrValue7          NVARCHAR (255) = N'',
    @ai_attrName8           NVARCHAR (30) = N'',
    @ai_attrValue8          NVARCHAR (255) = N'',
    @ai_attrName9           NVARCHAR (30) = N'',
    @ai_attrValue9          NVARCHAR (255) = N'',
    @ai_attrName10          NVARCHAR (30) = N'',
    @ai_attrValue10         NVARCHAR (255) = N''
    -- output parameters:
) 
AS
DECLARE
    -- constants: 
 
    -- local variables: 
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (255),  -- error position description
    @l_eText                NVARCHAR (255),  -- full error text
    @l_msg                  NVARCHAR (255),  -- the actual message
    @l_cmdString            NVARCHAR (255),  -- command line to be executed
    @l_cursorId             INTEGER,         -- id of cmd cursor
    @l_rowsProcessed        INTEGER,         -- number of rows of last cmd exec.
    @l_lastErrorPos         INTEGER,         -- last error in cmd line execution
    @l_oldTableName         NVARCHAR (30),   -- name of table with old scheme
    @l_newTableName         NVARCHAR (30),   -- name of table with new scheme
    -- the comma-separated attributes of the old table structure which
    -- shall be assigned to the new attributes:
    @l_countOldAttributes   INT,             -- count old attributes
    @l_oldAttributes        NVARCHAR (4000), -- names of old attributes
    -- the comma-separated attributes of the new table structure:
    @l_countNewAttributes   INT,     -- count new attributes
    @l_newAttributes        NVARCHAR (4000), -- names of new attributes
    -- the attributes which where added:
    @l_countAddedAttributes INT,             -- count added attributes
    @l_addedAttributes      NVARCHAR (4000), -- names of added attributes
    -- the attributes which where deleted:
    @l_countDeletedAttributes INT,           -- count deleted attributes
    @l_deletedAttributes    NVARCHAR (4000), -- names of deleted attributes
    -- the attributes which where not found in the parameters:
    @l_countNotFoundAttributes INTEGER,      -- count not found attributes
    @l_notFoundAttributes   NVARCHAR (4000), -- names of not found attributes
    @l_sep                  NVARCHAR (2),    -- the actual attribute separator
    @l_attrNameNew          NVARCHAR (30),   -- the actual attribute name in the
                                             -- new scheme
    @l_attrNameOld          NVARCHAR (30),   -- the actual attribute name in the
                                             -- old scheme
    @l_attrValue            NVARCHAR (255),  -- value of actual attribute
    -- exceptions:
    @e_attributeNotFound    INTEGER   -- an attribute was not found


    -- assign constants:

    -- initialize local variables:
SELECT
    @l_newTableName = @ai_tempTableName,
    @l_oldTableName = @ai_tableName,
    @l_countOldAttributes = 0,
    @l_countNewAttributes = 0,
    @l_countAddedAttributes = 0,
    @l_countDeletedAttributes = 0,
    @l_countNotFoundAttributes = 0,
    @l_sep = N'',
    @e_attributeNotFound = 100101  -- must be > 50000

-- body:
    -- check if the target table and the temporary table are the same:
    IF (@l_newTableName <> @l_oldTableName) -- the tables are different?
    BEGIN
        BEGIN TRANSACTION -- begin new TRANSACTION

/**
 * This is not necessary because the table is already created.
 * But maybe we can use this in the future for some purpose.
 *
            -- create the new table:
            EXEC (
                'CREATE TABLE ' + @l_tempTableName +
                ' (' + @ai_tableStructure + ')'
                )

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                'Error in CREATE TABLE', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
*/

            -- check if the target table exists:
            IF EXISTS ( SELECT  * 
                        FROM    sysobjects 
                        WHERE   id = object_id (@l_oldTableName) 
                            AND sysstat & 0xf = 3)
            BEGIN                       -- target table already exists?
                -- define cursor:
                -- get all attribute names of the new table scheme and the
                -- corresponding attribute names of the old scheme:
                DECLARE tableCursor CURSOR FOR 
                    SELECT  UPPER (cNew.name) as newName,
                            UPPER (cOld.name) AS oldName
                    FROM    syscolumns cNew, sysobjects oNew,
                            syscolumns cOld, sysobjects oOld
                    WHERE   oNew.id = object_id (@l_newTableName)
                        AND oNew.sysstat & 0xf = 3
                        AND oNew.id = cNew.id
                        AND oOld.id = object_id (@l_oldTableName)
                        AND oOld.sysstat & 0xf = 3
                        AND oOld.id = cOld.id
                        AND cNew.name = cOld.name
                    UNION
                    SELECT  UPPER (c.name) AS newName,
                            CONVERT (NVARCHAR, null) AS oldName
                    FROM    syscolumns c, sysobjects o
                    WHERE   o.id = object_id (@l_newTableName)
                        AND o.sysstat & 0xf = 3
                        AND o.id = c.id
                        AND UPPER (c.name) NOT IN
                            (
                                SELECT  UPPER (c.name)
                                FROM    syscolumns c, sysobjects o
                                WHERE   o.id = object_id (@l_oldTableName)
                                    AND o.sysstat & 0xf = 3
                                    AND o.id = c.id
                            )
                    UNION
                    SELECT  CONVERT (NVARCHAR, null) AS newName,
                            UPPER (c.name) AS oldName
                    FROM    syscolumns c, sysobjects o
                    WHERE   o.id = object_id (@l_oldTableName)
                        AND o.sysstat & 0xf = 3
                        AND o.id = c.id
                        AND UPPER (c.name) NOT IN
                            (
                                SELECT  UPPER (c.name)
                                FROM    syscolumns c, sysobjects o
                                WHERE   o.id = object_id (@l_newTableName)
                                    AND o.sysstat & 0xf = 3
                                    AND o.id = c.id
                            )

                -- open the cursor:
                OPEN    tableCursor

                -- get the first object:
                FETCH NEXT FROM tableCursor INTO @l_attrNameNew, @l_attrNameOld

                -- loop through all objects:
                WHILE (@@FETCH_STATUS <> -1)        -- another object found?
                BEGIN
                    -- Because @@FETCH_STATUS may have one of the three values
                    -- -2, -1, or 0 all of these cases must be checked.
                    -- In this case the tuple is skipped if it was deleted
                    -- during the execution of this procedure.
                    IF (@@FETCH_STATUS <> -2)
                    BEGIN
/* for debugging purposes:
SELECT  @l_msg = 'attribute: ' + @l_attrNameNew + '/' + @l_attrNameOld
PRINT   @l_msg
*/
                        -- check if the attribute exists in the new scheme:
                        IF (@l_attrNameNew IS NOT NULL)
                                        -- the attribute exists in new scheme?
                        BEGIN
                            -- add the attribute to the new attribute string:
                            EXEC p_addString @l_attrNameNew, @l_sep,
                                @l_countNewAttributes OUTPUT,
                                @l_newAttributes OUTPUT

                            -- check if the new attribute was also in the old
                            -- scheme:
                            IF (@l_attrNameOld IS NOT NULL)
                                        -- the attribute existed in old scheme?
                            BEGIN
                                -- add the attribute to the old attribute
                                -- string:
                                EXEC p_addString @l_attrNameOld, @l_sep,
                                    @l_countOldAttributes OUTPUT,
                                    @l_oldAttributes OUTPUT
                            END -- if the attribute existed in old scheme
                            ELSE        -- attribute was not in old scheme
                            BEGIN
                                -- check if the attribute was in the attribute
                                -- list:
                                IF (@l_attrNameNew = UPPER (@ai_attrName1))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue1
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName2))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue2
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName3))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue3
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName4))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue4
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName5))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue5
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName6))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue6
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName7))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue7
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName8))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue8
                                ELSE IF (@l_attrNameNew = UPPER (@ai_attrName9))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue9
                                ELSE IF (@l_attrNameNew =
                                            UPPER (@ai_attrName10))
                                        -- attribute found?
                                    -- get the attribute value:
                                    SELECT  @l_attrValue = @ai_attrValue10
                                ELSE            -- attribute was not found
                                BEGIN
                                    -- add the attribute to the not found
                                    -- attribute string:
                                    EXEC p_addString @l_attrNameNew, @l_sep,
                                        @l_countNotFoundAttributes OUTPUT,
                                        @l_notFoundAttributes OUTPUT
                                END -- else attribute was not found

                                -- add the attribute value to the old attribute
                                -- string:
                                EXEC p_addString @l_attrValue, @l_sep,
                                    @l_countOldAttributes OUTPUT,
                                    @l_oldAttributes OUTPUT
                                -- add the attribute to the added attribute
                                -- string:
                                EXEC p_addString @l_attrNameNew, @l_sep,
                                    @l_countAddedAttributes OUTPUT,
                                    @l_addedAttributes OUTPUT
                            END -- else attribute was not in old scheme

                            -- set the next separator:
                            SELECT  @l_sep = N','
                        END -- if the attribute exists in new scheme
                        ELSE            -- attribute is not in new scheme
                        BEGIN
                            -- add the attribute to the deleted attribute
                            -- string:
                            EXEC p_addString @l_attrNameOld, @l_sep,
                                @l_countDeletedAttributes OUTPUT,
                                @l_deletedAttributes OUTPUT
                        END -- else attribute is not in new scheme
                    END -- if
                    -- get next tuple:
                    FETCH NEXT FROM tableCursor
                        INTO @l_attrNameNew, @l_attrNameOld
                END -- while another tuple found

                -- close the not longer needed cursor:
                CLOSE tableCursor
                DEALLOCATE tableCursor

                -- check if all necessary parameters are there:
                IF (@l_countNotFoundAttributes > 0)
                                        -- not all parameters are there
                BEGIN
                    -- create error entry:
                    SELECT  @l_error = @e_attributeNotFound,
                            @l_ePos = N'Error in find attribute ' +
                                N' - no value for attributes: ' +
                                @l_notFoundAttributes
                    GOTO exception      -- call common exception handler
                END -- if not all parameters are there

                -- copy the old attribute values to the new table:
                EXEC (
                    'INSERT INTO ' + @l_newTableName +
                    ' (' + @l_newAttributes + ')' +
                    ' SELECT ' + @l_oldAttributes +
                    ' FROM ' + @l_oldTableName
                    )

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'Error in INSERT', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler


                -- drop old table:
                EXEC p_dropTable @l_oldTableName
            END -- if target table already exists

            -- rename new table:
            EXEC (
                'EXEC sp_rename ' + @l_newTableName + ', ' + @l_oldTableName
                )

            -- ensure that the temporary table is dropped:
            EXEC p_dropTable @ai_tempTableName

        -- make changes permanent:
        COMMIT TRANSACTION

        -- print state report:
        SELECT  @l_msg = @ai_context + N': table ' + @ai_tableName +
                N' changed to new scheme.'
        PRINT   @l_msg

        EXEC p_displayString N' - added attributes: ',
                @l_countAddedAttributes, @l_addedAttributes

        EXEC p_displayString N' - deleted attributes: ',
                @l_countDeletedAttributes, @l_deletedAttributes

/* for debugging purposes:
        EXEC p_displayString ' - new attributes: ',
                @l_countNewAttributes, @l_newAttributes

        EXEC p_displayString ' - old attributes: ',
                @l_countOldAttributes, @l_oldAttributes
*/
    END -- if the tables are different
    ELSE                                -- the tables are the same
    BEGIN
        SELECT  @l_msg = @ai_context +
                N': The tables are the same -> nothing to be done.'
        PRINT   @l_msg
    END -- else the tables are the same

    -- finish the procedure:
    RETURN

exception:
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes

    -- check kind of exception:
    IF (@l_error = @e_attributeNotFound)
        -- create error entry:
        SELECT  @l_eText = @ai_context + N': Error when changing table ' +
            @ai_tableName + N': ' + @l_ePos +
            N'; errorcode = 0 ' +
            N', errormessage = Attribute not found'
    ELSE
        -- create error entry:
        SELECT  @l_eText = @ai_context + N': Error when changing table ' +
            @ai_tableName + N': ' + @l_ePos +
            N'; errorcode = ' + CONVERT (NVARCHAR, @l_error)

    -- log the error:
    EXEC ibs_error.logError 500, N'p_changeTable',
            @l_error, @l_eText
    PRINT   @l_eText
GO
-- p_changeTable


/******************************************************************************
 * Change name of a table. <BR>
 *
 * @param   ai_context          Context name or description from within this
 *                              procedure was called. This string is used as
 *                              output and logging prefix.
 * @param   ai_oldTableName     The original name of the table.
 * @param   ai_newTableName     The new name of the table.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_renameTable'
GO

-- create the new procedure:
CREATE PROCEDURE p_renameTable
( 
    -- input parameters: 
    @ai_context             NVARCHAR (255),
    @ai_oldTableName        NVARCHAR (30),
    @ai_newTableName        NVARCHAR (30)
    -- output parameters:
) 
AS
DECLARE
    -- constants: 
 
    -- local variables: 
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (255),  -- error position description
    @l_eText                NVARCHAR (255),  -- full error text
    @l_msg                  NVARCHAR (255)   -- the actual message

    -- assign constants:

    -- initialize local variables:

-- body:
    -- check if the old and the new table name are the same:
    IF (@ai_newTableName <> @ai_oldTableName) -- the tables are different?
    BEGIN
        BEGIN TRANSACTION -- begin new TRANSACTION

            -- rename the table:
            EXEC @l_error = sp_rename @ai_oldTableName, @ai_newTableName

            SELECT @l_ePos = N'Error in RENAME'
            IF (@l_error <> 0)      -- an error occurred?
                GOTO exception      -- call common exception handler

        -- make changes permanent:
        COMMIT TRANSACTION

        -- print state report:
        SELECT  @l_msg = @ai_context + N': table ''' + @ai_oldTableName +
                N''' renamed to ''' + @ai_newTableName + N'''.'
        PRINT   @l_msg
    END -- if the tables are different
    ELSE                                -- the tables are the same
    BEGIN
        SELECT  @l_msg = @ai_context +
                N': The table names are the same -> nothing to be done.'
        PRINT   @l_msg
    END -- else the tables are the same

    -- finish the procedure:
    RETURN

exception:
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes

    -- create error entry:
    SELECT  @l_eText = @ai_context + N': Error when renaming table ''' +
        @ai_oldTableName + N''' to ''' + @ai_newTableName + N''': ' + @l_ePos +
        N'; errorcode = ' + CONVERT (NVARCHAR, @l_error)

    -- log the error:
    EXEC ibs_error.logError 500, N'p_renameTable',
            @l_error, @l_eText
    PRINT   @l_eText
GO
-- p_renameTable


/******************************************************************************
 * Retrieve the OID of the original from a copied object. <BR>
 * When copying an object, this function can be used to retrieve the OID of the
 * original object when the OID of the copy, the root OID of the original objects
 * (i.e. the OID of the object where the copy operation started recursively)
 * and the root OID of the copied objects are known.
 *
 * @param   @copyOID_s          The OID of the copied object.
 * @param   ai_oldTableName     The OID of the original root object.
 * @param   ai_newTableName     The OID of the copied root object.
 * @return						The OID of the original object.
 */
 -- delete existing function:
EXEC p_dropFunc N'f_retrieveOriginalOidFromCopy'
GO

CREATE FUNCTION f_retrieveOriginalOidFromCopy
(
  @copyOID_s      OBJECTIDSTRING,
  @origRootOID_s  OBJECTIDSTRING,
  @copyRootOID_s  OBJECTIDSTRING
)
RETURNS OBJECTID
AS
BEGIN
DECLARE
-- the OID of the original object and return value
@p_origOid        OBJECTID,
-- the posNoPath of the original object
@p_origPath       POSNOPATH_VC,
-- the posNoPath of the original root object (the object the copy action starts with)
@p_origRootPath   POSNOPATH_VC,
-- the posNoPath of the copied object
@p_copyPath       POSNOPATH_VC,
-- the posNoPath of the copied root object
@p_copyRootPath   POSNOPATH_VC

SET @p_origOid = 0x0000000000000000
SET @p_origPath = ''
SET @p_origRootPath = ''
SET @p_copyPath = ''
SET @p_copyRootPath = ''

-- get the posNoPath of the original root object
SELECT @p_origRootPath = posNoPath
FROM ibs_Object
WHERE oid = @origRootOID_s
AND state = 2

-- get the posNoPath of the current copied object
SELECT @p_copyPath = posNoPath
FROM ibs_Object
WHERE oid = @copyOID_s

-- get the posNoPath of the copied root object
SELECT @p_copyRootPath = posNoPath
FROM ibs_Object
WHERE oid = @copyRootOID_s
AND state = 2

-- get the posNoPath of the current copied object
SELECT @p_copyPath = posNoPath
FROM ibs_Object
WHERE oid = @copyOID_s
  
-- get the OID of the current original object
SELECT @p_origOid = oid
FROM ibs_Object
WHERE posNoPath LIKE @p_origRootPath + 
	SUBSTRING (@p_copyPath, LEN (@p_copyRootPath) + 1, LEN (@p_copyPath))

-- return the searched OID
RETURN @p_origOid
END
GO
