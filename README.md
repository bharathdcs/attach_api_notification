# attach_api_notification
@Author=Bharath K Devaraju<br/>
Steps to run the project<br/>
1. Compile using "mvn clean compile assembly:single"<br/>
2. Run the application using following syntax<br/>
java -cp ${JAVA_HOME}/lib/tools.jar:heap-0.0.1-SNAPSHOT-jar-with-dependencies.jar monitor.heap.App sample.properties <br/>
<br/>
Sample properties file<br/>
-----<br/>
procId=3124<br/>
smtp_host=smtp.gmail.com<br/>
smtp_port=465<br/>
smpt_user=test@gmail.com<br/>
stmp_password=testing123<br/>
threshold=10<br/>
email=test@gmail.com<br/>
