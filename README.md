# dbstep

CSV-Data Overtone Dubstep Generator

Developed at the Big Data Hackathon in San Francisco 22-23 Sep 2012 for converting NASDAQ quote data into Dubstep.

## Get started

Clone the repo

<pre>
git clone https://github.com/motin/dbstep.git -b develop
cd dbstep/
git submodule init
git submodule update
</pre>

Put csv files into a directory named sample_data next to the project directory. 
Download the Dubstep Construction Kit from [here](http://creativemonkeyz.com/free-stuff/dubstep-costruction-kit/) and extract it next to the project directory, so that it resides in a directory called Dubstep_construction_Kit.

Then, cd into the project directory and run:

<pre>
lein repl
</pre>

Within REPL, run:

<pre>
(require 'dbstep.main)
# what for Overtone to load
(in-ns 'dbstep.main)
</pre>

Now you can use the code available in src/live/main.clj to play around with Overtone. To actually use data as the input source you'll need to adapt src/dbstep/data.clj to import your csv files and of course write/adapt the logic that uses that data.

## License

Copyright 2012, dbstep Hackers, Distributed under the Eclipse Public License, the same as Clojure.