// controllers/budgetController.js
const db = require("../config/db");

// * Create Budget Plan
exports.createBudgetPlan = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { id_category, amount_limit, start_date, end_date } = req.body;

  if (!id_category || !amount_limit || !start_date || !end_date) {
    return res.status(400).json({ message: "Missing required fields" });
  }

  const query = `
    INSERT INTO budget_plan (id_user, id_category, amount_limit, start_date, end_date)
    VALUES (?, ?, ?, ?, ?)
  `;

  db.query(
    query,
    [user.id_user, id_category, amount_limit, start_date, end_date],
    (err, result) => {
      if (err) {
        console.error("Error creating budget plan:", err);
        return res.status(500).json({ message: "Database error", error: err });
      }
      res.status(201).json({
        message: "Budget plan created successfully",
        id_budget: result.insertId,
      });
    }
  );
};

// * Get All Budget Plans
exports.getAllBudgetPlans = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const query = `
    SELECT bp.*, tc.category_name
    FROM budget_plan bp
    JOIN transaction_category tc ON bp.id_category = tc.id_category
    WHERE bp.id_user = ?
    ORDER BY bp.start_date DESC
  `;

  db.query(query, [user.id_user], (err, results) => {
    if (err) {
      console.error("Error fetching budget plans:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }

    const currentDate = new Date();
    const updatedPlans = results.map((plan) => {
      const endDate = new Date(plan.end_date);
      let status = plan.status;

      if (plan.spent_amount >= plan.amount_limit) {
        status = 1; // Completed
      } else if (currentDate > endDate) {
        status = 2; // Expired
      } else {
        status = 0; // Active
      }

      return { ...plan, status };
    });

    res.json(updatedPlans);
  });
};

// * Get Budget Plan by ID
exports.getBudgetPlanById = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const query = `
    SELECT bp.*, tc.category_name
    FROM budget_plan bp
    JOIN transaction_category tc ON bp.id_category = tc.id_category
    WHERE bp.id_user = ? AND bp.id_budget = ?
  `;

  db.query(query, [user.id_user, req.params.id], (err, results) => {
    if (err) {
      console.error("Error fetching budget plan:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }

    if (results.length === 0)
      return res.status(404).json({ message: "Budget plan not found" });

    const plan = results[0];
    const currentDate = new Date();
    const endDate = new Date(plan.end_date);

    if (plan.spent_amount >= plan.amount_limit) {
      plan.status = 1; // Completed
    } else if (currentDate > endDate) {
      plan.status = 2; // Expired
    } else {
      plan.status = 0; // Active
    }

    res.json(plan);
  });
};

// * Update Budget Plan
exports.updateBudgetPlan = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const { id_category, amount_limit, start_date, end_date, status } = req.body;
  const fields = [];
  const values = [];

  if (id_category) {
    fields.push("id_category = ?");
    values.push(id_category);
  }
  if (amount_limit) {
    fields.push("amount_limit = ?");
    values.push(amount_limit);
  }
  if (start_date) {
    fields.push("start_date = ?");
    values.push(start_date);
  }
  if (end_date) {
    fields.push("end_date = ?");
    values.push(end_date);
  }
  if (status !== undefined) {
    fields.push("status = ?");
    values.push(status);
  }

  if (fields.length === 0)
    return res.status(400).json({ message: "No fields to update" });

  values.push(user.id_user, req.params.id);
  const query = `UPDATE budget_plan SET ${fields.join(
    ", "
  )} WHERE id_user = ? AND id_budget = ?`;

  db.query(query, values, (err, result) => {
    if (err) {
      console.error("Error updating budget plan:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }
    res.json({ message: "Budget plan updated successfully" });
  });
};

// * Delete Budget Plan
exports.deleteBudgetPlan = (req, res) => {
  const user = req.session.user;
  if (!user) return res.status(401).json({ message: "Unauthorized" });

  const query = "DELETE FROM budget_plan WHERE id_user = ? AND id_budget = ?";

  db.query(query, [user.id_user, req.params.id], (err, result) => {
    if (err) {
      console.error("Error deleting budget plan:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }
    res.json({ message: "Budget plan deleted successfully" });
  });
};

module.exports = {
  createBudgetPlan: exports.createBudgetPlan,
  getUserBudgetPlans: exports.getAllBudgetPlans,
  getBudgetById: exports.getBudgetPlanById,
  updateBudgetPlan: exports.updateBudgetPlan,
  deleteBudgetPlan: exports.deleteBudgetPlan,
};
