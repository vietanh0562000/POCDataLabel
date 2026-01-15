CREATE TABLE mq_aggregate_15m (
    id BIGSERIAL PRIMARY KEY,
    mq_id VARCHAR(255) NOT NULL,
    time_bucket TIMESTAMP NOT NULL,
    q_kfz_sum INTEGER,
    q_lkw_sum INTEGER,
    q_pkw_sum INTEGER,
    v_kfz_weighted_avg NUMERIC(10, 2),
    v_lkw_weighted_avg NUMERIC(10, 2),
    v_pkw_weighted_avg NUMERIC(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT mq_aggregate_15m_mq_id_time_bucket_unique
        UNIQUE (mq_id, time_bucket)
);
