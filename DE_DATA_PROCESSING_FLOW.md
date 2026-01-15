# DE Data Processing Flow

[[_TOC_]]

  

## Overview

  

This document describes the data processing flow for DE (Data Element) traffic data in the data assessment system. The flow starts with a Kafka consumer receiving traffic data events and processes them through three main use cases to ensure data quality and validity.

  

## Architecture Diagram

  

```

┌─────────────────────────────────────────────────────────────────┐

│                      Kafka Topic                                 │

│        "gap.traffic-data.short-term-data-ingested"              │

└────────────────────────────┬────────────────────────────────────┘

                             │

                             │ UpdateDeEvent

                             ▼

                  ┌──────────────────────┐

                  │    DeConsumer        │

                  │  @KafkaListener      │

                  └──────────┬───────────┘

                             │

                ┌────────────┼────────────┐

                │            │            │

                ▼            ▼            ▼

    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐

    │   Upsert     │  │ CheckZeros   │  │ CheckValid   │

    │ DESupport    │  │   Daily      │  │   Daily      │

    │   Point      │  │              │  │              │

    └──────┬───────┘  └──────┬───────┘  └──────┬───────┘

           │                 │                 │

           ▼                 ▼                 ▼

    DeSupportPoint    DeDailyChart      DeDailyChart

       _15m              Status             Status

```

  

## Flow Components

  

### 1. DeConsumer (Entry Point)

  

**File:** `DeConsumer.java`

  

**Purpose:** Kafka consumer that receives traffic data ingestion events

  

**Key Details:**

- **Topic:** `gap.traffic-data.short-term-data-ingested`

- **Event Type:** `UpdateDeEvent` containing:

  - `permanentId`: Traffic station identifier

  - `timeBucket`: Timestamp of the 15-minute interval

  

**Processing Steps:**

```java

1. Receive UpdateDeEvent from Kafka

2. Execute UpsertDESupportPointUseCase

3. Execute CheckZerosDailyDEUseCase

4. Execute CheckValidDailyDEUseCase

```

  

---

  

### 2. UpsertDESupportPointUseCase

  

**File:** `UpsertDESupportPointUseCase.java`

  

**Purpose:** Creates or updates support point records for 15-minute traffic intervals

  

**Business Logic:**

  

1. **Find or Create Support Point**

   - Searches for existing `DeSupportPoint_15m` record by `permanentId` and `timeBucket`

   - Creates new record if not found, initialized with zero values

  

2. **Calculate Traffic Counts**

   - Queries `TrafficShortTermData` within a 15-minute window

   - Counts include: implausible, missing, and total records for each metric

  

3. **Determine and Store Status Codes**

   - Calculates status codes for 6 traffic metrics (does NOT store actual traffic values):

     - `qKfz` - Total vehicle flow (quantity)

     - `qPkw` - Passenger car flow

     - `qLkw` - Truck flow

     - `vKfz` - Total vehicle velocity

     - `vPkw` - Passenger car velocity

     - `vLkw` - Truck velocity

  

4. **Determine Status Code** (for each metric)

   - **COMPLETED** (status = 0): When data is sufficient and plausible

   - **IMPLAUSIBLE** (status = 1): When any implausible data exists

   - **MISSING** (status = 2): When total count < 15 or missing data exists

**Key Point:** The support point table contains ONLY status codes (0, 1, or 2), not the actual traffic flow or velocity values. It acts as a quality assessment summary.

  

**Constants:**

- `MIN_DATA_COUNT = 15` (expected records per 15-min window)

- `TIME_WINDOW_SECONDS = 900` (15 minutes)

  

---

  

### 3. CheckZerosDailyDEUseCase

  

**File:** `CheckZerosDailyDEUseCase.java`

  

**Purpose:** Detects consecutive zero values in daily traffic data that may indicate sensor or communication issues

  

**Business Logic:**

  

1. **Retrieve Data**

   - Fetches all aggregated 15-minute traffic data for the given date and permanentId

   - Retrieves or creates `DeDailyChartStatus` record

  

2. **Track Consecutive Zeros**

   - Uses `ConsecutiveZeroTracker` to count sequential zero values

   - Tracks all 6 metrics independently

   - Resets counter when non-zero value detected

  

3. **Handle Time Gaps**

   - Detects gaps in 15-minute intervals

   - Resets all counters when gap detected (data discontinuity)

  

4. **Update Daily Status**

   - Sets `*ZerosValid = false` if consecutive zeros ≥ threshold

   - Example: `qKfzZerosValid`, `vPkwZerosValid`, etc.

  

