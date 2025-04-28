const db = require("../config/db");

// * Top-Up Function
// {
//     "amount": 500000
// }
exports.topUp = (req, res) => {
  const { amount } = req.body;
  const sender_account_number = req.session.user?.account_number;

  if (!sender_account_number) {
    return res.status(401).json({ message: "Unauthorized: Please log in" });
  }

  if (!amount || amount <= 0) {
    return res.status(400).json({ message: "Invalid top-up request" });
  }

  // Find the user by account number
  db.query(
    "SELECT id_user, balance FROM user WHERE account_number = ?",
    [sender_account_number],
    (userErr, userResults) => {
      if (userErr) {
        console.error("Database error:", userErr);
        return res.status(500).json({ message: "Database error" });
      }

      if (userResults.length === 0) {
        return res.status(404).json({ message: "User not found" });
      }

      const user = userResults[0];

      // Insert 'Top-Up' into the category ID
      db.query(
        "SELECT id_category FROM transaction_category WHERE category_name = 'Top-Up' LIMIT 1",
        (catErr, catResults) => {
          if (catErr) {
            console.error("Error fetching category:", catErr);
            return res.status(500).json({ message: "Database error" });
          }

          if (catResults.length === 0) {
            return res
              .status(500)
              .json({ message: "Top-Up category not found" });
          }

          const id_category = catResults[0].id_category;
          const newBalance = user.balance + amount;

          // Update user balance
          db.query(
            "UPDATE user SET balance = ? WHERE id_user = ?",
            [newBalance, user.id_user],
            (updateErr) => {
              if (updateErr) {
                console.error("Error updating balance:", updateErr);
                return res
                  .status(500)
                  .json({ message: "Failed to update balance" });
              }

              // Record the transaction into the transaction log
              const insertQuery = `
                INSERT INTO transaction (
                  source_id_user, target_id_user, id_category,
                  transaction_type, amount, status, note
                ) VALUES (?, NULL, ?, 'Top-Up', ?, 0, 'User top-up')
              `;

              db.query(
                insertQuery,
                [user.id_user, id_category, amount],
                (insertErr) => {
                  if (insertErr) {
                    console.error("Error inserting transaction:", insertErr);
                    return res
                      .status(500)
                      .json({ message: "Failed to record transaction" });
                  }

                  res.json({
                    message: "Top-Up successful",
                    new_balance: newBalance,
                  });
                }
              );
            }
          );
        }
      );
    }
  );
};

