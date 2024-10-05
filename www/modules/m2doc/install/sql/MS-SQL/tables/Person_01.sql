/******************************************************************************
 * The MAD_PERSON_01 table . <BR>
 * 
 * @version     $Id: Person_01.sql,v 1.5 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */


CREATE TABLE mad_Person_01
(
    oid         OBJECTID        NOT NULL UNIQUE,
    fullname    NAME            NOT NULL,
    prefix      NVARCHAR(15)    NOT NULL,     
    title       NVARCHAR(31)    NOT NULL,
    position    NVARCHAR(31)    NOT NULL,
    company     NVARCHAR(63)    NOT NULL,
    offemail    NVARCHAR(127)   NOT NULL,
    offhomepage NVARCHAR(255)   NOT NULL,
    userOid     OBJECTID        NOT NULL
)
GO
