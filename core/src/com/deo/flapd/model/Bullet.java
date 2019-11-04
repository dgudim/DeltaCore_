package com.deo.flapd.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.deo.flapd.model.enemies.EnemyBullet;
import com.deo.flapd.view.GameUi;
import com.deo.flapd.view.MenuScreen;

import java.util.Random;

public class Bullet {

    private Polygon bounds;
    public static Array<Rectangle> bullets;
    public static Array<Float> damages;
    public static Array<Float> degrees;
    private static Array <ParticleEffect> explosions;
    private Sprite bullet;

    private Sound shot;

    private boolean sound;

    public static int bulletsShot;

    private Random random;

    private float spread;

    public Bullet(Texture bulletTexture, float spread, Polygon shipBounds) {
        bounds = shipBounds;
        this.spread = spread;
        bullet = new Sprite(bulletTexture);

        random = new Random();

        bullets = new Array<>();
        damages = new Array<>();
        degrees = new Array<>();
        explosions = new Array<>();

        bulletsShot = 0;

        sound = MenuScreen.Sound;
        shot = Gdx.audio.newSound(Gdx.files.internal("music/gun4.ogg"));
    }

    public void Spawn(float damage, float scale) {

            Rectangle bullet = new Rectangle();

            bullet.x = bounds.getX() + 68;
            bullet.y = bounds.getY() + 10 - 10*(scale-1);

            bullet.setSize(75*scale, 20*scale);

            bullets.add(bullet);
            damages.add(damage);

            degrees.add((random.nextFloat()-0.5f)*spread+bounds.getRotation()/20);

            bulletsShot++;

            if(sound) {
                shot.play(MenuScreen.SoundVolume/100);
            }

    }

    public void draw(SpriteBatch batch, boolean is_paused) {

        for (int i = 0; i < bullets.size; i ++) {

            Rectangle bullet = bullets.get(i);
            float angle = degrees.get(i);

            this.bullet.setPosition(bullet.x, bullet.y);
            this.bullet.setSize(bullet.width, bullet.height);
            this.bullet.setOrigin(bullet.width / 2f, bullet.height / 2f);
            this.bullet.setRotation(MathUtils.radiansToDegrees*MathUtils.atan2(300*angle, 1500));
            this.bullet.draw(batch);

            if (!is_paused){
                bullet.x += 1500 * Gdx.graphics.getDeltaTime();
                bullet.y += 300 * angle * Gdx.graphics.getDeltaTime();

                if(bullet.x > 800){
                    removeBullet(i, false);
                }
            }
        }
        for(int i3 = 0; i3 < explosions.size; i3 ++){
            explosions.get(i3).draw(batch);
            if(!is_paused) {
                explosions.get(i3).update(Gdx.graphics.getDeltaTime());
            }else{
                explosions.get(i3).update(0);
            }
            if(explosions.get(i3).isComplete()){
                explosions.get(i3).dispose();
                explosions.removeIndex(i3);
            }
        }
    }

    public void dispose(){
        shot.dispose();
        for(int i3 = 0; i3 < bullets.size; i3 ++){
            bullets.removeIndex(i3);
            damages.removeIndex(i3);
            degrees.removeIndex(i3);
        }
        for(int i3 = 0; i3 < explosions.size; i3 ++){
            explosions.get(i3).dispose();
            explosions.removeIndex(i3);
        }
    }

    public static void removeBullet(int i, boolean explode){
        if(explode){
            ParticleEffect explosionEffect = new ParticleEffect();
            explosionEffect.load(Gdx.files.internal("particles/explosion3_2.p"), Gdx.files.internal("particles"));
            explosionEffect.setPosition(bullets.get(i).x + bullets.get(i).width, bullets.get(i).y + bullets.get(i).height/2);
            explosionEffect.start();
            explosions.add(explosionEffect);
        }
        bullets.removeIndex(i);
        damages.removeIndex(i);
        degrees.removeIndex(i);
    }

}