package core

fun String.replaceBrackets() = this.replace("(", "").replace(")", "")
fun String.containsOptions() = this.contains("(") && this.contains(")")