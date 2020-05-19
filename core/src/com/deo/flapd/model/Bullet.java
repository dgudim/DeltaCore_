package com.deo.flapd.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.deo.flapd.control.GameLogic;
import com.deo.flapd.model.enemies.Enemies;

import java.util.Random;

import static com.deo.flapd.utils.DUtils.getFloat;
import static com.deo.flapd.utils.DUtils.getInteger;
import static com.deo.flapd.utils.DUtils.getItemCodeNameByName;
import static com.deo.flapd.utils.DUtils.getString;

public class Bullet {

    private Polygon bounds;
    public static Array<Rectangle> bullets;
    public static Array<Integer> damages;
    private Array<ParticleEffect> trails, disposedTrails;
    private Array<Float> trailCountDownTimers;
    private Array<Float> degrees;
    private Array<Float> explosionTimers;
    private Array<ParticleEffect> explosions;
    private Array<Boolean> types;
    private Sprite bullet;

    private Music laserSaw;
    private Sound shot;

    private float soundVolume;

    public static int bulletsShot;

    private Random random;

    private float spread;

    private static Array<Boolean> explosionQueue, remove_Bullet;

    private float shootingSpeedMultiplier;
    private float powerConsumption;

    public static Sprite laser;

    private String bulletExplosionEffect;
    private String bulletTrailEffect;
    private boolean hasTrail;

    private float width, height;

    public static int damage;
    private int baseDamage;
    private int bulletSpeed;
    private int bulletsPerShot;
    private float bulletTrailTimer;

    private float millis;

    private float laserHeight;
    private float laserDuration, currentDuration;
    private boolean isLaserActive;
    public boolean isLaser;
    private boolean isHoming;
    private float explosionTimer;

    private int gunCount;
    private int currentActiveGun;
    private float[] gunOffsetsX;
    private float[] gunOffsetsY;

