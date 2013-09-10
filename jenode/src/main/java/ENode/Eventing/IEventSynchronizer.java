package ENode.Eventing;

/** Represents a event persistence synchronizer.
 
  Code can be executed before and after the event persistence.
 
 
*/
public interface IEventSynchronizer
{
	/** Executed before persisting the event.
	 
	*/
	void OnBeforePersisting(IEvent evnt);
	/** Executed after the event was persisted.
	 
	*/
	void OnAfterPersisted(IEvent evnt);
	/** Represents the inner generic IEventSynchronizer.
	 
	*/
	Object GetInnerSynchronizer();
}
/** Represents a event persistence synchronizer.
 
  Code can be executed before and after the event persistence.
 
 
 <typeparam name="TEvent"></typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: Java does not allow specifying covariance or contravariance in a generic type list:
//ORIGINAL LINE: public interface IEventSynchronizer<in TEvent> where TEvent : class, IEvent
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public interface IEventSynchronizer<TEvent extends class & IEvent>
{
	/** Executed before persisting the event.
	 
	*/
	void OnBeforePersisting(TEvent evnt);
	/** Executed after the event was persisted.
	 
	*/
	void OnAfterPersisted(TEvent evnt);
}
