/******************************************************************************
 * The ibs system table incl. indexes. <BR>
 * The system table contains system variables used for configuring the system.
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Bernhard Walter (BW)  980702
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_System
(
    id          ID              NOT NULL UNIQUE,
    state       STATE           NOT NULL,
    name        NAME            NOT NULL,
    type        NAME            NULL,
    value       NVARCHAR (255)  NULL
)
GO
-- ibs_System
