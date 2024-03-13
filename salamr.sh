#!/bin/bash

while getopts ":l:m:help" opt; do
  case ${opt} in
    h )
      # -help option
      echo
      echo -l \(Locate\) - Locate and Tap single item. Usage:  $\salamr -l \"Home\"
      echo
      echo -m \(Multi Locate\) - Locate and Tap multiple item in a sequence. Usage $\salamr -m \"Account,Theme,Dark\" \(avoid extra spaces\)
      echo
      echo
      ;;
    l )
      # -l option
      bash /Users/muhammadabdulsalam/Dev/shell/salamr/locator "$OPTARG"
      ;;
    m )
      # -m option
      bash /Users/muhammadabdulsalam/Dev/shell/salamr/locate_multiple.sh "$OPTARG"
      ;;
    \? )
      echo "Invalid option: $OPTARG" >&2
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" >&2
      ;;
  esac
done
