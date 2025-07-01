import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import javalib.impworld.*;
import javalib.worldimages.*;
import tester.Tester;

// Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // Constructors
  Cell(int x, int y, Color color, boolean flooded) {
    this(x, y, color, flooded, null, null, null, null);
  }

  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // Updates this left cell to the given cell
  void updateLeft(Cell cell) {
    this.left = cell;
  }

  // Updates this top cell to the given cell
  void updateTop(Cell cell) {
    this.top = cell;
  }

  // Updates this right cell to the given cell
  void updateRight(Cell cell) {
    this.right = cell;
  }

  // Updates this bottom cell to the given cell
  void updateBottom(Cell cell) {
    this.bottom = cell;
  }

  // Draws this cell to the given scene
  void draw(WorldScene scene) {
    scene
        .placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                this.color),
            this.x + FloodItWorld.CELL_SIZE / 2, this.y + FloodItWorld.CELL_SIZE / 2);
  }

  boolean hasLeft() {
    return this.left != null;
  }

  boolean hasTop() {
    return this.top != null;
  }

  boolean hasRight() {
    return this.right != null;
  }

  boolean hasBottom() {
    return this.bottom != null;
  }

}

class ExamplesCell {

  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Cell cell5;
  Cell cell6;
  Cell cell7;
  WorldScene scene;

  void setup() {
    cell1 = new Cell(0, 0, Color.green, false);
    cell2 = new Cell(20, 20, Color.pink, false);
    cell3 = new Cell(50, 50, Color.black, false);
    cell4 = new Cell(150, 150, Color.red, false);

    cell5 = new Cell(30, 30, Color.pink, false, this.cell1, this.cell2, this.cell3, this.cell4);

    cell6 = new Cell(25, 30, Color.gray, true);
    cell7 = new Cell(10, 20, Color.yellow, true, this.cell6, this.cell5, this.cell4, this.cell3);

    scene = new WorldScene(250, 250);
  }

  void testhasLeft(Tester t) {
    setup();
    t.checkExpect(this.cell1.hasLeft(), false);
    t.checkExpect(this.cell2.hasLeft(), false);
    t.checkExpect(this.cell3.hasLeft(), false);
    t.checkExpect(this.cell4.hasLeft(), false);
    t.checkExpect(this.cell5.hasLeft(), true);
    t.checkExpect(this.cell6.hasLeft(), false);
    t.checkExpect(this.cell7.hasLeft(), true);
  }

  void testhasTop(Tester t) {
    setup();
    t.checkExpect(this.cell1.hasTop(), false);
    t.checkExpect(this.cell2.hasTop(), false);
    t.checkExpect(this.cell3.hasTop(), false);
    t.checkExpect(this.cell4.hasTop(), false);
    t.checkExpect(this.cell5.hasTop(), true);
    t.checkExpect(this.cell6.hasTop(), false);
    t.checkExpect(this.cell7.hasTop(), true);
  }

  void testhasRight(Tester t) {
    setup();
    t.checkExpect(this.cell1.hasRight(), false);
    t.checkExpect(this.cell2.hasRight(), false);
    t.checkExpect(this.cell3.hasRight(), false);
    t.checkExpect(this.cell4.hasRight(), false);
    t.checkExpect(this.cell5.hasRight(), true);
    t.checkExpect(this.cell6.hasRight(), false);
    t.checkExpect(this.cell7.hasRight(), true);
  }

  void testhasBottom(Tester t) {
    setup();
    t.checkExpect(this.cell1.hasBottom(), false);
    t.checkExpect(this.cell2.hasBottom(), false);
    t.checkExpect(this.cell3.hasBottom(), false);
    t.checkExpect(this.cell4.hasBottom(), false);
    t.checkExpect(this.cell5.hasBottom(), true);
    t.checkExpect(this.cell6.hasBottom(), false);
    t.checkExpect(this.cell7.hasBottom(), true);
  }

