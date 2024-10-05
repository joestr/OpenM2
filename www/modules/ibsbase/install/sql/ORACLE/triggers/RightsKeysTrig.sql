/******************************************************************************
 * The ibs rigtkeys triggers. <BR>
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

-- create the trigger
CREATE OR REPLACE TRIGGER TrigRightsKeysInsert
AFTER INSERT ON ibs_RightsKeys
DECLARE
    -- local variables:
    l_id        INTEGER;
    l_cnt       INTEGER;
BEGIN

    -- check if there is an id <= 0 within the table:
    SELECT  COUNT (*)
    INTO    l_cnt
    FROM    ibs_RightsKeys
    WHERE   id <= 0;

    -- ensure that an id is set:
    IF (l_cnt > 0)                      -- at least one invalid id?
    THEN
        -- compute new id:
        SELECT  rightsKeysSeq.NEXTVAL
        INTO    l_id
        FROM    sys.DUAL;

        -- set the new id:
        UPDATE  ibs_RightsKeys
        SET     id = l_id
        WHERE   id <= 0;

        -- set the number of actual values within the key:
        UPDATE  ibs_RightsKeys
        SET     cnt = (SELECT   COUNT (id)
                        FROM    ibs_RightsKeys
                        WHERE   id = l_id)
        WHERE   id = l_id;

        -- because the procedure p_Rights$updateRightsCumKey cannot be called
        -- from here it must be reimplemented:
        -- drop all cumulated rights already derived for the actual key:
        BEGIN
            DELETE  ibs_RightsCum
            WHERE   rKey = l_id;
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;    

        -- update the cumulated rights:
        INSERT INTO ibs_RightsCum (userId, rKey, rights)
        SELECT  p.userId, r.id,
                MAX (r00) + MAX (r01) + MAX (r02) + MAX (r03) +
                MAX (r04) + MAX (r05) + MAX (r06) + MAX (r07) +
                MAX (r08) + MAX (r09) + MAX (r0A) + MAX (r0B) +
                MAX (r0C) + MAX (r0D) + MAX (r0E) + MAX (r0F) +
                MAX (r10) + MAX (r11) + MAX (r12) + MAX (r13) +
                MAX (r14) + MAX (r15) + MAX (r16) + MAX (r17) +
                MAX (r18) + MAX (r19) + MAX (r1A) + MAX (r1B) +
                MAX (r1C) + MAX (r1D) + MAX (r1E)
        FROM    (
                    SELECT  *
                    FROM    ibs_RightsKeys
                    WHERE   id = l_id
                ) r,
                (
                    SELECT  id AS id, id AS userId
                    FROM    ibs_User
                    UNION
                    SELECT  gu.groupId AS id, gu.userId AS userId
                    FROM    ibs_GroupUser gu, ibs_User u
                    WHERE   gu.userId = u.id
                ) p
        WHERE p.id = r.rPersonId
        GROUP BY r.id, p.userId;

        -- insert rights for owner where these rights are not already set:
        INSERT INTO ibs_RightsCum (userId, rKey, rights)
        SELECT  DISTINCT 9437185, id, 2147483647 -- 0x00900001, id, 0x7FFFFFFF
        FROM    ibs_RightsKeys
        WHERE   id = l_id
            AND id NOT IN 
                (SELECT rKey 
                FROM    ibs_RightsCum 
                WHERE   userId = 9437185
                    AND rkey = l_id);
    END IF; -- at least one invalid id

EXCEPTION
    WHEN OTHERS THEN
        NULL;
err;
END TrigRightsKeysInsert;
/

show errors;

exit;
