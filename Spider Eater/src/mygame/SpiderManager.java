/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.TextureKey;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.Random;

/**
 *
 * @author Bob
 */
public class SpiderManager extends AbstractAppState {
    
  private SimpleApplication   app;
  private Material            mat;
  private Random              rand;
  public  Node                spiderNode;
  private InteractionManager  inter;
  private boolean             up,down,left,right;
    
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    inter    = app.getStateManager().getState(InteractionManager.class);
    init();
    setEnabled(false);
    }
  
  private void init(){
    spiderNode      = new Node();
    mat             = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    rand            = new Random();
    TextureKey key  = new TextureKey("Textures/Spider.png", false);
    Texture tex     = app.getAssetManager().loadTexture(key);
    mat.setTexture("ColorMap", tex);
    app.getRootNode().attachChild(spiderNode);
    }
  
  private void createSpider() {
    Spider spider    = new Spider();
    Node model       = (Node) app.getAssetManager().loadModel("Models/spider.j3o");
    AnimControl ac   = model.getControl(AnimControl.class);
    AnimChannel anch = ac.createChannel();
    anch.setAnim("Run");
    model.setMaterial(mat);
    model.setName("Model");
    spider.attachChild(model);
    spiderNode.attachChild(spider);
    randomizeSpider(spider);
    }
  
  private void randomizeSpider(Spider spider) {
      
    float x     = randInt(0, 50)-25;
    float y     = randInt(0, 50)-25;  
    
    x = x/2;
    y = y/2;
    
    if (x>y) {
        
      if (x>0) {
        x = 15.5f;
        spider.moveDir = new Vector3f(-1,0,0);
        spider.rotate(0,-89.5f,0);
        }
      
      else {
        x = -15.5f;
        spider.moveDir = new Vector3f(1,0,0);
        spider.rotate(0,89.5f,0);
        }
      
      }
    
    else {
        
      if (y>0) {
        y = 15.5f;
        spider.moveDir = new Vector3f(0,0,-1);
        spider.rotate(0,179f,0);
        }
      
      else {
        y = -15.5f;
        spider.moveDir = new Vector3f(0,0,1);
        }
      
      }
    
    spider.speed   = randInt(3,7);
    spider.moveDir = spider.moveDir.mult(spider.speed);
    spider.size    = (float) randInt(1,10)/10;
    spider.setLocalTranslation(x, 0, y);
    changeSize(spider);
    }
  
  private void changeSize(Spider spider){
    spider.setLocalScale(spider.size);
    }
  
  private int randInt(int min, int max) {
    int    randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
    }
  
  @Override
  public void update(float tpf) {
    
    //Creates a spider if there is less than 10 spiders  
    if (spiderNode.getQuantity() < 10) {
      createSpider();
      }
    
    //Checks Each Spider
    for (int i = 0; i < spiderNode.getQuantity(); i++) {
      
      //Checks the Interaction Manager for Input
      up    = inter.up;
      down  = inter.down;
      left  = inter.left;
      right = inter.right;
      
      //Sets players movement to 0
      float xMove = 0;
      float yMove = 0;
       
      //If there is any input, set the move accordingly 
      if (down) {
        yMove = 6;
        }
      
      else if (up) {
        yMove = -6;  
        }
      
      if (left) {
        xMove = -6;  
        }
      
      else if (right) {
        xMove = 6;  
        }
      
      //Gets the Current Spider
      Spider spider = (Spider) spiderNode.getChild(i);
      
      //Actually is doing the moving of the spider
      spider.move((spider.moveDir.add(xMove,0,yMove)).mult(tpf));
      
      //Remove the Spider if it is too far away
      if (spider.getLocalTranslation().x > 16 ^ spider.getLocalTranslation().x < -16)
      spider.removeFromParent();
      
      //Remove the Spider if it is too far away
      if (spider.getLocalTranslation().z > 16 ^ spider.getLocalTranslation().z < -16)
      spider.removeFromParent();
      
      //Checks each spider for collision with Current Spider
      for (int j = 0; j < spiderNode.getQuantity(); j++) {
       
        //Gets the Current Spider
        Spider currentSpider         = (Spider) spiderNode.getChild(j);
        CollisionResults results = new CollisionResults();
        
        //Checks to be sure it is not checking itself, then checks collision with current spider.
        if (spider != currentSpider) 
        spider.collideWith(currentSpider.getChild("Model").getWorldBound(), results);
        
        //Checks if a Spider is Hit
        if (results.size() > 0) {
          
          //If Current Spider is Bigger than the Hit Spider Make it Bigger and remove Hit Spider 
          if (currentSpider.size > spider.size) {
            spider.removeFromParent();
            if (currentSpider.size < .12) {
              currentSpider.size = spider.size + .1f;
              changeSize(currentSpider);
              }
            }
          
          //If Current Spider is Smaller than the Hit Spider Remove It and make Hit Spider Bigger
          else {
            currentSpider.removeFromParent();
            if (spider.size < .12) {
              spider.size = spider.size + .1f;
              changeSize(spider);
              }
            }
            
          }          
          
        }
      
      } 
    
    }
    
  }

