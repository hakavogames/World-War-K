/*
 * Copyright 2018 .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hakavo.game;
import com.hakavo.ineffable.core.collision.*;
import com.hakavo.ineffable.core.*;
import com.hakavo.ineffable.gameobjects.Map;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.hakavo.game.mechanics.*;
import com.hakavo.game.mechanics.DialogueSystem.*;
import com.hakavo.ineffable.*;
import com.hakavo.ineffable.core.*;
import com.hakavo.ineffable.core.GameComponent.Copiable;
import java.util.Comparator;

public class ActualGameMode implements GameMode {
    Map map;
    OrthographicCamera camera;
    static Engine engine;
    Tileset poses;
    
    public ActualGameMode() {
    }
    @Override
    public void init(Engine engine) {
        Tileset tileset=new Tileset(Gdx.files.internal("tileset.xml"));
        this.engine=engine;
        engine.camera=new OrthographicCamera();
        camera=engine.camera;
        camera.setToOrtho(false,400*2,225*2);
        camera.zoom=0.5f;
        camera.position.add(12f,12f,0);
        
        Sprite2D sprite=new Sprite2D();
        
        poses=new Tileset(Gdx.files.internal("Scavengers_SpriteSheet.png"),32);
        AnimationClip clip1=new AnimationClip();
        for(int i=0;i<6;i++)clip1.frames.add(poses.tiles.get(i).createTextureRegion());
        clip1.duration=1f;
        clip1.loop=true;
        
        AnimationClip clip2=new AnimationClip();
        for(int i=46;i<48;i++)clip2.frames.add(poses.tiles.get(i).createTextureRegion());
        clip2.duration=0.6f;
        clip2.loop=true;
        
        Animation idle=new Animation("idle",clip1,sprite);
        Animation fart=new Animation("fart",clip2,sprite);
        AnimationController animationController=new AnimationController(sprite,idle,fart);
        animationController.play("idle");
        
        Joint player=new Joint();
        player.name="player";
        player.addComponent(new Transform(camera.viewportWidth/2,camera.viewportHeight/2));
        player.addComponent(new SpriteRenderer(sprite));
        player.addComponent(animationController);
        player.addComponent(new PlayerController());
        player.getComponent(SpriteRenderer.class).layer=1;
        
        GameObject text=new GameObject();
        text.addComponent(new Transform(0,40,1f,1f).setRelative(player.getComponent(Transform.class)));
        text.addComponent(new TextRenderer("",new Color(1,1,1,1)));
        player.addGameObject(text);
        
        Sprite2D guySprite=new Sprite2D();
        Joint guy=(Joint)player.cpy();
        guy.getComponent(SpriteRenderer.class).sprite=guySprite;
        guy.getComponent(SpriteRenderer.class).layer=0;
        guy.getComponent(AnimationController.class).setTarget(guySprite);
        guy.getComponent(AnimationController.class).play("fart");
        guy.getComponent(Transform.class).matrix.setToTranslation(325,225);
        guy.addComponent(new BoxCollider());
        guy.gameObjects.get(0).getComponent(Transform.class).setRelative(guy.getComponent(Transform.class));
        guy.addComponent(new GameComponent() {
            DialogueSystem dialogue;
            SpriteRenderer spriteRenderer;
            BoxCollider collider;
            boolean busy=false;
            public void start() {
                collider=this.getGameObject().getComponent(BoxCollider.class);
                collider.name="guyCollider";
                spriteRenderer=this.getGameObject().getComponent(SpriteRenderer.class);
                spriteRenderer.color.set(1f,0.8f,0.3f,1f);
                
                collider.setCollisionAdapter(new CollisionAdapter() {
                    @Override
                    public void onCollisionEnter(Collider collider) {
                        if(collider.name.equals("mouse-pointer")&&!busy)
                            {dialogue.setDialogue("greeting");busy=true;}
                    }
                });
                dialogue=new DialogueSystem(true,0,0.3f,1f);
                Dialogue question=new Dialogue("question","Can you help me?",2.5f) {
                    @Override
                    public void onDialogueComplete() {
                        busy=false;
                    }
                };
                question.choices.add(new Choice("Give Tomato Soup","thanks"));
                question.choices.add(new Choice("Do nothing",""));
                Dialogue greeting=new Dialogue("greeting","Hello, I've been travelling for 2 weeks.",3f);
                greeting.choices.add(new Choice("","greeting2"));
                Dialogue greeting2=new Dialogue("greeting2","I feel I will die if I won't eat anything",3f);
                greeting2.choices.add(new Choice("","question"));
                dialogue.dialogues.add(greeting,question,greeting2,new Dialogue("thanks","You saved my life, I won't forget that!",3f));
                
                ((Joint)this.getGameObject()).gameObjects.get(0).addComponent(dialogue);
            }
            public void update(float delta) {
                collider.setSize(spriteRenderer.sprite.textureRegion.getRegionWidth(),
                                 spriteRenderer.sprite.textureRegion.getRegionHeight());
                dialogue.config.xOffset=spriteRenderer.sprite.textureRegion.getRegionWidth()/2;
            }
        });
        
        engine.getLevel().addGameObject(player);
        engine.getLevel().addGameObject(guy);
    }
    
    public static class PlayerController extends GameComponent {
        public float speed=50;
        private Transform transform;
        private SpriteRenderer spriteRenderer;
        
        @Override
        public void start() {
            transform=getGameObject().getComponent(Transform.class);
            spriteRenderer=getGameObject().getComponent(SpriteRenderer.class);
        }
        @Override
        public void update(float delta) {
            if(Gdx.input.isKeyPressed(Keys.A)) {transform.matrix.translate(-speed*delta,0);spriteRenderer.flipX=true;}
            else if(Gdx.input.isKeyPressed(Keys.D)) {transform.matrix.translate(speed*delta,0);spriteRenderer.flipX=false;}
            if(Gdx.input.isKeyPressed(Keys.W)) {transform.matrix.translate(0,speed*delta);}
            else if(Gdx.input.isKeyPressed(Keys.S)) {transform.matrix.translate(0,-speed*delta);}
        }
    }
    
    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Keys.R))
            engine.loadGameMode(new ActualGameMode());
        
        Array<SpriteRenderer> spriteRenderers=new Array<SpriteRenderer>();
        for(GameObject obj : engine.getLevel().getAllGameObjects())
            spriteRenderers.addAll(obj.getComponents(SpriteRenderer.class));
        boolean ok=false;
        while(!ok)
        {
            ok=true;
            for(int i=0;i<spriteRenderers.size-1;i++)
            {
                Object o1=spriteRenderers.get(i);
                Object o2=spriteRenderers.get(i+1);
                
                float y1=0,y2=0;
                Matrix3 mat=Pools.obtain(Matrix3.class);
                Vector2 pos=Pools.obtain(Vector2.class);
                
                ((Renderable)o1).getGameObject().getComponent(Transform.class).calculateMatrix(mat).getTranslation(pos);
                y1=pos.y;
                ((Renderable)o2).getGameObject().getComponent(Transform.class).calculateMatrix(mat).getTranslation(pos);
                y2=pos.y;
                
                Pools.free(mat);
                Pools.free(pos);
                if((y1<y2&&((Renderable)o1).layer>((Renderable)o2).layer) ||
                   (y1>y2&&((Renderable)o1).layer<((Renderable)o2).layer))
                {
                    int j=((Renderable)o1).layer;
                    ((Renderable)o1).layer=((Renderable)o2).layer;
                    ((Renderable)o2).layer=j;
                    ok=false;
                }
            }
        }
    }
    @Override
    public void renderGui(OrthographicCamera camera) {
        BitmapFont font=GameServices.getFonts().get("pixeltype");
        font.setColor(1,1,1,1);
        font.draw(GameServices.getSpriteBatch(),"Press R to reset",15,25);
    }
}
