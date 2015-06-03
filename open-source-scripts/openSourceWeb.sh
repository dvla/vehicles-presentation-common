#!/bin/bash
###
### Script to capture the steps required when open sourcing the web layer for a single exemplar
### Note that you need to set the GITLAB_ROOT environment variable before you can run this script
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

enterExemplarName() {
    read -p "Type the exemplar name to open source:" EXEMPLAR_NAME
}

enterRelease() {
    read -p "Type the release tag to open source:" RELEASE_TAG
}

enterDirectoryName() {
    read -p "Type the directory to create:" DIRECTORY
}

getExemplarName() {
    echo "Enter the exemplar name"
    FINISHED="False"
  
    while [ ${FINISHED} == "False" ]; do
        enterExemplarName
        echo "Open sourcing exemplar $EXEMPLAR_NAME. Do you want to continue?"
        answerYesNo

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
        FINISHED="True"
        fi
    done
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

        if [ ${ANS_YN} == "y" ] || [ ${ANS_YN} == "Y" ]; then
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
    git remote add gitlab ${GITLAB_ROOT}/${EXEMPLAR_NAME}.git
    git pull gitlab $RELEASE_TAG
    git remote add github git@github.com:dvla/${EXEMPLAR_NAME}.git
    git pull github master
}

pushToGitHub() {
    git remote remove gitlab
    git push -u github master
}

showInfo() {
    echo "Exemplar name: $EXEMPLAR_NAME"
    echo "Release tag:   $RELEASE_TAG"
    echo "Directory:     $DIRECTORY"
}

tagOpenSourceRelease() {
    git tag $RELEASE_TAG
    git push --tags github
}

checkEnvironmentVariables
echo "This script will open source the web layer code for a single exemplar"
echo "Please turn on your VPN connection so we can access GitLab"
getExemplarName
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
