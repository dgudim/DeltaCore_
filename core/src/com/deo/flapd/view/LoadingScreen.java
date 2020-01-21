package com.deo.flapd.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.deo.flapd.utils.DUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LoadingScreen implements Screen {

    private AssetManager assetManager;
    private SpriteBatch batch;
    private BitmapFont main;
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private int screen_type;
    private boolean newGame;
    private boolean is_first_launch;
    private Texture bg_loading, bg_loadingBase;
    private ProgressBar loadingBar;
    private ProgressBar.ProgressBarStyle loadingBarStyle;
    private Group group;
    private Preferences prefs;

    public LoadingScreen(Game game, SpriteBatch batch, AssetManager assetManager, int screen_type, boolean newGame, boolean is_first_launch){

        this.batch = batch;

        main = new BitmapFont(Gdx.files.internal("fonts/font2.fnt"), false);

        this.assetManager = assetManager;

        this.game = game;

        this.screen_type = screen_type;

        prefs = Gdx.app.getPreferences("Preferences");

        load(screen_type);
        if(prefs.getBoolean("fastLoading") && is_first_launch){
            load(1);
        }

        bg_loading = new Texture("loadingScreen.png");
        bg_loadingBase = new Texture("loadingScreenBase.png");

        camera = new OrthographicCamera(800, 480);
        viewport = new FitViewport(800, 480, camera);
        viewport.apply();

        this.newGame = newGame;

        this.is_first_launch = is_first_launch;

        loadingBarStyle = new ProgressBar.ProgressBarStyle();

        Pixmap pixmap4 = new Pixmap(0, 12, Pixmap.Format.RGBA8888);
        pixmap4.setColor(Color.valueOf("1ABDA5"));
        pixmap4.fill();
        TextureRegionDrawable BarForeground = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap4)));
        pixmap4.dispose();

        Pixmap pixmap5 = new Pixmap(100, 12, Pixmap.Format.RGBA8888);
        pixmap5.setColor(Color.valueOf("1ABDA5"));
        pixmap5.fill();
        TextureRegionDrawable BarForeground2 = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap5)));
        pixmap5.dispose();

        loadingBarStyle.knob = BarForeground;
        loadingBarStyle.knobBefore = BarForeground2;

        loadingBar = new ProgressBar(0, 100, 0.01f, false, loadingBarStyle);
        loadingBar.setSize(100, 12);
        loadingBar.setPosition(0, 0);
        loadingBar.setAnimateDuration(0.25f);
        group = new Group();
        group.addActor(loadingBar);
        group.setRotation(-16);
        group.setPosition(512, 347);
    }

    @Override
    public void show() {

    }

    public void load(int screen_type){
        switch (screen_type){
            case(1):
                assetManager.load("bg_layer1.png", Texture.class);
                assetManager.load("bg_layer2.png", Texture.class);
                assetManager.load("bg_layer3.png", Texture.class);
                assetManager.load("ship.png", Texture.class);
                assetManager.load("ColdShield.png", Texture.class);
                assetManager.load("HotShield.png", Texture.class);
                assetManager.load("pew3.png", Texture.class);
                assetManager.load("pew.png", Texture.class);
                assetManager.load("trainingbot.png", Texture.class);
                assetManager.load("enemy_shotgun.png", Texture.class);
                assetManager.load("enemy_sniper.png", Texture.class);
                assetManager.load("pew2.png", Texture.class);
                assetManager.load("Meteo.png", Texture.class);
                assetManager.load("atomic_bomb.png", Texture.class);

                assetManager.load("bonus_bullets.png", Texture.class);
                assetManager.load("bonus_health.png", Texture.class);
                assetManager.load("bonus_part.png", Texture.class);
                assetManager.load("bonus_shield.png", Texture.class);
                assetManager.load("bonus_boss.png", Texture.class);

                assetManager.load("boss_ship/boss.png", Texture.class);
                assetManager.load("boss_ship/boss_dead.png", Texture.class);
                assetManager.load("boss_ship/bullet_blue.png", Texture.class);
                assetManager.load("boss_ship/bullet_red.png", Texture.class);
                assetManager.load("boss_ship/bullet_red_thick.png", Texture.class);
                assetManager.load("boss_ship/cannon1.png", Texture.class);
                assetManager.load("boss_ship/cannon2.png", Texture.class);
                assetManager.load("boss_ship/upperCannon_part1.png", Texture.class);
                assetManager.load("boss_ship/upperCannon_part2.png", Texture.class);
                assetManager.load("boss_ship/bigCannon.png", Texture.class);

                assetManager.load("uraniumCell.png", Texture.class);

                assetManager.load("firebutton.png", Texture.class);
                assetManager.load("weaponbutton.png", Texture.class);
                assetManager.load("pause.png", Texture.class);
                assetManager.load("level score indicator.png", Texture.class);
                assetManager.load("health indicator.png", Texture.class);
                assetManager.load("money_display.png", Texture.class);
                assetManager.load("exit.png", Texture.class);
                assetManager.load("resume.png", Texture.class);
                assetManager.load("restart.png", Texture.class);

                assetManager.load("checkpoint.png", Texture.class);
                assetManager.load("checkpoint_green.png", Texture.class);

                assetManager.load("bu1.png", Texture.class);
                assetManager.load("bu2.png", Texture.class);
                assetManager.load("bu3.png", Texture.class);

                assetManager.load("cat.png", Texture.class);
                assetManager.load("cat2.png", Texture.class);
                assetManager.load("cat_meteorite.png", Texture.class);
                assetManager.load("cat_bomb.png", Texture.class);
                assetManager.load("whiskas.png", Texture.class);
                assetManager.load("laser.png", Texture.class);

                assetManager.load("boss_evil/evil_cannon.png", Texture.class);
                assetManager.load("boss_evil/evil_center.png", Texture.class);
                assetManager.load("boss_evil/evil_down_left.png", Texture.class);
                assetManager.load("boss_evil/evil_down_right.png", Texture.class);
                assetManager.load("boss_evil/evil_up_left.png", Texture.class);
                assetManager.load("boss_evil/evil_up_right.png", Texture.class);
                assetManager.load("boss_evil/evil_left.png", Texture.class);
                assetManager.load("boss_evil/evil_right.png", Texture.class);
                assetManager.load("boss_evil/evil_down.png", Texture.class);
                assetManager.load("boss_evil/evil_up.png", Texture.class);
                assetManager.load("boss_evil/evil_base.png", Texture.class);

                assetManager.load("items/bonus_warp.png", Texture.class);
                assetManager.load("items/bonus_warp2.png", Texture.class);
                assetManager.load("items/bonus_laser.png", Texture.class);
                assetManager.load("items/Circuit_Board.png", Texture.class);
                assetManager.load("items/crystal.png", Texture.class);
                assetManager.load("items/redCrystal.png", Texture.class);
                assetManager.load("items/core_yellow.png", Texture.class);
                assetManager.load("items/ore.png", Texture.class);
                assetManager.load("items/warp_ore.png", Texture.class);
                assetManager.load("items/warp_core.png", Texture.class);
                assetManager.load("items/cable.png", Texture.class);
                assetManager.load("items/orangeCrystal.png", Texture.class);
                assetManager.load("items/cyanCrystal.png", Texture.class);
                assetManager.load("items/purpleCrystal.png", Texture.class);
                assetManager.load("items/greenCrystal.png", Texture.class);
                assetManager.load("items/advancedChip.png", Texture.class);
                assetManager.load("items/craftingCard.png", Texture.class);
                assetManager.load("items/aiCard.png", Texture.class);
                assetManager.load("items/memoryCard.png", Texture.class);
                assetManager.load("items/card1.png", Texture.class);
                assetManager.load("items/card2.png", Texture.class);
                assetManager.load("items/energyCell.png", Texture.class);
                assetManager.load("items/fuelCell.png", Texture.class);
                assetManager.load("items/fuelCell2.png", Texture.class);
                assetManager.load("items/ironPlate.png", Texture.class);
                assetManager.load("items/ironPlate2.png", Texture.class);
                assetManager.load("items/cell1.png", Texture.class);
                assetManager.load("items/cell2.png", Texture.class);
                assetManager.load("items/screenCard.png", Texture.class);
                assetManager.load("items/processor1.png", Texture.class);
                assetManager.load("items/processor2.png", Texture.class);
                assetManager.load("items/processor3.png", Texture.class);
                assetManager.load("items/energyCrystal.png", Texture.class);
                assetManager.load("items/glassShard.png", Texture.class);
                assetManager.load("items/aiChip.png", Texture.class);
                assetManager.load("items/coolingUnit.png", Texture.class);
                assetManager.load("items/prism.png", Texture.class);
                assetManager.load("items/gun.png", Texture.class);
                assetManager.load("items/bolt.png", Texture.class);
                break;
            case(2):
                assetManager.load("greyishButton.png", Texture.class);
                assetManager.load("menuButtons/info_enabled.png", Texture.class);
                assetManager.load("menuButtons/info_disabled.png", Texture.class);
                assetManager.load("menuButtons/more_enabled.png", Texture.class);
                assetManager.load("menuButtons/more_disabled.png", Texture.class);
                assetManager.load("menuButtons/play_enabled.png", Texture.class);
                assetManager.load("menuButtons/play_disabled.png", Texture.class);
                assetManager.load("menuButtons/settings_enabled.png", Texture.class);
                assetManager.load("menuButtons/settings_disabled.png", Texture.class);
                assetManager.load("menuButtons/online_enabled.png", Texture.class);
                assetManager.load("menuButtons/online_disabled.png", Texture.class);

                assetManager.load("menuButtons/continue_e.png", Texture.class);
                assetManager.load("menuButtons/continue_d.png", Texture.class);
                assetManager.load("menuButtons/newGame_d.png", Texture.class);
                assetManager.load("menuButtons/shop_d.png", Texture.class);
                assetManager.load("menuButtons/newGame_e.png", Texture.class);
                assetManager.load("menuButtons/shop_e.png", Texture.class);

                assetManager.load("menuBg.png", Texture.class);
                assetManager.load("lamp.png", Texture.class);
                assetManager.load("infoBg.png", Texture.class);
                assetManager.load("bg_old.png", Texture.class);
                assetManager.load("ship.png", Texture.class);

                assetManager.load("checkBox_disabled.png", Texture.class);
                assetManager.load("checkBox_enabled.png", Texture.class);
                assetManager.load("progressBarKnob.png", Texture.class);
                assetManager.load("progressBarBg.png", Texture.class);

                assetManager.load("shop/main.png", Texture.class);
                assetManager.load("shop/button_small.png", Texture.class);
                assetManager.load("shop/button_small_enabled.png", Texture.class);
                assetManager.load("shop/button_tiny.png", Texture.class);
                assetManager.load("shop/button_tiny_enabled.png", Texture.class);

                assetManager.load("shop/engine1.png", Texture.class);
                assetManager.load("shop/engine2.png", Texture.class);
                assetManager.load("shop/engine3.png", Texture.class);

                assetManager.load("shop/menuBuy.png", Texture.class);
                assetManager.load("shop/menuBuy2.png", Texture.class);

                assetManager.load("shop/yes.png", Texture.class);
                assetManager.load("shop/yesDisabled.png", Texture.class);
                assetManager.load("shop/no.png", Texture.class);
                assetManager.load("shop/noDisabled.png", Texture.class);
                assetManager.load("shop/upgrade.png", Texture.class);
                assetManager.load("shop/upgradeDisabled.png", Texture.class);

                assetManager.load("shop/CategoryGun.png", Texture.class);
                assetManager.load("shop/CategoryGun2.png", Texture.class);
                assetManager.load("shop/CategoryEngine.png", Texture.class);
                assetManager.load("shop/Cannon1.png", Texture.class);
                assetManager.load("shop/Cannon2.png", Texture.class);
                assetManager.load("shop/Cannon3.png", Texture.class);
                assetManager.load("bonus_part.png", Texture.class);

                assetManager.load("trello.png", Texture.class);
                assetManager.load("gitHub.png", Texture.class);
                break;
        }
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(is_first_launch) {
            batch.setProjectionMatrix(camera.combined);
        }

        batch.begin();

        batch.draw(bg_loadingBase, 0 , 0, 800, 480);

        main.getData().setScale(0.05f);
        main.setColor(Color.valueOf("00db00"));
        main.draw(batch, assetManager.getDiagnostics() + (int)(assetManager.getProgress()*100) + "%"  , 95, 320, 20, 1,false);
        batch.draw(bg_loading, 0 , 0, 800, 480);
        loadingBar.setValue(assetManager.getProgress()*100);
        group.draw(batch, 1);
        group.act(delta);
        assetManager.update();
        batch.end();

        try {
            if (assetManager.isFinished()) {
                switch (screen_type) {
                    case (1):
                        game.setScreen(new GameScreen(game, batch, assetManager, newGame));
                        break;
                    case (2):
                        game.setScreen(new MenuScreen(game, batch, assetManager));
                        break;
                }
            }
        }catch (ClassCastException | NumberFormatException e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String fullStackTrace = sw.toString();
            DUtils.log("\n"+fullStackTrace + "\n");
            DUtils.log("\n wiping data :) \n");
            prefs.clear();
            prefs.putInteger("money", 7000);
            prefs.putFloat("ui", 1.25f);
            prefs.putFloat("soundEffectsVolume", 1);
            prefs.putFloat("musicVolume", 1 );
            prefs.putFloat("difficulty", 1);
            prefs.putBoolean("transparency", true);
            prefs.putBoolean("shaders", false);
            prefs.flush();
            DUtils.log("...done...restaring");
        } catch (Exception e2) {
            StringWriter sw = new StringWriter();
            e2.printStackTrace(new PrintWriter(sw));
            String fullStackTrace = sw.toString();
            DUtils.log("\n" + fullStackTrace + "\n");
            DUtils.log("force exiting");
            System.exit(1);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        if(is_first_launch) {
            camera.position.set(400, 240, 0);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        game.getScreen().dispose();
    }

    public void dispose(){
        main.dispose();
        bg_loading.dispose();
        bg_loadingBase.dispose();
    }

}
