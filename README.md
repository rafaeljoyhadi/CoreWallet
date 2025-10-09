# coreWallet

## Project Overview
**coreWallet** is a digital e-wallet system designed to help users manage their personal finances efficiently. It integrates money management features such as balance tracking, budgeting, goal saving, and transaction management — all within a secure, user-friendly ecosystem.

---

## Technical Stack

### Frontend (Mobile)
- **Android (Kotlin)**
- **Retrofit** for API communication
- **XML layouts** for UI
- Transitioned from **Activities** to **Fragments** for modular design
- Implements **session-based authentication** (login persistence)

### Backend
- **Node.js** with **Express.js**
- **MySQL** database
- **MySQL Workbench** for schema and data management
- RESTful API architecture with modular routing

### Architecture
- Follows **MVC** (Model–View–Controller) design pattern  
- Endpoints structured by feature: `/auth`, `/wallet`, `/budget`, `/goal`, `/contact`

---

## Core Features

### 1. User Authentication
- Registration, Login, and Logout endpoints  
- Password hashing and input validation  
- Session-based authentication for Android clients using Retrofit  

### 2. Balance & Transactions
- Each user has a wallet balance  
- Users can top up their balance through the app  
- All transactions are stored in the database for traceability  
- User can view their transaction history with distinction (income is green, expense is red)

### 3. coreBudget — Budget Planner & Tracker
- Create budget plans with:
  - Name  
  - Category (e.g., Food, Transport, Entertainment)  
  - Amount  
  - Start and End Dates  
- Tracks spending against the defined budget  
- Helps users maintain financial discipline  

### 4. coreGoal — Savings & Financial Goals
- Users set financial goals (e.g., “Buy a new phone”)  
- Define:
  - Goal name  
  - Target amount  
  - Saved amount (updated dynamically)  
- Deposit or withdraw funds (partial or full)  
- System ensures:
  - Deposits cannot exceed target amount  
  - Withdrawals cannot exceed current savings  

### 5. coreContact — Contact Management
- Manage user contacts within the wallet system  
- Supports quick lookup or transfers between coreWallet users  
- Foundation for peer-to-peer transaction functionality  

---

## Design Highlights
- Modular and scalable backend routing  
- Data integrity handled with MySQL constraints  
- Clean, well-documented controller logic  
- Consistent JSON API responses for smooth Android integration  
- Backend tested locally for stability and accuracy  

---

## User Interface
- Minimalist design inspired by modern digital wallets  
- Dashboard displays:
  - Username  
  - Account number  
  - Current balance  
- Fragment-based navigation (Dashboard, Budget, Goals, Contact)  
- Success toasts and redirect flow for authentication events  

---

## Setup and Installation

### 1. Backend Setup (Node.js + Express.js)

#### Prerequisites
- Node.js v16 or newer  
- MySQL server  
- MySQL Workbench (optional, for GUI management)  

#### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/corewallet.git
   cd corewallet/backend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Create a `.env` file in the backend root directory:
   ```bash
   DB_HOST=localhost
   DB_USER=root
   DB_PASSWORD=yourpassword
   DB_NAME=corewallet_db
   PORT=3000
   ```

4. Import the SQL schema into MySQL:
   - Open MySQL Workbench  
   - Create a database named `corewallet_db`  
   - Run the provided `.sql` file in `/database/schema.sql` (if available)

5. Start the backend server:
   ```bash
   npm start
   ```
   The API will run on `http://localhost:3000`.

---

### 2. Android App Setup (Frontend)

#### Prerequisites
- Android Studio (latest version)
- Minimum SDK: 26
- Target SDK: 34

#### Steps
1. Clone or open the Android project:
   ```bash
   cd corewallet/android
   ```

2. Open the project in Android Studio.

3. Update the API base URL in your Retrofit client (e.g., `ApiClient.kt`):
   ```kotlin
   const val BASE_URL = "http://your-local-ip:3000/"
   ```

4. Build and run the app on an emulator or physical device.

---
