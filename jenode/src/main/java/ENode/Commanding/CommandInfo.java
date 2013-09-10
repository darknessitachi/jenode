package ENode.Commanding;

/** This class contains a command and its retried count info.
 
*/
public class CommandInfo
{
	/** The command.
	 
	*/
	private ICommand privateCommand;
	public final ICommand getCommand()
	{
		return privateCommand;
	}
	private void setCommand(ICommand value)
	{
		privateCommand = value;
	}
	/** The retry count of command.
	 
	*/
	private int privateRetriedCount;
	public final int getRetriedCount()
	{
		return privateRetriedCount;
	}
	private void setRetriedCount(int value)
	{
		privateRetriedCount = value;
	}

	/** Parameterized constructor.
	 
	 @param command
	*/
	public CommandInfo(ICommand command)
	{
		if (command == null)
		{
			throw new ArgumentNullException("command");
		}

		setCommand(command);
	}

	/** Increase the command retried count.
	 
	*/
	public final void IncreaseRetriedCount()
	{
		setRetriedCount(getRetriedCount() + 1);
	}
}