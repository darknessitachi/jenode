package ENode.Snapshoting;

/** Snapshot of aggregate.
 
*/
public class Snapshot
{
	/** Parameterized constructor.
	 
	 @param aggregateRootName
	 @param aggregateRootId
	 @param version
	 @param payload
	 @param timestamp
	*/
	public Snapshot(String aggregateRootName, String aggregateRootId, long version, Object payload, java.util.Date timestamp)
	{
		setAggregateRootName(aggregateRootName);
		setAggregateRootId(aggregateRootId);
		setVersion(version);
		setPayload(payload);
		setTimestamp(timestamp);
	}

	/** The aggregate root id.
	 
	*/
	private String privateAggregateRootId;
	public final String getAggregateRootId()
	{
		return privateAggregateRootId;
	}
	public final void setAggregateRootId(String value)
	{
		privateAggregateRootId = value;
	}
	/** The aggregate root name.
	 
	*/
	private String privateAggregateRootName;
	public final String getAggregateRootName()
	{
		return privateAggregateRootName;
	}
	public final void setAggregateRootName(String value)
	{
		privateAggregateRootName = value;
	}
	/** The aggregate root version when creating this snapshot.
	 
	*/
	private long privateVersion;
	public final long getVersion()
	{
		return privateVersion;
	}
	public final void setVersion(long value)
	{
		privateVersion = value;
	}
	/** The aggregate root payload data when creating this snapshot.
	 
	*/
	private Object privatePayload;
	public final Object getPayload()
	{
		return privatePayload;
	}
	public final void setPayload(Object value)
	{
		privatePayload = value;
	}
	/** The created time of this snapshot.
	 
	*/
	private java.util.Date privateTimestamp = new java.util.Date(0);
	public final java.util.Date getTimestamp()
	{
		return privateTimestamp;
	}
	public final void setTimestamp(java.util.Date value)
	{
		privateTimestamp = value;
	}
}