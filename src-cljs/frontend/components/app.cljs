(ns frontend.components.app
  (:require [cljs.core.async :as async :refer [>! <! alts! chan sliding-buffer close!]]
            [frontend.async :refer [put!]]
            [frontend.components.inspector :as inspector]
            [frontend.components.key-queue :as keyq]
            [frontend.components.common :as common]
            [frontend.state :as state]
            [frontend.utils :as utils :include-macros true]
            [frontend.utils.seq :refer [dissoc-in]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ankha.core :as ankha])
  (:require-macros [frontend.utils :refer [html]]))

(def keymap
  (atom nil))

(defn app* [app owner]
  (reify
    om/IDisplayName (display-name [_] "App")
    om/IRender
    (render [_]
      (if-not (:navigation-point app)
        (html [:div#app])

        (let [controls-ch (om/get-shared owner [:comms :controls])
              persist-state! #(put! controls-ch [:state-persisted])
              restore-state! #(put! controls-ch [:state-restored])
              show-inspector? (get-in app state/show-inspector-path)
              logged-in? (get-in app state/user-path)]
          (reset! keymap {["ctrl+s"] persist-state!
                          ["ctrl+r"] restore-state!})
          (html
           [:div#app
            (om/build keyq/KeyboardHandler app
                      {:opts {:keymap keymap
                              :error-ch (get-in app [:comms :errors])}})
            (when show-inspector?
              ;; TODO inspector still needs lots of work. It's slow and it defaults to
              ;;     expanding all datastructures.
              (om/build inspector/inspector app))]))))))


(defn app [app owner]
  (reify om/IRender (render [_] (om/build app* (dissoc app :inputs)))))