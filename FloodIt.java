import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.TextImage;

import java.awt.Color;

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board = new ArrayList<Cell>();

  // Used in floodCells();
  ArrayList<Cell> cellsToMakeFlooded = new ArrayList<Cell>();

  // Used in updateFloodColor();
  ArrayList<Cell> cellsToFlood = new ArrayList<Cell>();
  ArrayList<Cell> cellsToAdd = new ArrayList<Cell>();

  // Size (height and width) of the board
  static int BOARD_SIZE = 10;

  static int COLOR_CHOICES = 6;

  static int CELL_SIZE = 20;

  int movesLeft;
  
  int numFlooded = 1;

  WorldScene scene = new WorldScene(FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE,
      FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE);

  Color floodColor;
  Color prevFloodColor;
  
  boolean flooding;
  boolean gameOver = false;
  boolean won = false;

  Random random;

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
    this.random = rand;
    new Util().instantiateCells(size, this.random, this.board);
    new Util().adjustDirection(size, this.board);
    this.floodColor = this.board.get(0).color;
    this.movesLeft = (FloodItWorld.BOARD_SIZE - 10) + (FloodItWorld.COLOR_CHOICES * FloodItWorld.COLOR_CHOICES);
  }

  @Override
  public WorldScene makeScene() {
    this.addCells();
    this.addInfo();
    
    return scene;
  }

  @Override
  public void onTick() {
    if(!gameOver) {
      this.updateFloodedColor();
    }
  }

  // Add the cells to the scene
  void addCells() {
    for (Cell c : this.board) {
      c.draw(this.scene);
    }
  }

  // Add moves remaining to the scene
  void addInfo() {
    this.scene.placeImageXY(new TextImage(this.movesLeft + " moves remaining", 12, Color.black),
        FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE / 2, 10);
    
    if(new Util().allFlooded(this.board, this.floodColor)) {
      this.scene.placeImageXY(new TextImage("YOU WIN!", 20, Color.black),
          FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE / 2,
          FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE / 2);
      this.gameOver = true;
    } else if(this.movesLeft == 0 && !this.flooding) {
      this.scene.placeImageXY(new TextImage("YOU LOSE!", 20, Color.black),
          FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE / 2,
          FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE / 2);
      this.gameOver = true;
    }
  }

  void updateFloodedColor() {
    // If there are no current cells to be flooded
    if (this.cellsToFlood.size() == 0) {
      // If the flooded are currently the correct color, do nothing
      if (this.board.get(0).color.equals(this.floodColor)) {
        this.flooding = false;
        return;
      }
      // If the flooded are not the correct color
      else {
        this.cellsToFlood.add(this.board.get(0));
      }
    }

    // For each cell in the cells to flood:
    for (int i = 0; i < this.cellsToFlood.size(); i++) {
      // Change the color of the flooded cell to the flood color
      Cell current = this.cellsToFlood.get(i);
      current.color = this.floodColor;
      current.flooded = true;

      // If the cell above the current one is flooded, add it to the temp list
      if (current.hasTop() && 
          current.top.color.equals(this.prevFloodColor)) {
        this.cellsToAdd.add(current.top);
      }

      // If the cell to the right of the current one is flooded,
      // add it to the temp list
      if (current.hasRight() && 
          current.right.color.equals(this.prevFloodColor))  {
        this.cellsToAdd.add(current.right);
      }

      // If the cell below the current one is flooded,
      // add it to the temp list
      if (current.hasBottom() && 
          current.bottom.color.equals(this.prevFloodColor)) {
        this.cellsToAdd.add(current.bottom);
      }
 
      // If the cell to the left of the current one is flooded,
      // add it to the temp list
      if (current.hasLeft() && 
          current.left.color.equals(this.prevFloodColor)) {
        this.cellsToAdd.add(current.left);
      }
    }

    // Empty the cells to flood (since they were just flooded)
   this.cellsToFlood = new ArrayList<Cell>();
    
    // Add the cells in the temporary list to the cells to flood
    for (Cell c : this.cellsToAdd) {
      this.cellsToFlood.add(c);
      c.flooded = true;
    }

    // Empty the temporary list (since they were just added to the cells to flood)
    this.cellsToAdd = new ArrayList<Cell>();
    
  }

  @Override
  public void onMousePressed(Posn pos) {
    if(!this.flooding) {
      int indexOfClicked = new Util().posnToCellNum(pos);
      if (!this.board.get(indexOfClicked).flooded && this.movesLeft > 0) {
        this.prevFloodColor = this.floodColor;
        this.floodColor = this.board.get(indexOfClicked).color;
        this.movesLeft--;
        this.flooding = true;
      }
    }
  }

  @Override
  public void onKeyEvent(String s) {
    if (s.equals("r")) {
      this.board = new ArrayList<Cell>();
      new Util().instantiateCells(FloodItWorld.BOARD_SIZE, this.random, this.board);
      new Util().adjustDirection(FloodItWorld.BOARD_SIZE, this.board);
      this.floodColor = this.board.get(0).color;
      this.movesLeft = (FloodItWorld.BOARD_SIZE - 10) + (FloodItWorld.COLOR_CHOICES * FloodItWorld.COLOR_CHOICES);
      
      this.cellsToMakeFlooded = new ArrayList<Cell>();

      this.cellsToFlood = new ArrayList<Cell>();
      this.cellsToAdd = new ArrayList<Cell>();
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
    ArrayList<Color> colors = new ArrayList<Color>(
        Arrays.asList(new Color(255, 102, 99), new Color(158, 224, 158), new Color(158, 193, 207),
            new Color(253, 253, 151), new Color(204, 153, 201), new Color(254, 177, 68),
            new Color(250, 202, 202), new Color(153, 240, 235)));

    return colors.get(val);
  }

  // Returns the index of the cell that the posn is in
  int posnToCellNum(Posn p) {
    int x = p.x;
    int y = p.y;

    int column = x / FloodItWorld.CELL_SIZE;
    int row = y / FloodItWorld.CELL_SIZE;

    return (row * FloodItWorld.BOARD_SIZE) + column;
  }

  boolean allFlooded(ArrayList<Cell> board, Color floodColor) {
    for (Cell c : board) {
      if (!c.color.equals(floodColor)) {
        return false;
      }
    }
    return true;
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
    int size = 12;
    int colors = 5; // Maxes out at 8
    FloodItWorld world = new FloodItWorld(size, colors, new Random());

    world.bigBang(FloodItWorld.CELL_SIZE * FloodItWorld.BOARD_SIZE,
        FloodItWorld.CELL_SIZE * FloodItWorld.BOARD_SIZE, 1.0 / 56.0);
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