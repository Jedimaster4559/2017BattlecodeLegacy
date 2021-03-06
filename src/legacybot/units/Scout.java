package legacybot.units;

import battlecode.common.*;

import static legacybot.tools.UnsortedTools.randomDirection;
import static legacybot.tools.UnsortedTools.tryMove;

public class Scout extends Unit {
    public Scout(RobotController rc){
        super(rc);
    }

    public void run() throws GameActionException{
        System.out.println("I'm a Scout");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}
