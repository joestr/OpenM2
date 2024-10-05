/******************************************************************************
 * This table holds the code values defined for a product. <BR>
 *
 * @version     $Id: ProductCodeValues_01.sql,v 1.4 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW)  981217
 ******************************************************************************
 */

CREATE TABLE m2_ProductCodeValues_01
(
    productOid          OBJECTID        NOT NULL,
    categoryOid         OBJECTID        NOT NULL,
    predefinedCodeOid   OBJECTID        NULL,
    codeValues          NVARCHAR(255)   NULL
)
GO