**Constants:**

- `EXPECTED_INTERVAL = 15 minutes`

- `CONSECUTIVE_ZERO_THRESHOLD` from `DataConst`

  

**Metrics Checked:**

- Sum values: `qKfzSum`, `qPkwSum`, `qLkwSum`

- Weighted averages: `vKfzWeightedAvg`, `vPkwWeightedAvg`, `vLkwWeightedAvg`

  

---

  

### 4. CheckValidDailyDEUseCase

  

**File:** `CheckValidDailyDEUseCase.java`

  

**Purpose:** Validates daily support point data based on consecutive and total invalid counts

  

**Business Logic:**

  

1. **Retrieve Data**

   - Fetches all `DeSupportPoint_15m` records for the date and permanentId

   - Retrieves or creates `DeDailyChartStatus` record

  

2. **Track Invalid Statuses**

   - Uses two trackers:

     - **ConsecutiveInvalidTracker**: Counts sequential invalid statuses

     - **TotalInvalidTracker**: Counts all invalid statuses for the day

   - Invalid = MISSING or IMPLAUSIBLE status

  

3. **Handle Time Gaps**

   - Detects gaps in 15-minute intervals

   - Resets consecutive counter (but NOT total counter)

  

4. **Update Daily Status**

   - Sets `*IsValid = false` if:

     - Consecutive invalid count ≥ `CONSECUTIVE_ZERO_THRESHOLD`

     - OR Total invalid count ≥ `TOTAL_ZERO_THRESHOLD`

  

**Constants:**

- `EXPECTED_INTERVAL = 15 minutes`

- `CONSECUTIVE_ZERO_THRESHOLD` from `DataConst`

- `TOTAL_ZERO_THRESHOLD` from `DataConst`

  

**Validation Logic:**

- Consecutive: Detects prolonged data quality issues

- Total: Ensures overall daily data quality

  

---

  

## Complete Example Scenario

  

### Scenario: Traffic Station Processing on 2026-01-15

  

**Initial Event:**

```json

{

  "permanentId": "DE-A1-KM150",

  "timeBucket": "2026-01-15T14:30:00"

}

```

  

### Step 1: UpsertDESupportPointUseCase

  

**Input:**

- Event: `permanentId=DE-A1-KM150`, `timeBucket=2026-01-15T14:30:00`

  

**Processing:**

1. Find existing support point for 14:30 interval → Not found

2. Create new `DeSupportPoint_15m` record

3. Query traffic counts for 14:30-14:45 window:

   ```

   qKfz: implausible=0, missing=2, total=13

   qPkw: implausible=0, missing=2, total=13

   qLkw: implausible=1, missing=0, total=14

   vKfz: implausible=0, missing=0, total=15

   vPkw: implausible=0, missing=0, total=15

   vLkw: implausible=0, missing=0, total=15

   ```

  

4. Determine statuses:

   - `qKfzStt = MISSING (2)` - total < 15

   - `qPkwStt = MISSING (2)` - total < 15

   - `qLkwStt = IMPLAUSIBLE (1)` - has implausible data

   - `vKfzStt = COMPLETED (0)` - all good

   - `vPkwStt = COMPLETED (0)` - all good

   - `vLkwStt = COMPLETED (0)` - all good

  

**Result:**

```sql

INSERT INTO de_support_point_15m VALUES (

  '2026-01-15 14:30:00', -- start_time (PK)

  'DE-A1-KM150',         -- permanent_id (PK)

  'MQ-123',              -- mq_id

  2,                     -- qKfzStt (MISSING)

  2,                     -- qPkwStt (MISSING)

  1,                     -- qLkwStt (IMPLAUSIBLE)

  0,                     -- vKfzStt (COMPLETED)

  0,                     -- vPkwStt (COMPLETED)

  0                      -- vLkwStt (COMPLETED)

);

```

  

**Important:** This table stores only status codes, not the actual traffic measurements. The status indicates the quality assessment of the underlying traffic data from `TrafficShortTermData`.

  

---

  

### Step 2: CheckZerosDailyDEUseCase

  

**Input:**

- Date: `2026-01-15`

- PermanentId: `DE-A1-KM150`

- Threshold: `6` consecutive zeros

  

**Processing:**

1. Retrieve all 15-min aggregated data for the day (96 intervals)

2. Iterate through intervals tracking consecutive zeros:

  

