package com.deo.flapd.control;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.deo.flapd.model.Bullet;
import com.deo.flapd.model.Checkpoint;
import com.deo.flapd.model.Meteorites;
import com.deo.flapd.model.ShipObject;
import com.deo.flapd.model.enemies.Meteorite;

import java.util.Random;

import static com.deo.flapd.utils.DUtils.getBoolean;
import static com.deo.flapd.utils.DUtils.getFloat;
import static com.deo.flapd.utils.DUtils.getInteger;
import static com.deo.flapd.utils.DUtils.getRandomBoolean;
import static com.deo.flapd.utils.DUtils.getString;


public class GameLogic {

    private ShipObject player;

    private Random random;

    private float difficulty;

    public static int bonuses_collected;

    public static boolean bossWave, has1stBossSpawned, has2ndBossSpawned;

    public static int lastCheckpoint;

    public static int Score;
    public static int enemiesKilled;
    public static int money, moneyEarned;

    private float speedMultiplier;

    private Game game;

    private Bullet playerBullet;
    private Polygon playerBounds;
    private Meteorites meteorites;
    private Checkpoint checkpoint;

    public GameLogic(ShipObject ship, boolean newGame, Game game, Meteorites meteorites, Checkpoint checkpoint) {
        player = ship;
        random = new Random();

        this.game = game;

        playerBullet = player.bullet;
        playerBounds = player.bounds;
        this.meteorites = meteorites;
        this.checkpoint = checkpoint;

        difficulty = getFloat("difficulty");

        JsonValue treeJson = new JsonReader().parse(Gdx.files.internal("shop/tree.json"));
        speedMultiplier = treeJson.get(getString("currentEngine")).get("parameterValues").asFloatArray()[0];

        float[] shipParamValues = treeJson.get(getString("currentArmour")).get("parameterValues").asFloatArray();
        String[] shipParams = treeJson.get(getString("currentArmour")).get("parameters").asStringArray();

        for (int i = 0; i < shipParamValues.length; i++) {
            if (shipParams[i].endsWith("speed multiplier")) {
                speedMultiplier *= shipParamValues[i];
            }
        }

        String[] params = treeJson.get(getString("currentCore")).get("parameters").asStringArray();
        float[] paramValues = treeJson.get(getString("currentCore")).get("parameterValues").asFloatArray();
        for (int i = 0; i < params.length; i++) {
            if (params[i].endsWith("speed multiplier")) {
                speedMultiplier *= paramValues[i];
            }
        }

        if (!newGame) {
            bonuses_collected = getInteger("bonuses_collected");
            lastCheckpoint = getInteger("lastCheckpoint");
            has1stBossSpawned = getBoolean("has1stBossSpawned");
            has2ndBossSpawned = getBoolean("has2ndBossSpawned");

            Score = getInteger("Score");
            moneyEarned = getInteger("moneyEarned");

            enemiesKilled = getInteger("enemiesKilled");
        } else {
            bonuses_collected = 0;
            lastCheckpoint = 0;
            has1stBossSpawned = false;
            has2ndBossSpawned = false;

            Score = 0;
            moneyEarned = 0;

            enemiesKilled = 0;
        }
        money = getInteger("money");
        bossWave = false;
    }

    public void handleInput(float delta, float deltaX, float deltaY, boolean is_firing, boolean is_firing_secondary) {
        deltaX *= speedMultiplier;
        deltaY *= speedMultiplier;

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            deltaY = speedMultiplier;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            deltaX = -speedMultiplier;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            deltaY = -speedMultiplier;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            deltaX = speedMultiplier;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
            is_firing = true;
        if (Gdx.input.isKeyPressed(Input.Keys.M))
            is_firing_secondary = true;
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            game.pause();

        if (delta > 0 && player.Health > 0) {
            playerBounds.setPosition(playerBounds.getX() + 250 * deltaX * delta, playerBounds.getY() + 250 * deltaY * delta);
            playerBounds.setRotation(MathUtils.clamp((deltaY - deltaX) * 7, -9, 9));
        }

        if (is_firing) {
            playerBullet.Spawn(1, false);
        }

        if (is_firing_secondary) {
            playerBullet.Spawn(1.5f, true);
        }

        if (playerBullet.isLaser) {
            playerBullet.updateLaser(is_firing || is_firing_secondary);
        }

        if (!bossWave) {

            if (getRandomBoolean(1)) {
                meteorites.Spawn(random.nextInt(480), (random.nextInt(60) - 30) / 10f, random.nextInt(40) + 30 * difficulty);
            }

            if (Score > lastCheckpoint + 9000 && !bossWave) {
                lastCheckpoint = Score;
                checkpoint.Spawn(random.nextInt(300) + 150, random.nextInt(201) + 100, 1);
            }
        }

        if (playerBounds.getX() < 0) {
            playerBounds.setPosition(0, playerBounds.getY());
        }
        if (playerBounds.getX() > 800 - playerBounds.getBoundingRectangle().getWidth()) {
            playerBounds.setPosition(800 - playerBounds.getBoundingRectangle().getWidth(), playerBounds.getY());
        }
        if (playerBounds.getY() < 0) {
            playerBounds.setPosition(playerBounds.getX(), 0);
        }
        if (playerBounds.getY() > 480 - playerBounds.getBoundingRectangle().getHeight()) {
            playerBounds.setPosition(playerBounds.getX(), 480 - playerBounds.getBoundingRectangle().getHeight());
        }
    }

    public void detectCollisions(boolean is_paused) {

        for (int i = 0; i < Meteorites.meteorites.size; i++) {

            Meteorite meteorite = Meteorites.meteorites.get(i);
            float radius = meteorite.radius;

            if (!is_paused) {

                if (meteorite.entityHitBox.overlaps(playerBounds.getBoundingRectangle())) {

                    player.takeDamage(meteorite.health);

                    Score = (int) (Score + radius / 2);

                    meteorite.health = 0;
                }else {
                    for (int i2 = 0; i2 < playerBullet.bullets.size; i2++) {
                        if (meteorite.entityHitBox.overlaps(playerBullet.bullets.get(i2))) {

                            Score = (int) (Score + radius / 2);

                            meteorite.health -= playerBullet.damages.get(i2);

                            playerBullet.removeBullet(i2, true);
                        }
                    }
                    if (playerBullet.laser.getBoundingRectangle().overlaps(meteorite.entityHitBox)) {
                        meteorite.health -= playerBullet.damage;
                    }
                }
            }
        }

    }
}
