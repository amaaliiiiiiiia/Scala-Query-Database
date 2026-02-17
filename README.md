# Scala Query Database

## Table Representation
Tables are represented using functional data structures to ensure immutability and clarity:
- `Row`: A `Map[String, String]` mapping column names to their respective values.
- `Tabular`: A `List[Row]` representing the collection of records.
- `Table`: A case class encapsulating `tableName` and `tableData`.

## Core Implementation
### 1. Table Operations
The `Table` class includes several methods for data processing:
- `toString` / `fromCSV`: Methods to convert the table into CSV format and reconstruct it from a CSV string.
- `insert` / `delete`: Functions to add new rows or remove specific entries.
- `sort`: Sorts rows by a given column in ascending or descending order.
- `select`: Filters the table to include only the specified columns.
- `cartesianProduct`: Computes the Cartesian product between two tables.
- `join`: Merges two tables based on specific columns, handling identical values, differences (using `;` as a separator), and `NULL` equivalents.

### 2. Filtering DSL (FilterCond)
A custom Abstract Data Type (ADT) was implemented to handle complex filtering logic through the `FilterCond` trait:
- `Field`: Evaluates a predicate on a specific column.
- `Compound` / `And` / `Or`: Logical operations for building complex queries.
- `Not`: Negates a filtering condition.
- `Any` / `All`: Evaluates if at least one or all conditions in a list are satisfied.
Operator Overloading: Overrode `&&`, `||`, `==`, and `!` to allow a concise, SQL-like syntax.

### 3. Database Management
The `Database` class acts as a container for multiple tables:
- `insert`, `update`, and `delete`: Manage the tables stored within the database.
- `selectTables`: Extracts a specific subset of tables by name.
- `apply`: Allows direct access to rows within a table or tables within a database using index notation.

## Queries
The engine supports complex one-liner queries to solve real-world backend scenarios:
- `query_1`: Selects customers by age and city, sorted by ID.
- `query_2`: Filters unprocessed orders by date, projecting only OrderID and Cost in descending order.
- `query_3`: Finds high-cost orders and returns details sorted by employee ID.
