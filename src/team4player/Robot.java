package team4player;
import battlecode.common.*;

import java.util.ArrayList;

public class Robot {
    ArrayList<int []> teamMessages = new ArrayList<int []>();
    boolean teamMessagesSearched = false;
    RobotController rc;
    Communications comms;
    boolean hqIsUnderAttack = false;

    int turnCount = 0;

    public Robot(RobotController r) {
        this.rc = r;
        comms = new Communications(rc);
    }

    public void takeTurn() throws GameActionException {
        if(teamMessages.isEmpty()){
            comms.getAllTeamMessages(teamMessages);
        }
        turnCount += 1;

        // Check if being rushed
        if(rc.getRoundNum() < 250 && !hqIsUnderAttack){
            RobotInfo[] robots = rc.senseNearbyRobots(50, rc.getTeam().opponent());
            if(robots.length > 0){
                comms.broadcastMessage(13,robots[0].location,3);
                for(RobotInfo robot : robots){
                    if(robot.getType() == RobotType.DESIGN_SCHOOL){
                        comms.broadcastMessage(robot.location, 10);
                    }
                }
                hqIsUnderAttack = true;
            }
        }
    }


    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }


    /**
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


    boolean nearbyRobot(RobotType target, Team team) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots){
            if(robot.getType() == target && robot.getTeam() == team){
                return true;
            }
        }
        return false;
    }
}
