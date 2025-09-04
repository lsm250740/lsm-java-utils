package com.lsm.common;

import com.lsm.utils.Assert;

public interface Resource {
	Integer getCode();

	String getName();

	String getDescription();

	default String name() {
		return getName();
	}

	default String getType() {
		return this.getClass().getSimpleName();
	}

	/**
	 * with name
	 * 
	 * @return
	 */
	default String toResourceBundleKey() {
		return String.valueOf(getType()).trim() + "." + Assert.ofEmpty(getName()).orElse(String.valueOf(getCode()));
	}

	/**
	 * with name
	 * 
	 * @return
	 */
	default String toHashKey() {
		return String.valueOf(getType()).trim() + "." + (getCode() == null ? getName() : getCode());
	}

	/**
	 * The load function doesn't updates source fields
	 * 
	 * @return default Resource load() { return
	 *         Assert.ofEmpty(getDescription()).map( d-> this).orElseGet( () ->
	 *         Resources.ofNulluble(this).orElse(this)); }

	public static String toHashKey(String type, Integer code) {
		return type + "." + code;
	}
    */
}