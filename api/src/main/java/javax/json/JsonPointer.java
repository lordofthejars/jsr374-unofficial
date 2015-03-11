package javax.json;

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
