package core.data

/**
 * names of events types to capture
 * these enums should be the same name as event log types from adb event logs
 */
enum class EventLogType {
    ABS_MT_POSITION_X, ABS_MT_POSITION_Y, ABS_MT_TRACKING_ID, OTHER
}