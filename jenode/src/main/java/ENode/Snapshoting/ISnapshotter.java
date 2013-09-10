package ENode.Snapshoting;

import ENode.Domain.*;

/** An interface which can create snapshot for aggregate or restore aggregate from snapshot.
 
*/
public interface ISnapshotter
{
	/** Create a snapshot for the given aggregate root.
	 
	*/
	Snapshot CreateSnapshot(AggregateRoot aggregateRoot);
	/** Restore the aggregate from the given snapshot.
	 
	*/
	AggregateRoot RestoreFromSnapshot(Snapshot snapshot);
}