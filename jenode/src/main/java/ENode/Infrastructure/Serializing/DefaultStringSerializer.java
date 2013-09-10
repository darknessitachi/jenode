package ENode.Infrastructure.Serializing;

/** The default implementation of IStringSerializer.
 
*/
public class DefaultStringSerializer implements IStringSerializer
{
	private IBinarySerializer _binarySerializer;

	/** Parameterized constructor.
	 
	 @param binarySerializer
	*/
	public DefaultStringSerializer(IBinarySerializer binarySerializer)
	{
		_binarySerializer = binarySerializer;
	}
	/** Serialize an object to string.
	 
	 @param obj
	 @return 
	*/
	public final String Serialize(Object obj)
	{
		return Convert.ToBase64String(_binarySerializer.Serialize(obj));
	}
	/** Deserialize an object from a string.
	 
	 @param data
	 @return 
	*/
	public final Object Deserialize(String data)
	{
		return _binarySerializer.Deserialize(Convert.FromBase64String(data));
	}
	/** Deserialize a typed object from a string.
	 
	 <typeparam name="T"></typeparam>
	 @param data
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <T extends class> T Deserialize(String data)
	{
		return _binarySerializer.getDeserialize()<T>(Convert.FromBase64String(data));
	}
}