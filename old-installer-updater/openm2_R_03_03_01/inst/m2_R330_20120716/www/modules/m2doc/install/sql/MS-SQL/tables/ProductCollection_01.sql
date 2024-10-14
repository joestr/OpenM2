/******************************************************************************
 * Currently this table doesn't hold any special values. For future
 * extension it was established tough (prices for collections etc.). <BR>
 *
 * @version     $Id: ProductCollection_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 981222
 ******************************************************************************
 */

CREATE TABLE m2_ProductCollection_01
(
	oid	            OBJECTID			PRIMARY KEY NOT NULL,
	cost            MONEY               NULL,
	costCurrency    NVARCHAR(5)         NULL,
    totalQuantity   INT                 NULL,
	validFrom       DATETIME            NULL,
	categoryOidX    OBJECTID            NULL,
	categoryOidY    OBJECTID            NULL,
	nrCodes         INT                 NULL
)
GO
