package com.mario.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mario.game.Main;
import com.mario.game.Scenes.Hud;
import com.mario.game.Screens.PlayScreen;

/**
 * 砖块类
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Main.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        //Mario为Big时才能摧毁砖块
        if (mario.isBig()) {
            setCategoryFilter(Main.DESTROYED_BIT);
            //被摧毁时清除图快
            getCell().setTile(null);
            Hud.addScore(200);
            Main.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else
            Main.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
