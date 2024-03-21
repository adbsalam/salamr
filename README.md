# salamr
![salamar](https://img.shields.io/badge/salamr_command_line-Experimental_1.0.2-blue)

Command line tool that can help you automate android emulator by either Passing inputs as component text or by recording inputs that salamr can repliacte

## Prerequisites
To use salamr you need to have 1 command line tool installed `adb platform tools`

To install `xmlstarlet`
```shell
brew install android-platform-tools
```

Next you need to have an `enumator running` with `application open` that you need to automate

## Installation

Clone this project in order to use slamr tool.

Once cloned, you can now use `salamr.sh`. Best practice would be to create an alias for `salamr.sh` script. In order to do so you need to edit your `~./zshrc` in root folder and add following alias

```shell
alias salamr="bash /path/to/salamr.sh"
```

To verify setup open `terminal` and run command `salamr -h`

## Usage

```shell
# list available features 'help'
salamr -h

# locate multiple items 'multiLocator'
salamr -m 'YOUR_VIEW_TEXT_HERE|YOUR_VIEW_TEXT_HERE'

# record your inputs
salamr -r

# record your inputs to a separate file
salamr -r -f 'FILE_NAME_HERE'

# play recorded inputs
salamr -p

# play recorded inputs
salamr -p 'FILE_NAME_HERE'

# output inputs from emulator
salamr -t

# delete a file pass either file name/names or "all" to delete all
salamr -d 'FILE_NAME_HERE'
salamr -d all
```

## Multi Locator element list
```shell
Element Summary:
SU - Swipe Up
SD - Swipe Down
SL - Swipe Left
SR - Swipe Right
D - Delay 
B - System Back Press
TF - Text Field
C - Custom Coordinates
K - KeyEvent
Any other element can be used by visible text name such as "Home"

# SWIPE
# Swipe can be used with custom params "(startX, startY, amountOfSwipe, duration)"
# Usage: 
salamr -m "Home|Info|SU|SU|"
salamr -m "Home|SU(100,100,200,350)"

# Delay
# Delay need to have a tleast 1 double duration "D0.5, D1.0 etc"
# Usage:
salamr -m "Home|D0.5|Info|D1.5"


# All visible elmeents
# call elements to find by "visible text name", additional option index can be added such as "[0]" 
# Usage:
salamr -m "Home|Info[2]|Videos[1]"

# TextFields
# TextFields can be found by index of screen along with text to send TF[0](my text here)
# these files can then be used to be played as a test suite
# Usage: 
salamr -m "Home|TF[0](my text here)|TF[1](my text here)"

# sample command
salamr -m "TF[0](some text)|Reviews|SU|SU|Relationship[1]|Relationship[1]|Info|SU|SU|see more|SU|see less|AutoTrader|D1|B|D1|B"
```

## Run test suites
Once multiple files are recorded, these can now be used to create and run a test suite
```shell
salamr -p "fileOne,fileTwo,fileThree"
```

## Important

Currently salamr only supports finding elements by the displayed text. You cannot select an element by any other attributes. In later releases this feature will be added.

watch video about salamr: [Link to usage video](https://www.linkedin.com/posts/muhammad-abdulsalam-1253a7178_salamr-salam-run-a-command-line-tool-activity-7173786881824817152-cMYG?utm_source=share&utm_medium=member_desktop)

adb_salam's LinkedIn: [LinkedIn Profile Link](https://www.linkedin.com/in/muhammad-abdulsalam-1253a7178/)
