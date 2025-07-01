import java.util.Comparator;
import java.util.function.Function;

import tester.Tester;

interface IPredicate<T> {
  boolean apply(T t);
}

class SameStudent implements Comparator<Student> {

  public int compare(Student o1, Student o2) {
    // based off of student id
    if (o1.id == o2.id)
      return 0;
    return -1;
  }
}

class StudentInCourse implements IPredicate<Course> {
  Student s;

  // Constructor for StudentInCourse
  StudentInCourse(Student s) {
    this.s = s;
  }

  public boolean apply(Course c) {
    return c.students.findFromPredicate(new StudentInStudents(this.s));
  }
}

class StudentInStudents implements IPredicate<Student> {
  Student s;

  // Constructor for StudentInStudents
  StudentInStudents(Student s) {
    this.s = s;
  }

  public boolean apply(Student st) {
    return new SameStudent().compare(this.s, st) == 0;
  }
}

class CourseToStudents implements Function<Course, IList<Student>> {

  public IList<Student> apply(Course t) {
    return t.students;
  }

}

interface IList<T> {
  boolean findFromPredicate(IPredicate<T> pred);

  <R> int countElement(R r, IPredicate<T> pred);

}

class MtList<T> implements IList<T> {

  public boolean findFromPredicate(IPredicate<T> pred) {
    return false;
  }

  public <R> int countElement(R r, IPredicate<T> pred) {
    return 0;
  }

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public boolean findFromPredicate(IPredicate<T> pred) {
    return pred.apply(first) || this.rest.findFromPredicate(pred);
  }

  public <R> int countElement(R r, IPredicate<T> pred) {
    if (pred.apply(this.first)) {
      return 1 + this.rest.countElement(r, pred);
    }
    return this.rest.countElement(r, pred);
  }

}

class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();
  }

  Course(String name, Instructor prof, IList<Student> students) {
    this.name = name;
    this.prof = prof;
    this.students = students;
  }

  void enroll(Student s) {
    this.students = new ConsList<Student>(s, this.students);
  }

}

class Instructor {
  String name;
  IList<Course> courses;

  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  Instructor(String name, IList<Course> courses) {
    this.name = name;
    this.courses = courses;
  }

  boolean dejavu(Student c) {
    return this.courses.countElement(c, new StudentInCourse(c)) > 1;
  }

}

class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  Student(String name, int id, IList<Course> courses) {
    this.name = name;
    this.id = id;
    this.courses = courses;
  }

  void enroll(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
    c.enroll(this);
  }

  boolean classmates(Student c) {
    return this.courses.findFromPredicate(new StudentInCourse(c));
  }
}

class ExamplesRegistrar {
  Course fundiesII, linAlg, calcIII, humanTrafficking, massIncarc = null;
  Student zey, alton, asha, lindsay, yasmin = null;
  Instructor vido, robert, pete, candi, seo = null;

  void setUp() {
    this.fundiesII = new Course("Fundies II", this.vido, new MtList<Student>());
    this.linAlg = new Course("Linear Algebra", this.robert, new MtList<Student>());
    this.calcIII = new Course("Calc III", this.seo, new MtList<Student>());
    this.humanTrafficking = new Course("Human Trafficking", this.candi, new MtList<Student>());
    this.massIncarc = new Course("Mass Incarceration", this.candi, new MtList<Student>());

    this.zey = new Student("Zey", 7, new MtList<Course>());
    this.alton = new Student("Alton", 42, new MtList<Course>());
    this.asha = new Student("Asha", 69, new MtList<Course>());
    this.lindsay = new Student("Lindsay", 2, new MtList<Course>());
    this.yasmin = new Student("Yasmin", 2000, new MtList<Course>());

    this.vido = new Instructor("Vido", new ConsList<Course>(this.fundiesII, new MtList<Course>()));
    this.robert = new Instructor("Robert", new ConsList<Course>(this.linAlg, new MtList<Course>()));
    this.pete = new Instructor("Pete", new MtList<Course>());
    this.candi = new Instructor("Candi", new ConsList<Course>(this.humanTrafficking,
        new ConsList<Course>(this.massIncarc, new MtList<Course>())));
    this.seo = new Instructor("Seo", new ConsList<Course>(this.calcIII, new MtList<Course>()));

  }

  void testEnroll(Tester t) {
    setUp();
    t.checkExpect(this.zey.courses, new MtList<Course>());
    t.checkExpect(this.fundiesII.students, new MtList<Student>());
    this.zey.enroll(this.fundiesII);
    t.checkExpect(this.zey.courses, new ConsList<Course>(this.fundiesII, new MtList<Course>()));
    t.checkExpect(this.fundiesII.students, new ConsList<Student>(this.zey, new MtList<Student>()));

    t.checkExpect(this.alton.courses, new MtList<Course>());
    this.alton.enroll(this.fundiesII);
    t.checkExpect(this.alton.courses, new ConsList<Course>(this.fundiesII, new MtList<Course>()));
    t.checkExpect(this.fundiesII.students,
        new ConsList<Student>(this.alton, new ConsList<Student>(this.zey, new MtList<Student>())));

    t.checkExpect(this.asha.courses, new MtList<Course>());
    this.asha.enroll(this.fundiesII);
    t.checkExpect(this.asha.courses, new ConsList<Course>(this.fundiesII, new MtList<Course>()));
    t.checkExpect(this.fundiesII.students, new ConsList<Student>(this.asha,
        new ConsList<Student>(this.alton, new ConsList<Student>(this.zey, new MtList<Student>()))));

    t.checkExpect(this.alton.courses, new ConsList<Course>(this.fundiesII, new MtList<Course>()));
    this.alton.enroll(this.calcIII);
    t.checkExpect(this.alton.courses, new ConsList<Course>(this.calcIII,
        new ConsList<Course>(this.fundiesII, new MtList<Course>())));
    t.checkExpect(this.calcIII.students, new ConsList<Student>(this.alton, new MtList<Student>()));

  }

