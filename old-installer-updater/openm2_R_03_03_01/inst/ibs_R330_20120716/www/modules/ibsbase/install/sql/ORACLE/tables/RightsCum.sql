/******************************************************************************
 * The ibs_rightsCum table incl. indexes. <BR>
 * The object table contains all currently existing system objects.
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  980803
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804   Code cleaning.
 ******************************************************************************
 */

CREATE TABLE /*USER*/ibs_RightsCum
(
    userId NUMBER(10,0) NOT NULL,
    rKey NUMBER(10,0) NOT NULL,
    rights NUMBER(10,0) NOT NULL
) /*TABLESPACE*/;
alter table /*USER*/ibs_rightsCum modify ( userId default 0 );
alter table /*USER*/ibs_rightsCum modify ( rKey default 0 );
alter table /*USER*/ibs_rightsCum modify ( rights default 0 );
exit;
