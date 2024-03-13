#!/bin/bash

# Check if at least one argument is provided
if [ $# -lt 1 ]; then
    echo "Usage: $0 <comma-separated-string>"
    exit 1
fi

# Split the string into an array
IFS=',' read -r -a array <<< "$1"

# Iterate over each element in the array
for element in "${array[@]}"; do

    if [ "$element" = "B" ]; then
        adb shell input keyevent KEYCODE_BACK
    elif [ "$element" = "S" ]; then
        bash /Users/muhammadabdulsalam/Dev/shell/salamr/swipe
    else
        echo "processing element: $element"
        adb shell uiautomator dump                                                            
        adb pull /sdcard/window_dump.xml .
        bash /Users/muhammadabdulsalam/Dev/shell/salamr/locator "$element"
    fi
 
done