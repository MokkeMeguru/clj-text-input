(ns clj-text-input.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [clj-text-input.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary :include-macros true])
  (:import goog.History))

(defonce session (r/atom {:page :home}))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page (:page @session)) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-primary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "Celsius <=> Fahrenheit"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]
     [nav-link "#/about" "About" :about]]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

;; -------------------------------------------------------------------
(defn form-input [label placeholder id fields start-time result]
  [:div.container
   [:div.form-group.flex.my-auto
    [:label label]
    [:input.form-control.input-lg
     {:type :text
      :placeholder placeholder
      :value (id @fields)
      :on-change #(do
                    (swap! fields assoc id (-> % .-target .-value)))
      :on-key-press (fn [e]
                      (when (= 13 (.-charCode e))
                        (swap! result assoc :state true)
                        (swap! result assoc :t (- (.getTime (js/Date.)) @start-time))
                        (if (= id :cel)
                          (swap! result assoc
                                 :res
                                 (+ 32 (* 1.8 (id @fields))))
                          (swap! result assoc
                                 :res
                                 (/ (- (id @fields) 32) 1.8))))
                      (swap! fields {:cel nil :far nil}))}]]])

(defn res [result]
  [:div.container
   (when (:state @result)
     [:div.flex.col
      [:label.col-md-4 "Result" [:p (:res @result)]]
      [:label.col-md-4 "Time" [:p (:t @result)]]])])

(defn home-page []
  [:div.container
   (let [fields (r/atom {:cel nil :far nil})
         start-time (r/atom nil)
         result (r/atom nil)]
     [:div.col
      [:button.btn.btn-primary.col-md-2
       {:on-click #(do (reset! start-time (.getTime (js/Date.)))
                       (swap! result assoc :state false))}
       "Timer Start!"]
      [form-input "Celsius" "XX.X" :cel fields start-time result]
      [form-input "Fahrenheit" "XX.X" :far fields start-time result]
      [res result]
      ])])
;; ------------------------------------------------------------------------

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! session assoc :page :home))

(secretary/defroute "/about" []
  (swap! session assoc :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
            (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
