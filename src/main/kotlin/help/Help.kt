package help

fun showHelp(header: String? = null) {
    val salamr = "salamr"
    header?.let {
        println()
        println(header)
        println()
    }
    println(
        """
             -l (Locate) - Locate and Tap single item - Usage: ~$$salamr -l Home"
            
             -m (Multi Locate)- Locate and Tap multiple item in a sequence - Usage: ~\$$salamr -m Account,Theme,Dark (avoid extra spaces)
            
             -r (Record)- Record inputs on emulator - Usage: ~\$$salamr -r
            
             -p (Play)- Play recorded inputs on emulator - Usage: ~\$$salamr -p
            
        """.trimIndent()
    )
}