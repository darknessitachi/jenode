package ENode.Infrastructure.PerformanceTesting;

/** Represents an item in the time recorder.
 
*/
public class RecorderItem
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param timeRecorder
	 @param description
	 @exception ArgumentNullException
	*/
	public RecorderItem(TimeRecorder timeRecorder, String description)
	{
		if (timeRecorder == null)
		{
			throw new ArgumentNullException("timeRecorder");
		}

		setId(Guid.NewGuid().toString());
		setTimeRecorder(timeRecorder);
		setStartTicks(getTimeRecorder().GetCurrentTicks());
		setStartTime(new java.util.Date());
		setDescription(description);
		setIsCompleted(false);
		setChildRecorderItems(new java.util.ArrayList<RecorderItem>());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Properties

	/** The owner timer recorder.
	 
	*/
	private TimeRecorder privateTimeRecorder;
	public final TimeRecorder getTimeRecorder()
	{
		return privateTimeRecorder;
	}
	private void setTimeRecorder(TimeRecorder value)
	{
		privateTimeRecorder = value;
	}
	/** The unique id of the recorder item.
	 
	*/
	private String privateId;
	public final String getId()
	{
		return privateId;
	}
	private void setId(String value)
	{
		privateId = value;
	}
	/** The parent recorder item.
	 
	*/
	private RecorderItem privateParentRecorderItem;
	public final RecorderItem getParentRecorderItem()
	{
		return privateParentRecorderItem;
	}
	public final void setParentRecorderItem(RecorderItem value)
	{
		privateParentRecorderItem = value;
	}
	/** The child recorder items.
	 
	*/
	private java.util.ArrayList<RecorderItem> privateChildRecorderItems;
	public final java.util.ArrayList<RecorderItem> getChildRecorderItems()
	{
		return privateChildRecorderItems;
	}
	public final void setChildRecorderItems(java.util.ArrayList<RecorderItem> value)
	{
		privateChildRecorderItems = value;
	}
	/** The tree level of the current recorder item.
	 
	*/
	private int privateTreeNodeDeepLevel;
	public final int getTreeNodeDeepLevel()
	{
		return privateTreeNodeDeepLevel;
	}
	public final void setTreeNodeDeepLevel(int value)
	{
		privateTreeNodeDeepLevel = value;
	}
	/** The start time of the recorder item.
	 
	*/
	private java.util.Date privateStartTime = new java.util.Date(0);
	public final java.util.Date getStartTime()
	{
		return privateStartTime;
	}
	private void setStartTime(java.util.Date value)
	{
		privateStartTime = value;
	}
	/** The end time of the recorder item.
	 
	*/
	private java.util.Date privateEndTime = new java.util.Date(0);
	public final java.util.Date getEndTime()
	{
		return privateEndTime;
	}
	private void setEndTime(java.util.Date value)
	{
		privateEndTime = value;
	}
	/** The description of the recorder item.
	 
	*/
	private String privateDescription;
	public final String getDescription()
	{
		return privateDescription;
	}
	private void setDescription(String value)
	{
		privateDescription = value;
	}
	/** The start ticks of the recorder item.
	 
	*/
	private double privateStartTicks;
	public final double getStartTicks()
	{
		return privateStartTicks;
	}
	private void setStartTicks(double value)
	{
		privateStartTicks = value;
	}
	/** The end ticks of the recorder item.
	 
	*/
	private double privateEndTicks;
	public final double getEndTicks()
	{
		return privateEndTicks;
	}
	private void setEndTicks(double value)
	{
		privateEndTicks = value;
	}
	/** The total ticks of the recorder item.
	 
	*/
	public final double getTotalTicks()
	{
		return getEndTicks() - getStartTicks();
	}
	/** Represents whether the current recorder item is completed.
	 
	*/
	private boolean privateIsCompleted;
	public final boolean getIsCompleted()
	{
		return privateIsCompleted;
	}
	private void setIsCompleted(boolean value)
	{
		privateIsCompleted = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Methods

	/** Complete the current recorder item.
	 
	*/
	public final void Complete()
	{
		setEndTicks(getTimeRecorder().GetCurrentTicks());
		setEndTime(new java.util.Date());
		setIsCompleted(true);
		getTimeRecorder().AddCompletedRecorderItem(this);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}