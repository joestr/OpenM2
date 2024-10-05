/******************************************************************************
 * The triggers for the ibs tab table. <BR>
 *
 * @version     2.10.0001, 06.02.2001
 *
 * @author      Mario Oberdorfer (MO)  010206
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_Tab
 */
-- create the trigger:
CREATE OR REPLACE TRIGGER TrigTabInsert 
BEFORE INSERT ON ibs_Tab
FOR EACH ROW
DECLARE
    -- constants:

    -- local variables:
    l_id                    INTEGER := 0;   -- id of the tab
    l_multilangKey          VARCHAR2 (63);  -- key in multilanguage table


BEGIN    
    l_id := :new.id;
    l_multilangKey := :new.multilangKey;

-- body:
   
    IF (l_id = 0)                       -- no id defined?
    THEN
        -- compute new id:
        SELECT  tabIdSeq.NEXTVAL
        INTO    l_id
        FROM    DUAL;
    END IF; -- if no id defined

    -- check if multilanguage key is defined:
    IF (l_multilangKey IS NULL OR l_multilangKey = '')
                                        -- no multilanguage key defined?
    THEN
        -- compute and set the new key:
        l_multilangKey := 'TAB_' || l_id;
    END IF; -- if no multilanguage key defined        

    -- assign the new values:
    :new.id := l_id;
    :new.multilangKey := l_multilangKey;
END TrigTabInsert;
/

exit;
