package com.lsm.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.lsm.utils.MessageFormatUtil;
import com.lsm.utils.ReflectionUtils;
import com.lsm.utils.cache.CachesBundle;

@Lazy
public interface Status extends Comparable<Status> {
	
	
	Integer getCode();

	String getName();

	String getDescription();
	
	Boolean isRerunable();
	
	public default String name(){
		return getName();
	}
	public static boolean isOk(Status status){
		return isOk(status.getCode());
	}

	public static boolean isOk(Integer status){
		return status == null || status.intValue() >= 0;
	}

	default boolean isFailed(){
		return isFailed(getCode());
	}

	public static boolean isFailed(Integer status){
		return (status != null && status.intValue() < 0);
	}

	public static boolean isFailed(Status status){
		return (status != null && status.getCode().intValue() < 0);
	}

	default boolean isRerunableError(){
		return isRerunableError(getCode());
	}
	
	static public boolean isRerunableError(Integer code) {
	    if (Status.isFailed(code)) {
	        return Optional.ofNullable(CachesBundle.INST.get("Status").get(code))
	                .map(Status.class::cast)
	                .map(Status::isRerunable)   
	                .orElse(false);             
	    }
	    return false;
	}

	public static Optional<Status> ofNulluble(Integer status){
		return  Optional.ofNullable(CachesBundle.INST.get("Status").get(status))
                .map(Status.class::cast);
	}
	
	public static Optional<Status> ofNulluble(String status){
		return  Optional.ofNullable(CachesBundle.INST.get("Status").get(status))
                .map(Status.class::cast);
	}

	public static Status of(Integer status){
		
		return ofNulluble( status).orElse(null);
	}
	
	public static Status of(String status){
		return ofNulluble( status).orElse(null);
	}

		
	@Override
	default int compareTo(Status o) {
		return getCode().compareTo(o.getCode());
	}

	
	public static Collection<? extends Status> values() {
		Map<String, ? extends Status>    map =  CachesBundle.INST.get("Status-Values", c -> build());
	    return map.values();
	}
  
  private static Map<String, ? extends Status>   build(){ 
	 Set<Class<? extends Status>> types = ReflectionUtils.getSubTypesOf(Status.class);
	Map<String, ? extends Status> res = types.stream(). flatMap( t->   ReflectionUtils.getAllFields(t).stream()
		                     .filter( e  -> isEnumField (e))
	                         .map (e ->  ReflectionUtils.buildProxy(t, "getName",e.getName(),"getCode",ReflectionUtils.getValue(e,"code"))
                         	  )).collect(Collectors.toMap(Status::getName, s->s, (v1, v2) -> v2));             
	 return res;
	  }
  
  private static boolean isEnumField(Field f){
	  int mode = f.getModifiers();
	  
	 System.out.println(MessageFormatUtil.format(
			  "name: {} , public: {} , static: {} , final: {} , type: {}"
			 , f.getName()
			  , Modifier.isPublic(mode)
			  , Modifier.isStatic(mode)
              , Modifier.isFinal(mode)
             , f.getType()));
	  return	  Modifier.isPublic(mode) && Modifier.isStatic(mode) 
			                                                    && Modifier.isFinal(mode)	 
			                                                    && (f.getType().equals(Integer.class)
			                                                     || f.getType().equals(int.class)
			                                                     || f.getType().equals(short.class));
  }
}
