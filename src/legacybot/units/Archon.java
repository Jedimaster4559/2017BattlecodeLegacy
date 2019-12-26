package legacybot.units;
import battlecode.common.*;

import static legacybot.tools.UnsortedTools.*;

public class Archon extends Unit{

    ArchonUnitState state;
    int roundsSinceLastGardner;
    int gardenersBuilt;
    int totalArchonNum;

    public Archon(RobotController rc){
        super(rc);
        state = ArchonUnitState.INITIAL;
        roundsSinceLastGardner = Integer.MAX_VALUE;
        gardenersBuilt = 0;
        totalArchonNum = rc.getInitialArchonLocations(enemy).length;
    }

    public void run() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Try to shake any trees that we can
                tryShake(rc);

                checkVictory(rc);

                checkState();

                switch (state){
                    case SLOW:
                        slow();
                        break;
                    case FAST:
                        fast();
                        break;
                    case AGGRESSIVE:
                        aggressive();
                        break;
                    case INITIAL:
                        initial();
                        break;
                    case DEFENSIVE:
                        defensive();
                        break;
                }

                // Try to shake any trees that we can
                tryShake(rc);

                checkVictory(rc);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    public void checkState() throws GameActionException {
        int round = rc.getRoundNum();

        if(round < 5){
            state = ArchonUnitState.INITIAL;
            return;
        }

        if (round > 2750){
            state = ArchonUnitState.DEFENSIVE;
            return;
        }

        if(round > 1000 && round / 100 > gardenersBuilt){
            state = ArchonUnitState.SLOW;
            return;
        } else if (round / 100 / totalArchonNum > gardenersBuilt){
            state = ArchonUnitState.FAST;
            return;
        }

        if(round > 2000 || rc.readBroadcast(13) != 0){
            state = ArchonUnitState.AGGRESSIVE;
            return;
        }

        state = ArchonUnitState.FAST;
    }

    public void slow() throws GameActionException {
        tryMove(randomDirection(), rc);
    }

    public void initial() throws GameActionException{
        tryHire();


    }

    public void aggressive() throws GameActionException{
        tryMove(randomDirection(), rc);
    }

    public void fast() throws GameActionException {
        initial();

        tryMove(randomDirection(), rc);
    }

    public void defensive() throws GameActionException{
        tryMove(randomDirection(), rc);
    }

    /**
     * Attempts to hire a gardener in any direction
     * @throws GameActionException
     */
    private void tryHire() throws GameActionException {
        Direction hireDirection = randomDirection();

        // TODO: This can be optimized a lot
        // Try rotating instead of random
        // Allow some type of escape cause this sometimes might break
        if(rc.getTeamBullets() >= 100) {
            while (!rc.canHireGardener(hireDirection)) {
                hireDirection = randomDirection();
                if(Clock.getBytecodesLeft() < 5000){
                    break;
                }
            }
        }

        if(rc.canHireGardener(hireDirection)){
            rc.hireGardener(hireDirection);
            roundsSinceLastGardner = 0;
            gardenersBuilt++;
        }
    }
}
