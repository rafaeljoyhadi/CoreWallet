const db = require("../config/db");

// * Top-Up Function
exports.topUp = (req, res) => {
  const { amount } = req.body;
  const sender_account_number = req.session.user?.account_number;

  if (!sender_account_number) {
    return res.status(401).json({ message: "Unauthorized: Please log in" });
  }
  if (!amount || amount <= 0) {
    return res.status(400).json({ message: "Invalid top-up request" });
  }

  // 1) Find the user
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
      const newBalance = user.balance + amount;

      // 2) Lookup Top-Up category ID
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
          const isCredit = 1;

          // 3) Begin atomic transaction
          db.beginTransaction((txErr) => {
            if (txErr) {
              console.error("Transaction start error:", txErr);
              return res.status(500).json({ message: "Database error" });
            }

            // a) Update the user's balance
            db.query(
              "UPDATE user SET balance = ? WHERE id_user = ?",
              [newBalance, user.id_user],
              (updateErr) => {
                if (updateErr) {
                  return db.rollback(() => {
                    console.error("Error updating balance:", updateErr);
                    res
                      .status(500)
                      .json({ message: "Failed to update balance" });
                  });
                }

                // b) Insert the transaction record with is_credit = 1
                const insertQuery = `
                  INSERT INTO transaction (
                    source_id_user,
                    target_id_user,
                    id_category,
                    transaction_type,
                    amount,
                    status,
                    note,
                    is_credit
                  ) VALUES (?, NULL, ?, 'Top-Up', ?, 0, 'Top-Up', 0)
                `;
                db.query(
                  insertQuery,
                  [user.id_user, id_category, amount, isCredit],
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

                    // c) Commit
                    db.commit((commitErr) => {
                      if (commitErr) {
                        return db.rollback(() => {
                          console.error("Transaction commit error:", commitErr);
                          res
                            .status(500)
                            .json({ message: "Transaction failed" });
                        });
                      }

                      // Success!
                      res.json({
                        message: "Top-Up successful",
                        new_balance: newBalance,
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

// * Transfer to User
exports.transferToUser = (req, res) => {
  const { recipient_account_number, amount, note } = req.body;
  const sender_account_number = req.session.user?.account_number;

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
        (u) => u.account_number === sender_account_number
      );
      const recipient = userResults.find(
        (u) => u.account_number === recipient_account_number
      );

      if (!sender || !recipient) {
        return res.status(404).json({ message: "User not found" });
      }

      if (sender.balance < amount) {
        return res.status(400).json({ message: "Insufficient balance" });
      }

      const categoryQuery =
        "SELECT id_category FROM transaction_category WHERE category_name = ? LIMIT 1";
      db.query(categoryQuery, [req.body.categoryName], (catErr, catResults) => {
        if (catErr || catResults.length === 0) {
          return res
            .status(400)
            .json({ message: "Unknown transaction category" });
        }

        if (catResults.length === 0) {
          return res
            .status(500)
            .json({ message: "Transfer category not found" });
        }

        const id_category = catResults[0].id_category;
        const categoryName = req.body.categoryName;
        const senderNewBalance = sender.balance - amount;
        const recipientNewBalance = recipient.balance + amount;
        const isCredit = 1;
         
        db.beginTransaction((err) => {   
          if (err) {
            console.error("Transaction start error:", err);
            return res.status(500).json({ message: "Database error" });
          }

          // 1) deduct sender
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

              // 2) credit recipient
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

                  console.log("About to insert transaction:", {
                    source: sender.id_user,
                    target: recipient.id_user,
                    category: id_category,
                    categoryName: "Transfer",
                    isCredit,
                    amount,
                    note: note || "User-to-User Transfer",
                  });

                  // 3) record the transaction
                  const insertQuery = `
                  INSERT INTO \`transaction\`
                    (source_id_user,
                    target_id_user,
                    id_category,
                    transaction_category_name,
                    is_credit,
                    status,
                    amount,
                    note)
                  VALUES (?, ?, ?, ?, ?, 0, ?, ?)
                `;

                  db.query(
                    insertQuery,
                    [
                      sender.id_user,
                      recipient.id_user,
                      id_category,
                      categoryName,
                      isCredit,
                      amount,
                      note || "User-to-User Transfer",
                    ],
                    (insertErr, insertResult) => {
                      if (insertErr) {
                        console.error(
                          "Error inserting transaction:",
                          insertErr
                        );
                        return db.rollback(() =>
                          res
                            .status(500)
                            .json({ message: "Failed to record transaction" })
                        );
                      }
                      console.log("insertResult:", insertResult);

                      // 4) commit everything
                      db.commit((commitErr) => {
                        if (commitErr) {
                          console.error("Commit error:", commitErr);
                          return db.rollback(() =>
                            res
                              .status(500)
                              .json({ message: "Transaction failed" })
                          );
                        }

                        // 🔍 DEBUG: fetch the newly inserted row
                        db.query(
                          "SELECT * FROM `transaction` WHERE id_transaction = ?",
                          [insertResult.insertId],
                          (fetchErr, rows) => {
                            if (fetchErr) {
                              console.error(
                                "Error fetching new transaction:",
                                fetchErr
                              );
                            } else {
                              console.log("📝 New transaction row:", rows[0]);
                            }

                            // final response
                            res.json({
                              message: "Transfer successful",
                              sender_new_balance: senderNewBalance,
                              recipient_new_balance: recipientNewBalance,
                              transaction_id: insertResult.insertId,
                              transaction: rows ? rows[0] : null,
                            });
                          }
                        );
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
  const userAccount = req.session.user?.account_number;
  if (!userAccount) {
    return res.status(401).json({ message: "Unauthorized: Please log in" });
  }

  let { transaction_type, start_date, end_date, sort, category_id } = req.query;
  let sql = `
    SELECT 
      t.id_transaction,
      DATE_FORMAT(t.datetime, '%Y-%m-%d') AS datetime,
      t.transaction_category_name AS transaction_type,
      t.amount,
      t.status,
      t.note,
      t.is_credit,   
      tc.category_name,
      u1.username AS sender_username,
      u1.account_number AS sender_account_number,
      u2.username AS receiver_username,
      u2.account_number AS receiver_account_number
    FROM \`transaction\` t
    LEFT JOIN transaction_category tc ON t.id_category = tc.id_category
    LEFT JOIN \`user\` u1 ON t.source_id_user = u1.id_user
    LEFT JOIN \`user\` u2 ON t.target_id_user = u2.id_user 
    WHERE (u1.account_number = ? OR u2.account_number = ?)
  `;
  const params = [userAccount, userAccount];

  if (transaction_type) {
    sql += " AND t.transaction_category_name = ?";
    params.push(transaction_type);
  }
  if (start_date) {
    sql += " AND DATE(t.datetime) >= ?";
    params.push(start_date);
  }
  if (end_date) {
    sql += " AND DATE(t.datetime) <= ?";
    params.push(end_date);
  }
  if (category_id) {
    sql += " AND t.id_category = ?";
    params.push(category_id);
  }

  sql +=
    sort === "oldest"
      ? " ORDER BY t.datetime ASC"
      : " ORDER BY t.datetime DESC";

  db.query(sql, params, (err, results) => {
    if (err) {
      console.error("Error fetching transactions:", err);
      return res.status(500).json({ message: "Database error", error: err });
    }
    res.json(results);
  });
};

// * Get Transaction Category List
exports.getTransactionCategories = (req, res) => {
  const user = req.session.user;

  if (!user) {
    return res.status(401).json({
      message: "Unauthorized: Please log in to access categories",
    });
  }

  const userId = user.id_user;

  let query = `
    SELECT id_category, category_name 
    FROM transaction_category 
    WHERE id_user IS NULL OR id_user = ?
    ORDER BY id_user IS NOT NULL DESC, category_name ASC
  `;

  db.query(query, [userId], (err, results) => {
    if (err) {
      console.error("Error fetching transaction categories:", err);
      return res.status(500).json({
        message: "Database error",
        error: err,
      });
    }

    res.json(results);
  });
};
