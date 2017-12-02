
/**
 * 在这里给出对类 MoveScore 的描述。
 * 
 * @作者（你的名字）
 * @版本（一个版本号或者一个日期）
 */
public class MoveScore implements Comparable<MoveScore>{
    private Coordinate move ;
    private int score ;
        
    public MoveScore(Coordinate move, int score){
            this.move = move;
            this.score = score;
    }
        
    public int getScore(){ 
            return score ;
    }
        
    public Coordinate getMove(){ 
            return move ;
    }

    @Override
    /**Comparator*/
    public int compareTo(MoveScore o) {
        if(o.score > this.score)
            return 1;
        else if (o.score < this.score)
            return -1;
        else
            return 0;
    }
}
