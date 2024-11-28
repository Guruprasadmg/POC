# Data Set POC

### How to run

Clone the project from
Github -- https://github.com/Guruprasadmg/POC.git
Build and Run:
mvn clean install <br>
mvn spring-boot:run

# Technologies :
Java : 17 <br>
Springboot : 3.3.5 <br>
Database : H2
Build : Maven

# Tools
IDE : IntelliJ

# Test Data
Added in TestData directory
# Swagger 
http://localhost:8089/swagger-ui.html

# DB Access
http://localhost:8089/h2-console
JDBC_URL : jdbc:h2:mem:poc
username : sa
password :

# Test Scenarios
1) Check file is empty
2) Check headers is missing
3) Validation for all columns like date , integer
4) Failed to read the file like invalid file (corrupted CSV)
5) Failed to upload the file  since the invalid file format like other than csv
6) partially upload like number of files are uploaded and failed  -- if any errors like validation or any issue , file upload should continue for other rows
7) Complete failure  -- all data issue
8) Duplicate record  -- if data present in db
9) Success
10) Get details -- in case data not present returning 404

Duplicate record check works based on combination of  quarter + stock + volume

If any errors , generating a csv file with details
Please change the file path using error.file.upload property