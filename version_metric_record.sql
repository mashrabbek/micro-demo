CREATE OR REPLACE PROCEDURE version_metric_record(
    p_rec_id IN NUMBER,
    p_metric_id IN VARCHAR2
) IS
    v_current_version VARCHAR2(10);
    v_new_version VARCHAR2(10);
    v_active_count NUMBER;
BEGIN
    -- Check if there's an active record with the same metric_id
    SELECT COUNT(*)
    INTO v_active_count
    FROM metric_records
    WHERE metric_id = p_metric_id
    AND status = 'active';

    IF v_active_count > 0 THEN
        -- Get the current version of the active record
        SELECT version
        INTO v_current_version
        FROM metric_records
        WHERE metric_id = p_metric_id
        AND status = 'active';

        -- Increment version number
        IF v_current_version LIKE 'v%' THEN
            -- Extract the number part and increment it
            v_new_version := 'v' || (TO_NUMBER(SUBSTR(v_current_version, 2)) + 1);
        ELSE
            v_new_version := 'v1';
        END IF;

        -- Inactivate the old record
        UPDATE metric_records
        SET status = 'inactive'
        WHERE metric_id = p_metric_id
        AND status = 'active';

        -- Update the current record with new version and activate it
        UPDATE metric_records
        SET version = v_new_version,
            status = 'active'
        WHERE rec_id = p_rec_id;
    ELSE
        -- No active record found, set version to v1 and activate
        UPDATE metric_records
        SET version = 'v1',
            status = 'active'
        WHERE rec_id = p_rec_id;
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Error in version_metric_record: ' || SQLERRM);
END version_metric_record;
/ 