// controllers/contactController.js
const db = require("../config/db");

// * Add contact
exports.addContact = (req, res) => {
  const user = req.session.user;
  const { account_number } = req.body;
  if (!user)   return res.status(401).json({ message: "Unauthorized" });
  if (!account_number) 
               return res.status(400).json({ message: "Missing account number" });

  db.query(
    "SELECT id_user FROM user WHERE account_number = ?",
    [account_number],
    (err, rows) => {
      if (err)      return res.status(500).json({ message: "Database error" });
      if (rows.length === 0)
                   return res.status(404).json({ message: "Account not found" });

      const targetId = rows[0].id_user;
      if (targetId === user.id_user)
                   return res.status(400).json({ message: "Cannot add yourself" });

      db.query(
        "INSERT INTO contact (id_user, saved_id_user) VALUES (?, ?)",
        [user.id_user, targetId], 
        (err2) => {
          if (err2 && err2.code === "ER_DUP_ENTRY")
            return res.status(409).json({ message: "Contact already exists" });
          if (err2)   return res.status(500).json({ message: "Database error" });
          res.status(201).json({ message: "Contact added" });  
        }
      );
    }
  );
}; 
 
// * Get contacts
exports.getContacts = (req, res) => {
  const user = req.session.user;

  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const query = `
    SELECT u.id_user, u.username, u.name, u.account_number, u.image_path
    FROM contact c
    JOIN user u ON c.saved_id_user = u.id_user
    WHERE c.id_user = ?
  `;
  
  db.query(query, [user.id_user], (err, results) => {  
    if (err) {
      console.error("Error fetching contacts:", err);
      return res.status(500).json({ message: "Database error" });
    }

    res.json(results);
  });
};

// * Delete contact
exports.deleteContact = (req, res) => {
  const user = req.session.user;
  const saved_id_user = req.params.saved_id_user;

  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const query = "DELETE FROM contact WHERE id_user = ? AND saved_id_user = ?";

  db.query(query, [user.id_user, saved_id_user], (err, result) => {
    if (err) {
      console.error("Error deleting contact:", err);
      return res.status(500).json({ message: "Database error" }); 
    }
 
    if (result.affectedRows === 0) { 
      return res.status(404).json({ message: "Contact not found" });
    } 

    res.json({ message: "Contact deleted successfully" });
  });
};
