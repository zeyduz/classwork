import tester.*;
import javalib.funworld.WorldScene;
import javalib.worldimages.*;
import java.awt.Color;

interface IGamePiece {
  WorldScene draw(WorldScene scene);
  boolean collided(AGamePiece other);
  
  boolean sameBullet(Bullet that);
  boolean sameShip(Ship that);
 
}

abstract class AGamePiece implements IGamePiece {
  int xPos;
  int yPos;
  int radius;
  
  AGamePiece(int xPos, int yPos, int radius) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.radius = radius;
  }
  
  public boolean collided(AGamePiece other) {
    double dist = Math.hypot(Math.abs(this.xPos - other.xPos),
                             Math.abs(this.yPos - other.yPos));
    if (dist < this.radius + other.radius) {
      return true;
    }
    return false;
  }
  
  public boolean sameBullet(Bullet that) {
    return false;
  }
  
  public boolean sameShip(Ship that) {
    return false;
  }
}

class Bullet extends AGamePiece {
  int generation;
  int degree;
  //speed is 8px/tick
  //pink
  
  Bullet(int generation, int degree, int xPos, int yPos) {
    super(xPos, yPos, generation * 2);
    this.generation = generation;
    this.degree = degree;
  }
  
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new CircleImage(this.radius, OutlineMode.SOLID, Color.pink), this.xPos, this.yPos);
  }
  
  @Override
  public boolean sameBullet(Bullet that) {
    return true;
  }
  
  boolean bulletHitShips(ILoShips ships) {
    return ships.collided(this);
  }
  
  //gen 1 = player fired bullet straight up
  //gen 2 = bullets go left and right
  //{0, 1, 2, ..., n}
  //degree = i * (360/n+1)
  ILoBullets makeNewBullets() {
    return this.makeNewBullets(0);
  }
  
  //accum is the current i-valued bullet
  ILoBullets makeNewBullets(int accum) {
    int newDegree = accum * (360/(this.generation + 1));
    Bullet current = new Bullet(this.generation + 1, newDegree, this.xPos, this.yPos);
    if (accum > this.generation) {
      return new MtLoBullets();
    }
    return new ConsLoBullets(current, this.makeNewBullets(accum + 1));
  }
  
  Bullet moveBullet() {
    int newXPos = (int) (this.xPos + 8 * Math.cos(Math.toRadians(this.degree)));
    int newYPos = (int) (this.yPos + -8 * Math.sin(Math.toRadians(this.degree)));
    return new Bullet(this.generation, this.degree, newXPos, newYPos);
  }
  
}

class Ship extends AGamePiece {
  int direction; //either 1 or -1
  //speed is 4px/tick
  //cyan
  
  Ship(int radius, int direction, int xPos, int yPos) {
    super(xPos, yPos, radius);
    this.direction = direction;
  }
  
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new CircleImage(this.radius, OutlineMode.SOLID, Color.cyan), this.xPos, this.yPos);
  }
  
  public boolean collided(IGamePiece other) {
    return false;
  }
  
  @Override
  public boolean sameShip(Ship that) {
    return true;
  }
  boolean shipHitBullets(ILoBullets bullets) {
    return bullets.collided(this);
  }
  
  Ship moveShip() {
    return new Ship(this.radius, this.direction, (this.direction * 4) + this.xPos, this.yPos);
  }
  
}

class ExamplesGamePiece {
  
  AGamePiece ship1 = new Ship( 10, -1, 250, 150);
  AGamePiece bullet1 = new Bullet(2, 90, 271, 150);
  
  AGamePiece ship2 = new Ship( 10, -1, 250, 150);
  AGamePiece bullet2 = new Bullet(2, 90, 270, 150);
  
  AGamePiece ship3 = new Ship( 10, -1, 250, 150);
  AGamePiece bullet3 = new Bullet(2, 90, 260, 150);
  
  AGamePiece ship4 = new Ship( 10, -1, 250, 160);
  AGamePiece bullet4 = new Bullet(2, 90, 259, 150);
  
  AGamePiece ship5 = new Ship( 10, -1, 250, 160);
  AGamePiece bullet5 = new Bullet(2, 90, 260, 150);
  
  boolean testCollided(Tester t) {
    return t.checkExpect(ship1.collided(bullet1), false)
        && t.checkExpect(bullet1.collided(ship1), false)
        && t.checkExpect(ship2.collided(bullet2), false)
        && t.checkExpect(ship3.collided(bullet3), true)
        && t.checkExpect(ship4.collided(bullet4), true)
        && t.checkExpect(ship5.collided(bullet5), false);
  }
  
}
