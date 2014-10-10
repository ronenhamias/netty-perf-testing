package io.netty.example.common;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class CPUCounter extends AbstractCounter {

	public CPUCounter(int maxQueueSize) {
		super(maxQueueSize);
	}

	double getProcessCpuLoad() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException {

		MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		if (list.isEmpty())     return Double.NaN;

		Attribute att = (Attribute)list.get(0);
		Double value  = (Double)att.getValue();

		if (value == -1.0)      return Double.NaN;  // usually takes a couple of seconds before we get real values

		return ((int)(value * 1000) / 10.0);        // returns a percentage value with 1 decimal point precision
	}

	@Override
	public Double getMovingAvg(){
		Object[] array = queue.toArray();
		if(array.length !=0){
			Double sum = 0.0;
			for(Object o : array){
				Double d = (Double) o;
				if(d==Double.NaN)
					return 0.0;
				sum+= d;
			}
			return sum / array.length;
		}else 
			return 0.0;
	}

	@Override
	public void add() {
		try {
			super.add(getProcessCpuLoad());
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
