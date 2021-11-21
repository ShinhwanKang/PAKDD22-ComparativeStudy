package alg;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.io.*;
import java.util.Random;

public class KGCommon {
    public double samplingCoefficient = 1;
    String objectiveType;
    public double maxMergeRE;

    public int numSubnodes=0;
    public int numSubedges=0;
    public Int2IntOpenHashMap ID2IDX = new Int2IntOpenHashMap();

    public int numSupernodes, numSuperedges;
    public int[] sizeSupernode;
    public Int2IntOpenHashMap degOfSuper;
    public IntArrayList snList;
    public Int2IntOpenHashMap[] superAdj;
    public Int2IntOpenHashMap[] super2SuperEdgeCnt;
    public double[] superRE;

    public IntArrayList[] insideSupernode;
    public Int2IntOpenHashMap[] subAdj;
    public Int2IntOpenHashMap subIdx2SuperIdx;

    public LongArrayList candidatePairs;


    public KGCommon(){ }

    public int getEdgeCnt(int A, int B){
        int edgeCnt = super2SuperEdgeCnt[A].getOrDefault(B,0);
        return (edgeCnt > 0 ? edgeCnt : -edgeCnt);
    }

    public double log2(double x){
        return Math.log(x)/Math.log10(2);
    }

    public boolean checkFormat(String number) {
        boolean isInt = false;
        try {
            Integer.parseInt(number);
            isInt = true;
        } catch (NumberFormatException e) {
        }
        return isInt;
    }

    public void addNode(int id){
        int idx = ID2IDX.getOrDefault(id, -1);
        if(idx < 0) {
            ID2IDX.put(id, numSubnodes);
            idx = numSubnodes;
            degOfSuper.put(idx, 0);
            numSubnodes++;
        }
        degOfSuper.addTo(idx, 1);
    }


