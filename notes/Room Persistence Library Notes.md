# Room Persistence Library Notes

SQLite is a relational database means one table is related to another.
SQLite databases have something called Schema which is nothing but a blueprint of the tables in SQLite. It contains info on the type of data it will hold. Ex: int, string, etc.
SQLite databases have a concept called Normalisation – The idea is to avoid duplication of data by splitting the types of data into multiple tables and mapping those tables with foreign keys or creating another table that holds the relationships between those 2 or more tables.
Room is an ORM – object relation mapping library. It is like a wrapper around SQLite.
ORM maps POJOs to SQLite tables.
Room Database has 3 things:
Database: This manages operations like connecting and querying the database.
Entity: It’s the mapping over the table. We use annotations in the POJO to represent table and column info.
Dao: Dao means Data access object which provides query functionality over Entity.