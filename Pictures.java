interface IPicture {
  double getWidth();
}

class Shape implements IPicture {
  String kind;
  double size;
  
  Shape(String kind, double size) {
    this.kind = kind;
    this.size = size;
  }
  
  public double getWidth() {
    return this.size;
  }
}

class Combo implements IPicture {
  String name;
  IOperation operation;
  
  Combo(String name, IOperation operation) {
    this.name = name;
    this.operation = operation;
  }
  
  public double getWidth() {
    return 0;
  }
}

interface IOperation {
}

class Scale implements IOperation {
  IPicture picture;
  
  Scale(IPicture picture) {
    this.picture = picture;
  }   
  
  double doubleSize() {
    return 0;
  }
}

class Beside implements IOperation {
  IPicture picture1;
  IPicture picture2;
  
  Beside(IPicture picture1, IPicture picture2) {
    this.picture1 = picture1;
    this.picture2 = picture2;
  }
}

class Overlay implements IOperation {
  IPicture topPicture;
  IPicture bottomPicture;
  
  Overlay(IPicture topPicture, IPicture bottomPicture) {
    this.topPicture = topPicture;
    this.bottomPicture = bottomPicture;
  }
}


