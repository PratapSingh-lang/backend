package in.co.bel.ims.initial.service.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImsStreamUtils {
	
	/**
	 * Fetch unique objects based on the property passed using streams
	 * 
	 * Single property: filter(distinctByKey(e -> e.getName()))
	 * 
	 * Multiple Properties: filter(distinctByKey(e -> e.getName() + e.getSalary()))
	 * 
	 * Filtered result contains the first occurrence of the duplicate object
	 * 
	 * @param <T>
	 * @param keyExtractor
	 * @return
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
