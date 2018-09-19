package com.mario.game.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mario.game.Sprites.Items.Item;
import com.mario.game.Sprites.Items.ItemDef;
import com.mario.game.Sprites.Items.Mushroom;
import com.mario.game.Main;
import com.mario.game.Scenes.Hud;
import com.mario.game.Sprites.Enemy;
import com.mario.game.Sprites.Mario;
import com.mario.game.Tools.B2WorldCreator;
import com.mario.game.Tools.Controller;
import com.mario.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 主游戏窗口
 */
public class PlayScreen implements Screen {

    private Main game;
    private TextureAtlas atlas;//保存一个纹理集

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;//加载TMX地图
    private TiledMap map;//创建一个地图
    private OrthogonalTiledMapRenderer renderer;//渲染地图

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;//用于保存多个蘑菇
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;//产生蘑菇

    Controller controller;

    /**
     * 初始化游戏资源
     * @param game
     */
    public PlayScreen(Main game){

        //初始化纹理集
        atlas = new TextureAtlas(Gdx.files.internal("Mario_and_Enemies.pack"));

        this.game = game;
        gameCam = new OrthographicCamera();
        //初始化游戏视口
        gamePort = new FitViewport(Main.V_WIDTH / Main.PPM
                ,Main.V_HEIGHT / Main.PPM,gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        //初始化地图
        map = mapLoader.load("mario_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1 / Main.PPM);

        gameCam.position.set(gamePort.getWorldWidth() / 2
                ,gamePort.getWorldHeight() / 2,0);

        //Vector2(引力,重力，是否为活动体)
        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        controller = new Controller(game.batch);

        world.setContactListener(new WorldContactListener());

        //初始化BGM
        music = Main.manager.get("audio/music/mario_music.ogg",Music.class);
        //是否循环播放
        music.setLooping(true);
        //音量
        music.setVolume(0.3f);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    /**
     * 产生蘑菇
     * @param idef
     */
    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    /**
     * 添加多个蘑菇
     */
    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class){
                items.add(new Mushroom(this,idef.position.x,idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        if (player.currentState != Mario.State.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.jump();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&
                    player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f,0)
                        ,player.b2body.getWorldCenter(),true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                    player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f,0)
                        ,player.b2body.getWorldCenter(),true);
        }

        /*if (player.currentState != Mario.State.DEAD){
            if (controller.isUpPress())
                player.jump();
            if (controller.isLeftPress() && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2body.getWorldCenter(),true);
            if (controller.isRightPress() && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f,0),player.b2body.getWorldCenter(),true);
        }*/
    }



    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f,6,2);

        player.update(dt);

        /*** 将所有敌人激活*/
        for (Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
//            if (enemy.getX() < player.getX() + 224 / Main.PPM)
            enemy.b2body.setActive(true);
        }

        for (Enemy enemy : creator.getEnemies()){
            enemy.update(dt);
            enemy.b2body.setActive(true);
        }
        /****/

        //检测所有Item是否有碰撞
        for (Item item : items)
            item.update(dt);

        hud.update(dt);

        //当Mario没有死亡相机跟随
        if (player.currentState != Mario.State.DEAD)
            gameCam.position.x = player.b2body.getPosition().x;

        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        game.batch.setProjectionMatrix(controller.stage.getCamera().combined);
        hud.stage.draw();
        if (Gdx.app.getType() == Application.ApplicationType.Android)
            controller.stage.draw();

        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    /**
     * 检测Mario是否为死亡状态
     * @return
     */
    public boolean gameOver(){
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
        controller.resize(width,height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        controller.dispose();
    }
}