```

Time        qKfzSum  qPkwSum  qLkwSum  vKfzAvg  Tracker

00:00       1250     1000     250      85.5     qKfz=0, qPkw=0, qLkw=0

00:15       1180     950      230      83.2     qKfz=0, qPkw=0, qLkw=0

...

13:15       0        0        0        0        qKfz=1, qPkw=1, qLkw=1, vKfz=1

13:30       0        0        0        0        qKfz=2, qPkw=2, qLkw=2, vKfz=2

...

14:30       0        0        0        0        qKfz=5, qPkw=5, qLkw=5, vKfz=5

14:45       0        0        0        0        qKfz=6, qPkw=6, qLkw=6, vKfz=6 ⚠️ THRESHOLD!

15:00       0        0        0        0        qKfz=7, qPkw=7, qLkw=7, vKfz=7

15:15       1420     1100     320      90.1     qKfz=0, qPkw=0, qLkw=0, vKfz=0 (RESET)

...

```

  

3. At 14:45, consecutive zeros reach threshold (6)

4. Update daily status flags:

   ```java

   qKfzZerosValid = false

   qPkwZerosValid = false

   qLkwZerosValid = false

   vKfzZerosValid = false

   ```

  

**Result:**

```sql

UPDATE de_daily_chart_status

SET q_kfz_zeros_valid = false,

    q_pkw_zeros_valid = false,

    q_lkw_zeros_valid = false,

    v_kfz_zeros_valid = false

WHERE date = '2026-01-15'

  AND permanent_id = 'DE-A1-KM150';

```

  

---

  

### Step 3: CheckValidDailyDEUseCase

  

**Input:**

- Date: `2026-01-15`

- PermanentId: `DE-A1-KM150`

- Consecutive Threshold: `6`

- Total Threshold: `12`

  

**Processing:**

1. Retrieve all support points for the day

2. Track invalid statuses:

  

```

Time   qKfzStt  qPkwStt  qLkwStt  ConsecInvalid  TotalInvalid

00:00  0        0        0        qKfz=0         qPkw=0

00:15  0        0        0        qKfz=0         qPkw=0

...

08:00  2        2        1        qKfz=1         qPkw=1  (MISSING/IMPLAUSIBLE)

08:15  2        0        1        qKfz=2         qPkw=2

08:30  0        0        0        qKfz=0         qPkw=2  (RESET consecutive)

...

14:00  2        2        2        qKfz=3         qPkw=2

14:15  2        2        2        qKfz=4         qPkw=2

...

14:30  2        2        1        qKfz=5        qPkw=10

14:45  2        2        1        qKfz=6 ⚠️     qPkw=11 

15:00  0        0        0        qKfz=0         qPkw=12 ⚠️

...

```

  

3. At 14:45:
   - Consecutive invalid reaches 6 → Flag invalid qKfz

3. At 15:00:
   - Total invalid reaches 12 -> Flag invalid qPkw

  
4. Update daily status:
   qKfzIsValid = false
   qPkwIsValid = false


  

**Result:**

```sql

UPDATE de_daily_chart_status

SET q_kfz_is_valid = false,

    q_pkw_is_valid = false

WHERE date = '2026-01-15'

  AND permanent_id = 'DE-A1-KM150';

```

  

---

  

## Final Daily Status

  

After processing all events for `DE-A1-KM150` on `2026-01-15`:

  

```sql

de_daily_chart_status:

├── date: 2026-01-15

├── permanent_id: DE-A1-KM150

├── q_kfz_is_valid: false        (consecutive invalid exceeded)

├── q_kfz_zeros_valid: false     (consecutive zeros exceeded)

├── q_pkw_is_valid: false (total invalid exceeded)

├── q_pkw_zeros_valid: false

├── q_lkw_is_valid: true

├── q_lkw_zeros_valid: false

├── v_kfz_is_valid: true         (velocities were mostly valid)

├── v_kfz_zeros_valid: false

├── v_pkw_is_valid: true

├── v_pkw_zeros_valid: true

├── v_lkw_is_valid: true

└── v_lkw_zeros_valid: true

```

  

---

  

## Key Concepts

  

### Support Point Status Enum

  

```java

public enum SupportPointStatus {

    COMPLETED,    // ordinal = 0, data is valid and complete

    IMPLAUSIBLE,  // ordinal = 1, data quality issue detected

    MISSING       // ordinal = 2, insufficient or missing data

}

```

  

### Traffic Metrics

  

| Metric | Description | Type |

