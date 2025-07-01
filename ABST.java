import tester.Tester;

interface IList<T> {}

class MtList<T> implements IList<T> {

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
}

interface Comparator<T> {
  int compare(T t1, T t2);
}

class BooksByTitle implements Comparator<Book> {

  public int compare(Book t1, Book t2) {
    return t1.title.compareTo(t2.title);
  }

}

class BooksByAuthor implements Comparator<Book> {

  public int compare(Book t1, Book t2) {
    return t1.author.compareTo(t2.author);
  }

}

class BooksByPrice implements Comparator<Book> {

  public int compare(Book t1, Book t2) {
    if (t1.price < t2.price) {
      return -1;
    } else if (t1.price == t2.price) {
      return 0;
    }
    return 1;
  }
}

abstract class ABST<T> {
  Comparator<T> order;

  ABST(Comparator<T> order) {
    this.order = order;
  }
  
  abstract ABST<T> insert(T item);
  
  abstract boolean present(T item);
  
  abstract T getLeftMostAux(T currentLeftMost);
  
  abstract T getLeftMost();
  
  abstract ABST<T> getRight();
  
  abstract ABST<T> getRightAux(Node<T> prevNode);
  
  abstract boolean sameTree(ABST<T> other);
  
  abstract boolean sameNode(Node<T> other);
  
  abstract boolean sameLeaf(Leaf<T> other);
  
  abstract boolean sameData(ABST<T> other);
  
  abstract boolean sameDataHelper(ABST<T> other);
  
  abstract IList<T> buildList();

}

class Leaf<T> extends ABST<T> {

  Leaf(Comparator<T> order) {
    super(order);
  }

  ABST<T> insert(T item) {
    return new Node<T>(this.order, item, new Leaf<T>(this.order), new Leaf<T>(this.order));
  }

  boolean present(T item) {
    return false;
  }

  T getLeftMostAux(T currentLeftMost) {
    return currentLeftMost;
  }

  T getLeftMost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  ABST<T> getRightAux(Node<T> prevNode) {
    return prevNode;
  }
  
  @Override
  boolean sameTree(ABST<T> other) {
    return other.sameLeaf(this);
  }
  
  @Override
  boolean sameNode(Node<T> other) {
    return false;
  }
  
  @Override
  boolean sameLeaf(Leaf<T> other) {
    return true;
  }
  
  @Override
  boolean sameData(ABST<T> other) {
    return other.sameLeaf(this);
  }
  
  @Override
  boolean sameDataHelper(ABST<T> other) {
    return true;
  }

  @Override
  IList<T> buildList() {
    return new MtList<T>();
  }
  
  
}

class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  Node(Comparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  @Override
  ABST<T> insert(T item) {
    if (this.order.compare(item, this.data) == 0) {
      return new Node<T>(this.order, this.data, this.left,
          new Node<T>(this.order, item, new Leaf<T>(this.order), this.right));
    } else if (this.order.compare(item, this.data) < 0) {
      return new Node<T>(this.order, this.data, this.left.insert(item), this.right);
    }
    return new Node<T>(this.order, this.data, this.left, this.right.insert(item));
  }

  @Override
  boolean present(T item) {
    return (this.order.compare(this.data, item) == 0) || this.left.present(item) || this.right.present(item);
  }

  T getLeftMost() {
    return this.getLeftMostAux(this.data);
  }

  @Override
  T getLeftMostAux(T currentLeftMost) {
    return this.left.getLeftMostAux(this.data);
  }

  @Override
  ABST<T> getRight() {
    if ((this.order.compare(this.getLeftMost(), this.data) == 0) && (!this.left.present(this.getLeftMost()))) {
      return this.right;
    }
    return new Node<T>(this.order, this.data, this.left.getRightAux(this), this.right);
  }

  @Override
  ABST<T> getRightAux(Node<T> prevNode) {
    if (this.order.compare(this.data, this.getLeftMost()) == 0 && (!this.left.present(this.getLeftMost()))) {
      return this.right;
    }
    return new Node<T>(this.order, this.data, this.left.getRightAux(this), this.right);
  }
  
  @Override
  boolean sameTree(ABST<T> other) {
    return other.sameNode(this);
  }
  
  @Override
  boolean sameNode(Node<T> other) {
    return this.order.compare(this.data, other.data) == 0
      && this.left.sameTree(other.left)
      && this.right.sameTree(other.right);
  }
  
  @Override
  boolean sameLeaf(Leaf<T> other) {
    return false;
  }
  
  @Override
  boolean sameData(ABST<T> other) {
    return this.sameDataHelper(other);
  }

  @Override
  boolean sameDataHelper(ABST<T> other) {
    return other.present(this.data)
        && this.left.sameDataHelper(other)
        && this.right.sameDataHelper(other);
  }
  
  @Override
  IList<T> buildList() {
    return new ConsList<T>(this.getLeftMost(), this.getRight().buildList());
  }

}

