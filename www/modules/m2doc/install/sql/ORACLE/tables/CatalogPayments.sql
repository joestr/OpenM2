/******************************************************************************
 * This table contains information about a payment type in a catalog.<BR>
 *
 * @version     $Id: CatalogPayments.sql,v 1.3 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Daniel Janesch (DJ) 001121
 ******************************************************************************
 */

CREATE TABLE /*USER*/M2_CATALOGPAYMENTS
(
    catalogOid              RAW (8),
    paymentOid              RAW (8)
) /*TABLESPACE*/;
-- m2_CatalogPayments
EXIT;