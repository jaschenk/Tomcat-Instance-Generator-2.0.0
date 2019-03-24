#!/usr/bin/bash
# ***********************************************************************
#  Upgrade Tomcat from v8.5.29 to v8.5.34.
#
# History:
# 2018-09-23 schenkje modify to be generated using utility.
# 2018-09-19 schenkje Updated for v8.5.34.
# 2018-08-31 schenkje Updated for v8.5.33.
# 2018-03-14 schenkje Initially Developed.
#
# ***********************************************************************
TC_UPGRADE_VERSION=${REPLACEMENT_UPGRADE_VERSION}
UPGRADE_SRC_DIR=./${TC_UPGRADE_VERSION}/
TOMCAT_HOME=/opt/tomcat-home
#
# This contains the partial path of a Tomcat Base.
# We assume we have instances 01-08 for this upgrade.
# there may be a base instance 00, but we leave alone, as that it's use will be deprecated.
TOMCAT_BASE=/opt/tomcat-base-

#
# Show a Header
echo "*********************************************************";
echo " Health Tomcat Environment Instance Upgrade Process";
echo "*********************************************************";

#
# Check for Install Directory.
if [ ! -d "${UPGRADE_SRC_DIR}" ]; then
  # Directory for Upgrade does not exist...
  echo "The Upgrade Source Directory ${UPGRADE_SRC_DIR} does not exist!";
  echo "You need to ensure you are running this script from the directory containing the Upgrade Source.";
  exit;
fi

#
# Check for Tomcat Home.
if [ ! -d "${TOMCAT_HOME}" ]; then
  # Tomcat Home Destination Directory for Upgrade does not exist...
  echo "The Tomcat Home Destination Directory ${TOMCAT_HOME} does not exist!";
  echo "This script appears to be running on a non-standard Tomcat Installation, unable to continue...";
  exit;
fi
echo "Found Tomcat Home to Upgrade: ${TOMCAT_HOME}";

#
# Check for each Tomcat Base.
for (( i=1; i<9; i++ ));
do
	TC_BASE_INSTANCE=${TOMCAT_BASE}0${i}
	if [ ! -d "${TC_BASE_INSTANCE}" ]; then
		# Tomcat Home Destination Directory for Upgrade does not exist...
		echo "The Tomcat Home Destination Directory ${TC_BASE_INSTANCE} does not exist!";
		echo "This script appears to be running on a non-standard Tomcat Installation, unable to continue...";
		exit;
	fi
	echo "Found Tomcat Base Instance to Upgrade: ${TC_BASE_INSTANCE}";
  done
  
#
# Now prompt to see if we should continue with the Upgrade?
echo "Do you wish to continue with the Upgrade of Tomcat to ${TC_UPGRADE_VERSION} ? 1=Yes, 2=No"
select yn in "Yes" "No"; do
    case $yn in
        Yes ) break;;
        No ) echo "Upgrade process has been cancelled..."; exit;;
    esac
done

#
# Have you stopped all of the Instances we are going to Upgrade?
# Check for each Tomcat Instance?
for (( i=1; i<9; i++ ));
do
	TC_BASE_INSTANCE=tc0${i}
	RUNNING=`ps -ef | grep -v pts | sed -n /${TC_BASE_INSTANCE}/p`
	if [ "${RUNNING:-null}" != null ]; then
		echo "Tomcat Instance ${TC_BASE_INSTANCE} is running on this Environment, unable to continue with upgrade!";
		echo "Ensure all Tomcat Instance for this Environment have been stopped, or else, upgrade can not take place...";
		exit;
	fi	
done  

#
# Proceed with Upgrade Process
echo "Upgrade process has been selected to continue with the upgrade...";

#
# First Upgrade Tomcat Home
echo "Upgrading Tomcat Home: ${TOMCAT_HOME} ...";
#
# Upgrade the Tomcat Home 
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/CONTRIBUTING.md ${TOMCAT_HOME}/.
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/LICENSE ${TOMCAT_HOME}/.
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/NOTICE ${TOMCAT_HOME}/.
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/RELEASE-NOTES ${TOMCAT_HOME}/.
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/RUNNING.txt ${TOMCAT_HOME}/.
#
# Remove any Old WIN Batch Scripts...
rm ${TOMCAT_HOME}/bin/*.bat 1> /dev/null 2>&1
chmod uog+x *.sh
#
# Remove previous Native Library
rm ${TOMCAT_HOME}/lib/libtcnative* 1> /dev/null 2>&1
rm -r ${TOMCAT_HOME}/lib/pkgconfig 1> /dev/null 2>&1
#
# Proceed updating bin and lib directories.
cp -pr ${UPGRADE_SRC_DIR}/tomcat-home/lib/* ${TOMCAT_HOME}/lib/.
cp -p ${UPGRADE_SRC_DIR}/tomcat-home/bin/* ${TOMCAT_HOME}/bin/.
chmod uog+x ${TOMCAT_HOME}/bin/*.sh
#
# Replace the JRE, if applicable
# rm -r ${TOMCAT_HOME}/jre_old 1> /dev/null 2>&1
# mv ${TOMCAT_HOME}/jre ${TOMCAT_HOME}/jre_old
# mkdir ${TOMCAT_HOME}/jre
# cp -pr ${UPGRADE_SRC_DIR}/tomcat-home/jre/* ${TOMCAT_HOME}/jre/.

# 
# Second Upgrade Each Tomcat Base Instance
for (( i=1; i<9; i++ ));
do
	TC_BASE_INSTANCE=${TOMCAT_BASE}0${i}
	echo "Upgrading Tomcat Base Instance: ${TC_BASE_INSTANCE} ...";
	#
	# Upgrade the Tomcat Base
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/LICENSE ${TC_BASE_INSTANCE}/.
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/NOTICE ${TC_BASE_INSTANCE}/.
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/RELEASE-NOTES ${TC_BASE_INSTANCE}/.
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/RUNNING.txt ${TC_BASE_INSTANCE}/.
	#
        # Update Tomcat Base bin ...
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/bin/* ${TC_BASE_INSTANCE}/bin/.
	# 
	# Update Tomcat Base Web Apps ...
	cp -pr ${UPGRADE_SRC_DIR}/tomcat-base-0x/webapps/* ${TC_BASE_INSTANCE}/webapps/.
	#
	# Update Tomcat Base Configuration ...
	cp -p ${UPGRADE_SRC_DIR}/tomcat-base-0x/conf/* ${TC_BASE_INSTANCE}/conf/.
	#
	# Now clean-up any previously serialized data.
	rm -r ${TC_BASE_INSTANCE}/temp/* 1> /dev/null 2>&1
	rm -r ${TC_BASE_INSTANCE}/work/Catalina 1> /dev/null 2>&1
	
	#
	# Any other Work for Tomcat Instance ...
	
  done

#
# Done...
echo "Tomcat Upgrade Process Complete, proceed to restart this Tomcat Environment to validate Upgrade.";

