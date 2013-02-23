/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */
package com.akiban.server.entity.changes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akiban.ais.model.AkibanInformationSchema;
import com.akiban.ais.model.Join;
import com.akiban.ais.model.TableName;
import com.akiban.ais.model.Types;
import com.akiban.ais.model.UserTable;
import com.akiban.server.test.it.ITBase;

public class EntityParserIT extends ITBase {
    private static final Logger LOG = LoggerFactory.getLogger(EntityParserIT.class);

    private EntityParser parser;
    
    @Before
    public void createParser() {
        parser = new EntityParser(dxl());
    }
 
    
    @Test 
    public void testCustomer() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "customers");

        String postInput = "{\"cid\": 3, \"first_name\": \"Bobby\",\"last_name\": \"Jones\"}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());
        
        assertNotNull(ais.getTable(tableName));
        assertTrue(ais.getTable(tableName).getColumns().size() == 3);
        assertTrue(ais.getTable(tableName).getColumn(0).getName().equals("cid"));
        assertTrue(ais.getTable(tableName).getColumn(0).getType().equals(Types.BIGINT));
        assertTrue(ais.getTable(tableName).getColumn(1).getName().equals("first_name"));
        assertTrue(ais.getTable(tableName).getColumn(1).getType().equals(Types.VARCHAR));
        assertTrue(ais.getTable(tableName).getColumn(2).getName().equals("last_name"));
        assertTrue(ais.getTable(tableName).getColumn(2).getType().equals(Types.VARCHAR));
    }
    
    @Test
    public void testOrders() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "orders");
        String postInput = "{\"oid\" : 103, \"cid\" : 2, \"odate\": \"2012-12-31 12:00:00\"}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());
        assertNotNull(ais.getTable(tableName));
        assertTrue(ais.getTable(tableName).getColumns().size() == 3);
        assertTrue(ais.getTable(tableName).getColumn(0).getType().equals(Types.BIGINT));
    }
    
    @Test
    public void testCA() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "customers");
        String postInput ="{\"cid\": 6, \"first_name\": \"John\",\"last_name\": \"Smith\"," +
                "\"test.addresses\": {\"aid\": 104, \"cid\": 6, \"state\": \"MA\", \"city\": \"Boston\"}}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());
        
        assertNotNull (ais.getTable(tableName));
        UserTable c = ais.getUserTable(tableName);
        assertTrue(c.getColumns().size() == 4);
        assertTrue(c.getColumn(3).getName().equals("_customers_id"));
        assertTrue(c.getColumn(3).getType().equals(Types.INT));

        tableName = new TableName ("test", "addresses");
        assertNotNull (ais.getTable(tableName));
        UserTable a = ais.getUserTable(tableName);
        assertTrue(a.getColumns().size() == 5);
        assertTrue(a.getColumn(0).getName().equals("aid"));
        assertTrue(a.getColumn(1).getName().equals("cid"));
        assertTrue(a.getColumn(2).getName().equals("state"));
        assertTrue(a.getColumn(3).getName().equals("city"));
        assertTrue(a.getColumn(4).getName().equals("_customers_id"));
        
        assertNotNull(a.getParentJoin());
        Join join = a.getParentJoin();
        assertTrue (join.getParent() == c);
        assertTrue (join.getChild() == a);
    }
    
    @Test
    public void testJsonTypes() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "customers");
        String postInput ="{\"cid\": 6, \"first_name\": \"John\", \"ordered\": false, \"order_date\": null, \"tax_rate\": 0.01}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());

        assertNotNull (ais.getTable(tableName));
        UserTable c = ais.getUserTable(tableName);
        assertTrue(c.getColumns().size() == 5);
        assertTrue(c.getColumn(0).getName().equals("cid"));
        assertTrue(c.getColumn(0).getType().equals(Types.BIGINT));
        assertTrue(c.getColumn(1).getName().equals("first_name"));
        assertTrue(c.getColumn(1).getType().equals(Types.VARCHAR));
        assertTrue(c.getColumn(2).getName().equals("ordered"));
        assertTrue(c.getColumn(2).getType().equals(Types.INT));
        assertTrue(c.getColumn(3).getName().equals("order_date"));
        assertTrue(c.getColumn(3).getType().equals(Types.VARCHAR));
        assertTrue(c.getColumn(4).getName().equals("tax_rate"));
        assertTrue(c.getColumn(4).getType().equals(Types.DOUBLE));
    }
    
    @Test
    public void testCAO() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "customers");
        String postInput ="{\"cid\": 6, \"first_name\": \"John\",\"last_name\": \"Smith\"," +
                "\"addresses\": {\"aid\": 104, \"cid\": 6, \"state\": \"MA\", \"city\": \"Boston\"}," +
                "\"orders\": {\"oid\" : 103, \"cid\" : 2, \"odate\": \"2012-12-31 12:00:00\"}}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());
        
        assertNotNull(ais.getTable(new TableName("test", "customers")));
        UserTable c = ais.getUserTable(tableName);
        
        assertNotNull (ais.getTable(new TableName("test", "addresses")));
        UserTable a = ais.getUserTable (new TableName ("test", "addresses"));
        assertNotNull (ais.getTable(new TableName("test", "orders")));
        UserTable o = ais.getUserTable(new TableName ("test", "orders"));
        Join join = a.getParentJoin();
        assertTrue (join.getParent() == c);
        assertTrue (join.getChild() == a);

        join = o.getParentJoin();
        assertTrue (join.getParent() == c);
        assertTrue (join.getChild() == o);
        
    }
    
    @Test
    public void testCA_array() throws JsonProcessingException, IOException {
        TableName tableName = new TableName ("test", "customers");
        String postInput ="{\"cid\": 6, \"first_name\": \"John\",\"last_name\": \"Smith\"," +
                "\"addresses\": [{\"aid\": 104, \"cid\": 6, \"state\": \"MA\", \"city\": \"Boston\"}, " +
                "{\"aid\": 105, \"cid\": 6, \"state\": \"MA\", \"city\": \"Boston\"}]}";
        ObjectMapper m = new ObjectMapper();
        JsonNode node = m.readTree(postInput);
        parser.parse(session(), tableName, node);
        AkibanInformationSchema ais = dxl().ddlFunctions().getAIS(session());
        
        assertNotNull (ais.getTable(tableName));
        UserTable c = ais.getUserTable(tableName);

        tableName = new TableName ("test", "addresses");
        assertNotNull (ais.getTable(tableName));
        UserTable a = ais.getUserTable(tableName);
        assertNotNull(a.getParentJoin());
        Join join = a.getParentJoin();
        assertTrue (join.getParent() == c);
        assertTrue (join.getChild() == a);
    }
}
