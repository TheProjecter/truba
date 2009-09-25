#! /bin/sh

# Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
# and distribution terms for this software are covered by the Eclipse
# Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
# which can be found in the file epl-v10.html at the root of this
# distribution. By using this software in any fashion, you are
# agreeing to be bound by the terms of this license. You must not
# remove this notice, or any other, from this software.

#
# Set current directory as base of Truba installation.
# Use this only when experimenting with new code.
#

cwd=`pwd`
bin_dir="$cwd/bin"
src_dir="$cwd/src"
lib_dir="$cwd/lib"

export TRUBA_HOME=$cwd
export TRUBA_CP="$src_dir:$lib_dir/"'*'
export TRUBA_MAIN="clojure.main @truba/main.clj"
export PATH="$bin_dir:$PATH"

# Start shell session with new env variables
$SHELL