// * Transfer to User
// {
//     "recipient_account_number": "99785918798",
//     "amount": 100000,
//     "note": "Dinner payment"
// }
exports.transferToUser = (req, res) => {
  const { recipient_account_number, amount, note } = req.body;
  const sender_account_number = req.session.user?.account_number; // Auto-detect sender

  if (!sender_account_number) {
    return res.status(401).json({ message: "Unauthorized: Please log in" });
  }

  if (!recipient_account_number || !amount || amount <= 0) {
    return res.status(400).json({ message: "Invalid transfer request" });
  }

  if (sender_account_number === recipient_account_number) {
    return res.status(400).json({ message: "Cannot transfer to yourself" });
  }

  const query =
    "SELECT id_user, balance, account_number FROM user WHERE account_number IN (?, ?)";
  db.query(
    query,
    [sender_account_number, recipient_account_number],
    (userErr, userResults) => {
      if (userErr) {
        console.error("Database error:", userErr);
        return res.status(500).json({ message: "Database error" });
      }

      if (userResults.length !== 2) {
        return res
          .status(404)
          .json({ message: "One or both account numbers not found" });
      }

      const sender = userResults.find(
        (user) => user.account_number === sender_account_number
      );
      const recipient = userResults.find(
        (user) => user.account_number === recipient_account_number
      );

      if (!sender || !recipient) {
        return res.status(404).json({ message: "User not found" });
      }

      if (sender.balance < amount) {
        return res.status(400).json({ message: "Insufficient balance" });
      }

      const categoryQuery =
        "SELECT id_category FROM transaction_category WHERE category_name = 'Transfer' LIMIT 1";
      db.query(categoryQuery, (catErr, catResults) => {
        if (catErr) {
          console.error("Error fetching category:", catErr);
          return res.status(500).json({ message: "Database error" });
        }

        if (catResults.length === 0) {
          return res
            .status(500)
            .json({ message: "Transfer category not found" });
        }

        const id_category = catResults[0].id_category;

        const senderNewBalance = sender.balance - amount;
        const recipientNewBalance = recipient.balance + amount;

        db.beginTransaction((err) => {
          if (err) {
            console.error("Transaction start error:", err);
            return res.status(500).json({ message: "Database error" });
          }

          db.query(
            "UPDATE user SET balance = ? WHERE id_user = ?",
            [senderNewBalance, sender.id_user],
            (updateSenderErr) => {
              if (updateSenderErr) {
                return db.rollback(() => {
                  console.error(
                    "Error updating sender balance:",
                    updateSenderErr
                  );
                  res
                    .status(500)
                    .json({ message: "Failed to update sender balance" });
                });
              }

              db.query(
                "UPDATE user SET balance = ? WHERE id_user = ?",
                [recipientNewBalance, recipient.id_user],
                (updateRecipientErr) => {
                  if (updateRecipientErr) {
                    return db.rollback(() => {
                      console.error(
                        "Error updating recipient balance:",
                        updateRecipientErr
                      );
                      res.status(500).json({
                        message: "Failed to update recipient balance",
                      });
                    });
                  }

                  const insertQuery = `
                INSERT INTO transaction (source_id_user, target_id_user, id_category, transaction_type, amount, status, note)
                VALUES (?, ?, ?, 'Transfer', ?, 0, ?)
              `;

                  db.query(
                    insertQuery,
                    [
                      sender.id_user,
                      recipient.id_user,
                      id_category,
                      amount,
                      note || "User-to-User Transfer",
                    ],
                    (insertErr) => {
                      if (insertErr) {
                        return db.rollback(() => {
                          console.error(
                            "Error inserting transaction:",
                            insertErr
                          );
                          res
                            .status(500)
                            .json({ message: "Failed to record transaction" });
                        });
                      }

                      db.commit((commitErr) => {
                        if (commitErr) {
                          return db.rollback(() => {
                            console.error(
                              "Transaction commit error:",
                              commitErr
                            );
                            res
                              .status(500)
                              .json({ message: "Transaction failed" });
                          });
                        }

                        res.json({
                          message: "Transfer successful",
                          sender_new_balance: senderNewBalance,
                          recipient_new_balance: recipientNewBalance,
                        });
                      });
                    }
                  );
                }
              );
            }
          );
        });
      });
    }
  );
};

// * Get Transaction History
exports.getTransactionHistory = (req, res) => {
  const user_account_number = req.session.user?.account_number;

  if (!user_account_number) {
    return res.status(401).json({ message: "Unauthorized: Please log in" });
  }

  let { transaction_type, start_date, end_date, sort, category_id } = req.query;
  let query = `
    SELECT 
      t.id_transaction,
      t.datetime,
      t.transaction_type,
      t.amount,
      t.status,
      t.note,
      tc.category_name,
      u_sender.username AS sender_username,
      u_sender.account_number AS sender_account_number,
      u_receiver.username AS receiver_username,
      u_receiver.account_number AS receiver_account_number
    FROM transaction t
    LEFT JOIN transaction_category tc ON t.id_category = tc.id_category
    LEFT JOIN user u_sender ON t.source_id_user = u_sender.id_user
    LEFT JOIN user u_receiver ON t.target_id_user = u_receiver.id_user
    WHERE (u_sender.account_number = ? OR u_receiver.account_number = ?)
  `;

  let queryParams = [user_account_number, user_account_number];

  // 🔹 Filter by Transaction Type
  if (transaction_type) {
    query += " AND t.transaction_type = ?";
    queryParams.push(transaction_type);
  }

  // 🔹 Filter by Date Range
  if (start_date) {
    query += " AND DATE(t.datetime) >= ?";
    queryParams.push(start_date);
  }
  if (end_date) {
    query += " AND DATE(t.datetime) <= ?";
    queryParams.push(end_date);
  }

  // 🔹 Filter by Category
  if (category_id) {
    query += " AND t.id_category = ?";
    queryParams.push(category_id);
  }

  // 🔹 Sorting (default: newest first)
  if (sort === "oldest") {
    query += " ORDER BY t.datetime ASC";
  } else {
    query += " ORDER BY t.datetime DESC";
  }

  // 🔹 Execute the Query
  db.query(query, queryParams, (err, results) => {
    if (err) {
      console.error("Error fetching transactions:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }

    res.json(results);
  });
};
