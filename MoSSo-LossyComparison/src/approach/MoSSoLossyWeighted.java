package approach;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Double.MIN_VALUE;

public class MoSSoLossyWeighted {
    public int sampleNumber = 300;
    public double escapeProb = 0.3;

    public int iteration = 0;
    public static final double log2 = Math.log(2.0);
    public int maxWeight = 1;

    public Int2IntOpenHashMap subnodeIDX2supernode= new Int2IntOpenHashMap();

    public int subnodeNewIdx = 0;
    public double sparseEdgeCost;
    public int supernodeNewIdx = 0;

    public static final Int2IntOpenHashMap subnodeID2IDX = new Int2IntOpenHashMap();
    public static final Int2IntOpenHashMap IDX2subnodeID = new Int2IntOpenHashMap();

    public static final IntArrayList deleteSupernodeList = new IntArrayList();
    public static final IntArrayList deleteSubnodeList = new IntArrayList();

    public int numSuperedge=0;
    public int numSubedge=0;

    public static final ObjectArrayList<IntArrayList> subnodeAdjList = new ObjectArrayList<IntArrayList>();
    public static final ObjectArrayList<IntOpenHashSet> supernodeAdjList = new ObjectArrayList<IntOpenHashSet>();
    public static final ObjectArrayList<Int2IntOpenHashMap> superedgeType = new ObjectArrayList<Int2IntOpenHashMap>();


    public static final ObjectArrayList<Int2DoubleOpenHashMap> datacostList = new ObjectArrayList<Int2DoubleOpenHashMap>();
    public static final ObjectArrayList<Int2IntOpenHashMap> edgeCntList = new ObjectArrayList<Int2IntOpenHashMap>();

    public static final ObjectArrayList<IntOpenHashSet> supernodeSubnodeList = new ObjectArrayList<IntOpenHashSet>();

    public IntArrayList[] minHash;
    public IntArrayList[] hf;

    public int n_hash = 4;
    public final int INF = 0x7FFFFFFF;
    public final ThreadLocalRandom random=ThreadLocalRandom.current();

    public int getSubedgeCount(int Su, int Sv){
        if(Su == Sv) return edgeCntList.get(Su).get(Sv) >> 1;
        else return edgeCntList.get(Su).get(Sv);
    }
    public int getSubnodeDegree(int idx){
        return subnodeAdjList.get(idx).size();
    }

    public int getSupernodeSize(int IDX){
        return supernodeSubnodeList.get(IDX).size();
    }
    public int getNumSupernode(){
        return supernodeNewIdx - deleteSupernodeList.size();
    }
    public int getNumSubnode(){
        return subnodeNewIdx - deleteSubnodeList.size();
    }

    public IntOpenHashSet getSupernodeList(){
        IntOpenHashSet snList = new IntOpenHashSet();
        for(int i=0;i<supernodeNewIdx;i++){
            snList.add(i);
        }
        for(int i : deleteSupernodeList) {
            snList.remove(i);
        }
        return snList;
    }

    public void hashInitialization(){
        minHash = new IntArrayList[n_hash];
        hf = new IntArrayList[n_hash];
        for(int i = 0; i< n_hash; i++){
            minHash[i] = new IntArrayList();
            hf[i] = new IntArrayList();
        }
    }
    public int newSubnode(){
        int newIdx;
        if(deleteSubnodeList.size()==0){
            newIdx = subnodeNewIdx++;
            subnodeAdjList.add(newIdx, new IntArrayList());
            for (int i = 0; i< n_hash; i++){
                minHash[i].add(newIdx, INF);
                hf[i].add(newIdx, random.nextInt(0x7FFFFFFE));
            }
        }else{
            newIdx = deleteSubnodeList.removeInt(0);
            subnodeAdjList.set(newIdx, new IntArrayList());
            for (int i = 0; i< n_hash; i++){
                minHash[i].set(newIdx, INF);
                hf[i].set(newIdx, random.nextInt(0x7FFFFFFE));
            }
        }
        sparseEdgeCost = 2 * log2(getNumSubnode());
        return newIdx;
    }

