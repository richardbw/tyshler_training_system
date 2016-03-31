#!/bin/bash
#
#  auth: rbw
#  date: 20160331
#  desc: 
#
#- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
BASE_DIR=`cd "${0%/*}/." && pwd`

echo -n "BRANCH:"
git branch | grep "^\*"

APK="${BASE_DIR}/app/build/outputs/apk/app-debug.apk"

[ ! -f "$APK" ] && $BASE_DIR/gradlew assembleDebug
[ -z "$ANDROID_HOME" ] && echo "ERROR: ANDROID_HOME not set" && exit 13

[ $(uname -o) = "Cygwin" ] && APK="$(cygpath -wa $APK)"

$ANDROID_HOME/platform-tools/adb -d install -r $APK


#- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
echo "Done."
#//EOF
