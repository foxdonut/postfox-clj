(ns postfox.core)

(defn arity [func]
  (->> (class func)
    (.getDeclaredMethods)
    (filter #(= "invoke" (.getName %)))
    first
    (.getParameterTypes)
    alength))

(defn qtn [& elems]
  nil)

(defn postfox [& elems]
  nil)
