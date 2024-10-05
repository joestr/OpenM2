DECLARE @l_oid OBJECTID,
        @l_name NAME,
        @l_owner USERID,
        @l_outoid OBJECTID

DECLARE documentCursor INSENSITIVE CURSOR FOR
    SELECT o.oid, o.name, o.owner
    FROM   ibs_type t, ibs_tversion tv, ibs_object o
    WHERE  t.code = 'Document'
      AND  t.id = tv.typeid
      AND  o.tversionid = tv.id
      AND  o.state = 2

BEGIN
    -- insert the target and the container of the copied object
    OPEN documentCursor
    --
    -- get first object to be copied:
    FETCH NEXT FROM documentCursor INTO @l_oid, @l_name, @l_owner
    --
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        --
        -- read and create entries for new object

    EXEC p_Object$createTab @l_owner, 0x00000001, @l_oid, 'm2_VersionContainer', @l_outoid OUTPUT

        -- get next object to be copied:
        FETCH NEXT FROM documentCursor INTO @l_oid, @l_name, @l_owner
    END -- while

    -- dump cursor structures
    CLOSE documentCursor
    DEALLOCATE documentCursor

END
