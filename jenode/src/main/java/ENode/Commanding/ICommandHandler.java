package ENode.Commanding;

/** Represents a command handler interface.
 
*/
public interface ICommandHandler
{
	/** Handles the given command with the provided context.
	 
	 @param context
	 @param command
	*/
	void Handle(ICommandContext context, ICommand command);
	/** Returns the inner really command handler.
	 
	 @return 
	*/
	Object GetInnerCommandHandler();
}
/** Represents a command handler interface.
 
 <typeparam name="TCommand"></typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: Java does not allow specifying covariance or contravariance in a generic type list:
//ORIGINAL LINE: public interface ICommandHandler<in TCommand> where TCommand : class, ICommand
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public interface ICommandHandler<TCommand extends class & ICommand>
{
	/** Handles the given command with the provided context.
	 
	 @param context
	 @param command
	*/
	void Handle(ICommandContext context, TCommand command);
}
