import approach.MoSSoLossyWeighted;
import approach.MoSSoLossyUnweighted;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.*;

public class RUN {

    public static void main(String[] args) throws IOException {
        final String inputPath = args[0];
        System.out.println("input_path: " + inputPath);
        final String modelType = args[1];// we, uwe

        System.out.println("Model Type: " + modelType);

        BufferedReader br = new BufferedReader(new FileReader(inputPath));
        ObjectArrayList<IntTuple> edges = new ObjectArrayList<IntTuple>();

        MoSSoLossyWeighted module;

        if(modelType.equals("we")){
            module = new MoSSoLossyWeighted();
        }else if(modelType.equals("uwe")){
            module = new MoSSoLossyUnweighted();
        }
        else{
            System.out.println("Model Type Error");
            return;
        }

//        int line_cnt = 0;
        IntTuple newEdge;
        while (true) {
            final String line = br.readLine();
            if (line == null) break;
            final int[] edge = parseEdge(line, "\t");
            if (edge == null) break;

            newEdge = new IntTuple(edge[0], edge[1], edge[2]);
            if (edge[2] > 0) {
                edges.add(newEdge);
                module.commonProcessEdge(edge[0], edge[1]);
//                line_cnt++;
            } else {
                System.out.println("ERR: NO DELETION");
            }


        }
        br.close();

        System.out.println("|V|\t " + module.getNumSubnode());
        System.out.println("|E|\t " + module.numSubedge);

        System.out.println("|S|\t " + module.getNumSupernode());
        System.out.println("|P|\t " + module.numSuperedge);

        double originalSize = 2 * module.numSubedge * module.log2(module.getNumSubnode());

        double summarySize;

        summarySize = summarySize(module, modelType);
        System.out.println("Compression Ratio\t " + String.format("%.6f", 100 * summarySize / originalSize) + "%");
        System.out.println("L1 Reconstruction Error\t " + L1Error(module, edges, modelType));
        System.out.println("L2 Reconstruction Error\t " + L2rror(module, edges, modelType));
        String outputname = inputPath.split("\\.")[inputPath.split("\\.").length-2];
        summarySave(module, outputname, modelType);

    }

    private static int[] parseEdge(String line, String delim){
        String[] tokens = line.split(delim);
        try {
            int src = Integer.valueOf(tokens[0]);
            int dst = Integer.valueOf(tokens[1]);
            int add = Integer.valueOf(tokens[2]);
            return new int[]{src, dst, add};
        }catch(Exception e){
            return null;
        }
    }


    public static double L1Error(MoSSoLossyWeighted module, ObjectArrayList<IntTuple> edges, String modelType){
        Long2IntOpenHashMap check = new Long2IntOpenHashMap();
        double err = 0;
        long UV, VU;
        int _u, _v, add, edgeCount,sz;
        double w;
        for(IntTuple e: edges){
            _u = module.subnodeID2IDX.get(e.getSrc());
            _v = module.subnodeID2IDX.get(e.getDst());
            add = e.getAdd();
            if(_u == _v || add == -1) continue;
            if(e.getSrc() < 0 || e.getDst() <0 || module.deleteSubnodeList.contains(_u) || module.deleteSubnodeList.contains(_v)) continue;
            if(!module.subnodeAdjList.get(_u).contains(_v)) continue;
            UV = (((long)_u)<<32) + _v; VU = (((long)_v)<<32) + _u;
            if(check.containsKey(UV) || check.containsKey(VU)) continue;
            check.put(UV,1); check.put(VU,1);
            int U = module.subnodeIDX2supernode.get(_u); int V = module.subnodeIDX2supernode.get(_v);
            edgeCount = (module.getSupernodeNeighborList(U).contains(V) ? module.edgeCntList.get(U).get(V) : 0);
            sz = module.getPi(U,V) *(U==V ? 2 :1);

            if(modelType.equals("we")){
                w = (double)edgeCount/(double)sz;
            }else{
                w = module.getSupernodeNeighborList(U).contains(V) ? 1.0 : 0.0;
            }
            err += (1-2*w);
        }

        err *= 2;
        int _edgeCount;
        for(int _U: module.getSupernodeList()){
            for(int _V: module.getSupernodeNeighborList(_U)){
                if(modelType.equals("we")){
                    _edgeCount = module.edgeCntList.get(_U).get(_V);
                }else{
                    _edgeCount = module.getPi(_U,_V) *(_U==_V ? 2 :1);
                }
                err += _edgeCount;
            }
        }
        err /= module.getNumSubnode();
        err /= (module.getNumSubnode() - 1);
        return err;
    }

