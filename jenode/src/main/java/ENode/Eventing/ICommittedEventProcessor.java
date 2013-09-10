package ENode.Eventing;

import ENode.Messaging.*;

/** Represents a processor to process committed event stream.
 
*/
public interface ICommittedEventProcessor extends IMessageProcessor<ICommittedEventQueue, EventStream>
{
}