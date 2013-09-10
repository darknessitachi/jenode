package ENode.Messaging;

import java.io.Serializable;

import ENode.Guid;

/** Represents a message.
 
*/
public class Message implements IMessage, Serializable
{
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
	//[NonSerialized]
	private boolean _isRestoreFromStorage = false;

	/** Represents the unique identifier for the message.
	 
	*/
	private Guid privateId = new Guid();
	public final Guid getId()
	{
		return privateId;
	}
	private void setId(Guid value)
	{
		privateId = value;
	}

	/** Parameterized constructor
	 
	 @param id
	*/
	public Message(Guid id)
	{
		setId(id);
	}

	/** Returns whether the message is restore from the message store.
	 
	*/
	public final boolean IsRestoreFromStorage()
	{
		return _isRestoreFromStorage;
	}
	/** Mark the message that is restored from storage.
	 
	*/
	public final void MarkAsRestoreFromStorage()
	{
		_isRestoreFromStorage = true;
	}
}