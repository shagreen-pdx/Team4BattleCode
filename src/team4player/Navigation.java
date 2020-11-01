package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.ArrayList;

public class Navigation {
    RobotController rc;
    int mapHeight;
    int mapWidth;
    ArrayList<MapLocation> prevLocations = new ArrayList<MapLocation>();

    public Navigation(RobotController r){
        rc = r;
        mapHeight = rc.getMapHeight();
        mapWidth = rc.getMapWidth();
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }

    boolean tryMove() throws GameActionException {
        for (Direction dir : Util.directions)
            if (tryMove(dir))
                return true;
        return false;
    }

    boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(),dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight(), dir.opposite().rotateLeft(), dir.opposite().rotateRight(),  dir.opposite() };
        for(Direction d : toTry){
            if(!prevLocations.contains(rc.getLocation().add(d))){
                if(tryMove(d)){
                    return true;
                }
            }
        }
        return false;
    }

    boolean goTo(MapLocation destination) throws GameActionException {
        return goTo(rc.getLocation().directionTo(destination));
    }
}
