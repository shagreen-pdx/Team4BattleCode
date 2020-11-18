package SimpleRushTestBot;

import battlecode.common.*;

public class HQ extends Building {
    static int numMiners = 0;

    public HQ(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        System.out.println("I'm the Hq");

        if(turnCount == 1) {
            comms.broadcastMessage(rc.getLocation(), 0);
        }
        if(turnCount < 3){
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
            comms.broadcastMessage(1,123213);
        }

        // Try and shoot robots
        RobotInfo[] robots = rc.senseNearbyRobots(49, rc.getTeam().opponent());
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
}
