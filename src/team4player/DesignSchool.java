package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class DesignSchool extends Building{

    int numLandscapers = 0;
    boolean broadcastedCreation = false;
    boolean rampUpProduction = false;

    public DesignSchool(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {


        System.out.println(numLandscapers);
        if(!teamMessagesSearched){
            decipherAllBlockChainMessages();
        }

        decipherCurrentBlockChainMessage();
        if(rampUpProduction){
            rampUp();
        } else {
            elseProduction();
        }
    }

    public void rampUp() throws GameActionException {
        for (Direction dir : Util.directions) {
            if (tryBuild(RobotType.LANDSCAPER, dir)) {
                numLandscapers++;
                System.out.println("Created a new landscaper!");
                if(numLandscapers == 5){
                    rampUpProduction = false;
                    comms.broadcastMessage(14, 1);
                }
            }
        }
        if(rc.getDirtCarrying() == 14){
            comms.broadcastMessage(14, 1);
        }
    }

    public void elseProduction() throws GameActionException {
        if(canBuild && rc.getTeamSoup() > 210 && rc.isReady()) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.LANDSCAPER, dir)) {
                    numLandscapers++;
                    System.out.println("Created a new landscaper!");
                    canBuild = false;
                    comms.broadcastMessage(rc.getLocation(),15);
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
            decipherCurrentBlockChainMessageHelper(message);
        }
    }

    public int[] decipherCurrentBlockChainMessageHelper(int[] message) throws GameActionException {
        if(message[1] == 8 && message[4] == rc.getID()){
            System.out.print("Recieved personal message");
            rampUpProduction = true;
            canBuild = true;
        } else if (message[1] == 16 ){
            MapLocation loc = new MapLocation(message[2], message[3]);
            if(!loc.equals(rc.getLocation())){
                canBuild = true;
            }
        }
        return message;
    }

}
