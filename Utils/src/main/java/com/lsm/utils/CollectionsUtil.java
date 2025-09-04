package com.lsm.utils;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;




/**
 *  utility class that assists in validating collection and arrays.
 * @author olgaso
 *
 */
public class CollectionsUtil {
	
	/**
	 * @return true if iteratble is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isEmpty(Iterable<?> collection) {
		return IterableUtils.isEmpty(collection);
	}
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isEmpty(Collection<?> collection) {
		return CollectionUtils.isEmpty(collection);
	}
	
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isNotEmpty(Collection<?> collection) {
		return !CollectionUtils.isEmpty(collection);
	}
	
	
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isEmpty(byte[] array) {
		return array==null || array.length==0;
	}
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isEmpty(int[] array) {
		return array==null || array.length==0;
	}

	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final boolean isEmpty(Object[] array) {
		return array==null || array.length==0;
	}
	

	
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final<I extends Collection<?>> Optional<I> ofEmpty(I value) {
		return isEmpty(value)?Optional.empty(): Optional.of(value);
	}

	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final Optional<byte[]> ofEmpty(byte[] value) {
		return isEmpty(value)?Optional.empty(): Optional.of(value);
	}
	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final Optional<int[]> ofEmpty(int[] value) {
		return isEmpty(value)?Optional.empty(): Optional.of(value);
	}

	/**
	 * @return true if array is null or has no elements.
	 * @param array the array to check
	 */
	public static final<T> Optional<T[]> ofEmpty(T[] value) {
		return isEmpty(value)?Optional.empty(): Optional.of(value);
	}

	
   /**
	 * @return true if map is null or has no elements.
	 * @param map the array to check
	 */
	public static final boolean isEmpty(Map<?,?> map) {
		return map==null || map.size()==0;
	}
	
   /**
	 * @return true if map is null or has no elements.
	 * @param map the array to check
	 */
	public static final<K,V> Optional<Map<K,V>> ofEmpty(Map<K,V> value) {
		return isEmpty(value)?Optional.empty(): Optional.of(value);
	}

	/**
	 * returns Map - @see Map.of but allows Null values
	 * key Null value also not allowed
	 * @param map the array to check
	 */
	@SuppressWarnings("unchecked")
	public static final<K,V> Map<K, V> mapOf( Object... input){
	    Map<K,V> map = new LinkedHashMap<>();
		Objects.requireNonNull(input);
        if ((input.length & 1) != 0) { // implicit null check of input
            throw new InternalError("length is odd");
        }
        for (int i = 0; i < input.length; i += 2) {
                K k = Objects.requireNonNull((K)input[i]);
                V v = (V)input[i+1];
            if (map.containsKey(k) ) {
                throw new IllegalArgumentException("duplicate key: " + k);
            } else {
            	map.put(k, v);
            }
        }
		return map;
	}  
	
	/**
	 * splits Collection by number of items
     * @param <A> - the type of the input and output objects to the function 
	 * @param in - input collection
	 * @param pageSize - split size
	 * @return - list of lists with max size pageSize 
	 */
	public static final <A> List<List<A>> split( List<A> in,
	                                       int pageSize) {
		return IntStream.range(0, (in.size() + pageSize - 1) / pageSize)
		        .boxed()
		        .map(i -> in.subList(i * pageSize, min(pageSize * (i + 1), in.size())))
		        .collect(Collectors.toList());
	}
	/**
	 * compares List of Objects
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static  boolean deepEquals(List<?> a1, List<?> a2) {
        if (a1 == a2)
            return true;
        if (a1 == null || a2==null)
            return false;
        int length = a1.size();
        if (a2.size() != length)
            return false;

        for (int i = 0; i < length; i++) {
            Object e1 = a1.get(i);
            Object e2 = a2.get(i);

            if (e1 == e2)
                continue;
            if (e1 == null)
                return false;

            // Figure out whether the two elements are equal
            boolean eq = Objects.equals(e1,e2);

            if (!eq)
                return false;
        }
        return true;
    }
	
	public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
	    if (coll1.size() < coll2.size()) {
	        for (final Object aColl1 : coll1) {
	            if (coll2.contains(aColl1)) {
	                return true;
	            }
	        }
	    } else {
	        for (final Object aColl2 : coll2) {
	            if (coll1.contains(aColl2)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}


	/**
	 * merge all kinds of collections into the set
	 * @param coll
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> Set<T> merge(final Collection<T>... coll) {
		return  Stream.of(coll).filter( s-> s!=null).flatMap( s->((Collection<T>) s).stream()).collect(Collectors.toSet());
	}
	
	/**
	 * @param <T> 
	 * @param in source
	 * @param compare collection to compare
	 * @return all keys, not existed in in source collection
	 */
	public static<T> Set<T> difference(final Set<T> in ,final Set<T> compare) {
		if(in ==null) {
			return compare;
		}
		return ofEmpty(compare).map( s-> {
			Set<T> res = new HashSet<>(in);
			res.removeAll(compare);
			return res;
		})
		.orElse(null);
	}
	
	/**
	 * @param <T> 
	 * @param in source
	 * @param compare collection to compare
	 * @return all keys, not existed in in source collection
	 */
	public static<T,V> Map<T,V> difference(final Map<T,V> in ,final Map<T,V>  compare) {
		if(in ==null) {
			return compare;
		}
	    
		return ofEmpty(compare)
				.map( s-> difference(compare.keySet(), in.keySet()))
		        .map( s-> {
		        	final Map<T,V> v=	new HashMap<>();
		        	s.forEach( k-> v.put( k, compare.get(k)));
		        	return v;
		        })		
		.orElse(null);
	}
	/**
	 * @param <T> 
	 * @param in source
	 * @param compare collection to compare
	 * @return the merged map
	 */
	@SuppressWarnings("unchecked")
	public static<T,V> Map<T,V> merge(final Map<T,V> in ,final Map<T,V>  compare) {
		if(in ==null) {
			return  ReflectionUtils.newInstance(compare.getClass(),new Class[] {Map.class},compare);
		}
	    
		return ofEmpty(compare)
				.map( s->(Map<T,V>) ReflectionUtils.newInstance(compare.getClass(),new Class[] {Map.class},in))
		        .orElse(in);
	}
	/**
	 * Set.of implementation with no verifications on null or duplicates
	 * Null values also are added
	 * @return - Set<I> not immutable 
	 */
	public static final<I>  Set<I> setOf( @SuppressWarnings("unchecked") I... args ) {
		if(!isEmpty(args)) {
			Set<I> set = new HashSet<>();
			for ( int i = 0; i<= args.length-1; i++ ){
				set.add( (I) args[i]);
			}
			return set;
		}
		return null;
	}
	
	public static final<I>  List<I> listOf(@SuppressWarnings("unchecked") I... args){
		if(!isEmpty(args)) {
			List<I> set = new ArrayList<>();
			for ( int i = 0; i<= args.length-1; i++ ){
				set.add( (I) args[i]);
			}
			return set;
		}
		return null;
	}
	
	/**
	 * Returns String with specified delimiter
	 * If args is null or empty, returns empty string
	 * skips null values
	 * Not returns Null and never fails
	 * @param delimiter the delimiter to use
	 * @param args the objects to join
	 * @return joined string
	 */
	public static String join(String delimiter, Object... args) {
		if(isEmpty(args)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		for(Object arg : args) {
			if(arg != null) {
				if(!first) {
					sb.append(delimiter != null ? delimiter : "");
				}
				sb.append(arg.toString());
				first = false;
			}
		}
		
		return sb.toString();
	}
	

}
