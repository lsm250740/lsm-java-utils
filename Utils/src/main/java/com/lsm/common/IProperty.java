package com.lsm.common;

import java.util.Optional;

@Lazy
public interface IProperty<T extends IProperty<T>> {

	String getName();

	@SuppressWarnings("unchecked")
	default T getValue() {
		return	(T) this;
	}
	default T valueOf() {
		return of( null).orElse(null);
	}
	
	default Optional<T> of() {
		return of( null);
	}

	<I> Optional<T> of(I defaultValue);
	
	
}
