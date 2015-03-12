package javax.json;

import javax.json.JsonValue.ValueType;

public class JsonPointer {

    private String pointer;
    private String[] tokens;

    public JsonPointer(final String pointer) {
        this.pointer = pointer;

        tokens = pointer.split("/", -1); // keep the trailing blanks
        if (!"".equals(tokens[0])) {
            throw new JsonException(
                    "A non-empty JSON pointer must begin with a '/'");
        }
        for (int i = 1; i < tokens.length; i++) { // start from 1
            tokens[i] = escapeChars(tokens[i]);
        }
    }

    private String escapeChars(String token) {

        StringBuilder escapedReferenceToken = new StringBuilder();

        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (ch == '~') {
                if (i + 1 == token.length()) {
                    escapedReferenceToken.append(ch);
                } else {
                    char index = token.charAt(i + 1);
                    if (index == '0') {
                        escapedReferenceToken.append("~");
                        i++;
                    } else if (index == '1') {
                        escapedReferenceToken.append("/");
                        i++;
                    } else {
                        escapedReferenceToken.append(ch);
                    }
                }
            } else {
                escapedReferenceToken.append(ch);
            }
        }
        return escapedReferenceToken.toString();
    }

    /**
     * Add or replace a value at the referenced location in the specified
     * {@code JsonStructure}.
     * <ol>
     * <li>If the reference is the target {@code JsonStructure}, the value,
     * which must be the same type as the target, is returned.</li>
     * <li>If the reference is an index to an array, the value is inserted into
     * the array at the index. If the index is specified with a "-", or if the
     * index is equal to the size of the array, the value is appended to the
     * array.</li>
     * <li>If the reference is a name of a {@code JsonObject}, and the
     * referenced value exists, the value is replaced by the specified value. If
     * it does not exists, a new name/value pair is added to the object.</li>
     * </ol>
     *
     * @param target
     *            the target {@code JsonStructure}
     * @param value
     *            the value to be added
     * @return the resultant JsonStructure
     * @throws IndexOutOfBoundsException
     *             if the index to the array is out of range
     */
    public JsonValue add(JsonStructure target, JsonValue value) {
        JsonValue modifiedNode = traverseAndCopyJsonObject(target, 1, value);
        return modifiedNode;
    }

    private JsonValue traverseAndCopyJsonObject(JsonValue current, int currentTokenIndex, JsonValue value) {
        //trivial case
        if(currentTokenIndex == tokens.length-1) {

            if (current.getValueType() == ValueType.OBJECT) {

                JsonObjectBuilder objectBuilder = Json
                    .createObjectBuilder((JsonObject) current);
            objectBuilder.add(tokens[tokens.length-1], value);
            return objectBuilder.build();

            } else if(current.getValueType() == ValueType.ARRAY) {

                JsonArray currentArray = (JsonArray) current;
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(currentArray);
                int position = getPositionToInsert(currentArray);
                arrayBuilder.add(position, value);
                return arrayBuilder.build();

            } else {
                throw new IllegalArgumentException("Illegal reference token="
                        + tokens[currentTokenIndex] + " for value=" + value);
            }
        } else {
            //not trivial case probably it can be refactored to be pretty similar to trivial case
            JsonValue copiedObject = traverseAndCopyJsonObject(getNextJsonValue(current, tokens[currentTokenIndex]), currentTokenIndex + 1, value);

            if(current.getValueType() == ValueType.OBJECT) {

                return Json.createObjectBuilder((JsonObject)current).add(tokens[currentTokenIndex], copiedObject).build();

            } else if (current.getValueType() == ValueType.ARRAY) {

                JsonArray currentArray = (JsonArray) current;
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(currentArray);
                int position = getPositionToInsert(currentArray);
                arrayBuilder.add(position, copiedObject);
                return arrayBuilder.build();

            } else {
                throw new IllegalArgumentException("Illegal reference token="
                        + tokens[currentTokenIndex] + " for value=" + value);
            }
        }
    }

    private int getPositionToInsert(JsonArray currentArray) {
        String positionString = tokens[tokens.length-1];
        int position = 0;
        if("-".equals(positionString)) {
            position = currentArray.size();
        } else {
            position = Integer.parseInt(positionString);
        }
        return position;
    }
    
    public JsonValue getValue(JsonValue target) {

        if ((this.pointer == null) || this.pointer.isEmpty()) {
            return target;
        }

        JsonValue current = target;
        for (int i = 1; i < tokens.length; i++) {
            current = getNextJsonValue(current, tokens[i]);
        }
        if (current == null) {
            throw new IllegalArgumentException(
                    "No value for reference pointer=" + this.pointer
                            + " for value=" + target);
        }
        return current;
    }

    private JsonValue getNextJsonValue(JsonValue target, String referenceToken) {
        if (target.getValueType() == JsonValue.ValueType.OBJECT) {
            return ((JsonObject) target).get(referenceToken);
        } else if (target.getValueType() == JsonValue.ValueType.ARRAY) {
            return ((JsonArray) target).get(Integer.parseInt(referenceToken));
        } else {
            throw new IllegalArgumentException("Illegal reference token="
                    + referenceToken + " for value=" + target);
        }
    }

    @Override
    public String toString() {
        return "JsonPointer [pointer=" + pointer + "]";
    }
}
