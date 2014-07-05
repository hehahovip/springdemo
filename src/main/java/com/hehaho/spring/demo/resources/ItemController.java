/**
 * 
 */
package com.hehaho.spring.demo.resources;

import com.hehaho.spring.demo.beans.Item;

/**
 * @author Kevin
 *
 */
public class ItemController {
	

	public Item newItem( String fileName){
		return new Item();
	}
	
}
