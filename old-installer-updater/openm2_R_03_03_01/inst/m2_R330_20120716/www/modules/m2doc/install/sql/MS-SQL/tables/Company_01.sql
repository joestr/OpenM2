 
/* Company_01 */


CREATE TABLE mad_Company_01
(
oid         OBJECTID        NOT NULL UNIQUE,
owner       NAME            NOT NULL,
manager     NAME            NOT NULL,
legal_form  NVARCHAR(63)    NOT NULL,
mwst        INT             NOT NULL
)
GO
