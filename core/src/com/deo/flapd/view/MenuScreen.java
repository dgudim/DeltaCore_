package com.deo.flapd.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.deo.flapd.utils.DUtils;
import com.deo.flapd.utils.JsonEntry;
import com.deo.flapd.utils.MusicManager;
import com.deo.flapd.utils.postprocessing.PostProcessor;
import com.deo.flapd.view.dialogues.ConfirmationDialogue;

import static com.deo.flapd.utils.DUtils.addInteger;
import static com.deo.flapd.utils.DUtils.clearPrefs;
import static com.deo.flapd.utils.DUtils.getBoolean;
import static com.deo.flapd.utils.DUtils.getFloat;
import static com.deo.flapd.utils.DUtils.getItemCodeNameByName;
import static com.deo.flapd.utils.DUtils.getString;
import static com.deo.flapd.utils.DUtils.initNewGame;
import static com.deo.flapd.utils.DUtils.loadPrefsFromFile;
import static com.deo.flapd.utils.DUtils.log;
import static com.deo.flapd.utils.DUtils.logException;
import static com.deo.flapd.utils.DUtils.putBoolean;
import static com.deo.flapd.utils.DUtils.putFloat;
import static com.deo.flapd.utils.DUtils.putLong;
import static com.deo.flapd.utils.DUtils.putString;
import static com.deo.flapd.utils.DUtils.savePrefsToFile;
import static com.deo.flapd.utils.DUtils.updateCamera;

public class MenuScreen implements Screen {

    private float number;
    private boolean lamp_animation;

    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Image MenuBg;
    private Texture Bg;
    private Sprite Ship;

    private Animation<TextureRegion> enemyAnimation;
    private float animationPosition;
    private boolean hasAnimation;
    private JsonEntry shipConfigs;

    private Image FillTexture;

    private Image Lamp;

    private BitmapFont font_main;

    private Stage Menu, ShopStage;

    private float movement;

    private InputMultiplexer multiplexer;

    private MusicManager musicManager;

    private Game game;

    private Array<ParticleEffect> fires;

    private boolean easterEgg;

    private int easterEggCounter;

    private int horizontalFillingThreshold;
    private int verticalFillingThreshold;

    private PostProcessor blurProcessor;

    private boolean enableShader;

    private Tree craftingTree;

    private CategoryManager menuCategoryManager, workshopCategoryManager;

    private final Slider musicVolumeS;

    private JsonEntry treeJson;

    private String lastFireEffect;

    private AssetManager assetManager;

    private boolean isConfirmationDialogActive = false;

    private String currentShipTexture;

