(ns dbstep.instruments
  (:use [overtone.core]
        [overtone.inst.sampled-piano]
        [tooloud.synthesizers]
        [tooloud.patterns]
        [tooloud.rhythmic]
        [tooloud.fx]
        [tooloud.samples]
    )
  ;(:use )
  ;(:import )
  )

;!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;First Load an kit before load the fx
;!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

;Load 1º
(def master (audio-bus))

;Fx
(def rev (reverb-tooloud :in-bus master))
(def cmp (compressor-tooloud :in-bus master))
(def lim (limiter-tooloud :in-bus master))

;kit 1
(def k1 (kicki :out-bus master))
(def s1 (snr :out-bus master :bpm 140))

;kit 2
(def k2 (kickii :out-bus master))
(def s2 (snr :out-bus master :bpm 280))


;kit 3 (11!!!)
(def k3 (kickiii :out-bus master))
(def s3 (snr :out-bus master :bpm 560))

;kills
(kill k1)
(kill k2)
(kill k3)

(kill s1)
(kill s2)
(kill s3)

;woobles

(def dub1 (dub-base-i :out-bus master))
; Fill
(def fill (p (cycle (pattern derezzed 140))))
(def dub2 (dub-base-ii :out-bus master))


(ctl dub1 :wobble 6)
(ctl dub1 :note 80)
(ctl dub1 :note 40)


(ctl dub2 :wobble 3)
(ctl dub2 :note 70)
(ctl dub2 :wobble 6)
(ctl dub2 :note 40)

;stop
(stop)

;kill dubs

(kill dub1)
(kill dub2)

; Some demo instrument
(definst foo [note 60 vel 0.8]
  (let [freq (midicps note)]
    (* vel
       (env-gen (perc 0.01 0.2) 1 1 0 1 :action FREE)
       (+ (sin-osc (/ freq 2))
           (rlpf (saw freq) (* 1.1 freq) 0.4)))))

