package monitor.heap;
import com.sun.tools.attach.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import javax.management.remote.*;
import javax.management.openmbean.*;
import javax.management.*;
/**
* Hello world!
*
*/
public class App
{
 
  public static void main( String[] args ) throws AttachNotSupportedException, Exception, IOException
  {
	  Properties prop = new Properties();
		InputStream inputFile = null;
		if(args.length!=1)
		{
			System.out.println("Please pass the location of properties file");
			System.exit(-1);
		}

		try {

			inputFile = new FileInputStream(args[0]);
			prop.load(inputFile);
			// set the properties value
			int processId=Integer.parseInt(prop.getProperty("procId"));
			String hostname=prop.getProperty("smtp_host");
			int port=Integer.parseInt(prop.getProperty("smtp_port"));
			String username=prop.getProperty("smpt_user");
			int threshold=Integer.parseInt(prop.getProperty("threshold"));
			String password=prop.getProperty("stmp_password");
			String touser=prop.getProperty("email");
			Attacher attacher=new Attacher(processId,threshold);
			
			Notifier notifier=new Notifier(hostname,port,username,password,touser);
			attacher.listen(notifier);
			attacher.attach();
			// save properties to project root folder
			

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (inputFile != null) {
				try {
					inputFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	  }
}
