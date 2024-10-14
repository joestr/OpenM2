/******************************************************************************
 * TASK#1579 countertable to simulate Sequences.
 *
 * @version     2.10.0001, 27.08.2001    
 *
 * @author      Andreas Jansa (AJ)  010827
 ******************************************************************************
 */
CREATE TABLE ibs_Counter
(
    counterName NAME UNIQUE NOT NULL,   -- name of counter
    currentCount INT NOT NULL           -- current count of counter
)
GO
-- ibs_ConsistsOf
