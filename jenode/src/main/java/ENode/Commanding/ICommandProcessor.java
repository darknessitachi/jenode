package ENode.Commanding;

import ENode.Messaging.*;

/** Represents a processor to receive and process command.
 
*/
public interface ICommandProcessor extends IMessageProcessor<ICommandQueue, ICommand>
{
}