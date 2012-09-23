(ns dbstep.stepseq)
        
# Some demo instrument
(definst foo [note 60 vel 0.8]
  (let [freq (midicps note)]
    (* vel
       (env-gen (perc 0.01 0.2) 1 1 0 1 :action FREE)
       (+ (sin-osc (/ freq 2))
           (rlpf (saw freq) (* 1.1 freq) 0.4)))))

# Example usage:
(foo 60)
(foo 120)
(foo 30)
(foo 60)
(foo 60)

(def kick  (sample "~/Dropbox/BigMusic/Dubstep_construction_Kit/_Dubstep Free Pack/Drums/Samples/Demon/Demon BD.wav"))
(def snare (sample "~/Dropbox/BigMusic/Dubstep_construction_Kit/_Dubstep Free Pack/Drums/Samples/Kirk/Kirk SD.wav"))
(def hit   (sample "~/Dropbox/BigMusic/Dubstep_construction_Kit/_Dubstep Free Pack/Drums/Samples/Hop/Hop HH.wav"))
(def insts {:kick  kick
            :snare snare
            :hit   hit
            :foo   foo})
            
(def pat {:kick  [1 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0]
          :snare [0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0]
          :hit   [0 0 1 0 0 0 0 0 0 1 0 0 0 1 0 0]
          :foo   [60 61 63 65 67 69 67 65 63 65 67 65 63 65 63 61] })

(def metro (metronome 140))

(defn play-pat [beat i]
  (let [t (mod beat 4)
        p (vec (take 4 (drop (* 4 t) (pat i))))]
    (if (= 1 (p 0)) (at (metro (+ 0.00 beat)) ((insts i))))
    (if (= 1 (p 1)) (at (metro (+ 0.25 beat)) ((insts i))))
    (if (= 1 (p 2)) (at (metro (+ 0.50 beat)) ((insts i))))
    (if (= 1 (p 3)) (at (metro (+ 0.75 beat)) ((insts i))))
    (if (> 30 (p 0)) (at (metro (+ 0.00 beat)) ((insts i p))))
    ))


(defn player [beat]
  (doseq [i (keys insts)] (play-pat beat i))
  (apply-at (metro (inc beat)) #'player (inc beat) []))

(player (metro))

(metro :bpm 160)
(metro :bpm 90)

(defn player [beat] "stop")

