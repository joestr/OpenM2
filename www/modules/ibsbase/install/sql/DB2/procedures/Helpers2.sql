-------------------------------------------------------------------------------
-- Stored procedures regarding basic database functions. <BR>
-- These stored procedures are using error handling functionality.
--
-- @version     $Revision: 1.5 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020807
-------------------------------------------------------------------------------


-------------------------------------------------------------------------------
-- Converts a string representation of an object id to its binary 
-- representation. <BR>
--
-- @input parameters:
-- @param   ai_oid_s            The value to be converted to byte.
--
-- @output parameters:
-- @param   ao_oid              The byte value of the oid.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_stringToByte');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_stringToByte
 (
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),--OBJECTIDSTRING
   -- output parameters:
    OUT ao_oid              CHAR (8) FOR BIT DATA--OBJECTID
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_val           CHAR (2);       -- actual string to convert
    DECLARE l_pos           INTEGER DEFAULT 1; -- actual position
                                            -- start with the lower most byte
    DECLARE l_retVal        VARCHAR (8) FOR BIT DATA DEFAULT '';
                                            -- actual converting byte
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
--CALL IBSDEV1.logError (100, 'p_stringToByte', l_sqlcode, 'start', '', 0, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- loop through all characters of the input string:
    WHILE (l_pos <= 8)                  -- another byte exists?
    DO
        -- get the actual digit:
        SET l_val = UPPER (SUBSTR (ai_oid_s, l_pos * 2 + 1, 2));
--CALL IBSDEV1.logError (100, 'p_stringToByte', l_sqlcode, 'start', '', 0, 'l_val', l_val, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

        -- convert the digit:
        CASE l_val
            WHEN '00' THEN
                SET l_retVal = l_retVal || X'00';
            WHEN '01' THEN
                SET l_retVal = l_retVal || X'01';
            WHEN '02' THEN
                SET l_retVal = l_retVal || X'02';
            WHEN '03' THEN
                SET l_retVal = l_retVal || X'03';
            WHEN '04' THEN
                SET l_retVal = l_retVal || X'04';
            WHEN '05' THEN
                SET l_retVal = l_retVal || X'05';
            WHEN '06' THEN
                SET l_retVal = l_retVal || X'06';
            WHEN '07' THEN
                SET l_retVal = l_retVal || X'07';
            WHEN '08' THEN
                SET l_retVal = l_retVal || X'08';
            WHEN '09' THEN
                SET l_retVal = l_retVal || X'09';
            WHEN '0A' THEN
                SET l_retVal = l_retVal || X'0A';
            WHEN '0B' THEN
                SET l_retVal = l_retVal || X'0B';
            WHEN '0C' THEN
                SET l_retVal = l_retVal || X'0C';
            WHEN '0D' THEN
                SET l_retVal = l_retVal || X'0D';
            WHEN '0E' THEN
                SET l_retVal = l_retVal || X'0E';
            WHEN '0F' THEN
                SET l_retVal = l_retVal || X'0F';
            WHEN '10' THEN
                SET l_retVal = l_retVal || X'10';
            WHEN '11' THEN
                SET l_retVal = l_retVal || X'11';
            WHEN '12' THEN
                SET l_retVal = l_retVal || X'12';
            WHEN '13' THEN
                SET l_retVal = l_retVal || X'13';
            WHEN '14' THEN
                SET l_retVal = l_retVal || X'14';
            WHEN '15' THEN
                SET l_retVal = l_retVal || X'15';
            WHEN '16' THEN
                SET l_retVal = l_retVal || X'16';
            WHEN '17' THEN
                SET l_retVal = l_retVal || X'17';
            WHEN '18' THEN
                SET l_retVal = l_retVal || X'18';
            WHEN '19' THEN
                SET l_retVal = l_retVal || X'19';
            WHEN '1A' THEN
                SET l_retVal = l_retVal || X'1A';
            WHEN '1B' THEN
                SET l_retVal = l_retVal || X'1B';
            WHEN '1C' THEN
                SET l_retVal = l_retVal || X'1C';
            WHEN '1D' THEN
                SET l_retVal = l_retVal || X'1D';
            WHEN '1E' THEN
                SET l_retVal = l_retVal || X'1E';
            WHEN '1F' THEN
                SET l_retVal = l_retVal || X'1F';
            WHEN '20' THEN
                SET l_retVal = l_retVal || X'20';
            WHEN '21' THEN
                SET l_retVal = l_retVal || X'21';
            WHEN '22' THEN
                SET l_retVal = l_retVal || X'22';
            WHEN '23' THEN
                SET l_retVal = l_retVal || X'23';
            WHEN '24' THEN
                SET l_retVal = l_retVal || X'24';
            WHEN '25' THEN
                SET l_retVal = l_retVal || X'25';
            WHEN '26' THEN
                SET l_retVal = l_retVal || X'26';
            WHEN '27' THEN
                SET l_retVal = l_retVal || X'27';
            WHEN '28' THEN
                SET l_retVal = l_retVal || X'28';
            WHEN '29' THEN
                SET l_retVal = l_retVal || X'29';
            WHEN '2A' THEN
                SET l_retVal = l_retVal || X'2A';
            WHEN '2B' THEN
                SET l_retVal = l_retVal || X'2B';
            WHEN '2C' THEN
                SET l_retVal = l_retVal || X'2C';
            WHEN '2D' THEN
                SET l_retVal = l_retVal || X'2D';
            WHEN '2E' THEN
                SET l_retVal = l_retVal || X'2E';
            WHEN '2F' THEN
                SET l_retVal = l_retVal || X'2F';
            WHEN '30' THEN
                SET l_retVal = l_retVal || X'30';
            WHEN '31' THEN
                SET l_retVal = l_retVal || X'31';
            WHEN '32' THEN
                SET l_retVal = l_retVal || X'32';
            WHEN '33' THEN
                SET l_retVal = l_retVal || X'33';
            WHEN '34' THEN
                SET l_retVal = l_retVal || X'34';
            WHEN '35' THEN
                SET l_retVal = l_retVal || X'35';
            WHEN '36' THEN
                SET l_retVal = l_retVal || X'36';
            WHEN '37' THEN
                SET l_retVal = l_retVal || X'37';
            WHEN '38' THEN
                SET l_retVal = l_retVal || X'38';
            WHEN '39' THEN
                SET l_retVal = l_retVal || X'39';
            WHEN '3A' THEN
                SET l_retVal = l_retVal || X'3A';
            WHEN '3B' THEN
                SET l_retVal = l_retVal || X'3B';
            WHEN '3C' THEN
                SET l_retVal = l_retVal || X'3C';
            WHEN '3D' THEN
                SET l_retVal = l_retVal || X'3D';
            WHEN '3E' THEN
                SET l_retVal = l_retVal || X'3E';
            WHEN '3F' THEN
                SET l_retVal = l_retVal || X'3F';
            WHEN '40' THEN
                SET l_retVal = l_retVal || X'40';
            WHEN '41' THEN
                SET l_retVal = l_retVal || X'41';
            WHEN '42' THEN
                SET l_retVal = l_retVal || X'42';
            WHEN '43' THEN
                SET l_retVal = l_retVal || X'43';
            WHEN '44' THEN
                SET l_retVal = l_retVal || X'44';
            WHEN '45' THEN
                SET l_retVal = l_retVal || X'45';
            WHEN '46' THEN
                SET l_retVal = l_retVal || X'46';
            WHEN '47' THEN
                SET l_retVal = l_retVal || X'47';
            WHEN '48' THEN
                SET l_retVal = l_retVal || X'48';
            WHEN '49' THEN
                SET l_retVal = l_retVal || X'49';
            WHEN '4A' THEN
                SET l_retVal = l_retVal || X'4A';
            WHEN '4B' THEN
                SET l_retVal = l_retVal || X'4B';
            WHEN '4C' THEN
                SET l_retVal = l_retVal || X'4C';
            WHEN '4D' THEN
                SET l_retVal = l_retVal || X'4D';
            WHEN '4E' THEN
                SET l_retVal = l_retVal || X'4E';
            WHEN '4F' THEN
                SET l_retVal = l_retVal || X'4F';
            WHEN '50' THEN
                SET l_retVal = l_retVal || X'50';
            WHEN '51' THEN
                SET l_retVal = l_retVal || X'51';
            WHEN '52' THEN
                SET l_retVal = l_retVal || X'52';
            WHEN '53' THEN
                SET l_retVal = l_retVal || X'53';
            WHEN '54' THEN
                SET l_retVal = l_retVal || X'54';
            WHEN '55' THEN
                SET l_retVal = l_retVal || X'55';
            WHEN '56' THEN
                SET l_retVal = l_retVal || X'56';
            WHEN '57' THEN
                SET l_retVal = l_retVal || X'57';
            WHEN '58' THEN
                SET l_retVal = l_retVal || X'58';
            WHEN '59' THEN
                SET l_retVal = l_retVal || X'59';
            WHEN '5A' THEN
                SET l_retVal = l_retVal || X'5A';
            WHEN '5B' THEN
                SET l_retVal = l_retVal || X'5B';
            WHEN '5C' THEN
                SET l_retVal = l_retVal || X'5C';
            WHEN '5D' THEN
                SET l_retVal = l_retVal || X'5D';
            WHEN '5E' THEN
                SET l_retVal = l_retVal || X'5E';
            WHEN '5F' THEN
                SET l_retVal = l_retVal || X'5F';
            WHEN '60' THEN
                SET l_retVal = l_retVal || X'60';
            WHEN '61' THEN
                SET l_retVal = l_retVal || X'61';
            WHEN '62' THEN
                SET l_retVal = l_retVal || X'62';
            WHEN '63' THEN
                SET l_retVal = l_retVal || X'63';
            WHEN '64' THEN
                SET l_retVal = l_retVal || X'64';
            WHEN '65' THEN
                SET l_retVal = l_retVal || X'65';
            WHEN '66' THEN
                SET l_retVal = l_retVal || X'66';
            WHEN '67' THEN
                SET l_retVal = l_retVal || X'67';
            WHEN '68' THEN
                SET l_retVal = l_retVal || X'68';
            WHEN '69' THEN
                SET l_retVal = l_retVal || X'69';
            WHEN '6A' THEN
                SET l_retVal = l_retVal || X'6A';
            WHEN '6B' THEN
                SET l_retVal = l_retVal || X'6B';
            WHEN '6C' THEN
                SET l_retVal = l_retVal || X'6C';
            WHEN '6D' THEN
                SET l_retVal = l_retVal || X'6D';
            WHEN '6E' THEN
                SET l_retVal = l_retVal || X'6E';
            WHEN '6F' THEN
                SET l_retVal = l_retVal || X'6F';
            WHEN '70' THEN
                SET l_retVal = l_retVal || X'70';
            WHEN '71' THEN
                SET l_retVal = l_retVal || X'71';
            WHEN '72' THEN
                SET l_retVal = l_retVal || X'72';
            WHEN '73' THEN
                SET l_retVal = l_retVal || X'73';
            WHEN '74' THEN
                SET l_retVal = l_retVal || X'74';
            WHEN '75' THEN
                SET l_retVal = l_retVal || X'75';
            WHEN '76' THEN
                SET l_retVal = l_retVal || X'76';
            WHEN '77' THEN
                SET l_retVal = l_retVal || X'77';
            WHEN '78' THEN
                SET l_retVal = l_retVal || X'78';
            WHEN '79' THEN
                SET l_retVal = l_retVal || X'79';
            WHEN '7A' THEN
                SET l_retVal = l_retVal || X'7A';
            WHEN '7B' THEN
                SET l_retVal = l_retVal || X'7B';
            WHEN '7C' THEN
                SET l_retVal = l_retVal || X'7C';
            WHEN '7D' THEN
                SET l_retVal = l_retVal || X'7D';
            WHEN '7E' THEN
                SET l_retVal = l_retVal || X'7E';
            WHEN '7F' THEN
                SET l_retVal = l_retVal || X'7F';
            WHEN '80' THEN
                SET l_retVal = l_retVal || X'80';
            WHEN '81' THEN
                SET l_retVal = l_retVal || X'81';
            WHEN '82' THEN
                SET l_retVal = l_retVal || X'82';
            WHEN '83' THEN
                SET l_retVal = l_retVal || X'83';
            WHEN '84' THEN
                SET l_retVal = l_retVal || X'84';
            WHEN '85' THEN
                SET l_retVal = l_retVal || X'85';
            WHEN '86' THEN
                SET l_retVal = l_retVal || X'86';
            WHEN '87' THEN
                SET l_retVal = l_retVal || X'87';
            WHEN '88' THEN
                SET l_retVal = l_retVal || X'88';
            WHEN '89' THEN
                SET l_retVal = l_retVal || X'89';
            WHEN '8A' THEN
                SET l_retVal = l_retVal || X'8A';
            WHEN '8B' THEN
                SET l_retVal = l_retVal || X'8B';
            WHEN '8C' THEN
                SET l_retVal = l_retVal || X'8C';
            WHEN '8D' THEN
                SET l_retVal = l_retVal || X'8D';
            WHEN '8E' THEN
                SET l_retVal = l_retVal || X'8E';
            WHEN '8F' THEN
                SET l_retVal = l_retVal || X'8F';
            WHEN '90' THEN
                SET l_retVal = l_retVal || X'90';
            WHEN '91' THEN
                SET l_retVal = l_retVal || X'91';
            WHEN '92' THEN
                SET l_retVal = l_retVal || X'92';
            WHEN '93' THEN
                SET l_retVal = l_retVal || X'93';
            WHEN '94' THEN
                SET l_retVal = l_retVal || X'94';
            WHEN '95' THEN
                SET l_retVal = l_retVal || X'95';
            WHEN '96' THEN
                SET l_retVal = l_retVal || X'96';
            WHEN '97' THEN
                SET l_retVal = l_retVal || X'97';
            WHEN '98' THEN
                SET l_retVal = l_retVal || X'98';
            WHEN '99' THEN
                SET l_retVal = l_retVal || X'99';
            WHEN '9A' THEN
                SET l_retVal = l_retVal || X'9A';
            WHEN '9B' THEN
                SET l_retVal = l_retVal || X'9B';
            WHEN '9C' THEN
                SET l_retVal = l_retVal || X'9C';
            WHEN '9D' THEN
                SET l_retVal = l_retVal || X'9D';
            WHEN '9E' THEN
                SET l_retVal = l_retVal || X'9E';
            WHEN '9F' THEN
                SET l_retVal = l_retVal || X'9F';
            WHEN 'A0' THEN
                SET l_retVal = l_retVal || X'A0';
            WHEN 'A1' THEN
                SET l_retVal = l_retVal || X'A1';
            WHEN 'A2' THEN
                SET l_retVal = l_retVal || X'A2';
            WHEN 'A3' THEN
                SET l_retVal = l_retVal || X'A3';
            WHEN 'A4' THEN
                SET l_retVal = l_retVal || X'A4';
            WHEN 'A5' THEN
                SET l_retVal = l_retVal || X'A5';
            WHEN 'A6' THEN
                SET l_retVal = l_retVal || X'A6';
            WHEN 'A7' THEN
                SET l_retVal = l_retVal || X'A7';
            WHEN 'A8' THEN
                SET l_retVal = l_retVal || X'A8';
            WHEN 'A9' THEN
                SET l_retVal = l_retVal || X'A9';
            WHEN 'AA' THEN
                SET l_retVal = l_retVal || X'AA';
            WHEN 'AB' THEN
                SET l_retVal = l_retVal || X'AB';
            WHEN 'AC' THEN
                SET l_retVal = l_retVal || X'AC';
            WHEN 'AD' THEN
                SET l_retVal = l_retVal || X'AD';
            WHEN 'AE' THEN
                SET l_retVal = l_retVal || X'AE';
            WHEN 'AF' THEN
                SET l_retVal = l_retVal || X'AF';
            WHEN 'B0' THEN
                SET l_retVal = l_retVal || X'B0';
            WHEN 'B1' THEN
                SET l_retVal = l_retVal || X'B1';
            WHEN 'B2' THEN
                SET l_retVal = l_retVal || X'B2';
            WHEN 'B3' THEN
                SET l_retVal = l_retVal || X'B3';
            WHEN 'B4' THEN
                SET l_retVal = l_retVal || X'B4';
            WHEN 'B5' THEN
                SET l_retVal = l_retVal || X'B5';
            WHEN 'B6' THEN
                SET l_retVal = l_retVal || X'B6';
            WHEN 'B7' THEN
                SET l_retVal = l_retVal || X'B7';
            WHEN 'B8' THEN
                SET l_retVal = l_retVal || X'B8';
            WHEN 'B9' THEN
                SET l_retVal = l_retVal || X'B9';
            WHEN 'BA' THEN
                SET l_retVal = l_retVal || X'BA';
            WHEN 'BB' THEN
                SET l_retVal = l_retVal || X'BB';
            WHEN 'BC' THEN
                SET l_retVal = l_retVal || X'BC';
            WHEN 'BD' THEN
                SET l_retVal = l_retVal || X'BD';
            WHEN 'BE' THEN
                SET l_retVal = l_retVal || X'BE';
            WHEN 'BF' THEN
                SET l_retVal = l_retVal || X'BF';
            WHEN 'C0' THEN
                SET l_retVal = l_retVal || X'C0';
            WHEN 'C1' THEN
                SET l_retVal = l_retVal || X'C1';
            WHEN 'C2' THEN
                SET l_retVal = l_retVal || X'C2';
            WHEN 'C3' THEN
                SET l_retVal = l_retVal || X'C3';
            WHEN 'C4' THEN
                SET l_retVal = l_retVal || X'C4';
            WHEN 'C5' THEN
                SET l_retVal = l_retVal || X'C5';
            WHEN 'C6' THEN
                SET l_retVal = l_retVal || X'C6';
            WHEN 'C7' THEN
                SET l_retVal = l_retVal || X'C7';
            WHEN 'C8' THEN
                SET l_retVal = l_retVal || X'C8';
            WHEN 'C9' THEN
                SET l_retVal = l_retVal || X'C9';
            WHEN 'CA' THEN
                SET l_retVal = l_retVal || X'CA';
            WHEN 'CB' THEN
                SET l_retVal = l_retVal || X'CB';
            WHEN 'CC' THEN
                SET l_retVal = l_retVal || X'CC';
            WHEN 'CD' THEN
                SET l_retVal = l_retVal || X'CD';
            WHEN 'CE' THEN
                SET l_retVal = l_retVal || X'CE';
            WHEN 'CF' THEN
                SET l_retVal = l_retVal || X'CF';
            WHEN 'D0' THEN
                SET l_retVal = l_retVal || X'D0';
            WHEN 'D1' THEN
                SET l_retVal = l_retVal || X'D1';
            WHEN 'D2' THEN
                SET l_retVal = l_retVal || X'D2';
            WHEN 'D3' THEN
                SET l_retVal = l_retVal || X'D3';
            WHEN 'D4' THEN
                SET l_retVal = l_retVal || X'D4';
            WHEN 'D5' THEN
                SET l_retVal = l_retVal || X'D5';
            WHEN 'D6' THEN
                SET l_retVal = l_retVal || X'D6';
            WHEN 'D7' THEN
                SET l_retVal = l_retVal || X'D7';
            WHEN 'D8' THEN
                SET l_retVal = l_retVal || X'D8';
            WHEN 'D9' THEN
                SET l_retVal = l_retVal || X'D9';
            WHEN 'DA' THEN
                SET l_retVal = l_retVal || X'DA';
            WHEN 'DB' THEN
                SET l_retVal = l_retVal || X'DB';
            WHEN 'DC' THEN
                SET l_retVal = l_retVal || X'DC';
            WHEN 'DD' THEN
                SET l_retVal = l_retVal || X'DD';
            WHEN 'DE' THEN
                SET l_retVal = l_retVal || X'DE';
            WHEN 'DF' THEN
                SET l_retVal = l_retVal || X'DF';
            WHEN 'E0' THEN
                SET l_retVal = l_retVal || X'E0';
            WHEN 'E1' THEN
                SET l_retVal = l_retVal || X'E1';
            WHEN 'E2' THEN
                SET l_retVal = l_retVal || X'E2';
            WHEN 'E3' THEN
                SET l_retVal = l_retVal || X'E3';
            WHEN 'E4' THEN
                SET l_retVal = l_retVal || X'E4';
            WHEN 'E5' THEN
                SET l_retVal = l_retVal || X'E5';
            WHEN 'E6' THEN
                SET l_retVal = l_retVal || X'E6';
            WHEN 'E7' THEN
                SET l_retVal = l_retVal || X'E7';
            WHEN 'E8' THEN
                SET l_retVal = l_retVal || X'E8';
            WHEN 'E9' THEN
                SET l_retVal = l_retVal || X'E9';
            WHEN 'EA' THEN
                SET l_retVal = l_retVal || X'EA';
            WHEN 'EB' THEN
                SET l_retVal = l_retVal || X'EB';
            WHEN 'EC' THEN
                SET l_retVal = l_retVal || X'EC';
            WHEN 'ED' THEN
                SET l_retVal = l_retVal || X'ED';
            WHEN 'EE' THEN
                SET l_retVal = l_retVal || X'EE';
            WHEN 'EF' THEN
                SET l_retVal = l_retVal || X'EF';
            WHEN 'F0' THEN
                SET l_retVal = l_retVal || X'F0';
            WHEN 'F1' THEN
                SET l_retVal = l_retVal || X'F1';
            WHEN 'F2' THEN
                SET l_retVal = l_retVal || X'F2';
            WHEN 'F3' THEN
                SET l_retVal = l_retVal || X'F3';
            WHEN 'F4' THEN
                SET l_retVal = l_retVal || X'F4';
            WHEN 'F5' THEN
                SET l_retVal = l_retVal || X'F5';
            WHEN 'F6' THEN
                SET l_retVal = l_retVal || X'F6';
            WHEN 'F7' THEN
                SET l_retVal = l_retVal || X'F7';
            WHEN 'F8' THEN
                SET l_retVal = l_retVal || X'F8';
            WHEN 'F9' THEN
                SET l_retVal = l_retVal || X'F9';
            WHEN 'FA' THEN
                SET l_retVal = l_retVal || X'FA';
            WHEN 'FB' THEN
                SET l_retVal = l_retVal || X'FB';
            WHEN 'FC' THEN
                SET l_retVal = l_retVal || X'FC';
            WHEN 'FD' THEN
                SET l_retVal = l_retVal || X'FD';
            WHEN 'FE' THEN
                SET l_retVal = l_retVal || X'FE';
            WHEN 'FF' THEN
                SET l_retVal = l_retVal || X'FF';
        END CASE;

        -- set the next position and multiplier:
        SET l_pos = l_pos + 1;
    END WHILE; -- another character exists

    SET ao_oid = l_retVal;

    -- return the computed value:
    RETURN 1;
END;
-- p_stringToByte


-------------------------------------------------------------------------------
-- Converts a binary representation of an object id to its string
-- representation. <BR>
--
-- @input parameters:
-- @param   ai_oid              The byte value to be converted to string.
--
-- @output parameters:
-- @param   ao_oid_s            The string value of the oid.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_byteToString');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_byteToString (
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,--OBJECTID
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)--OBJECTIDSTRING
   )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retVal        VARCHAR (16) DEFAULT '';
    DECLARE l_pos           INTEGER DEFAULT 1; -- the current position
                                            -- start with the upper most byte
    DECLARE l_charVal       CHAR (1) FOR BIT DATA;
                                            -- the binary value of the digit

