(ns clj-text-input.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-text-input started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-text-input has shut down successfully]=-"))
   :middleware identity})
