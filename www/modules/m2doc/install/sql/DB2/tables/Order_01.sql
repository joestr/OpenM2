-------------------------------------------------------------------------------
-- The M2_ORDER_01 table . <BR>
-- The address table contains all currently existing addresses.
--
-- @version     $Id: Order_01.sql,v 1.3 2003/10/31 00:12:54 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.M2_ORDER_01
(
    OID                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    VOUCHERNO       VARCHAR (63) DEFAULT 'undefined',
                                            -- sequential number
    VOUCHERDATE     TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                            -- voucherdate (e.g. order date)
    SUPPLIERCOMPANY VARCHAR (63) DEFAULT 'undefined',
                                            -- name of the supplier
    CONTACTSUPPLIER VARCHAR (63) DEFAULT 'undefined',
                                            -- contact person supplier
    CUSTOMERCOMPANY VARCHAR (63) DEFAULT 'undefined',
                                            -- the company makeing the order
    CONTACTCUSTOMER VARCHAR (63) DEFAULT 'undefined',
                                            -- contact person customer
    DELIVERYADDRESS VARCHAR (255),           
                                            -- the address where to send
                                            -- the order
    PAYMENTADDRESS  VARCHAR (255),           
                                            -- the address where to send 
                                            -- the bill
    DESCRIPTION1    VARCHAR (255),           
                                            -- description how the order should
                                            -- be handled if products not
                                            -- available                                               
    DESCRIPTION2    VARCHAR (255),           
                                            -- description of the delivery mode
    DESCRIPTION3    VARCHAR (255),           
                                            -- description of the 
                                            -- shippment method
    DELIVERYDATE    TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                            -- wished date of delivery
    CC_NUMBER       VARCHAR (16),            
                                            -- number of creditcard
    CC_EXPMONTH     VARCHAR (63),            
                                            -- expiry month of creditcard
    CC_EXPYEAR      VARCHAR (4),             
                                            -- expiry year of creditcard
    CC_OWNER        VARCHAR (255),           
                                            -- owner of creditcard
    ORIGINATOR      CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
                                            -- the originator of the voucher
    RECIPIENT       CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',
                                            -- the recipient of the voucher
    PAYMENTOID      CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000',      
                                            -- oid of paymenttype
    CATALOGOID      CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
                                            -- oid of catalog regarding 
                                            -- to this order
);

-- Primary key:
ALTER TABLE IBSDEV1.M2_ORDER_01 ADD PRIMARY KEY (OID);
