package ENode.Messaging;

import ENode.Guid;

/** Represents a message.
 
*/
public interface IMessage
{
	/** Represents the unique identifier for the message.
	 
	*/
	Guid getId();
	/** Returns whether the message is restore from the message store.
	 
	*/
	boolean IsRestoreFromStorage();
	/** Mark the message that is restored from storage.
	 
	*/
	void MarkAsRestoreFromStorage();
}