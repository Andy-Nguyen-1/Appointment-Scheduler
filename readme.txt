-------------------------
WGU Appointment System
-------------------------
An application made to create, read, update, delete, schedule appointments, 
and store customer information.

Author: Andy Nguyen 

Version: 1.0

July 21, 2022

Developed with IntelliJ IDEA  2022.1.3 (Community Edition)

JDK Version: 18.0.1

JavaFX Version: 18.0

MySQL Connector mysql-connector-java-8.0.29

----
How To Run Application

1. Open IntelliJ
2. File > Open
3. Search for "Andy_Nguyen_C195" and open
4. Edit Configurations on the top right
5. Click on the "+" and add "Application"
6. Name the configuration "Andy_Nguyen_C195"
7. Make sure Java 18 is selected
5. Then on the top right click "Modify Options" > "Add VM Options"
6. Copy and paste to VM options

--module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics

7. Click the green play button on the top right.
8. Sign in with

Username: "test"

Password: "test"
-------------------------------------------------
Connecting The Database
1. Confirm you have the compatible MySQL Connector Driver from above
2. If not, download mysql-connector-java-8.0.29
2. Goto File > Project Structures > Libraries > "+" > Java
3. Locate the connector driver and add the  "mysql-connector-java-8.0.29.jar" file.

___________
Part A3F.

The addition report I choose to include in my report was to count the total
number of customers in each Country by counting customer id and inner joining
the CUSTOMERS table with FIRST_LEVEL_DIVISIONS table and COUNTRIES table.

______________________________________________________
Lambda Expression:

1. \Andy_Nguyen_C195\src\controller\welcome

(Line 192)

2. \Andy_Nguyen_C195\src\controller\welcome

(Line 303)
_____________________________________
User login activity file:
\Andy_Nguyen_C195\login_activity.txt
__________________________
Javadoc index.html Location:
\Andy_Nguyen_C195\Javadoc
_______________________________
F.A.Q

Why can't I log in?

Make sure that you have the right MySQL Connector Driver "MySQL Connector mysql-connector-java-8.0.29"

Why can't I run the application?

Make sure you have the right JDK and JavaFX version. Also make sure your configurations are set up correctly.





