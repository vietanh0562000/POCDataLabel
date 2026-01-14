CREATE TABLE de_daily_chart_status(
day_date        DATE          NOT NULL,
permanent_id    VARCHAR(50)   NOT NULL,

-- 1. General Validity Status (Per Attribute) 
-- Checks Rule 4: Are there too many bad/missing points in this specific curve? 
-- TRUE = Valid (Show Chart), FALSE = Invalid (Hide/Error) 
q_kfz_is_valid BOOLEAN DEFAULT TRUE, 
q_lkw_is_valid BOOLEAN DEFAULT TRUE, 
q_pkw_is_valid BOOLEAN DEFAULT TRUE, 
v_kfz_is_valid BOOLEAN DEFAULT TRUE, 
v_pkw_is_valid BOOLEAN DEFAULT TRUE, 
v_lkw_is_valid BOOLEAN DEFAULT TRUE, 

-- 2. Zero-Line Check Status (Per Attribute) 
-- Checks: Is the data implausibly stuck at zero for this specific curve? 
-- TRUE = Passed (Valid), FALSE = Failed (Stuck at zero) 
q_kfz_zeros_valid BOOLEAN DEFAULT TRUE, 
q_lkw_zeros_valid BOOLEAN DEFAULT TRUE, 
q_pkw_zeros_valid BOOLEAN DEFAULT TRUE, 
v_kfz_zeros_valid BOOLEAN DEFAULT TRUE, 
v_pkw_zeros_valid BOOLEAN DEFAULT TRUE, 
v_lkw_zeros_valid BOOLEAN DEFAULT TRUE,

-- Metadata
updated_at      TIMESTAMP     DEFAULT NOW(),

CONSTRAINT de_daily_chart_status_pkey
    PRIMARY KEY (day_date, permanent_id));

COMMENT ON TABLE de_daily_chart_status IS 'Stores the final validity of a DE daily line chart based on aggregated errors.';
COMMENT ON COLUMN de_daily_chart_status.q_kfz_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.q_lkw_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.q_pkw_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.v_kfz_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.v_pkw_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.v_lkw_is_valid IS 'FALSE if >12 bad points total OR >6 bad points in a row (Rule 4).';
COMMENT ON COLUMN de_daily_chart_status.q_kfz_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';
COMMENT ON COLUMN de_daily_chart_status.q_lkw_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';
COMMENT ON COLUMN de_daily_chart_status.q_pkw_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';
COMMENT ON COLUMN de_daily_chart_status.v_kfz_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';
COMMENT ON COLUMN de_daily_chart_status.v_pkw_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';
COMMENT ON COLUMN de_daily_chart_status.v_lkw_zeros_valid IS 'FALSE if the data is implausibly stuck at zero for this specific curve.';