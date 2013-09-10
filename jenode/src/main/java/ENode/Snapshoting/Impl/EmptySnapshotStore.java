package ENode.Snapshoting.Impl;

/** Represents a snapshot store that always not store any snapshot.
 
*/
public class EmptySnapshotStore implements ISnapshotStore
{
	/** Do nothing.
	 
	 @param snapshot
	*/
	public final void StoreShapshot(Snapshot snapshot)
	{
	}
	/** Always return null.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @return 
	*/
	public final Snapshot GetLastestSnapshot(String aggregateRootId, java.lang.Class aggregateRootType)
	{
		return null;
	}
}