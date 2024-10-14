/******************************************************************************
 * All stored procedures regarding to the class Counter. <BR>
 *
 * @version     2.10.0011, 27.08.2001
 *
 * @author      Andreas Jansa (AJ)  010827
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
-- delete existing procedure:
EXEC p_dropProc N'p_Counter$getNext'
GO

-- create the new procedure:
CREATE PROCEDURE p_Counter$getNext
(
    -- input parameters:
    @ai_counterName         NAME,
    -- output parameters:
    @ao_nextCount           INT OUTPUT
)
AS
BEGIN TRANSACTION 
    -- check if counter allready exist, if not create new counter
    IF NOT EXISTS (
        SELECT  counterName 
        FROM    ibs_Counter 
        WHERE   counterName = @ai_counterName)
    BEGIN
        -- set outputparameter to starting count
        SELECT @ao_nextCount = 1

        -- create new counter
        INSERT INTO ibs_Counter (counterName, currentCount)
        VALUES (@ai_counterName, @ao_nextCount)
        
    END
    ELSE
    BEGIN        
        -- get next count of required counter
        SELECT  @ao_nextCount = currentCount + 1
        FROM    ibs_Counter
        WHERE   counterName = @ai_counterName
    
        -- increment counter
        UPDATE  ibs_Counter 
        SET     currentCount = @ao_nextCount
        WHERE   counterName = @ai_counterName
    END
    
COMMIT TRANSACTION
GO
-- p_Counter$getNext



/******************************************************************************
 * Reset a counter to 0 (the next call of getNext returns 1) <BR>
 * If required counter does not exist, nothing happens. <BR>
 *
 * @param   ai_counterName      Name of the counter to be reseted
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Counter$reset'
GO

-- create the new procedure:
CREATE PROCEDURE p_Counter$reset
(
    -- input parameters:
    @ai_counterName         NAME
)
AS
BEGIN
    
    -- increment counter
    UPDATE  ibs_Counter 
    SET     currentCount = 0
    WHERE   counterName = @ai_counterName
END
GO
-- p_Counter$reset

