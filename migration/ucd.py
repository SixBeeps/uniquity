import sqlite3
import os
import sys
from lxml import etree as ET

def hex2(x):
  return ('0' * (len(x) % 2)) + x

# Ensure the database file exists
if not os.path.exists("ucd.db"):
  print("The ucd database file does not exist. Please run the initialization script first.")
  print("Usage: python init.py")
  sys.exit(1)

db = sqlite3.connect("ucd.db")
cursor = db.cursor()

# Load the XML file
xml_file = "./ucd.all.grouped.xml"
tree = ET.iterparse(xml_file, events=('start',), tag="{http://www.unicode.org/ns/2003/ucd/1.0}*")

# Iterate through the XML elements
group_name = None
cp = None
existing_groups = set()
for event, elem in tree:
  tag = elem.tag.split('}')[-1]
  if tag == "group":
    group_name = elem.get("blk")
    na = elem.get("na")
    use_name = na if group_name is None else group_name
    if use_name is None:
      print("[WARN] Group element without a valid name attribute found. Skipping.")
      elem.clear()
      continue
    
    if use_name in existing_groups:
      continue

    cursor.execute("INSERT INTO UnicodeGroup (name) VALUES (?)", (use_name,))
    db.commit()
    existing_groups.add(use_name)
    print(f"Group {use_name}")
    elem.clear()
  elif tag == "char":
    cp = elem.get("cp")
    na1 = elem.get("na1")
    if cp is None:
      print("[WARN] Character element without 'cp' attribute found. Skipping.")
      elem.clear()
      continue
    try:
      bytes.fromhex(hex2(cp))
    except ValueError:
      print(f"[WARN] Invalid character code point '{cp}' found. Skipping.")
      elem.clear()
      continue
    cursor.execute("INSERT INTO UnicodeCharacter (codepoint, name, groupName) VALUES (?, ?, ?)",
                   (cp, na1, group_name))
    elem.clear()
  elif tag == "name-alias":
    if cp is None:
      continue
    alias = elem.get("alias")
    if alias is None:
      print("[WARN] Name-alias element without 'alias' attribute found. Skipping.")
      elem.clear()
      continue
    
    cursor.execute("INSERT INTO UnicodeCharacterAlias (codepoint, alias) VALUES (?, ?)",
                   (cp, alias))
    elem.clear()

print("Done.")
db.commit()
cursor.close()