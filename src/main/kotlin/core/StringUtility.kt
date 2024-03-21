package core

fun String.removeBrackets() = this.replace("(", "").replace(")", "")
fun String.containsOptions() = this.contains("(") && this.contains(")")