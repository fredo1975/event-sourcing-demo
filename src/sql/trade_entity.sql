-- --------------------------------------------------------
-- Hôte :                        192.168.1.104
-- Version du serveur:           5.7.29-0ubuntu0.18.04.1 - (Ubuntu)
-- SE du serveur:                Linux
-- HeidiSQL Version:             10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Listage de la structure de la base pour event-sourcing
CREATE DATABASE IF NOT EXISTS `event-sourcing` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `event-sourcing`;

-- Listage de la structure de la table event-sourcing. TRADE_ENTITY
CREATE TABLE IF NOT EXISTS `TRADE_ENTITY` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ISIN` varchar(50) DEFAULT NULL,
  `CCY` varchar(50) DEFAULT NULL,
  `PRICE` double DEFAULT NULL,
  `QTY` bigint(20) DEFAULT NULL,
  `CFIN` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
