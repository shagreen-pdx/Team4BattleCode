package team4player;

import battlecode.common.*;

import java.util.ArrayList;

public class FulfillmentCenter extends Building{

    int numDeliveryDrones = 0;
    public FulfillmentCenter(RobotController r){
        super(r);
    }

    public void takeTurn() throws GameActionException {
        if(!comms.broadcastedCreation){
            comms.broadcastedCreation = comms.broadcastMessage(rc.getLocation(), 4);
        }
        System.out.println(numDeliveryDrones);
        decipherCurrentBlockChainMessage();

        if(canBuild && rc.getTeamSoup() > 210){
            if (rc.isReady() ) {
                for (Direction dir : Util.directions) {
                    if (tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                        ++numDeliveryDrones;
                        canBuild = false;
                        comms.broadcastMessage(rc.getLocation(),15);
                        if(numDeliveryDrones == -1){
                            RobotInfo drone = rc.senseRobotAtLocation(rc.getLocation().add(dir));
                            System.out.println("Drone id: " + drone.ID);
                            comms.broadcastMessage(drone.ID, 5);
                        }
                    }
                }
            }
        }
    }

    public void decipherCurrentBlockChainMessage() throws GameActionException{
        ArrayList<int []> currentBlockChainMessage = comms.getPrevRoundMessages();
        for(int [] message : currentBlockChainMessage){
            if (message[1] == 15) {
                MapLocation loc = new MapLocation(message[2], message[3]);
                if(!loc.equals(rc.getLocation())){
                    canBuild = true;
                }
            }
        }
    }
}
