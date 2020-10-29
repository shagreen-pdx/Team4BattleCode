package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Landscaper extends Unit{

    public Landscaper(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
         /* Landscaper: moves dirt around the map to adjust elevation and destroy buildings.

Produced by the design school.
Can perform the action rc.digDirt() to remove one unit of dirt from an adjacent tile or its current tile, increasing the landscaper’s stored dirt by 1 up to a max of RobotType.LANDSCAPER.dirtLimit (currently set to 25). If the tile is empty, flooded, or contains another unit, this reduces the tile’s elevation by 1. If the tile contains a building, it removes one unit of dirt from the building, or if the building is not buried, has no effect.
Can perform the action rc.depositDirt() to reduce its stored dirt by one and place one unit of dirt onto an adjacent tile or its current tile. If the tile contains a building, the dirt partially buries it–the health of a building is how much dirt can be placed on it before it is destroyed. If the tile is empty, flooded, or contains another unit, the only effect is that the elevation of that tile increases by 1.
Note: all this means that buildings may never change elevation, so be careful to contain that water level.
When a landscaper dies, the dirt it’s carrying is dropped on the current tile.
If enough dirt is placed on a flooded tile to raise its elevation above the water level, it becomes not flooded. */
        if(rc.getDirtCarrying() == 0){
            tryDig();
        }

        MapLocation bestLocation = null;
        if(hqLoc != null){
            System.out.println("Found hq");

            int lowestElevation = 9999999;
            //Loops through all of the locations around hq and checks for the lowest elevation that can be dropped, then drops it
            for(Direction dir : Util.directions){
                // Add function: Takes a map location add a direction and returns the first location plus the direction
                MapLocation tileToCheck = hqLoc.add(dir);
                if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4
                        && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))){
                    if(rc.senseElevation(tileToCheck) < lowestElevation){
                        lowestElevation = rc.senseElevation(tileToCheck);
                        bestLocation = tileToCheck;
                    }
                }
            }
            // Will be null if it knows where the hq is but all of the locations are blocked
        }else{
            System.out.println("Can't find HQ");
        }
        if(Math.random() < 0.4){
            if(bestLocation != null){
                rc.depositDirt(rc.getLocation().directionTo(bestLocation));
                System.out.println("Building a wall");
            }
        }

        // Try to get to hq
        if(hqLoc != null){
            nav.goTo(hqLoc);
        }else{
            nav.tryMove(Util.randomDirection());
        }
    }

    boolean tryDig() throws GameActionException {
        Direction dir = Util.randomDirection();
        if(rc.canDigDirt(dir)){
            rc.digDirt(dir);
            return true;
        }
        return false;
    }
}
