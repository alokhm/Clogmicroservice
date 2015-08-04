package com.covisint.platform.clog.server.cloginstance.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.covisint.platform.clog.model.Action;
import com.covisint.platform.clog.model.Add;
import com.covisint.platform.clog.model.Bool;
import com.covisint.platform.clog.model.ClogInstance;
import com.covisint.platform.clog.model.Filter;
import com.covisint.platform.clog.model.Should;
import com.covisint.platform.clog.model.Term;

public class ClogInstanceJsonHelper {
	
	private static final String ALIAS = "ALIAS";
	private static final String INDEX = "covs_logs";
	private static final String GROUP_ID = "GROUP_ID";
	private static final String REALM_ID = "REALM_ID";
	
	/** Class logger. */
    private static final Logger logger = LoggerFactory.getLogger(ClogInstanceJsonHelper.class);

    /**
     * Main class for verify the Json payload
     * @param alias
     * @param index
     * @param group_id
     * @param realm_id
     * @return 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    
	public static void main(String[] args) throws JsonGenerationException,
			JsonMappingException, IOException {
		String jsonPayLoad = new ClogInstanceJsonHelper()
				.createJsonForClogInstance(ALIAS, INDEX,
						GROUP_ID, REALM_ID);
		logger.info(jsonPayLoad);
	}
/**
 * 
 * @param alias
 * @param index
 * @param group_id
 * @param realm_id
 * @return 
 * @throws JsonGenerationException
 * @throws JsonMappingException
 * @throws IOException
 */
	public String createJsonForClogInstance(String alias, String index,
			String group_id, String realm_id) throws JsonGenerationException,
			JsonMappingException, IOException {

		return new ObjectMapper().writeValueAsString(createClogInstance(alias,
				index, group_id, realm_id));
	}
/**
 * 
 * @param alias
 * @param index
 * @param group_id
 * @param realm_id
 * @return ClogInstance
 */
	private ClogInstance createClogInstance(String alias, String index,
			String group_id, String realm_id) {

		ClogInstance clogInstance = new ClogInstance();
		Filter filter = createFilter(createBool(createShouldList(group_id,
				realm_id)));
		clogInstance
				.setActions(creatActionList(createAdd(alias, index, filter)));
		return clogInstance;

	}
/**
 * 
 * @param add
 * @return list of action
 */
	private List<Action> creatActionList(Add add) {
		List<Action> actionList = new ArrayList<Action>();
		Action action = new Action();
		action.setAdd(add);
		actionList.add(action);
		return actionList;
	}
/**
 * 
 * @param alias
 * @param index
 * @param filter
 * @return add
 */
	private Add createAdd(String alias, String index, Filter filter) {
		Add add = new Add();
		add.setAlias(alias);
		add.setIndex(index);
		add.setFilter(filter);
		return add;
	}
/**
 * 
 * @param bool
 * @return filter
 */
	private Filter createFilter(Bool bool) {
		Filter filter = new Filter();
		filter.setBool(bool);
		return filter;
	}
/**
 * 
 * @param shouldList
 * @return bool
 */
	private Bool createBool(List<Should> shouldList) {
		Bool bool = new Bool();
		bool.setShould(shouldList);
		return bool;
	}
/**
 * 
 * @param group_id
 * @param realm_id
 * @return list of should
 */
	private List<Should> createShouldList(String group_id, String realm_id) {
		List<Should> shouldList = new ArrayList<Should>();
		Should should = new Should();
		Term term = new Term();
		term.setGroupId(group_id);
		should.setTerm(term);
		Should should1 = new Should();
		Term t1 = new Term();
		t1.setX_realm(realm_id);
		should1.setTerm(t1);
		shouldList.add(should);
		shouldList.add(should1);
		return shouldList;
	}

}
