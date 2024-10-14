/******************************************************************************
 * The M2_PROFILECATEGORY_01 table. <BR>
 * 
 * @version     $Id: ProfileCategory_01.sql,v 1.4 2003/10/31 00:13:18 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE /*USER*/M2_PROFILECATEGORY_01
(
    PRODUCTPROFILEOID RAW (8) NOT NULL,
    CATEGORYOID RAW (8) NOT NULL
) /*TABLESPACE*/;

alter table /*USER*/m2_profilecategory_01 modify ( productprofileoid default hextoraw('0000000000000000'));
alter table /*USER*/m2_profilecategory_01 modify ( categoryoid default hextoraw('0000000000000000'));

EXIT;
