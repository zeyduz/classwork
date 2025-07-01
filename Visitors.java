import java.util.function.BiFunction;
import java.util.function.Function;

import tester.Tester;

interface IArithVisitor<R> {
  R visitConst(Const c);

  R visitUnaryFormula(UnaryFormula uf);

  R visitBinaryFormula(BinaryFormula bf);
}

class EvalVisitor implements IArithVisitor<Double> {

  public Double apply(IArith t) {
    return t.accept(this);
  }

  public Double visitConst(Const c) {
    return c.num;
  }

  public Double visitUnaryFormula(UnaryFormula uf) {
    return uf.func.apply(uf.child.accept(this));
  }

  public Double visitBinaryFormula(BinaryFormula bf) {
    return bf.func.apply(bf.left.accept(this), bf.right.accept(this));
  }

}

class Negate implements Function<Double, Double> {

  public Double apply(Double t) {
    if (Math.abs(t) < 0.00000000000000000001)
      return 0.0;
    return -1.0 * t;
  }

}

class Square implements Function<Double, Double> {

  public Double apply(Double t) {
    return t * t;
  }

}

class Plus implements BiFunction<Double, Double, Double> {

  public Double apply(Double t, Double u) {
    return t + u;
  }

}

class Minus implements BiFunction<Double, Double, Double> {

  public Double apply(Double t, Double u) {
    return t - u;
  }

}

class Mul implements BiFunction<Double, Double, Double> {

  public Double apply(Double t, Double u) {
    return t * u;
  }

}

class Div implements BiFunction<Double, Double, Double> {

  public Double apply(Double t, Double u) {
    if (Math.abs(u) < 0.00000000000000000001) {
      throw new IllegalArgumentException("Cannot divide by zero");
    }
    return t / u;
  }

}

class PrintVisitor implements IArithVisitor<String> {

  public String apply(IArith t) {
    return t.accept(this);
  }

  public String visitConst(Const c) {
    return Double.toString(c.num);
  }

  public String visitUnaryFormula(UnaryFormula uf) {
    return "(" + uf.name + " " + uf.child.accept(this) + ")";
  }

  public String visitBinaryFormula(BinaryFormula bf) {
    return "(" + bf.name + " " + bf.left.accept(this) + " " + bf.right.accept(this) + ")";
  }

}

class DoublerVisitor implements IArithVisitor<IArith> {

  public IArith apply(IArith t) {
    return t.accept(this);
  }

  public IArith visitConst(Const c) {
    return new Const(c.num * 2.0);
  }

  public IArith visitUnaryFormula(UnaryFormula uf) {
    return new UnaryFormula(uf.func, uf.name, uf.child.accept(this));
  }

  public IArith visitBinaryFormula(BinaryFormula bf) {
    return new BinaryFormula(bf.func, bf.name, bf.left.accept(this), bf.right.accept(this));
  }

}

class NoNegativeResults implements IArithVisitor<Boolean> {

  public Boolean apply(IArith t) {
    return t.accept(this);
  }

  public Boolean visitConst(Const c) {
    return c.num >= 0;
  }

  public Boolean visitUnaryFormula(UnaryFormula uf) {
    return new StaysPositive().apply(uf) && uf.child.accept(this);
  }

  public Boolean visitBinaryFormula(BinaryFormula bf) {
    return new StaysPositive().apply(bf) && bf.left.accept(this) && bf.right.accept(this);
  }

}

class StaysPositive implements IArithVisitor<Boolean> {

  public Boolean apply(IArith t) {
    return t.accept(this);
  }

  public Boolean visitConst(Const c) {
    return c.num >= 0;
  }

  public Boolean visitUnaryFormula(UnaryFormula uf) {
    return uf.accept(new EvalVisitor()) >= 0;
  }

  public Boolean visitBinaryFormula(BinaryFormula bf) {
    return bf.accept(new EvalVisitor()) >= 0;
  }

}

interface IArith {
  <R> R accept(IArithVisitor<R> visitor);
}

class Const implements IArith {
  double num;

  Const(double num) {
    this.num = num;
  }

  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitConst(this);
  }
}

