package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public class FeatureSupport
{
	/** 
	 Dictionary of supported features index by connection type name
	 
	*/
	java.util.HashMap<String, FeatureSupport> tempVar = new java.util.HashMap<String, FeatureSupport>(StringComparer.InvariantCultureIgnoreCase);
	tempVar.{"sqlserverconnection", new FeatureSupport { Arrays = false}};
	tempVar.{"npgsqlconnection", new FeatureSupport {Arrays = true}};
	private static final java.util.HashMap<String, FeatureSupport> FeatureList = tempVar;

	/** 
	 Gets the featureset based on the passed connection
	 
	*/
	public static FeatureSupport Get(IDbConnection connection)
	{
		String name = connection.getClass().getName();
		FeatureSupport features = null;
		return (features = FeatureList.get(name)) != null ? features : FeatureList.values().First();
	}

	/** 
	 True if the db supports array columns e.g. Postgresql
	 
	*/
	private boolean privateArrays;
	public final boolean getArrays()
	{
		return privateArrays;
	}
	public final void setArrays(boolean value)
	{
		privateArrays = value;
	}



}