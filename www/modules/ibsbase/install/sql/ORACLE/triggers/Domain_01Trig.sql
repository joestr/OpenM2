/******************************************************************************
 * The triggers for Domain_01. <BR>
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

-- create the trigger
CREATE OR REPLACE TRIGGER TrigDomain_01Insert
BEFORE INSERT ON ibs_Domain_01
FOR EACH ROW
DECLARE
    -- constants:
    c_NOOID     CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    Trig_tVersionId INT := 16842993;
    Trig_countSchemes INT := 0;
BEGIN

    -- ensure that an id is set:
    IF (:new.id <= 0)              -- no id defined?
    THEN
        -- compute new id:
        SELECT  domainIdSeq.NEXTVAL
        INTO    :new.id
        FROM    sys.DUAL;
    END IF; -- no id defined

    -- ensure that an oid is set:
    IF (:new.oid = c_NOOID)  -- no oid defined?
    THEN
        -- compute new oid:
        :new.oid := createOid (Trig_tVersionId, :new.id);
    END IF; -- no oid defined

/* the following code is not longer necessary:
    -- ensure that there is a valid scheme:
    SELECT  COUNT (*)
    INTO    Trig_countSchemes
    FROM    ibs_DomainScheme_01
    WHERE   id = :new.scheme;

    IF (Trig_countSchemes = 0)         -- no valid scheme?
    THEN
        -- set first foundable scheme:
        SELECT  MIN (id)
        INTO    :new.scheme
        FROM    ibs_DomainScheme_01;
    END IF; -- no valid scheme

    -- get workspaceProc from the scheme:
    SELECT  workspaceProc
    INTO    :new.workspaceProc
    FROM    ibs_DomainScheme_01
    WHERE   id = :new.scheme;
*/
    -- ensure that there is a valid workspace procedure set:
    IF (:new.workspaceProc IS NULL)
    THEN
        :new.workspaceProc := 'p_Workspace_01$createObjects';
    END IF; -- if
END TrigDomain_01Insert;
/

show errors;

exit;
