/******************************************************************************
 * The triggers for the ibs address table. <BR>
 * 
 * @version     $Id: Address_01Trig.sql,v 1.4 2006/01/19 15:56:47 klreimue Exp $
 *
 * @author      Christine Keim (CK)  9800603
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
IF EXISTS
        (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.TrigAddressInsert') 
                AND sysstat & 0xf = 8
        )
    DROP TRIGGER #CONFVAR.ibsbase.dbOwner#.TrigAddressInsert
GO


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
IF EXISTS
        (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.TrigAddressUpdate') 
                AND sysstat & 0xf = 8
        )
    DROP TRIGGER #CONFVAR.ibsbase.dbOwner#.TrigAddressUpdate
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
IF EXISTS
        (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.TrigAddressDelete') 
                AND sysstat & 0xf = 8
        )
    DROP TRIGGER #CONFVAR.ibsbase.dbOwner#.TrigAddressDelete
GO


PRINT 'Trigger für Tabelle m2_Address_01 angelegt'
GO
