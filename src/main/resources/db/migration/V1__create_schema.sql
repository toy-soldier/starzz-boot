CREATE TABLE
	users (
		user_id INTEGER AUTO_INCREMENT,
		username VARCHAR(100),
		email VARCHAR(100),
		password VARCHAR(100),
		first_name VARCHAR(100),
		last_name VARCHAR(100),
		date_of_birth VARCHAR(100),
		PRIMARY KEY (user_id)
	);

CREATE TABLE
	galaxies (
		galaxy_id INTEGER AUTO_INCREMENT,
		galaxy_name VARCHAR(100),
		galaxy_type VARCHAR(100),
		distance_mly INTEGER,
		redshift INTEGER,
		mass_solar INTEGER,
		diameter_ly INTEGER,
		added_by INTEGER,
		verified_by INTEGER,
		PRIMARY KEY (galaxy_id),
		FOREIGN KEY (added_by) REFERENCES users (user_id),
		FOREIGN KEY (verified_by) REFERENCES users (user_id)
	);

CREATE TABLE
	constellations (
		constellation_id INTEGER AUTO_INCREMENT,
		constellation_name VARCHAR(100),
		galaxy_id INTEGER,
		added_by INTEGER,
		verified_by INTEGER,
		PRIMARY KEY (constellation_id),
		FOREIGN KEY (galaxy_id) REFERENCES galaxies (galaxy_id),
		FOREIGN KEY (added_by) REFERENCES users (user_id),
		FOREIGN KEY (verified_by) REFERENCES users (user_id)
	);

CREATE TABLE
	stars (
		star_id INTEGER AUTO_INCREMENT,
		star_name VARCHAR(100),
		star_type VARCHAR(100),
		constellation_id INTEGER,
		right_ascension INTEGER,
		declination INTEGER,
		apparent_magnitude INTEGER,
		spectral_type VARCHAR(5),
		added_by INTEGER,
		verified_by INTEGER,
		PRIMARY KEY (star_id),
		FOREIGN KEY (constellation_id) REFERENCES constellations (constellation_id),
		FOREIGN KEY (added_by) REFERENCES users (user_id),
		FOREIGN KEY (verified_by) REFERENCES users (user_id)
	);