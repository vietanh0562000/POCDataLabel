CREATE TABLE de_support_point_15m(
start_time    TIMESTAMP    NOT NULL,
permanent_id  VARCHAR(50)  NOT NULL, -- Sensor/DE ID
mq_id         VARCHAR(50),           -- Cross-section ID (useful for Rule 3 aggregation)

-- Quality Status Flags
q_kfz_stt     SMALLINT DEFAULT 0,
q_lkw_stt     SMALLINT DEFAULT 0,
q_pkw_stt     SMALLINT DEFAULT 0,
v_kfz_stt     SMALLINT DEFAULT 0,
v_pkw_stt     SMALLINT DEFAULT 0,
v_lkw_stt     SMALLINT DEFAULT 0,
CONSTRAINT de_support_point_15m_pkey
    PRIMARY KEY (start_time, permanent_id)
);
-- Documentation for the Table
COMMENT ON TABLE de_support_point_15m IS 'Stores 15-minute aggregated traffic data with quality status flags per attribute.';

-- Documentation for Status Columns
-- Mapping Business Rules to Codes:
-- 0: Completed
-- 1: Missing (Missing Data)
-- 2: Implausible (Negative or null data)

COMMENT ON COLUMN de_support_point_15m.q_kfz_stt IS 'Status: 0=Completed, 1=Missing (insufficient data), 2=Implausible (negative/logic error)';
COMMENT ON COLUMN de_support_point_15m.q_lkw_stt IS 'Status: 0=Completed, 1=Missing, 2=Implausible';
COMMENT ON COLUMN de_support_point_15m.q_pkw_stt IS 'Status: 0=Completed, 1=Missing, 2=Implausible';
COMMENT ON COLUMN de_support_point_15m.v_kfz_stt IS 'Status: 0=Completed, 1=Missing, 2=Implausible';
COMMENT ON COLUMN de_support_point_15m.v_pkw_stt IS 'Status: 0=Completed, 1=Missing, 2=Implausible';
COMMENT ON COLUMN de_support_point_15m.v_lkw_stt IS 'Status: 0=Completed, 1=Missing, 2=Implausible';