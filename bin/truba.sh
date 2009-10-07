#! /bin/sh

# Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
# and distribution terms for this software are covered by the Eclipse
# Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
# which can be found in the file epl-v10.html at the root of this
# distribution. By using this software in any fashion, you are
# agreeing to be bound by the terms of this license. You must not
# remove this notice, or any other, from this software.

# Main application laucher script for Truba.

TRUBA_BIN=$0
if [ ! "$TRUBA_MAIN" ]; then
    TRUBA_MAIN=truba.main
fi

TRUBA_LOCAL_HOME="$HOME/.truba"
TRUBA_SHARE_HOME="/usr/share/truba"

# Find truba
if [ -z "$TRUBA_HOME" ]; then
    if [ -a $TRUBA_LOCAL_HOME ]; then
        TRUBA_HOME=$TRUBA_LOCAL_HOME
    elif [ -a $TRUBA_SHARE_HOME ]; then
        TRUBA_HOME=$TRUBA_SHARE_HOME
    else:
        echo "Error: TRUBA_HOME is not defined correctly."
        exit 1
    fi
fi

# Load truba settings
TRUBA_RC_FILE="$TRUBA_HOME/.trubarc"
if [ -f TRUBA_RC_FILE ]; then
    TRUBA_RC=$TRUBA_RC_FILE
fi

# Truba classpath
if [ ! "$TRUBA_CP" ]; then
    TRUBA_CP=$TRUBA_HOME'/*'
fi

# Truba configuration file.
if [ ! "$TRUBA_CONF" ]; then
    TRUBA_CONF="$TRUBA_HOME/etc/trubaconf.clj"
fi

# Configure rlwrap and use it if present.
BREAK_CHARS="(){}[],^%$#@\"\";:''|\\"
COMPLETITIONS=".completitions"
RLWRAP='rlwrap --remember -c -b $BREAK_CHARS'

if [ -f $COMPLETITIONS ]; then
    RLWRAP="$RLWRAP -f $COMPLETITIONS"
fi

if which rlwrap; then
    CMD="$RLWRAP java"
else
    CMD="java"
fi

# Split command line arguments prefixed with -J, they are for jvm not truba
jvm_args=
bin_args=
while [[ $1 ]]
do
    if [[ "$1" =~ -J(.+) ]]
    then
        jvm_args="$jvm_args ${BASH_REMATCH[1]}"
    else
        bin_args="$bin_args $1"
    fi
    shift
done

# Run truba
$CMD \
    $jvm_args \
    -D_main=truba.main \
    -D_main.bin=$TRUBA_BIN \
    -Dtruba.home=$TRUBA_HOME \
    -Dtruba.conf=$TRUBA_CONF \
    -cp "$TRUBA_CP" $TRUBA_MAIN \
    $bin_args
