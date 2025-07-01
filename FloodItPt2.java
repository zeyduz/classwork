import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.Posn;

import java.awt.Color;

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board = new ArrayList<Cell>();

  // Size (height and width) of the board
  static int BOARD_SIZE;

  static int COLOR_CHOICES;

  static int CELL_SIZE = 20;

  // Current tick of the game
  int currentTick;

  WorldScene scene = new WorldScene(FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE,
      FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE);

  Color floodColor;

  // Random object used for testing
  Random random = new Random(1);

  FloodItWorld() {
    this(15, 6, new Random());
  }

  // Constructor for FloodIt game
  FloodItWorld(int size, int colors, Random rand) {
    FloodItWorld.BOARD_SIZE = size;
    if (colors < 3) {
      FloodItWorld.COLOR_CHOICES = 3;
    }
    else if (colors > 8) {
      FloodItWorld.COLOR_CHOICES = 8;
    }
    else {
      FloodItWorld.COLOR_CHOICES = colors;
    }
    new Util().instantiateCells(size, this.random, this.board);
    new Util().adjustDirection(size, this.board);
    this.floodColor = this.board.get(0).color;
  }

  @Override
  public WorldScene makeScene() {
    this.addCells();
    return scene;
  }

  @Override
  public void onTick() {
      this.updateFloodedColor();
      this.floodCells();
  }

  // Add the cells to the scene
  void addCells() {
    for (Cell c : this.board) {
      c.draw(this.scene);
    }
  }

  // Flood the cells
  void floodCells() {
    for (Cell c : this.board) {
      if (c.flooded) {
        if(c.x != 0 && c.left.color.equals(this.floodColor)) {
          c.left.flooded = true;
        }
        if(c.x < (FloodItWorld.BOARD_SIZE - 1) * FloodItWorld.CELL_SIZE 
            && c.right.color.equals(this.floodColor)) {
          c.right.flooded = true;
        }
        if(c.y != 0 && c.top.color.equals(this.floodColor)) {
          c.top.flooded = true;
        }
        if(c.y < (FloodItWorld.BOARD_SIZE - 1) * FloodItWorld.CELL_SIZE 
            && c.bottom.color.equals(this.floodColor)) {
          c.bottom.flooded = true;
        }
      }
    }
  }
  
  void updateFloodedColor() {
    for (Cell c : this.board) {
      if(c.flooded) {
        c.color = this.floodColor;
      }
    }
  }

  @Override
  public void onMousePressed(Posn pos) {
    int indexOfClicked = new Util().posnToCellNum(pos);

    if (!this.board.get(indexOfClicked).flooded) {
      this.floodColor = this.board.get(indexOfClicked).color;
    }
  }
}

// Utility class
class Util {

  // Instantiates an array of cells with no connections
  void instantiateCells(int size, Random random, ArrayList<Cell> board) {
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        if (x == 0 && y == 0) {
          board.add(new Cell(x * FloodItWorld.CELL_SIZE, y * 20, this.randomColor(random), true));
        }
        else {
          board.add(new Cell(x * FloodItWorld.CELL_SIZE, y * 20, this.randomColor(random), false));
        }
      }
    }
  }

  void adjustDirection(int size, ArrayList<Cell> board) {
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        if (y != 0) {
          board.get(y * size + x).updateTop(board.get((y - 1) * size + x));
        }
        if (y != size - 1) {
          board.get(y * size + x).updateBottom(board.get((y + 1) * size + x));
        }
        if (x != 0) {
          board.get(y * size + x).updateLeft(board.get(y * size + x - 1));
        }
        if (x != size - 1) {
          board.get(y * size + x).updateRight(board.get(y * size + x + 1));
        }
      }
    }
  }

  // Generates a random color from a predetermined list
  Color randomColor(Random rand) {
    int val = rand.nextInt(FloodItWorld.COLOR_CHOICES);
    if (val == 0) {
      // Red
      return new Color(255, 102, 99);
    }
    if (val == 1) {
      // Green
      return new Color(158, 224, 158);
    }
    if (val == 2) {
      // Blue
      return new Color(158, 193, 207);
    }
    if (val == 3) {
      // Yellow
      return new Color(253, 253, 151);
    }
    if (val == 4) {
      // Purple
      return new Color(204, 153, 201);
    }
    if (val == 5) {
      // Orange
      return new Color(254, 177, 68);
    }
    if (val == 6) {
      // Pink
      return new Color(250, 202, 202);
    }
    // Cyan
    return new Color(153, 240, 235);
  }

  // Returns the index of the cell that the posn is in
  int posnToCellNum(Posn p) {
    int x = p.x;
    int y = p.y;

    int column = x / FloodItWorld.CELL_SIZE;
    int row = y / FloodItWorld.CELL_SIZE;

    return (row * FloodItWorld.BOARD_SIZE) + column;
  }

}

class ExamplesFloodIt {
  Random rand = new Random(1);
  ArrayList<Cell> board;
  Util u = new Util();

