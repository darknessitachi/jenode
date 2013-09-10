package ENode.Commanding;

import ENode.Domain.*;

/** Represents an internal tracking context for tracking aggregate roots withing a command context.
 
*/
public interface ITrackingContext
{
	/** Get all the tracked aggregates.
	 
	 @return 
	*/
	Iterable<AggregateRoot> GetTrackedAggregateRoots();
	/** Clear all the tracked aggregates.
	 
	*/
	void Clear();
}