package org.jboss.arquillian.populator.nosql.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import org.arquillian.cube.HostIp;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.populator.nosql.api.NoSqlPopulator;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CouchbaseTest {

    @HostIp
    private String hostIp;

    @ArquillianResource
    @Couchbase
    NoSqlPopulator populator;

    @Test
    public void should_find_books() {
        populator.forServer(hostIp, 0)
                .withStorage("travel-sample")
                .usingDataSet("airlines.json")
                .execute();

        CouchbaseCluster couchbaseCluster = CouchbaseCluster.create(hostIp);
        final Bucket books = couchbaseCluster.openBucket("travel-sample");
        final JsonDocument vueling = books.get("airline_1");
        final JsonObject vuelingObject = vueling.content();

        assertThat(vuelingObject.getString("name")).isEqualTo("Vueling Airlines");
    }

}
