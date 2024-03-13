#!/bin/bash

while getopts ":l:m:help" opt; do
  case ${opt} in
    h )
      # -h option
      echo
      echo -l \(Locate\) - Locate and Tap single item. Usage:  $\salamr -l \"Home\"
      echo
      echo -m \(Multi Locate\) - Locate and Tap multiple item in a sequence. Usage $\salamr -m \"Account,Theme,Dark\" \(avoid extra spaces\)
      echo
      ;;
    l )
      # -l option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"  
      bash "$script_dir/bin/locator"  "$OPTARG"
      ;;
    m )
      # -m option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"      
      bash "$script_dir/bin/locate_multiple" "$OPTARG"
      ;;
    \? )
      echo "Invalid option: $OPTARG" >&2
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" >&2
      ;;
  esac
done
