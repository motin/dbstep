(ns live.demo
  ;(:require )
  ;(:use )
  ;(:import )
  )

; silence stuff

(kill dubstep)

(do
  (buffer-write! buf-0 [0 0 0 0 0 0 0 0])  ;; kick
  (buffer-write! buf-1 [0 0 0 0 0 0 0 0])  ;; click
  (buffer-write! buf-2 [0 0 0 0 0 0 0 0])  ;; boom
  (buffer-write! buf-3 [0 0 0 0 0 0 0 0])) ;; subby

(do
  (buffer-write! buf-4 [0 0 0 0 0 0 0 0])  ;; melody
  (buffer-write! buf-5 [0 0 0 0 0 0 0 0])) ;; bass

(kill base)
(kill base-mouse)











; DEMO

(stop)

(do
  (def r-cnt (root-cnt))
  (def b-cnt (beat-cnt))
  (def b-trg (beat-trg))
  (def r-trg (root-trg))
  (def m-cnt8  (meter-cnt meter-cnt-bus8 280))
  (def m-cnt16 (meter-cnt meter-cnt-bus16 16))

  (def kicks (doall
              (for [x (range 8)]
                (mono-sequencer :buf kick-s :beat-num x :sequencer buf-0))))

  (def clicks (doall
               (for [x (range 8)]
                 (mono-sequencer :buf click-s :beat-num x :sequencer buf-1))))

  (def booms (doall
              (for [x (range 8)]
                (mono-sequencer :buf boom-s :beat-num x :sequencer buf-2))))

  (def subbies (doall
                (for [x (range 8)]
                  (mono-sequencer :buf subby-s :beat-num x :sequencer buf-3))))

  (def dub-note-seq (note-sequencer buf-4 meter-cnt-bus8 dub-note-bus))

  (def dub-bass-note-seq (note-sequencer buf-5 meter-cnt-bus16 dub-bass-note-bus))

  (def foo-note-seq (note-sequencer buf-6 meter-cnt-bus16 foo-note-bus))

  (def supersaw2-1-note-seq (note-sequencer buf-7 meter-cnt-bus16 supersaw2-1-note-bus))
  (def supersaw2-2-note-seq (note-sequencer buf-8 meter-cnt-bus16 supersaw2-2-note-bus))
  (def supersaw2-3-note-seq (note-sequencer buf-9 meter-cnt-bus16 supersaw2-3-note-bus))
  (def supersaw2-4-note-seq (note-sequencer buf-10 meter-cnt-bus16 supersaw2-4-note-bus))

  )


(ctl r-trg :rate 100)




; start a melody, depending on realtime bid_volume

