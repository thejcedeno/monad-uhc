#!/bin/bash
set -x
# Declare variables
MC_VERSION="1.18.1"
PURPUR_URI="https://api.purpurmc.org/v2/purpur/${MC_VERSION}/latest/download"
INIT_RAM="1G"
MAX_RAM="2G"
EXTRA_FLAGS=""
# If directory debug doesn't exist, create one
if [ ! -d "debug" ]; then
    mkdir debug
fi
# Move to debug dir and download jar if not present
cd debug
if [ ! -f "purpur.jar" ]; then
    wget -O purpur.jar "${PURPUR_URI}"
fi
# Accept EULA
echo "eula=true" >eula.txt

# Run the server
eval java -Xms${INIT_RAM} -Xmx${MAX_RAM} $EXTRA_FLAGS -jar purpur.jar nogui
