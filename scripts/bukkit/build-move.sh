# !/bin/bash
set -x
# Declare variables
PREFIX="anmelden"
# Build the jar
./gradlew :bukkit:build
# Move to debug plugins jar, if debug/plugins doesn't exist, create the dir
if [ ! -d "debug/plugins" ]; then
    mkdir debug/plugins
fi
# Move jar
mv bukkit/build/libs/bukkit*-all.jar debug/plugins/${PREFIX}-bukkit.jar
