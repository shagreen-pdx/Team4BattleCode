package team4player;

import battlecode.common.*;

import java.util.Arrays;

import static team4player.Util.directions;
import static team4player.Util.randomDirection;

public class DeliveryDrone extends Unit{

    public DeliveryDrone(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException{
        MapLocation loc = rc.getLocation();
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            nav.tryMove(randomDirection());
            System.out.println("nothing" + Arrays.toString(rc.senseNearbyRobots(5, enemy)));
            if(rc.senseFlooding(loc))
                System.out.println("This place is flooded.");
        }

        if(rc.isCurrentlyHoldingUnit()){
            nav.tryMove(randomDirection());
            if(rc.senseFlooding(loc))
                System.out.println("This place is flooded.");
        }



//            // See if there are any enemy robots within capturing range
//            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);
//
//            if (robots.length > 0) {
//                // Pick up a first robot within range
//                rc.pickUpUnit(robots[0].getID());
//                System.out.println("I picked up " + robots[0].getID() + "!");
//            }
//        } else {
//            // No close robots, so search for robots within sight radius
//            nav.tryMove(randomDirection());
//        }
    }
}
