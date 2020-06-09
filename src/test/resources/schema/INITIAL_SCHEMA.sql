CREATE TABLE IF NOT EXISTS `access_log` (
  `date` datetime NOT NULL,
  `ip` varchar(15) NOT NULL,
  `request` varchar(1000) NOT NULL,
  `status` varchar(20) NOT NULL,
  `user_agent` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS `block_occurrences` (
  `ip` varchar(15) PRIMARY KEY NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `comment` varchar(150) NOT NULL,
  `threshold` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;
COMMIT;
