package bschecker.util;

import java.util.HashMap;

/**
 * Provides performance monitoring for the project.
 * 
 * @author JeremiahDeGreeff
 */
public class PerformanceMonitor {
	
	private static HashMap<String, PerformanceMonitor> monitors = new HashMap<String, PerformanceMonitor>();
	
	private long start;
	
	private PerformanceMonitor(String id) {
		monitors.put(id, this);
	}
	
	/**
	 * Creates a new PerformanceMonitor.
	 * Does not start the monitor.
	 * 
	 * @param id the id to reference the monitor
	 */
	public static void addMonitor(String id) {
		new PerformanceMonitor(id);
	}
	
	/**
	 * Starts the specified PerformaceMonitor.
	 * If a monitor with this id hasn't been created yet, it will be created.
	 * 
	 * @param id the id of the desired monitor
	 */
	public static void start(String id) {
		if(monitors.get(id) == null)
			addMonitor(id);
		monitors.get(id).start = System.currentTimeMillis();
	}
	
	/**
	 * Stops the specified PerformanceMonitor and returns its results.
	 * The monitor is subsequently disposed of.
	 * 
	 * @param id the id of the desired monitor
	 * @return a String representing the amount of time during which the monitor has run
	 */
	public static String stop(String id) {
		String result = ((System.currentTimeMillis() - monitors.get(id).start) / 1000d) + "s";
		monitors.remove(id);
		return result;
	}
}
