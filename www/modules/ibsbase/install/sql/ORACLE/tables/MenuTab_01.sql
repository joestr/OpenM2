/******************************************************************************
 *
 * The ibs_MenuTab_01 indexes and triggers. <BR> 
 * The ibs_MenuTab_01 table contains the values for the m2 object MenuTab_01.
 * 
 *
 * @version 1.10.0001
 *
 * @author  Monika Eisenkolb (ME)  011001
 *
 ******************************************************************************
 */
 CREATE TABLE /*USER*/ibs_MenuTab_01
 (
    oid         RAW (8)         ,
    objectOid   RAW (8)	        , 
    description VARCHAR2 (255)  ,
    isPrivate   NUMBER (1)      ,
    priorityKey INTEGER         ,
    domainId    INTEGER	        ,
    classFront  VARCHAR2 (255)  ,
    classBack   VARCHAR2 (255)  ,
    fileName    VARCHAR2 (255)
 ) /*TABLESPACE*/;
 -- ibs_MenuTab_01

EXIT;