    public Bullet(AssetManager assetManager, Polygon shipBounds, boolean newGame) {
        bounds = shipBounds;

        TextureAtlas bullets = assetManager.get("player/bullets.atlas");

        JsonValue treeJson = new JsonReader().parse(Gdx.files.internal("shop/tree.json"));
        JsonValue shipConfig = new JsonReader().parse(Gdx.files.internal("player/shipConfigs.json")).get(getString("currentArmour"));

        gunCount = shipConfig.get("gunCount").asInt();
        currentActiveGun = 0;

        gunOffsetsX = new float[gunCount];
        gunOffsetsY = new float[gunCount];

        for(int i = 0; i<gunCount; i++){
            gunOffsetsX[i] = shipConfig.get("guns").get("gun"+i+"OffsetX").asFloat();
            gunOffsetsY[i] = shipConfig.get("guns").get("gun"+i+"OffsetY").asFloat();
        }

        bulletExplosionEffect = treeJson.get(getString("currentCannon")).get("usesEffect").asString();

        if (treeJson.get(getString("currentCannon")).get("usesTrail").asBoolean()) {
            bulletTrailEffect = treeJson.get(getString("currentCannon")).get("trailEffect").asString();
            bulletTrailTimer = treeJson.get(getString("currentCannon")).get("trailFadeOutTimer").asFloat();
            hasTrail = true;
        }else{
            bulletTrailEffect = "";
            bulletTrailTimer = 0;
            hasTrail = false;
        }

        String[] params = treeJson.get(getString("currentCannon")).get("parameters").asStringArray();
        float[] paramValues = treeJson.get(getString("currentCannon")).get("parameterValues").asFloatArray();
        for (int i = 0; i < params.length; i++) {
            if (params[i].endsWith("damage")) {
                damage = (int) paramValues[i];
                baseDamage = (int) paramValues[i];
            }
            if (params[i].endsWith("shooting speed")) {
                shootingSpeedMultiplier = paramValues[i];
            }
            if (params[i].endsWith("spread")) {
                spread = paramValues[i];
            }
            if (params[i].endsWith("power consumption")) {
                powerConsumption = paramValues[i];
            }
            if (params[i].endsWith("bullet speed")) {
                bulletSpeed = (int) paramValues[i];
            }
            if (params[i].endsWith("bullets per shot")) {
                bulletsPerShot = (int) paramValues[i];
            }
            if (params[i].endsWith("laser beam thickness")) {
                laserHeight = paramValues[i];
                isLaser = true;
            }
            if (params[i].endsWith("laser pulse duration")) {
                laserDuration = paramValues[i];
                currentDuration = paramValues[i];
            }
        }

        if (isLaser) {
            bullet = new Sprite();
        } else {
            bullet = new Sprite(bullets.findRegion("bullet_" + getItemCodeNameByName(getString("currentCannon"))));
        }

        params = treeJson.get(getString("currentCore")).get("parameters").asStringArray();
        paramValues = treeJson.get(getString("currentCore")).get("parameterValues").asFloatArray();
        for (int i = 0; i < params.length; i++) {
            if (params[i].endsWith("damage multiplier")) {
                damage *= paramValues[i];
                baseDamage *= paramValues[i];
            }
        }

        isHoming = treeJson.get(getString("currentCannon")).get("homing").asBoolean();
        if(isHoming){
            explosionTimer = treeJson.get(getString("currentCannon")).get("explosionTimer").asFloat();
        }

        random = new Random();

        Bullet.bullets = new Array<>();
        damages = new Array<>();
        degrees = new Array<>();
        explosions = new Array<>();
        explosionQueue = new Array<>();
        remove_Bullet = new Array<>();
        types = new Array<>();
        trails = new Array<>();
        disposedTrails = new Array<>();
        trailCountDownTimers = new Array<>();
        explosionTimers = new Array<>();

        if (!newGame) {
            bulletsShot = getInteger("bulletsShot");
        } else {
            bulletsShot = 0;
        }

        laser = new Sprite((Texture) assetManager.get("laser.png"));
        laser.setSize(3, laserHeight);
        laser.setPosition(-100, -100);

        soundVolume = getFloat("soundVolume");
        if (isLaser) {
            laserSaw = Gdx.audio.newMusic(Gdx.files.internal("sfx/laserSaw.ogg"));
            laserSaw.play();
            laserSaw.setLooping(true);
            laserSaw.setVolume(0);
        } else {
            shot = Gdx.audio.newSound(Gdx.files.internal("sfx/gun4.ogg"));
        }

        width = bullet.getWidth();
        height = bullet.getHeight();

        float scale = 10 / height;
        width = width * scale;
        height = 10;

        bullet.setOrigin(0, 5);
    }

    public void Spawn(float damageMultiplier, boolean is_charged) {

        if (!isLaser) {
            if (SpaceShip.Charge >= powerConsumption && millis > 11 / (shootingSpeedMultiplier + (GameLogic.bonuses_collected + 1) / 10.0f)) {
                for (int i = 0; i < bulletsPerShot; i++) {
                    Rectangle bullet = new Rectangle();

                    bullet.setSize(width, height);

                    bullet.x = bounds.getX() + gunOffsetsX[currentActiveGun];
                    bullet.y = bounds.getY() + gunOffsetsY[currentActiveGun];

                    if(currentActiveGun+1 < gunCount){
                        currentActiveGun++;
                    }else{
                        currentActiveGun = 0;
                    }

                    bullets.add(bullet);
                    explosionQueue.add(false);
                    remove_Bullet.add(false);
                    if (SpaceShip.Charge >= powerConsumption * damageMultiplier / bulletsPerShot + 0.5f && is_charged) {
                        types.add(true);
                        damages.add((int) (damage * damageMultiplier));
                        SpaceShip.Charge -= powerConsumption * damageMultiplier / bulletsPerShot + 0.5f;
                    } else {
                        types.add(false);
                        damages.add(damage);
                        SpaceShip.Charge -= powerConsumption / bulletsPerShot;
                    }

                    degrees.add((random.nextFloat() - 0.5f) * spread + bounds.getRotation() / 20);

                    if(isHoming){
                        explosionTimers.add(explosionTimer);
                    }

                    bullet.x += MathUtils.cosDeg(bounds.getRotation()) * 6;
                    bullet.y += MathUtils.cosDeg(bounds.getRotation());

                    if (hasTrail) {
                        ParticleEffect trail = new ParticleEffect();
                        trail.load(Gdx.files.internal("particles/" + bulletTrailEffect + ".p"), Gdx.files.internal("particles"));
                        trail.start();
                        trails.add(trail);
                    }

                    bulletsShot++;
                }

                if (soundVolume > 0) {
                    shot.play(soundVolume / 100);
                }

                millis = 0;
            }
        } else {
            damage = baseDamage * (GameLogic.bonuses_collected + 1) / 10;
        }
    }