class Book {
  String title;
  String author;
  int price;

  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }

  int compareBy(Comparator<Book> c, Book other) {
    return c.compare(this, other);
  }

}

class ExamplesABST {

  // Example Books
  Book book1 = new Book("Spanish Love Deception", "Elena Armas", 12);
  Book book2 = new Book("Twilight", "Stephenie Meyer", 20);
  Book book3 = new Book("Pet Cemetary", "Stephen King", 18);
  Book book4 = new Book("The Giver", "Lois Lowry", 8);
  Book book5 = new Book("50 Shades of Gray", "E. L. James", 18);
  Book book6 = new Book("Zombies", "Yammy Doe", 200);
  Book book7 = new Book("Green Eggs and Ham", "Dr. Seuss", 10);
  Book book8 = new Book("ABC", "Alphabet", 19);

  // Examples of Book BST
  ABST<Book> leafTitle = new Leaf<Book>(new BooksByTitle());
  ABST<Book> leafAuthor = new Leaf<Book>(new BooksByAuthor());
  ABST<Book> leafPrice = new Leaf<Book>(new BooksByPrice());
  Leaf<Book> leafTitle2 = new Leaf<Book>(new BooksByTitle());
  Leaf<Book> leafAuthor2 = new Leaf<Book>(new BooksByAuthor());
  Leaf<Book> leafPrice2 = new Leaf<Book>(new BooksByPrice());

  // sorted by title
  // the individual nodes
  ABST<Book> treeTitle1 = new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.leafTitle);
  ABST<Book> treeTitle2 = new Node<Book>(new BooksByTitle(), this.book2, this.leafTitle, this.leafTitle);
  ABST<Book> treeTitle3 = new Node<Book>(new BooksByTitle(), this.book3, this.leafTitle, this.leafTitle);
  ABST<Book> treeTitle4 = new Node<Book>(new BooksByTitle(), this.book4, this.leafTitle, this.leafTitle);
  ABST<Book> treeTitle5 = new Node<Book>(new BooksByTitle(), this.book5, this.leafTitle, this.leafTitle);

  ABST<Book> treeTitle6 = new Node<Book>(new BooksByTitle(), this.book2, this.treeTitle4, this.treeTitle5);
  // Right-leaning tree sorted by title
  ABST<Book> treeTitle7 = new Node<Book>(new BooksByTitle(), this.book1, this.treeTitle3, this.treeTitle6);
  // This tree has a leftmost element that has right elements
  ABST<Book> treeTitle8 = new Node<Book>(new BooksByTitle(), this.book2,
      new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.treeTitle4), this.treeTitle5);

  // Example Lists
  IList<Book> mt = new MtList<Book>();
  IList<Book> list1 = new ConsList<Book>(this.book3, new ConsList<Book>(this.book1,
      new ConsList<Book>(this.book4, new ConsList<Book>(this.book2, new ConsList<Book>(this.book5, this.mt)))));
  IList<Book> list2 = new ConsList<Book>(this.book5, new ConsList<Book>(this.book1,
      new ConsList<Book>(this.book4, new ConsList<Book>(this.book3, new ConsList<Book>(this.book2, this.mt)))));
  IList<Book> list3 = new ConsList<Book>(this.book4, new ConsList<Book>(this.book1,
      new ConsList<Book>(this.book3, new ConsList<Book>(this.book2, this.mt))));
  
  boolean testBuildList(Tester t) {
    return t.checkExpect(this.leafAuthor.buildList(), this.mt)
        && t.checkExpect(this.treeTitle1.buildList(), new ConsList<Book>(this.book1, this.mt))
        && t.checkExpect(this.treeTitle7.buildList(), this.list1);
  }
  
  // TODO
  boolean testGetRightTitleAux(Tester t) {
    return t.checkExpect(this.treeTitle1.getRightAux(
        new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.leafTitle)), this.leafTitle)
        && t.checkExpect(
            this.leafTitle.getRightAux(
                new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.leafTitle)),
            new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.leafTitle));
  }

  // sorted by author
  // the individual nodes
  ABST<Book> treeAuthor1 = new Node<Book>(new BooksByAuthor(), this.book1, this.leafAuthor, this.leafAuthor);
  ABST<Book> treeAuthor2 = new Node<Book>(new BooksByAuthor(), this.book2, this.leafAuthor, this.leafAuthor);
  ABST<Book> treeAuthor3 = new Node<Book>(new BooksByAuthor(), this.book3, this.leafAuthor, this.leafAuthor);
  ABST<Book> treeAuthor4 = new Node<Book>(new BooksByAuthor(), this.book4, this.leafAuthor, this.leafAuthor);
  ABST<Book> treeAuthor5 = new Node<Book>(new BooksByAuthor(), this.book5, this.leafAuthor, this.leafAuthor);

  ABST<Book> treeAuthor6 = new Node<Book>(new BooksByAuthor(), this.book4, this.treeAuthor5, this.treeAuthor2);
  ABST<Book> treeAuthor7 = new Node<Book>(new BooksByAuthor(), this.book1, this.treeAuthor5, this.leafAuthor);
  ABST<Book> treeAuthor8 = new Node<Book>(new BooksByAuthor(), this.book3, this.leafAuthor, this.treeAuthor2);
  // The tree with equal branches that is sorted by author
  ABST<Book> treeAuthor9 = new Node<Book>(new BooksByAuthor(), this.book4, this.treeAuthor7, this.treeAuthor8);

  // sorted by price
