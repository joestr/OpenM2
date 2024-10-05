/******************************************************************************
 * This table contains information about a payment type in a catalog.<BR>
 *
 * @version     $Id: PaymentType_01.sql,v 1.3 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Daniel Janesch (DJ) 001121
 ******************************************************************************
 */

CREATE TABLE /*USER*/M2_PAYMENTTYPE_01
(
    oid                     RAW (8),
    paymentTypeId           INTEGER,
    name                    VARCHAR2 (63)
) /*TABLESPACE*/;
-- m2_PaymentType_01
EXIT;