package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class HQ extends Building{
    static int numMiners = 0;

    public HQ(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        System.out.println("I'm the Hq");

        if(turnCount == 1) {
            comms.sendHqLoc(rc.getLocation());
        }
        if(numMiners < 10){
            for (Direction dir : Util.directions){
                if(tryBuild(RobotType.MINER, dir)){
                    ++numMiners;
                }
            }
        }
    }
}
