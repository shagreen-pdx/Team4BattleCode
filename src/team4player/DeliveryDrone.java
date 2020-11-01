package team4player;

import battlecode.common.*;

import static team4player.Util.randomDirection;

public class DeliveryDrone extends Unit{

    public DeliveryDrone(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException{
//        if(!comms.broadcastedCreation){
//            comms.broadcastDeliveryDroneCreation(rc.getLocation());
//        }
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            } else {
                // No close robots, so search for robots within sight radius
                nav.tryMove(randomDirection());
            }
        }
    }
}