|--------|-------------|------|

| qKfz | Total vehicle flow (Quantity Kraftfahrzeug) | Sum |

| qPkw | Passenger car flow (Quantity Personenkraftwagen) | Sum |

| qLkw | Truck flow (Quantity Lastkraftwagen) | Sum |

| vKfz | Total vehicle velocity | Weighted Avg |

| vPkw | Passenger car velocity | Weighted Avg |

| vLkw | Truck velocity | Weighted Avg |

  

### Time Intervals

  

- **15-minute windows**: 96 intervals per day (00:00-00:15, 00:15-00:30, ..., 23:45-00:00)

- **Expected data points**: 15 per window (1-minute sampling rate)

- **Time gap detection**: Any interval not exactly 15 minutes apart

  

### Validation Rules

  

1. **Support Point Validation (Per 15-min interval)**

   - Implausible if any invalid data exists

   - Missing if < 15 data points or gaps detected

   - Completed otherwise

  

2. **Zero Value Detection (Daily)**

   - Flags data when consecutive zeros ≥ threshold

   - Indicates potential sensor failure or communication loss

  

3. **Daily Validity Check (Daily)**

   - Consecutive: Detects prolonged issues

   - Total: Ensures minimum daily data quality

   - Both must pass for valid day

  

---

  

## Database Tables

  

### DeSupportPoint_15m

**Purpose:** Stores quality status flags for each traffic metric (no actual traffic values)

  

```sql

- start_time (PK)

- permanent_id (PK)

- q_kfz_stt (status: 0=Completed, 1=Implausible, 2=Missing)

- q_pkw_stt (status: 0=Completed, 1=Implausible, 2=Missing)

- q_lkw_stt (status: 0=Completed, 1=Implausible, 2=Missing)

- v_kfz_stt (status: 0=Completed, 1=Implausible, 2=Missing)

- v_pkw_stt (status: 0=Completed, 1=Implausible, 2=Missing)

- v_lkw_stt (status: 0=Completed, 1=Implausible, 2=Missing)

```

  

**Note:** This table contains only status codes, not actual traffic measurements. The actual traffic data resides in `TrafficShortTermData` and `TrafficAggregatedData_15m` tables.

  

### DeDailyChartStatus

```sql

- date (PK)

- permanent_id (PK)

- q_kfz_is_valid (boolean)

- q_kfz_zeros_valid (boolean)

- q_pkw_is_valid (boolean)

- q_pkw_zeros_valid (boolean)

- q_lkw_is_valid (boolean)

- q_lkw_zeros_valid (boolean)

- v_kfz_is_valid (boolean)

- v_kfz_zeros_valid (boolean)

- v_pkw_is_valid (boolean)

- v_pkw_zeros_valid (boolean)

- v_lkw_is_valid (boolean)

- v_lkw_zeros_valid (boolean)

```

  

---

  

## Error Scenarios

  

### Scenario 1: Sensor Communication Loss

- **Symptom**: Extended consecutive zeros

- **Detection**: CheckZerosDailyDEUseCase

- **Result**: `*ZerosValid = false`

  

### Scenario 2: Intermittent Data Loss

- **Symptom**: Many MISSING statuses throughout day

- **Detection**: CheckValidDailyDEUseCase (Total tracker)

- **Result**: `*IsValid = false`

  

### Scenario 3: Calibration Issue

- **Symptom**: Many IMPLAUSIBLE statuses

- **Detection**: UpsertDESupportPointUseCase + CheckValidDailyDEUseCase

- **Result**: `*IsValid = false`

  

### Scenario 4: Network Outage

- **Symptom**: Time gaps in data

- **Detection**: All use cases (time gap detection)

- **Result**: Tracker resets, separate evaluation of segments

  

---

  

## Performance Considerations

  

1. **Batch Processing**: Get batch data from Kafka to process


  

## Configuration

  

**Constants in DataConst.java:**

```java

CONSECUTIVE_ZERO_THRESHOLD = 6  // 90 minutes of consecutive issues

TOTAL_ZERO_THRESHOLD = 12        

```

  

---

  

## Summary

  

This flow ensures comprehensive data quality assessment through:

1. **Real-time validation** at 15-minute intervals

2. **Pattern detection** for consecutive issues

3. **Daily aggregation** for overall quality metrics

4. **Multiple validation dimensions** (zeros, missing, implausible)

  

The system provides both granular (15-min) and aggregate (daily) views of data quality, enabling quick identification of sensor, communication, or data quality issues.