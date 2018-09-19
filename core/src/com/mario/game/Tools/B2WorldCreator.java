package com.mario.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mario.game.Main;
import com.mario.game.Screens.PlayScreen;
import com.mario.game.Sprites.Brick;
import com.mario.game.Sprites.Coin;
import com.mario.game.Sprites.Enemy;
import com.mario.game.Sprites.Goomba;
import com.mario.game.Sprites.Turtle;

/**
 * 加载2d世界
 */
public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        //循环第几个地图图层 往下从0开始
        //地面
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / 100
                    ,(rect.getY() + rect.getHeight() / 2) / 100);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth() / 2 / 100
                    ,rect.getHeight() / 2 / 100);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        //管道
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM
                    ,(rect.getY() + rect.getHeight() / 2) / Main.PPM);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth() / 2 / Main.PPM
                    , rect.getHeight() / 2 / Main.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Main.OBJECT_BIT;
            body.createFixture(fixtureDef);
        }

        //砖块
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen,object);
        }

        //硬币
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen,object);
        }

        //goombas
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            goombas.add(new Goomba(screen,rect.x / Main.PPM,rect.getY() / Main.PPM));
        }

        //turtles
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            turtles.add(new Turtle(screen,rect.x / Main.PPM,rect.getY() / Main.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
    /*public static void removeTurtle(Turtle turtle){
        turtle.removeValue(turtle,true);
    }*/

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
