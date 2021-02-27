db = new Mongo().getDB("StarWars");

db.createCollection("jedi", {capped: false});
db.createCollection("sith", {capped: false});

db.jedi.insertMany([
    {id: "f04e5e8a-7b8d-4519-9a8b-8d04101086f7", name: "Luke", age: 19},
    {id: "f89444e7-189c-4bba-b48c-e291146c2f5a", name: "Yoda", age: 942},
    {id: "93a1d159-eb4d-4a61-abf6-b8eab94bc9cc", name: "Obi Wan", age: 67},
]);

db.sith.insertMany([
    {id: "c1cd2cc7-1fa8-47de-b528-33b090654fdf", name: "Palpatine", age: 340},
    {id: "dda88970-6730-400a-861c-08f992970f80", name: "Anakin", age: 29},
]);