-- body:
    -- loop through all characters of the input string:
    WHILE (l_pos <= 8)                  -- another byte exists?
    DO
        -- get the actual digit:
        SET l_charVal = SUBSTR (ai_oid, l_pos, 1);

        -- convert the digit:
        CASE l_charVal
            WHEN X'00' THEN
                SET l_retVal = l_retVal || '00';
            WHEN X'01' THEN
                SET l_retVal = l_retVal || '01';
            WHEN X'02' THEN
                SET l_retVal = l_retVal || '02';
            WHEN X'03' THEN
                SET l_retVal = l_retVal || '03';
            WHEN X'04' THEN
                SET l_retVal = l_retVal || '04';
            WHEN X'05' THEN
                SET l_retVal = l_retVal || '05';
            WHEN X'06' THEN
                SET l_retVal = l_retVal || '06';
            WHEN X'07' THEN
                SET l_retVal = l_retVal || '07';
            WHEN X'08' THEN
                SET l_retVal = l_retVal || '08';
            WHEN X'09' THEN
                SET l_retVal = l_retVal || '09';
            WHEN X'0A' THEN
                SET l_retVal = l_retVal || '0A';
            WHEN X'0B' THEN
                SET l_retVal = l_retVal || '0B';
            WHEN X'0C' THEN
                SET l_retVal = l_retVal || '0C';
            WHEN X'0D' THEN
                SET l_retVal = l_retVal || '0D';
            WHEN X'0E' THEN
                SET l_retVal = l_retVal || '0E';
            WHEN X'0F' THEN
                SET l_retVal = l_retVal || '0F';
            WHEN X'10' THEN
                SET l_retVal = l_retVal || '10';
            WHEN X'11' THEN
                SET l_retVal = l_retVal || '11';
            WHEN X'12' THEN
                SET l_retVal = l_retVal || '12';
            WHEN X'13' THEN
                SET l_retVal = l_retVal || '13';
            WHEN X'14' THEN
                SET l_retVal = l_retVal || '14';
            WHEN X'15' THEN
                SET l_retVal = l_retVal || '15';
            WHEN X'16' THEN
                SET l_retVal = l_retVal || '16';
            WHEN X'17' THEN
                SET l_retVal = l_retVal || '17';
            WHEN X'18' THEN
                SET l_retVal = l_retVal || '18';
            WHEN X'19' THEN
                SET l_retVal = l_retVal || '19';
            WHEN X'1A' THEN
                SET l_retVal = l_retVal || '1A';
            WHEN X'1B' THEN
                SET l_retVal = l_retVal || '1B';
            WHEN X'1C' THEN
                SET l_retVal = l_retVal || '1C';
            WHEN X'1D' THEN
                SET l_retVal = l_retVal || '1D';
            WHEN X'1E' THEN
                SET l_retVal = l_retVal || '1E';
            WHEN X'1F' THEN
                SET l_retVal = l_retVal || '1F';
            WHEN X'20' THEN
                SET l_retVal = l_retVal || '20';
            WHEN X'21' THEN
                SET l_retVal = l_retVal || '21';
            WHEN X'22' THEN
                SET l_retVal = l_retVal || '22';
            WHEN X'23' THEN
                SET l_retVal = l_retVal || '23';
            WHEN X'24' THEN
                SET l_retVal = l_retVal || '24';
            WHEN X'25' THEN
                SET l_retVal = l_retVal || '25';
            WHEN X'26' THEN
                SET l_retVal = l_retVal || '26';
            WHEN X'27' THEN
                SET l_retVal = l_retVal || '27';
            WHEN X'28' THEN
                SET l_retVal = l_retVal || '28';
            WHEN X'29' THEN
                SET l_retVal = l_retVal || '29';
            WHEN X'2A' THEN
                SET l_retVal = l_retVal || '2A';
            WHEN X'2B' THEN
                SET l_retVal = l_retVal || '2B';
            WHEN X'2C' THEN
                SET l_retVal = l_retVal || '2C';
            WHEN X'2D' THEN
                SET l_retVal = l_retVal || '2D';
            WHEN X'2E' THEN
                SET l_retVal = l_retVal || '2E';
            WHEN X'2F' THEN
                SET l_retVal = l_retVal || '2F';
            WHEN X'30' THEN
                SET l_retVal = l_retVal || '30';
            WHEN X'31' THEN
                SET l_retVal = l_retVal || '31';
            WHEN X'32' THEN
                SET l_retVal = l_retVal || '32';
            WHEN X'33' THEN
                SET l_retVal = l_retVal || '33';
            WHEN X'34' THEN
                SET l_retVal = l_retVal || '34';
            WHEN X'35' THEN
                SET l_retVal = l_retVal || '35';
            WHEN X'36' THEN
                SET l_retVal = l_retVal || '36';
            WHEN X'37' THEN
                SET l_retVal = l_retVal || '37';
            WHEN X'38' THEN
                SET l_retVal = l_retVal || '38';
            WHEN X'39' THEN
                SET l_retVal = l_retVal || '39';
            WHEN X'3A' THEN
                SET l_retVal = l_retVal || '3A';
            WHEN X'3B' THEN
                SET l_retVal = l_retVal || '3B';
            WHEN X'3C' THEN
                SET l_retVal = l_retVal || '3C';
            WHEN X'3D' THEN
                SET l_retVal = l_retVal || '3D';
            WHEN X'3E' THEN
                SET l_retVal = l_retVal || '3E';
            WHEN X'3F' THEN
                SET l_retVal = l_retVal || '3F';
            WHEN X'40' THEN
                SET l_retVal = l_retVal || '40';
            WHEN X'41' THEN
                SET l_retVal = l_retVal || '41';
            WHEN X'42' THEN
                SET l_retVal = l_retVal || '42';
            WHEN X'43' THEN
                SET l_retVal = l_retVal || '43';
            WHEN X'44' THEN
                SET l_retVal = l_retVal || '44';
            WHEN X'45' THEN
                SET l_retVal = l_retVal || '45';
            WHEN X'46' THEN
                SET l_retVal = l_retVal || '46';
            WHEN X'47' THEN
                SET l_retVal = l_retVal || '47';
            WHEN X'48' THEN
                SET l_retVal = l_retVal || '48';
            WHEN X'49' THEN
                SET l_retVal = l_retVal || '49';
            WHEN X'4A' THEN
                SET l_retVal = l_retVal || '4A';
            WHEN X'4B' THEN
                SET l_retVal = l_retVal || '4B';
            WHEN X'4C' THEN
                SET l_retVal = l_retVal || '4C';
            WHEN X'4D' THEN
                SET l_retVal = l_retVal || '4D';
            WHEN X'4E' THEN
                SET l_retVal = l_retVal || '4E';
            WHEN X'4F' THEN
                SET l_retVal = l_retVal || '4F';
            WHEN X'50' THEN
                SET l_retVal = l_retVal || '50';
            WHEN X'51' THEN
                SET l_retVal = l_retVal || '51';
            WHEN X'52' THEN
                SET l_retVal = l_retVal || '52';
            WHEN X'53' THEN
                SET l_retVal = l_retVal || '53';
            WHEN X'54' THEN
                SET l_retVal = l_retVal || '54';
            WHEN X'55' THEN
                SET l_retVal = l_retVal || '55';
            WHEN X'56' THEN
                SET l_retVal = l_retVal || '56';
            WHEN X'57' THEN
                SET l_retVal = l_retVal || '57';
            WHEN X'58' THEN
                SET l_retVal = l_retVal || '58';
            WHEN X'59' THEN
                SET l_retVal = l_retVal || '59';
            WHEN X'5A' THEN
                SET l_retVal = l_retVal || '5A';
            WHEN X'5B' THEN
                SET l_retVal = l_retVal || '5B';
            WHEN X'5C' THEN
                SET l_retVal = l_retVal || '5C';
            WHEN X'5D' THEN
                SET l_retVal = l_retVal || '5D';
            WHEN X'5E' THEN
                SET l_retVal = l_retVal || '5E';
            WHEN X'5F' THEN
                SET l_retVal = l_retVal || '5F';
            WHEN X'60' THEN
                SET l_retVal = l_retVal || '60';
            WHEN X'61' THEN
                SET l_retVal = l_retVal || '61';
            WHEN X'62' THEN
                SET l_retVal = l_retVal || '62';
            WHEN X'63' THEN
                SET l_retVal = l_retVal || '63';
            WHEN X'64' THEN
                SET l_retVal = l_retVal || '64';
            WHEN X'65' THEN
                SET l_retVal = l_retVal || '65';
            WHEN X'66' THEN
                SET l_retVal = l_retVal || '66';
            WHEN X'67' THEN
                SET l_retVal = l_retVal || '67';
            WHEN X'68' THEN
                SET l_retVal = l_retVal || '68';
            WHEN X'69' THEN
                SET l_retVal = l_retVal || '69';
            WHEN X'6A' THEN
                SET l_retVal = l_retVal || '6A';
            WHEN X'6B' THEN
                SET l_retVal = l_retVal || '6B';
            WHEN X'6C' THEN
                SET l_retVal = l_retVal || '6C';
            WHEN X'6D' THEN
                SET l_retVal = l_retVal || '6D';
            WHEN X'6E' THEN
                SET l_retVal = l_retVal || '6E';
            WHEN X'6F' THEN
                SET l_retVal = l_retVal || '6F';
            WHEN X'70' THEN
                SET l_retVal = l_retVal || '70';
            WHEN X'71' THEN
                SET l_retVal = l_retVal || '71';
            WHEN X'72' THEN
                SET l_retVal = l_retVal || '72';
            WHEN X'73' THEN
                SET l_retVal = l_retVal || '73';
            WHEN X'74' THEN
                SET l_retVal = l_retVal || '74';
            WHEN X'75' THEN
                SET l_retVal = l_retVal || '75';
            WHEN X'76' THEN
                SET l_retVal = l_retVal || '76';
            WHEN X'77' THEN
                SET l_retVal = l_retVal || '77';
            WHEN X'78' THEN
                SET l_retVal = l_retVal || '78';
            WHEN X'79' THEN
                SET l_retVal = l_retVal || '79';
            WHEN X'7A' THEN
                SET l_retVal = l_retVal || '7A';
            WHEN X'7B' THEN
                SET l_retVal = l_retVal || '7B';
            WHEN X'7C' THEN
                SET l_retVal = l_retVal || '7C';
            WHEN X'7D' THEN
                SET l_retVal = l_retVal || '7D';
            WHEN X'7E' THEN
                SET l_retVal = l_retVal || '7E';
            WHEN X'7F' THEN
                SET l_retVal = l_retVal || '7F';
            WHEN X'80' THEN
                SET l_retVal = l_retVal || '80';
            WHEN X'81' THEN
                SET l_retVal = l_retVal || '81';
            WHEN X'82' THEN
                SET l_retVal = l_retVal || '82';
            WHEN X'83' THEN
                SET l_retVal = l_retVal || '83';
            WHEN X'84' THEN
                SET l_retVal = l_retVal || '84';
            WHEN X'85' THEN
                SET l_retVal = l_retVal || '85';
            WHEN X'86' THEN
                SET l_retVal = l_retVal || '86';
            WHEN X'87' THEN
                SET l_retVal = l_retVal || '87';
            WHEN X'88' THEN
                SET l_retVal = l_retVal || '88';
            WHEN X'89' THEN
                SET l_retVal = l_retVal || '89';
            WHEN X'8A' THEN
                SET l_retVal = l_retVal || '8A';
            WHEN X'8B' THEN
                SET l_retVal = l_retVal || '8B';
            WHEN X'8C' THEN
                SET l_retVal = l_retVal || '8C';
            WHEN X'8D' THEN
                SET l_retVal = l_retVal || '8D';
            WHEN X'8E' THEN
                SET l_retVal = l_retVal || '8E';
            WHEN X'8F' THEN
                SET l_retVal = l_retVal || '8F';
            WHEN X'90' THEN
                SET l_retVal = l_retVal || '90';
            WHEN X'91' THEN
                SET l_retVal = l_retVal || '91';
            WHEN X'92' THEN
                SET l_retVal = l_retVal || '92';
            WHEN X'93' THEN
                SET l_retVal = l_retVal || '93';
            WHEN X'94' THEN
                SET l_retVal = l_retVal || '94';
            WHEN X'95' THEN
                SET l_retVal = l_retVal || '95';
            WHEN X'96' THEN
                SET l_retVal = l_retVal || '96';
            WHEN X'97' THEN
                SET l_retVal = l_retVal || '97';
            WHEN X'98' THEN
                SET l_retVal = l_retVal || '98';
            WHEN X'99' THEN
                SET l_retVal = l_retVal || '99';
            WHEN X'9A' THEN
                SET l_retVal = l_retVal || '9A';
            WHEN X'9B' THEN
                SET l_retVal = l_retVal || '9B';
            WHEN X'9C' THEN
                SET l_retVal = l_retVal || '9C';
            WHEN X'9D' THEN
                SET l_retVal = l_retVal || '9D';
            WHEN X'9E' THEN
                SET l_retVal = l_retVal || '9E';
            WHEN X'9F' THEN
                SET l_retVal = l_retVal || '9F';
            WHEN X'A0' THEN
                SET l_retVal = l_retVal || 'A0';
            WHEN X'A1' THEN
                SET l_retVal = l_retVal || 'A1';
            WHEN X'A2' THEN
                SET l_retVal = l_retVal || 'A2';
            WHEN X'A3' THEN
                SET l_retVal = l_retVal || 'A3';
            WHEN X'A4' THEN
                SET l_retVal = l_retVal || 'A4';
            WHEN X'A5' THEN
                SET l_retVal = l_retVal || 'A5';
            WHEN X'A6' THEN
                SET l_retVal = l_retVal || 'A6';
            WHEN X'A7' THEN
                SET l_retVal = l_retVal || 'A7';
            WHEN X'A8' THEN
                SET l_retVal = l_retVal || 'A8';
            WHEN X'A9' THEN
                SET l_retVal = l_retVal || 'A9';
            WHEN X'AA' THEN
                SET l_retVal = l_retVal || 'AA';
            WHEN X'AB' THEN
                SET l_retVal = l_retVal || 'AB';
            WHEN X'AC' THEN
                SET l_retVal = l_retVal || 'AC';
            WHEN X'AD' THEN
                SET l_retVal = l_retVal || 'AD';
            WHEN X'AE' THEN
                SET l_retVal = l_retVal || 'AE';
            WHEN X'AF' THEN
                SET l_retVal = l_retVal || 'AF';
            WHEN X'B0' THEN
                SET l_retVal = l_retVal || 'B0';
            WHEN X'B1' THEN
                SET l_retVal = l_retVal || 'B1';
            WHEN X'B2' THEN
                SET l_retVal = l_retVal || 'B2';
            WHEN X'B3' THEN
                SET l_retVal = l_retVal || 'B3';
            WHEN X'B4' THEN
                SET l_retVal = l_retVal || 'B4';
            WHEN X'B5' THEN
                SET l_retVal = l_retVal || 'B5';
            WHEN X'B6' THEN
                SET l_retVal = l_retVal || 'B6';
            WHEN X'B7' THEN
                SET l_retVal = l_retVal || 'B7';
            WHEN X'B8' THEN
                SET l_retVal = l_retVal || 'B8';
            WHEN X'B9' THEN
                SET l_retVal = l_retVal || 'B9';
            WHEN X'BA' THEN
                SET l_retVal = l_retVal || 'BA';
            WHEN X'BB' THEN
                SET l_retVal = l_retVal || 'BB';
            WHEN X'BC' THEN
                SET l_retVal = l_retVal || 'BC';
            WHEN X'BD' THEN
                SET l_retVal = l_retVal || 'BD';
            WHEN X'BE' THEN
                SET l_retVal = l_retVal || 'BE';
            WHEN X'BF' THEN
                SET l_retVal = l_retVal || 'BF';
            WHEN X'C0' THEN
                SET l_retVal = l_retVal || 'C0';
            WHEN X'C1' THEN
                SET l_retVal = l_retVal || 'C1';
            WHEN X'C2' THEN
                SET l_retVal = l_retVal || 'C2';
            WHEN X'C3' THEN
                SET l_retVal = l_retVal || 'C3';
            WHEN X'C4' THEN
                SET l_retVal = l_retVal || 'C4';
            WHEN X'C5' THEN
                SET l_retVal = l_retVal || 'C5';
            WHEN X'C6' THEN
                SET l_retVal = l_retVal || 'C6';
            WHEN X'C7' THEN
                SET l_retVal = l_retVal || 'C7';
            WHEN X'C8' THEN
                SET l_retVal = l_retVal || 'C8';
            WHEN X'C9' THEN
                SET l_retVal = l_retVal || 'C9';
            WHEN X'CA' THEN
                SET l_retVal = l_retVal || 'CA';
            WHEN X'CB' THEN
                SET l_retVal = l_retVal || 'CB';
            WHEN X'CC' THEN
                SET l_retVal = l_retVal || 'CC';
            WHEN X'CD' THEN
                SET l_retVal = l_retVal || 'CD';
            WHEN X'CE' THEN
                SET l_retVal = l_retVal || 'CE';
            WHEN X'CF' THEN
                SET l_retVal = l_retVal || 'CF';
            WHEN X'D0' THEN
                SET l_retVal = l_retVal || 'D0';
            WHEN X'D1' THEN
                SET l_retVal = l_retVal || 'D1';
            WHEN X'D2' THEN
                SET l_retVal = l_retVal || 'D2';
            WHEN X'D3' THEN
                SET l_retVal = l_retVal || 'D3';
            WHEN X'D4' THEN
                SET l_retVal = l_retVal || 'D4';
            WHEN X'D5' THEN
                SET l_retVal = l_retVal || 'D5';
            WHEN X'D6' THEN
                SET l_retVal = l_retVal || 'D6';
            WHEN X'D7' THEN
                SET l_retVal = l_retVal || 'D7';
            WHEN X'D8' THEN
                SET l_retVal = l_retVal || 'D8';
            WHEN X'D9' THEN
                SET l_retVal = l_retVal || 'D9';
            WHEN X'DA' THEN
                SET l_retVal = l_retVal || 'DA';
            WHEN X'DB' THEN
                SET l_retVal = l_retVal || 'DB';
            WHEN X'DC' THEN
                SET l_retVal = l_retVal || 'DC';
            WHEN X'DD' THEN
                SET l_retVal = l_retVal || 'DD';
            WHEN X'DE' THEN
                SET l_retVal = l_retVal || 'DE';
            WHEN X'DF' THEN
                SET l_retVal = l_retVal || 'DF';
            WHEN X'E0' THEN
                SET l_retVal = l_retVal || 'E0';
            WHEN X'E1' THEN
                SET l_retVal = l_retVal || 'E1';
            WHEN X'E2' THEN
                SET l_retVal = l_retVal || 'E2';
            WHEN X'E3' THEN
                SET l_retVal = l_retVal || 'E3';
            WHEN X'E4' THEN
                SET l_retVal = l_retVal || 'E4';
            WHEN X'E5' THEN
                SET l_retVal = l_retVal || 'E5';
            WHEN X'E6' THEN
                SET l_retVal = l_retVal || 'E6';
            WHEN X'E7' THEN
                SET l_retVal = l_retVal || 'E7';
            WHEN X'E8' THEN
                SET l_retVal = l_retVal || 'E8';
            WHEN X'E9' THEN
                SET l_retVal = l_retVal || 'E9';
            WHEN X'EA' THEN
                SET l_retVal = l_retVal || 'EA';
            WHEN X'EB' THEN
                SET l_retVal = l_retVal || 'EB';
            WHEN X'EC' THEN
                SET l_retVal = l_retVal || 'EC';
            WHEN X'ED' THEN
                SET l_retVal = l_retVal || 'ED';
            WHEN X'EE' THEN
                SET l_retVal = l_retVal || 'EE';
            WHEN X'EF' THEN
                SET l_retVal = l_retVal || 'EF';
            WHEN X'F0' THEN
                SET l_retVal = l_retVal || 'F0';
            WHEN X'F1' THEN
                SET l_retVal = l_retVal || 'F1';
            WHEN X'F2' THEN
                SET l_retVal = l_retVal || 'F2';
            WHEN X'F3' THEN
                SET l_retVal = l_retVal || 'F3';
            WHEN X'F4' THEN
                SET l_retVal = l_retVal || 'F4';
            WHEN X'F5' THEN
                SET l_retVal = l_retVal || 'F5';
            WHEN X'F6' THEN
                SET l_retVal = l_retVal || 'F6';
            WHEN X'F7' THEN
                SET l_retVal = l_retVal || 'F7';
            WHEN X'F8' THEN
                SET l_retVal = l_retVal || 'F8';
            WHEN X'F9' THEN
                SET l_retVal = l_retVal || 'F9';
            WHEN X'FA' THEN
                SET l_retVal = l_retVal || 'FA';
            WHEN X'FB' THEN
                SET l_retVal = l_retVal || 'FB';
            WHEN X'FC' THEN
                SET l_retVal = l_retVal || 'FC';
            WHEN X'FD' THEN
                SET l_retVal = l_retVal || 'FD';
            WHEN X'FE' THEN
                SET l_retVal = l_retVal || 'FE';
            WHEN X'FF' THEN
                SET l_retVal = l_retVal || 'FF';
        END CASE;

        -- set the next position:
        SET l_pos = l_pos + 1;
    END WHILE; -- another character exists

    -- return the computed value:
    SET ao_oid_s = '0x' || l_retVal;

    RETURN 1;
