package com.mario.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mario.game.Main;

/**
 * Mobile Input
 */
public class Controller implements Disposable {
    Viewport viewport;
    public Stage stage;
    boolean upPress,leftPress,rightPress;
    private Texture rightImg;
    private Texture topImg;
    private Texture leftImg;
    private Button.ButtonStyle rightStyle;
    private Button.ButtonStyle topStyle;
    private Button.ButtonStyle leftStyle;
    private Button right;
    private Button top;
    private Button left;

    public Controller(SpriteBatch sb) {
        viewport = new FitViewport(Main.V_WIDTH,Main.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,sb);
        Gdx.input.setInputProcessor(stage);
        rightImg = new Texture(Gdx.files.internal("direction/right.png"));
        rightStyle = new Button.ButtonStyle();
        rightStyle.up = new TextureRegionDrawable(new TextureRegion(rightImg));
        rightStyle.down = new TextureRegionDrawable(new TextureRegion(rightImg));

        topImg = new Texture(Gdx.files.internal("direction/up.png"));
        topStyle = new Button.ButtonStyle();
        topStyle.up = new TextureRegionDrawable(new TextureRegion(topImg));
        topStyle.down = new TextureRegionDrawable(new TextureRegion(topImg));

        leftImg = new Texture(Gdx.files.internal("direction/left.png"));
        leftStyle = new Button.ButtonStyle();
        leftStyle.up = new TextureRegionDrawable(new TextureRegion(leftImg));
        leftStyle.down = new TextureRegionDrawable(new TextureRegion(leftImg));

        Table table = new Table();
        table.left().bottom();

        right = new Button(rightStyle);
        right.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPress = false;
            }
        });

        top = new Button(topStyle);
        top.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPress = false;
            }
        });

        left = new Button(leftStyle);
        left.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPress = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPress = false;
            }
        });

        table.add();
        table.add(top).size(topImg.getWidth()/3,top.getHeight()/3);
        table.add();
        table.row().pad(5,5,5,5);
        table.add(left).size(leftImg.getWidth()/3,leftImg.getHeight()/3);
        table.add();
        table.add(right).size(rightImg.getWidth()/3,rightImg.getHeight()/3);

        stage.addActor(table);
    }

    public void resize(int width,int height){
        viewport.update(width,height);
    }

    public boolean isUpPress() {
        return upPress;
    }

    public void setUpPress(boolean upPress) {
        this.upPress = upPress;
    }

    public boolean isLeftPress() {
        return leftPress;
    }

    public void setLeftPress(boolean leftPress) {
        this.leftPress = leftPress;
    }

    public boolean isRightPress() {
        return rightPress;
    }

    public void setRightPress(boolean rightPress) {
        this.rightPress = rightPress;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
