/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hakavo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.hakavo.ineffable.Engine;
import com.hakavo.ineffable.GameMode;
import com.hakavo.ineffable.Tileset;
import com.hakavo.ineffable.core.GameObject;
import com.hakavo.ineffable.core.Sprite2D;
import com.hakavo.ineffable.core.SpriteRenderer;
import com.hakavo.ineffable.core.Transform;

/**
 *
 * @author Victor
 */
public class TextureVariationsTestGameMode implements GameMode {
    
    Engine engine;
    
    
    @Override
    public void init(Engine engine) {
        this.engine = engine;
        this.engine.camera.setToOrtho(false, 400f, 255f);
        
        Tileset geluVariations = new Tileset(Gdx.files.internal("Scavengers_SpriteSheet.png"), 32);
        
        Sprite2D geluSprite = new Sprite2D();
        
        GameObject gelu = new GameObject();
        gelu.addComponent(new Transform());
        gelu.addComponent(geluSprite);
        gelu.addComponent(new SpriteRenderer(geluSprite, geluVariations.tiles.get(0).createTextureRegion(), 
                                                         geluVariations.tiles.get(6).createTextureRegion(), 
                                                         geluVariations.tiles.get(12).createTextureRegion()));
        
        engine.getLevel().addGameObject(gelu);
    }

    @Override
    public void update(float delta) {
        
    }

    @Override
    public void renderGui(OrthographicCamera camera) {
        
    }
    
}
