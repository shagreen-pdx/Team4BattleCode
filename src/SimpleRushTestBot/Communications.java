package SimpleRushTestBot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Transaction;

import java.util.ArrayList;

public class Communications {
    RobotController rc;

    static final int teamSecret = 888;
    public static boolean broadcastedCreation = false;
    public static boolean rush = false;

    // every message has a message type
    static final String[] messageType = {
            "HQ loc", // 0
            "design school loc", // 1
            "Soup loc", // 2
            "Refinery loc", // 3
            "Fulfilment Center loc", // 4
            "Search for hq", // 5
            "Enemy HQ loc", // 6
            "Build Design School", // 7
            "Rush HQ" // 8
    };


    public Communications(RobotController r){
        rc = r;
    }

    // Broadcast message to every team
    public boolean broadcastMessage(MapLocation loc, int messageIndex) throws GameActionException {
        int [] message = new int [7];
        message[5] = teamSecret;
        message[1] = messageIndex; //index of message type - 6 = landscaper location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            return true;
        }
        return false;
    }

    // Send message to specific robot
    public boolean broadcastMessage(int id, int messageIndex) throws GameActionException {
        int [] message = new int [7];
        message[5] = teamSecret;
        message[1] = messageIndex; //index of message type - 6 = landscaper location
        message[4] = id;

        if(rc.canSubmitTransaction(message, 5)){
            rc.submitTransaction(message, 5);
            return true;
        }
        return false;
    }


    // Used in robot.java. After robot is created, it searches through the blockchain for team messages
    public void getAllTeamMessages(ArrayList<int []> teamMessages) throws GameActionException {

        for(int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] myMessage = tx.getMessage();
                if (myMessage[5] == teamSecret) { //check that message is from our team and the type is hqloc
                    teamMessages.add(myMessage);
                }
            }
        }
    }

    // Get all messages in previous round
    public ArrayList<int []> getPrevRoundMessages() throws GameActionException{
        ArrayList<int []> currentRoundMessages = new ArrayList<int []>();

        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] myMessage = tx.getMessage();
            if (myMessage[5] == teamSecret) { //check that message is from our team and the type is hqloc
                currentRoundMessages.add(myMessage);
            }
        }
        return currentRoundMessages;
    }
}
