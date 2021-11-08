# kGrass, kGrass(Unweighted)

## Requirements

* \>= OpenJDK 12

## Input Format

kGrassComparison assumes that the input graph *G = (**V**, **E**)* is undirected without self-loops.
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
supernode *s ∈ **S*** of the output **unweighted** summary graph *G_uwe = (**S**, **P**)* and information about each
superedge *p ∈ **P***. The first integer on each line following the line <Subnode of each
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




