package ENode.Commanding.Impl;

/** A wrapper of command handler.
 
 <typeparam name="T">The type of the command.</typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public class CommandHandlerWrapper<T extends class & ICommand> implements ICommandHandler
{
	private ICommandHandler<T> _commandHandler;

	/** Parameterized constructor.
	 
	 @param commandHandler
	*/
	public CommandHandlerWrapper(ICommandHandler<T> commandHandler)
	{
		_commandHandler = commandHandler;
	}

	/** Handles the given command with the provided context.
	 
	 @param context
	 @param command
	*/
	public final void Handle(ICommandContext context, ICommand command)
	{
		_commandHandler.Handle(context, (T)((command instanceof T) ? command : null));
	}
	/** Returns the inner really command handler.
	 
	 @return 
	*/
	public final Object GetInnerCommandHandler()
	{
		return _commandHandler;
	}
}