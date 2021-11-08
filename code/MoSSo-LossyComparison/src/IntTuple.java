public class IntTuple {

    int src, dst, add;

    public IntTuple(){
        this.src = -1;
        this.dst = -1;
        this.add = -1;
    }
    public IntTuple(int _src, int _dst, int _add){
        this.src = _src;
        this.dst = _dst;
        this.add = _add;
    }

    public int getDst() {
        return dst;
    }

    public int getSrc() {
        return src;
    }

    public int getAdd(){
        return add;
    }
}
