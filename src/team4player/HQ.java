package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class HQ extends Building{

    int numRobotsAdjacentToHq = 0;
    boolean hqAttacked = false;
    static int numMiners = 0;
    public HQ(RobotController r){
        super(r);
    }
    boolean wallBuilt = false;
    boolean locBroadcasted = false;

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // Broadcast location
        if(turnCount == 1) {
            comms.broadcastMessage(0, rc.getLocation(), 15);
        }

        // Every turn try to decipher blockchain messages
        decipherCurrentBlockChainMessage();


        if(!hqAttacked){
            RobotInfo[] robots = rc.senseNearbyRobots(rc.getCurrentSensorRadiusSquared(),rc.getTeam().opponent());
            if(robots.length != 0){
                hqAttacked = true;
                for(RobotInfo robot : robots){
                    // If enemy units found, broadcast warning
                    if(robot.getType() == RobotType.MINER || robot.getType() == RobotType.LANDSCAPER){
                        comms.broadcastMessage(robot.location,9);
                    }
                    // Broadcast enemy design school location
                    if(robot.getType() == RobotType.DESIGN_SCHOOL){
                        comms.broadcastMessage(robot.location, 10);
                    }
                }
            }
        }

        // Every turn try broadcast the number of robots around hq
        RobotInfo [] robotsAdjacentToHq = rc.senseNearbyRobots(3);
        int currentNumRobotsAroundHq = 0;
        for(RobotInfo robot : robotsAdjacentToHq){
            ++currentNumRobotsAroundHq;
        }
        System.out.println(numRobotsAdjacentToHq);
        System.out.println(currentNumRobotsAroundHq);
        if(currentNumRobotsAroundHq != numRobotsAdjacentToHq){
            numRobotsAdjacentToHq = currentNumRobotsAroundHq;
            comms.broadcastMessage(numRobotsAdjacentToHq, 12);
        }

        // Try and shoot robots
        RobotInfo [] robots = rc.senseNearbyRobots(49, rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(robot.getID())){
                    rc.shootUnit(robot.getID());
                }
            }
        }

        // Try and build miners
        if((numMiners < 5 && rc.getTeamSoup() > 300) || rc.getRoundNum() < 50){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }

    // Read block chain messages
    public void decipherCurrentBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getEnemyPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            // Try dumb hack
            int [] enemyMessage = new int [7];
            enemyMessage[0] = message[0];
            enemyMessage[1] = message[1];
            enemyMessage[2] = 0;
            enemyMessage[3] = 0;


            if(rc.canSubmitTransaction(enemyMessage, 3)){
                rc.submitTransaction(enemyMessage, 3);
                System.out.println("Broadcasted Enemy message");
            }else {
                System.out.println("failed to broadcast");
            }
        }
    }
}