END;
-- p_byteToString


-------------------------------------------------------------------------------
-- Compute the length of a string. <BR>
--
-- @input parameters:
-- @param   ai_string           String whose length shall be computed.
--
-- @output parameters:
-- @return  The length of the String or 0 if the String is NULL.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_stringLength');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_stringLength (
    -- input parameters: 
    IN  ai_string           VARCHAR (8000)
   )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
  
    -- initialize local variables:
    DECLARE l_retValue INT;
    DECLARE l_sqlcode INT DEFAULT 0;
    
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
 
    SET l_retValue = 0;
  
-- body:
    -- compute the length:
    SET l_retValue = LENGTH (ai_string);
  
    -- return the result:
    RETURN l_retValue;
END;
-- p_stringLength

-------------------------------------------------------------------------------
-- Add a string to a long string. <BR>
-- The long string is represented through a set of string variables. These
-- variables must be concatenated to get the whole string.
-- The procedure first searches for the currently last filled position of the
-- string and then concatenates the separator and the new string a this
-- position.
-- If the long string is empty the separator is not used.
--
-- @input parameters:
-- @param   ai_string           String which shall be concatenated.
-- @param   ai_sep              Separator string to be used between the previous
--                              and the current value.
-- @param   aio_length          The length of the full string.
-- @param   aio_str             Content of the string.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_addString');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_addString (
    -- input parameters: 
    IN  ai_string           VARCHAR (8000),
    IN  ai_sep              VARCHAR (10),
    -- input output parameters:
    OUT aio_length          INT,
    OUT aio_str             VARCHAR (8000)
   )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants: 
    DECLARE c_maxLength     INT;            -- max. length
    -- local variables: 
    DECLARE l_length        INT;            -- length of the string to be
                                            -- concatenated
    DECLARE l_concatenationString VARCHAR (8000); -- full string for
                                            -- concatenation
    DECLARE l_sqlcode       INT DEFAULT 0;
    
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- assign constants:
    SET c_maxLength = 8000;
    -- initialize local variables:
    SET l_concatenationString = ai_sep || ai_string;
  
