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
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResults;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bob
 */
public class PlayerManager extends AbstractAppState {
  
  private SimpleApplication app;  
  public  Node              player;
  private Node              cubeNode;
  private float             playerSize;
  public  int               spidersEaten;
  public  int               highScore;
  public  AnimChannel       animChannel;
    
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app  = (SimpleApplication) app;
    cubeNode  = app.getStateManager().getState(SpiderManager.class).spiderNode;
    highScore = readScore(stateManager);
    initPlayer();
    }
    
  public void initPlayer(){
    Node model      = (Node) app.getAssetManager().loadModel("Models/spider.j3o");
    player          = new Node("Player");
    playerSize      = .3f;
    Material mat    = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key  = new TextureKey("Textures/Spider.png", false);
    Texture tex     = app.getAssetManager().loadTexture(key);
    AnimControl ac  = model.getControl(AnimControl.class);
    animChannel     = ac.createChannel();
    animChannel.setAnim("Run");
    mat.setTexture("ColorMap", tex);
    model.setMaterial(mat);
    player.attachChild(model);
    app.getRootNode().attachChild(player);
    scalePlayer();
    }
  
  private void scalePlayer(){
    player.setLocalScale(playerSize);
    }
  
  private void lose(){
    SpiderManager cm = app.getStateManager().getState(SpiderManager.class);
    playerSize       = .3f;
    scalePlayer(); 
    app.getStateManager().getState(InteractionManager.class).dead = true;
    app.getStateManager().getState(GuiManager.class).showTitle();
    app.getStateManager().getState(GuiManager.class).titleDisplay.setText("You Lose");
    app.getStateManager().getState(GuiManager.class).hideJoystick();
    cm.setEnabled(false);
    cm.spiderNode.detachAllChildren();
    InteractionManager inter = app.getStateManager().getState(InteractionManager.class);
    inter.up    = false;
    inter.down  = false;
    inter.left  = false;
    inter.right = false;
    
    if (spidersEaten > highScore) {
      saveScore(spidersEaten, app.getStateManager());
      highScore = spidersEaten;
      }
    
    }
  
  public void saveScore(int newScore, AppStateManager stateManager) {
    
    String filePath;  
      
    try {  
      filePath         = stateManager.getState(AndroidManager.class).filePath;
      }
    
    catch (NullPointerException e){
      filePath = JmeSystem.getStorageFolder().toString();
      }
    
    BinaryExporter exporter = BinaryExporter.getInstance();
    Node score              = new Node();
    score.setUserData("Name", "Hope");
    score.setUserData("Score", newScore);
    File file               = new File(filePath + "/score.j3o");
    
    System.out.println("Saving Score");
    
    try {
        
      exporter.save(score, file);  
      System.out.println("Score saved to: " + filePath);
        
      }
    
    catch (IOException e) {
        
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Failed to save game!", e);  
        System.out.println("Failure");
      }
    
      System.out.println("score completion");
    
    }
  
  public int readScore(AppStateManager stateManager) {
      
    String filePath;  
      
    try {  
      filePath         = stateManager.getState(AndroidManager.class).filePath;
      }
    
    catch (NullPointerException e){
      filePath = JmeSystem.getStorageFolder().toString();
      }
    
     AssetManager assetManager = stateManager.getApplication().getAssetManager();
     
     assetManager.registerLocator(filePath, FileLocator.class);
     
     Node newNode;
     int  score;
     
     try {
       newNode = (Node) assetManager.loadModel("score.j3o");
       score   = newNode.getUserData("Score");
       }
     
     catch (AssetNotFoundException ex) {
       saveScore(0, stateManager);
       score = 0;    
       }
     
     catch (IllegalArgumentException e) {
       saveScore(0, stateManager);
       score = 0;
       }
     
     
     System.out.println("You've loaded: " + score);
     return score;
     }
  
private void rotate() {
      
   InteractionManager inter = app.getStateManager().getState(InteractionManager.class);
   boolean up    = inter.up;
   boolean down  = inter.down;
   boolean left  = inter.left;
   boolean right = inter.right;
    
    if (up) {
      
      if (left) {
        player.lookAt(new Vector3f(1,0,1), new Vector3f(0,1,0));
        }
      
      else if (right) {
        player.lookAt(new Vector3f(-1,0,1), new Vector3f(0,1,0));
        }
      
      else {
        player.lookAt(new Vector3f(0,0,1), new Vector3f(0,1,0));
        }
      
      }
    
    else if (down) {
      
      if (left) {
        player.lookAt(new Vector3f(1,0,-1), new Vector3f(0,1,0));
        }
      
      else if (right) {
        player.lookAt(new Vector3f(-1,0,-1), new Vector3f(0,1,0));
        }
      
      else {
        player.lookAt(new Vector3f(0,0,-1), new Vector3f(0,1,0));
        } 
        
      }
    
    else if (left) {
      player.lookAt(new Vector3f(1,0,0), new Vector3f(0,1,0));
      }
    
    else if (right){
      player.lookAt(new Vector3f(-1,0,0), new Vector3f(0,1,0));
      }
    }
  
  @Override
  public void update(float tpf) {
    
    rotate();
      
    for (int i = 0; i < cubeNode.getQuantity(); i++) {
      
      CollisionResults results = new CollisionResults();  
      Spider currentCube         = (Spider) cubeNode.getChild(i);
      
      currentCube.collideWith(player.getWorldBound(), results);
      
      if (results.size() > 0){
        
        if (playerSize > currentCube.size) {
          
          currentCube.removeFromParent();
          spidersEaten++;
          
          if (playerSize < .9f) {
            playerSize = playerSize + .01f;
            scalePlayer();
            }
          
          }
        
        else {
          lose();
          }
          
        }
      
      }  
      
    }
  
  }
