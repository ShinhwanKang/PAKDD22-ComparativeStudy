#SSumM, SSumM(Unweighted)

## Requirements

* \>= OpenJDK 12

## Input Format

SSumMComparison assumes that the input graph *G = (**V**, **E**)* is undirected without self-loops.
Thus, the format of an input file is as follows.
Each line represents a single edge.
Each edge *{u, v} ∈ **E*** joins two distinct nodes *u != v ∈ **V***, separated by a tab.
Each node *v ∈ **V*** is assigned to a unique integer id.
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
supernode *s ∈ **S*** of the output **weighted** summary graph *G_we = (**S**, **P**, ω)* and information about each
superedge *p ∈ **P***. The first integer on each line following the line <Subnode of each
supernode> represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line <Superedge
info> represents a single superedge. The three integers separated
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
supernode *s ∈ **S*** of the output **unweighted** summary graph *G_uwe = (**S**, **P**)* and information about each
superedge *p ∈ **P***. The first integer on each line following the line <Subnode of each
supernode> represents the id of the supernode, and the following integers separated by
tabs represent the ids of the subnodes belonging to that supernode.
Each line following  the line <Superedge
info> represents a single superedge. The two integers separated
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




