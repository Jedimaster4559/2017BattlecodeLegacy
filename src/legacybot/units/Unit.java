package legacybot.units;

import battlecode.common.*;

import java.util.Random;

public abstract class Unit {

    // This is the RobotController object. You use it to perform actions from this robot,
    // and to get information on its current status.
    RobotController rc;
    Team enemy;
    Random rand;

    public Unit(RobotController rc){
        this.rc = rc;
        enemy = rc.getTeam().opponent();
        
        //rand = new Random(1000);
        rand = new Random();
    }

    public abstract void run() throws GameActionException;
}
