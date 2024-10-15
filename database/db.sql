USE master

CREATE DATABASE Online_Exam_Management_System
GO
USE Online_Exam_Management_System
GO

CREATE TABLE tbl_Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) UNIQUE NOT NULL,
    Password NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100),
    Role NVARCHAR(20) CHECK (Role IN ('Admin', 'Instructor', 'Student')),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE tbl_Subjects (
    SubjectID INT IDENTITY(1,1) PRIMARY KEY,
    SubjectName NVARCHAR(100) NOT NULL
);
GO

CREATE TABLE tbl_Teach (
    InstructorID INT NOT NULL,
    SubjectID INT NOT NULL,
    PRIMARY KEY (InstructorID, SubjectID), -- Khóa chính kép (composite key)
    FOREIGN KEY (InstructorID) REFERENCES tbl_Users(UserID),
    FOREIGN KEY (SubjectID) REFERENCES tbl_Subjects(SubjectID)
);
GO

CREATE TABLE tbl_Exams (
    ExamID INT IDENTITY(1,1) PRIMARY KEY,
    ExamName NVARCHAR(100) NOT NULL,
    SubjectID INT NOT NULL,
    InstructorID INT NOT NULL,
    ExamDate DATE NOT NULL,
    Duration INT, -- Duration in minutes
    TotalMarks INT,
    FOREIGN KEY (SubjectID) REFERENCES tbl_Subjects(SubjectID),
    FOREIGN KEY (InstructorID) REFERENCES tbl_Users(UserID)
);
GO


CREATE TABLE tbl_Questions (
    QuestionID INT IDENTITY(1,1) PRIMARY KEY,
    QuestionText NVARCHAR(MAX) NOT NULL,
    QuestionType NVARCHAR(20) CHECK (QuestionType IN ('MCQ', 'ShortAnswer')),
    Marks INT NOT NULL,
    ExamID INT NOT NULL,
    SubjectID INT NOT NULL,
    FOREIGN KEY (ExamID) REFERENCES tbl_Exams(ExamID),
    FOREIGN KEY (SubjectID) REFERENCES tbl_Subjects(SubjectID)
);
GO

CREATE TABLE tbl_Choices (
    ChoiceID INT IDENTITY(1,1) PRIMARY KEY,
    QuestionID INT NOT NULL,
    ChoiceText NVARCHAR(MAX),
    IsCorrect BIT,
    FOREIGN KEY (QuestionID) REFERENCES tbl_Questions(QuestionID)
);
GO

CREATE TABLE tbl_StudentExams (
    StudentExamID INT IDENTITY(1,1) PRIMARY KEY,
    ExamID INT NOT NULL,
    StudentID INT NOT NULL,
    IsCompleted BIT DEFAULT 0,
    StartTime DATETIME DEFAULT GETDATE(),
    EndTime DATETIME,
    FOREIGN KEY (ExamID) REFERENCES tbl_Exams(ExamID),
    FOREIGN KEY (StudentID) REFERENCES tbl_Users(UserID)
);
GO

CREATE TABLE tbl_StudentAnswers (
    StudentAnswerID INT IDENTITY(1,1) PRIMARY KEY,
    StudentExamID INT NOT NULL,
    SelectedAnswerID INT NULL, 
    ShortAnswer NVARCHAR(MAX) NULL,
    FOREIGN KEY (StudentExamID) REFERENCES tbl_StudentExams(StudentExamID),
    FOREIGN KEY (SelectedAnswerID) REFERENCES tbl_Choices(ChoiceID)
);
GO

CREATE TABLE tbl_Grades (
    GradeID INT IDENTITY(1,1) PRIMARY KEY,
    StudentExamID INT NOT NULL,
    TotalScore INT NOT NULL,
    GradedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (StudentExamID) REFERENCES tbl_StudentExams(StudentExamID)
);
GO