-- body:
    IF aio_length = 0 THEN                  -- string is empty?
        -- get the length of the string to be concatenated:
        SET l_length = LENGTH (ai_string);
        -- set the new first part of the string:
        SET aio_str = ai_string;
    ELSE                                    -- string not empty
        -- get the length of the string to be concatenated:
        SET l_length = LENGTH (l_concatenationString);
        IF aio_length <= c_maxLength THEN   -- string not full?
            SET aio_str = aio_str ||
                SUBSTR (l_concatenationString, 1,
                    c_maxLength - aio_length);
        ELSE                                -- the string is already full
            -- nothing to concatenate:
            SET l_length = 0;
        END IF;
    END IF;

    -- compute the new length of the whole string:
    SET aio_length = aio_length + l_length;
END;
-- p_addString

-------------------------------------------------------------------------------
-- Display a long string. <BR>
-- The long string is represented through a set of string variables. These
-- variables must be concatenated to get the whole string.
--
-- @input parameters:
-- @param   ai_title            Title to be displayed before the string.
-- @param   ai_length           The length of the full string.
-- @param   ai_string           Content of the string.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_displayString');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_displayString (
    -- input parameters: 
    IN  ai_title            VARCHAR (255),
    IN  ai_length           INT,
    IN  ai_string           VARCHAR (8000)
   )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