    public int newSupernode(){
        int newIdx;
        if(deleteSupernodeList.size()==0){
            newIdx = supernodeNewIdx++;
            supernodeAdjList.add(newIdx, new IntOpenHashSet());
            supernodeSubnodeList.add(newIdx, new IntOpenHashSet());
            datacostList.add(newIdx, newInt2DoubleHashMap());
            edgeCntList.add(newIdx, newInt2IntHashMap());
        }else{
            newIdx = deleteSupernodeList.removeInt(0);
        }

        return newIdx;
    }

    public void removeSupernode(int V){
        int[] NBDs = getSupernodeNeighborList(V).toIntArray();
        for(int NBD : NBDs) {
            updateSuperedge(V, NBD, false);
        }
        if(!deleteSupernodeList.contains(V)) {
            deleteSupernodeList.add(V);
        }
        for(int NBD : new IntArrayList(edgeCntList.get(V).keySet()) ){
            edgeCntList.get(NBD).remove(V);
        }
        edgeCntList.get(V).clear();

        for(int NBD : new IntArrayList(datacostList.get(V).keySet())){
            datacostList.get(NBD).remove(V);
        }
        datacostList.get(V).clear();
    }

    public Int2IntOpenHashMap newInt2IntHashMap(){
        Int2IntOpenHashMap newMap = new Int2IntOpenHashMap(0);
        newMap.defaultReturnValue(0);
        return newMap;
    }

    public Int2DoubleOpenHashMap newInt2DoubleHashMap(){
        Int2DoubleOpenHashMap newMap = new Int2DoubleOpenHashMap(0);
        newMap.defaultReturnValue(0);
        return newMap;
    }

    public IntSet getSupernodeNeighborList(int V){
        return supernodeAdjList.get(V);
    }

    public MoSSoLossyWeighted(){
        hashInitialization();
    }

    public int[] getRandomNeighbors(int v, int sampleNum) {

        int[] randomNeighbors = new int[sampleNum];
        IntArrayList subNBDList = subnodeAdjList.get(v);
        int neighborSize = subNBDList.size();

        for (int i = 0; i < sampleNum; i++)
            randomNeighbors[i]=subNBDList.getInt(random.nextInt(neighborSize));

        return randomNeighbors;
    }

    public void tryUpdateSuperedge(int src, int dst, boolean add){
        final int SRC = subnodeIDX2supernode.get(src), DST = subnodeIDX2supernode.get(dst);
        double pi = getPi(SRC, DST);
        double edgecnt = getSubedgeCount(SRC, DST);
        double weight = edgecnt/pi;
        double dense = pi * entropy(weight);
        double sparse = edgecnt * sparseEdgeCost;
        double superEdgeCost = 2 * log2(getNumSupernode()) + log2(pi);

        if(!sparseORdense(sparse, superEdgeCost, dense)){
            if (!supernodeAdjList.get(SRC).contains(DST)) updateSuperedge(SRC, DST, true);
            datacostList.get(SRC).put(DST, dense); datacostList.get(DST).put(SRC, dense);
        }
        else{
            datacostList.get(SRC).put(DST, -sparse); datacostList.get(DST).put(SRC, -sparse);
        }
    }

    public void commonProcessEdge(int srcID, int dstID){
        iteration++;
        if(!subnodeID2IDX.containsKey(srcID)) addNode(srcID);
        if(!subnodeID2IDX.containsKey(dstID)) addNode(dstID);

        int srcIDX = subnodeID2IDX.get(srcID); int dstIDX = subnodeID2IDX.get(dstID);

        updateSubedge(srcIDX, dstIDX);
        tryUpdateSuperedge(srcIDX, dstIDX, true);
        updateHash(srcIDX, dstIDX);

        int which = random.nextInt(n_hash);
        if(getSubnodeDegree(srcIDX)>0){
            int[] srcnbd = getRandomNeighbors(srcIDX, sampleNumber);
            _processEdge(srcnbd, which);
        }
        if(getSubnodeDegree(dstIDX)>0){
            int[] dstnbd = getRandomNeighbors(dstIDX, sampleNumber);
            _processEdge(dstnbd, which);
        }

    }