  Color myRed = new Color(255, 102, 99);
  Color myBlue = new Color(158, 193, 207);
  Color myGreen = new Color(158, 224, 158);

  void testBigBang(Tester t) {
    int size = 22;
    int colors = 3; // Maxes out at 8
    FloodItWorld world = new FloodItWorld(size, colors, this.rand);

    world.bigBang(FloodItWorld.CELL_SIZE * FloodItWorld.BOARD_SIZE,
        FloodItWorld.CELL_SIZE * FloodItWorld.BOARD_SIZE, 1.0/28.0);
  }

  void setup() {
    this.board = new ArrayList<Cell>();
    FloodItWorld.COLOR_CHOICES = 3;
  }

  void testInstantiateAndAdjustDirection(Tester t) {
    setup();
    // Test instantiateCells
    u.instantiateCells(2, rand, board);
    t.checkExpect(this.board,
        new ArrayList<Cell>(
            Arrays.asList(new Cell(0, 0, this.myRed, true), new Cell(20, 0, this.myGreen, false),
                new Cell(0, 20, this.myGreen, false), new Cell(20, 20, this.myRed, false))));
    // Test adjustDirection
    u.adjustDirection(2, board);
    t.checkExpect(this.board, new ArrayList<Cell>(Arrays.asList(
        new Cell(0, 0, this.myRed, true, null, null, this.board.get(1), this.board.get(2)),
        new Cell(20, 0, this.myGreen, false, this.board.get(0), null, null, this.board.get(3)),
        new Cell(0, 20, this.myGreen, false, null, this.board.get(0), this.board.get(3), null),
        new Cell(20, 20, this.myRed, false, this.board.get(2), this.board.get(1), null, null))));

    setup();
    u.instantiateCells(3, rand, board);
    t.checkExpect(this.board,
        new ArrayList<Cell>(Arrays.asList(new Cell(0, 0, this.myBlue, true),
            new Cell(20, 0, this.myGreen, false), new Cell(40, 0, this.myBlue, false),
            new Cell(0, 20, this.myGreen, false), new Cell(20, 20, this.myGreen, false),
            new Cell(40, 20, this.myGreen, false), new Cell(0, 40, this.myGreen, false),
            new Cell(20, 40, this.myGreen, false), new Cell(40, 40, this.myGreen, false))));
    u.adjustDirection(3, board);
    t.checkExpect(this.board.get(0),
        new Cell(0, 0, this.myBlue, true, null, null, this.board.get(1), this.board.get(3)));
    t.checkExpect(this.board.get(1), new Cell(20, 0, this.myGreen, false, this.board.get(0), null,
        this.board.get(2), this.board.get(4)));
    t.checkExpect(this.board.get(2),
        new Cell(40, 0, this.myBlue, false, this.board.get(1), null, null, this.board.get(5)));
    t.checkExpect(this.board.get(3), new Cell(0, 20, this.myGreen, false, null, this.board.get(0),
        this.board.get(4), this.board.get(6)));
    t.checkExpect(this.board.get(4), new Cell(20, 20, this.myGreen, false, this.board.get(3),
        this.board.get(1), this.board.get(5), this.board.get(7)));
    t.checkExpect(this.board.get(5), new Cell(40, 20, this.myGreen, false, this.board.get(4),
        this.board.get(2), null, this.board.get(8)));
    t.checkExpect(this.board.get(6),
        new Cell(0, 40, this.myGreen, false, null, this.board.get(3), this.board.get(7), null));
    t.checkExpect(this.board.get(7), new Cell(20, 40, this.myGreen, false, this.board.get(6),
        this.board.get(4), this.board.get(8), null));
    t.checkExpect(this.board.get(8),
        new Cell(40, 40, this.myGreen, false, this.board.get(7), this.board.get(5), null, null));
    t.checkExpect(this.board, new ArrayList<Cell>(Arrays.asList(
        new Cell(0, 0, this.myBlue, true, null, null, this.board.get(1), this.board.get(3)),
        new Cell(20, 0, this.myGreen, false, this.board.get(0), null, this.board.get(2),
            this.board.get(4)),
        new Cell(40, 0, this.myBlue, false, this.board.get(1), null, null, this.board.get(5)),
        new Cell(0, 20, this.myGreen, false, null, this.board.get(0), this.board.get(4),
            this.board.get(6)),
        new Cell(20, 20, this.myGreen, false, this.board.get(3), this.board.get(1),
            this.board.get(5), this.board.get(7)),
        new Cell(40, 20, this.myGreen, false, this.board.get(4), this.board.get(2), null,
            this.board.get(8)),
        new Cell(0, 40, this.myGreen, false, null, this.board.get(3), this.board.get(7), null),
        new Cell(20, 40, this.myGreen, false, this.board.get(6), this.board.get(4),
            this.board.get(8), null),
        new Cell(40, 40, this.myGreen, false, this.board.get(7), this.board.get(5), null, null))));

  }

}
