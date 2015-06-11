#!/bin/bash
###
### Script to capture the steps required when open sourcing a single micro service
### Note that you need to set the GITLAB_ROOT environment variable before you can run this script
###

# Include the common functions
. common.sh

enterMicroServiceName() {
    read -p "Type the micro service name to open source:" COMPONENT_NAME
}

getMicroServiceName() {
    echo "Enter the micro service name"
    FINISHED="False"
  
    while [ ${FINISHED} == "False" ]; do
        enterMicroServiceName
        echo "Open sourcing micro service $COMPONENT_NAME. Do you want to continue?"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
            FINISHED="True"
        fi
    done
}

showInfo() {
    echo "Micro service name: $COMPONENT_NAME"
    echo "Release tag:        $RELEASE_TAG"
    echo "Directory:          $DIRECTORY"
}

checkEnvironmentVariables
echo "This script will open source a single micro service"
echo "Please turn on your VPN connection so we can access GitLab"
getMicroServiceName
getRelease
createOpenSourceDirectory
pullGitLabMergeGitHub
confirmCanContinue
reviewCodeDifferences
confirmCanContinue
turnOffVpnBuildAndTest
confirmCanContinue
echo "The next step will be to push the code to GitHub so it will be open source"
showInfo
confirmCanContinue
pushToGitHub
echo "Now wait for travis to build. Once it is green the next step will be to tag the release and push that tag"
confirmCanContinue
tagOpenSourceRelease

echo "Done."
