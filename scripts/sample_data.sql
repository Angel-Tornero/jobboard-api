INSERT INTO users (email, password, role)
VALUES 
('employer1@company.com', 'password123', 'employer'),
('employer2@company.com', 'password123', 'employer'),
('employee1@test.com', 'password123', 'employee'),
('employee2@test.com', 'password123', 'employee'),
('employee3@test.com', 'password123', 'employee');

INSERT INTO jobs (employer_id, title, company_name, location, description, type, salary_min, salary_max, benefits, extras, created_at)
VALUES
-- TechCorp jobs
(1, 'Backend Developer', 'TechCorp', 'Madrid', 'Work on scalable backend systems using Java and Spring Boot.', 'full-time', 30000, 50000, 'Health insurance, gym membership', 'Remote-friendly', NOW() - INTERVAL '1 day'),
(1, 'Frontend Developer', 'TechCorp', 'Remote', 'Build beautiful user interfaces with React.', 'full-time', 28000, 45000, 'Snacks, flexible hours', 'Company laptop', NOW() - INTERVAL '3 days'),
(1, 'DevOps Engineer', 'TechCorp', 'Madrid', 'Manage CI/CD pipelines and cloud infrastructure.', 'full-time', 35000, 55000, 'Stock options, free lunch', 'On-call rotation', NOW() - INTERVAL '7 days'),
(1, 'QA Tester', 'TechCorp', 'Barcelona', 'Test and ensure software quality.', 'contract', 20000, 30000, 'Flexible hours', 'Remote work', NOW() - INTERVAL '15 days'),

-- DataSolutions jobs
(2, 'Data Analyst', 'DataSolutions', 'Barcelona', 'Analyze and visualize data for business insights.', 'contract', 25000, 40000, 'Free lunch, transport allowance', 'Hybrid work model', NOW() - INTERVAL '2 days'),
(2, 'HR Intern', 'DataSolutions', 'Valencia', 'Assist HR team with recruitment and employee engagement.', 'internship', 8000, 12000, 'Coffee, mentorship program', 'Short-term contract', NOW() - INTERVAL '10 days'),
(2, 'Business Analyst', 'DataSolutions', 'Madrid', 'Work with clients to gather business requirements.', 'full-time', 27000, 42000, 'Health insurance', 'Remote-friendly', NOW() - INTERVAL '20 days'),
(2, 'Data Engineer', 'DataSolutions', 'Remote', 'Build data pipelines and ETL processes.', 'full-time', 33000, 52000, 'Gym membership', 'Flexible schedule', NOW() - INTERVAL '25 days'),

-- AIInnovate jobs
(3, 'Machine Learning Engineer', 'AIInnovate', 'Barcelona', 'Develop ML models for product recommendations.', 'full-time', 40000, 65000, 'Stock options, health insurance', 'Remote-friendly', NOW() - INTERVAL '5 days'),
(3, 'AI Researcher', 'AIInnovate', 'Madrid', 'Conduct research on natural language processing.', 'full-time', 45000, 70000, 'Conference budget', 'Flexible schedule', NOW() - INTERVAL '12 days'),
(3, 'Data Scientist Intern', 'AIInnovate', 'Remote', 'Assist ML team with model evaluation.', 'internship', 10000, 15000, 'Mentorship program', 'Short-term contract', NOW() - INTERVAL '18 days'),

-- StartUpHub jobs
(4, 'Full Stack Developer', 'StartUpHub', 'Valencia', 'Build end-to-end applications.', 'full-time', 28000, 48000, 'Equity, free snacks', 'Remote-friendly', NOW() - INTERVAL '8 days'),
(4, 'Product Manager', 'StartUpHub', 'Madrid', 'Lead product development and strategy.', 'full-time', 35000, 60000, 'Flexible hours', 'Travel budget', NOW() - INTERVAL '16 days'),
(4, 'UI/UX Designer', 'StartUpHub', 'Remote', 'Design intuitive user interfaces.', 'full-time', 30000, 45000, 'Stock options', 'Flexible schedule', NOW() - INTERVAL '22 days'),

-- FinTechWorks jobs
(5, 'Financial Analyst', 'FinTechWorks', 'Madrid', 'Analyze financial data and trends.', 'full-time', 32000, 52000, 'Bonus scheme', 'Remote-friendly', NOW() - INTERVAL '4 days'),
(5, 'Risk Manager', 'FinTechWorks', 'Barcelona', 'Manage and mitigate financial risks.', 'full-time', 40000, 65000, 'Health insurance', 'Company laptop', NOW() - INTERVAL '9 days'),
(5, 'Intern', 'FinTechWorks', 'Valencia', 'Support finance team.', 'internship', 9000, 13000, 'Mentorship', 'Short-term contract', NOW() - INTERVAL '14 days');
