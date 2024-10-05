/******************************************************************************
 * The ibs_FunctionHandler indexes and triggers. <BR>
 * The ibs_FunctionHandler table contains all function which are registered
 * throughout the system.
 * 
 * @version     $Id: FunctionHandler.sql,v 1.1 2003/11/16 00:44:43 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)    031115
 ******************************************************************************
 */
CREATE TABLE ibs_FunctionHandler
(
    id              ID              NOT NULL PRIMARY KEY,
    className       NAME            NOT NULL    -- class which implements the
                                                -- business logic of the function
                                                -- handler
)
GO
-- ibs_FunctionHandler
