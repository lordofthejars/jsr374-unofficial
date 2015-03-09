package org.glassfish.json.tests;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import junit.framework.TestCase;

public class JsonPointerTest extends TestCase {

    public JsonPointerTest(String testName) {
        super(testName);
    }


    public void testEmptyJsonPointer() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example, result);
    }

    public void testReturnAnArray() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/foo");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonArray("foo"), result);
    }

    public void testReturnArrayIndex() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/foo/0");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonArray("foo").getString(0), ((JsonString)result).getString());
    }

    public void testReturnOutOfBoundsException() throws Exception {

        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/foo/5");

        try {
            JsonValue result = jsonPointer.getValue(rfc6901Example);
            fail("Should have thrown an IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
        }
    }

    public void testExceptionGettingArrayFromNotArrayObject() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/p/1");

        try {
            JsonValue result = jsonPointer.getValue(rfc6901Example);
            fail("Should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }
    }

    public void testRootPath() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber(""), result);
    }

    public void testSlashSubstitution() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/a~1b");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("a/b"), result);
    }
    
    public void testTildeSubstitution() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/m~0n");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("m~n"), result);
    }

    public void testWithPercentage() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/c%d");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("c%d"), result);
    }

    public void testWithCaret() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/e^f");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("e^f"), result);
    }

    public void testWithPipe() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/g|h");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("g|h"), result);
    }

    public void testWithDoubleSlash() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/i\\j");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("i\\j"), result);
    }

    public void testWithQuote() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/k\"l");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber("k\"l"), result);
    }

    public void testWhiteChar() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/ ");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.getJsonNumber(" "), result);
    }

    public void testShouldThrowExceptionIfNodeNotFound() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/notexist");
        try {
            JsonValue result = jsonPointer.getValue(rfc6901Example);
            fail("Should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }
    }

    public void testShouldThrowExceptionIfArrayIsFoundInsteadOfObject() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/s/t");
        try {
            JsonValue result = jsonPointer.getValue(rfc6901Example);
            fail("Should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }
    }
    
    public void testReturningNullValue() throws Exception {
        JsonObject rfc6901Example = JsonPointerTest.readRfc6901Example();
        JsonPointer jsonPointer = new JsonPointer("/o");
        JsonValue result = jsonPointer.getValue(rfc6901Example);
        assertEquals(rfc6901Example.get("o"), result);
    }

    static JsonObject readRfc6901Example() throws Exception {
        Reader rfc6901Reader = new InputStreamReader(JsonReaderTest.class.getResourceAsStream("/rfc6901.json"));
        JsonReader reader = Json.createReader(rfc6901Reader);
        JsonValue value = reader.readObject();
        reader.close();
        return (JsonObject) value;
    }
}