    public MenuScreen(final Game game, final SpriteBatch batch, final AssetManager assetManager, final PostProcessor blurProcessor) {
        long genTime = TimeUtils.millis();
        log("\n time to generate menu");

        this.game = game;

        this.blurProcessor = blurProcessor;

        this.assetManager = assetManager;

        treeJson = new JsonEntry(new JsonReader().parse(Gdx.files.internal("shop/tree.json")));

        easterEgg = getBoolean("easterEgg");

        this.batch = batch;

        camera = new OrthographicCamera(800, 480);
        viewport = new ScreenViewport(camera);

        MenuBg = new Image((Texture) assetManager.get("menuBg.png"));
        Lamp = new Image((Texture) (assetManager.get("lamp.png")));
        MenuBg.setBounds(0, 0, 800, 480);

        font_main = assetManager.get("fonts/font2(old).fnt");

        Bg = assetManager.get("bg_old.png");
        Bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        FillTexture = new Image((Texture) assetManager.get("menuFill.png"));
        FillTexture.setSize(456, 72);

        currentShipTexture = getString("currentArmour");

        shipConfigs = new JsonEntry(new JsonReader().parse(Gdx.files.internal("player/shipConfigs.json")));

        fires = new Array<>();

        initializeShip();

        Image buildNumber = new Image((Texture) assetManager.get("greyishButton.png"));
        buildNumber.setBounds(5, 5, 150, 50);

        Lamp.setBounds(730, 430, 15, 35);

        UIComposer uiComposer = new UIComposer(assetManager);
        uiComposer.loadStyles("defaultLight", "sliderDefaultNormal", "checkBoxDefault", "gitHub", "trello");

        Table playScreenTable = new Table();
        playScreenTable.align(Align.topLeft);
        playScreenTable.setBounds(15, 60, 531, 410);
        TextButton newGame = uiComposer.addTextButton("defaultLight", "new game", 0.4f);
        TextButton continueGame = uiComposer.addTextButton("defaultLight", "continue", 0.4f);
        TextButton workshop = uiComposer.addTextButton("defaultLight", "workshop", 0.4f);
        final Table difficultyT = uiComposer.addSlider("sliderDefaultNormal", 1, 5, 0.1f, "Difficulty ", "X", "difficulty");
        playScreenTable.add(newGame).padTop(5).padBottom(5).align(Align.left).row();
        playScreenTable.add(continueGame).padTop(5).padBottom(5).align(Align.left).row();
        playScreenTable.add(workshop).padTop(5).padBottom(5).align(Align.left).row();
        playScreenTable.add(difficultyT).padTop(5).padBottom(5).align(Align.left).row();
        final Slider difficultyControl = (Slider) difficultyT.getCells().get(0).getActor();
        difficultyT.getCells().get(1).getActor().setColor(new Color().fromHsv(120 - difficultyControl.getValue() * 20, 1.5f, 1).add(0, 0, 0, 1));

        ScrollPane settingsPane = uiComposer.createScrollGroup(15, 70, 531, 400, false, true);
        settingsPane.setCancelTouchFocus(false);
        Table settingsGroup = (Table) settingsPane.getActor();
        settingsGroup.align(Align.left);
        final Table musicVolumeT, soundVolumeT, uiScaleT, joystickOffsetX, joystickOffsetY;
        final CheckBox bloomT, showFpsT, transparentUIT, prefsLoggingT;
        musicVolumeT = uiComposer.addSlider("sliderDefaultNormal", 0, 100, 1, "[#32ff32]Music volume ", "%", "musicVolume", settingsPane);
        soundVolumeT = uiComposer.addSlider("sliderDefaultNormal", 0, 100, 1, "[#32ff32]Sound volume ", "%", "soundVolume", settingsPane);
        uiScaleT = uiComposer.addSlider("sliderDefaultNormal", 1, 2, 0.25f, "[#32ff32]Ui scale ", "X", "ui", settingsPane);
        bloomT = uiComposer.addCheckBox("checkBoxDefault", "[#32ff32]Bloom", "bloom");
        transparentUIT = uiComposer.addCheckBox("checkBoxDefault", "[#32ff32]Semi-transparent ui", "transparency");
        prefsLoggingT = uiComposer.addCheckBox("checkBoxDefault", "[#32ff32]Prefs logging", "logging");
        showFpsT = uiComposer.addCheckBox("checkBoxDefault", "[#32ff32]Show fps", "showFps");
        joystickOffsetX = uiComposer.addSlider("sliderDefaultNormal", 0, 50, 0.1f, "[#32ff32]joystick x offset ", "px", "joystickOffsetX", settingsPane);
        joystickOffsetY = uiComposer.addSlider("sliderDefaultNormal", 0, 50, 0.1f, "[#32ff32]joystick y offset ", "px", "joystickOffsetY", settingsPane);
        settingsGroup.add(musicVolumeT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(soundVolumeT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(uiScaleT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(bloomT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(prefsLoggingT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(transparentUIT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(showFpsT).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(joystickOffsetX).padTop(5).padBottom(5).align(Align.left).row();
        settingsGroup.add(joystickOffsetY).padTop(5).padBottom(5).align(Align.left).row();

        Table moreTable = new Table();
        moreTable.align(Align.topLeft);
        moreTable.setBounds(15, 62, 531, 410);
        moreTable.add(uiComposer.addLinkButton("gitHub", "[#32ff32]Game source code", "https://github.com/dgudim/DeltaCore_")).padTop(5).padBottom(5).align(Align.left).row();
        moreTable.add(uiComposer.addLinkButton("trello", "[#32ff32]Official trello list of planned features", "https://trello.com/b/FowZ4XAO/delta-core")).padTop(5).padBottom(5).align(Align.left).row();
        final TextButton exportGameData = uiComposer.addTextButton("defaultLight", "export game data", 0.4f);
        TextButton importGameData = uiComposer.addTextButton("defaultLight", "import game data", 0.4f);
        TextButton clearGameData = uiComposer.addTextButton("defaultLight", "clear game data", 0.4f);

        final Label exportMessage = uiComposer.addText("", (BitmapFont) assetManager.get("fonts/font2(old).fnt"), 0.4f);
        exportMessage.setWrap(true);

        exportGameData.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exportMessage.setText("[#FFFF00]saved to " + savePrefsToFile());
            }
        });

        importGameData.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    loadPrefsFromFile();
                    game.setScreen(new LoadingScreen(game, batch, assetManager, blurProcessor));
                } catch (Exception e) {
                    exportMessage.setText("[#FF3300]" + e.getMessage());
                }
            }
        });

        clearGameData.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearPrefs();
                game.setScreen(new LoadingScreen(game, batch, assetManager, blurProcessor));
            }
        });

        moreTable.add(exportGameData).size(310, 50).padTop(5).padBottom(5).align(Align.left).row();
        moreTable.add(importGameData).size(310, 50).padTop(5).padBottom(5).align(Align.left).row();
        moreTable.add(clearGameData).size(310, 50).padTop(5).padBottom(5).align(Align.left).row();
        moreTable.add(exportMessage).padTop(5).padBottom(5).width(510).row();

        musicVolumeS = (Slider) musicVolumeT.getCells().get(0).getActor();

        Menu = new Stage(viewport, batch);

        Menu.addActor(buildNumber);

        ScrollPane infoText = (ScrollPane) uiComposer.addScrollText(
                "[#00ff55]Made by Deoxys\n" +
                        "Textures by DefenceX, VKLowe, Deoxys,\n" +
                        " Max2007\n" +
                        "Music by EvanKing\n" +
                        "[#5DBCD2]Inspired by DefenseX, PetruCHIOrus\n" +
                        "[#cccc22]Testers: Misterowl, Nikita.Beloglazov\n" +
                        "Kisliy_xleb, Watermelon0guy, PYTHØN\n" +
                        "Ha4upelmeney, Lukmanov, ZerOn\n" +
                        "[#0FE500]Contributors: Volkov, DefenseX\n" +
                        "Zsingularityz\n" +
                        "[#CAE500]Deltacore\n" +
                        "® All right reserved",
                font_main, 0.48f, true, false, 5, 100, 531, 410);

        craftingTree = LoadingScreen.craftingTree;
        craftingTree.hide();
        craftingTree.update();

        final ItemSlotManager blackMarket = new ItemSlotManager(assetManager);
        blackMarket.addShopSlots();
        blackMarket.setBounds(105, 70, 425, 400);

        final ItemSlotManager inventory = new ItemSlotManager(assetManager);
        inventory.addInventorySlots();
        inventory.setBounds(105, 70, 425, 400);

        workshopCategoryManager = new CategoryManager(assetManager, 90, 40, 2.5f, 0.25f, "defaultLight", "infoBg2", "treeBg", false, "lastClickedWorkshopButton");
        workshopCategoryManager.addCategory(craftingTree.treeScrollView, "crafting").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoadingScreen.craftingTree.update();
            }
        });
        workshopCategoryManager.addCategory(blackMarket.holderGroup, "market").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackMarket.update();
            }
        });
        workshopCategoryManager.addCategory(inventory.holderGroup, "inventory", 0.23f).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventory.update();
            }
        });
        workshopCategoryManager.addCloseButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateFire();
            }
        });
        workshopCategoryManager.setBounds(13, 67, 400);
        workshopCategoryManager.setBackgroundBounds(5, 65, 531, 410);
        workshopCategoryManager.setTableBackgroundBounds(10, 70, 95, 400);
        workshopCategoryManager.setVisible(false);

        menuCategoryManager = new CategoryManager(assetManager, 250, 75, 2.5f, 0.5f, "defaultDark", "infoBg", "", true, "lastClickedMenuButton");
        menuCategoryManager.addCategory(playScreenTable, "play").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateFire();
                updateShip();
            }
        });
        menuCategoryManager.addCategory(playScreenTable, "online");
        menuCategoryManager.addCategory(settingsPane, "settings");
        menuCategoryManager.addCategory(infoText, "info");
        menuCategoryManager.addCategory(moreTable, "more");
        menuCategoryManager.setBounds(545, 3, 400);
        menuCategoryManager.setBackgroundBounds(5, 65, 531, 410);
        menuCategoryManager.addOverrideActor(workshopCategoryManager);

        menuCategoryManager.attach(Menu);
        Menu.addActor(infoText);
        Menu.addActor(playScreenTable);
        Menu.addActor(moreTable);
        Menu.addActor(settingsPane);
        workshopCategoryManager.attach(Menu);
        infoText.setVisible(false);
        moreTable.setVisible(false);
        playScreenTable.setVisible(false);
        settingsPane.setVisible(false);

        craftingTree.attach(Menu);
        blackMarket.attach(Menu);
        inventory.attach(Menu);

        ShopStage = new Stage(viewport, batch);

        lastFireEffect = " ";
        updateFire();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(Menu);
        multiplexer.addProcessor(ShopStage);

        difficultyControl.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                difficultyT.getCells().get(1).getActor().setColor(new Color().fromHsv(120 - difficultyControl.getValue() * 20, 1.5f, 1).add(0, 0, 0, 1));
            }
        });

        newGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ConfirmationDialogue(assetManager, Menu, "Are you sure you want to start a new game? (you will loose current checkpoint)", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        initNewGame();
                        game.setScreen(new GameScreen(game, batch, assetManager, blurProcessor, true));
                    }
                });
            }
        });

        continueGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getFloat("Health") > 0) {
                    game.setScreen(new GameScreen(game, batch, assetManager, blurProcessor, false));
                }
            }
        });

        workshop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                workshopCategoryManager.setVisible(!workshopCategoryManager.isVisible());
            }
        });

        bloomT.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enableShader = bloomT.isChecked();
                putBoolean("bloom", bloomT.isChecked());
            }
        });

        prefsLoggingT.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DUtils.logging = prefsLoggingT.isChecked();
                putBoolean("logging", prefsLoggingT.isChecked());
            }
        });

        buildNumber.addListener(new ActorGestureListener(20, 0.4f, 60, 0.15f) {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                easterEggCounter++;
                if (easterEggCounter > 10) {
                    easterEgg = !easterEgg;
                    putBoolean("easterEgg", easterEgg);
                    easterEggCounter = 0;
                }
            }

            @Override
            public boolean longPress(Actor actor, float x, float y) {
                addInteger("money", 100000);
                addInteger("cogs", 100000);
                putLong("lastGenTime", 0);
                return true;
            }
        });

        musicManager = new MusicManager("music/ambient", 1, 5, 5);
        musicManager.setVolume(getFloat("musicVolume") / 100f);
        musicVolumeS.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                putFloat("musicVolume", musicVolumeS.getValue());
                musicManager.setVolume(musicVolumeS.getValue() / 100f);
            }
        });

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        enableShader = getBoolean("bloom");

        log("\n done, elapsed time " + TimeUtils.timeSinceMillis(genTime) + "ms");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            if (!isConfirmationDialogActive) {
                new ConfirmationDialogue(assetManager, Menu, "Are you sure you want to quit?", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                }, new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        isConfirmationDialogActive = false;
                    }
                });
            }
            isConfirmationDialogActive = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        if (enableShader) {
            blurProcessor.capture();
        }

        batch.begin();

        movement += (200 * delta);
        if (movement > 2880) {
            movement = 0;
        }

        batch.draw(Bg, 0, 0, (int) movement, -240, 800, 720);

        for (int i = 0; i < fires.size; i++) {
            fires.get(i).draw(batch, delta);
        }

        if (enableShader) {
            batch.end();
            blurProcessor.render();
            batch.begin();
        }

        if (hasAnimation) {
            Ship.setRegion(enemyAnimation.getKeyFrame(animationPosition));
            animationPosition += delta;
        }
        Ship.draw(batch);

        batch.end();
        ShopStage.draw();
        ShopStage.act(delta);
        batch.begin();

        MenuBg.draw(batch, 1);

        musicManager.update(delta);

        if (lamp_animation) {
            number = number + 0.01f;
        } else {
            number = number - 0.01f;
        }
        if (number >= 1) {
            lamp_animation = false;
        }
        if (number <= 0) {
            lamp_animation = true;
        }
        number = MathUtils.clamp(number, 0, 1);
        Lamp.setColor(1, 1, 1, number);
        Lamp.draw(batch, 1);

        batch.end();

        Menu.draw();
        Menu.act(delta);

        batch.begin();
        font_main.getData().setScale(0.35f);
        font_main.setColor(Color.GOLD);
        font_main.draw(batch, "V 0.0.8", 5, 35, 150, 1, false);
        if (easterEgg) {
            font_main.getData().setScale(0.2f);
            font_main.setColor(Color.ORANGE);
            font_main.draw(batch, "cat edition", 5, 20, 150, 1, false);
        }

        for (int i = 0; i < verticalFillingThreshold; i++) {
            FillTexture.setPosition(0, -72 * (i + 1));
            FillTexture.draw(batch, 1);
            FillTexture.setPosition(456, -72 * (i + 1));
            FillTexture.draw(batch, 1);
            FillTexture.setPosition(0, 408 + 72 * (i + 1));
            FillTexture.draw(batch, 1);
            FillTexture.setPosition(456, 408 + 72 * (i + 1));
            FillTexture.draw(batch, 1);
        }

        for (int i = 0; i < horizontalFillingThreshold; i++) {
            for (int i2 = 0; i2 < 7; i2++) {
                FillTexture.setPosition(-456 - 456 * i, 408 - i2 * 72);
                FillTexture.draw(batch, 1);
                FillTexture.setPosition(800 + 456 * i, 408 - i2 * 72);
                FillTexture.draw(batch, 1);
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        updateCamera(camera, viewport, width, height);
        float targetHeight = viewport.getScreenHeight();
        float targetWidth = viewport.getScreenWidth();

        float sourceHeight = 480.0f;
        float sourceWidth = 800.0f;

        float targetRatio = targetHeight / targetWidth;
        float sourceRatio = sourceHeight / sourceWidth;
        float scale;
        if (targetRatio > sourceRatio) {
            scale = targetWidth / sourceWidth;
        } else {
            scale = targetHeight / sourceHeight;
        }

        int actualWidth = (int) (sourceWidth * scale);
        int actualHeight = (int) (sourceHeight * scale);

        verticalFillingThreshold = (int) Math.ceil((targetHeight - actualHeight) / 144);
        horizontalFillingThreshold = (int) Math.ceil((targetWidth - actualWidth) / 912);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        blurProcessor.rebind();
    }

    @Override
    public void hide() {
        game.getScreen().dispose();
    }

    @Override
    public void dispose() {
        musicManager.dispose();
        Menu.dispose();
        ShopStage.dispose();
        for (int i = 0; i < fires.size; i++) {
            fires.get(i).dispose();
        }
        fires.clear();
    }

    private void loadFire() {
        JsonEntry shipConfig = shipConfigs.get(getString("currentArmour"));

        int fireCount = shipConfig.getInt("fireCount");

        for (int i = 0; i < fires.size; i++) {
            fires.get(i).dispose();
        }
        fires.clear();

        for (int i = 0; i < fireCount; i++) {
            ParticleEffect fire = new ParticleEffect();
            fire.load(Gdx.files.internal("particles/" + treeJson.getString(getString("currentEngine"), "usesEffect") + ".p"), Gdx.files.internal("particles"));
            fire.setPosition(Ship.getX() + shipConfig.getFloat("fires", "fire" + i + "OffsetX"), Ship.getY() + shipConfig.getFloat("fires", "fire" + i + "OffsetY"));
            fire.start();
            fires.add(fire);
        }
        lastFireEffect = treeJson.getString(getString("currentEngine"), "usesEffect");
    }

    private void updateFire() {
        try {
            if (lastFireEffect.equals(" ")) {
                loadFire();
            }
            if (!lastFireEffect.equals(treeJson.getString(getString("currentEngine"), "usesEffect"))) {
                loadFire();
            }
            String key = treeJson.getString(getString("currentEngine"), "usesEffect") + "_color";
            if (getString(key).equals("")) {
                setFireToDefault(key);
            } else {
                float[] colors = new JsonReader().parse("{\"colors\":" + getString(key) + "}").get("colors").asFloatArray();
                for (int i = 0; i < fires.size; i++) {
                    fires.get(i).getEmitters().get(0).getTint().setColors(colors);
                }
            }
        } catch (Exception e) {
            log("\ncorrupted fire color array");
            logException(e);
            setFireToDefault(treeJson.getString(getString("currentEngine"), "usesEffect") + "_color");
        }
    }

    private void updateShip() {
        if (!currentShipTexture.equals(getString("currentArmour"))) {
            initializeShip();
            lastFireEffect = " ";
            updateFire();
        }
    }

    private void initializeShip() {
        JsonEntry shipConfig = shipConfigs.get(getString("currentArmour"));
        hasAnimation = shipConfig.getBoolean("hasAnimation");

        if (!hasAnimation) {
            Ship = new Sprite(assetManager.get("items/items.atlas", TextureAtlas.class).findRegion(getItemCodeNameByName(getString("currentArmour"))));
        } else {
            Ship = new Sprite();
            enemyAnimation = new Animation<TextureRegion>(shipConfig.getFloat("frameDuration"), assetManager.get("player/animations/" + shipConfig.getString("animation") + ".atlas", TextureAtlas.class).findRegions(shipConfig.getString("animation")), Animation.PlayMode.LOOP);
        }

        float width = shipConfig.getFloat("width");
        float height = shipConfig.getFloat("height");

        Ship.setBounds(258 - width / 2, 279 - height / 2, width, height);
    }

    private void setFireToDefault(String key) {
        float[] colors = fires.get(0).getEmitters().get(0).getTint().getColors();

        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        buffer.append(colors[0]);
        for (int i = 1; i < colors.length; i++) {
            buffer.append(", ");
            buffer.append(colors[i]);
        }
        buffer.append("]");
        putString(key, buffer.toString());
    }

}