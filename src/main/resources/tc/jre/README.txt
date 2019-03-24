# ************************************************************************************
#
#         _    __      __            _ _____  ______
#        | |  /\ \    / /\          | |  __ \|  ____|
#        | | /  \ \  / /  \         | | |__) | |__
#    _   | |/ /\ \ \/ / /\ \    _   | |  _  /|  __|
#   | |__| / ____ \  / ____ \  | |__| | | \ \| |____
#    \____/_/    \_\/_/    \_\  \____/|_|  \_\______|
#
#
# JAVA JRE Location, only required when running on our Tomcat Environment on Linux.
# ************************************************************************************


Currently the  Web application run upon a base JVM Version Level 8.

It is recommended moving forward we continue to use the OpenJDK JRE for runtime as opposed
to an Oracle JRE, which will require a cost for production commercial use!

OpenJDK Linux Installs:

Fedora, Oracle Linux, Red Hat Enterprise Linux, etc.
On the command line, type:

$ su -c "yum install java-1.8.0-openjdk"
The java-1.8.0-openjdk package contains just the Java Runtime Environment.

