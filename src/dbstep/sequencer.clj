(ns dbstep.sequencer
  (:use [overtone.core])
  ;(:require )
  ;(:use )
  ;(:import )
  )

;; Originally from https://gist.github.com/3651346

;; A fully server-side sample sequencer.
;; =====================================

;; This example demonstrates some of the benefits of moving all synth
;; triggers inside the server itself. For example, it allows you to
;; modify the synthesis with *immediate* effect (rather than waiting for
;; the next bar/chunk to be scheduled) and you can use a global pulse to
;; drive both the timing and to also modulate aspects of the synthesis
;; so that the modulations are sympathetic to the rhythms being played.


;; First, let's create some sequencer buffers for specifying which beat
;; to trigger a sample. This will be our core data structure for a basic
;; emulation of an 8-step sequencer. A buffer is like a Clojure vector,
;; except it lives on the server and may only contain floats. Buffers
;; are initialised to have all values be 0.0
(defonce buf-0 (buffer 8))
(defonce buf-1 (buffer 8))
(defonce buf-2 (buffer 8))
(defonce buf-3 (buffer 8))
(defonce buf-4 (buffer 8))
(defonce buf-5 (buffer 16))
(defonce buf-6 (buffer 16))
(defonce buf-7 (buffer 16))
(defonce buf-8 (buffer 16))
(defonce buf-9 (buffer 16))
(defonce buf-10 (buffer 16))

;; Next let's create some timing busses. These can be visualised as
;; 'patch cables' - wires that carry pulse signals that may be
;; arbitrarily forked and fed into any synth that wants to be aware of
;; the pulses. We have two types of information being conveyed here -
;; firstly the trg busses contain a stream of 0s with an intermittant 1
;; every time there is a tick. Secondly we have the cnt busses which
;; contain a stream of the current tick count. We then have two of each
;; type of bus - one for a high resolution global metronome, and another
;; for a division of the global metronome for our beats.
(defonce root-trg-bus  (control-bus)) ;; global metronome pulse
(defonce root-cnt-bus  (control-bus)) ;; global metronome count
(defonce beat-trg-bus  (control-bus)) ;; beat pulse (fraction of root)
(defonce beat-cnt-bus  (control-bus)) ;; beat count
(defonce meter-cnt-bus8 (control-bus))
(defonce meter-cnt-bus16 (control-bus))
(defonce dub-note-bus  (control-bus))
(defonce dub-bass-note-bus (control-bus))
(defonce foo-note-bus (control-bus))
(defonce supersaw2-1-note-bus (control-bus))
(defonce supersaw2-2-note-bus (control-bus))
(defonce supersaw2-3-note-bus (control-bus))
(defonce supersaw2-4-note-bus (control-bus))

(def BEAT-FRACTION "Number of global pulses per beat" 30)

;; Here we design synths that will drive our pulse busses.
(defsynth root-trg [rate 100]
  (out:kr root-trg-bus (impulse:kr rate)))

(defsynth root-cnt []
  (out:kr root-cnt-bus (pulse-count:kr (in:kr root-trg-bus))))

(defsynth beat-trg [div BEAT-FRACTION]
  (out:kr beat-trg-bus (pulse-divider (in:kr root-trg-bus) div))  )

(defsynth beat-cnt []
  (out:kr beat-cnt-bus (pulse-count (in:kr beat-trg-bus))))

(defsynth meter-cnt [meter-cnt-bus 0 div 8]
  (out:kr meter-cnt-bus (mod (in:kr beat-cnt-bus) div)))

;; Now we get a little close to the sounds. Here's four nice sounding
;; samples from Freesound.org
(def kick-s (sample (freesound-path 777)))
(def click-s (sample (freesound-path 406)))
(def boom-s (sample (freesound-path 33637)))
(def subby-s (sample (freesound-path 25649)))

;; Here's a synth for playing back the samples with a bit of modulation
;; to keep things interesting.
(defsynth mono-sequencer
  "Plays a single channel audio buffer."
  [buf 0 rate 1 out-bus 0 beat-num 0 sequencer 0 amp 1]
  (let [cnt      (in:kr beat-cnt-bus)
        beat-trg (in:kr beat-trg-bus)
        bar-trg  (and (buf-rd:kr 1 sequencer cnt)
                      (= beat-num (mod cnt 8))
                      beat-trg)
        vol      (set-reset-ff bar-trg)]
    (out
     out-bus (* vol
                amp
                (pan2
                 (rlpf
                  (scaled-play-buf 1 buf rate bar-trg)
                  (demand bar-trg 0 (dbrown 200 20000 50 INF))
                  (lin-lin:kr (lf-tri:kr 0.01) -1 1 0.1 0.9)))))))

(defsynth note-sequencer
  "Plays a sequence of notes to a bus"
  [buf 0 meter-count-bus 0 out-bus 1]
  (out out-bus (buf-rd:kr 1 buf (in:kr meter-count-bus) 1 0)))

