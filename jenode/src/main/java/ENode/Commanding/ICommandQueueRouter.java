package ENode.Commanding;

/** Represents a command queue router to route a given command to an appropriate command queue.
 
*/
public interface ICommandQueueRouter
{
	/** Route a given command to an appropriate command queue.
	 
	 @param command
	 @return 
	*/
	ICommandQueue Route(ICommand command);
}