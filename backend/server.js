require("dotenv").config();
const express = require("express");         // Node.JS Framework buat API ama WebApp
const cors = require("cors");               // Cross-Origin Resource Sharing
const bodyParser = require("body-parser");  // Parse untuk req.body etc
const session = require("express-session"); // Middleware for Storing Session ID

const app = express();
const PORT = process.env.PORT || 3000;

// * Route Handlers
const userRoutes = require("./routes/userRoutes");
const transactionRoutes = require("./routes/transactionRoutes");
const budgetRoutes = require("./routes/budgetRoutes");
const goalRoutes = require("./routes/goalRoutes");
const contactRoutes = require("./routes/contactRoutes");

// * Middleware
app.use(
  session({
    secret: "secret_key",
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false },
  }) 
);

app.use(cors());
app.use(bodyParser.json());  

// * Test API   
app.get("/", (req, res) => {
  res.send("coreWallet API is running..."); 
});

// * Route Handlers
app.use("/users", userRoutes);
app.use("/transactions", transactionRoutes);
app.use("/budget", budgetRoutes);     
app.use("/goal", goalRoutes); 
app.use("/contact", contactRoutes);

// * 404 Handler
app.use((req, res) => {
  res.status(404).json({ message: "Route not found" });
});

// * Start
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`); 
}); 

// * Setup
// npm init -y
// npm install express mysql2 dotenv cors body-parser
// npm install -g nodemon (auto-refresh kalo ada change) 

// * Start Server 
// npm install (sekali aja diawal) 
// npm start 
// OR  
// npm run dev (kalo mo pake nodemon, biar ngerjain backend auto refresh changesnya)