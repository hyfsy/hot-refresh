
# Get home root path
BASE_DIR=$(dirname $0)/..

# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

# Set debug instruct
export REMOTE_DEBUG_INST="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
export REMOTE_DEBUG_OPTS=""
while getopts ":d:debug:" opt
do
    case $opt in
        d)
            REMOTE_DEBUG_OPTS=${REMOTE_DEBUG_INST};;
        debug)
            REMOTE_DEBUG_OPTS=${REMOTE_DEBUG_INST};;
    esac
done

# echo "$JAVA ${REMOTE_DEBUG_OPTS} -jar ${BASE_DIR}\lib\hot-refresh-client.jar $@"
$JAVA ${REMOTE_DEBUG_OPTS} -jar ${BASE_DIR}\lib\hot-refresh-client.jar $@
