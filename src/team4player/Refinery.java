package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

import static java.lang.Math.min;

public class Refinery extends Building{

    public Refinery(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        int old_soup = rc.getTeamSoup();
        //System.out.println("Pollution(sense): " + rc.sensePollution(rc.getLocation()));
        System.out.println("Team Soup deposited: " + rc.getTeamSoup());
        System.out.println("Soup Refined: " + min(RobotType.REFINERY.maxSoupProduced, rc.getSoupCarrying()));
        System.out.println("Globe pollution level: " + RobotType.REFINERY.globalPollutionAmount);
        System.out.println("Range of pollution: " + RobotType.REFINERY.pollutionRadiusSquared);
        System.out.println("Temp pollution in the range: " + RobotType.REFINERY.localPollutionAdditiveEffect);
    }
}