class UnaryFormula implements IArith {
  Function<Double, Double> func;
  String name;
  IArith child;

  UnaryFormula(Function<Double, Double> func, String name, IArith child) {
    this.func = func;
    this.name = name;
    this.child = child;
  }

  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitUnaryFormula(this);
  }
}

class BinaryFormula implements IArith {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith left;
  IArith right;

  BinaryFormula(BiFunction<Double, Double, Double> func, String name, IArith left, IArith right) {
    this.func = func;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitBinaryFormula(this);
  }
}

class ExamplesVisitors {
//--------------Constants--------------//
  IArith c1 = new Const(1.0);
  IArith c2 = new Const(2.0);
  IArith c5 = new Const(5.0);
  IArith c5_5 = new Const(5.5);
  IArith c10 = new Const(10.0);
  IArith c0 = new Const(0);
  IArith negC1 = new Const(-1.0);
  IArith negC2 = new Const(-2.0);
  IArith negC5 = new Const(-5.0);
  IArith negC5_5 = new Const(-5.5);
  IArith negC10 = new Const(-10.0);

  // --------------Unary Formulas--------------//
  // Negate a constant
  IArith uf1 = new UnaryFormula(new Negate(), "neg", this.c2); // -2
  IArith uf2 = new UnaryFormula(new Negate(), "neg", this.c0); // 0
  IArith uf3 = new UnaryFormula(new Negate(), "neg", this.negC2); // 2
  // Square a constant
  IArith uf4 = new UnaryFormula(new Square(), "sqr", this.c10); // 100
  IArith uf5 = new UnaryFormula(new Square(), "sqr", this.c0); // 0
  IArith uf6 = new UnaryFormula(new Square(), "sqr", this.negC10); // 100
  // Negate a negation
  IArith uf7 = new UnaryFormula(new Negate(), "neg", this.uf1); // 2
  IArith uf8 = new UnaryFormula(new Negate(), "neg", this.uf2); // 0
  IArith uf9 = new UnaryFormula(new Negate(), "neg", this.uf3); // -2
  // Negate a square
  IArith uf10 = new UnaryFormula(new Negate(), "neg", this.uf4); // -100
  IArith uf11 = new UnaryFormula(new Negate(), "neg", this.uf5); // 0
  IArith uf12 = new UnaryFormula(new Negate(), "neg", this.uf6); // -100
  // Square a negation
  IArith uf13 = new UnaryFormula(new Square(), "sqr", this.uf1); // 4
  IArith uf14 = new UnaryFormula(new Square(), "sqr", this.uf2); // 0
  IArith uf15 = new UnaryFormula(new Square(), "sqr", this.uf3); // 4
  // Square a square
  IArith uf16 = new UnaryFormula(new Square(), "sqr", this.uf4); // 10000
  IArith uf17 = new UnaryFormula(new Square(), "sqr", this.uf5); // 0
  IArith uf18 = new UnaryFormula(new Square(), "sqr", this.uf6); // 10000

