/******************************************************************************
 * Task:        IBS-71 - New VALUE type VALUEDOMAIN with new 
 *				attribute context.
 * 
 * Description: All tables regarding the value domain. <BR>
 *
 * Repeatable:  yes
 *
 * @version     $Id: v_getValueDomain.sql,v 1.1 2008/07/24 14:57:51 btatzmann Exp $
 *
 * @author      Christa Tran (CT)  080215
 *
 ******************************************************************************
 */

-- delete existing view:
EXEC p_dropView 'v_getValueDomain'
GO

-- create the new view:
CREATE VIEW v_getValueDomain
AS
	-- Get value domain objects.
    -- Attention: The form templates for object valueDomain and 
    -- valueDomainElement has to be installed first!
    
-- SELECT --
SELECT
-- Value Domain Element
ovde.oid,
ovde.name as value,
vde.m_ordercrit as orderCrit,
-- Value Domain
ovd.name as context

-- FROM --
FROM
-- Value Domain Element
ibs_Object ovde, dbm_valuedomainelem vde,
-- Value Domain
ibs_Object ovd

-- WHERE --
WHERE
-- Value Domain
ovd.state = 2 AND
ovd.isLink = 0 AND
ovd.containerkind = 1 AND
-- Value Domain has Value Domain Elements
ovd.oid = ovde.containerid AND
-- Value Domain Element
ovde.oid = vde.oid AND
ovde.state = 2 AND
ovde.isLink = 0 AND
ovde.containerkind = 1

GO
-- v_getValueDomain
