#!/bin/bash
###
### Script to capture the steps required when open sourcing the sbt plugins
### Note that you need to set the GITLAB_ROOT environment variable before you can run this script
###

# Include the common functions
. common.sh

COMPONENT_NAME="sbt-plugins"

showInfo() {
    echo "Common component name: $COMPONENT_NAME"
    echo "Release tag:           $RELEASE_TAG"
    echo "Directory:             $DIRECTORY"
}

turnOffVpnBuildSbtPlugins() {
    FINISHED="False"

    while [ ${FINISHED} == "False" ]; do
        echo "Please turn off your VPN connection. We will now build the code to open source"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
            FINISHED="True"
            sbt clean compile
        fi
    done
}

checkEnvironmentVariables
echo "This script will open source sbt-plugins"
echo "Please turn on your VPN connection so we can access GitLab"
getRelease
createOpenSourceDirectory
pullGitLabMergeGitHub
confirmCanContinue
reviewCodeDifferences
confirmCanContinue
turnOffVpnBuildSbtPlugins
confirmCanContinue
echo "The next step will be to push the code to GitHub so it will be open source"
showInfo
confirmCanContinue
pushToGitHub
echo "Now wait for travis to build. Once it is green check bintray to verify the new library has been published. The next step will be to tag the release and push that tag"
confirmCanContinue
tagOpenSourceRelease

echo "Done."