  // --------------Binary Formulas--------------//
  IArith bf1 = new BinaryFormula(new Plus(), "plus", this.c1, this.c1); // 2
  IArith bf2 = new BinaryFormula(new Plus(), "plus", this.uf1, this.c2); // 0
  IArith bf3 = new BinaryFormula(new Plus(), "plus", this.c1, this.uf4); // 101
  IArith bf4 = new BinaryFormula(new Plus(), "plus", this.uf11, this.uf14); // 0
  IArith bf5 = new BinaryFormula(new Plus(), "plus", this.uf7, this.uf16); // 10002
  IArith bf6 = new BinaryFormula(new Plus(), "plus", this.bf3, this.bf1); // 103
  IArith bf7 = new BinaryFormula(new Minus(), "minus", this.c1, this.c1); // 0
  IArith bf8 = new BinaryFormula(new Minus(), "minus", this.c1, this.c0); // 1
  IArith bf9 = new BinaryFormula(new Minus(), "minus", this.c5_5, this.c5); // 0.5
  IArith bf10 = new BinaryFormula(new Minus(), "minus", this.c0, this.c1); // -1
  IArith bf11 = new BinaryFormula(new Minus(), "minus", this.c0, this.c0); // 0
  IArith bf12 = new BinaryFormula(new Minus(), "minus", this.uf7, this.uf9); // 4
  IArith bf13 = new BinaryFormula(new Minus(), "minus", this.uf7, this.c2); // 0
  IArith bf14 = new BinaryFormula(new Minus(), "minus", this.uf10, this.uf4); // -200
  IArith bf15 = new BinaryFormula(new Minus(), "minus", this.c0, this.bf3); // -101
  IArith bf16 = new BinaryFormula(new Minus(), "minus", this.bf10, this.bf9); // -1.5
  IArith bf17 = new BinaryFormula(new Mul(), "mul", this.c0, this.c0); // 0
  IArith bf18 = new BinaryFormula(new Mul(), "mul", this.c0, this.c5_5); // 0
  IArith bf19 = new BinaryFormula(new Mul(), "mul", this.negC5, this.c0); // 0
  IArith bf20 = new BinaryFormula(new Mul(), "mul", this.c0, this.bf14); // 0
  IArith bf21 = new BinaryFormula(new Mul(), "mul", this.uf1, this.uf3); // -4
  IArith bf22 = new BinaryFormula(new Mul(), "mul", this.c5_5, this.negC2); // -11
  IArith bf23 = new BinaryFormula(new Mul(), "mul", this.negC5_5, this.negC2); // 11
  IArith bf24 = new BinaryFormula(new Mul(), "mul", this.uf3, this.uf4); // 200
  IArith bf25 = new BinaryFormula(new Mul(), "mul", this.bf6, this.bf12); // 412
  IArith bf26 = new BinaryFormula(new Div(), "div", this.c0, this.c1); // 0
  IArith bf27 = new BinaryFormula(new Div(), "div", this.negC10, this.c2); // -5
  IArith bf28 = new BinaryFormula(new Div(), "div", this.negC5_5, this.negC5_5); // 1
  IArith bf29 = new BinaryFormula(new Div(), "div", this.c5_5, this.negC5_5); // -1
  IArith bf30 = new BinaryFormula(new Div(), "div", this.uf4, this.uf3); // 50
  IArith bf31 = new BinaryFormula(new Div(), "div", this.uf3, this.uf4); // 0.02;
  IArith bf32 = new BinaryFormula(new Div(), "div", this.bf6, this.bf12); // 25.75
  IArith bf33 = new BinaryFormula(new Div(), "div", this.uf4, this.bf16); // -66.66667

