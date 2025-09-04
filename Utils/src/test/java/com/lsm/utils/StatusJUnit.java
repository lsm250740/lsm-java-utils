package com.lsm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.lsm.common.ApplicationStatus;
import com.lsm.common.Status;

class StatusJUnit {

	@Test
	void test() {
		//fail("Not yet implemented");
	}
	
	
	@Test
	void getValues() {
   	   Status.values().forEach(  s->  System.out.println( s.getName() +": " + (s instanceof Status)) );
	}
	@Test
	void getFields() {
		try {
			Set<Field> flds = ReflectionUtils.getAllFields(ApplicationStatus.class);
			flds.forEach( f-> {
				     int mode = f.getModifiers();
				     Modifier.isPublic(mode);
				     System.out.println( f.getName() +":" +Map.of("public" , Modifier.isPublic(mode),"final" , Modifier.isFinal(mode) ,"type" ,  f.getType()));
				     
			});
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void createProxy() {
		List<Status> statuses = new ArrayList<>();
		statuses.add(  buildStatus(ApplicationStatus.class, "OK",0) );
		statuses.add(   buildStatus(ApplicationStatus.class, "FAILED",-1)  );
		statuses.forEach(  s->  System.out.println( s.getName() +": " + (s instanceof Status)) );
	}
	
	private Status buildStatus(Class<?> status,String name,int code  ) {
		return (Status) Proxy.newProxyInstance(
					status.getClassLoader(),
			    new Class<?>[]{status},
			    (proxy, method, args1) -> CollectionsUtil.mapOf("getStatus",code,"getName",name)	.get(method.getName()));
	}
	
}
