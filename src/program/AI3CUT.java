package program;

import static program.Board.CHEATVALUE;
import static program.Board.MAXVALUE;

public class AI3CUT implements IPlayer {

    private boolean isP1 = false;
    public final static int VALMAX=96;
    public int maxDepth;

    private Pool pool;


    public AI3CUT(boolean isP1,int maxDepth){
        this.isP1 = isP1;
        this.maxDepth = maxDepth;
        pool = new Pool(maxDepth+5,maxDepth+5);
    }


    @Override
    public int play(Board board){
        int maxResult= 0;
        int index = -1;
        int alpha = -MAXVALUE;
        int beta = MAXVALUE;

        for(int i = 0; i<board.currentSize/2;i++){
            Board playNext = pool.pollBoard(board);

            if(!playNext.correctMove(convertIndex(isP1,i))) {
                pool.pushBoard(playNext);
                continue;
            }
            //l'ia fait tout les coup possible 1 par 1
            playNext.playMove(convertIndex(isP1,i));
            //l'ia fait jouer les autre coup en partant de l'inverse
            int result = minMaxValue(playNext,!this.isP1,1,alpha,beta);

            //On retient le coup le plus avantageux
            if(index==-1){
                maxResult = result;
                index = i;
            }
            if(isP1 && result >=maxResult){
                maxResult = result;
                index = i;
                alpha =result;

            }
            else if(!isP1 && result <=maxResult ){
                maxResult = result;
                index = i;
                beta =result;

            }
            pool.pushBoard(playNext);
        }
        return convertIndex(isP1, index);//Case 1 d'index 0
    }

    public int minMaxValue(Board board,boolean rushMax, int depth,int alpha, int beta){
        int sizeArray = board.currentSize/2;

        if(board.endPosition()){
            if (board.scoreP1 == board.scoreP2) return 0;
            else if (board.scoreP1 > board.scoreP2) return VALMAX;
            else return -VALMAX;
        }
        if(depth==maxDepth){
            return evaluation(board);
        }

        int currentScore;
        for(int i = 0; i<sizeArray;i++){
            if(board.correctMove(convertIndex(rushMax,i))){
                Board nextPos = pool.pollBoard(board);
                nextPos.playMove(convertIndex(rushMax,i));
                currentScore = minMaxValue(nextPos,!rushMax,depth+1,alpha,beta);
                pool.pushBoard(nextPos);

                if(rushMax){
                    if(alpha<=currentScore){
                        alpha =currentScore;
                        if (alpha>=beta){
                            return alpha;
                        }
                    }
                }
                else {
                    if (beta >= currentScore) {
                        beta = currentScore;
                        if (beta <= alpha) {
                            return beta;
                        }
                    }
                }
            }
        }

        if(rushMax) return alpha;
        return beta;

    }

    private int convertIndex(boolean isP1, int i){
        if(isP1){
            return i*2;
        }
        else{
            return i*2+1;
        }
    }


    private int evaluation(Board board) {

        if(board.scoreP1>48) return VALMAX;
        if(board.scoreP2>48) return -VALMAX;
        return board.scoreP1-board.scoreP2;

    }
}