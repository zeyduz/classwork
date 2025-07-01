import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

class MyGame extends World{
  int WIDTH = 500;
  int HEIGHT = 300;
  int currentTick;
  int finalTick;
  Random random;
  
  int bulletsRemaining;
  int score;
  
  ILoBullets bullets;
  ILoShips ships;
  
  boolean welcomeScreen;
  
  MyGame(int bulletsRemaining) {
    this(500, 300, 0, 20, bulletsRemaining, new Random());
  }
  
  MyGame(int bulletsRemaining, Random random) {
    this(500, 300, 0, 20, bulletsRemaining, random);
  }
  
  MyGame(int width, int height, int currentTick, int endTick, int bulletsRemaining, Random random){
    if ( width < 0 || height < 0 || endTick < 2) {
      throw new IllegalArgumentException("Invalid arguments passed to constructor.");
    }
    this.WIDTH = width;
    this.HEIGHT = height;
    this.currentTick = currentTick;
    this.finalTick = endTick;
    this.random = random;
    this.bulletsRemaining = bulletsRemaining;
    this.score = 0;
    this.bullets = new MtLoBullets();
    this.ships = new MtLoShips();
    this.welcomeScreen = true;
  }
  
  MyGame(int width, int height, int currentTick, int endTick, int bulletsRemaining, int score, ILoBullets bullets, ILoShips ships, Random random){
    if ( width < 0 || height < 0 || endTick < 2) {
      throw new IllegalArgumentException("Invalid arguments passed to constructor.");
    }
    this.WIDTH = width;
    this.HEIGHT = height;
    this.currentTick = currentTick;
    this.finalTick = endTick;
    this.random = random;
    this.bulletsRemaining = bulletsRemaining;
    this.score = score;
    this.bullets = bullets;
    this.ships = ships;
    this.welcomeScreen = false;
  }
  
  @Override
  public WorldScene makeScene() {
    //Make a new scene.
    WorldScene scene = new WorldScene(this.WIDTH, this.HEIGHT);
    
    if (this.welcomeScreen ) {
      scene = this.addWelcomeMessage(scene);
    }
    
    scene = this.addVido(scene);
    scene = this.addTank(scene);
    scene = this.addBullets(scene);
    scene = this.addShips(scene);
    
    scene = this.addInfoToScene(scene);
    
    return scene;
  }
  
  WorldScene addWelcomeMessage(WorldScene scene) {
    return scene.placeImageXY( new TextImage("Game will start shortly.", Color.white), WIDTH/2, HEIGHT/2);
  }
  
  WorldScene addVido(WorldScene scene) {
    return scene.placeImageXY( new FromFileImage("vido.png"), WIDTH/2, HEIGHT/2);
  }
  
  WorldScene addTank(WorldScene scene) {
    return scene.placeImageXY( new RectangleImage(20, 20, OutlineMode.SOLID, Color.green), WIDTH/2 , HEIGHT - 10);
  }
  
  WorldScene addBullets(WorldScene scene) {
    return bullets.drawBullets(scene);
  }
  
  WorldScene addShips(WorldScene scene) {
    //Add it to the scene and return the scene. 
    return ships.drawShips(scene);
  }
  
  //Display bullets remaining and current score
  WorldScene addInfoToScene(WorldScene scene) {
    return scene.placeImageXY( new TextImage("Bullets Remaining: " + Integer.toString(this.bulletsRemaining) + "  Current Score: " + Integer.toString(this.score) , Color.white),130, 20);
  }
  
  @Override
  //This method gets called every tickrate seconds ( see bellow in example class).
  public MyGame onTick() {
    return this.addShips().addBullets().checkCollisions().removeBullets().removeShips().spawnShips().incrementGameTick();
  }
  
  public MyGame addShips() {
    //TODO Move ships method 
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, this.score, this.bullets, this.ships.moveShips(), this.random);
  }
  
  public MyGame addBullets() {
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, this.score, this.bullets.moveBullets(), this.ships, this.random);
  }
  
  //Checks and updates if bullets collide with any ships
  public MyGame checkCollisions() {
    //ILoBullets bullets
    //ILoShips ships
    ILoBullets newBullets = this.bullets.checkBulletCollisions(this.ships);
    ILoShips newShips = this.ships.checkShipCollisions(this.bullets);
    int scoreGained = this.bullets.countCollisions(this.ships);
    
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, 
        this.score + scoreGained, 
        newBullets, newShips, this.random);
        
  }
  
  public MyGame removeBullets() {
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, this.score, this.bullets.removeOffScreen(this.HEIGHT, this.WIDTH), this.ships, this.random );
  }
  
  public MyGame removeShips() {
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, this.score, this.bullets, this.ships.removeOffScreen(this.HEIGHT, this.WIDTH), this.random );
  }
  
  public MyGame spawnShips() {
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick, this.bulletsRemaining, this.score, this.bullets, this.ships.spawnShips(this.random, this.HEIGHT, this.WIDTH, this.currentTick), this.random);
  }
  
  public MyGame incrementGameTick() {
    return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick + 1, this.finalTick, this.bulletsRemaining, this.score, this.bullets, this.ships, this.random);
  }
  
  public MyGame onKeyEvent(String key) {
     //did we press the space update the final tick of the game by 10. 
     if (key.equals(" ") && this.bulletsRemaining > 0 ) {
       return new MyGame(this.WIDTH, this.HEIGHT, this.currentTick, this.finalTick+10, this.bulletsRemaining - 1, this.score, this.bullets.addTankBullet(this.WIDTH, this.HEIGHT), this.ships, this.random);
     }else {
       return this;
     }
  }
  
  //Check to see if we need to end the game.
  @Override
    public WorldEnd worldEnds() {
      if (this.bulletsRemaining == 0 && this.bullets.isEmpty()) {
        return new WorldEnd(true, this.makeEndScene());
      } else {
        return new WorldEnd(false, this.makeEndScene());
      }
    }
    
    public WorldScene makeEndScene() {
      WorldScene endScene = new WorldScene(this.WIDTH, this.HEIGHT);
      return endScene.placeImageXY( new OverlayImage(new TextImage("Game Over. Score: " + this.score, Color.red), new FromFileImage("vido.png")), 250, 250);
      
    }

}

class ExamplesMyWorldProgram {
  Random random = new Random();
  
  boolean testBigBang(Tester t) {
  MyGame world = new MyGame(10, this.random);
  //width, height, tickrate = 0.5 means every 0.5 seconds the onTick method will get called.
    return world.bigBang(500, 300, 1.0/28.0);
  }
}




