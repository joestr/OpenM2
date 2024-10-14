ALTER TABLE ibs_Domain_01
ADD scheme ID NULL
ALTER TABLE ibs_Domain_01
ADD workspaceProc STOREDPROCNAME NULL

INSERT INTO ibs_DomainScheme_01
VALUES (-1, 0x0, 'p_Workspace_01$createObjects')

UPDATE ibs_Domain_01
SET scheme = 1

UPDATE ibs_Domain_01
SET workspaceProc = s.workspaceProc
FROM ibs_DOmain_01 d, ibs_DomainScheme_01 s
WHERE d.scheme = s.id

