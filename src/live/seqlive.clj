(ns live.seqlive
  ;(:require )
  ;(:use )
  ;(:import )

  (:use dbstep.sequencer)
  )


;; OK, let's make some noise!


;; An empty palatte to play with:
(do
  (buffer-write! buf-0 [1 0 1 1 0 0 1 0])  ;; kick
  (buffer-write! buf-1 [0 0 0 0 1 0 0 0])  ;; click
  (buffer-write! buf-2 [0 0 0 0 0 0 1 0])  ;; boom
  (buffer-write! buf-3 [0 0 0 0 0 0 0 0])) ;; subby

(buffer-write! buf-4 [60 0 60 65 67 69 70 67])
(buffer-write! buf-4 (repeatedly 8 #(choose (map (partial + 24) [60 60 0 65 65 67 70 67]))))

;; Maybe make a little lead-line
(buffer-write! buf-5 (map (partial - 24) [60 0 60 65 67 69 70 67]))
(buffer-write! buf-5 (repeatedly 8 #(choose (map (partial + 0) [60 60 0 65 65 67 70 67]))))

;; Four markoved chords starting with :I
(map #(degree->chord % :C4) (take 4 (get-chord-seq)))

;; Arpeggiated melody of markoved chords
(flatten (map #(degree->chord % :C4) (take 4 (get-chord-seq))))

;; Write these bad-asses to the buffer!
(buffer-write! buf-5 (flatten (map #(degree->chord % :C4) (take 4 (get-chord-seq)))))

;; What would happen if we took the notes of the chords and randomly selected them w/ repeats?
(flatten (map #(take 4 (repeatedly (fn [] (rand-nth %)))) (map #(degree->chord % :C4) (take 4 (get-chord-seq)))))

;; Write these bad-asses to the buffer!
(buffer-write! buf-5 (flatten (map #(take 4 (repeatedly (fn [] (rand-nth %)))) (map #(degree->chord % :C4) (take 4 (get-chord-seq))))))

;; Execute this a few times. :-)
(rand-bass)


;; try mixing up the sequences. Evaluate this a few times:
(do
  (buffer-write! buf-0 (repeatedly 8 #(choose [0 1])))
  (buffer-write! buf-1 (repeatedly 8 #(choose [0 1])))
  (buffer-write! buf-2 (repeatedly 8 #(choose [0 1])))
  (buffer-write! buf-3 (repeatedly 8 #(choose [0 1]))))

;; and then to something interesting
(do
  (buffer-write! buf-0 [1 1 1 1 1 1 1 1])
  (buffer-write! buf-1 [1 0 1 0 0 1 1 0])
  (buffer-write! buf-2 [1 1 0 1 0 1 1 0])
  (buffer-write! buf-3 [1 0 0 0 0 0 1 0]))

;; try changing the rate of the global pulse (everything else will
;; follow suit):
(ctl r-trg :rate 75)
(ctl r-trg :rate 300)
(ctl r-trg :rate 150)

;; get the dubstep bass involved:
(dubstep dub-note-bus
         :wobble (* BEAT-FRACTION 1)
         :lo-man 1)

(dubstep dub-bass-note-bus
         :wobble 10
         :lo-man 1)

;; go crazy - especially with the deci-man
(ctl dubstep
     :note 40
     :wobble (* BEAT-FRACTION 0.1)
     :lag-delay 0.05
     :hi-man 0
     :lo-man 0
     :deci-man 0)
(ctl dubstep
     :note 60
     :wobble (* BEAT-FRACTION 0.05)
     :lag-delay 0.05
     :hi-man 0
     :lo-man 0
     :deci-man 0.3)


;; Bring in the supersaws!

(def ssaw-rq 0.9)
(def ssaw-fil-mul 2)

;; Fire at will...
(supersaw2 (midi->hz 28) :amp 3 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 40) :amp 3 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 45) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 48) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 52) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 55) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 57) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 64) :amp 1 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 67) :amp 1 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz 69) :amp 1 :fil-mul ssaw-fil-mul :rq ssaw-rq)

;; modify saw params on the fly too...
(ctl supersaw2 :fil-mul 4 :rq 0.2)


