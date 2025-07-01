import javalib.funworld.*;
import java.util.Random;

interface ILoBullets {
  boolean collided(Ship ship);
  ILoBullets checkBulletCollisions(ILoShips ships);
  WorldScene drawBullets(WorldScene scene);
  ILoBullets moveBullets();
  ILoBullets addTankBullet(int width, int height);
  int countCollisions(ILoShips ships);
  boolean isEmpty();
  ILoBullets removeOffScreen(int height, int width);
  ILoBullets append(ILoBullets other);
}

class MtLoBullets implements ILoBullets {
  
  public boolean collided(Ship ship) {
    return false;
  }
  
  public ILoBullets checkBulletCollisions(ILoShips ships) {
    return new MtLoBullets();
  }
  
  public WorldScene drawBullets(WorldScene scene) {
    return scene;
  }
  
  public ILoBullets moveBullets() {
    return new MtLoBullets();
  }
  
  public ILoBullets addTankBullet(int width, int height) {
    return new ConsLoBullets(new Bullet(1, 90, width / 2, height - 10), new MtLoBullets());
  }
  
  public int countCollisions(ILoShips ships) {
    return 0;
  }
  
  public boolean isEmpty() {
    return true;
  }
  
  public ILoBullets removeOffScreen(int height, int width) {
    return new MtLoBullets();
  }
  
  public ILoBullets append(ILoBullets other) {
    return other;
  }
}

class ConsLoBullets implements ILoBullets {
  Bullet first;
  ILoBullets rest;
  
  ConsLoBullets(Bullet first, ILoBullets rest) {
    this.first = first;
    this.rest = rest;   
  }
  
  public boolean collided(Ship ship) {
    return ship.collided(this.first) || this.rest.collided(ship);
  }
  
  public ILoBullets checkBulletCollisions(ILoShips ships) {
    if( this.first.bulletHitShips(ships) ) {
      ILoBullets addedBullets = this.first.makeNewBullets();//broken
      return (this.rest.checkBulletCollisions(ships)).append(addedBullets);
    }
    return new ConsLoBullets(this.first, this.rest.checkBulletCollisions(ships));
  }
  
  public WorldScene drawBullets(WorldScene scene) {
    return this.rest.drawBullets(this.first.draw(scene));
  }
  
  public ILoBullets moveBullets() {
    return new ConsLoBullets(this.first.moveBullet(), this.rest.moveBullets());
  }
  
  public ILoBullets addTankBullet(int width, int height) {
    return new ConsLoBullets(this.first, this.rest.addTankBullet(width, height));
  }
  
  public int countCollisions(ILoShips ships) {
    if( this.first.bulletHitShips(ships) ) {
      return 1 + this.rest.countCollisions(ships);
    }
    return this.rest.countCollisions(ships);
  }
  
  public boolean isEmpty() {
    return false;
  }
  
  public ILoBullets removeOffScreen(int height, int width) {
    if (this.first.offScreen(height, width)) {
      return this.rest.removeOffScreen(height, width);
    }
    return new ConsLoBullets(this.first, this.rest.removeOffScreen(height, width));
  }
  
  public ILoBullets append(ILoBullets other) {
    return new ConsLoBullets(this.first, this.rest.append(other));
  }
}

interface ILoShips {
  boolean collided(Bullet bullet);
  ILoShips checkShipCollisions(ILoBullets bullets);
  WorldScene drawShips(WorldScene scene);
  ILoShips moveShips();
  ILoShips spawnShips(Random random, int height, int width, int currentTick);
  ILoShips removeOffScreen(int height, int width);
}

class MtLoShips implements ILoShips {
  
  public boolean collided(Bullet bullet) {
    return false;
  }
  
  public ILoShips checkShipCollisions(ILoBullets bullets) {
    return new MtLoShips();
  }
  
  public WorldScene drawShips(WorldScene scene) {
    return scene;
  }
  
  public ILoShips moveShips() {
    return new MtLoShips();
  }
  
  public ILoShips spawnShips(Random random, int height, int width, int currentTick) {
    if (currentTick % 28 == 0) {
      int shipsToSpawn = random.nextInt(4);
      return new Util().spawnNShips(random, shipsToSpawn, height, width);
    }
    return new MtLoShips();
  }
  
  public ILoShips removeOffScreen(int height, int width) {
    return new MtLoShips();
  }
  
}

class ConsLoShips implements ILoShips {
  Ship first;
  ILoShips rest;
  
  ConsLoShips(Ship first, ILoShips rest) {
    this.first = first;
    this.rest = rest;   
  }
  
  public boolean collided(Bullet bullet) {
    return bullet.collided(this.first) || this.rest.collided(bullet);
  }
  
  public ILoShips checkShipCollisions(ILoBullets bullets) {
    if( this.first.shipHitBullets(bullets) ) {
      return this.rest.checkShipCollisions(bullets);
    }
    return new ConsLoShips(this.first, this.rest.checkShipCollisions(bullets));
  }
  
  public WorldScene drawShips(WorldScene scene) {
    return this.rest.drawShips(this.first.draw(scene));
  }
  
  public ILoShips moveShips() {
    return new ConsLoShips(this.first.moveShip(), this.rest.moveShips());
  }
  
  public ILoShips spawnShips(Random random, int height, int width, int currentTick) {
    return new ConsLoShips(this.first, this.rest.spawnShips(random, height, width, currentTick));
    
  }
  
  public ILoShips removeOffScreen(int height, int width) {
    if (this.first.offScreen(height, width)) {
      return this.rest.removeOffScreen(height, width);
    }
    return new ConsLoShips(this.first, this.rest.removeOffScreen(height, width));
  }
  
}

class Util {
  
  public ILoShips spawnNShips(Random random, int shipsToSpawn, int height, int width){
    if(shipsToSpawn <= 0) {
      return new MtLoShips();
    }
    
    int maxHeight = height - (height / 7);
    int minHeight = height / 7;
    int xPos;
    int randHeight = random.nextInt(maxHeight - minHeight + 1) + minHeight;
    boolean randomDirection = random.nextBoolean();
    int direction;
    if(randomDirection) {
      direction = 1;
      xPos = 0 - (height / 30);
    }else {
      direction = -1;
      xPos = width + (height / 30);
    }
    
    return new ConsLoShips(new Ship(height / 30, direction, xPos, randHeight),
        new Util().spawnNShips(random, shipsToSpawn - 1, height, width));
  }
}