(ns clj-text-input.app
  (:require [clj-text-input.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
