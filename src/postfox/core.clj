(ns postfox.core
  (:require
    [clojure.test :refer [function?]]))

(defn arg-count [func]
  (or
    (:arg-count (meta func))
    (->> (class func)
      (.getDeclaredMethods)
      (filter #(= "invoke" (.getName %)))
      (map #(alength (.getParameterTypes %)))
      (apply max))))

(defn arity [arg-count func]
  (with-meta func {:arg-count arg-count}))

(defn apply-func [func arg-count lst]
  (apply
    (fn [elems args]
      (conj (vec elems) (apply func args)))
    (split-at (- (count lst) arg-count) lst)))

(defn qtn [& elems]
  (->
    (reduce
      (fn [state next]
        (let [args (:args state)]
          (if (function? next)
            (let [
                func (:func state)
                nextFunc (apply partial (cons next args))]
              {:func
                 (if (function? func)
                   (comp nextFunc func)
                   nextFunc)
               :args []})
            (assoc state :args (conj args next)))))
      {:args []}
      elems)
    :func
    (with-meta {:qtn true})))

(defn has-meta? [func k]
  (-> (k (meta func)) true?))

(defn qtn? [func]
  (has-meta? func :qtn))

(defn postfunc [& elems]
  (with-meta (fn [] elems) {:postfunc true}))

(defn postfunc? [func]
  (has-meta? func :postfunc))

(declare postfox-process)

(defn postfox-step [lst next]
  (if (function? next)
    (if (qtn? next)
      (conj lst next)
      (if (postfunc? next)
        (apply postfox-process (concat lst (next)))
        (apply-func next (arg-count next) lst)))
    (conj lst next)))

(defn postfox-process-reductions [& elems]
  (reductions postfox-step [] elems))

(defn postfox-process [& elems]
  (reduce postfox-step [] elems))

(defn postfox [& elems]
  (-> (apply postfox-process elems) first))

