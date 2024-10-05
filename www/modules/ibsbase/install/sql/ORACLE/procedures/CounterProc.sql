/******************************************************************************
 * TASK#1691 All stored procedures regarding to the class Counter. <BR>
 *
 * @version     2.10.0011, 05.11.2001
 *
 * @author      Andreas Jansa (AJ)  011105
 ******************************************************************************
 */


/******************************************************************************
 * Get Next count of specific counter if counter does not exist,
 * create counter. <BR>
 *
 * @param   ai_counterName      Name of the counter to be incremented
 *
 * @param   ao_nextCount        incremented count of specified counter
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Counter$getNext
(
    -- input parameters:
    ai_counterName          VARCHAR2,
    -- output parameters:
    ao_nextCount            OUT INTEGER
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;

    -- local variables:
    l_count                 INTEGER := 0;

BEGIN
    -- check if counter allready exist, if not create new counter
    SELECT  count (*)
    INTO    l_count
    FROM    ibs_Counter
    WHERE   counterName = ai_counterName;

    -- if counter does not exist allready - create it
    IF (l_count <= 0) THEN
        -- set outputparameter to starting count
        ao_nextCount := 1;

        -- create new counter
        INSERT INTO ibs_Counter (counterName, currentCount)
        VALUES (ai_counterName, ao_nextCount);
    ELSE
        -- get next count of required counter
        SELECT  currentCount + 1
        INTO    ao_nextCount
        FROM    ibs_Counter
        WHERE   counterName = ai_counterName;

        -- increment counter
        UPDATE  ibs_Counter
        SET     currentCount = ao_nextCount
        WHERE   counterName = ai_counterName;
    END IF;

    COMMIT WORK;

    RETURN c_ALL_RIGHT;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Counter$getNext',
            'Input: ai_counterName = ' || ai_counterName ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);

    RETURN c_NOT_OK;
END p_Counter$getNext;
/
-- p_Counter$getNext

show errors;


/******************************************************************************
 * Reset a counter to 0 (the next call of getNext returns 1) <BR>
 * If required counter does not exist, nothing happens. <BR>
 *
 * @param   ai_counterName      Name of the counter to be reseted
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Counter$reset
(
    -- input parameters:
    ai_counterName          VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1;

BEGIN

    -- reset counter
    UPDATE  ibs_Counter
    SET     currentCount = 0
    WHERE   counterName = ai_counterName;

    COMMIT WORK;

    RETURN c_ALL_RIGHT;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Counter$reset',
            'Input: ai_counterName = ' || ai_counterName ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);

    RETURN c_NOT_OK;
END p_Counter$reset;
/
-- p_Counter$reset

show errors;

exit;