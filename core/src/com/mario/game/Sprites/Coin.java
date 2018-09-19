package com.mario.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mario.game.Sprites.Items.ItemDef;
import com.mario.game.Sprites.Items.Mushroom;
import com.mario.game.Main;
import com.mario.game.Scenes.Hud;
import com.mario.game.Screens.PlayScreen;

/**
 * 金币类
 */
public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;//该金币图块ID
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Main.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        //检测被碰撞是否为金币图块ID
        if (getCell().getTile().getId() == BLANK_COIN)
            Main.manager.get("audio/sounds/bump.wav",Sound.class).play();
        else{
            //检测哪个金币有蘑菇
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x
                        ,body.getPosition().x + 2 / Main.PPM), Mushroom.class));
                Main.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }else
                Main.manager.get("audio/sounds/coin.wav", Sound.class).play();
        }
        //金币被摧毁时更换图块
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);
    }
}