//the individual nodes
  ABST<Book> treePrice1 = new Node<Book>(new BooksByPrice(), this.book1, this.leafPrice, this.leafPrice);
  ABST<Book> treePrice2 = new Node<Book>(new BooksByPrice(), this.book2, this.leafPrice, this.leafPrice);
  ABST<Book> treePrice3 = new Node<Book>(new BooksByPrice(), this.book3, this.leafPrice, this.leafPrice);
  ABST<Book> treePrice4 = new Node<Book>(new BooksByPrice(), this.book4, this.leafPrice, this.leafPrice);
  ABST<Book> treePrice5 = new Node<Book>(new BooksByPrice(), this.book5, this.leafPrice, this.leafPrice);

  ABST<Book> treePrice6 = new Node<Book>(new BooksByPrice(), this.book1, this.treePrice4, this.leafPrice);
  // Left-leaning tree sorted by price
  ABST<Book> treePrice7 = new Node<Book>(new BooksByPrice(), this.book3, this.treePrice6, this.treePrice2);

  boolean testCompareBooksByTitle(Tester t) {
    return t.checkExpect(this.book1.compareBy(new BooksByTitle(), this.book1), 0)
        && t.checkExpect(this.book1.compareBy(new BooksByTitle(), this.book2), -1)
        && t.checkExpect(this.book1.compareBy(new BooksByTitle(), this.book3), 3)
        && t.checkExpect(this.book1.compareBy(new BooksByTitle(), this.book5), 30)
        && t.checkExpect(this.book2.compareBy(new BooksByTitle(), this.book1), 1)
        && t.checkExpect(this.book2.compareBy(new BooksByTitle(), this.book3), 4)
        && t.checkExpect(this.book3.compareBy(new BooksByTitle(), this.book4), -4)
        && t.checkExpect(this.book2.compareBy(new BooksByTitle(), this.book4), 15)
        && t.checkExpect(this.book4.compareBy(new BooksByTitle(), this.book2), -15);
  }

  boolean testCompareBooksByAuthor(Tester t) {
    return t.checkExpect(this.book1.compareBy(new BooksByAuthor(), this.book1), 0)
        && t.checkExpect(this.book2.compareBy(new BooksByAuthor(), this.book3), 73)
        && t.checkExpect(this.book3.compareBy(new BooksByAuthor(), this.book2), -73)
        && t.checkExpect(this.book1.compareBy(new BooksByAuthor(), this.book5), 62)
        && t.checkExpect(this.book5.compareBy(new BooksByAuthor(), this.book1), -62)
        && t.checkExpect(this.book3.compareBy(new BooksByAuthor(), this.book4), 7)
        && t.checkExpect(this.book1.compareBy(new BooksByAuthor(), this.book4), -7);
  }

  boolean testCompareBooksByPrice(Tester t) {
    return t.checkExpect(this.book1.compareBy(new BooksByPrice(), this.book1), 0)
        && t.checkExpect(this.book2.compareBy(new BooksByPrice(), this.book1), 1)
        && t.checkExpect(this.book1.compareBy(new BooksByPrice(), this.book2), -1)
        && t.checkExpect(this.book3.compareBy(new BooksByPrice(), this.book5), 0)
        && t.checkExpect(this.book5.compareBy(new BooksByPrice(), this.book3), 0)
        && t.checkExpect(this.book4.compareBy(new BooksByPrice(), this.book2), -1);
  }

  boolean testInsertByTitle(Tester t) {
    return t.checkExpect(this.treeTitle1.insert(this.book1),
        new Node<Book>(new BooksByTitle(), this.book1, new Leaf<Book>(new BooksByTitle()), this.treeTitle1))
        && t.checkExpect(this.treeTitle1.insert(this.book2),
            new Node<Book>(new BooksByTitle(), this.book1, new Leaf<Book>(new BooksByTitle()),
                this.treeTitle2))
        && t.checkExpect(this.treeTitle1.insert(this.book3),
            new Node<Book>(new BooksByTitle(), this.book1, this.treeTitle3,
                new Leaf<Book>(new BooksByTitle())))
        && t.checkExpect(this.treeTitle6.insert(this.book1),
            new Node<Book>(new BooksByTitle(), this.book2,
                new Node<Book>(new BooksByTitle(), this.book4, this.treeTitle1,
                    new Leaf<Book>(new BooksByTitle())),
                this.treeTitle5))
        && t.checkExpect(this.treeTitle7.insert(this.book3),
            new Node<Book>(new BooksByTitle(), this.book1,
                new Node<Book>(new BooksByTitle(), this.book3, new Leaf<Book>(new BooksByTitle()),
                    this.treeTitle3),
                this.treeTitle6))
        && t.checkExpect(this.treeTitle7.insert(this.book6), new Node<Book>(new BooksByTitle(), this.book1,
            this.treeTitle3,
            new Node<Book>(new BooksByTitle(), this.book2, this.treeTitle4, new Node<Book>(
                new BooksByTitle(), this.book5, new Leaf<Book>(new BooksByTitle()),
                new Node<Book>(new BooksByTitle(), this.book6, new Leaf<Book>(new BooksByTitle()),
                    new Leaf<Book>(new BooksByTitle()))))))
        && t.checkExpect(this.leafTitle.insert(this.book1),
            new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.leafTitle));
  }

  boolean testInsertByAuthor(Tester t) {
    return t.checkExpect(this.treeAuthor1.insert(this.book1),
        new Node<Book>(new BooksByAuthor(), this.book1, new Leaf<Book>(new BooksByAuthor()), this.treeAuthor1))
        && t.checkExpect(this.treeAuthor1.insert(this.book5),
            new Node<Book>(new BooksByAuthor(), this.book1, this.treeAuthor5, this.leafAuthor))
        && t.checkExpect(this.treeAuthor6.insert(this.book1),
            new Node<Book>(new BooksByAuthor(), this.book4,
                new Node<Book>(new BooksByAuthor(), this.book5, this.leafAuthor, this.treeAuthor1),
                this.treeAuthor2))
        && t.checkExpect(this.treeAuthor6.insert(this.book3),
            new Node<Book>(new BooksByAuthor(), this.book4, this.treeAuthor5,
                new Node<Book>(new BooksByAuthor(), this.book2, this.treeAuthor3, this.leafAuthor)))
        && t.checkExpect(this.treeAuthor6.insert(this.book4),
            new Node<Book>(new BooksByAuthor(), this.book4, this.treeAuthor5,
                new Node<Book>(new BooksByAuthor(), this.book4, this.leafAuthor, this.treeAuthor2)))
        && t.checkExpect(this.treeAuthor9.insert(this.book6),
            new Node<Book>(new BooksByAuthor(), this.book4, this.treeAuthor7,
                new Node<Book>(new BooksByAuthor(), this.book3, this.leafAuthor,
                    new Node<Book>(new BooksByAuthor(), this.book2, this.leafAuthor,
                        new Node<Book>(new BooksByAuthor(), this.book6, this.leafAuthor,
                            this.leafAuthor)))))
        && t.checkExpect(this.leafAuthor.insert(this.book1),
            new Node<Book>(new BooksByAuthor(), this.book1, this.leafAuthor, this.leafAuthor));
  }

  boolean testInsertByPrice(Tester t) {
    return t.checkExpect(this.leafPrice.insert(this.book1),
        new Node<Book>(new BooksByPrice(), this.book1, this.leafPrice, this.leafPrice))
        && t.checkExpect(this.treePrice1.insert(this.book1),
            new Node<Book>(new BooksByPrice(), this.book1, this.leafPrice, this.treePrice1))
        && t.checkExpect(this.treePrice6.insert(this.book3),
            new Node<Book>(new BooksByPrice(), this.book1, this.treePrice4, this.treePrice3))
        && t.checkExpect(this.treePrice7.insert(this.book2),
            new Node<Book>(new BooksByPrice(), this.book3, this.treePrice6,
                new Node<Book>(new BooksByPrice(), this.book2, this.leafPrice, this.treePrice2)))
        && t.checkExpect(this.treePrice7.insert(this.book4),
            new Node<Book>(new BooksByPrice(), this.book3,
                new Node<Book>(new BooksByPrice(), this.book1,
                    new Node<Book>(new BooksByPrice(), this.book4, this.leafPrice, this.treePrice4),
                    this.leafPrice),
                new Node<Book>(new BooksByPrice(), this.book2, this.leafPrice, this.leafPrice)))
        && t.checkExpect(this.treePrice7.insert(this.book1),
            new Node<Book>(new BooksByPrice(), this.book3,
                new Node<Book>(new BooksByPrice(), this.book1, this.treePrice4, this.treePrice1),
                this.treePrice2))
        && t.checkExpect(this.treePrice7.insert(this.book7), new Node<Book>(new BooksByPrice(), this.book3,
            new Node<Book>(new BooksByPrice(), this.book1,
                new Node<Book>(new BooksByPrice(), this.book4, this.leafPrice,
                    new Node<Book>(new BooksByPrice(), this.book7, this.leafPrice, this.leafPrice)),
                this.leafPrice),
            this.treePrice2))
        && t.checkExpect(this.treePrice7.insert(this.book3),
            new Node<Book>(new BooksByPrice(), this.book3, this.treePrice6,
                new Node<Book>(new BooksByPrice(), this.book3, this.leafPrice, this.treePrice2)))
        && t.checkExpect(this.treePrice7.insert(this.book8),
            new Node<Book>(new BooksByPrice(), this.book3, this.treePrice6,
                new Node<Book>(new BooksByPrice(), this.book2,
                    new Node<Book>(new BooksByPrice(), this.book8, this.leafPrice, this.leafPrice),
                    this.leafPrice)));
  }

  boolean testPresentTitle(Tester t) {
    return t.checkExpect(this.treeTitle1.present(this.book1), true)
        && t.checkExpect(this.treeTitle1.present(this.book2), false)
        && t.checkExpect(this.treeTitle2.present(this.book2), true)
        && t.checkExpect(this.treeTitle6.present(this.book4), true)
        && t.checkExpect(this.treeTitle6.present(this.book5), true)
        && t.checkExpect(this.treeTitle6.present(this.book1), false)
        && t.checkExpect(this.treeTitle7.present(this.book3), true)
        && t.checkExpect(this.treeTitle7.present(this.book6), false)
        && t.checkExpect(this.treeTitle7.present(this.book4), true)
        && t.checkExpect(this.leafTitle.present(this.book1), false);
  }

  boolean testPresentAuthor(Tester t) {
    return t.checkExpect(this.treeAuthor6.present(this.book1), false)
        && t.checkExpect(this.treeAuthor6.present(this.book4), true)
        && t.checkExpect(this.treeAuthor6.present(this.book5), true)
        && t.checkExpect(this.treeAuthor6.present(this.book3), false)
        && t.checkExpect(this.treeAuthor9.present(this.book2), true)
        && t.checkExpect(this.treeAuthor9.present(this.book6), false)
        && t.checkExpect(this.treeAuthor9.present(this.book5), true)
        && t.checkExpect(this.treeAuthor1.present(this.book4), false)
        && t.checkExpect(this.treeAuthor1.present(this.book1), true)
        && t.checkExpect(this.leafAuthor.present(this.book2), false);
  }

  boolean testPresentPrice(Tester t) {
    return t.checkExpect(this.treePrice1.present(this.book1), true)
        && t.checkExpect(this.treePrice1.present(this.book2), false)
        && t.checkExpect(this.treePrice7.present(this.book3), true)
        && t.checkExpect(this.treePrice7.present(this.book1), true)
        && t.checkExpect(this.treePrice7.present(this.book4), true)
        && t.checkExpect(this.treePrice6.present(this.book5), false)
        && t.checkExpect(this.leafPrice.present(book1), false);
  }
  
  // TODO
  boolean testGetLeftMostTitle(Tester t) {
    return t.checkExpect(this.treeTitle1.getLeftMost(), this.book1)
        && t.checkExpect(this.treeTitle2.getLeftMost(), this.book2);
  }
  // TODO
  boolean testGetLeftMostAuthor(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetLeftMostPrice(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetLeftMostAuxTitle(Tester t) {
    return true;
  }
  // TODO
  boolean testGetLeftMostAuxAuthor(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetLeftMostAuxPrice(Tester t) {
    return true;
  }
  
  boolean testGetRightTitle(Tester t) {
    return t.checkExpect(this.treeTitle1.getRight(), this.leafTitle)
        && t.checkExpect(this.treeTitle6.getRight(),
            new Node<Book>(new BooksByTitle(), this.book2, this.leafTitle, this.treeTitle5))
        && t.checkExpect(this.treeTitle7.getRight(),
            new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.treeTitle6))
        && t.checkExpect(
            new Node<Book>(new BooksByTitle(), this.book1, this.leafTitle, this.treeTitle6).getRight(),
            this.treeTitle6)
        && t.checkExpect(this.treeTitle8.getRight(),
            new Node<Book>(new BooksByTitle(), this.book2, this.treeTitle4, this.treeTitle5))
        && t.checkException(new RuntimeException("No right of an empty tree"), this.leafTitle, "getRight");
  }
  
  // TODO
  boolean testGetRightAuthor(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetRightPrice(Tester t) {
    return true;
  }
  
  boolean testGetRightAuxTitle(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetRightAuxAuthor(Tester t) {
    return true;
  }
  
  // TODO
  boolean testGetRightAuxPrice(Tester t) {
    return true;
  }
  
  // TODO some more
  boolean testSameTree(Tester t) {
      return t.checkExpect(this.leafTitle2.sameTree(this.leafTitle2), true)
          && t.checkExpect(this.leafTitle2.sameTree(this.leafAuthor2), true)
          && t.checkExpect(this.treeAuthor1.sameTree(this.treeAuthor1), true)
          && t.checkExpect(this.treeAuthor6.sameTree(this.treeAuthor4), false)
          && t.checkExpect(this.treeAuthor2.sameTree(this.treeAuthor2), true)
          && t.checkExpect(this.treeAuthor3.sameTree(this.treePrice3), true)
          && t.checkExpect(this.treeTitle5.sameTree(this.treePrice7), false);
    }
  
  // TODO
  boolean testSameNode(Tester t) {
    return true;
  }
  
  // TODO
  boolean testSameLeaf(Tester t) {
    return true;
  }
  
  // TODO some more
  boolean testSameData(Tester t) {
    return t.checkExpect(this.leafTitle2.sameData(this.leafTitle2), true)
        && t.checkExpect(this.leafTitle.sameData(this.treeTitle2), false)
        && t.checkExpect(this.leafTitle2.sameData(this.leafAuthor2), true)
        && t.checkExpect(this.treeAuthor7.sameData(this.treeAuthor7), true)
        && t.checkExpect(this.treeAuthor7.sameData(this.treeAuthor8), false)
        && t.checkExpect(this.treeAuthor8.sameData(this.treeAuthor4), false);
  }
  
  // TODO some more
  boolean testSameDataHelper(Tester t) {
    return t.checkExpect(this.leafTitle2.sameDataHelper(this.leafTitle2), true)
        && t.checkExpect(this.leafTitle.sameDataHelper(this.treeTitle2), true)
        && t.checkExpect(this.leafTitle2.sameDataHelper(this.leafAuthor2), true)
        && t.checkExpect(this.treeAuthor7.sameDataHelper(this.treeAuthor7), true)
        && t.checkExpect(this.treeAuthor7.sameDataHelper(this.treeAuthor8), false)
        && t.checkExpect(this.treeAuthor8.sameDataHelper(this.treeAuthor4), false);
  }

}
