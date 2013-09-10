package ENode.Eventing;

/** Represents a event handler.
 
*/
public interface IEventHandler
{
	/** Handle the given event.
	 
	 @param evnt
	*/
	void Handle(Object evnt);
	/** Get the inner event handler.
	 
	 @return 
	*/
	Object GetInnerEventHandler();
}
/** Represents a event handler.
 
 <typeparam name="TEvent"></typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: Java does not allow specifying covariance or contravariance in a generic type list:
//ORIGINAL LINE: public interface IEventHandler<in TEvent> where TEvent : class, IEvent
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public interface IEventHandler<TEvent extends class & IEvent>
{
	/** Handle the given event.
	 
	 @param evnt
	*/
	void Handle(TEvent evnt);
}
