/******************************************************************************
 *
 * The ibs_Help_01 indexes and triggers. <BR>
 * The ibs_Help_01 table contains the values for the base object Help_01.
 * 
 *
 * @version         1.10.0001, 03.08.1999
 *
 * @author          Mario Stegbauer (MS)  990706
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803	Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_Help_01
(
    oid              OBJECTID       NOT NULL UNIQUE,
    searchContent    NVARCHAR (255) NULL,
    goal             NTEXT          NULL,
    helpUrl          NVARCHAR (255)
)
GO
-- ibs_Help_01

