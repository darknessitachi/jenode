package ENode.Snapshoting;

import ENode.Domain.*;

/** An policy interface which used to determine whether should create a snapshot for the aggregate.
 
*/
public interface ISnapshotPolicy
{
	/** Determines whether should create a snapshot for the given aggregate root.
	 
	*/
	boolean ShouldCreateSnapshot(AggregateRoot aggregateRoot);
}