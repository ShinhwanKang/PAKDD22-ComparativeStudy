# Are Edge Weights in Summary Graphs Useful? - A Comparative Study

## Overview
We conduct a systematic comparison between a weighted graph summarization model and an unweighted graph summarization model.
Our contributions are as follow:

* *Systematic Comparison*: coducting the first systematic comparison between two extensively-studied graph summarization models using 3 search algorithms, 8 datasets, and 5 evaluation metrics.

* *Unexpected Observation*: leading to a surprising observation that unweighted models is significantly better than weighted ones in all considered aspects.

* *Improvement of the State-of-the-art Algorithm*: improving a state-of-the-art graph-summarizaion algorithm for the weighted model by exploiting the observation.


## Code
The algorithms used in the paper is available at ```./code/``` and ```./Home.html```.

<!-- ## Algorithms
The algorithms of each path is listed below.

|           | Weighted                                       | Unweighted                   
| ----------|:-----------------------------------------------:| :---------------------:|
| kGrassComparison    | kGrass         |  kGrass (Unweighted)    
| SSumMComparison     | SSumM        | SSumM (Unweighted)       
| MoSSo-LossyComparison     | MoSSo-Lossy         | MoSSo-Lossy (Unweighted)     -->


## Datasets

|Name|#Nodes|#Edges|Summary|Source|Download|
|:---:|:---:|:---:|:---:|:---:|:---:|
|Email-Enron (EE)|36,692|183,831|Email|[SNAP](https://snap.stanford.edu/data/email-Enron.html)|[LINK](https://snap.stanford.edu/data/email-Enron.txt.gz)|
|DBLP (DB)|317,080|1,049,866|Collaboration|[SNAP](https://snap.stanford.edu/data/com-DBLP.html)|[LINK](https://snap.stanford.edu/data/bigdata/communities/com-dblp.ungraph.txt.gz)|
|Amazon0601 (A6)|403,394|2,443,408|Co-purchase|[SNAP](https://snap.stanford.edu/data/amazon0601.html)|[LINK](https://snap.stanford.edu/data/amazon0601.txt.gz)|
|CNR-2000 (C2)|325,557|2,738,969|Hyperlinks|[LAW](http://law.di.unimi.it/webdata/cnr-2000/)|[LINK](http://law.di.unimi.it/webdata/cnr-2000/)|
|Skitter (SK)|1,696,415|11,095,298|Internet|[SNAP](https://snap.stanford.edu/data/as-Skitter.html)|[LINK](https://snap.stanford.edu/data/as-skitter.txt.gz)|
|LiveJournal (LJ)|3,997,962|34,681,189|Social|[SNAP](https://snap.stanford.edu/data/com-LiveJournal.html)|[LINK](https://snap.stanford.edu/data/bigdata/communities/com-lj.ungraph.txt.gz)|
|DBPedia (DP)|18,268,991|126,890,209|Hyperlinks|[KONECT](http://konect.cc/networks/dbpedia-link/)|[LINK](http://konect.cc/files/download.tsv.dbpedia-link.tar.bz2)|
|WebLarge (WL)|18,483,186|261,787,258|Hyperlinks|[LAW](https://law.di.unimi.it/webdata/uk-2002/)|[LINK](http://data.law.di.unimi.it/webdata/uk-2002/uk-2002.graph)|

