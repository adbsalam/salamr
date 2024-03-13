# salamr

Command line tool that can help you automate android emulator

## Prerequisites
To use salamr you need to have 2 command line tools installed `xmlstarlet` and `adb platform tools`

To install `xmlstarlet`
```shell
brew install xmlstarlet
```

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
gi
```shell
# list available features 'help'
salamr -h 

# locate a sigle item 'locate'
salamr -l 'YOUR_VIEW_TEXT_HERE'

# locate multiple items 'multi'
salamr -m 'YOUR_VIEW_TEXT_HERE,YOUR_VIEW_TEXT_HERE'
```

## Important

Currently salamr only supports finding elements by the displayed text. You cannot select an element by any other attributes. In later releases this feature will be added. 

watch video about salamr: [Link to usage video](https://www.linkedin.com/posts/muhammad-abdulsalam-1253a7178_salamr-salam-run-a-command-line-tool-activity-7173786881824817152-cMYG?utm_source=share&utm_medium=member_desktop) 

adb_salam's LinkedIn: [LinkedIn Profile Link](https://www.linkedin.com/in/muhammad-abdulsalam-1253a7178/)