(ns user
  (:require [clj-text-input.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [clj-text-input.figwheel :refer [start-fw stop-fw cljs]]
            [clj-text-input.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'clj-text-input.core/repl-server))

(defn stop []
  (mount/stop-except #'clj-text-input.core/repl-server))

(defn restart []
  (stop)
  (start))


