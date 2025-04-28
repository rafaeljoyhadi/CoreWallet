const express = require("express");
const router = express.Router();
const contactController = require("../controllers/contactController");

router.post("/", contactController.addContact);
router.get("/", contactController.getContacts);
router.delete("/:saved_id_user", contactController.deleteContact);

module.exports = router;
