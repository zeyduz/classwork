import java.util.function.Predicate;
import tester.Tester;

class NumOver50 implements Predicate<Integer> {

  @Override
  public boolean test(Integer t) {
    return t > 50;
  }
}

class LengthOver300 implements Predicate<String> {

  @Override
  public boolean test(String t) {
    return t.length() > 300;
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  void updatePrev(ANode<T> newPrev) {
    this.prev = newPrev;
  }

  void updateNext(ANode<T> newNext) {
    this.next = newNext;
  }

  int sizeHelper() {
    return 0;
  }

  T remove() {
    return null;
  }

  ANode<T> findHelper(Predicate<T> pred) {
    return this;
  }

  void removeNodeHelper() {
    return;
  }

}

class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    super(null, null);
    this.data = data;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.data = data;
    if (next == null || prev == null) {
      throw new IllegalArgumentException("The given ANode cannot be null.");
    }
    next.updatePrev(this);
    prev.updateNext(this);
  }

  @Override
  int sizeHelper() {
    return 1 + this.next.sizeHelper();
  }

  @Override
  T remove() {
    this.prev.updateNext(this.next);
    this.next.updatePrev(this.prev);
    return this.data;
  }

  @Override
  ANode<T> findHelper(Predicate<T> pred) {
    if (pred.test(this.data)) {
      return this;
    }
    return this.next.findHelper(pred);
  }

  @Override
  void removeNodeHelper() {
    this.next.updatePrev(this.prev);
    this.prev.updateNext(this.next);
  }
}

class Sentinel<T> extends ANode<T> {

  Sentinel() {
    super(null, null);
    this.updateNext(this);
    this.updatePrev(this);
  }

  int size() {
    return this.next.sizeHelper();
  }

  // sizeHelper() is already in ANode

  void addHelper(T data, ANode<T> next, ANode<T> prev) {
    new Node<T>(data, next, prev);
  }

  @Override
  T remove() {
    throw new RuntimeException("Cannot remove from an empty list.");
  }

  @Override
  void removeNodeHelper() {
    return;
  }
}

class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  int size() {
    return this.header.size();
  }

  void addAtHead(T data) {
    this.header.addHelper(data, this.header.next, this.header);
  }

  void addAtTail(T data) {
    this.header.addHelper(data, this.header, this.header.prev);
  }

  T removeFromHead() {
    return this.header.next.remove();
  }

  T removeFromTail() {
    return this.header.prev.remove();
  }

  ANode<T> find(Predicate<T> pred) {
    return this.header.next.findHelper(pred);
  }

  void removeNode(ANode<T> node) {
    node.removeNodeHelper();
  }
}

class ExamplesDeque {

  // -------------EXAMPLE 1-------------//
  Deque<String> deque1;

  // -------------EXAMPLE 2-------------//
  Sentinel<String> abcSent;

  Node<String> abc;
  Node<String> bcd;
  Node<String> cde;
  Node<String> def;

  Deque<String> deque2;

  // -----EXAMPLE 3------//
  Sentinel<Integer> deque3Sent;

  Node<Integer> node3_18;
  Node<Integer> node3_1;
  Node<Integer> node3_100;
  Node<Integer> node3_69;
  Node<Integer> node3_42;

  Deque<Integer> deque3;

