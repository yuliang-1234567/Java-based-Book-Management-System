-- 数据库与表结构
CREATE DATABASE IF NOT EXISTS campus_library CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_library;

-- 图书表
CREATE TABLE IF NOT EXISTS book (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  category VARCHAR(100) NOT NULL,
  total_copies INT NOT NULL DEFAULT 1,
  available_copies INT NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 读者表
CREATE TABLE IF NOT EXISTS reader (
  id INT PRIMARY KEY AUTO_INCREMENT,
  no VARCHAR(64) NOT NULL UNIQUE, -- 学号或工号
  name VARCHAR(100) NOT NULL,
  role ENUM('student','staff') NOT NULL DEFAULT 'student',
  max_borrow INT NOT NULL DEFAULT 5,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 借阅记录表
CREATE TABLE IF NOT EXISTS loan_record (
  id INT PRIMARY KEY AUTO_INCREMENT,
  book_id INT NOT NULL,
  reader_id INT NOT NULL,
  borrow_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  renew_count INT NOT NULL DEFAULT 0,
  status ENUM('BORROWED','RETURNED','OVERDUE') NOT NULL DEFAULT 'BORROWED',
  CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE RESTRICT,
  CONSTRAINT fk_reader FOREIGN KEY (reader_id) REFERENCES reader(id) ON DELETE RESTRICT,
  INDEX idx_reader_status (reader_id, status),
  INDEX idx_book (book_id)
) ENGINE=InnoDB;

-- 视图：分类借阅量统计
DROP VIEW IF EXISTS v_category_borrow_count;
CREATE VIEW v_category_borrow_count AS
SELECT b.category, COUNT(l.id) AS borrow_count
FROM loan_record l
JOIN book b ON b.id = l.book_id
GROUP BY b.category;

-- 示例数据
INSERT INTO book (code, title, author, category, total_copies, available_copies) VALUES
('B001','Java 核心技术','Cay S. Horstmann','计算机',5,5),
('B002','算法导论','Thomas H. Cormen','计算机',3,3),
('B003','三体','刘慈欣','文学',4,4),
('B004','红楼梦','曹雪芹','文学',2,2);

INSERT INTO reader (`no`, `name`, `role`, `max_borrow`) VALUES
('20230001','张三','student',5),
('20230002','李四','student',5),
('T0001','王老师','staff',10);
