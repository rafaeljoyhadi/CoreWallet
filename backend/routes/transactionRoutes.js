const express = require("express");
const router = express.Router();
const transactionController = require("../controllers/transactionController");

router.post("/topup", transactionController.topUp);
router.post("/withdraw", transactionController.withdraw);
router.post("/transfer", transactionController.transferToUser);
router.get("/history", transactionController.getTransactionHistory);
router.get("/categories", transactionController.getTransactionCategories);

module.exports = router;