-- body:
    -- display the title:
    CALL IBSDEV1.p_Debug (ai_title);
 
    -- display the string if it is not empty:
    IF ai_length > 0 THEN 
        CALL IBSDEV1.p_Debug (ai_title);
    END IF;
END;
-- p_displayString

-------------------------------------------------------------------------------
-- Change scheme of a table. <BR>
-- This procedure compares the original table and a table containing the new
-- scheme. It then copies each attribute value of the original table to the
-- new table which also exists in the new table. Attributes which do not exist
-- in the new table are omitted. <BR>
-- For each attribute which exists just in the new table and not in the old one
-- there must be a default value within the set of parameters. If such an
-- attribute is not mentioned the whole process is cancelled. Each attribute
-- value which represents a string must also contain quotes. Otherwise a syntax
-- error will be raised.
-- E.g.: ai_attrName1 = 'name', ai_attrValue1 = '''myName'''
--       ai_attrName2 = 'id', ai_attrValue2 = '78'
-- For SQL Server the ai_attrNameX and ai_attrValueX parameters are optional.
-- <BR>
-- After the copy process is finished the old table is deleted and the new
-- table is renamed to the name of the original table. <BR>
-- The procedure contains a TRANSACTION block, so it is not allowed to call it
-- from within another TRANSACTION block. <BR>
-- ATTENTION: Ensure that each trigger and procedure working on this table
-- are newly created.
--
-- @input parameters:
-- @param   ai_context          Context name or description from within this
--                              procedure was called. This string is used as
--                              output and logging prefix.
-- @param   ai_tableName        Name of the table to be changed.
-- @param   ai_tempTableName    The temporary name of the table containing the
--                              new structure.
-- @param   ai_attrName1        Name of attribute to be added.
-- @param   ai_attrValue1       Default value of attribute to be added.
-- @param   ai_attrName2        Name of attribute to be added.
-- @param   ai_attrValue2       Default value of attribute to be added.
-- @param   ai_attrName3        Name of attribute to be added.
-- @param   ai_attrValue3       Default value of attribute to be added.
-- @param   ai_attrName4        Name of attribute to be added.
-- @param   ai_attrValue4       Default value of attribute to be added.
-- @param   ai_attrName5        Name of attribute to be added.
-- @param   ai_attrValue5       Default value of attribute to be added.
-- @param   ai_attrName6        Name of attribute to be added.
-- @param   ai_attrValue6       Default value of attribute to be added.
-- @param   ai_attrName7        Name of attribute to be added.
-- @param   ai_attrValue7       Default value of attribute to be added.
-- @param   ai_attrName8        Name of attribute to be added.
-- @param   ai_attrValue8       Default value of attribute to be added.
-- @param   ai_attrName9        Name of attribute to be added.
-- @param   ai_attrValue9       Default value of attribute to be added.
-- @param   ai_attrName10       Name of attribute to be added.
-- @param   ai_attrValue10      Default value of attribute to be added.
--
-- @output parameters:
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_changeTable');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_changeTable (
    -- input parameters: 
    IN  ai_context          VARCHAR (255),
    IN  ai_tableName        VARCHAR (30),
    IN  ai_tempTableName    VARCHAR (30),
    IN  ai_attrName1        VARCHAR (30),
    IN  ai_attrValue1       VARCHAR (255),
    IN  ai_attrName2        VARCHAR (30),
    IN  ai_attrValue2       VARCHAR (255),
    IN  ai_attrName3        VARCHAR (30),
    IN  ai_attrValue3       VARCHAR (255),
    IN  ai_attrName4        VARCHAR (30),
    IN  ai_attrValue4       VARCHAR (255),
    IN  ai_attrName5        VARCHAR (30),
    IN  ai_attrValue5       VARCHAR (255),
    IN  ai_attrName6        VARCHAR (30),
    IN  ai_attrValue6       VARCHAR (255),
    IN  ai_attrName7        VARCHAR (30),
    IN  ai_attrValue7       VARCHAR (255),
    IN  ai_attrName8        VARCHAR (30),
    IN  ai_attrValue8       VARCHAR (255),
    IN  ai_attrName9        VARCHAR (30),
    IN  ai_attrValue9       VARCHAR (255),
    IN  ai_attrName10       VARCHAR (30),
    IN  ai_attrValue10      VARCHAR (255))
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- local variables: 
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_eText         VARCHAR (255);   -- full error text
    DECLARE l_msg           VARCHAR (255);   -- the actual message
    DECLARE l_cmdString     VARCHAR (255);   -- command line to be executed
    DECLARE l_cursorId      INT;            -- id of cmd cursor
    DECLARE l_rowsProcessed INT;            -- number of rows of last cmd exec.
    DECLARE l_lastErrorPos  INT;            -- last error in cmd line execution
    DECLARE l_oldTableName  VARCHAR (30);    -- name of table with old scheme
    DECLARE l_newTableName  VARCHAR (30);    -- name of table with new scheme
    -- the comma-separated attributes of the old table structure which
    -- shall be assigned to the new attributes:
    DECLARE l_countOldAttributes INT;       -- count old attributes
    DECLARE l_oldAttributes VARCHAR (8000);  -- names of old attributes
    -- the comma-separated attributes of the new table structure:
    DECLARE l_countNewAttributes INT;       -- count new attributes
    DECLARE l_newAttributes VARCHAR (8000);  -- names of new attributes
    -- the attributes which where added:
    DECLARE l_countAddedAttributes INT;     -- count added attributes
    DECLARE l_addedAttributes VARCHAR (8000);-- names of added attributes
    -- the attributes which where deleted:
    DECLARE l_countDeletedAttributes INT;   -- count deleted attributes
    DECLARE l_deletedAttributes VARCHAR (8000);-- names of deleted attributes
    -- the attributes which where not found in the parameters:
    DECLARE l_countNotFoundAttributes INT;  -- count not found attributes
    DECLARE l_notFoundAttributes VARCHAR (8000);-- names of not found attributes
    DECLARE l_sep           VARCHAR (2);     -- the actual attribute separator
    DECLARE l_attrNameNew   VARCHAR (30);    -- the actual attribute name in the
                                            -- new scheme
    DECLARE l_attrNameOld   VARCHAR (30);    -- the actual attribute name in the
                                            -- old scheme
    DECLARE l_SQLString     VARCHAR (255);   
    DECLARE l_attrValue     VARCHAR (255);   -- value of actual attribute
    -- exceptions:
    DECLARE e_attributeNotFound INT;        -- an attribute was not found
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
  
