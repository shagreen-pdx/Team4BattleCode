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
        MapLocation ememyUnitLoc;
        if (!rc.isCurrentlyHoldingUnit()) {
            nav.tryMove(randomDirection());
            //if(rc.canPickUpUnit())
            System.out.println("nothing" + Arrays.toString(rc.senseNearbyRobots(24, enemy)));
            if(rc.senseFlooding(loc))
                System.out.println("This place is flooded.");
        }


    }
}
