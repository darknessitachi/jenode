package ENode.Infrastructure.Retring;

/** Represents a retry service interface.
 
*/
public interface IRetryService
{
	/** Initialize the retry service.
	 
	 @param period
	*/
	void Initialize(long period);
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	void TryAction(String actionName, Action action, int maxRetryCount, Action nextAction);
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, Action nextAction);
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, Func<Boolean> nextAction);
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, ActionInfo nextAction);
}