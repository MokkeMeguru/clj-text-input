(ns clj-text-input.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clj-text-input.core-test]))

(doo-tests 'clj-text-input.core-test)

