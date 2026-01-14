ALTER TABLE traffic_short_term_data
DROP COLUMN stt;

ALTER TABLE traffic_short_term_data
ADD COLUMN stt_q_kfz SMALLINT DEFAULT 0;
ALTER TABLE traffic_short_term_data
ADD COLUMN stt_q_pkw SMALLINT DEFAULT 0;
ALTER TABLE traffic_short_term_data
ADD COLUMN stt_q_lkw SMALLINT DEFAULT 0;
ALTER TABLE traffic_short_term_data
ADD COLUMN stt_v_kfz SMALLINT DEFAULT 0;
ALTER TABLE traffic_short_term_data
ADD COLUMN stt_v_pkw SMALLINT DEFAULT 0;
ALTER TABLE traffic_short_term_data
ADD COLUMN stt_v_lkw SMALLINT DEFAULT 0;