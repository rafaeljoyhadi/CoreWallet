const db = require("../config/db");
const bcrypt = require("bcryptjs");
const path = require('path');
const fs = require('fs');

// * Get all user
exports.getAllUsers = (req, res) => {
  const query =
    "SELECT id_user, username, email, name, account_number, balance FROM user";

  db.query(query, (err, results) => {
    if (err) {
      console.error("Error fetching users:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }
    res.json(results);
  });
};

// * Get user by ID
exports.getUserById = (req, res) => {
  const query =
    "SELECT id_user, username, email, name, account_number, balance FROM user WHERE id_user = ?";

  db.query(query, [req.params.id], (err, results) => {
    if (err) {
      console.error("Error fetching user:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    res.json(results[0]);
  });
};

// * Generate unique account number
const generateAccountNumber = async () => {
  return new Promise((resolve, reject) => {
    const accountNumber =
      "99" + Math.floor(100000000 + Math.random() * 900000000);
    const checkQuery =
      "SELECT COUNT(*) AS count FROM user WHERE account_number = ?";

    db.query(checkQuery, [accountNumber], (err, results) => {
      if (err) return reject(err);
      if (results[0].count > 0) {
        return resolve(generateAccountNumber());
      }
      resolve(accountNumber);
    });
  });
};

// * Register user
exports.createUser = async (req, res) => {
  try {
    const { username, password, email, name, pin, image_path, balance } =
      req.body;

    if (!username || !password || !email || !name || !pin) {
      return res.status(400).json({ message: "Missing required fields" });
    }

    if (pin.length !== 6 || isNaN(pin)) {
      return res.status(400).json({ message: "PIN must be exactly 6 digits" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const hashedPin = await bcrypt.hash(pin, 10);
    const accountNumber = await generateAccountNumber();

    const query =
      "INSERT INTO user (username, password, email, name, account_number, pin, image_path, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    db.query(
      query,
      [
        username,
        hashedPassword,
        email,
        name,
        accountNumber,
        hashedPin,
        image_path || null,
        balance || 0,
      ],
      (err, result) => {
        if (err) {
          console.error("Error adding user:", err);
          return res
            .status(500)
            .json({ message: "Database error", error: err });
        }
        res.status(201).json({
          message: "User created successfully",
          userId: result.insertId,
          accountNumber,
        });
      }
    );
  } catch (error) {
    console.error("Error processing request:", error);
    res.status(500).json({ message: "Server error" });
  }
};

// * Delete user
exports.deleteUser = (req, res) => {
  const query = "DELETE FROM user WHERE id_user = ?";

  db.query(query, [req.params.id], (err, result) => {
    if (err) {
      console.error("Error deleting user:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    res.json({ message: "User deleted successfully" });
  });
};

// * Update User
exports.updateUser = async (req, res) => {
    try {
      const { username, email, name, password, pin, image_path } = req.body;
  
      if (!username && !email && !name && !password && !pin && !image_path) {
        return res.status(400).json({ message: "No fields to update" });
      }
  
      const updateFields = [];
      const updateValues = [];
  
      if (username) {
        updateFields.push("username = ?");
        updateValues.push(username);
      }
      if (email) {
        updateFields.push("email = ?");
        updateValues.push(email);
      }
      if (name) {
        updateFields.push("name = ?");
        updateValues.push(name);
      }
      if (image_path) {
        updateFields.push("image_path = ?");
        updateValues.push(image_path);
      }
      if (password) {
        const hashedPassword = await bcrypt.hash(password, 10);
        updateFields.push("password = ?");
        updateValues.push(hashedPassword);
      }
      if (pin) {
        if (pin.length !== 6 || isNaN(pin)) {
          return res
            .status(400)
            .json({ message: "PIN must be exactly 6 digits" });
        }
        const hashedPin = await bcrypt.hash(pin, 10);
        updateFields.push("pin = ?");
        updateValues.push(hashedPin);
      }
  
      updateValues.push(req.params.id);
      const query = `UPDATE user SET ${updateFields.join(
        ", "
      )} WHERE id_user = ?`;
  
      db.query(query, updateValues, (err, result) => {
        if (err) {
          console.error("Error updating user:", err);
          return res.status(500).json({ message: "Database error", error: err });
        }
        if (result.affectedRows === 0) {
          return res.status(404).json({ message: "User not found" });
        }
        res.json({ message: "User updated successfully" });
      });
    } catch (error) {
      console.error("Error processing request:", error);
      res.status(500).json({ message: "Server error" });
    }
  };

exports.uploadProfilePicture = async (req, res) => {
    try {
        const userId = req.params.id;
        const token = req.headers.authorization?.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'Access denied, token missing' });
        }
        // Verify token (e.g., using jsonwebtoken)
        const decoded = jwt.verify(token, 'your-secret-key');
        if (decoded.id_user != parseInt(userId)) {
            return res.status(401).json({ message: 'Unauthorized' });
        }
        if (!req.files || !req.files.profile_picture) {
            return res.status(400).json({ message: 'No file uploaded' });
        }
        const file = req.files.profile_picture;
        const fileName = `${Date.now()}_${file.name}`;
        const filePath = path.join(__dirname, '../Uploads', fileName);
        await file.mv(filePath);
        await db.query('UPDATE user SET image_path = ? WHERE id_user = ?', [`/Uploads/${fileName}`, userId]);
        const [user] = await db.query('SELECT * FROM user WHERE id_user = ?', [userId]);
        res.status(200).json(user[0]);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error' });
    }
};
// * LOGIN USER
exports.loginUser = (req, res) => {
    if (req.session.user) {
      return res.status(400).json({ message: "User already logged in" });
    }
  
    const { username, password } = req.body;
  
    if (!username || !password) {
      return res
        .status(400)
        .json({ message: "Username and password are required" });
    }
  
    const query = "SELECT * FROM user WHERE username = ? OR email = ?";
    db.query(query, [username, username], async (err, results) => {
      if (err) {
        console.error("Database error:", err);
        return res.status(500).json({ message: "Database error" });
      }
  
      if (results.length === 0) {
        return res.status(404).json({ message: "User not found" });
      }
  
      const user = results[0];
  
      const isMatch = await bcrypt.compare(password, user.password);
      if (!isMatch) {
        return res.status(401).json({ message: "Invalid credentials" });
      }
  
      req.session.user = {
        id_user: user.id_user,
        name: user.name,
        username: user.username,
        account_number: user.account_number,
      };
  
      const userAgent = req.headers["user-agent"] || "Unknown Device";
      const logQuery =
        "INSERT INTO log_login (id_user, name, user_agent, note) VALUES (?, ?, ?, ?)";
      db.query(
        logQuery,
        [user.id_user, user.name, userAgent, "User logged in"],
        (logErr) => {
          if (logErr) console.error("Failed to insert login log:", logErr);
        }
      );
  
      res.json({ message: "Login successful", user: req.session.user });
    });
  };
  

// * LOGOUT USER
exports.logoutUser = (req, res) => {
    if (!req.session.user) {
      return res.status(400).json({ message: "No user is logged in" });
    }
  
    const { id_user, name } = req.session.user;
    const userAgent = req.headers["user-agent"] || "Unknown Device";
  
    const logQuery =
      "INSERT INTO log_login (id_user, name, user_agent, note) VALUES (?, ?, ?, ?)";
    db.query(
      logQuery,
      [id_user, name, userAgent, "User logged out"],
      (logErr) => {
        if (logErr) console.error("Failed to insert logout log:", logErr);
      }
    );
  
    req.session.destroy((err) => {
      if (err) {
        console.error("Logout error:", err);
        return res.status(500).json({ message: "Logout failed" });
      }
      res.json({ message: "Logout successful" });
    });
  };
  
