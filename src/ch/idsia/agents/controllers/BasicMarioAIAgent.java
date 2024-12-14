/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

 package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

import java.util.Random;

public class BasicMarioAIAgent implements Agent {

    protected boolean action[] = new boolean[Environment.numberOfKeys];
    protected String name = "Instance_of_BasicAIAgent._Change_this_name";

    protected byte[][] levelScene;
    protected byte[][] enemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;

    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;

    protected int receptiveFieldWidth;
    protected int receptiveFieldHeight;
    protected int marioEgoRow;
    protected int marioEgoCol;

    int zLevelScene = 1;
    int zLevelEnemies = 0;

    private Random random;

    public BasicMarioAIAgent(String s) {
        setName(s);
        random = new Random();
    }

    @Override
    public boolean[] getAction() {
        for (int i = 0; i < action.length; i++) {
            action[i] = false;
        }

        // High-level decision-making using Behavior Tree
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
        for (int y = marioEgoRow - 1; y <= marioEgoRow + 1; y++) {
            for (int x = marioEgoCol - 1; x <= marioEgoCol + 1; x++) {
                if (getReceptiveFieldCellValue(x, y) == 2) {
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
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);

        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        receptiveFieldWidth = environment.getReceptiveFieldWidth();
        receptiveFieldHeight = environment.getReceptiveFieldHeight();

        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }

    @Override
    public void reset() {
        action = new boolean[Environment.numberOfKeys];
    }

    @Override
    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol) {
        receptiveFieldWidth = rfWidth;
        receptiveFieldHeight = rfHeight;

        marioEgoRow = egoRow;
        marioEgoCol = egoCol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String Name) {
        this.name = Name;
    }

    public int getEnemiesCellValue(int x, int y) {
        if (x < 0 || x >= levelScene.length || y < 0 || y >= levelScene[0].length)
            return 0;

        return enemies[x][y];
    }

    public int getReceptiveFieldCellValue(int x, int y) {
        if (x < 0 || x >= levelScene.length || y < 0 || y >= levelScene[0].length)
            return 0;

        return levelScene[x][y];
    }

    @Override
    public void giveIntermediateReward(float intermediateReward) {
        // Método vazio para evitar erros
    }
}
