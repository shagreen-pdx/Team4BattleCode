package SimpleRushTestBot;

import battlecode.common.*;

public class FulfillmentCenter extends Building {

    int numDeliveryDrones = 0;

    public FulfillmentCenter(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {

//        numDeliveryDrones += comms.getNewDeliveryDroneCount();

        if(!comms.broadcastedCreation){
            comms.broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 4);
        }
        System.out.println(numDeliveryDrones);

        if(numDeliveryDrones < 5 && rc.getTeamSoup() > 300){
            if (rc.isReady() ) {
                for (Direction dir : Util.directions) {
                    if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                        ++numDeliveryDrones;
                        if(numDeliveryDrones < 3){
                            RobotInfo drone = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                            System.out.println("Drone id: " + drone.ID);
                            comms.broadcastMessage(drone.ID, 5);
                        }
                    }
                }
            }
        }
    }
}
