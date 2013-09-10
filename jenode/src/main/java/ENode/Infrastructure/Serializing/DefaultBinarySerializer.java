package ENode.Infrastructure.Serializing;

/** Defines a serializer to serialize object to byte array.
 
*/
public class DefaultBinarySerializer implements IBinarySerializer
{
	private final BinaryFormatter _binaryFormatter = new BinaryFormatter();

	/** Serialize an object to byte array.
	 
	 @param obj
	 @return 
	*/
	public final byte[] Serialize(Object obj)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var stream = new MemoryStream())
		MemoryStream stream = new MemoryStream();
		try
		{
			_binaryFormatter.Serialize(stream, obj);
			return stream.toArray();
		}
		finally
		{
			stream.dispose();
		}
	}
	/** Deserialize an object from a byte array.
	 
	 @param data
	 @return 
	*/
	public final Object Deserialize(byte[] data)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var stream = new MemoryStream(data))
		MemoryStream stream = new MemoryStream(data);
		try
		{
			return _binaryFormatter.Deserialize(stream);
		}
		finally
		{
			stream.dispose();
		}
	}
	/** Deserialize a typed object from a byte array.
	 
	 <typeparam name="T"></typeparam>
	 @param data
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <T extends class> T Deserialize(byte[] data)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var stream = new MemoryStream(data))
		MemoryStream stream = new MemoryStream(data);
		try
		{
			Object tempVar = _binaryFormatter.Deserialize(stream);
			return (T)((tempVar instanceof T) ? tempVar : null);
		}
		finally
		{
			stream.dispose();
		}
	}
}