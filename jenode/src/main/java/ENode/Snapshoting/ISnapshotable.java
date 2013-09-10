package ENode.Snapshoting;

/** An interface represents a class support snapshot.
 
*/
public interface ISnapshotable<TSnapshot>
{
	/** Create a snapshot for the current object.
	 
	 @return 
	*/
	TSnapshot CreateSnapshot();
	/** Restore the status of the current object from the given snapshot.
	 
	 @param snapshot
	*/
	void RestoreFromSnapshot(TSnapshot snapshot);
}