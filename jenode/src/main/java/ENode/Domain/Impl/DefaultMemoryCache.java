package ENode.Domain.Impl;

import ENode.Infrastructure.Serializing.*;

/** Default implementation of IMemoryCache which using ConcurrentDictionary.
 
*/
public class DefaultMemoryCache implements IMemoryCache
{
	private final ConcurrentDictionary<String, byte[]> _cacheDict = new ConcurrentDictionary<String, byte[]>();
	private IBinarySerializer _binarySerializer;

	/** Parameterized constructor.
	 
	 @param binarySerializer
	*/
	public DefaultMemoryCache(IBinarySerializer binarySerializer)
	{
		_binarySerializer = binarySerializer;
	}

	/** Get an aggregate from memory cache.
	 
	 @param id
	 @return 
	*/
	public final AggregateRoot Get(Object id)
	{
		if (id == null)
		{
			throw new ArgumentNullException("id");
		}
		byte[] value;
		RefObject<Byte> tempRef_value = new RefObject<Byte>(value);
		boolean tempVar = _cacheDict.TryGetValue(id.toString(), tempRef_value);
			value = tempRef_value.argvalue;
		if (tempVar)
		{
			Object tempVar2 = _binarySerializer.Deserialize(value);
			return (AggregateRoot)((tempVar2 instanceof AggregateRoot) ? tempVar2 : null);
		}
		return null;
	}
	/** Get a strong type aggregate from memory cache.
	 
	 @param id
	 <typeparam name="T"></typeparam>
	 @return 
	*/
	public final <T extends AggregateRoot> T Get(Object id)
	{
		if (id == null)
		{
			throw new ArgumentNullException("id");
		}
		byte[] value;
		RefObject<Byte> tempRef_value = new RefObject<Byte>(value);
		T tempVar = _cacheDict.TryGetValue(id.toString(), tempRef_value) ? _binarySerializer.getDeserialize()<T>(value) : null;
		value = tempRef_value.argvalue;
		return tempVar;
	}
	/** Set an aggregate to memory cache.
	 
	 @param aggregateRoot
	 @exception ArgumentNullException
	*/
	public final void Set(AggregateRoot aggregateRoot)
	{
		if (aggregateRoot == null)
		{
			throw new ArgumentNullException("aggregateRoot");
		}
		_cacheDict[aggregateRoot.getUniqueId()] = _binarySerializer.Serialize(aggregateRoot);
	}
}