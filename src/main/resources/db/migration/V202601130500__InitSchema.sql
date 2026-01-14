CREATE TABLE traffic_short_term_data
(
    start_time   TIMESTAMP        NOT NULL,
    permanent_id VARCHAR(50)      NOT NULL,
    q_kfz        BIGINT,
    q_lkw        BIGINT,
    q_pkw        BIGINT,
    v_kfz        DOUBLE PRECISION,
    v_pkw        DOUBLE PRECISION,
    v_lkw        DOUBLE PRECISION,
    stt          SMALLINT DEFAULT 0,
    CONSTRAINT traffic_short_term_data_pkey
        PRIMARY KEY (start_time, permanent_id)
); 


COMMENT ON COLUMN traffic_short_term_data.stt IS 'Short-term data status: 0 - good, 1 - missing, 2 - implausible, ...';
