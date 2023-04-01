
#!/bin/bash
set -x
# Check if debug directory exists, if not create one and cd into it
if [ ! -d "debug" ]; then
    mkdir debug
    # Echo message to user indication creation of directory
    echo "Created debug directory"
fi
cd debug
# Check if paper jar exists, if not download it
if [ ! -f "paper-1.19*.jar" ]; then
    curl -o paper-1.19.4.jar https://api.papermc.io/v2/projects/paper/versions/1.19.4/builds/484/downloads/paper-1.19.4-484.jar
fi
# Accept eula and create plugins directory
echo "eula=true" > eula.txt
mkdir plugins
# Echo message to user indicating completion of setup
echo -e  "==\n\nSetup complete!\n\nPlease use ./hotstart to compile the plugin and start the server!\n=="