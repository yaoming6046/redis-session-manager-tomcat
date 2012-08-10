package com.gozap.sm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gozap.sm.help.GozapJavaJson;
import com.gozap.sm.help.GozapJson;

public class TestJavaJson {

	private PersonObject initOBject() {
		PersonObject po = new PersonObject();
		po.setAge("10");
		po.setLocation("北京");
		po.setName("name");
		return po;
	}

	private List<PersonObject> initArray() {
		List<PersonObject> list = new ArrayList<PersonObject>();

		PersonObject po = new PersonObject();
		po.setAge("10");
		po.setLocation("北京");
		po.setName("beijing");
		list.add(po);

		po = new PersonObject();
		po.setAge("20");
		po.setLocation("河北");
		po.setName("hebei");
		list.add(po);
		return list;
	}

	@Test
	public void testJsonObject() throws Exception {
		PersonObject po = initOBject();
		GozapJson json = new GozapJavaJson();

		String t = json.format(po);

		po = (PersonObject) json.parseObject(t);
		System.out.println(po);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonArray() throws Exception {
		List<PersonObject> po = initArray();
		GozapJson json = new GozapJavaJson();

		String t = json.format(po);

		po = (List<PersonObject>) json.parseObject(t);
		System.out.println(po);
	}

}
