package ENode.Snapshoting.Impl;

import ENode.Domain.*;

/** A policy that always not create snapshot.
 
*/
public class NoSnapshotPolicy implements ISnapshotPolicy
{
	/** Always return false.
	 
	 @param aggregateRoot
	 @return 
	*/
	public final boolean ShouldCreateSnapshot(AggregateRoot aggregateRoot)
	{
		return false;
	}
}