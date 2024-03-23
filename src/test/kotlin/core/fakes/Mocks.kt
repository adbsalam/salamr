package core.fakes

import core.data.ScreenResolutions
import core.data.UserInput
import core.readTextFileFromResources

val mockEventLogs = readTextFileFromResources("adb/eventLog.txt").split("/n")

val mockWindowDump = readTextFileFromResources("adb/dump.xml")

val mockScreenResolutions = ScreenResolutions(1440, 3320)

val mockUserInputList = listOf(
    UserInput.Swipe(599, 1951, 599, 1315, 145),
    UserInput.Tap(57, 23)
)

const val recordedInputJsonFile =
    "[{\"swipe\":{\"startX\":599,\"startY\":1951,\"endX\":599,\"endY\":1315,\"duration\":145}},{\"tap\":{\"x\":57,\"y\":23}}]"