;; Here's Dan Stowell's dubstep synth modified to work with the global
;; pulses
(definst dubstep [note-bus 0 wobble BEAT-FRACTION hi-man 0 lo-man 0 sweep-man 0 deci-man 0 tan-man 0 shape 0 sweep-max-freq 3000 hi-man-max 1000 lo-man-max 500 beat-vol 0 lag-delay 0.5]
  (let [bpm     300
        note    (in:kr note-bus)
        wob     (pulse-divider (in:kr root-trg-bus) wobble)
        sweep   (lin-lin:kr (lag-ud wob 0.01 lag-delay) 0 1 400 sweep-max-freq)
        snd     (mix (saw (* (midicps note) [0.99 1.01])))
        snd     (lpf snd sweep)
        snd     (normalizer snd)

        snd     (bpf snd 1500 2)
        ;;special flavours
        ;;hi manster
        snd     (select (> hi-man 0.05) [snd (* 4 (hpf snd hi-man-max))])

        ;;sweep manster
        snd     (select (> sweep-man 0.05) [snd (* 4 (hpf snd sweep))])

        ;;lo manster
        snd     (select (> lo-man 0.05) [snd (lpf snd lo-man-max)])

        ;;decimate
        snd     (select (> deci-man 0.05) [snd (round snd 0.1)])

        ;;crunch
        snd     (select (> tan-man 0.05) [snd (tanh (* snd 5))])

        snd     (* 0.5 (+ (* 0.8 snd) (* 0.3 (g-verb snd 100 0.7 0.7))))
        ]
    (normalizer snd)))

;; Here's a nice supersaw synth
(definst supersaw2 [freq 440 amp 1 fil-mul 2 rq 0.3]
  (let [input  (lf-saw freq)
        shift1 (lf-saw 4)
        shift2 (lf-saw 7)
        shift3 (lf-saw 5)
        shift4 (lf-saw 2)
        comp1  (> input shift1)
        comp2  (> input shift2)
        comp3  (> input shift3)
        comp4  (> input shift4)
        output (+ (- input comp1)
                  (- input comp2)
                  (- input comp3)
                  (- input comp4))
        output (- output input)
        output (leak-dc:ar (* output 0.25))
        output (normalizer (rlpf output (* freq fil-mul) rq))]

    (* amp output (line 1 0 10 FREE))))


