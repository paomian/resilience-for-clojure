(ns resilience.util
  (:require [clojure.spec.alpha :as s])
  (:import (java.util Iterator)))

(defn lazy-seq-from-iterator [^Iterator iter]
  (if (.hasNext iter)
    (cons (.next iter)
          (lazy-seq (lazy-seq-from-iterator iter)))
    []))

(defn enum->keyword [^Enum e]
  (keyword (.name e)))

(defmacro case-enum
  "Like `case`, but explicitly dispatch on Java enum ordinals."
  [e & clauses]
  (letfn [(enum-ordinal [e] `(let [^Enum e# ~e] (.ordinal e#)))]
    `(case ~(enum-ordinal e)
       ~@(concat
           (mapcat (fn [[test result]]
                     [(eval (enum-ordinal test)) result])
                   (partition 2 clauses))
           (when (odd? (count clauses))
             (list (last clauses)))))))
