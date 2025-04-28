const express = require("express");
const router = express.Router();
const goalController = require("../controllers/goalController");

router.post("/", goalController.createGoal);
router.get("/", goalController.getGoals);
router.get("/:id", goalController.getGoalById);
router.put("/:id", goalController.updateGoal);
router.post("/:id/deposit", goalController.depositToGoal);
router.post("/:id/withdraw", goalController.withdrawFromGoal);
router.delete("/:id", goalController.deleteGoal);

module.exports = router;
