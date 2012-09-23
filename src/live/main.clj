(ns live.main
  ;(:require )
  ;(:use )
  ;(:import )
  )

; This file holds the best live code snippets in an orderly manner

; RHYTM

(do
  (buffer-write! buf-0 [1 1 1 0 0 0 0 0])  ;; kick
  (buffer-write! buf-1 [0 0 0 0 0 1 0 0])  ;; click
  (buffer-write! buf-2 [0 0 0 0 1 0 0 0])  ;; boom
  (buffer-write! buf-3 [0 1 0 0 0 0 0 0])) ;; subby

; MELODY 1

(do
  (buffer-write! buf-4 [0 45 34 67 56 0 0 0])  ;; melody
   )

(dubstep dub-note-bus
         :wobble (* BEAT-FRACTION 1)
         :lo-man 1)

; MELODY 2 - wip - not yet working

(do
  (buffer-write! buf-6 [76 100 56 45 88 90 92 95 76 100 56 45 88 90 92 95])  ;; foo instrument
   )

# Example usage:
(foo 60)
(foo 90)
(foo 120)
(foo 30)
(foo 60)
(foo 60)

(foo foo-note-bus)

; BASS

;; get the dubstep bass involved:
(dubstep dub-bass-note-bus
         :wobble 10
         :lo-man 1)

;; hard-coded notes
(do
  (buffer-write! buf-5 [89 12 34 45 23 78 34 56 56 89 34 34 45 34 23 35])  ;; bass
   )

;; Put a random C2-based baseline into buf-5
(rand-bass)

; PAD/saw - wip - not yet working

(do
  (buffer-write! buf-7   [(midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 60) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40)])  ;; supersaw2-1
  (buffer-write! buf-8   [(midi->hz 60) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40)])  ;; supersaw2-2
  (buffer-write! buf-9   [(midi->hz 55) (midi->hz 40) (midi->hz 40) (midi->hz 60) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40)])  ;; supersaw2-3
  (buffer-write! buf-10  [(midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40) (midi->hz 40)])  ;; supersaw2-4
   )

(def ssaw-rq 0.9)
(def ssaw-fil-mul 2)

(supersaw2 supersaw2-1-note-bus :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 supersaw2-2-note-bus :amp 2 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 supersaw2-3-note-bus :amp 3 :fil-mul ssaw-fil-mul :rq ssaw-rq)
(supersaw2 supersaw2-4-note-bus :amp 3 :fil-mul ssaw-fil-mul :rq ssaw-rq)

; SFX ??

(def siren-drop (sample (freesound-path 156440)))
(def karnage2 (sample (freesound-path 102144)))

(siren-drop)
(karnage2)

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

