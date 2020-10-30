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
        if(!comms.broadcastedCreation){
            comms.broadcastFulfillementCenterCreation(rc.getLocation());
        }
        if (rc.isReady()) {
            for (Direction dir : Util.directions) {
                if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                    System.out.println("Created a new delivery drone!");
                }
            }
        }
    }
}