  void testAndSetup(Tester t) {
    // -------------EXAMPLE 1-------------//
    this.deque1 = new Deque<String>();

    // -------------EXAMPLE 2-------------//
    this.abcSent = new Sentinel<String>();
    t.checkExpect(this.abcSent.next, this.abcSent);
    t.checkExpect(this.abcSent.prev, this.abcSent);

    this.abc = new Node<String>("abc", this.abcSent, this.abcSent);
    t.checkExpect(this.abcSent.next, this.abc);
    t.checkExpect(this.abcSent.prev, this.abc);
    t.checkExpect(this.abc.next, this.abcSent);
    t.checkExpect(this.abc.prev, this.abcSent);

    this.bcd = new Node<String>("bcd", this.abcSent, this.abc);
    t.checkExpect(this.abcSent.next, this.abc);
    t.checkExpect(this.abcSent.prev, this.bcd);
    t.checkExpect(this.abc.next, this.bcd);
    t.checkExpect(this.abc.prev, this.abcSent);
    t.checkExpect(this.bcd.next, this.abcSent);
    t.checkExpect(this.bcd.prev, this.abc);

    this.cde = new Node<String>("cde", this.abcSent, this.bcd);
    t.checkExpect(this.abcSent.next, this.abc);
    t.checkExpect(this.abcSent.prev, this.cde);
    t.checkExpect(this.abc.next, this.bcd);
    t.checkExpect(this.abc.prev, this.abcSent);
    t.checkExpect(this.bcd.next, this.cde);
    t.checkExpect(this.bcd.prev, this.abc);
    t.checkExpect(this.cde.next, this.abcSent);
    t.checkExpect(this.cde.prev, this.bcd);

    this.def = new Node<String>("def", this.abcSent, this.cde);
    t.checkExpect(this.abcSent.next, this.abc);
    t.checkExpect(this.abcSent.prev, this.def);
    t.checkExpect(this.abc.next, this.bcd);
    t.checkExpect(this.abc.prev, this.abcSent);
    t.checkExpect(this.bcd.next, this.cde);
    t.checkExpect(this.bcd.prev, this.abc);
    t.checkExpect(this.cde.next, this.def);
    t.checkExpect(this.cde.prev, this.bcd);
    t.checkExpect(this.def.next, this.abcSent);
    t.checkExpect(this.def.prev, this.cde);

    this.deque2 = new Deque<String>(this.abcSent);
    t.checkExpect(this.deque2.header, this.abcSent);

    // -------------EXAMPLE 3-------------//
    deque3Sent = new Sentinel<Integer>();
    t.checkExpect(this.deque3Sent.next, this.deque3Sent);
    t.checkExpect(this.deque3Sent.prev, this.deque3Sent);

    node3_18 = new Node<Integer>(18, this.deque3Sent, this.deque3Sent);
    t.checkExpect(this.deque3Sent.next, this.node3_18);
    t.checkExpect(this.deque3Sent.prev, this.node3_18);
    t.checkExpect(this.node3_18.next, this.deque3Sent);
    t.checkExpect(this.node3_18.prev, this.deque3Sent);

    node3_1 = new Node<Integer>(1, this.deque3Sent, this.node3_18);
    t.checkExpect(this.deque3Sent.next, this.node3_18);
    t.checkExpect(this.deque3Sent.prev, this.node3_1);
    t.checkExpect(this.node3_18.next, this.node3_1);
    t.checkExpect(this.node3_18.prev, this.deque3Sent);
    t.checkExpect(this.node3_1.next, this.deque3Sent);
    t.checkExpect(this.node3_1.prev, this.node3_18);

    node3_100 = new Node<Integer>(100, this.deque3Sent, this.node3_1);
    t.checkExpect(this.deque3Sent.next, this.node3_18);
    t.checkExpect(this.deque3Sent.prev, this.node3_100);
    t.checkExpect(this.node3_18.next, this.node3_1);
    t.checkExpect(this.node3_18.prev, this.deque3Sent);
    t.checkExpect(this.node3_1.next, this.node3_100);
    t.checkExpect(this.node3_1.prev, this.node3_18);
    t.checkExpect(this.node3_100.next, this.deque3Sent);
    t.checkExpect(this.node3_100.prev, this.node3_1);

    node3_69 = new Node<Integer>(69, this.deque3Sent, this.node3_100);
    t.checkExpect(this.deque3Sent.next, this.node3_18);
    t.checkExpect(this.deque3Sent.prev, this.node3_69);
    t.checkExpect(this.node3_18.next, this.node3_1);
    t.checkExpect(this.node3_18.prev, this.deque3Sent);
    t.checkExpect(this.node3_1.next, this.node3_100);
    t.checkExpect(this.node3_1.prev, this.node3_18);
    t.checkExpect(this.node3_100.next, this.node3_69);
    t.checkExpect(this.node3_100.prev, this.node3_1);
    t.checkExpect(this.node3_69.next, this.deque3Sent);
    t.checkExpect(this.node3_69.prev, this.node3_100);

    node3_42 = new Node<Integer>(42, this.deque3Sent, this.node3_69);
    t.checkExpect(this.deque3Sent.next, this.node3_18);
    t.checkExpect(this.deque3Sent.prev, this.node3_42);
    t.checkExpect(this.node3_18.next, this.node3_1);
    t.checkExpect(this.node3_18.prev, this.deque3Sent);
    t.checkExpect(this.node3_1.next, this.node3_100);
    t.checkExpect(this.node3_1.prev, this.node3_18);
    t.checkExpect(this.node3_100.next, this.node3_69);
    t.checkExpect(this.node3_100.prev, this.node3_1);
    t.checkExpect(this.node3_69.next, this.node3_42);
    t.checkExpect(this.node3_69.prev, this.node3_100);
    t.checkExpect(this.node3_42.next, this.deque3Sent);
    t.checkExpect(this.node3_42.prev, this.node3_69);

    deque3 = new Deque<Integer>(this.deque3Sent);
    t.checkExpect(this.deque3.header, this.deque3Sent);
  }

