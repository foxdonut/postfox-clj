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

(defn apply-fn [func arg-count lst]
  (cons (apply func (-> (take arg-count lst) reverse)) (drop arg-count lst)))

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
    (with-meta {:qtn true})))

(defn qtn? [func]
  (-> (:qtn (meta func)) true?))

(defn postfox [& elems]
  (->
    (reduce
      (fn [lst next]
        (if (function? next)
          (if (qtn? next)
            (cons next lst)
            (apply-fn next (arg-count next) lst))
          (cons next lst)))
      []
      elems)
    first))

