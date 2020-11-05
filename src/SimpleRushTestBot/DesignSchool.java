package SimpleRushTestBot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import java.util.ArrayList;

public class DesignSchool extends Building {


    int numLandscapers = 0;
    boolean broadcastedCreation = false;
    boolean rampUpProduction = false;

    public DesignSchool(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {


        System.out.println(numLandscapers);

        if(!broadcastedCreation){
            broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 1);
        }

        if(!teamMessagesSearched){
            decipherAllBlockChainMessages();
        }

        decipherCurrentBlockChainMessage();

        if(rampUpProduction){
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.LANDSCAPER, dir)) {
                    numLandscapers++;
                    System.out.println("Created a new landscaper!");

                }
            }
        } else {
            if(numLandscapers < 5) {
                if (rc.isReady()) {
                    for (Direction dir : Util.directions) {
                        if (tryBuild(RobotType.LANDSCAPER, dir)) {
                            numLandscapers++;
                            System.out.println("Created a new landscaper!");
                        }
                    }
                }
            }
        }
    }

    public void decipherAllBlockChainMessages(){
        for(int [] message : teamMessages){
            // Robot specific messages
            if(message[1] == 8 && message[4] == rc.getID()){
                System.out.print("Recieved personal message");
                rampUpProduction = true;
            }
        }
        teamMessagesSearched = true;
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException{
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if(message[1] == 8 && message[4] == rc.getID()){
                System.out.print("Recieved personal message");
                rampUpProduction = true;
            }
        }
    }
}