    public static double L2rror(MoSSoLossyWeighted module, ObjectArrayList<IntTuple> edges, String modelType){
        Long2IntOpenHashMap check = new Long2IntOpenHashMap();
        double err = 0;
        long UV, VU;
        int _u, _v, add, V, edgeCount, U;
        double w,sz;
        for(IntTuple e: edges){
            _u = module.subnodeID2IDX.get(e.getSrc());
            _v = module.subnodeID2IDX.get(e.getDst());
            add = e.getAdd();
            if(_u == _v || add == -1) continue;
            if(e.getSrc() < 0 || e.getDst() <0 || module.deleteSubnodeList.contains(_u) || module.deleteSubnodeList.contains(_v)) continue;
            if(!module.subnodeAdjList.get(_u).contains(_v)) continue;

            UV = (((long)_u)<<32) + _v; VU = (((long)_v)<<32) + _u;
            if(check.containsKey(UV) || check.containsKey(VU)) continue;
            check.put(UV,1); check.put(VU,1);
            V = module.subnodeIDX2supernode.get(_v); U = module.subnodeIDX2supernode.get(_u);
            edgeCount = (module.getSupernodeNeighborList(U).contains(V) ? module.edgeCntList.get(U).get(V) : 0);
            sz = module.getSupernodeSize(U);
            sz *= ((U == V) ? (sz - 1) : module.getSupernodeSize(V));

            if(modelType.equals("we")){
                w = edgeCount / sz;
            }
            else{
                w = module.getSupernodeNeighborList(U).contains(V) ? 1.0 : edgeCount / sz;
            }
            err += ((1-w)*(1-w)-w*w);
        }
        err *= 2;
        int _edgeCount;
        for(int _V: module.getSupernodeList()){
            if(module.deleteSupernodeList.contains(_V)) continue;
            for(int _U: module.getSupernodeNeighborList(_V)){
                if(module.deleteSupernodeList.contains(_U)) continue;
                sz = module.getSupernodeSize(_V);
                sz *= ((_V == _U) ? (sz - 1) : module.getSupernodeSize(_U));
                _edgeCount = module.edgeCntList.get(_U).get(_V);

                if(modelType.equals("we")){
                    w = _edgeCount / sz;
                }else{
                    w = module.getSupernodeNeighborList(_U).contains(_V) ? 1.0 : _edgeCount / sz;
                }
                err += (w*w*sz);
            }
        }
        err = Math.sqrt(err);
        err /= module.getNumSubnode();
        err /= (module.getNumSubnode() - 1);
        return err;
    }


    public static double summarySize(MoSSoLossyWeighted module, String encodingMethod){

        double result = module.getNumSubnode() * module.log2(module.getNumSupernode());
        double superEdgeSizeNoWeight = 2 * module.log2(module.getNumSupernode());
        for (int U : module.getSupernodeList()) {
            for (int V : module.getSupernodeNeighborList(U)) {
                if (U >= V) {
                    if(encodingMethod.equals("we")){
                        result += (superEdgeSizeNoWeight + module.log2(module.getPi(U, V)));
                    }else{
                        result += superEdgeSizeNoWeight;
                    }

                }
            }
        }

        return result;
    }

    public static void summarySave(MoSSoLossyWeighted module, String filename, String modelType) {
        String[] fileNames = filename.split("/");
        String file = fileNames[fileNames.length -1];

        filename = "summary_" + file + ".txt";
        File f = new File("output/" + filename);
        IntOpenHashSet snList = module.getSupernodeList();
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("<Subnode of each supernode>");
            fw.write(System.getProperty( "line.separator" ));

            for (int V : snList) {
                fw.write(String.format("%d", V));
                for (int subv : module.supernodeSubnodeList.get(V)) {
                    fw.write("\t"+String.format("%d", module.IDX2subnodeID.get(subv)));
                }
                fw.write(System.getProperty( "line.separator" ));
            }
            fw.write("<Superedge info>");
            fw.write(System.getProperty( "line.separator" ));
            for(int V: snList){
                for(int neighbor_v : module.getSupernodeNeighborList(V)){
                    if(V>=neighbor_v){
                        if(modelType.equals("we")){
                            fw.write(String.format("%d", V) +"\t"+ String.format("%d", neighbor_v) +"\t"+ String.format("%d", module.getSubedgeCount(V, neighbor_v)));
                        }else{
                            fw.write(String.format("%d", V) +"\t"+ String.format("%d", neighbor_v));
                        }
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
