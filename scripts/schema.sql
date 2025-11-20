-- Creation of table users
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(8) NOT NULL,
  -- Constraints
  CONSTRAINT email_format_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
  CHECK (role IN ('employee', 'employer'))
);

-- Creation of table jobs
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    employer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    type VARCHAR(10) NOT NULL,
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    benefits TEXT,
    extras TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (type IN ('full-time', 'part-time', 'contract', 'internship', 'temporary'))
);
