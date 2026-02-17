
type Row = Map[String, String]
type Tabular = List[Row]

case class Table (tableName: String, tableData: Tabular) {
  
  // TODO 1.0
  def header: List[String] =
    if (tableData.isEmpty == false)
      tableData.head.keys.toList
    else Nil
  def data: Tabular = tableData
  def name: String = tableName


  // TODO 1.1
  override def toString: String = {
    val cols = header
    val str = cols.mkString(",")
    val rows = tableData.map {row => cols.map(col => row.getOrElse(col, "")).mkString(",")}.mkString("\n")
    s"$str\n$rows"
  }

  // TODO 1.3
  def insert(row: Row): Table = {
    if (this.data.contains(row)) this
    else {
      val newData = this.data :+ row
      Table(this.name, newData)
    }
  }

  // TODO 1.4
  def delete(row: Row): Table = {
    Table(this.name, this.data.filter(_ != row))
  }

  // TODO 1.5
  def sort(column: String, ascending: Boolean = true): Table = {
    val data = this.data.sortBy(row => row(column))
    if (ascending == true) Table(this.name, data)
    else Table(this.name, data.reverse)
  }

  // TODO 1.6
  def select(columns: List[String]): Table = {
    val newData = this.data.map(row => row.filter { case (key, _) => columns.contains(key)})
    Table(this.name, newData)
  }

  // TODO 1.7
  // Construiti headerele noi concatenand numele tabelei la headere
  def cartesianProduct(otherTable: Table): Table = {
    val thisData = this.data.map(row => row.map { case (key, value) => s"${this.name}.$key" -> value })
    val otherData = otherTable.data.map(row => row.map { case (key, value) => s"${otherTable.name}.$key" -> value })

    val data = thisData.flatMap(row1 => otherData.map(row2 => row1 ++ row2))

    Table(s"${this.name} x ${otherTable.name}", data)
  }
  
  // TODO 1.8
  def join(other: Table)(col1: String, col2: String): Table = {
    def merge(v1: String, v2: String): String = {
      if (v1.isEmpty && v2.isEmpty) ""
      else if (v1.isEmpty) v2
      else if (v2.isEmpty) v1
      else if (v1 == v2) v1
      else if (v1.contains(v2)) v1
      else if (v2.contains(v1)) v2
      else s"$v1;$v2"
    }

    if (this == null || other == null)
      throw new IllegalArgumentException("Tabelul nu exista.")
    if (this.data.isEmpty) return other
    if (other.data.isEmpty) return this

    val newHeader = (this.header ++ other.header.filterNot(_ == col2)).distinct

    // grupez coloanele
    val thisGrouped = this.data.groupBy(_.getOrElse(col1, ""))
    val otherGrouped = other.data.groupBy(_.getOrElse(col2, ""))

    // valori comune, doar in primul tabel sau doar in al doilea tabel
    val commonKeys = this.data.map(_(col1)).filter(otherGrouped.contains).distinct
    val onlyA = this.data.map(_(col1)).filterNot(commonKeys.toSet).filterNot(otherGrouped.contains).distinct
    val onlyB = other.data.map(_(col2)).filterNot(commonKeys.toSet).filterNot(thisGrouped.contains).distinct

    // comune
    val joinedRows = commonKeys.flatMap { key =>
      for {
        rowA <- thisGrouped(key)
        rowB <- otherGrouped(key)
      } yield {
        newHeader.map { col =>
          val valA = rowA.getOrElse(col, "")
          val valB = rowB.getOrElse(col, "")
          col -> merge(valA, valB)
        }.toMap
      }
    }

    // doar in primul tabel
    val rowsA = onlyA.flatMap { key =>
      thisGrouped(key).map { rowA =>
        newHeader.map { col =>
          val valA = rowA.getOrElse(col, "")
          val valB = ""
          col -> merge(valA, valB)
        }.toMap
      }
    }

    // doar in al doilea tabel
    val rowsB = onlyB.flatMap { key =>
      otherGrouped(key).map { rowB =>
        newHeader.map { col =>
          val valA = ""
          val valB = if (col == col1 && !rowB.contains(col)) rowB.getOrElse(col2, "") else rowB.getOrElse(col, "")
          col -> merge(valA, valB)
        }.toMap
      }
    }

    val allRows = joinedRows ++ rowsA ++ rowsB

    Table(s"${this.name}_join_${other.name}", allRows)
  }


  // TODO 2.3
  def filter(f: FilterCond): Table = {
    val rows = this.data.filter(row => f.eval(row).getOrElse(false))
    Table(this.name, rows)
  }
  
  // TODO 2.4
  def update(f: FilterCond, updates: Map[String, String]): Table = {
    val rows = this.data.map {
      r => f.eval(r) match {
        case Some(true) => r ++ updates
        case _ => r
      }
    }
    Table(this.name, rows)
  }

  // TODO 3.5
  // Implement indexing
  def getRow(index: Int): Map[String, String] = {
    tableData(index)
  }
}

object Table {
  // TODO 1.2
  def fromCSV(csv: String): Table = {
    val lines = csv.stripMargin.trim.split("\n").toList
    val cols = lines.head.split(",").map(_.trim).toList
    val tableData: Tabular = lines.tail.map { line =>
      val values = line.split(",").map(_.trim).toList
      cols.zip(values).toMap
    }
    Table("", tableData)
  }

  // TODO 1.9
  def apply(name: String, s: String): Table = {
    val csv = s.toString
    fromCSV(csv)
  }
}