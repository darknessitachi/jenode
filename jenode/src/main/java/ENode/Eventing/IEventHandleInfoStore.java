package ENode.Eventing;

/** Represents a storage to store the event handle information of aggregate.
 
*/
public interface IEventHandleInfoStore
{
	/** Add an event handle info.
	 
	*/
	void AddEventHandleInfo(Guid eventId, String eventHandlerTypeName);
	/** Check whether the given event was handled by the given event handler.
	 
	*/
	boolean IsEventHandleInfoExist(Guid eventId, String eventHandlerTypeName);
}