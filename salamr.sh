#!/bin/bash

: '
echo information about usage and options to be used
'
show_help() {
  echo
  echo -l \(Locate\) - Locate and Tap single item - Usage: ~\$salamr -l \"Home\"
  echo
  echo -m \(Multi Locate\) - Locate and Tap multiple item in a sequence - Usage: ~\$salamr -m \"Account,Theme,Dark\" \(avoid extra spaces\)
  echo
  echo -r \(Record\) - Record inputs on emulator - Usage: ~\$salamr -r
  echo
  echo -p \(Play\) - Play recorded inputs on emulator - Usage: ~\$salamr -p
  echo
}

: '
Entry point for salamr.sh script_dir
Handle tool options that can be used within this project
@option: -l requires at least 1 argument
@option: -m requires at least 1 argument
@option: -r requires no arguments
@option: -p requires no arguments
'
while getopts ":l:m:hrp" opt; do
  case ${opt} in
    h)
      # -h option
      show_help
      ;;
    l)
      # -l option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
      bash "$script_dir/bin/locator" "$OPTARG"
      ;;
    m)
      # -m option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
      bash "$script_dir/bin/locate_multiple" "$OPTARG"
      ;;
    r)
      # -r option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
      bash "$script_dir/bin/recorder"
      ;;
    p)
      # -p option
      script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
      bash "$script_dir/bin/replay"
      ;;
    \?)
      echo "Invalid option: $OPTARG" >&2
      ;;
    :)
      ;;
  esac
done