    public int getPi(final int Su, final int Sv){
        int pi = getSupernodeSize(Su); pi *= getSupernodeSize(Sv);
        if(Su == Sv){
            pi -= getSupernodeSize(Sv);
            pi = pi >> 1;
        }
        return pi;
    }

    public void _processEdge(int[] srcnbd, int which) {
        Long2ObjectOpenHashMap<IntArrayList> srcGrp = new Long2ObjectOpenHashMap<>();
        long target;
        for (int vIDX : srcnbd) {
            target = minHash[which].getInt(vIDX);
            if (!srcGrp.containsKey(target)) srcGrp.put(target, new IntArrayList());
            srcGrp.get(target).add(vIDX);
        }
        int nbd, sz, _target;
        long mh;

        for (int i = 0; i < sampleNumber; i++) {
            nbd = srcnbd[i];
            if (random.nextInt(1, getSubnodeDegree(nbd)+1)<=1){
                mh = minHash[which].getInt(nbd);
                sz = srcGrp.get(mh).size();
                _target = srcGrp.get(mh).getInt(random.nextInt(0, sz));
                if (random.nextDouble() > escapeProb) {
                    tryMoveNode(nbd, subnodeIDX2supernode.get(_target));
                } else {
                    if(getSupernodeSize(subnodeIDX2supernode.get(nbd))>1){
                        int newSuperNode = newSupernode();
                        boolean success = tryMoveNode(nbd, newSuperNode);
                        if(!success) {
                            removeSupernode(newSuperNode);
                        }
                    }
                }
            }
        }
    }

    public void removeNodeFromSupernode(int vIDX, int V){
        supernodeSubnodeList.get(V).remove(vIDX);

        int U;
        for(int u : subnodeAdjList.get(vIDX)){
            U = subnodeIDX2supernode.get(u);
            edgeCntList.get(V).addTo(U,-1); edgeCntList.get(U).addTo(V,-1);
        }

        if(getSupernodeSize(V)==0){
            removeSupernode(V);
        }
    }
    public void addNode(int id){
        int subNewIdx = newSubnode();
        int superNewIdx = newSupernode();
        subnodeID2IDX.put(id, subNewIdx);
        IDX2subnodeID.put(subNewIdx, id);
        subnodeIDX2supernode.put(subNewIdx, superNewIdx);
        addNodeToSupernode(subNewIdx, superNewIdx);
    }
    public void addNodeToSupernode(int vIDX, int V){
        if(!supernodeSubnodeList.get(V).contains(vIDX)) {
            supernodeSubnodeList.get(V).add(vIDX);
            for(int u : subnodeAdjList.get(vIDX)){
                int U = subnodeIDX2supernode.get(u);
                edgeCntList.get(V).addTo(U,1); edgeCntList.get(U).addTo(V,1);
            }

        }
        for(int NBD : getSupernodeNeighborList(V)){
            int tmpMax = getSubedgeCount(V, NBD);
            if(maxWeight<tmpMax){
                maxWeight = tmpMax;
            }
        }
        subnodeIDX2supernode.put(vIDX,V);
    }

    public double log2(double x){
        if(x==0) return 0;
        return Math.log(x)/log2;
    }

    public double entropy(double p) {
        double entropy;
        if ((p == 0.0) || (p == 1.0)) {
            entropy = 0;
        } else {
            entropy = -(p * log2(p) + (1 - p) * log2(1 - p));
        }
        return entropy;
    }

    public void updateHash(int srcIDX, int dstIDX) {
        int srcHash, dstHash;
        for (int i = 0; i< n_hash; i++){
            srcHash = hf[i].getInt(srcIDX);
            dstHash = hf[i].getInt(dstIDX);
            if (minHash[i].getInt(srcIDX) > dstHash) minHash[i].set(srcIDX, dstHash);
            if (minHash[i].getInt(dstIDX) > srcHash) minHash[i].set(dstIDX, srcHash);
        }
    }

