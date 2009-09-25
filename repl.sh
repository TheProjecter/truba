#!/bin/sh

BREAK_CHARS="(){}[],^%$#@\"\";:''|\\"
COMPLETITIONS=".completitions"
RLWRAP='rlwrap --remember -c -b $BREAK_CHARS'

CP='lib/*:src'

if [ -f $COMPLETITIONS ]; then
    RLWRAP="$RLWRAP -f $COMPLETITIONS"
fi

if which rlwrap; then
    CMD="$RLWRAP java"
else
    CMD="java"
fi

$CMD -cp "$CP:." clojure.main -r
