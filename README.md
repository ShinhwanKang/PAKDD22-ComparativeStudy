# Are Edge Weights in Summary Graphs Useful? - A Comparative Study

## Overview
We conduct a systematic comparison between a weighted graph summarization model and an unweighted graph summarization model.
Our contributions are as follow:

* *Systematic Comparison*: coducting the first systematic comparison between two extensively-studied graph summarization models using 3 search algorithms, 8 datasets, and 5 evaluation metrics.

* *Unexpected Observation*: leading to a surprising observation that unweighted models is significantly better than weighted ones in all considered aspects.

* *Improvement of the State-of-the-art Algorithm*: improving a state-of-the-art graph-summarizaion algorithm for the weighted model by exploiting the observation.

## Appendix
The online-appendix is available at [Here](https://anonymous.4open.science/r/PAKDD22-D880/PAKDD22_Appendix.pdf)


## Code
The algorithms of each path is listed below.

|Path| Weighted                                       | Unweighted                   
|:----------:|:-----------------------------------------------:| :---------------------:|
| kGrassComparison   | kGrass         |  kGrass (Unweighted)    
| SSumMComparison    | SSumM        | SSumM (Unweighted)       
| MoSSo-LossyComparison     | MoSSo-Lossy         | MoSSo-Lossy (Unweighted)  


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

 
---
# kGrass, kGrass(Unweighted)
## Requirements

* \>= OpenJDK 12

## Input Format

kGrassComparison assumes that the input graph *G = (**V**, **E**)* is undirected without self-loops.
Thus, the format of an input file is as follows. 
Each line represents a single edge. 
Each edge {u, v} ∈ **E** joins two distinct nodes u != v ∈ **V**, separated by a tab. 
Each node v ∈ **V** is assigned to a unique integer id. 
The format of the example file is given below.

### Example Input Format
```
    0   1
    2   3
    3   4
```
- The example consists of 5 nodes with 3 edges.

## Execution

```
    java -jar kGrassComparison.jar [data path] [target compression ratio] [target reconstruction error] [model type]
    ex) java -jar kGrassComparison.jar ./email-enron.txt 0.4 1 uwe    
```

### argument
- data path: Path to the input text file
- target compression ratio [0,1]: The desired size of a summary graph compared relative to the input graph size in bits
- target reconstruction error {1,2}: The reconstruction error measure to be used.
- model type {we, uwe}: The model type to be used

## Output Format

### kGrass

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **weighted** summary graph *G_we = (**S**, **P**, ω)* and information about each
superedge p ∈ **P**. The first integer on each line following the line <Subnode of each
supernode> represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode. 
Each line following  the line <Superedge 
info> represents a single superedge. The three integers separated
by tabs represent the id of the source supernode, the id of the destination supernode, and
the weight of the superedge (i.e., the number of subedges belonging to the superedge).

* Output
```
    Model Type: we
    |V|: 36692
    |E|: 183831
    Compression Ratio	39.98541285884974%
    L1 Reconstruction Error	3.8248179192134426E-4
    |S|	985
    |P|	61086
```


* Output file
```
    <Subnode of each supernode>
    5	5
    9	9
    27	27
    45	45  39  59
    .
    .
    <Superedge info>
    .
    .
    36357   36357   10
    36444   36444   24
    36588   36588   10
    36604   36604   10
```


### kGrass(Unweighted)

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **unweighted** summary graph *G_uwe = (**S**, **P**)* and information about each
superedge p ∈ **P**. The first integer on each line following the line <Subnode of each
supernode> represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line <Superedge
info> represents a single superedge. The two integers separated
by tabs represent the id of the source supernode and the id of the destination supernode.

* Output
```
    Model Type: uwe
    |V|: 36692
    |E|: 183831
    Compression Ratio	39.991402550397524%
    L1 Reconstruction Error	1.3075126127435666E-4
    |S|	3380
    |P|	76746
```


* Output file
```
    <Subnode of each supernode>
    5	5
    9	9
    27	27
    46	46
    .
    .
    <Superedge info>
    53	46
    54	53
    56	5
    56	27
    .
    .
```
---
# MoSSo-Lossy, MoSSo-Lossy(Unweighted)

## Requirements

* \>= OpenJDK 12

## Input Format

*MoSSo-LossyComparison* assumes that The input file lists the additions in an undirected graph without self-loops in
the order that they arrive. Each line corresponds to an edge addition. Each
line consists of a source node id, a destination node id, and an indicator, which are integers separated by a tab. 
The format of the example file is given below.

### Example Input Format
```
    0   1   1
    2   3   1
    3   4   1
```
- The example consists of 3 additions (i.e., 5 nodes with 3 edges).

## Execution

```
    java -jar MoSSo-LossyComparsion.jar [data path] [model type]
    ex) java -jar MoSSo-LossyComparsion.jar ./email-enron.txt we  
```

### argument
- data path: Path to the input text file
- model type {we, uwe}: The model type to be used

## Output Format

### MoSSo-Lossy

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **weighted** summary graph *G_we = (**S**, **P**, ω)* and information about each
superedge p ∈ **P**. The first integer on each line following the line "\<Subnode of each supernode\>" represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line "\<Superedge info\>" represents a single superedge. The three integers separated
by tabs represent the id of the source supernode, the id of the destination supernode, and
the weight of the superedge (i.e., the number of subedges belonging to the superedge).

* Output
```
    Model Type: we
    |V|	 36692
    |E|	 183831
    |S|	 4850
    |P|	 12841
    Compression Ratio	 15.428383%
    L1 Reconstruction Error	 4.551099050444017E-4
    L2 Reconstruction Error	 4.1576597176530075E-7
```


* Output file
```
    <Subnode of each supernode>
    0       26077   26085   26076   26084   26078   26080   26075   26086
    3430    31185
    6860    33327
    5673    28028
    .
    .
    <Superedge info>
    31584   31584   3
    31584   17215   3
    29287   21040   42
    .
    .
```


### MoSSo-Lossy(Unweighted)

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **unweighted** summary graph *G_uwe = (**S**, **P**)* and information about each
superedge p ∈ **P**. The first integer on each line following the line  "\<Subnode of each
supernode\>" represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line  "\<Superedge
info\>" represents a single superedge. The two integers separated
by tabs represent the id of the source supernode and the id of the destination supernode.

* Output
```
    Model Type: uwe
    |V|	 36692
    |E|	 183831
    |S|	 12896
    |P|	 57345
    Compression Ratio	 37.077922%
    L1 Reconstruction Error	 1.1157377576891236E-4
    L2 Reconstruction Error	 2.8788272074421435E-7
```


* Output file
```
    <Subnode of each supernode>
    0   7282
    6860    3685
    8047    11279   4369
    5673    25080   20307
    .
    .
    <Superedge info>
    12760   1296
    12760   16
    12760   3923
    6380    2144
    .
    .
```
---

# SSumM, SSumM(Unweighted)

## Requirements

* \>= OpenJDK 12

## Input Format

SSumMComparison assumes that the input graph *G = (**V**, **E**)* is undirected without self-loops.
Thus, the format of an input file is as follows.
Each line represents a single edge.
Each edge {u, v} ∈ **E** joins two distinct nodes u != v ∈ **V**, separated by a tab.
Each node v ∈ **V** is assigned to a unique integer id.
The format of the example file is given below.

### Example Input Format
```
    0   1
    2   3
    3   4
```
- The example consists of 5 nodes with 3 edges.

## Execution

```
    java -jar SSumMComparison.jar [data path] [target compression ratio] [target reconstruction error] [model type]
    ex) java -jar SSumMComparison.jar ./email-enron.txt 0.4 1 uwe    
```

### argument
- data path: Path to the input text file
- target compression ratio [0,1]: The desired size of a summary graph compared relative to the input graph size in bits
- target reconstruction error {1,2}: The reconstruction error measure to be used.
- model type {we, uwe}: The model type to be used

## Output Format

### SSumM

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **weighted** summary graph *G_we = (**S**, **P**, ω)* and information about each
superedge p ∈ **P**. The first integer on each line following the line  "\<Subnode of each
supernode\>" represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line "\<Superedge
info\>" represents a single superedge. The three integers separated
by tabs represent the id of the source supernode, the id of the destination supernode, and
the weight of the superedge (i.e., the number of subedges belonging to the superedge).

* Output
```
    Model Type:     we
    ---------------------------------------------------
    |V| 36692
    |E| 183831
    iter: 1
    iter: 2
    .
    .
    ---------------------------------------------------
    |S|	14201
    |P|	47942
    Compression Ratio	39.832586%
    L1 Reconstriction Error	1.62e-04
```


* Output file
```
    <Subnode of each supernode>
    29126   32989   32990
    1       1
    25863   22032
    3       3       4
    .
    .
    <Superedge info>
    31584   31584   3
    31584   17215   3
    29287   21040   42
    .
    .
```


### SSumM(Unweighted)

The output file contains information about subnodes (nodes in *G*) belonging to each
supernode s ∈ **S** of the output **unweighted** summary graph *G_uwe = (**S**, **P**)* and information about each
superedge p ∈ **P**. The first integer on each line following the line  "\<Subnode of each
supernode\>" represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line "\<Superedge
info\>" represents a single superedge. The two integers separated
by tabs represent the id of the source supernode and the id of the destination supernode.

* Output
```
    Model Type: 	uwe
    ---------------------------------------------------
    |V| 36692
    |E| 183831
    iter: 1
    iter: 2
    .
    .
    frac	0.4383425410897358
    ---------------------------------------------------
    |S|	12909
    |P|	63301
    Compression Ratio	39.999781%
    L1 Reconstriction Error	1.10e-04
```


* Output file
```
    <Subnode of each supernode>
    34787   31202
    1	    1
    32151   25092
    3       3
    .
    .
    <Superedge info>
    12824   7503
    12824   8111
    12824   4383
    12824   4620
    .
    .
```













