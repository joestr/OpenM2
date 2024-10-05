/******************************************************************************
 * All stored procedures regarding the Register_01 Object. <BR>
 * 
 * @version     $Id: Register_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  000415
 ******************************************************************************
 */

/*
delete from ibs_System
WHERE name= 'MASTERDATACONTAINER'
insert into ibs_System
(state,name,type,value)
VALUES (2,'MASTERDATACONTAINER', 'OBJECTID', '0x0101290100000997')
UPDATE ibs_Token_01
SET value = 'Region'
WHERE name = 'TOK_COMPOWNER'
*/

/******************************************************************************
 * Creates the necessary objects. <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Register_01$createObjects') 
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Register_01$createObjects
GO

-- create the new procedure:
CREATE PROCEDURE p_Register_01$createObjects
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @description    DESCRIPTION,
    @region         DESCRIPTION,
    @mwst           INT,
    @street         NVARCHAR(63),
    @zip            NVARCHAR(15),
    @town           NVARCHAR(63),
    @tel            NVARCHAR(63),
    @fax            NVARCHAR(63),
    @email          NVARCHAR(127),
    @homepage       NVARCHAR(255),
    @aname          NAME,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID, @containerId OBJECTID, @addressId OBJECTID, 
            @userOid OBJECTID, @personOid OBJECTID
    SELECT  @oid = 0x0000000000000000
    -- masterdatacontainer in which the company has to be created
    DECLARE @containerId_s OBJECTIDSTRING, @personId_s OBJECTIDSTRING, @tempOid_s OBJECTIDSTRING

    -- initialize local variables:

    -- body:
    BEGIN TRANSACTION

    -- select the oid of the masterdatacontaineroid
    SELECT @containerId_s = value
    FROM ibs_System
    WHERE name = N'MASTERDATACONTAINER'
    AND state = 2

    IF (@@ROWCOUNT >= 1)
    BEGIN

    -- oid has been found
        -- create Company        
        EXEC @retValue = p_Company_01$create @userId, @op, 0x01012c01, @name,
                                @containerId_s, 1, 0, '0x0000000000000000', 
                                @description, @oid_s OUTPUT

        IF (@retValue = @ALL_RIGHT)
        BEGIN
        
            -- convert oid to string:
            EXEC    p_stringToByte @oid_s, @oid OUTPUT
        
            -- update the rest of the company with the right values
            UPDATE mad_Company_01
            SET owner = @region,
                mwst = @mwst
            WHERE oid = @oid
    
                -- select the address of the company
            SELECT @addressId = oid
            FROM ibs_Object
            WHERE tVersionId = 0x01012f01
            AND containerId = @oid

            -- update the address ot the company
            UPDATE m2_Address_01
            SET street = @street,
                zip = @zip,
                town = @town,
                tel = @tel,
                fax = @fax,
                email = @email,
                homepage = @homepage
            WHERE oid = @addressId

                -- select the contactcontainer of the company
            SELECT @containerId = oid
            FROM ibs_Object
            WHERE tVersionId = 0x01012b01
            AND containerId = @oid

            -- convert oid to string:
            EXEC    p_byteToString @containerId, @containerId_s OUTPUT

            -- create a new person as a contact
            EXEC p_Person_01$create @userId, @op, 0x01012a01, @aname,
                       @containerId_s, 1, 0, '0x0000000000000000', 
                       @description, @personId_s OUTPUT

            EXEC p_StringToByte @personId_s, @personOid OUTPUT

            -- select the working directory of the user
            SELECT @containerId = workbox
            FROM ibs_Workspace
            WHERE userId = @userId
        
            -- convert oid to string:
            EXEC    p_byteToString @containerId, @containerId_s OUTPUT
    
            -- create reference of company in the working directory of the user
            EXEC p_Referenz_01$create @userId, @op, 0x01010031, @name, 
                       @containerId_s, 1, 1, @oid_s,
                   '', @tempOid_s OUTPUT

            -- select oid of the user-object
            SELECT @userOid = oid
            FROM ibs_User
            WHERE id = @userId

            -- update person set userOid of related user
            UPDATE mad_Person_01
            SET    userOid = @userOid
            WHERE oid = @personOid

        END -- create not successfull
 
    END -- no container defined for login
    ELSE 
        SELECT @retValue = @OBJECTNOTFOUND 
        
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Register_01$createObjects
