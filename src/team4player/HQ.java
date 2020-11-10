package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class HQ extends Building{
    static int numMiners = 0;

    public HQ(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        decipherCurrentBlockChainMessage();
        System.out.println("I'm the Hq");

        if(turnCount == 1) {
            comms.broadcastMessage(rc.getLocation(), 0);
        }

        RobotInfo [] robots = rc.senseNearbyRobots(49, rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(robot.getID())){
                    rc.shootUnit(robot.getID());
                }
            }
        }

        if((numMiners < 7 && rc.getTeamSoup() > 300) || rc.getRoundNum() < 50){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException {
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if (message[0] != comms.getSecretCode()) {
                // Try dumb hack
                int [] enemyMessage = new int [7];
                enemyMessage[0] = message[0];
                enemyMessage[1] = message[1]; //index of message type - 6 = landscaper location
                enemyMessage[2] = 0;
                enemyMessage[3] = 0;

                if(rc.canSubmitTransaction(enemyMessage, 3)){
                    rc.submitTransaction(enemyMessage, 3);
                    System.out.println("Broadcasted Enemy message");
                }
            }

        }
    }
}
