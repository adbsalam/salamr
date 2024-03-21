package multiLocator

import actionExecutor.*
import core.*
import core.Delay.ofSeconds
import core.Interactions.*
import locator.Locator

/**
 * multi locator helps performs actions on multiple elements as a sequence
 */
class MultiLocator(
    private val locator: Locator = Locator(),
    private val actionExecutor: ActionExecutor = ActionExecutorImpl()
) {
    /**
     * Parses the given list of strings joined with "," into individual strings and performs actions on each item.
     * @param args the string list joined with ",".
     */
    fun run(args: String?) {
        if (args.isNullOrEmpty()) {
            actionExecutor.systemExit.exitWithHelp("no args passed for -l, -l requires at least 1 arg, see below for usage")
        }

        args.split("|").forEach { input ->
            Logger.log("processing input.. $input")
            val element = convertToElement(input)
            if (element != Other) {
                ofSeconds(Duration(1.0)) // delay will be handled by locator
            }
            when (element) {
                SystemBack -> actionExecutor.sendKeyEvent(keyEvent = KeyEvent.Back.input)
                DelayIn -> performDelay(input)
                SwipeDown -> performSwipe(input, Direction.UpToDown)
                SwipeUp -> performSwipe(input, Direction.DownToUp)
                SwipeRight -> performSwipe(input, Direction.RightToLeft)
                SwipeLeft -> performSwipe(input, Direction.LeftToRight)
                Coordinates -> performCustomTap(input)
                KeyCode -> performKeyCodeEvent(input)
                Other -> locator.run(input)
            }
        }
    }

    /**
     * Performs a swipe action with the specified direction.
     * @param input the input string representing the swipe action.
     * @param direction the direction of the swipe action.
     */
    private fun performSwipe(input: String, direction: Direction) {
        if (input.containsOptions()) {
            handleSwipeWithParameters(input, direction)
        } else {
            actionExecutor.swipe(SwipeAction.Directional(direction))
        }
    }

    /**
     * Handles a swipe action with parameters.
     * @param input the input string representing the swipe action with parameters.
     * @param direction the direction of the swipe action.
     */
    private fun handleSwipeWithParameters(input: String, direction: Direction) {
        try {
            // remove prefix of swipe
            val swipeOptionsString = input.removeRange(IntRange(0, 1)).removeBrackets()

            val swipeOptions = swipeOptionsString.split(",")
            if (swipeOptions.size < 4) {
                actionExecutor.systemExit.exitWithHelp("invalid options for Swipe, Swipe takes 4 values - x,y,amount,duration, usage example: SF(100,100, 1000, 500))\"")
            }
            val x = swipeOptions.first().toDoubleOrNull()
            val y = swipeOptions[1].toDoubleOrNull()
            val amount = swipeOptions[2].toDoubleOrNull()
            val duration = swipeOptions[3].toDoubleOrNull()

            if (x != null && y != null && amount != null && duration != null) {
                var endX = x
                var endY = y
                when (direction) {
                    Direction.UpToDown -> endY = y + amount
                    Direction.DownToUp -> endY = y - amount
                    Direction.LeftToRight -> endX = x - amount
                    Direction.RightToLeft -> endX = x + amount
                }

                actionExecutor.swipe(
                    actionDelay = Duration(1.0),
                    input = SwipeAction.Custom(
                        startX = x.toInt(),
                        startY = y.toInt(),
                        endX = endX.toInt(),
                        endY = endY.toInt(),
                        duration = duration.toInt()
                    )
                )
            } else {
                actionExecutor.systemExit.exitWithHelp("invalid options for Swipe, Swipe takes 4 values - x,y,amount,duration, usage example: SF(100,100, 1000, 0.5))")
            }

        } catch (e: Exception) {
            actionExecutor.systemExit.exitWithHelp("S do not have valid coordinates, usage example: S(x,y,amount,duration) such as S()")
        }
    }

    /**
     * Performs a delay action.
     * @param input the input string representing the delay action.
     */
    private fun performDelay(input: String) {
        val durationString = input.removePrefix(DelayIn.inputName).trim()
        ofSeconds(Duration(durationString.toDoubleOrNull() ?: 1.0))
    }

    /**
     * Performs a custom tap action based on the provided input string.
     * The input string should be in the format of T(x,y), where x and y are coordinates.
     * Example usage: T(100,100)
     *
     * @param input The input string representing the tap action.
     */
    private fun performCustomTap(input: String) {
        val cleanElement = input.removePrefix(Coordinates.inputName).removeBrackets()
        val elements = cleanElement.split(",")
        val x = elements.firstOrNull()?.toIntOrNull()
        val y = elements.lastOrNull()?.toIntOrNull()

        if (elements.size != 2 || x == null || y == null) {
            actionExecutor.systemExit.exitWithHelp("Element $input do not have correct values for x and Y, usage: T(x,y) - T(100,100)")
        }

        actionExecutor.tap(x, y)
    }

    /**
     * Performs a key code event based on the provided input string.
     * The input string should be in the format of K(int,int,...), where each int represents a key code.
     * Example usage: K(100,200)
     *
     * @param input The input string representing the key code event.
     */
    private fun performKeyCodeEvent(input: String) {
        val cleanInput = input.removePrefix(KeyCode.inputName).removeBrackets()
        val keyEvents = cleanInput.split(",")
        if (keyEvents.isEmpty()) {
            actionExecutor.systemExit.exitWithHelp("KeyEvent $input do not have correct values for x and Y, usage: K(int,int...) - K(100,100)")
        }

        keyEvents.forEach { keyCode ->
            val intValue = keyCode.toIntOrNull()
            intValue?.let { actionExecutor.sendKeyEvent(it) }
        }
    }

    /**
     * Converts the input string into an Interactions enum element.
     * @param inputName the input string to be converted.
     * @return the corresponding Interactions enum element.
     */
    private fun convertToElement(inputName: String): Interactions {
        return when {
            inputName isType SwipeDown -> SwipeDown
            inputName isType SwipeRight -> SwipeRight
            inputName isType SwipeLeft -> SwipeLeft
            inputName isType SwipeUp -> SwipeUp
            inputName isType DelayIn -> DelayIn
            inputName isType Coordinates -> Coordinates
            inputName isType KeyCode -> KeyCode
            else -> Interactions.entries.firstOrNull { it.inputName == inputName } ?: Other
        }
    }

    /**
     * Checks if the input string represents the specified interaction.
     * @param interactions the interaction to check against.
     * @return true if the input string starts with the interaction's input name, false otherwise.
     */
    private infix fun String.isType(interactions: Interactions): Boolean {
        val isOptionText = this.filter { it.isLetter() }
        if (isOptionText.length > 2) return false
        return this.startsWith(interactions.inputName)
    }

}