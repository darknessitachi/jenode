package ENode.Commanding;

import ENode.Infrastructure.*;

/** An interface of manager to manage the command async result.
 
*/
public interface ICommandAsyncResultManager
{
	/** Add a command async result.
	 
	 @param commandId
	 @param commandAsyncResult
	*/
	void Add(Guid commandId, CommandAsyncResult commandAsyncResult);
	/** Remove a command async result.
	 
	 @param commandId
	*/
	void Remove(Guid commandId);
	/** Try to complete a command async result if exist;
	 
	 @param commandId
	 @param aggregateRootId
	*/
	void TryComplete(Guid commandId, String aggregateRootId);
	/** Try to complete a command async result if it exist.
	 
	 @param commandId
	 @param aggregateRootId
	 @param errorInfo
	*/
	void TryComplete(Guid commandId, String aggregateRootId, ErrorInfo errorInfo);
}