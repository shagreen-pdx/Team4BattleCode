package team4player;

import battlecode.common.*;

public class Unit extends Robot{

    MapLocation hqLoc;
    Navigation nav;

    public Unit(RobotController r) {
        super(r);
        nav = new Navigation(rc);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
        findHQ();
        System.out.println("I'm a Unit");
    }

    public void findHQ() throws GameActionException{
        if(hqLoc == null){
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(RobotInfo robot : robots){
                if(robot.type == RobotType.HQ && robot.team == rc.getTeam()){
                    hqLoc = robot.location;
                }
            }
        }

        // Later: Communicate via blockchain to find HQ location
        if(hqLoc == null){
            comms.getHqFromBlockchain();
        }
    }

    /**
     *
     * @param target the robot that we want to see if nearby
     * @return true if target robot is nearby
     * @throws GameActionException
     */
     boolean nearbyRobot(RobotType target) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots){
            if(robot.getType() == target){
                return true;
            }
        }
        return false;
    }

}
