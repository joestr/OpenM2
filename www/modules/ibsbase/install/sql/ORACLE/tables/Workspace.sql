/******************************************************************************
 * The ibs_workspace table incl. indexes. <BR>
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

CREATE TABLE /*USER*/IBS_WORKSPACE
(
    userId NUMBER (10,0) NOT NULL,
    DOMAINID NUMBER (10,0) NOT NULL,
    WORKSPACE RAW (8) NOT NULL,
    WORKBOX RAW (8) NOT NULL,
    OUTBOX RAW (8) NOT NULL,
    INBOX RAW (8) NOT NULL,
    NEWS RAW (8) NOT NULL,
    HOTLIST RAW (8) NOT NULL,
    PROFILE RAW (8) NOT NULL,
    PUBLICWSP RAW (8) NOT NULL,
    SHOPPINGCART RAW (8) NOT NULL,
    ORDERS RAW (8) NOT NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (userId DEFAULT 0);
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (domainId DEFAULT 0);
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (workspace DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (workBox DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (outBox DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (inBox DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (news DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (hotList DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (profile DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (publicWsp DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (shoppingCart DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/IBS_WORKSPACE  MODIFY (orders DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE IBS_WORKSPACE ADD ( CONSTRAINT PK__IBS_WORKSPA__UID__039D293F PRIMARY KEY ( userId ) );

exit;
