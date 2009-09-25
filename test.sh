#! /bin/sh

# Copyright (c) Krešimir Šojat, 2009. All rights reserved. The use
# and distribution terms for this software are covered by the Eclipse
# Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
# which can be found in the file epl-v10.html at the root of this
# distribution. By using this software in any fashion, you are
# agreeing to be bound by the terms of this license. You must not
# remove this notice, or any other, from this software.

# Helper script to run all Truba tests.

TEST_CP="src:test:lib/"'*'
java -cp $TEST_CP clojure.main @run.clj
