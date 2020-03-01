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


-- Listage de la structure de la base pour trade_crud
CREATE DATABASE IF NOT EXISTS `trade_crud` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `trade_crud`;

-- Listage de la structure de la table trade_crud. domain_event_entry
CREATE TABLE IF NOT EXISTS `domain_event_entry` (
  `global_index` int(11) NOT NULL,
  `aggregate_identifier` varchar(255) DEFAULT NULL,
  `event_identifier` varchar(255) NOT NULL,
  `payload` blob,
  `payload_type` varchar(255) DEFAULT NULL,
  `sequence_number` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`global_index`),
  UNIQUE KEY `Index 4` (`event_identifier`),
  UNIQUE KEY `Index 3` (`aggregate_identifier`,`sequence_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table trade_crud. domain_status_event_entry
CREATE TABLE IF NOT EXISTS `domain_status_event_entry` (
  `global_index` int(11) NOT NULL,
  PRIMARY KEY (`global_index`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table trade_crud. next_val
CREATE TABLE IF NOT EXISTS `next_val` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table trade_crud. trade_crud_entity
CREATE TABLE IF NOT EXISTS `trade_crud_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ccy` varchar(255) DEFAULT NULL,
  `cfin` varchar(255) DEFAULT NULL,
  `isin` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `qty` int(11) DEFAULT NULL,
  `sent` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33402 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
-- Listage de la structure de la table trade_crud. TRADE_ENTITY
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
-- Listage de la structure de la table trade_crud. trade_entity
CREATE TABLE IF NOT EXISTS `trade_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ccy` varchar(255) DEFAULT NULL,
  `cfin` varchar(255) DEFAULT NULL,
  `isin` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `qty` int(11) DEFAULT NULL,
  `sent` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Les données exportées n'étaient pas sélectionnées.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;




SELECT * 
FROM domain_event_entry INNER JOIN (
SELECT MAX(sequence_number) AS m,aggregate_identifier as id FROM domain_event_entry GROUP BY aggregate_identifier) temp ON temp.id=aggregate_identifier 
AND sequence_number=m 
AND payload_type<>'fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeSentEvent';