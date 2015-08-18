#!/bin/bash
###
### Set of common functions that can be used by other scripts. Use it by including
### the following line in the dependent script: . common.sh
###

checkEnvironmentVariables() {
    if [ -z ${GITLAB_ROOT} ]; then
        echo "Please set environment variable GITLAB_ROOT. Now exiting"
        exit 1
    fi
}

answerYesNo() {
    read -p "(y/n) to continue:" ANS_YN
    case "$ANS_YN" in
        [Yy]|[Nn]) return;;
        *) answerYesNo;;
    esac
}

enterRelease() {
    read -p "Type the release tag to open source:" RELEASE_TAG
}

enterDirectoryName() {
    read -p "Type the directory to create:" DIRECTORY
}

getRelease() {
    echo "Enter the release tag"
    FINISHED="False"
  
    while [ ${FINISHED} == "False" ]; do
        enterRelease
        echo "Open sourcing release tag $RELEASE_TAG. Do you want to continue?"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
            FINISHED="True"
        fi
    done
}

createOpenSourceDirectory() {
    echo "Create open source directory"
    FINISHED="False"

    while [ ${FINISHED} == "False" ]; do
        enterDirectoryName
        echo "Will create directory $DIRECTORY. Do you want to continue?"
        answerYesNo

        if ([ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]) && [ -d "$DIRECTORY" ]; then
            echo "Directory $DIRECTORY already exists. Please choose another."
        fi
        if ([ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]) && [ ! -d "$DIRECTORY" ]; then
            FINISHED="True"
            mkdir $DIRECTORY
            cd $DIRECTORY
        fi
    done
}

confirmCanContinue() {
    FINISHED="False"

    while [ ${FINISHED} == "False" ]; do
        echo "Can we move to the next step? If you answer N we will terminate"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
            FINISHED="True"
        else
            exit
        fi
    done
}

reviewCodeDifferences() {
    git fetch gitlab master
    git diff master gitlab/master
    echo "These are the code differences that are in GitHub that are not in GitLab"
}

turnOffVpnBuildAndTest() {
    FINISHED="False"
  
    while [ ${FINISHED} == "False" ]; do
        echo "Please turn off your VPN connection. We will now build and test the code to open source"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
            FINISHED="True"
            sbt clean test
        fi
    done
}

pullGitLabMergeGitHub() {
    git init
    git remote add gitlab ${GITLAB_ROOT}/${COMPONENT_NAME}.git
    git pull gitlab $RELEASE_TAG
    git remote add github git@github.com:dvla/${COMPONENT_NAME}.git
    git pull github master
}

pushToGitHub() {
    git remote remove gitlab
    git push -u github master
}

tagOpenSourceRelease() {
    git tag $RELEASE_TAG
    git push --tags github
}
