/******************************************************************************
 * Stored Procedure to get Addresses for Order out of DB. <BR>
 *
 * @version     $Id: OrderAddress_01Proc.sql,v 1.3 2003/10/31 16:27:54 klaus Exp $
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

-- create the new procedure:
CREATE OR REPLACE PROCEDURE p_Order_01$retrieveAddresses
(
    ai_companyOid         RAW,
    ai_customerCompany    VARCHAR2,
    ao_deliveryName       OUT   VARCHAR2,
    ao_paymentName        OUT   VARCHAR2,
    ao_deliveryAddress    OUT   VARCHAR2,
    ao_paymentAddress     OUT   VARCHAR2,
    ao_deliveryZIP        OUT   VARCHAR2,
    ao_paymentZIP         OUT   VARCHAR2,
    ao_deliveryTown       OUT   VARCHAR2,
    ao_paymentTown        OUT   VARCHAR2,
    ao_deliveryCountry    OUT   VARCHAR2,
    ao_paymentCountry     OUT   VARCHAR2
)
AS
BEGIN
    ao_deliveryName := ai_customerCompany;
    ao_paymentName := ai_customerCompany;
    SELECT  a.street,
            a.street,
            a.zip,
            a.zip,
            a.town,
            a.town,
            a.country,
            country
    INTO    ao_deliveryAddress,
            ao_paymentAddress,
            ao_deliveryZIP,
            ao_paymentZIP,
            ao_deliveryTown,
            ao_paymentTown,
            ao_deliveryCountry,
            ao_paymentCountry
    FROM    ibs_Object o1, m2_Address_01 a
    WHERE   o1.containerId = ai_companyOid
        AND o1.oid = a.oid;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Order_01$retrieveAddresses',
                          'companyOid: ' || ai_companyOid ||
                          ', customerCompany: ' || ai_customerCompany ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );

END p_Order_01$retrieveAddresses;
/

show errors;

EXIT;