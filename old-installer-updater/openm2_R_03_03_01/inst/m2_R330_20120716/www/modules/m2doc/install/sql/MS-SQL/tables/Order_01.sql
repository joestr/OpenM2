/******************************************************************************
 * The m2_Order_01 table incl. indexes and triggers. <BR>
 *
 * @version     $Id: Order_01.sql,v 1.6 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW)  980921
 ******************************************************************************
 */
CREATE TABLE m2_Order_01
(
    oid                     OBJECTID    NOT NULL PRIMARY KEY
    ,voucherNo              NAME        NULL        -- sequential number
    ,voucherDate            DATETIME    NULL        -- voucherdate (e.g. order date)
    ,supplierCompany        NAME        NULL        -- name of the supplier
    ,contactSupplier        NAME        NULL        -- contact person supplier
    ,customerCompany        NAME        NULL        -- the company makeing the order
    ,contactCustomer        NAME        NULL        -- contact person customer
    ,deliveryAddress        DESCRIPTION NULL        -- the address where to send the order
    ,paymentAddress         DESCRIPTION NULL        -- the address where to send the bill
    ,description1           DESCRIPTION NULL        -- description how the order should be
                                                    -- handled if products not available
    ,description2           DESCRIPTION NULL        -- description of the delivery mode
    ,description3           DESCRIPTION NULL        -- description of the shippment method
    ,deliveryDate           DATETIME    NULL        -- wished date of delivery
    ,originator             OBJECTID    NULL        -- the originator of the voucher
    ,recipient              OBJECTID    NULL        -- the recipient of the voucher
    ,paymentOid             OBJECTID        NOT NULL    -- oid of paymenttype
    ,cc_number              NVARCHAR(16)     NULL        -- number of creditcard
    ,cc_expmonth            NAME            NULL        -- expiry month of creditcard
    ,cc_expyear             NVARCHAR(4)      NULL        -- expiry year of creditcard
    ,cc_owner               NVARCHAR(200)    NULL        -- owner of creditcard
    ,catalogOid             OBJECTID        NOT NULL    -- oid of catalog regarding to this order
)
GO
-- m2_Order_01
