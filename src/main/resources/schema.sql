CREATE TABLE domain_event_entry (
	aggregate_identifier VARCHAR(255) NOT NULL,
	event_identifier VARCHAR(255) NOT NULL,
	payload VARCHAR(255) NULL DEFAULT NULL,
	payload_type VARCHAR(255) NOT NULL,
	sequence_number INT(11) NOT NULL,
	type VARCHAR(255) NULL DEFAULT NULL,
	timestamp DATETIME(6) NULL DEFAULT NULL,
	PRIMARY KEY (event_identifier),
	UNIQUE INDEX `Index 2` (aggregate_identifier, sequence_number)
);