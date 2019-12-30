package legacybot.units;

import battlecode.common.*;

import java.util.Random;

import static legacybot.tools.UnsortedTools.checkVictory;
import static legacybot.tools.UnsortedTools.tryShake;

public abstract class Unit {

    // This is the RobotController object. You use it to perform actions from this robot,
    // and to get information on its current status.
    RobotController rc;
    Team enemy;
    Random rand;
    int age; // The number of rounds this unit has been alive.
    MapLocation spawn;

    public Unit(RobotController rc){
        this.rc = rc;
        enemy = rc.getTeam().opponent();
        
        //rand = new Random(1000);
        rand = new Random();

        spawn = rc.getLocation();

        age = 0;
    }

    public abstract void run() throws GameActionException;

    public final void roundStart() throws GameActionException {
        // Try to shake any trees that we can
        tryShake(rc);

        // Check to see if we can win the match
        checkVictory(rc);
    }

    public final void roundEnd() throws GameActionException {
        // Try to shake any trees that we can
        tryShake(rc);

        // Check to see if we can win the match
        checkVictory(rc);

        // Increment the age counter
        age++;

        // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
        Clock.yield();
    }
}
