package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public final class DbString
{
	/** 
	 Create a new DbString
	 
	*/
	public DbString()
	{
		setLength(-1);
	}
	/** 
	 Ansi vs Unicode 
	 
	*/
	private boolean privateIsAnsi;
	public boolean getIsAnsi()
	{
		return privateIsAnsi;
	}
	public void setIsAnsi(boolean value)
	{
		privateIsAnsi = value;
	}
	/** 
	 Fixed length 
	 
	*/
	private boolean privateIsFixedLength;
	public boolean getIsFixedLength()
	{
		return privateIsFixedLength;
	}
	public void setIsFixedLength(boolean value)
	{
		privateIsFixedLength = value;
	}
	/** 
	 Length of the string -1 for max
	 
	*/
	private int privateLength;
	public int getLength()
	{
		return privateLength;
	}
	public void setLength(int value)
	{
		privateLength = value;
	}
	/** 
	 The value of the string
	 
	*/
	private String privateValue;
	public String getValue()
	{
		return privateValue;
	}
	public void setValue(String value)
	{
		privateValue = value;
	}
	/** 
	 Add the parameter to the command... internal use only
	 
	 @param command
	 @param name
	*/
	public void AddParameter(IDbCommand command, String name)
	{
		if (getIsFixedLength() && getLength() == -1)
		{
			throw new InvalidOperationException("If specifying IsFixedLength,  a Length must also be specified");
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var param = command.CreateParameter();
		param.ParameterName = name;
		param.setValue((((Object)getValue()) != null) ? (Object)getValue() : DBNull.getValue());
		if (getLength() == -1 && getValue() != null && getValue().length() <= 4000)
		{
			param.Size = 4000;
		}
		else
		{
			param.Size = getLength();
		}
		param.DbType = getIsAnsi() ? (getIsFixedLength() ? DbType.AnsiStringFixedLength : DbType.AnsiString) : (getIsFixedLength() ? DbType.StringFixedLength : DbType.String);
		command.Parameters.Add(param);
	}



}