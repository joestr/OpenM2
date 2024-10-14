--------------------------------------------------------------------------------
-- Stored Procedure to get Addresses for Order out of DB. <BR>
--
-- @version     $Id: OrderAddress_01Proc.sql,v 1.5 2003/10/31 16:29:02 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
--
-- Retrieve the addresses for an order out of db.
--
-- @returns
--
--------------------------------------------------------------------------------

 -- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Order_01$retrieveAddresses');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Order_01$retrieveAddresses(
    IN  ai_companyOid       CHAR (8) FOR BIT DATA,
    IN  ai_customerCompany  VARCHAR (63),
    OUT ao_deliveryName     VARCHAR (63),
    OUT ao_paymentName      VARCHAR (63),
    OUT ao_deliveryAddress  VARCHAR (63),
    OUT ao_paymentAddress   VARCHAR (63),
    OUT ao_deliveryZIP      VARCHAR (63),
    OUT ao_paymentZIP       VARCHAR (63),
    OUT ao_deliveryTown     VARCHAR (63),
    OUT ao_paymentTown      VARCHAR (63),
    OUT ao_deliveryCountry  VARCHAR (63),
    OUT ao_paymentCountry   VARCHAR (63)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    SELECT ai_customerCompany, ai_customerCompany, a.street,
        a.street, a.zip, a.zip, a.town, a.town, a.country, a.country 
    INTO ao_deliveryName, ao_paymentName, ao_deliveryAddress,
        ao_paymentAddress, ao_deliveryZip, ao_paymentZip, ao_deliveryTown,
        ao_paymentTown, ao_deliveryCountry, ao_paymentCountry
    FROM IBSDEV1.ibs_Object o1 INNER JOIN IBSDEV1.m2_Address_01 a ON o1.oid = a.oid
    WHERE o1.containerId = ai_companyOid;
END;
-- p_Order_01$retrieveAddresses