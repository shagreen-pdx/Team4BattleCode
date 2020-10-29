package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class FulfillmentCenter extends Building{

    public FulfillmentCenter(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        for (Direction dir : Util.directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }
}
