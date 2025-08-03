import sqlite3
import os
import sys
db_filename = sys.argv[1] if len(sys.argv) > 1 else "ucd.db"

# Make the output database if it doesn't exist
if not os.path.exists(db_filename):
  with open(db_filename, "w") as f:
    pass

print(f"Using database file: {db_filename}")
db = sqlite3.connect(db_filename)
cursor = db.cursor()

cursor.executescript("""
CREATE TABLE IF NOT EXISTS UnicodeGroup (
  name TEXT NOT NULL,
  PRIMARY KEY(name)
);

CREATE TABLE IF NOT EXISTS UnicodeCharacter (
  codepoint TEXT NOT NULL,
  name TEXT,
  groupName TEXT NOT NULL,
  PRIMARY KEY(codepoint)
);

CREATE TABLE IF NOT EXISTS UnicodeCharacterAlias (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  codepoint TEXT NOT NULL,
  alias TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS room_master_table (
  id INTEGER PRIMARY KEY,
  identity_hash TEXT
);

INSERT OR REPLACE INTO room_master_table (id, identity_hash)
VALUES (42, '97f03dcdfb957fe79a7c6c2ed3b6729e');
""")

db.commit()
cursor.close()
db.close()
print("Database initialized successfully.")