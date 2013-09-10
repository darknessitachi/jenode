package ENode.Snapshoting;

/** An interface to store the snapshot.
 
*/
public interface ISnapshotStore
{
	/** Store the given snapshot.
	 
	 @param snapshot The snapshot to store.
	*/
	void StoreShapshot(Snapshot snapshot);
	/** Get the latest snapshot for the specified aggregate root.
	 
	 @param aggregateRootId The aggregate root id.
	 @param aggregateRootType The aggregate root type.
	 @return Returns the snapshot if exist; otherwise, returns null.
	*/
	Snapshot GetLastestSnapshot(String aggregateRootId, java.lang.Class aggregateRootType);
}