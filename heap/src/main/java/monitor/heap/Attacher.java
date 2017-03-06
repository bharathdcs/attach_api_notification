package monitor.heap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Attacher implements Listener {
	private int processId;
	private VirtualMachine virtualMachine=null;
	private ArrayList<Consumer> consumers;
	private int threshold;
	 private final ScheduledExecutorService scheduler =
		       Executors.newScheduledThreadPool(1);

	Attacher(int processId,int threshold)
	{	
		this.processId=processId;
		this.threshold=threshold;
		consumers=new ArrayList<Consumer>();
		
	}
	 public  void loadMangementAgent(VirtualMachine virtualMachine) {
		    final String id = virtualMachine.id();
		    String agent = null;
		    Boolean loaded = false;
		    try {
		      String javaHome = readSystemProperty(virtualMachine, "java.home");
		      agent = javaHome + "/lib/management-agent.jar";
		      virtualMachine.loadAgent(agent);
		      loaded = true;
		    } catch (IOException e) {
		      //logger.error("Reading system properties or loading the agent resulted in a exception for pid " + id, e);
		    } catch (AgentLoadException e) {
		      //logger.error("Loading agent failed for pid " + id, e);
		    } catch (AgentInitializationException e) {
		      //logger.error("Agent initialization failed for pid " + id, e);
		    }
		    //logger.info("Loading management agent \" succeeded = );
		  }
	 public  String readSystemProperty(VirtualMachine virtualMachine, String propertyName) {
		    String propertyValue = null;
		    try {
		      Properties systemProperties = virtualMachine.getSystemProperties();
		      propertyValue = systemProperties.getProperty(propertyName);
		    } catch (IOException e) {
		      //            logger.error("Reading system property failed", e);
		    }
		    return propertyValue;
		  }

	 public  void attach() throws AttachNotSupportedException, IOException,Exception {
		    List<VirtualMachineDescriptor> vms = VirtualMachine.list();
		    for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
		      //System.out.println("Output"+virtualMachineDescriptor.id()+""+virtualMachineDescriptor.displayName());
		    	if(Integer.parseInt(virtualMachineDescriptor.id())==processId)
		    	{
		    		System.out.println("Found process with Id"+virtualMachineDescriptor.id()+"now attaching the monitor");
		      VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);// attach(virtualMachineDescriptor);
		      if (virtualMachine != null) {
		        Properties agentProperties = virtualMachine.getAgentProperties();
		        String propertyValue = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");
		        System.out.println("JMX URL found = {}"+ propertyValue);
		        if(propertyValue == null)
		        {
		          loadMangementAgent(virtualMachine);
		        }
		        monitorMemory(virtualMachine);
		      }
		    }
		    }
		  }
		     
	 public	  void monitorMemory(VirtualMachine virtualMachine) throws IOException
		  {

			  Properties agentProperties = virtualMachine.getAgentProperties();
			  String  propertyValue = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");
		      final JMXConnector c = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(propertyValue),null);
		      c.connect();
		       
		      final Runnable beeper = new Runnable() {
	                public void run() 
	                {
	                	Object o;
						try {
							o = c.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
							 CompositeData cd = (CompositeData) o;
						        int totalHeap=Integer.parseInt(cd.get("max").toString());
						        int usedHeap=Integer.parseInt(cd.get("used").toString());
						        System.out.println("Used memory:"+usedHeap/1024+"KB");
						        int thresholdValue= totalHeap*(threshold/100);
						        System.out.println("calculated threshold value:"+thresholdValue/1024+"KB");
						       if(usedHeap>=thresholdValue)
						       {
						    	   for(Consumer eachConsumer:consumers)
						    	   {
						    		   eachConsumer.notify("Alert memory usage exceeded threhold");
						    		   System.out.println("Memory usage exceeded");
						    	   }
						    	   scheduler.shutdown();
						    	   return;
						    	   //System.out.println("Alert memory usage exceeded threhold");
						       }
						} catch (AttributeNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstanceNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MalformedObjectNameException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MBeanException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ReflectionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
	            };
	        final ScheduledFuture<?> beeperHandle =
	            scheduler.scheduleAtFixedRate(beeper, 10, 10, TimeUnit.SECONDS);
	     //  beeperHandle.isDone()
			
			  virtualMachine.detach();
		  }
		public void listen(Consumer consumer) {
			// TODO Auto-generated method stub
			consumers.add(consumer);
			
		}

}
