
;; adapted from
;;   https://github.com/jonase/mlx/wiki/Reading-csv-files-with-data.csv

(ns dbstep.data
  (:require [clojure.data.csv :as csv]
	    [clojure.walk     :as walk]
            [clojure.java.io  :as io]))

;; schema of sample_data.csv
;;  0 symbol
;;  1 start_date
;;  2 start_time
;;  3 end_date
;;  4 end_time
;;  5 market_center
;;  6 bid_quantity
;;  7 bid_price
;;  8 ask_quantity
;;  9 ask_price
;; 10 quote_condition

(def bid-quant #(nth % 6))
(def bid-price #(nth % 7))
(def ask-quant #(nth % 8))
(def ask-price #(nth % 9))

(def extract (juxt bid-quant
		   bid-price
		   ask-quant
		   ask-price))

(defn reldiff [x y] (/ (- x y) (+ x y)))

(defn mapify [dataset]
     (let [keynames (first dataset)
	   items (rest dataset)]
       (map (fn [i] (walk/keywordize-keys (zipmap keynames i))) items)))

(defn parse-row [data-row]
      (let [[bid-quant bid-price ask-quant ask-price]
	    (extract data-row)]
	[(reldiff bid-quant ask-quant)
	 (reldiff bid-price ask-price)]))

(defn rel-diffs [csv-file]
  (with-open [reader (io/reader csv-file)]
    (->> (rest (csv/read-csv reader))
         (map parse-row)
	 doall
         ;;(apply max-key second)
	 )))

(defn print-diffs
  "I don't do a whole lot."
  [filename]
  (->> (rel-diffs filename)
       (map println)
       ))

;;(defn -main []
;;  (print-diffs "../sample_data/sample_data.csv")
;;  )

(defn read-data [filename] (mapify (csv/read-csv (io/reader filename))))
(def data (read-data "../sample_data/sample_data.csv"))
(def apple (read-data "../sample_data/apple_10k.csv"))
;(def foosdfsdfdsf (read-data "../sample_data/dfsdfdsfsfd_data.csv"))
