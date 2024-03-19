package core.fakes

import core.data.ScreenResolutions
import core.readTextFileFromResources

val mockEventLogs = readTextFileFromResources("adb/eventLog.txt").split("/n")

val mockWindowDump = readTextFileFromResources("adb/dump.xml")

val mockScreenResolutions = ScreenResolutions(1440, 3320)

const val recordedInputJsonFile =
    "[{\"swipe\":{\"startX\":599,\"startY\":1951,\"endX\":599,\"endY\":1315,\"duration\":145}},{\"tap\":{\"x\":57,\"y\":23}}]"

