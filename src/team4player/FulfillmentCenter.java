package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class FulfillmentCenter extends Building{

    int numDeliveryDrones = 0;

    public FulfillmentCenter(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {

        numDeliveryDrones += comms.getNewDeliveryDroneCount();

        if(!comms.broadcastedCreation){
            comms.broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 4);
        }
        if(numDeliveryDrones < 3) {
            if (rc.isReady()) {
                for (Direction dir : Util.directions) {
                    if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                        System.out.println("Created a new delivery drone!");
                        ++numDeliveryDrones;
                    }
                }
            }
        }
    }
}
