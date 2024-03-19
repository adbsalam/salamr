package core

import java.io.BufferedReader
import java.io.InputStreamReader

fun readTextFileFromResources(fileName: String): String {
    val inputStream = object {}.javaClass.getResourceAsStream("/$fileName")
        ?: throw IllegalArgumentException("File not found: $fileName")

    val reader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        stringBuilder.appendln(line)
    }
    reader.close()

    return stringBuilder.toString()
}