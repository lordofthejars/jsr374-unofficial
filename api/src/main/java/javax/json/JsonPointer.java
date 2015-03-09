package javax.json;

public class JsonPointer {

    private String pointer;

    public JsonPointer(final String pointer) {
        this.pointer = pointer;
    }

    /**
     * Return the value at the referenced location
     * in the specified {@code JsonValue}
     *
     * @param target the {@code JsonValue} referenced by this {@code JsonPointer}
     *
     * @return the {@code JsonValue} referenced by this {@code JsonPointer}
     */
    public JsonValue getValue(JsonValue target) {

        // First quick checks for well-known 'empty' pointer
        if ((this.pointer == null) || this.pointer.isEmpty()) {
            return target;
        }

        // And then quick validity check:
        if (!pointer.isEmpty() && pointer.charAt(0) != '/') {
            throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': "+"\""+pointer+"\"");
        }

        StringBuilder referenceToken = new StringBuilder();
        for (int i=1; i < this.pointer.length(); i++) {   // 1 to skip first /
            char ch = this.pointer.charAt(i);
            if (ch == '/') {
                JsonPointer jsonPointer = new JsonPointer(this.pointer.substring(i));
                return jsonPointer.getValue(getNextJsonValue(target, referenceToken.toString()));
            } else if (ch == '~') {
                // handle escaping ~0, ~1
                if (i+1 == this.pointer.length()) {
                    throw new IllegalArgumentException("Illegal escaping: expected ~0 or ~1, but got only ~ in pointer="+this.pointer);
                }
                ch = pointer.charAt(++i);
                if (ch == '0') {
                    referenceToken.append('~');
                } else if (ch == '1') {
                    referenceToken.append('/');
                } else {
                    throw new IllegalArgumentException("Illegal escaping: expected ~0 or ~1, but got ~"+ch+" in pointer="+this.pointer);
                }
            } else {
                referenceToken.append(ch);
            }
        }
        return getNextJsonValue(target, referenceToken.toString());
    }

    private JsonValue getNextJsonValue(JsonValue target, String referenceToken) {
        if (target.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonValue value = ((JsonObject)target).get(referenceToken);
            if(value == null) {
                throw new IllegalArgumentException("Illegal reference token="+referenceToken+" for value="+target);
            }
            return value;
        } else if (target.getValueType() == JsonValue.ValueType.ARRAY) {
            return ((JsonArray)target).get(Integer.parseInt(referenceToken));
        } else {
            throw new IllegalArgumentException("Illegal reference token="+referenceToken+" for value="+target);
        }
    }
}
