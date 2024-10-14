/******************************************************************************
 * The MAD_COMPANY_01 table incl. indexes. <BR>
 * 
 * @version     $Id: Company_01.sql,v 1.4 2003/10/31 00:13:17 klaus Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

CREATE TABLE /*USER*/MAD_COMPANY_01
(
    OID RAW (8) NOT NULL,
    OWNER VARCHAR2 (63) NOT NULL,
    MANAGER VARCHAR2 (63) NOT NULL,
    LEGAL_FORM VARCHAR2 (63) NOT NULL,
    mwst number(38,0) 
) /*TABLESPACE*/;

alter table /*USER*/mad_company_01 modify ( oid default hextoraw ('0000000000000000'));
alter table /*USER*/mad_company_01 modify ( owner default 'undefined');
alter table /*USER*/mad_company_01 modify ( manager default 'undefined');
ALTER TABLE MAD_COMPANY_01 ADD ( CONSTRAINT UQ__MAD_COMPANY__OID__48076225 UNIQUE ( oid ) );

EXIT;
