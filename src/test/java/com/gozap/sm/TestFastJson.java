package com.gozap.sm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gozap.sm.help.GozapFastJson;
import com.gozap.sm.help.GozapJson;

public class TestFastJson {

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
		GozapJson json = new GozapFastJson();

		String t = json.format(po);

		Object o = json.parseObject(t);

		System.out.println(o);
	}

	@Test
	public void testJsonArray() throws Exception {
		List<PersonObject> po = initArray();
		GozapJson json = new GozapFastJson();

		String t = json.format(po);

		List<Object> list = (List<Object>) json.parseArray(t);
		System.out.println(list);
	}

}
