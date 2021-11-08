#MoSSo-Lossy, MoSSo-Lossy(Unweighted)

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




