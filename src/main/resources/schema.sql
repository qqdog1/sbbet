DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(16) NOT NULL,
  password VARCHAR(256) NOT NULL,
  authority VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS company (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) PRIMARY KEY,
  address VARCHAR(256) NOT NULL,
  created_by VARCHAR(16) NOT NULL,
  created_at TIMESTAMP,
  updated_by VARCHAR(16),
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS client (
  id INT AUTO_INCREMENT,
  company_id INT,
  name VARCHAR(32) PRIMARY KEY,
  email VARCHAR(64) NOT NULL,
  phone VARCHAR(12) NOT NULL,
  created_by VARCHAR(16) NOT NULL,
  created_at TIMESTAMP,
  updated_by VARCHAR(16),
  updated_at TIMESTAMP
);
