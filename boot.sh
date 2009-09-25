#! /bin/sh

# Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
# and distribution terms for this software are covered by the Eclipse
# Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
# which can be found in the file epl-v10.html at the root of this
# distribution. By using this software in any fashion, you are
# agreeing to be bound by the terms of this license. You must not
# remove this notice, or any other, from this software.

# Script used only to initialize Truba dependencies if you don't
# have previous version of Truba installed on your system.

BIN=$0

CACHE_DIR="cache"
LIB_DIR="lib"

# Ivy is used to download initial set of dependencies, including
# Clojure 1.0.0 from central Maven repository.

IVY_SETTINGS_XML="ivysettings.xml"
IVY_XML="ivy.xml"

IVY_VER="2.1.0-rc2"
IVY_URL="http://www.apache.org/dist/ant/ivy/$IVY_VER/apache-ivy-$IVY_VER-bin.tar.gz"
IVY_GZ="$CACHE_DIR/ivy.tar.gz"
IVY_JAR="$CACHE_DIR/ivy.jar"
IVY_CACHE_DIR="$CACHE_DIR/ivy2"

# While Truba is using Clojure 1.0.0, it needs clojure.test for unit tests
# that is included in Clojure 1.1.0alpha.

CLOJURE_GIT="git://github.com/richhickey/clojure.git"
CLOJURE_CACHE_DIR="$CACHE_DIR/testlib"

#
# Prepare cache and lib directories.
#
function prepare_dirs {
    if [ ! -e "$CACHE_DIR" ]; then
        mkdir $CACHE_DIR
    fi
    if [ ! -e "$LIB_DIR" ]; then
        mkdir $LIB_DIR
    fi
}

#
# Download and unpack Ivy
#
function init_ivy {
    if [ ! -e $IVY_GZ ]; then
        wget $IVY_URL -O $IVY_GZ
    fi

    if [ ! -e "$IVY_JAR" ]; then
        tar -xvvzf \
            "$IVY_GZ" "apache-ivy-$IVY_VER/ivy-$IVY_VER.jar" -O > "$IVY_JAR"
    fi
}

function get_testlib {
    if [ ! -e $CLOJURE_CACHE_DIR ]; then
        git clone --quiet --depth 1 $CLOJURE_GIT $CLOJURE_CACHE_DIR
    fi
    jar cf "$LIB_DIR/clojure-test-1.0.0.jar" \
        -C "$CLOJURE_CACHE_DIR/src/clj" clojure/test.clj \
        -C "$CLOJURE_CACHE_DIR/src/clj" clojure/template.clj \
        -C "$CLOJURE_CACHE_DIR/src/clj" clojure/stacktrace.clj \
        -C "$CLOJURE_CACHE_DIR/src/clj" clojure/walk.clj
}

#
# Prepare Truba dependencies.
#

function get_deps {
    prepare_dirs
    init_ivy

    java -cp $IVY_JAR org.apache.ivy.Main \
         -cache $IVY_CACHE_DIR \
         -settings $IVY_SETTINGS_XML \
         -ivy $IVY_XML \
         -retrieve "$LIB_DIR/[artifact]-[revision].[ext]"
}

get_deps
get_testlib