    public void readGraph(String dataPath){
        String line;
        String[] parts;
        degOfSuper = new Int2IntOpenHashMap();
        try {
            int srcID, dstID;
            BufferedReader br = new BufferedReader(new FileReader(dataPath));
            while ((line = br.readLine()) != null) {
                parts = line.split("\\s");
                if ((parts.length >= 2) && checkFormat(parts[0]) && checkFormat(parts[1])) {
                    try {
                        srcID = Integer.parseInt(parts[0]); addNode(srcID);
                        dstID = Integer.parseInt(parts[1]); addNode(dstID);
                    } catch (NumberFormatException e) {
                        System.out.println(e);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        sizeSupernode = new int[numSubnodes];
        snList = new IntArrayList(numSubnodes);
        super2SuperEdgeCnt = new Int2IntOpenHashMap[numSubnodes];
        superRE = new double[numSubnodes];

        insideSupernode = new IntArrayList[numSubnodes];
        subAdj = new Int2IntOpenHashMap[numSubnodes];
        superAdj = new Int2IntOpenHashMap[numSubnodes];
        subIdx2SuperIdx = new Int2IntOpenHashMap(numSubnodes);

        int deg;
        for(int i=0;i<numSubnodes;i++) {
            deg = degOfSuper.get(i);
            subIdx2SuperIdx.put(i, i);
            subAdj[i] = new Int2IntOpenHashMap(deg);
            superAdj[i] = new Int2IntOpenHashMap(deg);
            sizeSupernode[i] = 1;
            snList.add(i);
            super2SuperEdgeCnt[i] = new Int2IntOpenHashMap(deg);
            superRE[i]=0;
            insideSupernode[i] = new IntArrayList(new int[]{i});
        }

        try {
            int srcIDX, dstIDX;
            BufferedReader br = new BufferedReader(new FileReader(dataPath));
            while ((line = br.readLine()) != null) {
                parts = line.split("\\s");
                if ((parts.length >= 2) && checkFormat(parts[0]) && checkFormat(parts[1])) {
                    try {
                        srcIDX = ID2IDX.get(Integer.parseInt(parts[0]));
                        dstIDX = ID2IDX.get(Integer.parseInt(parts[1]));
                        if(srcIDX == dstIDX) continue;
                        if(!super2SuperEdgeCnt[srcIDX].containsKey(dstIDX)){
                            numSubedges++;
                            super2SuperEdgeCnt[srcIDX].put(dstIDX, 1); super2SuperEdgeCnt[dstIDX].put(srcIDX, 1);
                            subAdj[srcIDX].put(dstIDX, 1); subAdj[dstIDX].put(srcIDX, 1);
                            superAdj[srcIDX].put(dstIDX, 1); superAdj[dstIDX].put(srcIDX, 1);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        numSupernodes = numSubnodes;
        numSuperedges = numSubedges;
        System.out.println("|V|\t" + numSubnodes);
        System.out.println("|E|\t" + numSubedges);

    }

    public void editSuperedge(int A, int B, boolean add){
        if(add){
            if(superAdj[A].getOrDefault(B,0) == 0) numSuperedges++;
            superAdj[A].put(B,1);
            superAdj[B].put(A,1);
        }
        else{
            if(superAdj[A].getOrDefault(B,0) == 1) numSuperedges--;
            superAdj[A].remove(B);
            superAdj[B].remove(A);
        }
    }


    public void setEdgeCnt(int A, int B, int cnt){
        super2SuperEdgeCnt[A].put(B, cnt);
        super2SuperEdgeCnt[B].put(A, cnt);
    }

    public double getSummarySize(){
        return 0;
    }

    public long getBestPair(){
        int A,B;
        double maxReduction = -Double.MAX_VALUE;
        maxMergeRE=-Double.MAX_VALUE;
        long maxReductionPair=-1;
        for(long SRCDST : candidatePairs){
            A = (int)(SRCDST >> 32); B = (int)(SRCDST & 0x7FFFFFFFL);
            if(A==B) continue;

            double beforeRE = superRE[A]+superRE[B]-(super2SuperEdgeCnt[A].getOrDefault(B,0)!=0 ? getRE(A,B) : 0);
            double afterRE = getMergeCost(A, B);
            if(maxReduction < beforeRE - afterRE) {
                maxReductionPair = SRCDST;
                maxReduction =  beforeRE - afterRE;
                maxMergeRE = afterRE;
            }
        }

        return maxReductionPair;
    }

    public boolean merge(){
        long bestPair = getBestPair();
        int A = (int)(bestPair >> 32); int B = (int)(bestPair & 0x7FFFFFFFL);
        mergeInner(A,B);
        return true;
    }

    public void saveSummary(String fn){
        System.out.println("Not Implemented");
    }

    public void summarize(String dp, double targetRatio){
        double inputGraphSize = numSubedges*2*log2(numSubnodes);
        double targetSize = inputGraphSize*targetRatio;
        double summarySize = inputGraphSize;
        while(getSummarySize()>targetSize){
            samplePairs();
            if(!merge()) break;
            summarySize=getSummarySize();
        }
        System.out.println("Compression Ratio\t" + summarySize/inputGraphSize*100+"%");
        if(objectiveType.equals("1")){
            System.out.println("L1 Reconstruction Error\t" + L1Error());
        }else{
            System.out.println("L2 Reconstruction Error\t" + L2Error());
        }
        System.out.println("|S|\t" + numSupernodes);
        System.out.println("|P|\t" + numSuperedges);

        saveSummary(dp);

    }



    public int getSize(int A){
        return sizeSupernode[A];
    }

    public IntArrayList getMergeCandidate(int A, int B){
        IntArrayList candidate = new IntArrayList(super2SuperEdgeCnt[A].keySet());
        for(int canB :  super2SuperEdgeCnt[B].keySet()){
            if(!candidate.contains(canB)) candidate.add(canB);
        }

        if(candidate.contains(B)) {
            if(!candidate.contains(A)) candidate.add(A);
            candidate.rem(B);
        }

        return candidate;
    }

    public double getMergeCost(int A, int B){
        System.out.println("Not Implemented");
        return -1;
    }


    public double getRE(int A, int B){
        System.out.println("Not Implemented");
        return -1;
    }

    public void mergeInner(int A, int B){
        System.out.println("Not Implemented");

    }

    public void samplePairs(){
        int numSampling = (int)(samplingCoefficient * numSupernodes);
        IntArrayList snListTemp = snList.clone();

        Random rand;
        candidatePairs = new LongArrayList(numSampling);

        int sz = snListTemp.size();
        int idxA, idxB, A, B;
        while(candidatePairs.size()<numSampling){
            rand = new Random();
            idxA = rand.nextInt(sz);
            do{
                idxB = rand.nextInt(sz);
            }while(idxA==idxB);
            A = snListTemp.getInt(idxA);
            B = snListTemp.getInt(idxB);
            candidatePairs.add(((long)A << 32) + B);
        }
    }

    public double L1Error(){
        System.out.println("Not Implemented");
        return -Double.MAX_VALUE;
    }

    public double L2Error(){
        System.out.println("Not Implemented");
        return -Double.MAX_VALUE;
    }

}
