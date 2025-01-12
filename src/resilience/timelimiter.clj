(ns resilience.timelimiter
  (:refer-clojure :exclude [name])
  (:require [resilience.spec :as s])
  (:import (java.time Duration)
           (io.github.resilience4j.timelimiter TimeLimiterConfig TimeLimiterConfig$Builder TimeLimiter)))

(defn ^TimeLimiterConfig time-limiter-config
  "Create a TimeLimiterConfig.

  Allowed options are:
  * :timeout-millis
    Configures the thread execution timeout.
    Default value is 1 second.

  * :cancel-running-future?
    Configures whether cancel is called on the running future.
    Defaults to true.
   "
  [opts]
  (s/verify-opt-map-keys-with-spec :timelimiter/time-limiter-config opts)

  (if (empty? opts)
    (TimeLimiterConfig/ofDefaults)
    (let [^TimeLimiterConfig$Builder config (TimeLimiterConfig/custom)]
      (when-let [timeout (:timeout-millis opts)]
        (.timeoutDuration config (Duration/ofMillis timeout)))

      (when (:cancel-running-future? opts)
        (.cancelRunningFuture config true))

      (.build config))))

(defn ^TimeLimiter time-limiter
  "Create a time limiter with a default or custom time limiter configuration.

   Please refer to `time-limiter-config` for allowed key value pairs
   within the time limiter configuration."
  ([] (TimeLimiter/ofDefaults))
  ([config]
   (let [config (time-limiter-config config)]
     (TimeLimiter/of ^TimeLimiterConfig config))))

(defmacro deftimelimiter
  "Define a time limiter under `name`.

   Please refer to `time-limiter-config` for allowed key value pairs
   within the time limiter configuration."
  ([name]
   (let [sym (with-meta (symbol name) {:tag `TimeLimiter})]
     `(def ~sym (time-limiter))))
  ([name config]
   (let [sym (with-meta (symbol name) {:tag `TimeLimiter})]
     `(def ~sym (time-limiter ~config)))))