    public void updateSubedge(int srcIDX, int dstIDX) {
        int SRC = subnodeIDX2supernode.get(srcIDX); int DST = subnodeIDX2supernode.get(dstIDX);
        if(!subnodeAdjList.get(srcIDX).contains(dstIDX)){
            subnodeAdjList.get(srcIDX).add(dstIDX); subnodeAdjList.get(dstIDX).add(srcIDX); numSubedge++;
            edgeCntList.get(DST).addTo(SRC,1);
            edgeCntList.get(SRC).addTo(DST,1);
        }
    }
    public boolean updateSuperedge(int SRC, int DST, boolean add) {
        if (SRC == -1 || DST == -1) {
            return false;
        }
        if (add) {
            if(supernodeAdjList.get(SRC).contains(DST))
                return false;
            supernodeAdjList.get(SRC).add(DST);
            supernodeAdjList.get(DST).add(SRC);
            numSuperedge++;
            return true;
        } else {
            if(!supernodeAdjList.get(SRC).contains(DST))
                return false;
            supernodeAdjList.get(SRC).remove(DST);
            supernodeAdjList.get(DST).remove(SRC);
            numSuperedge--;
            return true;
        }
    }

    public double getDataCost(int Su, int Sv){
        double dataCost = 0;
        for(double v: datacostList.get(Su).values()) dataCost += Math.abs(v);
        for(double v: datacostList.get(Sv).values()) dataCost += Math.abs(v);
        if(datacostList.get(Su).containsKey(Sv)) dataCost -= Math.abs(datacostList.get(Su).get(Sv));
        return dataCost;
    }

    public IntArrayList mergeNbdList(IntArrayList A, IntArrayList B){
        A.addAll(B);
        return new IntArrayList(new IntArraySet(A));
    }

    public boolean sparseORdense(double sparse, double superEdgeCost, double dense){
        return sparse < (dense + superEdgeCost);
    }

