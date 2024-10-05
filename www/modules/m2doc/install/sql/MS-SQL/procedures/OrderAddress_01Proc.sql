/******************************************************************************
 * Stored Procedure to get Addresses for Order out of DB. <BR>
 *
 * @version     $Id: OrderAddress_01Proc.sql,v 1.4 2006/01/19 15:56:46 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  990305
 ******************************************************************************
 */

/******************************************************************************
 *
 * Retrieve the addresses for an order out of db.
 *
 * @returns
 *
 */

 -- delete existing procedure:
IF EXISTS (select * from sysobjects where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_Order_01$retrieveAddresses') and sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Order_01$retrieveAddresses
GO

-- create the new procedure:
CREATE PROCEDURE p_Order_01$retrieveAddresses
(
    @companyOid        OBJECTID,
    @customerCompany    NAME,
    @deliveryName       NAME            OUTPUT,
    @paymentName        NAME            OUTPUT,
    @deliveryAddress    NAME            OUTPUT,
    @paymentAddress     NAME            OUTPUT,
    @deliveryZIP        NAME            OUTPUT,
    @paymentZIP         NAME            OUTPUT,
    @deliveryTown       NAME            OUTPUT,
    @paymentTown        NAME            OUTPUT,
    @deliveryCountry    NAME            OUTPUT,
    @paymentCountry     NAME            OUTPUT
)
AS
    SELECT  @deliveryName = @customerCompany,
            @paymentName = @customerCompany,
            @deliveryAddress = a.street,
            @paymentAddress = a.street,
            @deliveryZip = a.zip,
            @paymentZip = a.zip,
            @deliveryTown = a.town,
            @paymentTown = a.town,
            @deliveryCountry = a.country,
            @paymentCountry = a.country
    FROM    ibs_Object o1
    JOIN    m2_Address_01 a
    ON      o1.oid = a.oid 
    WHERE   o1.containerId = @companyOid
GO  
-- p_Order_01$retrieveAddresses
 