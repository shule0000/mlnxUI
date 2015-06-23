package com.medlinx.core.constant;

public class HttpConstants {

    public static final int DEFAULT_HTTP_PORT = 8080;

    public static final int DEFAULT_HTTPS_PORT = 8443;

    public static final String REAL_TIME_ECG_CONTENT_TYPE = "application/x-mlnx-rtecg";

    public static final String PATIENT_ID_HEADER = "X-MLNX-Patient-ID";

    public static final String ECG_LEAD_HEADER = "X-MLNX-ECG-Lead";

    public static final String ECG_TOKEN_HEADER = "X-MLNX-ECG-Token";

    public static final String ECG_COUNT_HEADER = "X-MLNX-ECG-Count";

    /**
     * Value is an int. Its lower 10 bits represent the connection status of
     * each of the 10 electrodes (0: connected, 1: disconnected). See the
     * definition of the "Electrode Impedance Bits (CxIMP)" field in the VSTP
     * Specification.
     */
    public static final String ECG_ELECTRODE_HEADER = "X-MLNX-ECG-Electrode";

    public static final String HEART_RATE_HEADER = "X-MLNX-Heart-Rate";

    public static final String ACCELERATION_HEADER = "X-MLNX-Acceleration";
    
    public static final String CALL_TIME_HEADER = "X-MLNX-Call-Time";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";


    /**
     * Value is an int (between 0 and 100). It represents the percentage of
     * battery remaining. See the definition of the "Battery Remaining (BATT)"
     * field in the VSTP Specification.
     */
    public static final String BATTERY_REMAINING_HEADER = "X-MLNX-Battery-Remaining";

    /**
     * Value is an int (between 0 and 255). See the definition of the
     * "Wi-Fi Signal Strength" field in the VSTP Specification.
     */
    public static final String SIGNAL_STRENGTH_HEADER = "X-MLNX-Signal-Strength";
}
