(ns dbstep.main
  (:use [overtone.live]
;  (:use [overtone.live]
        [dbstep.data]
    )
  (:import java.util.Date)
)

;(connect-external-server 57110)

(use
        '[overtone.inst.sampled-piano]
        '[tooloud.synthesizers]
        '[tooloud.patterns]
        '[tooloud.rhythmic]
        '[tooloud.fx]
        '[tooloud.samples]
        '[dbstep.instruments]
        '[dbstep.sequencer]
)

;; Useful for variable inspection
(defn dbg [x] (println x) x)

;; Now, let's start up all the synths, mapping each buffer to a particular instrument
(do
  (def r-cnt (root-cnt))
  (def b-cnt (beat-cnt))
  (def b-trg (beat-trg))
  (def r-trg (root-trg))
  (def m-cnt8  (meter-cnt meter-cnt-bus8 8))
  (def m-cnt280  (meter-cnt meter-cnt-bus280 280))
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

  (def dub-note-seq (note-sequencer buf-4 meter-cnt-bus280 dub-note-bus))
  
  (def dub-bass-note-seq (note-sequencer buf-5 meter-cnt-bus16 dub-bass-note-bus))

  (def foo-note-seq (note-sequencer buf-6 meter-cnt-bus16 foo-note-bus))

  (def supersaw2-1-note-seq (note-sequencer buf-7 meter-cnt-bus16 supersaw2-1-note-bus))
  (def supersaw2-2-note-seq (note-sequencer buf-8 meter-cnt-bus16 supersaw2-2-note-bus))
  (def supersaw2-3-note-seq (note-sequencer buf-9 meter-cnt-bus16 supersaw2-3-note-bus))
  (def supersaw2-4-note-seq (note-sequencer buf-10 meter-cnt-bus16 supersaw2-4-note-bus))
  
  )

; Empty palette

(do
  (buffer-write! buf-0 [0 0 0 0 0 0 0 0])  ;; kick
  (buffer-write! buf-1 [0 0 0 0 0 0 0 0])  ;; click
  (buffer-write! buf-2 [0 0 0 0 0 0 0 0])  ;; boom
  (buffer-write! buf-3 [0 0 0 0 0 0 0 0])) ;; subby

(do
  (buffer-write! buf-4 [0 0 0 0 0 0 0 0])  ;; melody
  (buffer-write! buf-5 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0])) ;; bass