  void testdraw(Tester t) {
    setup();
    WorldScene expectedScene = new WorldScene(250, 250);

    t.checkExpect(this.scene, new WorldScene(250, 250));
    this.cell1.draw(this.scene);
    expectedScene.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        OutlineMode.SOLID, Color.green), 10, 10);
    t.checkExpect(this.scene, expectedScene);

    this.cell2.draw(this.scene);
    expectedScene.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        OutlineMode.SOLID, Color.pink), 30, 30);
    t.checkExpect(this.scene, expectedScene);

    this.cell3.draw(this.scene);
    expectedScene.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        OutlineMode.SOLID, Color.black), 60, 60);
    t.checkExpect(this.scene, expectedScene);

    this.cell4.draw(this.scene);
    expectedScene.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        OutlineMode.SOLID, Color.red), 160, 160);
    t.checkExpect(this.scene, expectedScene);

    this.cell5.draw(this.scene);
    expectedScene.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
        OutlineMode.SOLID, Color.pink), 40, 40);
    t.checkExpect(this.scene, expectedScene);

  }

  void testupdateLeft(Tester t) {
    setup();
    t.checkExpect(this.cell1.left, null);
    this.cell1.updateLeft(cell1);
    t.checkExpect(this.cell1.left, this.cell1);
    this.cell1.updateLeft(this.cell2);
    t.checkExpect(this.cell1.left, this.cell2);

    t.checkExpect(this.cell2.left, null);
    this.cell2.updateLeft(cell1);
    t.checkExpect(this.cell2.left, this.cell1);
    this.cell2.updateLeft(this.cell2);
    t.checkExpect(this.cell2.left, this.cell2);

    t.checkExpect(this.cell3.left, null);
    this.cell3.updateLeft(cell3);
    t.checkExpect(this.cell3.left, this.cell3);
    this.cell3.updateLeft(this.cell2);
    t.checkExpect(this.cell3.left, this.cell2);

    t.checkExpect(this.cell4.left, null);
    this.cell4.updateLeft(cell5);
    t.checkExpect(this.cell4.left, this.cell5);
    this.cell4.updateLeft(this.cell4);
    t.checkExpect(this.cell4.left, this.cell4);

    t.checkExpect(this.cell5.left, this.cell1);
    this.cell5.updateLeft(cell2);
    t.checkExpect(this.cell5.left, this.cell2);
    this.cell5.updateLeft(this.cell5);
    t.checkExpect(this.cell5.left, this.cell5);
    this.cell5.updateLeft(this.cell4);
    t.checkExpect(this.cell5.left, this.cell4);
  }

  void testupdateTop(Tester t) {
    setup();
    t.checkExpect(this.cell1.top, null);
    this.cell1.updateTop(cell1);
    t.checkExpect(this.cell1.top, this.cell1);
    this.cell1.updateTop(this.cell2);
    t.checkExpect(this.cell1.top, this.cell2);

    t.checkExpect(this.cell2.top, null);
    this.cell2.updateTop(cell1);
    t.checkExpect(this.cell2.top, this.cell1);
    this.cell2.updateTop(this.cell2);
    t.checkExpect(this.cell2.top, this.cell2);

    t.checkExpect(this.cell3.top, null);
    this.cell3.updateTop(cell3);
    t.checkExpect(this.cell3.top, this.cell3);
    this.cell3.updateTop(this.cell2);
    t.checkExpect(this.cell3.top, this.cell2);

    t.checkExpect(this.cell4.top, null);
    this.cell4.updateTop(cell5);
    t.checkExpect(this.cell4.top, this.cell5);
    this.cell4.updateTop(this.cell4);
    t.checkExpect(this.cell4.top, this.cell4);

    t.checkExpect(this.cell5.top, this.cell2);
    this.cell5.updateTop(cell1);
    t.checkExpect(this.cell5.top, this.cell1);
    this.cell5.updateTop(this.cell5);
    t.checkExpect(this.cell5.top, this.cell5);
    this.cell5.updateTop(this.cell4);
    t.checkExpect(this.cell5.top, this.cell4);
  }

  void testupdateRight(Tester t) {
    setup();
    t.checkExpect(this.cell1.right, null);
    this.cell1.updateRight(cell1);
    t.checkExpect(this.cell1.right, this.cell1);
    this.cell1.updateRight(this.cell2);
    t.checkExpect(this.cell1.right, this.cell2);

    t.checkExpect(this.cell2.right, null);
    this.cell2.updateRight(cell1);
    t.checkExpect(this.cell2.right, this.cell1);
    this.cell2.updateRight(this.cell2);
    t.checkExpect(this.cell2.right, this.cell2);

    t.checkExpect(this.cell3.right, null);
    this.cell3.updateRight(cell3);
    t.checkExpect(this.cell3.right, this.cell3);
    this.cell3.updateRight(this.cell2);
    t.checkExpect(this.cell3.right, this.cell2);

    t.checkExpect(this.cell4.right, null);
    this.cell4.updateRight(cell5);
    t.checkExpect(this.cell4.right, this.cell5);
    this.cell4.updateRight(this.cell4);
    t.checkExpect(this.cell4.right, this.cell4);

    t.checkExpect(this.cell5.right, this.cell3);
    this.cell5.updateRight(cell1);
    t.checkExpect(this.cell5.right, this.cell1);
    this.cell5.updateRight(this.cell5);
    t.checkExpect(this.cell5.right, this.cell5);
    this.cell5.updateRight(this.cell4);
    t.checkExpect(this.cell5.right, this.cell4);
  }

  void testupdateBottom(Tester t) {
    setup();
    t.checkExpect(this.cell1.bottom, null);
    this.cell1.updateBottom(cell1);
    t.checkExpect(this.cell1.bottom, this.cell1);
    this.cell1.updateBottom(this.cell2);
    t.checkExpect(this.cell1.bottom, this.cell2);

    t.checkExpect(this.cell2.bottom, null);
    this.cell2.updateBottom(cell1);
    t.checkExpect(this.cell2.bottom, this.cell1);
    this.cell2.updateBottom(this.cell2);
    t.checkExpect(this.cell2.bottom, this.cell2);

    t.checkExpect(this.cell3.bottom, null);
    this.cell3.updateBottom(cell3);
    t.checkExpect(this.cell3.bottom, this.cell3);
    this.cell3.updateBottom(this.cell2);
    t.checkExpect(this.cell3.bottom, this.cell2);

    t.checkExpect(this.cell4.bottom, null);
    this.cell4.updateBottom(cell5);
    t.checkExpect(this.cell4.bottom, this.cell5);
    this.cell4.updateBottom(this.cell4);
    t.checkExpect(this.cell4.bottom, this.cell4);

    t.checkExpect(this.cell5.bottom, this.cell4);
    this.cell5.updateBottom(cell1);
    t.checkExpect(this.cell5.bottom, this.cell1);
    this.cell5.updateBottom(this.cell5);
    t.checkExpect(this.cell5.bottom, this.cell5);
    this.cell5.updateBottom(this.cell4);
    t.checkExpect(this.cell5.bottom, this.cell4);
  }

}