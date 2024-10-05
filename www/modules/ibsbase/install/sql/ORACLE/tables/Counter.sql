/******************************************************************************
 * TASK#1691 table for m2 counter.
 *
 * @version     2.10.0001, 05.11.2001
 *
 * @author      Andreas Jansa (AJ)  011105
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_Counter
(
    counterName VARCHAR2 (63) UNIQUE NOT NULL,   -- name of counter
    currentCount INTEGER NOT NULL           -- current count of counter
)  /*TABLESPACE*/;
-- ibs_Counter

exit;