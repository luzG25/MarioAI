package ch.idsia.agents.controllers;

import ch.idsia.benchmark.mario.environments.Environment;

import java.util.Random;

public class BehaviorTreeMarioAgent extends BasicMarioAIAgent {

    private Random random;

    public BehaviorTreeMarioAgent(String name) {
        super(name);
        random = new Random();
    }

    @Override
    public boolean[] getAction() {
        // Reset actions
        for (int i = 0; i < action.length; i++) {
            action[i] = false;
        }

        // High-level decision-making
        if (isEnemyAhead()) {
            if (isMarioAbleToShoot) {
                shootEnemy();
            } else {
                jumpOverEnemy();
            }
        } else if (isCoinNearby()) {
            collectCoin();
        } else {
            moveForward();
        }

        return action;
    }

    private boolean isEnemyAhead() {
        // Check for enemies in front of Mario in the observation grid
        for (int y = marioEgoRow - 1; y <= marioEgoRow + 1; y++) {
            for (int x = marioEgoCol + 1; x <= marioEgoCol + 2; x++) {
                if (getEnemiesCellValue(x, y) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCoinNearby() {
        // Check for coins in the observation grid
        for (int y = marioEgoRow - 1; y <= marioEgoRow + 1; y++) {
            for (int x = marioEgoCol - 1; x <= marioEgoCol + 1; x++) {
                if (getReceptiveFieldCellValue(x, y) == 2) { // Assuming '2' represents coins
                    return true;
                }
            }
        }
        return false;
    }

    private void shootEnemy() {
        action[Environment.MARIO_KEY_SPEED] = true;
    }

    private void jumpOverEnemy() {
        action[Environment.MARIO_KEY_JUMP] = true;
    }

    private void collectCoin() {
        action[Environment.MARIO_KEY_SPEED] = true;
        action[Environment.MARIO_KEY_RIGHT] = true;
    }

    private void moveForward() {
        action[Environment.MARIO_KEY_RIGHT] = true;
        if (isMarioAbleToJump || !isMarioOnGround) {
            action[Environment.MARIO_KEY_JUMP] = random.nextBoolean();
        }
    }

    @Override
    public void integrateObservation(Environment environment) {
        super.integrateObservation(environment);
    }

    @Override
    public void reset() {
        super.reset();
    }
}
 
    

