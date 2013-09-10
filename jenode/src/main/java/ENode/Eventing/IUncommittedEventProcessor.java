package ENode.Eventing;

import ENode.Messaging.*;

/** Represents a processor to process uncommitted event stream.
 
*/
public interface IUncommittedEventProcessor extends IMessageProcessor<IUncommittedEventQueue, EventStream>
{
}