package team4player;

import battlecode.common.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;

public class HQ extends Building {

    boolean rush = true;
    boolean startedAttack = false;
    boolean broadcastedRushFailed = false;

    int numRobotsAdjacentToHq = 0;
    boolean hqAttacked = false;
    boolean wallBuilt = false;

    int minerLimit = 4;
    static int numMiners = 0;


    public HQ(RobotController r) {
        super(r);
    }

    ArrayList<MapLocation> allEnemyDesignSchoolLocations = new ArrayList<>();

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Broadcast location
        if (turnCount == 1) {
            comms.broadcastMessage(rc.getLocation(), 0);
        } else {
            // Every turn try to decipher blockchain messages
            decipherCurrentBlockChainMessage();
        }

        // Sense Enemy Robots
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        // If rushing, limit production of miners
        if (rush) {
            enemyRobots = takeRush(enemyRobots);
        }
        // If not rush, implement defensive procedures
        else {
            enemyRobots = takeElse(enemyRobots);
        }

        // Try and build miners
        buildMiners();

    }

    public RobotInfo[] takeRush(RobotInfo[] enemyRobots) throws GameActionException {
        minerLimit = 4;
        if (!startedAttack) {
            System.out.println("ATTACK NOT STARTED");
            // If attack has not started by round, call off attack
            if (enemyRobots.length != 0 || rc.getRoundNum() == roundNumberToCallOffRushIfAttackNotStarted) {
                System.out.println("ATTACK FAILED");
                rush = false;
                broadcastedRushFailed = comms.broadcastMessage(14, 2);
            }
        } else {
            System.out.println("STARTED ATTACK");
        }
        return enemyRobots;
    }

    public RobotInfo[] takeElse(RobotInfo[] enemyRobots) throws GameActionException {
        if (!broadcastedRushFailed) {
            broadcastedRushFailed = comms.broadcastMessage(14, 2);
        }

        System.out.println("DEFEND HQ");
        minerLimit = 5;

        RobotInfo[] robotsAdjacentToHq = rc.senseNearbyRobots(3);
        broadcastNumUnitsAdjacentToHq(robotsAdjacentToHq);

        if (enemyRobots.length != 0) {
            defendHq(enemyRobots);
        }
        return enemyRobots;
    }






    // Build miners if wall is not built
    public int buildMiners() throws GameActionException {
        if(!wallBuilt && numMiners < minerLimit){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
        return numMiners;
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

            // Broadcast enemy design school location
            if(robot.getType() == RobotType.DESIGN_SCHOOL){
                if(!allEnemyDesignSchoolLocations.contains(robot.location)){
                    allEnemyDesignSchoolLocations.add(robot.location);
                    comms.broadcastMessage(10, robot.location, 2);
                }
            }
        }
    }

    public void broadcastNumUnitsAdjacentToHq(RobotInfo[] robotsAdjacentToHq) throws GameActionException {
        int currentNumRobotsAroundHq = robotsAdjacentToHq.length;

        if(isNearbyRobot(robotsAdjacentToHq,RobotType.LANDSCAPER,rc.getTeam())){
            wallBuilt = true;
        }

        if(currentNumRobotsAroundHq != numRobotsAdjacentToHq){
            numRobotsAdjacentToHq = currentNumRobotsAroundHq;
            comms.broadcastMessage( 12, numRobotsAdjacentToHq, 1);
        }
    }

    // *************************BLOCK CHAIN*****************************

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
                startedAttack = true;
            }
            // Add enemy hq loc
            else if (message[1] == 14) {
                rush = false;
            }
        }
    }
}