(def low_note 53)
(def note_pattern [0 2 3 5 7 8 10])
(def gamut (concat
	(map #(+ low_note %) note_pattern)
	(map #(+ (+ 12 low_note) %) note_pattern)
	(map #(+ (+ 24 low_note) %) note_pattern)
	;(map #(+ (+ 36 low_note) %) note_pattern)
	;(map #(+ (+ 48 low_note) %) note_pattern)
             ))

;(def gamut (flatten (map #(take 4 (repeatedly (fn [] (rand-nth %)))) (map #(degree->chord % :F2) (take 4 (get-chord-seq))))))

(def num_notes (count gamut))
(def ask_volume (map #(Float/parseFloat %) (map :bid_volume volume_280_tatum)))
(def max_av (reduce max ask_volume))
(def norm_av (map #(int %)
	(map #(* (- num_notes 1) %) (map #(/ % max_av) ask_volume))))
(def filter_av_intermediate (map #(nth gamut %) norm_av))
(def filter_av (map #(if (= low_note %) 0 %) filter_av_intermediate))


(def bar (into [] filter_av))
(do(buffer-write! buf-4 bar))

(dubstep dub-note-bus
         :wobble (* BEAT-FRACTION 1)
         :lo-man 1)




; start a base line, depending on realtime bid_volatility

(def gamut (flatten (map #(take 4 (repeatedly (fn [] (rand-nth %)))) (map #(degree->chord % :F2) (take 4 (get-chord-seq))))))

(def num_notes (count gamut))
(def ask_volume (map #(Float/parseFloat %) (map :bid_volatility volume_280_tatum)))
(def max_av (reduce max ask_volume))
(def norm_av (map #(int %)
	(map #(* (- num_notes 1) %) (map #(/ % max_av) ask_volume))))
(def filter_av_intermediate (map #(nth gamut %) norm_av))
(def filter_av (map #(if (= low_note %) 0 %) filter_av_intermediate))


(def melnotes (partition 16 filter_av))

(def bar (into [] (nth melnotes 1)))
(do(buffer-write! buf-5 bar))

(dubstep dub-bass-note-bus
         :wobble 10
         :lo-man 1)




; add a basic rhytm

(do
  (buffer-write! buf-0 [1 1 1 1 1 1 1 1])  ;; kick
  (buffer-write! buf-1 [1 1 1 1 1 1 1 1])  ;; click
  (buffer-write! buf-2 [0 0 0 0 1 0 0 0])  ;; boom
  (buffer-write! buf-3 [0 1 0 0 0 0 0 0])) ;; subby




; start a simple wobble wobbles and baseline based upon aggregated data

; todo - actually base the below synth on aggregated data
(partition 8 (map :trade_diversity diversity_by_10min)


(ctl r-trg :rate 140)

(def base-data
  (demo 999999

      (
        let [
        foo1 (mouse-x:kr 30 40)
        foo2 (mouse-y:kr 40 70)
        foo3 (mouse-x:kr 1 6)
        foo4 (mouse-y:kr 6 12)
        foo5 (mouse-x:kr 0.8 0.9)
        foo6 (mouse-y:kr 1 1.1)
        bpm 140
       notes [foo1 foo2 29 41 44 48]
       ;trig (impulse:kr 1)
       trig (impulse:kr (/ bpm 140))
       freq (midicps (lag (demand trig 0 notes) 0.25))
       swr (demand trig 0 (dxrand [foo3 foo4] INF))
       sweep (lin-exp (lf-tri swr) -1 1 notes 3000)
       wob (apply + (saw (* freq [foo5 foo6])))
       wob (lpf wob sweep)
       wob (* 1 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))
       kickenv (decay (t2a (demand (impulse:kr (/ bpm 30)) 0 (dseq [[1 0 0 1 0 0 1 0 1 0 0 1 0 0 0 0]
                                                                    [1 0 0 1 0 0 1 0 1 0 0 1 0 0 10 0]
                                                                    [1 0 0 0 0 0 1 0 1 1 0 1 0 0 0 0]
                                                                    [1 0 0 1 0 0 1 0 1 0 0 1 0 0 1 1]] INF))) 0.7)
       kick (* (* kickenv 7) (sin-osc (+ 40 (* kickenv kickenv kickenv 200))))
       kick (clip2 kick 3)

       snare (* 3 (pink-noise [1 1]) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))
       snare (+ snare (bpf (* 4 snare) 2000))
       snare (clip2 snare 3)]

        (println "trig")
        (println trig)

        (out 0 (pan2 (+ wob kick snare) 2))

        )

    ))



; attempts to make it read new data for each beat

(def songln 120)
(def bpm 140)

(def secondsperbeat (/ 60 bpm))
(def beats (/ songln secondsperbeat))


(def tstart (System/currentTimeMillis))



(def base-data
  (demo songln

      (
        let [
        ;t (- (System/currentTimeMillis) tstart)

        foo1 (rand 30 40)
        foo2 (mouse-y:kr 40 70)
        foo3 (mouse-x:kr 1 6)
        foo4 (mouse-y:kr 6 12)
        foo5 (mouse-x:kr 0.8 0.9)
        foo6 (mouse-y:kr 1 1.1)
        bpm 140
       notes [foo1 foo2 30 40 70 80]
       trig (impulse:kr (/ bpm 140))
       freq (midicps (lag (demand trig 0 notes) 0.25))
       swr (demand trig 0 (dxrand [foo3 foo4] INF))
       sweep (lin-exp (lf-tri swr) -1 1 notes 3000)
       wob (apply + (saw (* freq [foo5 foo6])))
       wob (lpf wob sweep)
       wob (* 1 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))
       kickenv (decay (t2a (demand (impulse:kr (/ bpm 30)) 0 (dseq [[1 0 0 1 0 0 1 0 1 0 0 1 0 0 0 0]
                                                                    [1 0 0 1 0 0 1 0 1 0 0 1 0 0 10 0]
                                                                    [1 0 0 0 0 0 1 0 1 1 0 1 0 0 0 0]
                                                                    [1 0 0 1 0 0 1 0 1 0 0 1 0 0 1 1]] INF))) 0.7)
       kick (* (* kickenv 7) (sin-osc (+ 40 (* kickenv kickenv kickenv 200))))
       kick (clip2 kick 3)

       snare (* 3 (pink-noise [1 1]) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))
       snare (+ snare (bpf (* 4 snare) 2000))
       snare (clip2 snare 3)]

        (print "adsasd")

        (out 0 (pan2 (+ wob kick snare) 2))

        )

    ))


; the rest was not used in demo



  
; start a pad, depending on SOMETHING IN DATA, generated from data

(def ssaw-rq 0.9)
(def ssaw-fil-mul 2)

(def pad [51 55 57])

(supersaw2 (midi->hz (nth pad 1)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz (nth pad 2)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz (nth pad 3)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)

(def pad [51 55 57])

(supersaw2 (midi->hz (nth pad 1)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz (nth pad 2)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 (midi->hz (nth pad 3)) :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)







; RESET

(stop)

(do
  (def r-cnt (root-cnt))
  (def b-cnt (beat-cnt))
  (def b-trg (beat-trg))
  (def r-trg (root-trg))
  (def m-cnt8  (meter-cnt meter-cnt-bus8 280))
  (def m-cnt16 (meter-cnt meter-cnt-bus16 16))

  (def kicks (doall
              (for [x (range 8)]
                (mono-sequencer :buf kick-s :beat-num x :sequencer buf-0))))

  (def clicks (doall
               (for [x (range 8)]
                 (mono-sequencer :buf click-s :beat-num x :sequencer buf-1))))

  (def booms (doall
              (for [x (range 8)]
                (mono-sequencer :buf boom-s :beat-num x :sequencer buf-2))))

  (def subbies (doall
                (for [x (range 8)]
                  (mono-sequencer :buf subby-s :beat-num x :sequencer buf-3))))

  (def dub-note-seq (note-sequencer buf-4 meter-cnt-bus8 dub-note-bus))

  (def dub-bass-note-seq (note-sequencer buf-5 meter-cnt-bus16 dub-bass-note-bus))

  (def foo-note-seq (note-sequencer buf-6 meter-cnt-bus16 foo-note-bus))

  (def supersaw2-1-note-seq (note-sequencer buf-7 meter-cnt-bus16 supersaw2-1-note-bus))
  (def supersaw2-2-note-seq (note-sequencer buf-8 meter-cnt-bus16 supersaw2-2-note-bus))
  (def supersaw2-3-note-seq (note-sequencer buf-9 meter-cnt-bus16 supersaw2-3-note-bus))
  (def supersaw2-4-note-seq (note-sequencer buf-10 meter-cnt-bus16 supersaw2-4-note-bus))

  )
