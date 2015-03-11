package org.glassfish.json.tests;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;

import org.junit.Test;

public class JsonPointerAddOperationTest {

    @Test
    public void shouldUpdateSimpleObject() {
        JsonObject patch = buildSimplePatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildAddress(), patch.get("value"));
        assertThat(modified, is(buildExpectedAddress()));
    }

    @Test
    public void shouldUpdateComplexObjects() {
        JsonObject patch = buildComplexPatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildPerson(), patch.get("value"));
        assertThat(modified, is(buildExpectedPerson()));
    }
    
    static JsonObject buildAddress() {
        return Json.createObjectBuilder()
                .add("streetAddress", "21 2nd Street")
                .add("city", "New York")
                .add("state", "NY")
                .add("postalCode", "10021")
                .build();
    }
    
    static JsonObject buildComplexPatch() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/address/streetAddress")
                .add("value", "myaddress")
                .build();
    }
    static JsonObject buildSimplePatch() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/streetAddress")
                .add("value", "myaddress")
                .build();
    }
    static JsonObject buildExpectedAddress() {
        return Json.createObjectBuilder()
                .add("streetAddress", "myaddress")
                .add("city", "New York")
                .add("state", "NY")
                .add("postalCode", "10021")
                .build();
    }
    static JsonObject buildPerson() {
        return Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", Json.createObjectBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(Json.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
    }
    static JsonObject buildExpectedPerson() {
        return Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", Json.createObjectBuilder()
                        .add("streetAddress", "myaddress")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(Json.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
    }
}
