/******************************************************************************
 * The table contains the values for the object TabTemplate_01.
 * 
 * @version     2.21.0002, 25.06.2002 KR
 *
 * @author      Michael Steiner (MS)  001004
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_TabTemplate_01
(
    oid             RAW (8)         NOT NULL,
    id              INTEGER         DEFAULT (0),
    kind            INTEGER         DEFAULT (0),    -- 1 = View, 2 = Object,
                                                    -- 3 = Link, 4 = Function
    tVersionId      INTEGER         DEFAULT (0),    -- the tVersionId of the tab
                                                    -- object (for kind = 2)
    fct             INTEGER         DEFAULT (0),    -- the function of the tab
    priority        INTEGER         DEFAULT (0),    -- the priority of the tab
    name            VARCHAR2 (63),                  -- the name of the tab
    description     VARCHAR2 (255),                 -- description of the tab
    tabCode         VARCHAR2 (63),                  -- the tab code
    class           VARCHAR2 (255)                  -- the class to show the
                                                    -- view tab
) /*TABLESPACE*/;

EXIT;