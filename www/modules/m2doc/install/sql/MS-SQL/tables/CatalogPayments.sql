/******************************************************************************
 * This table contains information about a payment type in a catalog.<BR>
 *
 * @version     $Id: CatalogPayments.sql,v 1.2 2003/10/31 00:13:05 klaus Exp $
 *
 * @author      Daniel Janesch (DJ) 001121
 ******************************************************************************
 */

CREATE TABLE m2_CatalogPayments
(
    catalogOid              OBJECTID,
    paymentOid              OBJECTID
)
GO
-- m2_CatalogPayments