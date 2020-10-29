package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Building extends Robot{

    public Building(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        System.out.println("I'm a building");
    }
}