  void testDequeSize(Tester t) {
    testAndSetup(t);
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);
  }

  void testSentinelSize(Tester t) {
    testAndSetup(t);
    t.checkExpect(new Sentinel<Boolean>().size(), 0);
    t.checkExpect(this.abcSent.size(), 4);
    t.checkExpect(this.deque3Sent.size(), 5);
  }

  void testSizeHelper(Tester t) {
    testAndSetup(t);
    t.checkExpect(this.deque1.header.sizeHelper(), 0);
    t.checkExpect(this.abcSent.sizeHelper(), 0);
    t.checkExpect(this.deque3Sent.sizeHelper(), 0);
    t.checkExpect(
        new Node<Integer>(4, new Sentinel<Integer>(), new Sentinel<Integer>()).sizeHelper(), 1);
    t.checkExpect(new Node<Boolean>(false,
        new Node<Boolean>(true, new Sentinel<Boolean>(), new Sentinel<Boolean>()),
        new Sentinel<Boolean>()).sizeHelper(), 2);
  }

  void testAddAtHead(Tester t) {
    testAndSetup(t);
    this.deque1.addAtHead("Zey");
    t.checkExpect(this.deque1.header.next,
        new Node<String>("Zey", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.prev,
        new Node<String>("Zey", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.next.next, this.deque1.header);
    t.checkExpect(this.deque1.header.prev.prev, this.deque1.header);

    this.deque2.addAtHead("aab");
    t.checkExpect(this.deque2.header.next, new Node<String>("aab", new Node<String>("abc",
        new Node<String>("bcd", new Node<String>("cde",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header),
        this.deque2.header), this.deque2.header));
  }

  void testAddHelper(Tester t) {
    testAndSetup(t);

    this.deque1.header.addHelper("Zey", this.deque1.header.next, this.deque1.header);
    t.checkExpect(this.deque1.header.next,
        new Node<String>("Zey", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.prev,
        new Node<String>("Zey", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.next.next, this.deque1.header);
    t.checkExpect(this.deque1.header.prev.prev, this.deque1.header);

    this.deque2.header.addHelper("aab", this.deque2.header.next, this.deque2.header);
    t.checkExpect(this.deque2.header.next, new Node<String>("aab", new Node<String>("abc",
        new Node<String>("bcd", new Node<String>("cde",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header),
        this.deque2.header), this.deque2.header));

    this.deque3.header.addHelper(-1, this.deque3.header, this.deque3.header.prev);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(18,
            new Node<Integer>(1, new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, new Node<Integer>(-1, this.deque3.header, this.deque3.header),
                    this.deque3.header),
                this.deque3.header), this.deque3.header), this.deque3.header),
            this.deque3.header));
  }

  void testAddAtTail(Tester t) {
    testAndSetup(t);
    this.deque1.addAtTail("Alton");
    t.checkExpect(this.deque1.header.next,
        new Node<String>("Alton", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.prev,
        new Node<String>("Alton", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header.next.next, this.deque1.header);
    t.checkExpect(this.deque1.header.prev.prev, this.deque1.header);

    this.deque2.addAtTail("efg");
    t.checkExpect(this.deque2.header.next, new Node<String>("abc", new Node<String>("bcd",
        new Node<String>("cde", new Node<String>("def",
            new Node<String>("efg", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header),
        this.deque2.header), this.deque2.header));

  }

  void testRemoveFromHead(Tester t) {
    testAndSetup(t);
    t.checkException(new RuntimeException("Cannot remove from an empty list."), this.deque1,
        "removeFromHead");

    t.checkExpect(this.deque2.removeFromHead(), "abc");
    t.checkExpect(this.deque2.header.next,
        new Node<String>("bcd", new Node<String>("cde",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header));

    t.checkExpect(this.deque3.removeFromHead(), 18);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(1,
            new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header),
            this.deque3.header));
  }

  void testRemoveFromTail(Tester t) {
    testAndSetup(t);
    t.checkException(new RuntimeException("Cannot remove from an empty list."), this.deque1,
        "removeFromTail");

    t.checkExpect(this.deque3.removeFromTail(), 42);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(18,
            new Node<Integer>(1, new Node<Integer>(100,
                new Node<Integer>(69, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header),
            this.deque3.header));
  }

  void testRemove(Tester t) {
    testAndSetup(t);
    t.checkException(new RuntimeException("Cannot remove from an empty list."), this.deque1.header,
        "remove");

    // Remove from the head
    t.checkExpect(this.deque2.header.next.remove(), "abc");
    t.checkExpect(this.deque2.header.next,
        new Node<String>("bcd", new Node<String>("cde",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header));

    // Remove from the head
    t.checkExpect(this.deque3.header.next.remove(), 18);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(1,
            new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header),
            this.deque3.header));

    // Remove from the tail
    t.checkExpect(this.deque3.header.prev.remove(), 42);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(1, new Node<Integer>(100,
            new Node<Integer>(69, this.deque3.header, this.deque3.header), this.deque3.header),
            this.deque3.header));
  }

  void testFind(Tester t) {
    testAndSetup(t);
    t.checkExpect(this.deque1.find(new LengthOver300()), this.deque1.header);
    t.checkExpect(this.deque2.find(new LengthOver300()), this.deque2.header);
    t.checkExpect(this.deque3.find(new NumOver50()), this.deque3.header.next.next.next);
  }

  void testRemoveNode(Tester t) {
    testAndSetup(t);

    this.deque1.removeNode(new Node<String>("sup", this.deque1.header, this.deque1.header));
    t.checkExpect(this.deque1.header, new Sentinel<String>());

    this.deque1.removeNode(this.deque1.header);
    t.checkExpect(this.deque1.header, new Sentinel<String>());

    this.deque2.removeNode(this.cde);
    t.checkExpect(this.deque2.header.next,
        new Node<String>("abc", new Node<String>("bcd",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header));

    this.deque2.removeNode(this.deque2.header);
    t.checkExpect(this.deque2.header.next,
        new Node<String>("abc", new Node<String>("bcd",
            new Node<String>("def", this.deque2.header, this.deque2.header), this.deque2.header),
            this.deque2.header));

    this.deque3.removeNode(this.node3_18);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(1,
            new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header),
            this.deque3.header));
    
    this.deque3.removeNode(this.node3_18);
    t.checkExpect(this.deque3.header.next,
        new Node<Integer>(1,
            new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header),
            this.deque3.header));
    
    this.deque3.removeNode(this.node3_1);
    t.checkExpect(this.deque3.header.next,
            new Node<Integer>(100, new Node<Integer>(69,
                new Node<Integer>(42, this.deque3.header, this.deque3.header), this.deque3.header),
                this.deque3.header));
  }
  
  //TODO
  void testRemoveNodeHelper(Tester t) {
    testAndSetup(t);
  }
}
