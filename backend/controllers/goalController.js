// controllers/goalController.js
const db = require("../config/db");

// Create Goal
exports.createGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { goal_name, target_amount, deadline } = req.body;
  if (!goal_name || !target_amount || !deadline) {
    return res.status(400).json({ message: "Missing required fields" });
  }

  const sql = `
    INSERT INTO budget_goal (id_user, goal_name, target_amount, deadline)
    VALUES (?, ?, ?, ?)
  `;
  db.query(
    sql,
    [user.id_user, goal_name, target_amount, deadline],
    (err, result) => {
      if (err) {
        console.error("Error creating goal:", err);
        return res.status(500).json({ message: "Database error" });
      }
      res.status(201).json({
        message: "Goal created successfully",
        id_goal: result.insertId,
      });
    }
  );
};

// Get All Goals (and recalc status)
exports.getGoals = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const updateStatus = `
    UPDATE budget_goal
    SET status = CASE
      WHEN saved_amount >= target_amount THEN 1
      WHEN deadline < CURDATE() THEN 2
      ELSE 0
    END
    WHERE id_user = ?
  `;
  db.query(updateStatus, [user.id_user], (updateErr) => {
    if (updateErr) {
      console.error("Failed to update statuses:", updateErr);
      return res.status(500).json({ message: "Failed to update status" });
    }
    const fetch = `SELECT * FROM budget_goal WHERE id_user = ? ORDER BY deadline ASC`;
    db.query(fetch, [user.id_user], (err, results) => {
      if (err) {
        console.error("Error fetching goals:", err);
        return res.status(500).json({ message: "Database error" });
      }
      res.json(results);
    });
  });
};

// Get One Goal (recalc status)
exports.getGoalById = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const updateStatus = `
    UPDATE budget_goal
    SET status = CASE
      WHEN saved_amount >= target_amount THEN 1
      WHEN deadline < CURDATE() THEN 2
      ELSE 0
    END
    WHERE id_user = ? AND id_goal = ?
  `;
  db.query(updateStatus, [user.id_user, req.params.id], (updateErr) => {
    if (updateErr) {
      console.error("Failed to update status:", updateErr);
      return res.status(500).json({ message: "Failed to update status" });
    }
    const fetch = `SELECT * FROM budget_goal WHERE id_user = ? AND id_goal = ?`;
    db.query(fetch, [user.id_user, req.params.id], (err, results) => {
      if (err) {
        console.error("Error fetching goal:", err);
        return res.status(500).json({ message: "Database error" });
      }
      if (!results.length)
        return res.status(404).json({ message: "Goal not found" });
      res.json(results[0]);
    });
  });
};

// Update Goal
exports.updateGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { goal_name, target_amount, deadline, status } = req.body;
  const fields = [],
    vals = [];
  if (goal_name) {
    fields.push("goal_name = ?");
    vals.push(goal_name);
  }
  if (target_amount) {
    fields.push("target_amount = ?");
    vals.push(target_amount);
  }
  if (deadline) {
    fields.push("deadline = ?");
    vals.push(deadline);
  }
  if (status !== undefined) {
    fields.push("status = ?");
    vals.push(status);
  }
  if (!fields.length)
    return res.status(400).json({ message: "No fields to update" });

  vals.push(user.id_user, req.params.id);
  const sql = `UPDATE budget_goal SET ${fields.join(
    ", "
  )} WHERE id_user = ? AND id_goal = ?`;
  db.query(sql, vals, (err) => {
    if (err) {
      console.error("Error updating goal:", err);
      return res.status(500).json({ message: "Database error" });
    }
    res.json({ message: "Goal updated successfully" });
  });
};

// Deposit into Goal
exports.depositToGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { amount } = req.body;
  if (!amount || amount <= 0)
    return res.status(400).json({ message: "Invalid deposit amount" });
  const id_goal = req.params.id;

  // 1) Check user balance
  db.query(
    `SELECT balance FROM user WHERE id_user = ?`,
    [user.id_user],
    (err, uRes) => {
      if (err || !uRes.length)
        return res.status(500).json({ message: "Database error" });
      const balance = uRes[0].balance;
      if (balance < amount)
        return res.status(400).json({ message: "Insufficient balance" });

      // 2) Check goal
      db.query(
        `SELECT saved_amount, target_amount FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
        [user.id_user, id_goal],
        (err, gRes) => {
          if (err || !gRes.length)
            return res.status(500).json({ message: "Database error" });
          const { saved_amount, target_amount } = gRes[0];
          if (saved_amount + amount > target_amount) {
            return res
              .status(400)
              .json({ message: "Cannot deposit beyond goal target" });
          }

          const newBalance = balance - amount;
          const newSaved = saved_amount + amount;
          const newStatus = newSaved >= target_amount ? 1 : 0;

          // 3) Transaction
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
                  `UPDATE budget_goal SET saved_amount = ?, status = ? WHERE id_user = ? AND id_goal = ?`,
                  [newSaved, newStatus, user.id_user, id_goal],
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
                        new_saved_amount: newSaved,
                        target_amount,
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

// Withdraw from Goal
exports.withdrawFromGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { amount } = req.body;
  if (!amount || amount <= 0)
    return res.status(400).json({ message: "Invalid withdraw amount" });
  const id_goal = req.params.id;

  // 1) Check user balance
  db.query(
    `SELECT balance FROM user WHERE id_user = ?`,
    [user.id_user],
    (err, uRes) => {
      if (err || !uRes.length)
        return res.status(500).json({ message: "Database error" });
      const balance = uRes[0].balance;

      // 2) Check goal saved_amount
      db.query(
        `SELECT saved_amount, target_amount FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
        [user.id_user, id_goal],
        (err, gRes) => {
          if (err || !gRes.length)
            return res.status(500).json({ message: "Database error" });
          const { saved_amount, target_amount } = gRes[0];
          if (saved_amount < amount) {
            return res
              .status(400)
              .json({ message: "Insufficient saved amount" });
          }

          const newSaved = saved_amount - amount;
          const newBalance = balance + amount;
          const newStatus = newSaved >= target_amount ? 1 : 0;

          // 3) Transaction
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
                  `UPDATE budget_goal SET saved_amount = ?, status = ? WHERE id_user = ? AND id_goal = ?`,
                  [newSaved, newStatus, user.id_user, id_goal],
                  (err) => {
                    if (err)
                      return db.rollback(() =>
                        res.status(500).json({ message: "Failed to withdraw" })
                      );

                    db.commit((err) => {
                      if (err)
                        return db.rollback(() =>
                          res.status(500).json({ message: "Commit failed" })
                        );

                      res.json({
                        message: "Withdrawal successful",
                        new_balance: newBalance,
                        new_saved_amount: newSaved,
                        target_amount,
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

// Delete Goal
exports.deleteGoal = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const goalId = req.params.id;

  db.query(
    `DELETE FROM budget_goal WHERE id_user = ? AND id_goal = ?`,
    [user.id_user, goalId],
    (err, result) => {
      if (err) {
        console.error("Delete error:", err);
        return res.status(500).json({ message: "Database error" });
      }
      if (result.affectedRows === 0) {
        return res.status(404).json({ message: "Goal not found or not yours" });
      }
      res.json({ message: "Goal deleted successfully" });
    }
  );
}; 
