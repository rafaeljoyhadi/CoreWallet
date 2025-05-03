// controllers/goalController.js
const db = require("../config/db");

// * Create Goal
exports.createGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { goal_name, target_amount, deadline } = req.body;

  if (!goal_name || !target_amount || !deadline) {
    return res.status(400).json({ message: "Missing required fields" });
  }

  const query = `
    INSERT INTO budget_goal (id_user, goal_name, target_amount, deadline)
    VALUES (?, ?, ?, ?)
  `;

  db.query(
    query,
    [user.id_user, goal_name, target_amount, deadline],
    (err, result) => {
      if (err) {
        console.error("Error creating goal:", err);
        return res.status(500).json({ message: "Database error", error: err });
      }
      res.status(201).json({
        message: "Goal created successfully",
        id_goal: result.insertId,
      });
    }
  );
};

// * Get All Goals
exports.getGoals = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const updateQuery = `
    UPDATE budget_goal
    SET status = 
      CASE 
        WHEN saved_amount >= target_amount THEN 1
        WHEN deadline < CURDATE() THEN 2
        ELSE 0
      END
    WHERE id_user = ?
  `;

  db.query(updateQuery, [user.id_user], (updateErr) => {
    if (updateErr) {
      console.error("Failed to update goal statuses:", updateErr);
      return res.status(500).json({ message: "Failed to update status" });
    }
    const query = `SELECT * FROM budget_goal WHERE id_user = ? ORDER BY deadline ASC`;
    db.query(query, [user.id_user], (err, results) => {
      if (err) {
        console.error("Error fetching goals:", err);
        return res.status(500).json({ message: "Database error", error: err });
      }
      res.json(results);
    });
  });
};

// * Get Goal by ID
exports.getGoalById = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const updateQuery = `
    UPDATE budget_goal
    SET status = 
      CASE 
        WHEN saved_amount >= target_amount THEN 1
        WHEN deadline < CURDATE() THEN 2
        ELSE 0
      END
    WHERE id_user = ? AND id_goal = ?
  `;

  db.query(updateQuery, [user.id_user, req.params.id], (updateErr) => {
    if (updateErr) {
      console.error("Failed to update goal status:", updateErr);
      return res.status(500).json({ message: "Failed to update goal status" });
    }
    const fetchQuery = `SELECT * FROM budget_goal WHERE id_user = ? AND id_goal = ?`;
    db.query(fetchQuery, [user.id_user, req.params.id], (err, results) => {
      if (err) {
        console.error("Error fetching goal:", err);
        return res.status(500).json({ message: "Database error", error: err });
      }
      if (results.length === 0)
        return res.status(404).json({ message: "Goal not found" });

      res.json(results[0]);
    });
  });
};

