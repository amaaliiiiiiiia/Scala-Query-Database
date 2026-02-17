import scala.language.implicitConversions

trait FilterCond {
  def eval(r: Row): Option[Boolean]

  // TODO 2.2
  def ===(other: FilterCond): FilterCond =
    Equal(this, other)
  def &&(other: FilterCond): FilterCond =
    And(this, other)
  def ||(other: FilterCond): FilterCond =
    Or(this, other)
  def unary_! : FilterCond =
    Not(this)
}

case class Field(colName: String, predicate: String => Boolean) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    r.get(colName).map(predicate)
  }
}

case class Compound(op: (Boolean, Boolean) => Boolean, conditions: List[FilterCond]) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    val res = conditions.flatMap(_.eval(r))
    if (res.isEmpty) None
    else Some(!res.contains(false))
  }
}

case class Not(f: FilterCond) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    f.eval(r).map(x => !x)
  }
}

def And(f1: FilterCond, f2: FilterCond): FilterCond = new FilterCond {
  override def eval(r: Row): Option[Boolean] =
  (f1.eval(r), f2.eval(r)) match {
    case (Some(a), Some(b)) => Some(a && b)
    case _ => None
  }
}
def Or(f1: FilterCond, f2: FilterCond): FilterCond = new FilterCond {
  override def eval(r: Row): Option[Boolean] =
    (f1.eval(r), f2.eval(r)) match {
      case (Some(a), Some(b)) => Some(a || b)
      case (Some(a), None) => Some(a)
      case (None, Some(b)) => Some(b)
      case _ => None
    }
}
def Equal(f1: FilterCond, f2: FilterCond): FilterCond = new FilterCond {
  override def eval(r: Row): Option[Boolean] =
    (f1.eval(r), f2.eval(r)) match {
      case (Some(a), Some(b)) => Some(a == b)
      case _ => None
    }
}

case class Any(fs: List[FilterCond]) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    val res = fs.flatMap(_.eval(r))
    if (res.isEmpty) None
    else Some(res.contains(true))
  }
}

case class All(fs: List[FilterCond]) extends FilterCond {
  override def eval(r: Row): Option[Boolean] = {
    val res = fs.flatMap(_.eval(r))
    if (res.isEmpty) None
    else Some(!res.contains(false))
  }
}