;; Markov chain of chords transitions (depth 1). It's hand-programmed for now, and it could really use
;; IIdim and viidim and longer chains.
(def chord-chain
  [[:i :i   0.0]  [:ii :i   0.0] [:iii :i   0.0] [:iv :i   0.1] [:v :i   0.5] [:vi  :i   0.0] [:vii  :i   0.0]
   [:i :ii  0.0]  [:ii :ii  0.0] [:iii :ii  0.1] [:iv :ii  0.1] [:v :ii  0.1] [:vi  :ii  0.2] [:vii  :ii  0.1]
   [:i :iii 0.0]  [:ii :iii 0.1] [:iii :iii 0.0] [:iv :iii 0.0] [:v :iii 0.0] [:vi  :iii 0.0] [:vii  :iii 0.0]
   [:i :iv  0.2]  [:ii :iv  0.0] [:iii :iv  0.0] [:iv :iv  0.0] [:v :iv  0.0] [:vi  :iv  0.0] [:vii  :iv  0.0]
   [:i :v   0.2]  [:ii :v   0.4] [:iii :v   0.0] [:iv :v   0.3] [:v :v   0.0] [:vi  :v   0.5] [:vii  :v   0.1]
   [:i :vi  0.1]  [:ii :vi  0.0] [:iii :vi  0.1] [:iv :vi  0.0] [:v :vi  0.0] [:vi  :vi  0.0] [:vii  :vi  0.1]
   [:i :vii 0.0]  [:ii :vii 0.0] [:iii :vii 0.0] [:iv :vii 0.0] [:v :vii 0.0] [:vi  :vii 0.0] [:vii  :vii 0.0]

   [:i :I   0.0]  [:ii :I   0.1] [:iii :I   0.1] [:iv :I   0.0] [:v :I   0.0] [:vi  :I   0.1] [:vii  :I   0.0]
   [:i :II  0.5]  [:ii :II  0.0] [:iii :II  0.1] [:iv :II  0.0] [:v :II  0.0] [:vi  :II  0.0] [:vii  :II  0.0]
   [:i :III 0.5]  [:ii :III 0.1] [:iii :III 0.0] [:iv :III 0.0] [:v :III 0.0] [:vi  :III 0.0] [:vii  :III 0.5]
   [:i :IV  0.0]  [:ii :IV  0.1] [:iii :IV  0.1] [:iv :IV  0.0] [:v :IV  0.0] [:vi  :IV  0.1] [:vii  :IV  0.1]
   [:i :V   0.0]  [:ii :V   0.5] [:iii :V   0.1] [:iv :V   0.0] [:v :V   0.0] [:vi  :V   0.5] [:vii  :V   0.5]
   [:i :VI  0.5]  [:ii :VI  0.3] [:iii :VI  0.2] [:iv :VI  0.0] [:v :VI  0.2] [:vi  :VI  0.0] [:vii  :VI  0.0]
   [:i :VII 0.0]  [:ii :VII 0.0] [:iii :VII 0.0] [:iv :VII 0.0] [:v :VII 0.1] [:vi  :VII 0.0] [:vii  :VII 0.0]

   [:I :i   0.0]  [:II :i   0.0] [:III :i   0.1] [:IV :i   0.0] [:V :i   0.1] [:VI  :i   0.1] [:VII  :i   0.0]
   [:I :ii  0.2]  [:II :ii  0.0] [:III :ii  0.0] [:IV :ii  0.0] [:V :ii  0.0] [:VI  :ii  0.2] [:VII  :ii  0.0]
   [:I :iii 0.1]  [:II :iii 0.0] [:III :iii 0.0] [:IV :iii 0.1] [:V :iii 0.0] [:VI  :iii 0.0] [:VII  :iii 0.0]
   [:I :iv  0.0]  [:II :iv  0.0] [:III :iv  0.0] [:IV :iv  0.0] [:V :iv  0.0] [:VI  :iv  0.0] [:VII  :iv  0.0]
   [:I :v   0.0]  [:II :v   0.2] [:III :v   0.2] [:IV :v   0.0] [:V :v   0.0] [:VI  :v   0.3] [:VII  :v   0.0]
   [:I :vi  0.2]  [:II :vi  0.0] [:III :vi  0.0] [:IV :vi  0.3] [:V :vi  0.2] [:VI  :vi  0.0] [:VII  :vi  0.0]
   [:I :vii 0.1]  [:II :vii 0.0] [:III :vii 0.0] [:IV :vii 0.0] [:V :vii 0.0] [:VI  :vii 0.0] [:VII  :vii 0.0]

   [:I :I   0.0]  [:II :I   0.0] [:III :I   0.0] [:IV :I   0.3] [:V :I   0.5] [:VI  :I   0.0] [:VII  :I   0.5]
   [:I :II  0.0]  [:II :II  0.0] [:III :II  0.0] [:IV :II  0.1] [:V :II  0.0] [:VI  :II  0.0] [:VII  :II  0.0]
   [:I :III 0.0]  [:II :III 0.0] [:III :III 0.0] [:IV :III 0.0] [:V :III 0.0] [:VI  :III 0.0] [:VII  :III 0.0]
   [:I :IV  0.3]  [:II :IV  0.0] [:III :IV  0.0] [:IV :IV  0.0] [:V :IV  0.1] [:VI  :IV  0.0] [:VII  :IV  0.0]
   [:I :V   0.4]  [:II :V   0.0] [:III :V   0.0] [:IV :V   0.7] [:V :V   0.0] [:VI  :V   0.0] [:VII  :V   0.1]
   [:I :VI  0.0]  [:II :VI  0.0] [:III :VI  0.1] [:IV :VI  0.0] [:V :VI  0.2] [:VI  :VI  0.0] [:VII  :VI  0.0]
   [:I :VII 0.0]  [:II :VII 0.0] [:III :VII 0.1] [:IV :VII 0.0] [:V :VII 0.0] [:VI  :VII 0.0] [:VII  :VII 0.0]])


(defn get-next-chord [chord]
  (let [choices (filter #(= chord (first %)) chord-chain)
        sum-p   (reduce + (map last choices))
        r       (rand sum-p)
        cumul-p (reduce (fn [a [_ f l]] (concat a [[(+ l (reduce max 0 (map first a))) f]])) [[0]] choices)
        e       (first (filter (fn [[f _]] (> f r)) cumul-p))]
    (last e)))

(defn get-chord-seq
  ([] (get-chord-seq :I))
  ([chord] (iterate get-next-chord chord)))

(defn major?
  [degree]
  (= (str degree) (clojure.string/upper-case degree)))

(defn minor?
  [degree]
  (not (major? degree)))

(defn degree->chord
  [degree note]
  (chord-degree (keyword (clojure.string/replace-first (clojure.string/lower-case degree) ":" ""))
                note
                (if (major? degree)
                    :major
                    :minor)))

;; Who wants a minior bassline starting at C2? I do!
(defn rand-bass []
  (let [chords (take 4 (get-chord-seq :i))
        notes (flatten (map #(take 4 (repeatedly (fn [] (rand-nth %)))) (map #(degree->chord % :C2) chords)))
        mean (float (/ (reduce + notes) (count notes)))]
    (println "--- Changing Bass ---")
    (println "Chords " chords)
    (println "Notes " notes)
    (println "Number of unique notes " (count (frequencies notes)))
    (println "Mean " mean)
    (println "Variance " (/ (reduce + (map (fn [x] (* ( - x mean) (- x mean))) notes)) (dec (count notes))))
    (buffer-write! buf-5 notes)))