--Translator Omission: The bit MOD, AND, OR, XOR, and bit negation operators are not supported.
/*
    DECLARE tableCursor CURSOR WITH HOLD FOR 
        SELECT ucase (cNew.name) AS newName, ucase (cOld.name) AS oldName 
        FROM syscolumns, sysobjects, syscolumns, sysobjects
        WHERE oNew.id = (Untranslated expression) AND
              oNew.sysstat (Untranslated operator) X'0f' = 3 AND oNew.id = cNew.id
              AND oOld.id = (Untranslated expression) AND
              oOld.sysstat (Untranslated operator) X'0f' = 3 AND oOld.id = cOld.id
              AND cNew.name = cOld.name
        UNION
        SELECT ucase (c.name) AS newName, CAST (NULL AS VARCHAR (30)) AS oldName 
        FROM syscolumns, sysobjects
        WHERE o.id = (Untranslated expression) AND
              o.sysstat (Untranslated operator) X'0f' = 3 AND o.id = c.id AND
              ucase (c.name) NOT IN
              (SELECT ucase (c.name) 
               FROM syscolumns, sysobjects
               WHERE o.id = (Untranslated expression) AND
                     o.sysstat (Untranslated operator) X'0f' = 3 AND
                     o.id = c.id
                    )
        UNION
        SELECT CAST (NULL AS VARCHAR (30)) AS newName, ucase (c.name) AS oldName 
        FROM syscolumns, sysobjects
        WHERE o.id = (Untranslated expression) AND
              o.sysstat (Untranslated operator) X'0f' = 3 AND o.id = c.id AND
              ucase (c.name) NOT IN
              (SELECT ucase (c.name) 
               FROM syscolumns, sysobjects
               WHERE o.id = (Untranslated expression) AND
                     o.sysstat (Untranslated operator) X'0f' = 3 AND
                     o.id = c.id);
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- initialize local variables:
    SET l_newTableName = ai_tempTableName;
    SET l_oldTableName = v_ai_tableName;
    SET l_countOldAttributes = 0;
    SET l_countNewAttributes = 0;
    SET l_countAddedAttributes = 0;
    SET l_countDeletedAttributes = 0;
    SET l_countNotFoundAttributes = 0;
    SET l_sep = '';
    SET e_attributeNotFound = 100101;
  
-- body:
    IF l_newTableName <> l_oldTableName THEN 
    -- check if the target table exists:
        IF EXISTS (SELECT  *
            FROM    sysibm.systables
            WHERE   name =)
            -- object_id (@l_oldTableName)
        THEN
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
                    CONVERT (VARCHAR, null) AS oldName
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
                SELECT  CONVERT (VARCHAR, null) AS newName,
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
                      );    
            OPEN tableCursor;

            SET l_sqlcode = 0;
            FETCH FROM tableCursor INTO l_attrNameNew, l_attrNameOld;
            SET l_sqlstatus = l_sqlcode;

            WHILE l_sqlcode <> 100 DO
                IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
                    IF l_attrNameNew IS NOT NULL THEN 
                        CALL p_addString (l_attrNameNew, l_sep,
                            l_countNewAttributes,
                            l_newAttributes);
                        IF l_attrNameOld IS NOT NULL THEN 
                            CALL p_addString (l_attrNameOld, l_sep,
                                l_countOldAttributes,
                                l_oldAttributes);
                        ELSE 
                            IF l_attrNameNew = ucase (ai_attrName1) THEN 
                                SET l_attrValue = ai_attrValue1;
                            ELSE IF l_attrNameNew = ucase (ai_attrName2) THEN 
                                SET l_attrValue = ai_attrValue2;
                            ELSE IF l_attrNameNew = ucase (ai_attrName3) THEN 
                                SET l_attrValue = ai_attrValue3;
                            ELSE IF l_attrNameNew = ucase (ai_attrName4) THEN 
                                SET l_attrValue = ai_attrValue4;
                            ELSE IF l_attrNameNew = ucase (ai_attrName5) THEN 
                                SET l_attrValue = ai_attrValue5;
                            ELSE IF l_attrNameNew = ucase (ai_attrName6) THEN 
                                SET l_attrValue = ai_attrValue6;
                            ELSE IF l_attrNameNew = ucase (ai_attrName7) THEN 
                                SET l_attrValue = ai_attrValue7;
                            ELSE IF l_attrNameNew = ucase (ai_attrName8) THEN 
                                SET l_attrValue = ai_attrValue8;
                            ELSE IF l_attrNameNew = ucase (ai_attrName9) THEN 
                                SET l_attrValue = ai_attrValue9;
                            ELSE IF l_attrNameNew = ucase (ai_attrName10) THEN 
                                SET l_attrValue = ai_attrValue10;
                            ELSE 
                                CALL p_addString (l_attrNameNew, l_sep,
                                    l_countNotFoundAttributes,
                                    l_notFoundAttributes);
                            END IF;

                            CALL p_addString (l_attrValue, l_sep, l_countOldAttributes,
                                l_oldAttributes);

                            CALL p_addString (l_attrNameNew, l_sep,
                                l_countAddedAttributes, l_addedAttributes);
                        END IF;
         
                        SET l_sep = ',';
                    ELSE 
                        CALL p_addString (l_attrNameOld, l_sep,
                            l_countDeletedAttributes, l_deletedAttributes);
                    END IF;
                END IF;
       
                SET l_sqlcode = 0;
                FETCH FROM tableCursor INTO l_attrNameNew, l_attrNameOld;
                SET l_sqlstatus = l_sqlcode;
            END WHILE;

            CLOSE tableCursor;

            IF v_l_countNotFoundAttributes > 0 THEN 
                SET v_l_error = v_e_attributeNotFound;
                SET v_l_ePos =
                    'Error in find attribute ' || ' - no value for attributes: ' ||
                    l_notFoundAttributes;
                GOTO exception1;
            END IF;
            SET l_SQLString =
                'INSERT INTO IBSDEV1.' || l_newTableName || ' (' || l_newAttributes || ')' ||
                ' SELECT ' || l_oldAttributes || ' FROM ' || l_oldTableName;
            EXECUTE IMMEDIATE l_SQLString;
            
            CALL ibs_erro.prepareError (l_sqlcode, 'Error in INSERT', l_ePos);
            GET DIAGNOSTICS l_error = RETURN_STATUS;

            IF l_error <> 0 THEN 
                GOTO exception1
            END IF;
    
            CALL p_dropTable (l_oldTableName);
        END IF;
    
        SET l_SQLString3 =
            'EXEC sp_rename ' || l_newTableName || ', ' || l_oldTableName;
        EXECUTE IMMEDIATE l_SQLString;
    
        CALL p_dropTable (ai_tempTableName);
    
        COMMIT;
    
        SET l_msg =
            ai_context || ': table ' || ai_tableName ||
           ' changed to new scheme.';
   
        CALL p_Debug (l_msg);
    
        CALL p_displayString (' - added attributes: ', l_countAddedAttributes,
            l_addedAttributes);
    
        CALL p_displayString (' - deleted attributes: ', l_countDeletedAttributes,
            l_deletedAttributes);
    ELSE 
    
        SET l_msg =
            ai_context || ': The tables are the same -> nothing to be done.';
    
-- PRINT   @l_msg
-- Translator Omission: Only top-level PRINT statements are translated.
    END IF;
*/
  
    RETURN 0;

/*  
    exception1:
-- Translator Omission: A DB2 rollback is slightly different from a TSQL rollback. Open cursors will be closed. Some result sets might not be returned.
    ROLLBACK;
  
    IF l_error = e_attributeNotFound THEN 
        SET l_eText =
            ai_context || ': Error when changing table ' || ai_tableName ||
            ': ' || l_ePos || '; errorcode = 0 ' ||
            ', errormessage = Attribute not found';
    ELSE 
        SET l_eText =
            ai_context || ': Error when changing table ' || ai_tableName ||
            ': ' || l_ePos || '; errorcode = ' ||
        CAST (rtrim (CHAR (l_error)) AS VARCHAR (30));
    END IF;
  
    CALL ibs_erro.logError (500, 'p_changeTable', l_error, l_eText);
  
    p_Debug (l_eText);
*/
END;

                              