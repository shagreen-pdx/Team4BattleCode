package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class HQ extends Building{

    boolean rushFailed = false;

    int numRobotsAdjacentToHq = 0;
    boolean hqAttacked = false;
    static int numMiners = 0;
    public HQ(RobotController r){
        super(r);
    }
    boolean wallBuilt = false;

    ArrayList<MapLocation> allEnemyDesignSchoolLocations = new ArrayList<>();

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Broadcast location
        if(turnCount == 1) {
            comms.broadcastMessage(rc.getLocation(), 0);
        } else {
            // Every turn try to decipher blockchain messages
            decipherCurrentBlockChainMessage();
        }

//        if(rushFailed){
//            if(turnCount % 2 == 0){
//                decipherEnemyBlockChainMessage();
//            }
//        }

        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1,rc.getTeam().opponent());


        // Check to see if attacked
        if(!hqAttacked){
            // If enemy rush happens first, call off rush
            if(enemyRobots.length != 0 && !rush){
                comms.broadcastMessage(14, 2);
                hqAttacked = true;
            }
        } else {
            // If HQ is attack
            // Broadcast the number of robots around hq
            RobotInfo [] robotsAdjacentToHq = rc.senseNearbyRobots(3);
            broadcastNumUnitsAdjacentToHq(robotsAdjacentToHq);
        }

        if (enemyRobots.length != 0){
            defendHq(enemyRobots);
        }


        // Try and build miners
        if(numMiners < 4){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }

    // Read block chain messages
    public void decipherEnemyBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getEnemyPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            // Try dumb hack
            int [] enemyMessage = new int [7];
            enemyMessage[0] = message[0];
            enemyMessage[1] = message[1];
            enemyMessage[2] = -5;
            enemyMessage[3] = -5;
            enemyMessage[4] = -5;
            enemyMessage[5] = -5;
            enemyMessage[6] = -5;


            if(rc.canSubmitTransaction(enemyMessage, 1)){
                rc.submitTransaction(enemyMessage, 1);
                System.out.println("Broadcasted Enemy message");
            }else {
                System.out.println("failed to broadcast");
            }
        }
    }

    // Decipher current blockchain messages
    public void decipherCurrentBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            // Add enemy buildings
            if (message[1] == 6) {
                enemyHqLoc = new MapLocation(message[2], message[3]);
            }
            // Rush enemy HQ
            if (message[1] == 8){
                rush = true;
            }
            // Add enemy hq loc
            else if (message[1] == 14) {
                rush = false;
            }
        }
    }

    // Depending on the unit type, perform different defensive actions
    public void defendHq(RobotInfo[] enemyRobots) throws GameActionException{
        for(RobotInfo robot : enemyRobots){
            // Shoot Delivery Drones
            if(robot.getType() == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(robot.getID())){
                    rc.shootUnit(robot.getID());
                }
            }

            // If enemy units found, broadcast warning
            if(robot.getType() == RobotType.MINER || robot.getType() == RobotType.LANDSCAPER){
                comms.broadcastMessage(9,robot.location,1);
            }
            // Broadcast enemy design school location
            if(robot.getType() == RobotType.DESIGN_SCHOOL){
                if(!allEnemyDesignSchoolLocations.contains(robot.location)){
                    comms.broadcastMessage(10, robot.location, 2);
                }
            }
        }
    }

    public void broadcastNumUnitsAdjacentToHq(RobotInfo[] robotsAdjacentToHq) throws GameActionException {
        int currentNumRobotsAroundHq = robotsAdjacentToHq.length;

        if(currentNumRobotsAroundHq != numRobotsAdjacentToHq){
            numRobotsAdjacentToHq = currentNumRobotsAroundHq;
            comms.broadcastMessage( 12, numRobotsAdjacentToHq, 1);
        }
    }
}
