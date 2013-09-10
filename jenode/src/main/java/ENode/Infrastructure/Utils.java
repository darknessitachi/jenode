package ENode.Infrastructure;

/** A class provides utility methods.
 
*/
public final class Utils
{
	/** Convert the given object to a given strong type.
	 
	*/
	public static <T> T ConvertType(Object value)
	{
		if (value == null)
		{
			return null;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var typeConverter1 = TypeDescriptor.GetConverter(T.class);
		if (typeConverter1.CanConvertFrom(value.getClass()))
		{
			return (T)typeConverter1.ConvertFrom(value);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var typeConverter2 = TypeDescriptor.GetConverter(value.getClass());
		if (typeConverter2.CanConvertTo(T.class))
		{
			return (T)typeConverter2.ConvertTo(value, T.class);
		}

		return (T)Convert.ChangeType(value, T.class);
	}
	/** Create an object from the source object, assign the properties by the same name.
	 
	 <typeparam name="T"></typeparam>
	 @param source
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
//C# TO JAVA CONVERTER TODO TASK: The C# 'new()' constraint has no equivalent in Java:
	public static <T extends class & new()> T CreateObject(Object source)
	{
		T obj = new T();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var propertiesFromSource = source.getClass().GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var properties = T.class.GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var property : properties)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			var sourceProperty = propertiesFromSource.FirstOrDefault(x => x.getName() == property.getName());
			if (sourceProperty != null)
			{
				property.SetValue(obj, sourceProperty.GetValue(source, null), null);
			}
		}

		return obj;
	}
	/** Update the target object by the source object, assign the properties by the same name.
	 
	 <typeparam name="TTarget"></typeparam>
	 <typeparam name="TSource"></typeparam>
	 @param target
	 @param source
	 @param propertyExpressionsFromSource
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public static <TTarget extends class, TSource extends class> void UpdateObject(TTarget target, TSource source, Expression... propertyExpressionsFromSource)
	{
		if (target == null)
		{
			throw new ArgumentNullException("target");
		}
		if (source == null)
		{
			throw new ArgumentNullException("source");
		}
		if (propertyExpressionsFromSource == null)
		{
			throw new ArgumentNullException("propertyExpressionsFromSource");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var properties = target.getClass().GetProperties();

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var propertyExpression : propertyExpressionsFromSource)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var propertyFromSource = GetProperty<TSource, Object>(propertyExpression);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			var propertyFromTarget = properties.SingleOrDefault(x => x.getName() == propertyFromSource.getName());
			if (propertyFromTarget != null)
			{
				propertyFromTarget.SetValue(target, propertyFromSource.GetValue(source, null), null);
			}
		}
	}
	/** Parse the current string to enum type.
	 
	 <typeparam name="TEnum"></typeparam>
	 @param value
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'struct' constraint has no equivalent in Java:
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static TEnum ParseEnum<TEnum>(this string value) where TEnum : struct
	public static <TEnum extends struct> TEnum ParseEnum(String value)
	{
		TEnum result = null;
		RefObject<TEnum> tempRef_result = new RefObject<TEnum>(result);
		boolean tempVar = !Enum.<TEnum>TryParse(value, true, tempRef_result);
			result = tempRef_result.argvalue;
		if (tempVar)
		{
			result = null;
		}
		return result;
	}

	private static <TSource, TProperty> PropertyInfo GetProperty(Expression<Func<TSource, TProperty>> lambda)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var type = TSource.class;
		MemberExpression memberExpression = null;

		switch (lambda.Body.NodeType)
		{
			case ExpressionType.Convert:
				memberExpression = (MemberExpression)((((UnaryExpression)lambda.Body).Operand instanceof MemberExpression) ? ((UnaryExpression)lambda.Body).Operand : null);
				break;
			case ExpressionType.MemberAccess:
				memberExpression = (MemberExpression)((lambda.Body instanceof MemberExpression) ? lambda.Body : null);
				break;
		}

		if (memberExpression == null)
		{
			throw new IllegalArgumentException(String.format("Invalid Lambda Expression '%1$s'.", lambda.toString()));
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var propInfo = (PropertyInfo)((memberExpression.Member instanceof PropertyInfo) ? memberExpression.Member : null);
		if (propInfo == null)
		{
			throw new IllegalArgumentException(String.format("Expression '%1$s' refers to a field, not a property.", lambda.toString()));
		}

		if (type != propInfo.ReflectedType && !type.IsSubclassOf(propInfo.ReflectedType))
		{
			throw new IllegalArgumentException(String.format("Expresion '%1$s' refers to a property that is not from type %2$s.", lambda.toString(), type));
		}

		return propInfo;
	}
}