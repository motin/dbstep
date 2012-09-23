# dbstep

CSV-Data Overtone Dubstep Generator

Turns csv data (originated from NASDAQ quote data) into Dubstep music using Overtone

Put csv files into a directory named sample_data next to the project directory. Then, cd into the project directory and run:

lein repl

Within REPL, run:

(require 'dbstep.main)
(in-ns 'dbstep.main)

## License

Copyright 2012, dbstep Hackers, Distributed under the Eclipse Public License, the same as Clojure.
