object Queries {
  def query_1(db: Database, ageLimit: Int, cities: List[String]): Option[Table] = {
    def condition(row: Row): Boolean =
      row.get("age").exists(_.toInt > ageLimit) &&
        row.get("city").exists(cities.contains)

    db.tables.find(_.name == "Customers").map(table =>
      Table(
        tableName = "CustomersRef",
        tableData = table.tableData
          .filter(condition)
          .sortBy(_.get("id").map(_.toInt).getOrElse(0))))
  }

  def query_2(db: Database, date: String, employeeID: Int): Option[Table] = {
    def condition(row: Row): Boolean =
      row.get("date").exists(_ > date) &&
        row.get("employee_id").flatMap(_.toIntOption).exists(_ != employeeID)

    db.tables.find(_.name == "Orders").map(table =>
      Table(
        tableName = "OrdersRef",
        tableData = table.tableData
          .filter(condition)
          .sortBy(row => -row.get("cost").flatMap(_.toIntOption).getOrElse(0))
          .map(r => Map(
            "order_id" -> r.getOrElse("order_id", ""),
            "cost" -> r.getOrElse("cost", "")))
      )
    )
  }

  def query_3(db: Database, minCost: Int): Option[Table] = {
    def condition(row: Row): Boolean =
      row.get("cost").exists(_.toInt > minCost)

    db.tables.find(_.name == "Orders").map(table =>
      Table(
        tableName = "OrdersRef",
        tableData = table.tableData
          .filter(condition)
          .sortBy(row => row.get("employee_id").flatMap(_.toIntOption).getOrElse(0))
          .map(r => Map(
            "order_id" -> r.getOrElse("order_id", ""),
            "employee_id" -> r.getOrElse("employee_id", ""),
            "cost" -> r.getOrElse("cost", "")))
      )
    )
  }
}