  void testClassmates(Tester t) {
    setUp();
    t.checkExpect(this.alton.classmates(this.zey), false);
    t.checkExpect(this.alton.classmates(this.asha), false);
    t.checkExpect(this.zey.classmates(this.yasmin), false);

    // vishal said this was ok
    this.alton.enroll(this.fundiesII);
    this.zey.enroll(this.fundiesII);
    this.asha.enroll(this.fundiesII);
    this.yasmin.enroll(this.calcIII);

    t.checkExpect(this.alton.classmates(this.zey), true);
    t.checkExpect(this.alton.classmates(this.asha), true);
    t.checkExpect(this.zey.classmates(this.yasmin), false);
    t.checkExpect(this.alton.classmates(this.yasmin), false);

    this.alton.enroll(this.calcIII);

    t.checkExpect(this.alton.classmates(this.yasmin), true);
    t.checkExpect(this.alton.classmates(this.alton), true);
    t.checkExpect(this.lindsay.classmates(this.alton), false);
    t.checkExpect(this.alton.classmates(this.lindsay), false);
  }

  void testCompare(Tester t) {
    setUp();

    t.checkExpect(new SameStudent().compare(this.alton, this.alton), 0);
    t.checkExpect(new SameStudent().compare(this.yasmin, this.alton), -1);
    t.checkExpect(new SameStudent().compare(this.alton, this.yasmin), -1);
    t.checkExpect(new SameStudent().compare(this.zey, this.asha), -1);
    t.checkExpect(new SameStudent().compare(this.asha, this.asha), 0);

  }

  void testStudentInCourseApply(Tester t) {
    setUp();

    t.checkExpect(new StudentInCourse(this.zey).apply(this.calcIII), false);
    t.checkExpect(new StudentInCourse(this.zey).apply(this.fundiesII), false);

    this.zey.enroll(this.fundiesII);

    t.checkExpect(new StudentInCourse(this.zey).apply(this.calcIII), false);
    t.checkExpect(new StudentInCourse(this.zey).apply(this.fundiesII), true);

    t.checkExpect(new StudentInCourse(this.alton).apply(this.calcIII), false);
    t.checkExpect(new StudentInCourse(this.alton).apply(this.fundiesII), false);
    t.checkExpect(new StudentInCourse(this.alton).apply(this.linAlg), false);

    this.alton.enroll(this.linAlg);

    t.checkExpect(new StudentInCourse(this.alton).apply(this.calcIII), false);
    t.checkExpect(new StudentInCourse(this.alton).apply(this.fundiesII), false);
    t.checkExpect(new StudentInCourse(this.alton).apply(this.linAlg), true);
  }

  void testStudentInStudentsApply(Tester t) {
    setUp();

    t.checkExpect(new StudentInStudents(this.alton).apply(this.alton), true);
    t.checkExpect(new StudentInStudents(this.alton).apply(this.yasmin), false);
    t.checkExpect(new StudentInStudents(this.yasmin).apply(this.alton), false);
    t.checkExpect(new StudentInStudents(this.zey).apply(this.asha), false);
    t.checkExpect(new StudentInStudents(this.asha).apply(this.asha), true);

  }

  void testFindFromPredicate(Tester t) {
    setUp();

    t.checkExpect(new MtList<Course>().findFromPredicate(new StudentInCourse(this.alton)), false);
    t.checkExpect(new MtList<Student>().findFromPredicate(new StudentInStudents(this.zey)), false);

    this.alton.enroll(this.fundiesII);
    this.alton.enroll(this.calcIII);
    this.zey.enroll(this.fundiesII);
    this.zey.enroll(this.humanTrafficking);

    t.checkExpect(new ConsList<Course>(this.fundiesII,
        new ConsList<Course>(this.humanTrafficking, new MtList<Course>()))
            .findFromPredicate(new StudentInCourse(this.alton)),
        true);
    t.checkExpect(new ConsList<Course>(this.calcIII, new MtList<Course>())
        .findFromPredicate(new StudentInCourse(this.zey)), false);

    t.checkExpect(
        new ConsList<Student>(this.zey, new ConsList<Student>(this.asha, new MtList<Student>()))
            .findFromPredicate(new StudentInStudents(this.asha)),
        true);
    t.checkExpect(
        new ConsList<Student>(this.zey, new ConsList<Student>(this.asha, new MtList<Student>()))
            .findFromPredicate(new StudentInStudents(this.alton)),
        false);
  }

  void testDejaVu(Tester t) {
    setUp();

    t.checkExpect(this.candi.dejavu(this.zey), false);

    this.zey.enroll(this.massIncarc);

    t.checkExpect(this.candi.dejavu(this.zey), false);

    this.zey.enroll(this.humanTrafficking);

    t.checkExpect(this.candi.dejavu(this.zey), true);

    t.checkExpect(this.candi.dejavu(this.alton), false);
    t.checkExpect(this.candi.dejavu(this.asha), false);

    this.alton.enroll(this.humanTrafficking);

    t.checkExpect(this.candi.dejavu(this.alton), false);

    this.alton.enroll(this.massIncarc);

    t.checkExpect(this.candi.dejavu(this.alton), true);

  }

  void testCountElement(Tester t) {
    setUp();
  }

}