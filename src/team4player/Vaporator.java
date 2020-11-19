package team4player;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Vaporator extends Building{

    public Vaporator(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        if (!comms.broadcastedCreation) {
            comms.broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 9);
        }
        int localPollution = rc.sensePollution(rc.getLocation());
        System.out.println("Global pollution level: " + RobotType.VAPORATOR.globalPollutionAmount);
        System.out.println("Local pollution level: "+ localPollution);
        System.out.println(("Local pollution with effect: " +
                localPollution * RobotType.VAPORATOR.localPollutionMultiplicativeEffect ));
    }
}
