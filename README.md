# dbstep

CSV-Data Overtone Dubstep Generator

Turns csv data (originated from NASDAQ quote data) into Dubstep music using Overtone

## Get started

Put csv files into a directory named sample_data next to the project directory. 
Download the Dubstep Construction Kit from http://creativemonkeyz.com/free-stuff/dubstep-costruction-kit/ and extract it next to the project directory, so that it resides in a directory called Dubstep_construction_Kit.

Then, cd into the project directory and run:

lein repl

Within REPL, run:

(require 'dbstep.main)
(in-ns 'dbstep.main)

## License

Copyright 2012, dbstep Hackers, Distributed under the Eclipse Public License, the same as Clojure.