    public boolean tryMoveNode(int u, int TO){
        int FROM = subnodeIDX2supernode.get(u);
        if(FROM == TO) {
            return false;
        }

        Int2IntOpenHashMap edgeDelta = new Int2IntOpenHashMap();
        for(int nbd: subnodeAdjList.get(u)){
            edgeDelta.addTo(subnodeIDX2supernode.get(nbd),1);
        }

        double oldModelCost = 0;

        Int2DoubleOpenHashMap deltaFromDataCost = new Int2DoubleOpenHashMap();
        Int2DoubleOpenHashMap deltaToDataCost = new Int2DoubleOpenHashMap();
        IntArrayList datacostNbdFROM = new IntArrayList(datacostList.get(FROM).keySet());
        IntArrayList datacostNbdTO = new IntArrayList(datacostList.get(TO).keySet());

        double globalSupernodeCnt = getNumSupernode();
        double superEdgeCostNoWeight = 2 * log2(globalSupernodeCnt);
        for(int NBD : getSupernodeNeighborList(FROM)) {
            oldModelCost += superEdgeCostNoWeight + log2(getPi(FROM,NBD));
        }
        for(int NBD : getSupernodeNeighborList(TO)) {
            if(NBD != FROM) {
                oldModelCost += superEdgeCostNoWeight + log2(getPi(TO, NBD));
            }
        }
        double oldDataCost = getDataCost(FROM, TO);
        int sizeofFROM = getSupernodeSize(FROM); int sizeofTO = getSupernodeSize(TO);
        double newSupernodeCnt = globalSupernodeCnt;

        double edgecnt, pi;
        double newDataCost = 0, newModelCost = 0;
        double weight, sparse, dense;


        if(sizeofFROM == 1) newSupernodeCnt--;
        if(sizeofTO == 0) newSupernodeCnt++;

        superEdgeCostNoWeight = 2*log2(newSupernodeCnt);

        if(sizeofFROM != 1) {

            for (int NBD : datacostNbdFROM) {

                if (NBD == TO)
                    continue;

                edgecnt = getSubedgeCount(NBD, FROM) - edgeDelta.get(NBD);
                pi = (FROM == NBD ? (double) ((sizeofFROM - 1) * (sizeofFROM - 2)) : getPi(FROM, NBD) - getSupernodeSize(NBD));

                if (pi == 0) {
                    deltaFromDataCost.put(NBD, -MIN_VALUE);
                    continue;
                }

                weight = edgecnt / pi;
                sparse = edgecnt * sparseEdgeCost;
                dense = (pi) * entropy(weight);
                double superEdgeCost = superEdgeCostNoWeight + log2(maxWeight);

                if (sparseORdense(sparse, superEdgeCost, dense)) {
                    sparse = (sparse == 0 ? MIN_VALUE : sparse);
                    deltaFromDataCost.put(NBD, -sparse);
                    newDataCost += sparse;

                } else {
                    dense = (dense == 0 ? MIN_VALUE : dense);
                    deltaFromDataCost.put(NBD, dense);
                    newDataCost += dense;
                    newModelCost += superEdgeCost;
                }
            }
        }

        datacostNbdTO = mergeNbdList(new IntArrayList(edgeDelta.keySet()), datacostNbdTO);

        for(int NBD : datacostNbdTO) {
            if (NBD == FROM) {
                edgecnt = getSubedgeCount(FROM, TO) + edgeDelta.get(FROM) - edgeDelta.get(TO);
                pi = (getSupernodeSize(FROM) - 1) * (getSupernodeSize(TO) + 1);
            } else {
                edgecnt = getSubedgeCount(NBD, TO) + edgeDelta.get(NBD);
                pi = (TO == NBD ? (double) (sizeofTO * (sizeofTO + 1)) : getPi(NBD, TO) + getSupernodeSize(NBD));
            }

            if (pi == 0) {
                deltaToDataCost.put(NBD, -MIN_VALUE);
                continue;
            }

            weight = edgecnt/pi;
            sparse = edgecnt*sparseEdgeCost;
            dense = (pi)*entropy(weight);
            double superEdgeCost = superEdgeCostNoWeight + log2(pi);

            if(sparseORdense(sparse, superEdgeCost, dense)){
                sparse = (sparse == 0 ? MIN_VALUE : sparse);
                deltaToDataCost.put(NBD, -sparse);
                newDataCost += sparse;
            }else{
                dense = (dense == 0 ? MIN_VALUE : dense);
                deltaToDataCost.put(NBD, dense);
                newDataCost += dense;
                newModelCost += superEdgeCost;
            }
        }

        double delta;
        delta = (oldDataCost + oldModelCost) - (newDataCost + newModelCost) ;

        if(delta > 0){

            for(Int2DoubleMap.Entry NBD: deltaFromDataCost.int2DoubleEntrySet()){
                int IDX = NBD.getIntKey();
                double change = NBD.getDoubleValue();
                if(change > 0){
                    datacostList.get(FROM).put(IDX, change); datacostList.get(IDX).put(FROM, change);
                    if(!supernodeAdjList.get(FROM).contains(IDX)) updateSuperedge(IDX, FROM, true);
                }else if(change < 0){
                    datacostList.get(FROM).put(IDX, change); datacostList.get(IDX).put(FROM, change);
                    if(supernodeAdjList.get(FROM).contains(IDX)) updateSuperedge(IDX, FROM, false);
                    if(change == - MIN_VALUE) {
                        datacostList.get(FROM).remove(IDX);
                        datacostList.get(IDX).remove(FROM);
                    }
                }
            }
            for(Int2DoubleMap.Entry NBD: deltaToDataCost.int2DoubleEntrySet()){
                int IDX = NBD.getIntKey();
                double change = NBD.getDoubleValue();
                if(change>0){
                    datacostList.get(TO).put(IDX, change); datacostList.get(IDX).put(TO, change);
                    if(!supernodeAdjList.get(TO).contains(IDX)) updateSuperedge(IDX, TO, true);

                }else if(change < 0){
                    datacostList.get(TO).put(IDX, change); datacostList.get(IDX).put(TO, change);
                    if(supernodeAdjList.get(TO).contains(IDX)) updateSuperedge(IDX, TO, false);
                    if(change == - MIN_VALUE) {
                        datacostList.get(TO).remove(IDX);
                        datacostList.get(IDX).remove(TO);
                    }
                }
            }

            removeNodeFromSupernode(u, FROM);
            addNodeToSupernode(u,TO);

            return true;

        }

        else {
            return false;
        }
    }

}
