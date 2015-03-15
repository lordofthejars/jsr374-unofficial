package org.glassfish.json.tests;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;

import org.junit.Test;

public class JsonPointerOperationsTest {

    @Test
    public void shouldUpdateSimpleObject() {
        JsonObject patch = buildSimpleAddPatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildAddress(), patch.get("value"));
        assertThat(modified, is(buildExpectedAddress()));
    }

    @Test
    public void shouldUpdateComplexObjects() {
        JsonObject patch = buildComplexAddPatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildPerson(), patch.get("value"));
        assertThat(modified, is(buildExpectedPerson()));
    }

    @Test
    public void shouldAddElementAtPosition() {
        JsonObject patch = buildArrayAddPatchInPosition();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildPerson(), patch.get("value"));
        assertThat(modified, is(buildExpectedPersonConcreteArrayPosition()));
    }

    @Test
    public void shouldAddElementAtLastPosition() {
        JsonObject patch = buildArrayAddPatchInLastPosition();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.add(buildPerson(), patch.get("value"));
        assertThat(modified, is(buildExpectedPersonArrayLastPosition()));
    }

    @Test
    public void shouldRemoveSimpleAttribute() {
        JsonObject patch = buildSimpleRemovePatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.remove(buildAddress());
        assertThat(modified, is(buildExpectedRemovedAddress()));
    }
    @Test
    public void shouldRemoveComplexObjects() {
        JsonObject patch = buildComplexRemovePatch();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.remove(buildPerson());
        assertThat(modified, is(buildExpectedPersonWithoutStreetAddress()));
    }
    @Test
    public void shouldRemoveElementAtPosition() {
        JsonObject patch = buildArrayRemovePatchInPosition();
        JsonPointer pointer = new JsonPointer(patch.getString("path"));
        JsonObject modified = (JsonObject) pointer.remove(buildPerson());
        assertThat(modified, is(buildPersonWithoutFirstPhone()));
    }
    static JsonObject buildAddress() {
        return Json.createObjectBuilder()
                .add("streetAddress", "21 2nd Street")
                .add("city", "New York")
                .add("state", "NY")
                .add("postalCode", "10021")
                .build();
    }
    static JsonObject buildComplexRemovePatch() {
        return Json.createObjectBuilder()
                .add("op", "remove")
                .add("path", "/address/streetAddress")
                .build();
    }
    static JsonObject buildSimpleRemovePatch() {
        return Json.createObjectBuilder()
                .add("op", "remove")
                .add("path", "/streetAddress")
                .build();
    }
    static JsonObject buildArrayRemovePatchInPosition() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/phoneNumber/0")
                .build();
    }
    static JsonObject buildComplexAddPatch() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/address/streetAddress")
                .add("value", "myaddress")
                .build();
    }
    static JsonObject buildSimpleAddPatch() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/streetAddress")
                .add("value", "myaddress")
                .build();
    }
    static JsonObject buildArrayAddPatchInPosition() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/phoneNumber/0")
                .add("value", Json.createObjectBuilder()
                        .add("type", "home")
                        .add("number", "200 555-1234"))
                .build();
    }
    static JsonObject buildArrayAddPatchInLastPosition() {
        return Json.createObjectBuilder()
                .add("op", "add")
                .add("path", "/phoneNumber/-")
                .add("value", Json.createObjectBuilder()
                        .add("type", "home")
                        .add("number", "200 555-1234"))
                .build();
    }
    static JsonObject buildExpectedRemovedAddress() {
        return Json.createObjectBuilder()
                .add("city", "New York")
                .add("state", "NY")
                .add("postalCode", "10021")
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
    static JsonObject buildPersonWithoutFirstPhone() {
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
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
    }
    static JsonObject buildExpectedPersonConcreteArrayPosition() {
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
                        .add((Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "200 555-1234")))
                        .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(Json.createObjectBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();
    }
    static JsonObject buildExpectedPersonArrayLastPosition() {
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
                                .add("number", "646 555-4567"))
                         .add(Json.createObjectBuilder()
                                .add("type", "home")
                                .add("number", "200 555-1234")))
                .build();
    }
    static JsonObject buildExpectedPersonWithoutStreetAddress() {
        return Json.createObjectBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", Json.createObjectBuilder()
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
