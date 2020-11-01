package team4player;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

public class Communications {
    RobotController rc;

    static final int teamSecret = 77;
    public static boolean broadcastedCreation = false;
    public static boolean rush = false;

    // every message has a message type
    static final String[] messageType = {
            "HQ loc",
            "design school loc",
            "Soup loc",
            "Refinery loc",
            "Fufilment Center loc"
    };

    public Communications(RobotController r){
        rc = r;
    }

    public MapLocation GetMessageFromBlockchain(int messageIndex) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == messageIndex) {
                return new MapLocation(myMessage[2], myMessage[3]);
            }
        }

        return null;
    }

    public MapLocation getHqFromBlockchain() throws GameActionException {
        for(int i = 1; i < rc.getRoundNum(); i++) {
            for(Transaction tx : rc.getBlock(i)) {
                int [] myMessage = tx.getMessage();
                if(myMessage[0] == teamSecret && myMessage[1] == 0) { //check that message is from our team and the type is hqloc
                    System.out.println("got HQ location!");
                    return new MapLocation(myMessage[2], myMessage[3]); //hqloc = mew map location with x and y coords from message
                }
            }
        }
        return null;
    }

    public int getNewDesignSchoolCount() throws GameActionException {
        int count = 0;
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 1) { //check that message is from our team and the type is design school
                count += 1;
            }
        }
        return count;
    }

    public int getNewLandscaperCount() throws GameActionException {
        int count = 0;
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 6) { //check that message is from our team and the type is landscaper
                count += 1;
            }
        }
        return count;
    }

    public int getNewRefineryCount() throws GameActionException {
        int count = 0;
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 3) { //check that message is from our team and the type is refinery
                count += 1;
            }
        }
        return count;
    }

    public void updateRefineryLocation(ArrayList<MapLocation> refineryLocations) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1))
        {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 3) { //check that message is from our team and the type is refinery
                refineryLocations.add(new MapLocation(myMessage[2], myMessage[3]));
            }
        }
    }

    public int getNewFulfillmentCenterCount() throws GameActionException {
        int count = 0;
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 4) { //check that message is from our team and the type is fulfillment center
                count += 1;
            }
        }
        return count;
    }

    public int getNewDeliveryDroneCount() throws GameActionException {
        int count = 0;
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 5) { //check that message is from our team and the type is delivery drone
                count += 1;
            }
        }
        return count;
    }

    public void updateSoupLocation(ArrayList<MapLocation> soupLocations) throws GameActionException {
        for(Transaction tx : rc.getBlock(rc.getRoundNum() - 1))
        {
            int [] myMessage = tx.getMessage();
            if(myMessage[0] == teamSecret && myMessage[1] == 2) { //check that message is from our team and the type is hqloc
                // TODO: don't add duplicate locations
                soupLocations.add(new MapLocation(myMessage[2], myMessage[3]));
            }
        }
    }

    public void broadcastSoupLocation(MapLocation loc) throws GameActionException {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = 2; //index of message type - 0 = hq location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            System.out.println("new soup!" + loc);
        }
    }

    public MapLocation getEnemyHQFromBlockchain() throws GameActionException {
        int count = 0;

        for (Transaction tx : rc.getBlock(rc.getRoundNum() - 1)) {
            int[] myMessage = tx.getMessage();
            if (myMessage[0] == teamSecret && myMessage[1] == 3) { //check that message is from our team and the type is hqloc
                return new MapLocation(myMessage[2], myMessage[3]);
            }
        }
        return null;
    }

    public void broadcastRush(MapLocation loc) throws GameActionException {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = 3; //index of message type - 0 = hq location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            System.out.println("Enemy Location found!" + loc);
            rush = true;
        }
    }



    public void broadcastDesignSchoolCreation(MapLocation loc) throws GameActionException {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = 1; //index of message type - 1 = design school location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            broadcastedCreation = true;
        }
    }

    public void broadcastRefineryCreation(MapLocation loc) throws GameActionException {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = 3; //index of message type - 3 = refinery location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            broadcastedCreation = true;
        }
    }

    public boolean broadcastMessage(MapLocation loc, int messageIndex) throws GameActionException {
        int [] message = new int [7];
        message[0] = teamSecret;
        message[1] = messageIndex; //index of message type - 6 = landscaper location
        message[2] = loc.x;
        message[3] = loc.y;

        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
            return true;
        }
        return false;
    }
}
