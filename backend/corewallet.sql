-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 20, 2025 at 06:07 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `corewallet`
--

-- --------------------------------------------------------

--
-- Table structure for table `budget_goal`
--

CREATE TABLE `budget_goal` (
  `id_goal` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `goal_name` varchar(64) NOT NULL,
  `target_amount` bigint(20) NOT NULL,
  `saved_amount` bigint(20) DEFAULT 0,
  `deadline` date NOT NULL,
  `status` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `budget_goal`
--

INSERT INTO `budget_goal` (`id_goal`, `id_user`, `goal_name`, `target_amount`, `saved_amount`, `deadline`, `status`) VALUES
(1, 3, 'Vacation', 15000000, 0, '2025-12-31', 0),
(5, 4, 'Vacation', 20, 14, '2025-12-31', 0),
(6, 4, 'Test Goal Made', 123123, 0, '2025-05-06', 2);

-- --------------------------------------------------------

--
-- Table structure for table `budget_plan`
--

CREATE TABLE `budget_plan` (
  `id_budget` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `plan_name` varchar(64) NOT NULL,
  `id_category` int(11) NOT NULL,
  `amount_limit` bigint(20) NOT NULL,
  `spent_amount` bigint(20) DEFAULT 0,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `budget_plan`
--

INSERT INTO `budget_plan` (`id_budget`, `id_user`, `plan_name`, `id_category`, `amount_limit`, `spent_amount`, `start_date`, `end_date`, `status`) VALUES
(4, 4, 'Testsss', 2, 11111111, 0, '2025-05-01', '2025-05-31', 0),
(6, 4, 'testsetsetsetstestse sguiofgsergesrgeg', 2, 123123123, 0, '2025-05-01', '2025-05-30', 0),
(7, 4, 'This is now billing', 5, 15000000, 0, '2025-05-01', '2025-05-31', 0);

-- --------------------------------------------------------

--
-- Table structure for table `contact`
--

CREATE TABLE `contact` (
  `id_user` int(11) NOT NULL,
  `saved_id_user` int(11) NOT NULL,
  `datetime` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contact`
--

INSERT INTO `contact` (`id_user`, `saved_id_user`, `datetime`) VALUES
(4, 1, '2025-05-17 20:44:56'),
(4, 3, '2025-05-17 20:45:06'),
(4, 11, '2025-05-18 14:57:51');

-- --------------------------------------------------------

--
-- Table structure for table `log_login`
--

CREATE TABLE `log_login` (
  `timestamp` datetime DEFAULT current_timestamp(),
  `id_user` int(11) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `note` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `log_login`
--

INSERT INTO `log_login` (`timestamp`, `id_user`, `name`, `user_agent`, `note`) VALUES
('2025-04-20 16:49:25', 3, 'Joy2', 'PostmanRuntime/7.43.2', 'User logged in'),
('2025-04-20 17:10:02', 3, 'Joy2', 'PostmanRuntime/7.43.2', 'User logged in'),
('2025-04-20 17:10:05', 3, 'Joy2', 'PostmanRuntime/7.43.2', 'User logged out'),
('2025-04-21 07:06:40', 3, 'Joy2', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-04-21 07:49:02', 3, 'Joy2', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-04-21 07:51:25', 3, 'Joy2', 'PostmanRuntime/7.43.3', 'User logged out'),
('2025-04-21 07:51:32', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-04-21 11:18:19', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-04-21 11:55:19', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-05-01 13:20:48', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:23:58', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:24:28', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:36:10', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:45:52', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-05-01 13:46:16', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged out'),
('2025-05-01 13:51:49', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:56:49', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 13:57:22', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:06:44', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:11:03', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:11:19', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-05-01 14:16:19', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:20:18', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:21:00', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-05-01 14:21:03', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged out'),
('2025-05-01 14:22:23', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:25:07', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 14:25:38', 4, 'joy3', 'PostmanRuntime/7.43.3', 'User logged in'),
('2025-05-01 15:13:07', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-01 21:08:27', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 09:32:54', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 12:43:48', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 13:12:49', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 13:12:52', 4, 'joy3', 'okhttp/3.14.9', 'User logged out'),
('2025-05-02 13:13:00', 5, 'Joy1', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 13:13:02', 5, 'Joy1', 'okhttp/3.14.9', 'User logged out'),
('2025-05-02 18:59:41', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 18:59:43', 4, 'joy3', 'okhttp/3.14.9', 'User logged out'),
('2025-05-02 18:59:51', 3, 'Joy2', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 18:59:53', 3, 'Joy2', 'okhttp/3.14.9', 'User logged out'),
('2025-05-02 19:01:13', 11, 'Rafael Joy Hadi', 'okhttp/3.14.9', 'User logged in'),
('2025-05-02 19:01:18', 11, 'Rafael Joy Hadi', 'okhttp/3.14.9', 'User logged out'),
('2025-05-05 07:46:00', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-05 07:46:16', 4, 'joy3', 'okhttp/3.14.9', 'User logged out'),
('2025-05-05 07:46:26', 5, 'Joy1', 'okhttp/3.14.9', 'User logged in'),
('2025-05-05 07:46:35', 5, 'Joy1', 'okhttp/3.14.9', 'User logged out'),
('2025-05-10 00:32:35', 4, 'joy3', 'okhttp/3.14.9', 'User logged in'),
('2025-05-16 09:54:49', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 09:55:30', 4, 'joy3', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 09:55:39', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 09:55:44', 3, 'Joy2', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 19:31:33', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 19:31:35', 3, 'Joy2', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 19:32:02', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:03:18', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:03:43', 3, 'Joy2', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 20:04:27', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:07:37', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:09:21', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:09:33', 3, 'Joy2', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 20:29:42', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:32:04', 5, 'Joy1', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:38:53', 5, 'Joy1', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:39:42', 5, 'Joy1', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 20:39:56', 5, 'Joy1', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:50:48', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:53:57', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:55:44', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:56:53', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 20:59:59', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:20:06', 5, 'Joy1', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:24:47', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:25:23', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:32:06', 1, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 21:33:37', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:33:44', 1, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 21:34:09', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:34:21', 1, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 21:35:42', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:36:06', 1, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 21:39:54', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:59:54', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 21:59:54', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 22:01:24', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 22:05:29', 1, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 22:05:47', 1, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 22:12:04', 3, 'Joy2', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 22:16:10', 3, 'Joy2', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 22:16:59', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 22:17:28', 4, 'joy3', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-16 23:07:09', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:07:09', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:07:27', 4, 'joy3', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:07:32', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:07:55', 4, 'joy3', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:08:01', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:11:02', 4, 'joy3', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:14:26', 4, 'joy3', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:18:28', 4, 'joy3', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:18:43', 4, 'a', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:23:31', 4, 'a', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:23:42', 4, 'a', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:27:50', 4, 'a', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:28:37', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:31:00', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:31:12', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:34:00', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-16 23:36:21', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:38:38', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-16 23:41:43', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 00:08:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 00:09:32', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 00:09:58', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 00:22:37', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 00:22:46', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 00:57:18', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 01:13:26', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 01:16:21', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 01:19:18', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 01:20:34', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 12:02:36', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 12:54:29', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 12:55:16', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:24:17', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:25:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:25:16', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:26:04', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:26:14', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:47:04', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:47:14', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:52:05', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:52:19', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:52:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:53:01', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:53:22', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:53:33', 5, 'Joy1', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:53:41', 5, 'Joy1', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:53:47', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 13:56:19', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 13:56:30', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:05:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:20:58', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 14:24:04', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 14:34:17', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged out'),
('2025-05-17 14:43:13', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:45:56', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 14:46:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:46:39', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 14:46:48', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:48:28', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 14:52:15', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged out'),
('2025-05-17 14:52:30', 11, 'Rafael Joy Hadi', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 14:53:11', 11, 'Rafael Joy Hadi', 'PostmanRuntime/7.43.4', 'User logged out'),
('2025-05-17 14:53:38', 11, 'Rafael Joy Hadi', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 14:53:48', 11, 'Rafael Joy Hadi', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 14:53:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:10:42', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:10:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:15:01', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 15:19:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:20:38', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:20:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:31:14', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:32:09', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:37:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:37:59', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:39:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:40:04', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 15:41:59', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-17 15:42:09', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 16:20:06', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 16:23:27', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 18:51:15', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 18:53:46', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 18:58:18', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:01:31', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:09:41', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:11:29', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:12:59', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:14:25', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:17:42', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:21:00', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:38:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 19:54:19', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 20:02:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:03:11', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:04:03', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:04:19', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 20:08:58', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 20:09:45', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged out'),
('2025-05-17 20:09:57', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:18:10', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:19:12', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:41:35', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:43:37', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:43:47', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 20:44:09', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged out'),
('2025-05-17 20:44:38', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:45:16', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 20:48:58', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:51:11', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 20:52:01', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:05:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:07:24', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:16:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:18:36', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:32:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:36:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:37:53', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 21:39:21', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:47:06', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 21:48:57', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-17 22:00:49', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 22:02:29', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 22:04:21', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 22:05:07', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-17 22:05:31', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:02:16', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:03:54', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:06:53', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:08:17', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:10:42', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 14:19:57', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:21:38', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:26:18', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:30:24', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:32:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:35:05', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:39:11', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:40:36', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:41:30', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:43:28', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:44:34', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:45:59', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:46:41', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:46:41', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:48:48', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:50:01', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:53:44', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:56:06', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 14:57:37', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:02:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:27:45', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:34:32', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:36:40', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:38:36', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:39:50', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 15:53:37', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 15:55:30', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:04:16', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 16:05:47', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 16:14:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:16:22', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:19:15', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:21:39', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:33:11', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:37:14', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:42:10', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 16:49:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:49:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:51:23', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:53:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:57:27', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 16:59:56', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:01:51', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 17:21:01', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 17:21:45', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:23:31', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:28:00', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:29:03', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:29:31', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:32:15', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:32:43', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 17:34:49', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:37:16', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:40:34', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:43:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-18 17:44:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:45:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 17:45:57', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-18 17:48:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-18 18:01:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-18 18:13:46', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:39:45', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:47:06', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:52:46', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:53:52', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:54:15', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-19 01:54:28', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:54:43', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 01:55:15', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-19 01:55:48', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 01:56:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 01:58:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-19 01:59:03', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:07:43', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:10:26', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:16:33', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:18:04', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 02:19:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:23:53', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:37:15', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 02:40:53', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 02:41:57', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in'),
('2025-05-19 02:43:51', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:46:09', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:47:45', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:48:32', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:51:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:54:20', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:54:20', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 02:57:50', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:03:08', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:13:26', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:18:54', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:21:07', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:35:02', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:41:58', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:43:21', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:43:50', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 03:46:23', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 08:11:30', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 08:19:59', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 08:21:33', 4, 'Joy', 'okhttp/4.9.3', 'User logged out'),
('2025-05-19 08:25:29', 4, 'Joy', 'okhttp/4.9.3', 'User logged in'),
('2025-05-19 08:41:30', 4, 'Joy', 'PostmanRuntime/7.43.4', 'User logged in');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id_notification` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `id_transaction` int(11) NOT NULL,
  `datetime` datetime DEFAULT current_timestamp(),
  `source_id_user` int(11) NOT NULL,
  `target_id_user` int(11) DEFAULT NULL,
  `id_category` int(11) NOT NULL,
  `transaction_category_name` varchar(64) DEFAULT NULL,
  `is_credit` tinyint(1) NOT NULL DEFAULT 0,
  `status` tinyint(1) DEFAULT 0,
  `amount` bigint(20) NOT NULL,
  `note` varchar(64) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`id_transaction`, `datetime`, `source_id_user`, `target_id_user`, `id_category`, `transaction_category_name`, `is_credit`, `status`, `amount`, `note`) VALUES
(1, '2025-04-20 16:49:28', 3, NULL, 1, 'Top-Up', 0, 0, 500000, 'User top-up'),
(2, '2025-04-20 16:49:29', 3, NULL, 1, 'Top-Up', 0, 0, 500000, 'User top-up'),
(3, '2025-04-20 16:49:30', 3, NULL, 1, 'Top-Up', 0, 0, 500000, 'User top-up'),
(4, '2025-04-20 16:49:46', 3, 4, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(5, '2025-04-20 16:49:48', 3, 4, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(6, '2025-04-20 16:49:49', 3, 4, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(7, '2025-04-20 16:49:50', 3, 4, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(8, '2025-04-20 16:49:50', 3, 4, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(9, '2025-04-21 11:19:31', 4, NULL, 1, 'Top-Up', 0, 0, 500000, 'User top-up'),
(10, '2025-04-21 11:19:34', 4, NULL, 1, 'Top-Up', 0, 0, 5000000, 'User top-up'),
(11, '2025-04-21 11:20:30', 4, NULL, 1, 'Top-Up', 0, 0, 15000000, 'User top-up'),
(12, '2025-04-21 11:59:25', 4, 1, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(13, '2025-05-01 14:11:23', 4, NULL, 1, 'Top-Up', 0, 0, 15000000, 'User top-up'),
(14, '2025-05-01 15:13:15', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(15, '2025-05-01 15:13:34', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(16, '2025-05-01 15:13:37', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(17, '2025-05-01 21:08:30', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(18, '2025-05-01 21:08:31', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(19, '2025-05-01 21:34:20', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(20, '2025-05-01 21:35:04', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(21, '2025-05-01 21:35:05', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(22, '2025-05-01 21:35:05', 4, NULL, 1, 'Top-Up', 0, 0, 500, 'User top-up'),
(23, '2025-05-17 14:21:34', 4, 5, 6, 'Transfer', 1, 0, 100000, 'Transfer Testttt'),
(24, '2025-05-17 14:22:09', 4, NULL, 1, 'Top-Up', 0, 0, 150000, 'User top-up'),
(25, '2025-05-17 14:22:47', 4, NULL, 1, 'Top-Up', 0, 0, 17096000, 'User top-up'),
(26, '2025-05-17 14:24:13', 4, NULL, 1, 'Top-Up', 0, 0, 10000, 'User top-up'),
(27, '2025-05-17 14:48:31', 4, NULL, 1, 'Top-Up', 0, 0, 10000, 'Top-Up'),
(28, '2025-05-17 14:52:35', 11, NULL, 1, 'Top-Up', 0, 0, 10000000, 'Top-Up'),
(29, '2025-05-17 14:52:57', 11, 4, 6, 'Transfer', 1, 0, 100020, 'Transfer Test'),
(30, '2025-05-17 15:15:19', 4, 3, 6, 'Transfer', 1, 0, 10000, 'Transfer Test'),
(31, '2025-05-17 21:49:02', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(32, '2025-05-17 21:49:50', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(33, '2025-05-17 21:50:49', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(34, '2025-05-17 21:51:02', 4, 1, 6, 'Transfer', 1, 0, 1111, '1111'),
(35, '2025-05-17 21:52:04', 4, 1, 6, 'Transfer', 1, 0, 10000, '123123'),
(38, '2025-05-17 22:00:59', 4, 3, 6, 'Transfer', 1, 0, 10000, 'Transfer Test'),
(39, '2025-05-17 22:02:31', 4, 3, 6, 'Transfer', 0, 0, 6, '100'),
(40, '2025-05-17 22:04:23', 4, 3, 6, 'Transfer', 0, 0, 6, '100'),
(41, '2025-05-17 22:05:10', 4, 3, 6, 'Transfer', 0, 0, 6, '100'),
(42, '2025-05-17 22:05:34', 4, 3, 6, 'Transfer', 0, 0, 6, '100'),
(43, '2025-05-18 14:02:23', 4, 3, 6, 'Transfer', 1, 0, 100, 'Transfer Test'),
(44, '2025-05-18 14:06:56', 4, 3, 6, '6', 0, 0, 1, '100'),
(45, '2025-05-18 14:08:53', 4, 5, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(46, '2025-05-18 14:10:44', 4, 5, 6, 'Transfer', 1, 0, 100000, 'Dinner payment'),
(47, '2025-05-18 14:20:06', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(48, '2025-05-18 14:20:11', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(49, '2025-05-18 14:20:18', 4, 1, 6, 'Transfer', 1, 0, 123, '123'),
(50, '2025-05-18 14:26:35', 4, 1, 5, 'Transfer', 1, 0, 100, 'Paying my bills like the good boy I am'),
(51, '2025-05-18 14:30:31', 4, 1, 5, 'Bills', 1, 0, 123, 'testsetsteset'),
(52, '2025-05-18 14:30:43', 4, 1, 4, 'Entertainment', 1, 0, 123, 'testsetsteset'),
(53, '2025-05-18 14:33:02', 4, 1, 1, 'Top-Up', 1, 0, 123, '123'),
(54, '2025-05-18 14:35:10', 4, 1, 5, 'Bills', 1, 0, 188, '123'),
(55, '2025-05-18 14:39:20', 4, 1, 3, 'Shopping', 1, 0, 1111, '1111111'),
(56, '2025-05-18 14:40:41', 4, 1, 5, 'Bills', 1, 0, 124124, '124124'),
(57, '2025-05-18 14:41:34', 4, 1, 5, 'Bills', 1, 0, 11, 'User-to-User Transfer'),
(58, '2025-05-18 14:43:32', 4, 1, 5, 'Bills', 1, 0, 124124, 'User-to-User Transfer'),
(59, '2025-05-18 14:44:38', 4, 1, 5, 'Bills', 1, 0, 123, 'User-to-User Transfer'),
(60, '2025-05-18 14:46:04', 4, 1, 5, 'Bills', 1, 0, 123, '123'),
(61, '2025-05-18 14:46:46', 4, 1, 5, 'Bills', 1, 0, 111, 'User-to-User Transfer'),
(62, '2025-05-18 14:47:12', 4, 1, 5, 'Bills', 1, 0, 111, 'User-to-User Transfer'),
(63, '2025-05-18 14:48:52', 4, 1, 5, 'Bills', 1, 0, 1, 'User-to-User Transfer'),
(64, '2025-05-18 14:50:05', 4, 1, 5, 'Bills', 1, 0, 123, 'User-to-User Transfer'),
(65, '2025-05-18 14:58:42', 4, 11, 4, 'Entertainment', 1, 0, 123123, '123123'),
(66, '2025-05-19 03:38:35', 4, 1, 3, 'Shopping', 1, 0, 123123, '123123');

-- --------------------------------------------------------

--
-- Table structure for table `transaction_category`
--

CREATE TABLE `transaction_category` (
  `id_category` int(11) NOT NULL,
  `id_user` int(11) DEFAULT NULL,
  `category_name` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction_category`
--

INSERT INTO `transaction_category` (`id_category`, `id_user`, `category_name`) VALUES
(5, NULL, 'Bills'),
(4, NULL, 'Entertainment'),
(2, NULL, 'Food'),
(7, NULL, 'Other/Miscellaneous'),
(3, NULL, 'Shopping'),
(1, NULL, 'Top-Up'),
(6, NULL, 'Transfer');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(16) NOT NULL,
  `password` varchar(64) NOT NULL,
  `pin` char(6) NOT NULL,
  `email` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `account_number` varchar(16) NOT NULL,
  `balance` bigint(20) DEFAULT 0,
  `image_path` varchar(255) DEFAULT 'default_image_path'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `pin`, `email`, `name`, `account_number`, `balance`, `image_path`) VALUES
(1, 'joy', 'password', '$2b$10', 'joy@example.com', 'Joy', '99126161667', 1485591, NULL),
(3, 'joy2', 'password', '$2b$10', 'joy2@example.com', 'Joy2', '99822391408', 2020600, NULL),
(4, 'joy3', '$2b$10$v06g0X2sHPmoxIOAH.zVG.xf990vIS7bhu6glGqD5jH7THQoU2som', '$2b$10', 'joy3@gmail.com', 'Joy', '99544093297', 99302754, NULL),
(5, 'joy1', '$2b$10$92BE590qgnZjeXuqOh5aVukLpKRIQkttIrytG8zne03eYNOYOmpbi', '$2b$10', 'joy1@example.com', 'Joy1', '99226139559', 301555, NULL),
(10, 'test1', '$2b$10$rp4.mHHVZw04IlaNGlYVWeVDH5NqK7Apa.HwmYDNi6x77Eg6uh6hS', '$2b$10', 'test1', 'test1', '99971232337', 0, NULL),
(11, 'rafaeljoyhadi', '$2b$10$jt47rqF4fkp2Yyh08euWGOTGKJLI/LComHsWGhznEmB2sBZFdZ46i', '$2b$10', 'rafaeljoyhadi@example.com', 'Rafael Joy Hadi', '99147152856', 10023103, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `budget_goal`
--
ALTER TABLE `budget_goal`
  ADD PRIMARY KEY (`id_goal`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `budget_plan`
--
ALTER TABLE `budget_plan`
  ADD PRIMARY KEY (`id_budget`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_category` (`id_category`);

--
-- Indexes for table `contact`
--
ALTER TABLE `contact`
  ADD PRIMARY KEY (`id_user`,`saved_id_user`),
  ADD KEY `saved_id_user` (`saved_id_user`);

--
-- Indexes for table `log_login`
--
ALTER TABLE `log_login`
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id_notification`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id_transaction`),
  ADD KEY `source_id_user` (`source_id_user`),
  ADD KEY `target_id_user` (`target_id_user`),
  ADD KEY `id_category` (`id_category`);

--
-- Indexes for table `transaction_category`
--
ALTER TABLE `transaction_category`
  ADD PRIMARY KEY (`id_category`),
  ADD UNIQUE KEY `id_user` (`id_user`,`category_name`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `account_number` (`account_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `budget_goal`
--
ALTER TABLE `budget_goal`
  MODIFY `id_goal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `budget_plan`
--
ALTER TABLE `budget_plan`
  MODIFY `id_budget` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id_notification` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `id_transaction` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;

--
-- AUTO_INCREMENT for table `transaction_category`
--
ALTER TABLE `transaction_category`
  MODIFY `id_category` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `budget_goal`
--
ALTER TABLE `budget_goal`
  ADD CONSTRAINT `budget_goal_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `budget_plan`
--
ALTER TABLE `budget_plan`
  ADD CONSTRAINT `budget_plan_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `budget_plan_ibfk_2` FOREIGN KEY (`id_category`) REFERENCES `transaction_category` (`id_category`) ON DELETE CASCADE;

--
-- Constraints for table `contact`
--
ALTER TABLE `contact`
  ADD CONSTRAINT `contact_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `contact_ibfk_2` FOREIGN KEY (`saved_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `log_login`
--
ALTER TABLE `log_login`
  ADD CONSTRAINT `log_login_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`source_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`target_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`id_category`) REFERENCES `transaction_category` (`id_category`);

--
-- Constraints for table `transaction_category`
--
ALTER TABLE `transaction_category`
  ADD CONSTRAINT `transaction_category_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
