package com.mario.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mario.game.Screens.PlayScreen;

/**
 * 敌人类
 */
public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen,float x,float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(2,-3);
        b2body.setActive(false);
    }

    /**
     * 定义敌人刚体
     */
    protected abstract void defineEnemy();
    public abstract void update(float dt);

    /**
     * 被Mario碰撞
     * @param mario
     */
    public abstract void hitOnHead(Mario mario);

    /**
     * 被敌人碰撞
     * @param enemy
     */
    public abstract void onEnemyHit(Enemy enemy);

    public void reverseVelocity(boolean x,boolean y){
        if (x)
            velocity.x = - velocity.x;
        if (y)
            velocity.y = - velocity.y;
    }
}
