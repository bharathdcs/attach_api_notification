# attach_api_notification
@Author=Bharath K Devaraju
Steps to run the project
1. Compile using "mvn clean compile assembly:single"
2. Run the application using following syntax
java -cp ${JAVA_HOME}/lib/tools.jar:heap-0.0.1-SNAPSHOT-jar-with-dependencies.jar monitor.heap.App sample.properties 

Sample properties file
-----
procId=3124
smtp_host=smtp.gmail.com
smtp_port=465
smpt_user=test@gmail.com
stmp_password=testing123
threshold=10
email=test@gmail.com
