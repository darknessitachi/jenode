package ENode.Infrastructure.PerformanceTesting;

/** A time recorder used to do performance test.
 
*/
public class TimeRecorder
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Members

	private java.util.ArrayList<RecorderItem> _recorderItemList;
	private Stopwatch _stopWatch;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param name
	 @exception ArgumentNullException
	*/
	public TimeRecorder(String name)
	{
		if (name == null)
		{
			throw new ArgumentNullException("name");
		}
		setName(name);
		_recorderItemList = new java.util.ArrayList<RecorderItem>();
		_stopWatch = new Stopwatch();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Properties

	/** The name of the time recorder.
	 
	*/
	private String privateName;
	public final String getName()
	{
		return privateName;
	}
	private void setName(String value)
	{
		privateName = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Methods

	/** Reset the time recorder, reset the time and clear all the recorder items.
	 
	*/
	public final void Reset()
	{
		_stopWatch.Stop();
		_stopWatch.Reset();
		_recorderItemList.clear();
	}
	/** Begin a recorder item with some description.
	 
	 @param description
	 @return 
	 @exception ArgumentNullException
	*/
	public final RecorderItem BeginRecorderItem(String description)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(description))
		{
			throw new ArgumentNullException("description");
		}
		return new RecorderItem(this, description);
	}
	/** Generate a report for the current performance test.
	 
	 @return 
	*/
	public final String GenerateReport()
	{
		StringBuilder reportBuilder = new StringBuilder();

		reportBuilder.AppendLine(Environment.NewLine);
		reportBuilder.AppendLine("------------------------------------------------------------------------------------------------------------------------------------");

		reportBuilder.AppendLine(String.format("TimeRecorder Name:%1$s  Total RecorderItem Times:%2$sms", getName(), (GetTotalTicks() / 10000)));
		reportBuilder.AppendLine("RecorderItem Time Details:");
		reportBuilder.AppendLine(GenerateTreeReport());

		reportBuilder.AppendLine("------------------------------------------------------------------------------------------------------------------------------------" + Environment.NewLine);

		return reportBuilder.toString();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Internal Methods

	public final void AddCompletedRecorderItem(RecorderItem recorderItem)
	{
		if (recorderItem != null && recorderItem.getIsCompleted())
		{
			_recorderItemList.add(recorderItem);
		}
	}
	public final double GetCurrentTicks()
	{
		_stopWatch.Stop();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var currentTicks = (double)_stopWatch.Elapsed.Ticks;
		_stopWatch.Start();
		return currentTicks;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private String GenerateTreeReport()
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var totalString = "";
		final String leftSpace = "";
		final String unitIndentString = "    ";
		java.util.ArrayList<String> recorderItemTimeStrings = new java.util.ArrayList<String>();

		java.util.ArrayList<RecorderItem> topLevelRecorderItems = GetTopLevelRecorderItems();

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var recorderItem : topLevelRecorderItems)
		{
			recorderItem.TreeNodeDeepLevel = 1;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var recorderItem : topLevelRecorderItems)
		{
			BuildChildRecorderItemTree(recorderItem);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var recorderItem : topLevelRecorderItems)
		{
			GenerateRecorderItemTimeStrings(recorderItem, leftSpace, unitIndentString, recorderItemTimeStrings);
			totalString += DotNetToJavaStringHelper.join(Environment.NewLine, recorderItemTimeStrings.toArray(new String[]{}));
			if (topLevelRecorderItems.indexOf(recorderItem) < topLevelRecorderItems.size()() - 1)
			{
				totalString += Environment.NewLine;
			}
			recorderItemTimeStrings.clear();
		}

		return totalString;
	}
	private void BuildChildRecorderItemTree(RecorderItem parentRecorderItem)
	{
		Iterable<RecorderItem> childRecorderItems = GetChildRecorderItems(parentRecorderItem);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var childRecorderItem : childRecorderItems)
		{
			childRecorderItem.TreeNodeDeepLevel = parentRecorderItem.getTreeNodeDeepLevel() + 1;
			childRecorderItem.ParentRecorderItem = parentRecorderItem;
			parentRecorderItem.getChildRecorderItems().add(childRecorderItem);
			BuildChildRecorderItemTree(childRecorderItem);
		}
	}
	private double GetTotalTicks()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _recorderItemList.isEmpty() ? 0D : GetTopLevelRecorderItems().<RecorderItem, Double>Aggregate(0, (current, recorderItem) => current + recorderItem.TotalTicks);
	}
	private boolean IsTopLevelRecorderItem(RecorderItem recorderItem)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return recorderItem != null && _recorderItemList.Where(a => !recorderItem.getId().equals(a.Id)).All(a => !(a.StartTicks < recorderItem.getStartTicks()) || !(a.EndTicks > recorderItem.getEndTicks()));
	}
	private java.util.ArrayList<RecorderItem> GetTopLevelRecorderItems()
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
		return _recorderItemList.Where(IsTopLevelRecorderItem).ToList();
	}
	private RecorderItem GetDirectParent(RecorderItem recorderItem)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return recorderItem == null ? null : _recorderItemList.Where(a => recorderItem.getId() != a.Id).FirstOrDefault(a => a.StartTicks < recorderItem.getStartTicks() && a.EndTicks > recorderItem.getEndTicks());
	}
	private Iterable<RecorderItem> GetChildRecorderItems(RecorderItem parentRecorderItem)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
		return parentRecorderItem == null ? new java.util.ArrayList<RecorderItem>() : (from recorderItem in _recorderItemList where !parentRecorderItem.getId().equals(recorderItem.Id) where recorderItem.StartTicks > parentRecorderItem.getStartTicks() && recorderItem.EndTicks < parentRecorderItem.getEndTicks() let directParent = GetDirectParent(recorderItem) where directParent != null && parentRecorderItem.getId().equals(directParent.Id) select recorderItem).ToList();
	}
	private void GenerateRecorderItemTimeStrings(RecorderItem recorderItem, String leftSpace, String unitIndentString, java.util.ArrayList<String> recorderItemTimeStrings)
	{
		final String recorderItemTimeStringFormat = "{0}{1}({2})  {3}  {4}  {5}";
		String recorderItemTimeLeftSpaceString = leftSpace;
		for (var i = 0; i <= recorderItem.getTreeNodeDeepLevel() - 1; i++)
		{
			recorderItemTimeLeftSpaceString += unitIndentString;
		}

		recorderItemTimeStrings.add(String.format(recorderItemTimeStringFormat, new Object[] { recorderItemTimeLeftSpaceString, (recorderItem.getTotalTicks() / 10000) + "ms", GetTimePercent(recorderItem), recorderItem.getDescription(), recorderItem.getStartTime() + ":" + recorderItem.getStartTime().Millisecond, recorderItem.getEndTime() + ":" + recorderItem.getEndTime().Millisecond }));

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var childRecorderItem : recorderItem.getChildRecorderItems())
		{
			GenerateRecorderItemTimeStrings(childRecorderItem, leftSpace, unitIndentString, recorderItemTimeStrings);
		}
	}
	private String GetTimePercent(RecorderItem recorderItem)
	{
		if (recorderItem.getTreeNodeDeepLevel() == 1)
		{
			double totalTicks = GetTotalTicks();
			return (int)totalTicks == 0 ? "0.00%" : (recorderItem.getTotalTicks() / totalTicks).ToString("##.##%");
		}
		if (recorderItem.getTreeNodeDeepLevel() >= 2)
		{
			return (int)recorderItem.getParentRecorderItem().getTotalTicks() == 0 ? "0.00%" : (recorderItem.getTotalTicks() / recorderItem.getParentRecorderItem().getTotalTicks()).ToString("##.##%");
		}
		return "0.00%";
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}