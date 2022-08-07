/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.xpanda.dsl.server.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.sql.type.SqlTypeName;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Schema mapped onto a directory of MONGO files. Each table in the schema
 * is a MONGO file in that directory.
 */
public class MongoSchema extends AbstractSchema {
    /**
     * Map<String, SqlTypeName> table = new HashMap<>();
     *         table.put("N_NATIONKEY", SqlTypeName.INTEGER);
     *         table.put("N_NAME", SqlTypeName.VARCHAR);
     *         table.put("N_REGIONKEY", SqlTypeName.INTEGER);
     *         table.put("N_COMMENT", SqlTypeName.VARCHAR);
     *         TABLES.put("NATION", table);
     */
    private static Map<String, Map<String, SqlTypeName>> TABLES = new ConcurrentHashMap<>();

    public static void load(File file) throws Exception{
        ObjectMapper om = new ObjectMapper();
        Map<String, Map<String, String>> tables = om.readValue(file, Map.class);
        for(Map.Entry<String, Map<String, String>> table : tables.entrySet()){
            String name = table.getKey();
            Map<String, String> rowType = table.getValue();

            Map<String, SqlTypeName> tableSchema = new HashMap<>();
            for(Map.Entry<String, String> row : rowType.entrySet()) {
                tableSchema.put(row.getKey(), SqlTypeName.valueOf(row.getValue()));
            }
            TABLES.put(name, tableSchema);
        }
    }
    final MongoDatabase mongoDb;

    /**
     * Creates a MongoDB schema.
     *
     * @param host       Mongo host, e.g. "localhost"
     * @param credential Optional credentials (null for none)
     * @param options    Mongo connection options
     * @param database   Mongo database name, e.g. "foodmart"
     */
    MongoSchema(String host, String database,
                MongoCredential credential, MongoClientOptions options) {
        super();
        try {
            final MongoClient mongo = credential == null
                    ? new MongoClient(new ServerAddress(host), options)
                    : new MongoClient(new ServerAddress(host), credential, options);
            this.mongoDb = mongo.getDatabase(database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows tests to inject their instance of the database.
     *
     * @param mongoDb existing mongo database instance
     */
    @VisibleForTesting
    MongoSchema(MongoDatabase mongoDb) {
        super();
        this.mongoDb = Objects.requireNonNull(mongoDb, "mongoDb");
    }

    @Override
    protected Map<String, Table> getTableMap() {
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
        for (String collectionName : mongoDb.listCollectionNames()) {
            if(TABLES.containsKey(collectionName)){
                builder.put(collectionName, new MongoTable(collectionName, TABLES.get(collectionName)));
            }
        }
        return builder.build();
    }
}
