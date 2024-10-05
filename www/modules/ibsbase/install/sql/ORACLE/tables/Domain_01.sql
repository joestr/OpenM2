/******************************************************************************
 * Create table IBS_DOMAIN_01. <BR>
 * The object table contains all currently existing system objects.
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  980803
 ******************************************************************************
 */

CREATE SEQUENCE domainIdSeq
	INCREMENT BY 1
	START WITH 1
	NOMAXVALUE
	NOCYCLE
        NOCACHE;

CREATE TABLE /*USER*/ibs_Domain_01
(
    id              NUMBER (10, 0)  NOT NULL,
    oid             RAW (8),
    scheme          NUMBER (10, 0)  NOT NULL, -- scheme of the domain
    workspaceProc   VARCHAR2 (63),      -- procedure for creating a workspace
                                        -- within this domain
    adminId         NUMBER (10, 0)  NOT NULL, -- default domain administrator
    adminGroupId    NUMBER (10, 0)  NOT NULL, -- group for domain administrators
    allGroupId      NUMBER (10, 0)  NOT NULL, -- group for all users
    userAdminGroupId NUMBER (10, 0) NOT NULL, -- group for administring users 
                                              -- and groups
    structAdminGroupId NUMBER (10, 0) NOT NULL, -- group for administring the 
                                        -- structure
    groupsOid       RAW (8)         NOT NULL, -- container for the groups
    usersOid        RAW (8)         NOT NULL, -- container for the users
    publicOid       RAW (8)         NOT NULL, -- public container of domain
    workspacesOid   RAW (8)         NOT NULL, -- container for workspaces of
                                        -- domain
    homepagePath    VARCHAR2 (63),      -- path of the homepage of the domain
    logo            VARCHAR2 (63),      -- logo for the domain
    sslRequired     NUMBER (1, 0)   NOT NULL -- ssl for the domain
) /*TABLESPACE*/;
-- ibs_Domain_01


ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (oid DEFAULT hexToRaw('0000000000000000'));
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (scheme DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (adminId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (adminGroupId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (allGroupId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (userAdminGroupId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (structAdminGroupId DEFAULT 0);
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (groupsOid DEFAULT hexToRaw ('0000000000000000'));
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (usersOid DEFAULT hexToRaw ('0000000000000000'));
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (publicOid DEFAULT hexToRaw ('0000000000000000'));
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (workspacesOid DEFAULT hexToRaw ('0000000000000000'));
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (homepagePath DEFAULT '');
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (logo DEFAULT '');
ALTER TABLE /*USER*/ibs_Domain_01  MODIFY (sslrequired DEFAULT 0);

exit;
