/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.mongodb;

import com.mongodb.client.MongoCursor;
import io.ballerina.runtime.api.PredefinedTypes;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.internal.JsonDataSource;
import io.ballerina.runtime.internal.JsonGenerator;
import io.ballerina.runtime.internal.JsonParser;
import io.ballerina.runtime.internal.types.BArrayType;
import io.ballerina.runtime.internal.types.BMapType;
import org.bson.Document;

import java.io.IOException;

/**
 * MongoDB result cursor implementation.
 */
public class MongoDBIterator implements JsonDataSource {

    private MongoCursor<Document> mc;

    public MongoDBIterator(MongoCursor<Document> mc) {
        this.mc = mc;
    }

    @Override
    public void serialize(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartArray();
        while (this.hasNext()) {
            jsonGenerator.serialize(this.next());
        }
        jsonGenerator.writeEndArray();
    }

    @Override
    public boolean hasNext() {
        return mc.hasNext();
    }

    @Override
    public Object next() {
        return JsonParser.parse(this.mc.next().toJson());
    }

    @Override
    public Object build() {
        BArray values = ValueCreator
                .createArrayValue(new Object[]{}, new BArrayType(new BMapType(PredefinedTypes.TYPE_MAP)));
        while (this.hasNext()) {
            values.append(this.next());
        }
        return values;
    }
}
