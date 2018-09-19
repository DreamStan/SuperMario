package com.mario.game.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mario.game.Main;
import com.mario.game.Screens.PlayScreen;
import com.mario.game.Sprites.Items.Mushroom;

/**
 * 马里奥类
 */
public class Mario extends Sprite {

    public enum State {FALLING,JUMPING,STANDING,RUNNING,GROWING,DEAD}//Mario动作
    public State currentState;//当前动作
    public State previousState;//下一动作

    public World world;
    public Body b2body;
    private TextureRegion marioStand;//Mario Idle状态

    private Animation<TextureRegion> marioRun;//Mario跑步
    private TextureRegion marioJump;//Mario跳跃
    private TextureRegion marioDead;//Mario死亡
    private TextureRegion bigMarioStand;//bigMario Idle
    private TextureRegion bigMarioJump;//bigMario跳跃
    private Animation<TextureRegion> bigMarioRun;//bigMario跑步
    private Animation<TextureRegion> growMario;//Mario增大

    private float stateTimer;
    private boolean runningRight;//判断Mario左右面向
    private static boolean marioIsBig;//bigMario状态
    private static boolean runGrowAnimation;//是否正在播放grow动画
    private static boolean timeToDefineBigMario;//是否bigMario
    private boolean timeToRedefineMario;//Mario是否还原
    private boolean marioIsDead;//Mario是否死亡

    public Mario(){}

    public Mario(PlayScreen screen){
        //initialize default values
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //littleMario动画
        //get run animation frames and add them to marioRun Animation
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i * 16,0,16,16));
        }
        marioRun = new Animation<TextureRegion>(0.1f,frames);
        frames.clear();

        //bigMario动画
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i * 16,0,16,32));
        }
        bigMarioRun = new Animation<TextureRegion>(0.1f,frames);
        frames.clear();

        //播放增大动画
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario = new Animation<TextureRegion>(0.2f,frames);

        //little_mario跳跃
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,0,16,16);
        //big_mario跳跃
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0,16,32);

        //little_mario站立
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,16);
        //big_mario站立
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);

        //mario死亡
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"),96,0,16,16);

        //初始化mario刚体
        defineMario();

        setBounds(0,0,16 / Main.PPM,16 / Main.PPM);
        setRegion(marioStand);
    }

    public void update(float dt){
        if (marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2,b2body.getPosition().y - getHeight() / 2 - 6 / Main.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2,b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (timeToDefineBigMario){
            //Mario为big时定义bigMario刚体
            defineBigMario();
        }
        if (timeToRedefineMario)
            //Mario复原
            redefineMario();
    }

    /**
     * 播放状态
     * @param dt
     * @return 返回当前播放状态
     */
    public TextureRegion getFrame(float dt) {
        currentState = getSate();

        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                /**
                 * if(marioIsBig){
                 *     bigMarioJump
                 * }else{
                 *     marioJump
                 * }
                 */
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer,true) : marioRun.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        /**
         * 判断Mario面向
         */
        if ((b2body.getLinearVelocity().x < 0 || !runningRight )
                && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }else if ((b2body.getLinearVelocity().x > 0 || runningRight)
                && region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }
        /****/

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;

    }

    /**
     * 当前状态
     * @return 返回当前Mario状态
     */
    public State getSate() {
        if (marioIsDead)
            return State.DEAD;
        else if (runGrowAnimation){
            setBounds(getX(),getY(),16/Main.PPM,16/Main.PPM * 2);
            return State.GROWING;
        }
        else if (b2body.getLinearVelocity().y > 0 ||
                (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y > 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    /**
     * Mario吃了蘑菇以后增大
     */
    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        Main.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    /**
     * bigMario刚体
     */
    private void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
//        bdef.position.set(32 / Main.PPM,32 / Main.PPM);
        bdef.position.set(currentPosition.add(0,10 / Main.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Main.PPM);
        fdef.filter.categoryBits = Main.MARIO_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT |
                Main.COIN_BIT |
                Main.BRICK_BIT |
                Main.ENEMY_BIT |
                Main.OBJECT_BIT |
                Main.ENEMY_HEAD_BIT |
                Main.ITEM_BIT;

        fdef.shape = shape;
//        b2body.createFixture(fdef);
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0,-13 / Main.PPM));
        b2body.createFixture(fdef).setUserData(this);
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM,6 / Main.PPM),new Vector2(2 / Main.PPM,6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    /**
     * 原始Mario刚体
     */
    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Main.PPM, 32 / Main.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        fdef.filter.categoryBits = Main.MARIO_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT |
                Main.COIN_BIT |
                Main.BRICK_BIT |
                Main.ENEMY_BIT |
                Main.OBJECT_BIT |
                Main.ENEMY_HEAD_BIT |
                Main.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    /**
     * 复原Mario
     */
    private void redefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        fdef.filter.categoryBits = Main.MARIO_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT |
                Main.COIN_BIT |
                Main.BRICK_BIT |
                Main.ENEMY_BIT |
                Main.OBJECT_BIT |
                Main.ENEMY_HEAD_BIT |
                Main.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Main.PPM, 6 / Main.PPM), new Vector2(2 / Main.PPM, 6 / Main.PPM));
        fdef.filter.categoryBits = Main.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    /**
     * 检测Mario是否与敌人发生碰撞
     * @param enemy
     */
    public void hit(Enemy enemy) {
        //当Mario与Turtle发生碰撞时检测Turtle是否为STANDING_SHELL状态
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            //在STANDING_SHELL碰撞时Turtle会触发MOVING_SHELL状态
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), 16 / Main.PPM, 16 / Main.PPM);
                Main.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                currentState = State.DEAD;
                Main.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                Main.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = Main.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public void jump(){
        if (currentState != State.JUMPING){
            b2body.applyLinearImpulse(new Vector2(0,4f),b2body.getWorldCenter(),true);
            currentState = State.JUMPING;
        }
    }

    public boolean isBig(){
        return marioIsBig;
    }

}
