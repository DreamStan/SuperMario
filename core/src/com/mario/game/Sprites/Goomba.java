package com.mario.game.Sprites;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mario.game.Main;
import com.mario.game.Screens.PlayScreen;

/**
 * Goomba类
 */
public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;//Goomba动画
    private Array<TextureRegion> frames;//剪切Goomba动画
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        //Goomba动画只有3帧 所以循环三次
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"),
                    i * 16,0,16,16));
        }
        //每隔0.4s播放Goomba动画
        walkAnimation = new Animation(0.4f,frames);
        stateTime = 0;
        setBounds(getX(),getY(),16 / Main.PPM,16 / Main.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt){
        stateTime += dt;
        //Goomba被摧毁
        if (setToDestroy && ! destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba")
                    ,32,0,16,16));
            stateTime = 0;
        } else if (!destroyed){//Goomba正常
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2
                    ,b2body.getPosition().y -getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime,true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX()+0.3f,getY()+0.3f);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Main.PPM);
        fdef.filter.categoryBits = Main.ENEMY_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT |
                Main.COIN_BIT |
                Main.BRICK_BIT |
                Main.ENEMY_BIT |
                Main.OBJECT_BIT |
                Main.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        /***设置刚体形状*/
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5,8).scl(1 / Main.PPM);
        vertice[1] = new Vector2(5,8).scl(1 / Main.PPM);
        vertice[2] = new Vector2(-3,3).scl(1 / Main.PPM);
        vertice[3] = new Vector2(3,3).scl(1 / Main.PPM);
        head.set(vertice);
        /****/

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = Main.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if (!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        Main.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        //当Turtle为MOVING_SHELL状态将Goomba摧毁
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
            setToDestroy = true;
        else
            reverseVelocity(true,false );
    }
}
