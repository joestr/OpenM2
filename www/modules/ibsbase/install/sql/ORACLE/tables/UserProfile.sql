/******************************************************************************
 * The ibs_userprofile table incl. indexes. <BR>
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

CREATE TABLE /*USER*/IBS_USERPROFILE(
    OID                                 RAW (8)         NOT NULL,
    userId                              NUMBER (10,0)   NOT NULL,
    NEWSTIMELIMIT                       NUMBER (10,0),
    NEWSSHOWONLYUNREAD                  NUMBER (1,0)    NOT NULL,
    OUTBOXUSETIMELIMIT                  NUMBER (1,0),
    OUTBOXTIMELIMIT                     NUMBER (10,0),
    OUTBOXUSETIMEFRAME                  NUMBER (1,0),
    OUTBOXTIMEFRAMEFROM                 DATE,
    OUTBOXTIMEFRAMETO                   DATE,
    SHOWEXTENDEDATTRIBUTES              NUMBER (1,0),
    SHOWFILESINWINDOWS                  NUMBER (1,0),
    LASTLOGIN                           DATE,
    LAYOUTID                            RAW (8), 
    SHOWREF                             NUMBER (1, 0), 
    SHOWEXTENDEDRIGHTS                  NUMBER (1, 0),
    SAVEPROFILE                         NUMBER (1, 0), 
    NOTIFICATIONKIND                    INTEGER,
    SENDSMS                             NUMBER (1, 0),
    ADDWEBLINK                          NUMBER (1, 0),
    LOCALEID                            RAW (8)
);

ALTER TABLE /*USER*/ibs_userprofile modify ( oid default hextoraw('0000000000000000'));
ALTER TABLE /*USER*/ibs_userprofile modify ( userId default 0);
ALTER TABLE /*USER*/ibs_userprofile modify ( layoutId default hextoraw('0000000000000000'));
ALTER TABLE IBS_USERPROFILE ADD ( CONSTRAINT PK__IBS_USERPRO__UID__78FF9F3B PRIMARY KEY ( userId ) );

EXIT;
