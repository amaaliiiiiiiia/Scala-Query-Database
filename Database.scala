case class Database(tables: List[Table]) {
  // TODO 3.0
  override def toString: String = {
    tables.zipWithIndex.map { case (table, index) =>
      s"Table ${index + 1}:\n${table.toString}"}.mkString("\n\n")
  }

  // TODO 3.1
  def insert(tableName: String): Database = {
    if (tables.exists(_.name == tableName)) Database(tables)
    else Database(tables :+ Table(tableName, List()))
  }

  // TODO 3.2
  def update(tableName: String, newTable: Table): Database = {
    if (!tables.exists(_.name == tableName)) Database(tables)
    else {
      val newTables = tables.map { t =>
        if (t.name == tableName) newTable
        else t
      }
      Database(newTables)
    }
  }

  // TODO 3.3
  def delete(tableName: String): Database = {
    val newTables = tables.filter(_.name != tableName)
    Database(newTables)
  }

  // TODO 3.4
  def selectTables(tableNames: List[String]): Option[Database] = {
    val newTables = tables.filter(t => tableNames.contains(t.name))
    if (newTables.map(_.name).sorted == tableNames.sorted) Some(Database(newTables))
    else None
  }

  // TODO 3.5
  // Implement indexing here
  def apply(index: Int): Table = {
    tables(index)
  }
}
