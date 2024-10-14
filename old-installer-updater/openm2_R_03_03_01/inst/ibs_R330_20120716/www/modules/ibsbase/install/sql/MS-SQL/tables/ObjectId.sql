/******************************************************************************
 * The ibs object id table incl. indexes. <BR>
 * The object id table contains only one value for the id of the next object.
 *
 * @version     $Id: ObjectId.sql,v 1.2 2005/03/07 15:56:31 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  20050302
 ******************************************************************************
 */
CREATE TABLE ibs_ObjectId
(
    id          ID              IDENTITY (1000001, 1) -- the object id
)
GO
-- ibs_ObjectId
 
-- ibs_ObjectId indexes
CREATE UNIQUE INDEX I_ObjectIdId ON ibs_ObjectId (id)
GO