  void testConstEvalVisitor(Tester t) {
    t.checkInexact(this.c1.accept(new EvalVisitor()), 1.0, 0.001);
    t.checkInexact(this.c5_5.accept(new EvalVisitor()), 5.5, 0.001);
    t.checkInexact(this.c0.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.negC2.accept(new EvalVisitor()), -2.0, 0.001);

    t.checkInexact(new EvalVisitor().apply(this.c1), 1.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.c5_5), 5.5, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.c0), 0.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.negC2), -2.0, 0.0001);
  }

  void testUnaryEvalVisitor(Tester t) { // zey
    t.checkInexact(this.uf1.accept(new EvalVisitor()), -2.0, 0.001);
    t.checkInexact(this.uf2.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf3.accept(new EvalVisitor()), 2.0, 0.001);
    t.checkInexact(this.uf4.accept(new EvalVisitor()), 100.0, 0.001);
    t.checkInexact(this.uf5.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf6.accept(new EvalVisitor()), 100.0, 0.001);
    t.checkInexact(this.uf7.accept(new EvalVisitor()), 2.0, 0.001);
    t.checkInexact(this.uf8.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf9.accept(new EvalVisitor()), -2.0, 0.001);
    t.checkInexact(this.uf10.accept(new EvalVisitor()), -100.0, 0.001);
    t.checkInexact(this.uf11.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf12.accept(new EvalVisitor()), -100.0, 0.001);
    t.checkInexact(this.uf13.accept(new EvalVisitor()), 4.0, 0.001);
    t.checkInexact(this.uf14.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf15.accept(new EvalVisitor()), 4.0, 0.001);
    t.checkInexact(this.uf16.accept(new EvalVisitor()), 10000.0, 0.001);
    t.checkInexact(this.uf17.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.uf18.accept(new EvalVisitor()), 10000.0, 0.001);

    t.checkInexact(new EvalVisitor().apply(this.uf1), -2.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.uf2), 0.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.uf3), 2.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.uf4), 100.0, 0.0001);
    t.checkInexact(new EvalVisitor().apply(this.uf5), 0.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf6), 100.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf7), 2.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf8), 0.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf9), -2.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf10), -100.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf11), 0.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf12), -100.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf13), 4.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf14), 0.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf15), 4.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf16), 10000.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf17), 0.0, 0.001);
    t.checkInexact(new EvalVisitor().apply(this.uf18), 10000.0, 0.001);

  }

  void testBinaryPlusEvalVisitor(Tester t) {
    t.checkInexact(this.bf1.accept(new EvalVisitor()), 2.0, 0.001);
    t.checkInexact(this.bf2.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.bf3.accept(new EvalVisitor()), 101.0, 0.001);
    t.checkInexact(this.bf4.accept(new EvalVisitor()), 0.0, 0.001);
    t.checkInexact(this.bf5.accept(new EvalVisitor()), 10002.0, 0.001);
    t.checkInexact(this.bf6.accept(new EvalVisitor()), 103.0, 0.001);
  }

  void testBinaryMinusEvalVisitor(Tester t) {
    t.checkInexact(this.bf7.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf8.accept(new EvalVisitor()), 1.0, 0.01);
    t.checkInexact(this.bf9.accept(new EvalVisitor()), 0.5, 0.01);
    t.checkInexact(this.bf10.accept(new EvalVisitor()), -1.0, 0.01);
    t.checkInexact(this.bf11.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf12.accept(new EvalVisitor()), 4.0, 0.01);
    t.checkInexact(this.bf13.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf14.accept(new EvalVisitor()), -200.0, 0.01);
    t.checkInexact(this.bf15.accept(new EvalVisitor()), -101.0, 0.01);
    t.checkInexact(this.bf16.accept(new EvalVisitor()), -1.5, 0.01);
  }

  void testBinaryMulEvalVisitor(Tester t) {
    t.checkInexact(this.bf17.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf18.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf19.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf20.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf21.accept(new EvalVisitor()), -4.0, 0.01);
    t.checkInexact(this.bf22.accept(new EvalVisitor()), -11.0, 0.01);
    t.checkInexact(this.bf23.accept(new EvalVisitor()), 11.0, 0.01);
    t.checkInexact(this.bf24.accept(new EvalVisitor()), 200.0, 0.01);
    t.checkInexact(this.bf25.accept(new EvalVisitor()), 412.0, 0.01);
  }

  void testBinaryDivEvalVisitor(Tester t) {
    t.checkInexact(this.bf26.accept(new EvalVisitor()), 0.0, 0.01);
    t.checkInexact(this.bf27.accept(new EvalVisitor()), -5.0, 0.01);
    t.checkInexact(this.bf28.accept(new EvalVisitor()), 1.0, 0.01);
    t.checkInexact(this.bf29.accept(new EvalVisitor()), -1.0, 0.01);
    t.checkInexact(this.bf30.accept(new EvalVisitor()), 50.0, 0.01);
    t.checkInexact(this.bf31.accept(new EvalVisitor()), 0.02, 0.01);
    t.checkInexact(this.bf32.accept(new EvalVisitor()), 25.75, 0.01);
    t.checkInexact(this.bf33.accept(new EvalVisitor()), -66.666666667, 0.01);
    t.checkException(new IllegalArgumentException("Cannot divide by zero"),
        new BinaryFormula(new Div(), "div", this.c1, this.c0), "accept", new EvalVisitor());
  }

  void testConstPrintVisitor(Tester t) {
    t.checkExpect(this.c1.accept(new PrintVisitor()), "1.0");
    t.checkExpect(this.c2.accept(new PrintVisitor()), "2.0");
    t.checkExpect(this.c5.accept(new PrintVisitor()), "5.0");
    t.checkExpect(this.c5_5.accept(new PrintVisitor()), "5.5");
    t.checkExpect(this.c10.accept(new PrintVisitor()), "10.0");
    t.checkExpect(this.c0.accept(new PrintVisitor()), "0.0");
    t.checkExpect(this.negC1.accept(new PrintVisitor()), "-1.0");
    t.checkExpect(this.negC2.accept(new PrintVisitor()), "-2.0");
    t.checkExpect(this.negC5.accept(new PrintVisitor()), "-5.0");
    t.checkExpect(this.negC5_5.accept(new PrintVisitor()), "-5.5");
    t.checkExpect(this.negC10.accept(new PrintVisitor()), "-10.0");

    t.checkExpect(new PrintVisitor().apply(this.c1), "1.0");
    t.checkExpect(new PrintVisitor().apply(this.c2), "2.0");
    t.checkExpect(new PrintVisitor().apply(this.c5), "5.0");
    t.checkExpect(new PrintVisitor().apply(this.c5_5), "5.5");
    t.checkExpect(new PrintVisitor().apply(this.c10), "10.0");
    t.checkExpect(new PrintVisitor().apply(this.c0), "0.0");
    t.checkExpect(new PrintVisitor().apply(this.negC1), "-1.0");
    t.checkExpect(new PrintVisitor().apply(this.negC2), "-2.0");
    t.checkExpect(new PrintVisitor().apply(this.negC5), "-5.0");
    t.checkExpect(new PrintVisitor().apply(this.negC5_5), "-5.5");
    t.checkExpect(new PrintVisitor().apply(this.negC10), "-10.0");
  }

  void testUnaryPrintVisitor(Tester t) {
    t.checkExpect(this.uf1.accept(new PrintVisitor()), "(neg 2.0)");
    t.checkExpect(this.uf2.accept(new PrintVisitor()), "(neg 0.0)");
    t.checkExpect(this.uf3.accept(new PrintVisitor()), "(neg -2.0)");
    t.checkExpect(this.uf4.accept(new PrintVisitor()), "(sqr 10.0)");
    t.checkExpect(this.uf5.accept(new PrintVisitor()), "(sqr 0.0)");
    t.checkExpect(this.uf6.accept(new PrintVisitor()), "(sqr -10.0)");
    t.checkExpect(this.uf7.accept(new PrintVisitor()), "(neg (neg 2.0))");
    t.checkExpect(this.uf8.accept(new PrintVisitor()), "(neg (neg 0.0))");
    t.checkExpect(this.uf9.accept(new PrintVisitor()), "(neg (neg -2.0))");
    t.checkExpect(this.uf10.accept(new PrintVisitor()), "(neg (sqr 10.0))");
    t.checkExpect(this.uf11.accept(new PrintVisitor()), "(neg (sqr 0.0))");
    t.checkExpect(this.uf12.accept(new PrintVisitor()), "(neg (sqr -10.0))");
    t.checkExpect(this.uf13.accept(new PrintVisitor()), "(sqr (neg 2.0))");
    t.checkExpect(this.uf14.accept(new PrintVisitor()), "(sqr (neg 0.0))");
    t.checkExpect(this.uf15.accept(new PrintVisitor()), "(sqr (neg -2.0))");
    t.checkExpect(this.uf16.accept(new PrintVisitor()), "(sqr (sqr 10.0))");
    t.checkExpect(this.uf17.accept(new PrintVisitor()), "(sqr (sqr 0.0))");
    t.checkExpect(this.uf18.accept(new PrintVisitor()), "(sqr (sqr -10.0))");

    t.checkExpect(new PrintVisitor().apply(this.uf1), "(neg 2.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf2), "(neg 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf3), "(neg -2.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf4), "(sqr 10.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf5), "(sqr 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf6), "(sqr -10.0)");
    t.checkExpect(new PrintVisitor().apply(this.uf7), "(neg (neg 2.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf8), "(neg (neg 0.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf9), "(neg (neg -2.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf10), "(neg (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf11), "(neg (sqr 0.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf12), "(neg (sqr -10.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf13), "(sqr (neg 2.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf14), "(sqr (neg 0.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf15), "(sqr (neg -2.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf16), "(sqr (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf17), "(sqr (sqr 0.0))");
    t.checkExpect(new PrintVisitor().apply(this.uf18), "(sqr (sqr -10.0))");

  }

  void testBinaryPrintVisitor(Tester t) {
    t.checkExpect(this.bf1.accept(new PrintVisitor()), "(plus 1.0 1.0)");
    t.checkExpect(this.bf2.accept(new PrintVisitor()), "(plus (neg 2.0) 2.0)");
    t.checkExpect(this.bf3.accept(new PrintVisitor()), "(plus 1.0 (sqr 10.0))");
    t.checkExpect(this.bf4.accept(new PrintVisitor()), "(plus (neg (sqr 0.0)) (sqr (neg 0.0)))");
    t.checkExpect(this.bf5.accept(new PrintVisitor()), "(plus (neg (neg 2.0)) (sqr (sqr 10.0)))");
    t.checkExpect(this.bf6.accept(new PrintVisitor()),
        "(plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0))");
    t.checkExpect(this.bf7.accept(new PrintVisitor()), "(minus 1.0 1.0)");
    t.checkExpect(this.bf8.accept(new PrintVisitor()), "(minus 1.0 0.0)");
    t.checkExpect(this.bf9.accept(new PrintVisitor()), "(minus 5.5 5.0)");
    t.checkExpect(this.bf10.accept(new PrintVisitor()), "(minus 0.0 1.0)");
    t.checkExpect(this.bf11.accept(new PrintVisitor()), "(minus 0.0 0.0)");
    t.checkExpect(this.bf12.accept(new PrintVisitor()), "(minus (neg (neg 2.0)) (neg (neg -2.0)))");
    t.checkExpect(this.bf13.accept(new PrintVisitor()), "(minus (neg (neg 2.0)) 2.0)");
    t.checkExpect(this.bf14.accept(new PrintVisitor()), "(minus (neg (sqr 10.0)) (sqr 10.0))");
    t.checkExpect(this.bf15.accept(new PrintVisitor()), "(minus 0.0 (plus 1.0 (sqr 10.0)))");
    t.checkExpect(this.bf16.accept(new PrintVisitor()), "(minus (minus 0.0 1.0) (minus 5.5 5.0))");
    t.checkExpect(this.bf17.accept(new PrintVisitor()), "(mul 0.0 0.0)");
    t.checkExpect(this.bf18.accept(new PrintVisitor()), "(mul 0.0 5.5)");
    t.checkExpect(this.bf19.accept(new PrintVisitor()), "(mul -5.0 0.0)");
    t.checkExpect(this.bf20.accept(new PrintVisitor()),
        "(mul 0.0 (minus (neg (sqr 10.0)) (sqr 10.0)))");
    t.checkExpect(this.bf21.accept(new PrintVisitor()), "(mul (neg 2.0) (neg -2.0))");
    t.checkExpect(this.bf22.accept(new PrintVisitor()), "(mul 5.5 -2.0)");
    t.checkExpect(this.bf23.accept(new PrintVisitor()), "(mul -5.5 -2.0)");
    t.checkExpect(this.bf24.accept(new PrintVisitor()), "(mul (neg -2.0) (sqr 10.0))");
    t.checkExpect(this.bf25.accept(new PrintVisitor()),
        "(mul (plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0)) (minus (neg (neg 2.0)) (neg (neg -2.0))))");
    t.checkExpect(this.bf26.accept(new PrintVisitor()), "(div 0.0 1.0)");
    t.checkExpect(this.bf27.accept(new PrintVisitor()), "(div -10.0 2.0)");
    t.checkExpect(this.bf28.accept(new PrintVisitor()), "(div -5.5 -5.5)");
    t.checkExpect(this.bf29.accept(new PrintVisitor()), "(div 5.5 -5.5)");
    t.checkExpect(this.bf30.accept(new PrintVisitor()), "(div (sqr 10.0) (neg -2.0))");
    t.checkExpect(this.bf31.accept(new PrintVisitor()), "(div (neg -2.0) (sqr 10.0))");
    t.checkExpect(this.bf32.accept(new PrintVisitor()),
        "(div (plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0)) (minus (neg (neg 2.0)) (neg (neg -2.0))))");
    t.checkExpect(this.bf33.accept(new PrintVisitor()),
        "(div (sqr 10.0) (minus (minus 0.0 1.0) (minus 5.5 5.0)))");

    t.checkExpect(new PrintVisitor().apply(this.bf1), "(plus 1.0 1.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf2), "(plus (neg 2.0) 2.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf3), "(plus 1.0 (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf4), "(plus (neg (sqr 0.0)) (sqr (neg 0.0)))");
    t.checkExpect(new PrintVisitor().apply(this.bf5), "(plus (neg (neg 2.0)) (sqr (sqr 10.0)))");
    t.checkExpect(new PrintVisitor().apply(this.bf6),
        "(plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf7), "(minus 1.0 1.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf8), "(minus 1.0 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf9), "(minus 5.5 5.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf10), "(minus 0.0 1.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf11), "(minus 0.0 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf12), "(minus (neg (neg 2.0)) (neg (neg -2.0)))");
    t.checkExpect(new PrintVisitor().apply(this.bf13), "(minus (neg (neg 2.0)) 2.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf14), "(minus (neg (sqr 10.0)) (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf15), "(minus 0.0 (plus 1.0 (sqr 10.0)))");
    t.checkExpect(new PrintVisitor().apply(this.bf16), "(minus (minus 0.0 1.0) (minus 5.5 5.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf17), "(mul 0.0 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf18), "(mul 0.0 5.5)");
    t.checkExpect(new PrintVisitor().apply(this.bf19), "(mul -5.0 0.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf20),
        "(mul 0.0 (minus (neg (sqr 10.0)) (sqr 10.0)))");
    t.checkExpect(new PrintVisitor().apply(this.bf21), "(mul (neg 2.0) (neg -2.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf22), "(mul 5.5 -2.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf23), "(mul -5.5 -2.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf24), "(mul (neg -2.0) (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf25),
        "(mul (plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0)) (minus (neg (neg 2.0)) (neg (neg -2.0))))");
    t.checkExpect(new PrintVisitor().apply(this.bf26), "(div 0.0 1.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf27), "(div -10.0 2.0)");
    t.checkExpect(new PrintVisitor().apply(this.bf28), "(div -5.5 -5.5)");
    t.checkExpect(new PrintVisitor().apply(this.bf29), "(div 5.5 -5.5)");
    t.checkExpect(new PrintVisitor().apply(this.bf30), "(div (sqr 10.0) (neg -2.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf31), "(div (neg -2.0) (sqr 10.0))");
    t.checkExpect(new PrintVisitor().apply(this.bf32),
        "(div (plus (plus 1.0 (sqr 10.0)) (plus 1.0 1.0)) (minus (neg (neg 2.0)) (neg (neg -2.0))))");
    t.checkExpect(new PrintVisitor().apply(this.bf33),
        "(div (sqr 10.0) (minus (minus 0.0 1.0) (minus 5.5 5.0)))");

  }

  void testConstDoublerVisitor(Tester t) {
    t.checkExpect(this.c1.accept(new DoublerVisitor()), this.c2);
    t.checkExpect(this.c2.accept(new DoublerVisitor()), new Const(4.0));
    t.checkExpect(this.c5.accept(new DoublerVisitor()), this.c10);
    t.checkExpect(this.c5_5.accept(new DoublerVisitor()), new Const(11.0));
    t.checkExpect(this.c10.accept(new DoublerVisitor()), new Const(20.0));
    t.checkExpect(this.c0.accept(new DoublerVisitor()), this.c0);
    t.checkExpect(this.negC1.accept(new DoublerVisitor()), this.negC2);
    t.checkExpect(this.negC2.accept(new DoublerVisitor()), new Const(-4.0));
    t.checkExpect(this.negC5.accept(new DoublerVisitor()), this.negC10);
    t.checkExpect(this.negC5_5.accept(new DoublerVisitor()), new Const(-11.0));
    t.checkExpect(this.negC10.accept(new DoublerVisitor()), new Const(-20.0));
  }

  void testUnaryDoublerVisitor(Tester t) {
    t.checkExpect(this.uf1.accept(new DoublerVisitor()),
        new UnaryFormula(new Negate(), "neg", new Const(4.0)));
    t.checkExpect(this.uf2.accept(new DoublerVisitor()), this.uf2);
    t.checkExpect(this.uf3.accept(new DoublerVisitor()),
        new UnaryFormula(new Negate(), "neg", new Const(-4.0)));

    t.checkExpect(this.uf13.accept(new DoublerVisitor()), new UnaryFormula(new Square(), "sqr",
        new UnaryFormula(new Negate(), "neg", new Const(4.0))));
    t.checkExpect(this.uf14.accept(new DoublerVisitor()), new UnaryFormula(new Square(), "sqr",
        new UnaryFormula(new Negate(), "neg", new Const(0.0))));
    t.checkExpect(this.uf15.accept(new DoublerVisitor()), new UnaryFormula(new Square(), "sqr",
        new UnaryFormula(new Negate(), "neg", new Const(-4.0))));
  }

  void testBinaryDoublerVisitor(Tester t) {
    t.checkExpect(this.bf32.accept(new DoublerVisitor()),
        new BinaryFormula(new Div(), "div",
            new BinaryFormula(new Plus(), "plus",
                new BinaryFormula(new Plus(), "plus", this.c2,
                    new UnaryFormula(new Square(), "sqr", new Const(20.0))),
                new BinaryFormula(new Plus(), "plus", this.c2, this.c2)),
            new BinaryFormula(new Minus(), "minus",
                new UnaryFormula(new Negate(), "neg",
                    new UnaryFormula(new Negate(), "neg", new Const(4.0))),
                new UnaryFormula(new Negate(), "neg",
                    new UnaryFormula(new Negate(), "neg", new Const(-4.0))))));

    t.checkExpect(this.bf2.accept(new DoublerVisitor()), new BinaryFormula(new Plus(), "plus",
        new UnaryFormula(new Negate(), "neg", new Const(4.0)), new Const(4.0)));

    t.checkExpect(this.bf1.accept(new DoublerVisitor()),
        new BinaryFormula(new Plus(), "plus", new Const(2.0), new Const(2.0)));
  }

  void testNoNegativeResults(Tester t) {
    t.checkExpect(this.c1.accept(new NoNegativeResults()), true);
    t.checkExpect(this.negC1.accept(new NoNegativeResults()), false);
    t.checkExpect(this.c0.accept(new NoNegativeResults()), true);

    t.checkExpect(this.uf1.accept(new NoNegativeResults()), false);
    t.checkExpect(this.uf16.accept(new NoNegativeResults()), true);
    t.checkExpect(this.uf17.accept(new NoNegativeResults()), true);

    t.checkExpect(this.bf21.accept(new NoNegativeResults()), false);
    t.checkExpect(this.bf23.accept(new NoNegativeResults()), false);
    t.checkExpect(this.bf21.accept(new NoNegativeResults()), false);
    t.checkExpect(this.bf6.accept(new NoNegativeResults()), true);
  }

  void testStaysPositive(Tester t) {
    t.checkExpect(this.c1.accept(new StaysPositive()), true);
    t.checkExpect(this.c0.accept(new StaysPositive()), true);
    t.checkExpect(this.negC1.accept(new StaysPositive()), false);

    t.checkExpect(this.uf2.accept(new StaysPositive()), true);
    t.checkExpect(this.uf16.accept(new StaysPositive()), true);
    t.checkExpect(this.uf12.accept(new StaysPositive()), false);

    t.checkExpect(this.bf21.accept(new StaysPositive()), false);
    t.checkExpect(this.bf23.accept(new StaysPositive()), true);
    t.checkExpect(this.bf21.accept(new StaysPositive()), false);
    t.checkExpect(this.bf6.accept(new StaysPositive()), true);
  }

}