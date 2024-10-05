/******************************************************************************
 *
 * The table contains the values for the object
 * TabTemplate_01.
 * 
 *
 * @version         1.10.0001, 04.08.1998
 *
 * @author      Michael Steiner (MS)  001004
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_TabTemplate_01
(
    oid             OBJECTID        NOT NULL,
    id              INT,
    kind            INT,            -- 1 = View, 2 = Object, 3 = Link, 4 = Function
    tVersionId      TVERSIONID,     -- the tVersionId of the tab object (for kind = 2)
    fct             INT,            -- the function of the tab
    priority        INT,            -- the priority of the tab
    name            NAME,           -- the name of the tab
    description     DESCRIPTION,    -- the description of the tab
    tabCode         NAME,           -- the tab code
    class           DESCRIPTION     -- the class to show the view tab
)
GO
-- ibs_TabTemplate_01
