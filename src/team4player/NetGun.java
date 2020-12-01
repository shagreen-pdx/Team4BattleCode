package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class NetGun extends Building{

    public NetGun(RobotController r){
        super(r);
    }

    @Override
    public void takeTurn() throws GameActionException {
        super.takeTurn();

        RobotInfo[] robots = rc.senseNearbyRobots(16, rc.getTeam().opponent());
        for(RobotInfo robot : robots){
            if(robot.getType() == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(robot.getID())){
                    rc.shootUnit(robot.getID());
                }
            }
        }
    }
}