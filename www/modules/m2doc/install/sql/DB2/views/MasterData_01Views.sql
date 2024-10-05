------------------------------------------------------------------------------
-- All views regarding a master data container. <BR>
-- 
-- @version     $Id: MasterData_01Views.sql,v 1.4 2003/10/31 16:29:04 klaus Exp $
--
-- @author      Marcel Samek (MS)  020816
------------------------------------------------------------------------------

    -- Gets the data of the objects within a given container (incl. rights). 
    -- This view returns if the user has already read the object and the name of 
    -- the owner. <BR>
    -- It also returns the email address associated to the person or company.

    -- delete existing view: 
CALL IBSDEV1.p_dropView ('V_MASTERDATACONTAINER_01$CONT');

    -- create the new view: 
CREATE VIEW IBSDEV1.v_MasterDataContainer_01$cont  
AS      
    SELECT  o.*, c.email, c.compowner      
    FROM    (                  
             SELECT v.*,                           
             CASE 
                WHEN isLink = 0 
                THEN oid 
                ELSE linkedObjectId 
             END AS joinOid                  
             FROM    IBSDEV1.v_Container$content v 
             ) o,              
             (
             SELECT oid, '' AS compowner, offemail AS email
             FROM   IBSDEV1.mad_Person_01 
             UNION
             SELECT f.oid, f.owner AS compowner, a.email AS email
             FROM   IBSDEV1.mad_Company_01 f, 
                 IBSDEV1.ibs_Object b, IBSDEV1.m2_Address_01 a 
             WHERE  b.containerId = f.oid
             AND    b.containerKind = 2
             AND    b.tVersionId = 16854785
             AND    a.oid = b.oid
             UNION
             SELECT oid, '' AS compowner, '' AS email
             FROM   IBSDEV1.ibs_Object
             WHERE  tVersionId IN 
                    (SELECT tVersionId FROM IBSDEV1.ibs_DocumentTemplate_01)
             ) c      
             WHERE c.oid = o.joinOid;
    -- v_MasterDataContainer_01$cont