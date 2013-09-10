package ENode.Domain;

/** A DDD value object base class. Provide the mechanism to compare two objects by values.
 
*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public abstract class ValueObject<T extends class> implements Serializable
{
	/** Returns all the atomic values of the current object.
	 
	*/
	public abstract Iterable<Object> GetAtomicValues();
	/** Clone a new object from the current object with the specified default values.
	 
	 @param objectContainsNewValues
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public T Clone(object objectContainsNewValues = null)
	public final T clone(Object objectContainsNewValues)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var propertyInfos = getClass().GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var newPropertyInfoArray = objectContainsNewValues != null ? objectContainsNewValues.getClass().GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.DeclaredOnly) : null;
		Object tempVar = T.class.GetConstructor(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic, null, java.lang.Class.EmptyTypes, null).invoke(null);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var cloneObject = (T)((tempVar instanceof T) ? tempVar : null);

		if (newPropertyInfoArray != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var propertyInfo : propertyInfos)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				var property = newPropertyInfoArray.FirstOrDefault(x => x.getName() == propertyInfo.getName());
				propertyInfo.SetValue(cloneObject, property != null ? property.GetValue(objectContainsNewValues, null) : propertyInfo.GetValue(this, null), null);
			}
		}
		else
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var propertyInfo : propertyInfos)
			{
				propertyInfo.SetValue(cloneObject, propertyInfo.GetValue(this, null), null);
			}
		}

		return cloneObject;
	}

	/** Operator overrides.
	 
	*/
	public static boolean OpEquality(ValueObject<T> left, ValueObject<T> right)
	{
		return IsEqual(left, right);
	}
	/** Operator overrides.
	 
	*/
	public static boolean OpInequality(ValueObject<T> left, ValueObject<T> right)
	{
		return !IsEqual(left, right);
	}
	/** Method overrides.
	 
	*/
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || obj.getClass() != getClass())
		{
			return false;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var other = (ValueObject<T>)obj;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator1 = GetAtomicValues().iterator();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator2 = other.GetAtomicValues().iterator();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator1HasNextValue = enumerator1.MoveNext();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator2HasNextValue = enumerator2.MoveNext();

		while (enumerator1HasNextValue && enumerator2HasNextValue)
		{
			if (ReferenceEquals(enumerator1.Current, null) ^ ReferenceEquals(enumerator2.Current, null))
			{
				return false;
			}
			if (enumerator1.Current != null)
			{
				if (enumerator1.Current instanceof java.util.List && enumerator2.Current instanceof java.util.List)
				{
					if (!CompareEnumerables((java.util.List)((enumerator1.Current instanceof java.util.List) ? enumerator1.Current : null), (java.util.List)((enumerator2.Current instanceof java.util.List) ? enumerator2.Current : null)))
					{
						return false;
					}
				}
				else if (!enumerator1.Current.equals(enumerator2.Current))
				{
					return false;
				}
			}
			enumerator1HasNextValue = enumerator1.MoveNext();
			enumerator2HasNextValue = enumerator2.MoveNext();
		}

		return !enumerator1HasNextValue && !enumerator2HasNextValue;
	}
	/** Method overrides.
	 
	*/
	@Override
	public int hashCode()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return GetAtomicValues().Select(x => x != null ? x.hashCode() : 0).Aggregate((x, y) => x ^ y);
	}

	private static boolean IsEqual(ValueObject<T> left, ValueObject<T> right)
	{
		if (ReferenceEquals(left, null) ^ ReferenceEquals(right, null))
		{
			return false;
		}
		return ReferenceEquals(left, null) || left.equals(right);
	}
	private static boolean CompareEnumerables(Iterable enumerable1, Iterable enumerable2)
	{
		if (enumerable1 == null)
		{
			throw new ArgumentNullException("enumerable1");
		}
		if (enumerable2 == null)
		{
			throw new ArgumentNullException("enumerable2");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator1 = enumerable1.iterator();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator2 = enumerable2.iterator();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator1HasNextValue = enumerator1.MoveNext();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var enumerator2HasNextValue = enumerator2.MoveNext();

		while (enumerator1HasNextValue && enumerator2HasNextValue)
		{
			if (ReferenceEquals(enumerator1.Current, null) ^ ReferenceEquals(enumerator2.Current, null))
			{
				return false;
			}
			if (enumerator1.Current != null && enumerator2.Current != null)
			{
				if (enumerator1.Current instanceof java.util.List && enumerator2.Current instanceof java.util.List)
				{
					if (!CompareEnumerables((java.util.List)((enumerator1.Current instanceof java.util.List) ? enumerator1.Current : null), (java.util.List)((enumerator2.Current instanceof java.util.List) ? enumerator2.Current : null)))
					{
						return false;
					}
				}
				else if (!enumerator1.Current.equals(enumerator2.Current))
				{
					return false;
				}
			}
			enumerator1HasNextValue = enumerator1.MoveNext();
			enumerator2HasNextValue = enumerator2.MoveNext();
		}

		return !enumerator1HasNextValue && !enumerator2HasNextValue;
	}
}