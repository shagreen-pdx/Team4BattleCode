package team4player;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Navigation {
    RobotController rc;

    public Navigation(RobotController r){
        rc = r;
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
        if (rc.isReady() && rc.canMove(dir)) {
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
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateRight().rotateRight(), dir.rotateLeft().rotateLeft()};
        for(Direction d : toTry){
            if(tryMove(d)){
                return true;
            }
        }
        return false;
    }

    boolean goTo(MapLocation destination) throws GameActionException {
        return goTo(rc.getLocation().directionTo(destination));
    }
}
