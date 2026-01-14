SELECT create_hypertable('traffic_short_term_data', 'start_time');

CREATE MATERIALIZED VIEW traffic_aggregated_data_1m
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 minute', start_time) AS bucket,
    permanent_id,
    SUM(q_kfz) AS q_kfz_sum,
    SUM(q_lkw) AS q_lkw_sum,
    SUM(q_pkw) AS q_pkw_sum,
    SUM(v_kfz * q_kfz) / NULLIF(SUM(q_kfz), 0) AS v_kfz_weighted_avg,
    SUM(v_pkw * q_pkw) / NULLIF(SUM(q_pkw), 0) AS v_pkw_weighted_avg,
    SUM(v_lkw * q_lkw) / NULLIF(SUM(q_lkw), 0) AS v_lkw_weighted_avg
FROM traffic_short_term_data
GROUP BY bucket, permanent_id
WITH NO DATA;

CREATE MATERIALIZED VIEW traffic_aggregated_data_15m
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('15 minutes', start_time) AS bucket,
    permanent_id,
    SUM(q_kfz) AS q_kfz_sum,
    SUM(q_lkw) AS q_lkw_sum,
    SUM(q_pkw) AS q_pkw_sum,
    SUM(v_kfz * q_kfz) / NULLIF(SUM(q_kfz), 0) AS v_kfz_weighted_avg,
    SUM(v_pkw * q_pkw) / NULLIF(SUM(q_pkw), 0) AS v_pkw_weighted_avg,
    SUM(v_lkw * q_lkw) / NULLIF(SUM(q_lkw), 0) AS v_lkw_weighted_avg
FROM traffic_short_term_data
GROUP BY bucket, permanent_id
WITH NO DATA;