    public void updateLaser(boolean active) {
        isLaserActive = active;
    }

    public void draw(SpriteBatch batch, float delta) {

        if (!isLaser) {
            for (int i = 0; i < bullets.size; i++) {

                Rectangle bullet = bullets.get(i);
                float angle = degrees.get(i);

                if (hasTrail) {
                    trails.get(i).setPosition(bullet.x + bullet.width / 2, bullet.y + bullet.height / 2);
                }

                this.bullet.setPosition(bullet.x, bullet.y);
                this.bullet.setSize(bullet.width, bullet.height);
                if (types.get(i)) {
                    this.bullet.setColor(Color.GREEN);
                } else {
                    this.bullet.setColor(Color.WHITE);
                }
                this.bullet.draw(batch);

                if(!isHoming) {
                    bullet.x += bulletSpeed * delta;
                    bullet.y += 300 * bulletSpeed / 1500.0f * angle * delta;
                    this.bullet.setRotation(MathUtils.radiansToDegrees * MathUtils.atan2(300 * angle, 1500));
                }else{
                    float posX = bounds.getX() + 1000;
                    float posY = bounds.getY();
                    for(int i2 = 0; i2<Enemies.enemyEntities.size; i2++){
                        if(!Enemies.enemyEntities.get(i2).isDead){
                            posX = Enemies.enemyEntities.get(i2).data.x + Enemies.enemyEntities.get(i2).data.width / 2;
                            posY = Enemies.enemyEntities.get(i2).data.y + Enemies.enemyEntities.get(i2).data.height / 2;
                            break;
                        }
                    }
                    bullet.x = MathUtils.lerp(bullet.x, posX, bulletSpeed * delta / 1300);
                    bullet.y = MathUtils.lerp(bullet.y, posY, bulletSpeed * delta / 1300);
                    this.bullet.setRotation(MathUtils.radiansToDegrees * MathUtils.atan2(posY - bullet.y, posX - bullet.x));
                    explosionTimers.set(i, explosionTimers.get(i) - delta);
                    if(explosionTimers.get(i)<=0){
                        Bullet.removeBullet(i, true);
                    }
                }

                if (bullet.x > 800) {
                    Bullet.removeBullet(i, false);
                }
            }
            for (int i3 = 0; i3 < explosions.size; i3++) {
                explosions.get(i3).draw(batch, delta);
                if (explosions.get(i3).isComplete()) {
                    explosions.get(i3).dispose();
                    explosions.removeIndex(i3);
                }
            }
            for (int i4 = 0; i4 < bullets.size; i4++) {
                if (explosionQueue.get(i4)) {
                    ParticleEffect explosionEffect = new ParticleEffect();
                    if (types.get(i4)) {
                        explosionEffect.load(Gdx.files.internal("particles/explosion3_4.p"), Gdx.files.internal("particles"));
                    } else {
                        explosionEffect.load(Gdx.files.internal("particles/" + bulletExplosionEffect + ".p"), Gdx.files.internal("particles"));
                    }
                    explosionEffect.setPosition(bullets.get(i4).x + bullets.get(i4).width / 2, bullets.get(i4).y + bullets.get(i4).height / 2);
                    explosionEffect.start();
                    explosions.add(explosionEffect);
                    explosionQueue.removeIndex(i4);
                    bullets.removeIndex(i4);
                    degrees.removeIndex(i4);
                    damages.removeIndex(i4);
                    remove_Bullet.removeIndex(i4);
                    types.removeIndex(i4);
                    if (hasTrail) {
                        disposedTrails.add(trails.get(i4));
                        trailCountDownTimers.add(bulletTrailTimer);
                        trails.removeIndex(i4);
                    }
                    if(isHoming){
                        explosionTimers.removeIndex(i4);
                    }
                } else if (remove_Bullet.get(i4)) {
                    explosionQueue.removeIndex(i4);
                    bullets.removeIndex(i4);
                    degrees.removeIndex(i4);
                    damages.removeIndex(i4);
                    remove_Bullet.removeIndex(i4);
                    types.removeIndex(i4);
                    if (hasTrail) {
                        disposedTrails.add(trails.get(i4));
                        trailCountDownTimers.add(bulletTrailTimer);
                        trails.removeIndex(i4);
                    }
                    if(isHoming){
                        explosionTimers.removeIndex(i4);
                    }
                }
            }
            for(int i = 0; i<disposedTrails.size; i++){
                disposedTrails.get(i).draw(batch, delta);
                trailCountDownTimers.set(i, trailCountDownTimers.get(i)-delta);
                if(trailCountDownTimers.get(i)<=0){
                    disposedTrails.get(i).dispose();
                    disposedTrails.removeIndex(i);
                    trailCountDownTimers.removeIndex(i);
                }
            }
            for(int i = 0; i<trails.size; i++){
                trails.get(i).draw(batch, delta);
            }
        } else if (isLaserActive && currentDuration >= 10) {
            laser.setRotation(bounds.getRotation());
            laser.setPosition(bounds.getX() + MathUtils.cosDeg(laser.getRotation()) * 75, bounds.getY() + 16 / MathUtils.cosDeg(laser.getRotation()) + MathUtils.sinDeg(laser.getRotation()) * 80);
            laser.setSize(800, laserHeight);
            laser.setColor(Color.CYAN);
            laser.draw(batch);
            laserSaw.setVolume(soundVolume / 100);
            currentDuration -= delta * 1000;
        } else {
            if (currentDuration < laserDuration) {
                if (SpaceShip.Charge >= powerConsumption * delta * 10) {
                    SpaceShip.Charge -= powerConsumption * delta * 10;
                    currentDuration += delta * 500;
                }
            }
            laser.setSize(0, 0);
            laser.setRotation(0);
            laser.setPosition(-100, -100);
            laserSaw.setVolume(0);
        }

        millis = millis + 50 * (GameLogic.bonuses_collected / 50.0f + 1) * delta * gunCount;

    }

    public void dispose() {
        if (isLaser) {
            laserSaw.dispose();
        } else {
            shot.dispose();
        }
        bullets.clear();
        damages.clear();
        degrees.clear();
        for (int i3 = 0; i3 < explosions.size; i3++) {
            explosions.get(i3).dispose();
            explosions.removeIndex(i3);
        }

        for (int i3 = 0; i3 < trails.size; i3++) {
            trails.get(i3).dispose();
            trails.removeIndex(i3);
        }

        for (int i3 = 0; i3 < disposedTrails.size; i3++) {
            disposedTrails.get(i3).dispose();
            disposedTrails.removeIndex(i3);
        }

        trailCountDownTimers.clear();
        explosionTimers.clear();
        explosionQueue.clear();
        remove_Bullet.clear();
        types.clear();
    }

    public static void removeBullet(int i, boolean explode) {
        if (explode) {
            explosionQueue.set(i, true);
            remove_Bullet.set(i, true);
        } else {
            explosionQueue.set(i, false);
            remove_Bullet.set(i, true);
        }
    }
}
