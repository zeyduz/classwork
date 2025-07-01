import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class Node {

  int x;
  int y;
  int name; // unique numerical identifier

  Node(int x, int y, int name) {
    this.x = x;
    this.y = y;
    this.name = name;
  }

}

class Edge {

  Node to;
  Node from;

  int weight;

  Edge(Node to, Node from, int weight) {
    this.to = to;
    this.from = from;
    this.weight = weight;
  }

}

class Maze extends World {

  // Used for setp
  ArrayList<ArrayList<Node>> nodes;
  ArrayList<Edge> edges;

  // Actual minimum spanning tree
  ArrayList<Edge> mst;

  int width;
  int height;

  static int CELL_SIZE = 20; // in pixels

  Maze() {
    this.nodes = new ArrayList<ArrayList<Node>>();
    this.edges = new ArrayList<Edge>();

    this.width = 2;
    this.height = 2;
  }

  Maze(int width, int height) {
    this.nodes = new ArrayList<ArrayList<Node>>();
    this.edges = new ArrayList<Edge>();

    this.width = width;
    this.height = height;

    // Instantiates every node that will be needed for the maze
    this.makeNodes();

    // Create edges between all of the adjacent nodes
    this.connectNodes();

    // Kruskalize
    this.kruskalize();
  }

  @Override
  public WorldScene makeScene() {
    return null;
  }

  // Instantiates a node for every "cell" in the graph
  // EFFECT: Adds nodes to this.nodes
  void makeNodes() {
    int count = 0;
    for (int y = 0; y < this.height; y++) {
      this.nodes.add(new ArrayList<Node>());
      for (int x = 0; x < this.width; x++) {
        this.nodes.get(y).add(new Node(x, y, count));
        count++;
      }
    }
  }

  // Create edges between each of the adjacent nodes
  // EFFECT: Adds edges to this.edges
  void connectNodes() {
    // First, make the horizontal connections
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width - 1; x++) {
        this.edges.add(new Edge(this.nodes.get(y).get(x), this.nodes.get(y).get(x + 1), 0));
      }
    }

    // Now, make the vertical connections
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height - 1; y++) {
        this.edges.add(new Edge(this.nodes.get(y).get(x), this.nodes.get(y + 1).get(x), 0));
      }
    }
  }

  // Returns a list of edges sorted by weight
  ArrayList<Edge> sortByWeight() {
    return null;
  }

  // Create a minimum spanning tree for the edges using Kruskal's Algorithm
  // EFFECT: Adds a minimum spanning tree to this.mst
  void kruskalize() {
    // Initialized the representatives of each node to be itself
    Map<Integer, Integer> representatives = new HashMap<Integer, Integer>();
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height; y++) {
        representatives.put(this.nodes.get(y).get(x).name, this.nodes.get(y).get(x).name);
      }
    }

    // All the edges in the graph, sorted by weight
    ArrayList<Edge> workList = new ArrayList<Edge>();
    workList = this.sortByWeight();

    // "While the nodes are not part of the same tree..."
    while (!new Util().sameTree(representatives, this.width, this.height)) {
      // code
    }
  }

}

class Util {

  // Determines if every representative points to the same value
  boolean sameTree(Map<Integer, Integer> representatives, int width, int height) {
    int commonRepresentative = representatives.get(0);

    // Keys range from [0, width * height)
    for (int key = 0; key < width * height; key++) {
      if (representatives.get(key) != commonRepresentative) {
        return false;
      }
    }

    return true;
  }

}

class ExamplesMaze {

  // Examples for Maze 1
  Maze maze1;

  void setupMaze1() {
    this.maze1 = new Maze(2, 2);
  }

  void testMakeNodes(Tester t) {
    // Automatically set to size 2x2
    Maze maze = new Maze();

    // Before
    t.checkExpect(maze.nodes, new ArrayList<ArrayList<Node>>());

    // Mutate
    maze.makeNodes();

    ArrayList<ArrayList<Node>> tempNodes = new ArrayList<ArrayList<Node>>();
    // Add the rows to tempNodes
    tempNodes.add(new ArrayList<Node>());
    tempNodes.add(new ArrayList<Node>());
    // Add the nodes to the rows
    tempNodes.get(0).add(new Node(0, 0, 0));
    tempNodes.get(0).add(new Node(1, 0, 1));
    tempNodes.get(1).add(new Node(0, 1, 2));
    tempNodes.get(1).add(new Node(1, 1, 3));

    // After
    t.checkExpect(maze.nodes, tempNodes);
  }

  void testConnectNodes(Tester t) {
    Maze maze = new Maze();
    maze.makeNodes();

    // Before
    t.checkExpect(maze.edges, new ArrayList<Edge>());

    // Mutate
    maze.connectNodes();

    ArrayList<Edge> tempEdges = new ArrayList<Edge>();
    // Add Horizontal Connections
    tempEdges.add(new Edge(new Node(0, 0, 0), new Node(1, 0, 1), 0));
    tempEdges.add(new Edge(new Node(0, 1, 2), new Node(1, 1, 3), 0));

    // Add Vertical Connections
    tempEdges.add(new Edge(new Node(0, 0, 0), new Node(0, 1, 2), 0));
    tempEdges.add(new Edge(new Node(1, 0, 1), new Node(1, 1, 3), 0));

    // After
    t.checkExpect(maze.edges, tempEdges);
  }

}
