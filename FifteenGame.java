import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.lang.reflect.Array;

//Over 10 predicate
class over10 implements Predicate<Integer> {

  // Test the predicate on an integer
  public boolean test(Integer t) {
    return t > 10;
  }

}

//Is odd predicate
class odds implements Predicate<Integer> {

  // Test the predicate on an integer
  public boolean test(Integer t) {
    return t % 2 == 1;
  }

}

//Utility Class
class Utils {

  // Filters the list and returns elements that satisfy the predicate
  <T> ArrayList<T> filter(ArrayList<T> arr, Predicate<T> pred) {
    ArrayList<T> out = new ArrayList<T>();
    for (T elem : arr) {
      if (pred.test(elem)) {
        out.add(elem);
      }
    }
    return out;
  }

  // Edits the list and removes those that do not satisfy the predicate
  <T> void removeExcept(ArrayList<T> arr, Predicate<T> pred) {
    for (int i = arr.size() - 1; i >= 0; i--) {
      if (!pred.test(arr.get(i))) {
        arr.remove(i);
      }
    }
  }

}

//Represents an individual tile
class Tile {
// The number on the tile.  Use 0 to represent the hole
  int value;

  Tile(int value) {
    this.value = value;
  }

  // Draws this tile onto the background at the specified logical coordinates
  void drawAt(int col, int row, WorldScene background) {
    // Assuming the board is 120 by 120
    // Each tile will be 30x30
    if (this.value != 0) {
      WorldImage tileTemp = new RectangleImage(30, 30, OutlineMode.SOLID, Color.black);
      WorldImage tile = new OverlayImage(new TextImage("" + this.value + "", Color.white),
          tileTemp);
      background.placeImageXY(tile, -15 + 30 * row, -15 + 30 * col);
    }

  }

  // Swap these mofos
  void swap(Tile other) {
    int temp = this.value;
    this.value = other.value;
    other.value = temp;
  }

}

//our game
class FifteenGame extends World {
  // represents the rows of tiles
  // ArrayList<ArrayList<Tile>> tiles = new ArrayList<ArrayList<Tile>>();
  Tile[][] tiles = new Tile[4][4];

  FifteenGame() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        if (i == 3 && j == 3) {
          tiles[i][j] = new Tile(0);
        }
        else {
          tiles[i][j] = new Tile((4 * i) + j + 1);
        }
      }
    }
  }

  // draws the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(120, 120);

    this.addTiles(scene);

    return scene;
  }
  
  @Override
  public void onTick() {
    WorldScene scene = new WorldScene(120, 120);

    this.addTiles(scene);
  }

  // Adds tiles
  void addTiles(WorldScene scene) {

    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        tiles[i][j].drawAt(i + 1, j + 1, scene);
      }
    }

  }
  
  //get row index of 0
  int rowIndexofZero() {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if( tiles[i][j].value == 0 ) {
          return i;
        }
      }
    }
    return -1;
  }
  
  //get column index of 0
  int colIndexofZero() {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if( tiles[i][j].value == 0 ) {
          return j;
        }
      }
    }
    return -1;
  }

  // handles keystrokes
  public void onKeyEvent(String k) {
    int rowZero = this.rowIndexofZero();
    int colZero = this.colIndexofZero();
    // needs to handle up, down, left, right to move the hole (WASD)
    if (k.equals("w")) { // Up
      if(rowZero != 3) {
        tiles[rowZero][colZero].swap(tiles[rowZero + 1][colZero]);
      }
    }
    else if (k.equals("a")) { // Left
      if(colZero != 0) {
        tiles[rowZero][colZero].swap(tiles[rowZero][colZero - 1]);
      }
    }
    else if (k.equals("s")) { // Down
      if(rowZero != 0) {
        tiles[rowZero][colZero].swap(tiles[rowZero - 1][colZero]);
      }
    }
    else if (k.equals("d")) { // Right
      if(colZero != 3) {
        tiles[rowZero][colZero].swap(tiles[rowZero][colZero + 1]);
      }
    }
  }

}

class ExamplesFifteenGame {

  void testGame(Tester t) {
    FifteenGame g = new FifteenGame();
    g.bigBang(120, 120);
  }

  ArrayList<Integer> arr1;
  Utils u = new Utils();

  void setup() {
    arr1 = new ArrayList<Integer>(Arrays.asList(1, 10, 11, 6, 15));
  }

  void testFilter(Tester t) {
    setup();
    t.checkExpect(u.filter(this.arr1, new over10()), new ArrayList<Integer>(Arrays.asList(11, 15)));

    t.checkExpect(u.filter(this.arr1, new odds()),
        new ArrayList<Integer>(Arrays.asList(1, 11, 15)));

  }

  void testremoveExcept(Tester t) {
    setup();
    t.checkExpect(this.arr1, new ArrayList<Integer>(Arrays.asList(1, 10, 11, 6, 15)));
    this.u.removeExcept(this.arr1, new odds());
    t.checkExpect(this.arr1, new ArrayList<Integer>(Arrays.asList(1, 11, 15)));

    setup();
    this.u.removeExcept(this.arr1, new over10());
    t.checkExpect(this.arr1, new ArrayList<Integer>(Arrays.asList(11, 15)));
  }
}