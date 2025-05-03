const express = require("express");
const router = express.Router();
const budgetController = require("../controllers/budgetController");

router.post("/", budgetController.createBudgetPlan);
router.get("/", budgetController.getUserBudgetPlans);
router.get("/:id", budgetController.getBudgetById);
router.put("/:id", budgetController.updateBudgetPlan);
router.delete("/:id", budgetController.deleteBudgetPlan);

module.exports = router;