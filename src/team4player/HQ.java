package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class HQ extends Building{

    static int numMiners = 0;
    public HQ(RobotController r){
        super(r);
    }
    boolean wallBuilt = false;

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        System.out.println("I'm the Hq");

        if(turnCount == 1) {
            comms.broadcastMessage(rc.getLocation(), 0);
        }
        decipherCurrentBlockChainMessage();

        RobotInfo [] robots = rc.senseNearbyRobots(49, rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(robot.getID())){
                    rc.shootUnit(robot.getID());
                }
            }
        }

        if((numMiners < 5 && rc.getTeamSoup() > 300) || rc.getRoundNum() < 50){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }

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
