package approach;

import it.unimi.dsi.fastutil.ints.*;

import static java.lang.Double.MIN_VALUE;

public class MoSSoLossyUnweighted extends MoSSoLossyWeighted {

    public void tryUpdateSuperedge(int src, int dst, boolean add){
        final int SRC = subnodeIDX2supernode.get(src), DST = subnodeIDX2supernode.get(dst);
        double pi = getPi(SRC, DST);
        double edgecnt = getSubedgeCount(SRC, DST);
        double sparse = edgecnt * sparseEdgeCost;
        double dense = (pi - edgecnt) * sparseEdgeCost;
        double superEdgeCost = 2 * log2(getNumSupernode());

        if(!sparseORdense(sparse, superEdgeCost, dense)){
            if (!supernodeAdjList.get(SRC).contains(DST)) updateSuperedge(SRC, DST, true);
            datacostList.get(SRC).put(DST, dense);
            datacostList.get(DST).put(SRC, dense);
        }
        else{
            datacostList.get(SRC).put(DST, -sparse);
            datacostList.get(DST).put(SRC, -sparse);
        }
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
        double sparse, dense;


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

                sparse = edgecnt * sparseEdgeCost;
                dense = (pi - edgecnt) * sparseEdgeCost;
                double superEdgeCost = superEdgeCostNoWeight;

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

            sparse = edgecnt*sparseEdgeCost;
            dense = (pi - edgecnt) * sparseEdgeCost;
            double superEdgeCost = superEdgeCostNoWeight;

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
