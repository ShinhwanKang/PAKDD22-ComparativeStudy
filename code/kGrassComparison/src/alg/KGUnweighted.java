package alg;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KGUnweighted extends KGCommon {

    public KGUnweighted(String type){
        objectiveType = type;
    }
    public double getSummarySize(){
        return numSubnodes*log2(numSupernodes) + numSuperedges*2*log2(numSupernodes);
    }


    public int getEdgeCnt(int A, int B){
        return super2SuperEdgeCnt[A].getOrDefault(B,0);
    }

    public double getMergeCost(int A, int B){
        IntArrayList candidate = getMergeCandidate(A,B);

        double edgeCnt, pi;
        double mergeCost=0;
        double size = getSize(A) + getSize(B);

        edgeCnt = getEdgeCnt(A,A) + 2*getEdgeCnt(A,B) + getEdgeCnt(B,B);
        pi = size * (size-1);

        double denseCost, sparseCost;
        denseCost = (pi-edgeCnt);
        sparseCost = edgeCnt;

        mergeCost += (Math.min(denseCost, sparseCost));

        candidate.rem(A);
        for(int can : candidate){
            edgeCnt = getEdgeCnt(A,can) + getEdgeCnt(B, can);
            pi = size * getSize(can);

            denseCost = (pi-edgeCnt);
            sparseCost = edgeCnt;
            mergeCost += (Math.min(denseCost, sparseCost));

        }
        return mergeCost;
    }


    public double getRE(int A, int B){
        double edgeCnt = getEdgeCnt(A,B);
        double pi = getSize(A);
        pi *= (A==B?(pi-1):getSize(B));

        double denseCost, sparseCost;
        denseCost = (pi-edgeCnt);
        sparseCost = edgeCnt;

        return (superAdj[A].getOrDefault(B,0)>0 ? denseCost : sparseCost);
    }

    public void mergeInner(int A, int B){
        IntArrayList candidate = getMergeCandidate(A,B);

        for(int can : candidate){
            superRE[can] -= getRE(A,can); superRE[can] -= getRE(B,can);
        }

        for(int b : insideSupernode[B]) subIdx2SuperIdx.put(b, A);
        insideSupernode[A].addAll(insideSupernode[B]);
        sizeSupernode[A] += sizeSupernode[B];

        int pi, edgeCnt;
        edgeCnt = getEdgeCnt(A,A) + 2*getEdgeCnt(A,B) + getEdgeCnt(B,B);
        pi = getSize(A); pi *= (pi-1);

        double denseCost, sparseCost;
        denseCost = (pi-edgeCnt);
        sparseCost = edgeCnt;

        editSuperedge(A,A, denseCost<sparseCost);
        setEdgeCnt(A, A, edgeCnt);
        candidate.rem(A);

        for(int can : candidate){
            edgeCnt = 0;
            edgeCnt += getEdgeCnt(A,can); edgeCnt += getEdgeCnt(B, can);
            pi = getSize(A)*getSize(can);

            denseCost = (pi-edgeCnt);
            sparseCost = edgeCnt;

            editSuperedge(A,can, denseCost<sparseCost);
            setEdgeCnt(A, can, edgeCnt);
            super2SuperEdgeCnt[can].remove(B);
        }

        for(int NBD : new IntArrayList(superAdj[B].keySet())) editSuperedge(B, NBD, false);
        superAdj[B] = null;

        super2SuperEdgeCnt[A].remove(B);
        super2SuperEdgeCnt[B] = null;
        sizeSupernode[B]= 0;
        insideSupernode[B]=null;

        snList.rem(B);
        numSupernodes--;

        for(int can : candidate){
            superRE[can] += getRE(A, can);
        }
        superRE[A] = maxMergeRE; superRE[B] = -Double.MAX_VALUE;
    }


    public double L1Error(){
        double edgeCnt, pi;
        double error = 0;

        for(int n: snList){
            for(Int2IntOpenHashMap.Entry X:super2SuperEdgeCnt[n].int2IntEntrySet()){
                int m = X.getIntKey();
                edgeCnt = X.getIntValue();
                if(superAdj[n].getOrDefault(m,0) == 0) {
                    error += edgeCnt;
                    continue;
                }

                pi = getSize(n);
                if(n == m){
                    pi *= (pi-1);
                }
                else{
                    pi *= getSize(m);
                }
                error += (pi-edgeCnt);

            }

        }

        error/=numSubnodes;
        error/=(numSubnodes-1);

        return error;
    }

    public double L2Error(){
        double edgeCnt, pi;
        double error = 0;

        for(int n: snList){
            for(Int2IntOpenHashMap.Entry X:super2SuperEdgeCnt[n].int2IntEntrySet()){
                int m = X.getIntKey();
                edgeCnt = X.getIntValue();
                if(superAdj[n].getOrDefault(m,0) == 0) {
                    error += (edgeCnt);
                    continue;
                }

                pi = getSize(n);
                if(n == m){
                    pi *= (pi-1);
                }
                else{
                    pi *= getSize(m);
                }

                error += (pi-edgeCnt);

            }

        }

        error = Math.sqrt(error);

        error/=numSubnodes;
        error/=(numSubnodes-1);

        return error;
    }

    public void saveSummary(String fn){
        Int2IntOpenHashMap IDX2ID = new Int2IntOpenHashMap();
        for(Int2IntMap.Entry x : ID2IDX.int2IntEntrySet()){
            IDX2ID.put(x.getIntValue(), x.getIntKey());
        }

        String[] fileNames = fn.split("/");
        String dn = fileNames[fileNames.length-1];
        dn = dn.split("\\.")[0];

        String saveFileName = "summary_"+dn+".txt";

        File f = new File("output/"+ saveFileName);
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("<Subnode of each supernode>");
            fw.write(System.getProperty( "line.separator" ));
            for (int sup_v : snList) {
                fw.write(String.format("%d", sup_v));
                for (int sub_v : insideSupernode[sup_v]) {
                    fw.write("\t"+String.format("%d", IDX2ID.get(sub_v)));
                }
                fw.write(System.getProperty( "line.separator" ));
            }
            fw.write("<Superedge info>");
            fw.write(System.getProperty( "line.separator" ));
            for(int sup_v: snList){
                for(int neighbor_v : superAdj[sup_v].keySet()){
                    if(sup_v>=neighbor_v){
                        fw.write(String.format("%d", sup_v) +"\t"+ String.format("%d", neighbor_v));
                        fw.write(System.getProperty( "line.separator" ));
                    }
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