// * Update Goal
exports.updateGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { goal_name, target_amount, deadline, status } = req.body;
  const fields = [];
  const values = [];

  if (goal_name) {
    fields.push("goal_name = ?");
    values.push(goal_name);
  }
  if (target_amount) {
    fields.push("target_amount = ?");
    values.push(target_amount);
  }
  if (deadline) {
    fields.push("deadline = ?");
    values.push(deadline);
  }
  if (status !== undefined) {
    fields.push("status = ?");
    values.push(status);
  }

  if (fields.length === 0)
    return res.status(400).json({ message: "No fields to update" });

  values.push(user.id_user, req.params.id);
  const query = `UPDATE budget_goal SET ${fields.join(
    ", "
  )} WHERE id_user = ? AND id_goal = ?`;

  db.query(query, values, (err) => {
    if (err) {
      console.error("Error updating goal:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }
    res.json({ message: "Goal updated successfully" });
  });
};

// * Deposit into Goal
exports.depositToGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { amount } = req.body;
  const id_goal = req.params.id;

  if (!amount || amount <= 0) {
    return res.status(400).json({ message: "Invalid deposit amount" });
  }

  db.query(
    `SELECT balance FROM user WHERE id_user = ?`,
    [user.id_user],
    (err, userResult) => {
      if (err || userResult.length === 0) {
        console.error("Error fetching user:", err);
        return res.status(500).json({ message: "Database error" });
      }

      const balance = userResult[0].balance;
      if (balance < amount) {
        return res.status(400).json({ message: "Insufficient balance" });
      }

      db.query(
        `SELECT saved_amount, target_amount FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
        [user.id_user, id_goal],
        (err, goalResult) => {
          if (err || goalResult.length === 0) {
            console.error("Error fetching goal:", err);
            return res.status(500).json({ message: "Database error" });
          }

          const saved = goalResult[0].saved_amount;
          const target = goalResult[0].target_amount;

          if (saved + amount > target) {
            return res
              .status(400)
              .json({ message: "Cannot deposit beyond goal target" });
          }

          const newBalance = balance - amount;
          const newSaved = saved + amount;

          db.beginTransaction((err) => {
            if (err)
              return res.status(500).json({ message: "Transaction error" });

            db.query(
              `UPDATE user SET balance = ? WHERE id_user = ?`,
              [newBalance, user.id_user],
              (err) => {
                if (err)
                  return db.rollback(() =>
                    res
                      .status(500)
                      .json({ message: "Failed to update balance" })
                  );

                db.query(
                  `UPDATE budget_goal SET saved_amount = ? WHERE id_user = ? AND id_goal = ?`,
                  [newSaved, user.id_user, id_goal],
                  (err) => {
                    if (err)
                      return db.rollback(() =>
                        res.status(500).json({ message: "Failed to deposit" })
                      );

                    db.commit((err) => {
                      if (err)
                        return db.rollback(() =>
                          res.status(500).json({ message: "Commit failed" })
                        );
                      res.json({
                        message: "Deposit successful",
                        new_balance: newBalance,
                        new_saved_amount: newSavedAmount,
                        target_amount: goalResult[0].target_amount,
                      });
                    });
                  }
                );
              }
            );
          });
        }
      );
    }
  );
};

// * Withdraw from Goal
exports.withdrawFromGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { amount } = req.body;
  const id_goal = req.params.id;

  if (!amount || amount <= 0) {
    return res.status(400).json({ message: "Invalid withdraw amount" });
  }

  db.query(
    `SELECT balance FROM user WHERE id_user = ?`,
    [user.id_user],
    (err, userResult) => {
      if (err || userResult.length === 0) {
        console.error("User fetch error:", err);
        return res.status(500).json({ message: "Database error" });
      }

      const balance = userResult[0].balance;

      db.query(
        `SELECT saved_amount FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
        [user.id_user, id_goal],
        (err, goalResult) => {
          if (err || goalResult.length === 0) {
            console.error("Goal fetch error:", err);
            return res.status(500).json({ message: "Database error" });
          }

          const saved = goalResult[0].saved_amount;
          if (saved < amount) {
            return res
              .status(400)
              .json({ message: "Insufficient saved amount" });
          }

          const newSaved = saved - amount;
          const newBalance = balance + amount;

          db.beginTransaction((err) => {
            if (err)
              return res.status(500).json({ message: "Transaction error" });

            db.query(
              `UPDATE user SET balance = ? WHERE id_user = ?`,
              [newBalance, user.id_user],
              (err) => {
                if (err)
                  return db.rollback(() =>
                    res
                      .status(500)
                      .json({ message: "Failed to update balance" })
                  );

                db.query(
                  `UPDATE budget_goal SET saved_amount = ? WHERE id_user = ? AND id_goal = ?`,
                  [newSaved, user.id_user, id_goal],
                  (err) => {
                    if (err)
                      return db.rollback(() =>
                        res
                          .status(500)
                          .json({ message: "Failed to update goal" })
                      );

                    db.commit((err) => {
                      if (err)
                        return db.rollback(() =>
                          res.status(500).json({ message: "Commit failed" })
                        );
                      res.json({
                        message: "Withdrawal successful",
                        new_balance: newBalance,
                        new_saved_amount: newSavedAmount,
                        target_amount: goalAmount,
                      });
                    });
                  }
                );
              }
            );
          });
        }
      );
    }
  );
};

// * Delete Goal
exports.deleteGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  db.query(
    `DELETE FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
    [user.id_user, req.params.id],
    (err) => {
      if (err) {
        console.error("Delete error:", err);
        return res.status(500).json({ message: "Database error", error: err });
      }
      res.json({ message: "Goal deleted successfully" });
    }
  );
};
