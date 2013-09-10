package ENode.Infrastructure.PerformanceTesting;

/** A utility class used to do performance test.
 
*/
public class PerformanceTester
{
	private static final java.util.HashMap<String, TimeRecorder> TimeRecorderDictionary = new java.util.HashMap<String, TimeRecorder>();

	/** Get a time recorder.
	 
	 @param timeRecorderName
	 @return 
	*/
	public static TimeRecorder GetTimeRecorder(String timeRecorderName)
	{
		return GetTimeRecorder(timeRecorderName, false);
	}
	/** Get a time recorder.
	 
	 @param timeRecorderName
	 @param reset
	 @return 
	*/
	public static TimeRecorder GetTimeRecorder(String timeRecorderName, boolean reset)
	{
		if (!TimeRecorderDictionary.containsKey(timeRecorderName))
		{
			TimeRecorderDictionary.put(timeRecorderName, new TimeRecorder(timeRecorderName));
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var recorder = TimeRecorderDictionary.get(timeRecorderName);

		if (reset)
		{
			recorder.Reset();
		}

		return recorder;
	}
}