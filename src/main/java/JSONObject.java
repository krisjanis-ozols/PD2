import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JSONObject {
    private String type;
    private String value;
    private Map<String, JSONObject> children;

    public JSONObject(String type, String value, Map<String, JSONObject> children) {
        this.type = type;
        this.value = value;
        this.children = children;
    }

    public JSONObject(String type, String value) {
        this(type, value, new HashMap<>());
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Map<String, JSONObject> getChildren() {
        if (children == null) {
            throw new IllegalStateException("This node has no children map");
        }
        return children;
    }
    public void addChild(String key, JSONObject child) {
        children.put(key, child);
    }

    public JSONObject getObject(String key) {
        if (children == null) {
            throw new IllegalStateException("This node has no children");
        }
        JSONObject child = children.get(key);
        if (child == null) {
            throw new IllegalArgumentException("Key '" + key + "' does not exist");
        }
        return child;
    }

    public String getString(String key){
        if (children == null) {
            throw new IllegalStateException("This node has no children");
        }
        JSONObject child = this.children.get(key);
        if (child == null) {
            throw new IllegalArgumentException("Key '" + key + "' does not exist");
        }
        return child.value;
    }

    public JSONObject[] getArray(String key){
        if (children == null) {
            throw new IllegalStateException("This node has no children");
        }
        JSONObject child = this.children.get(key);
        if (child == null) {
            throw new IllegalArgumentException("Key '" + key + "' does not exist");
        }
        if (!Objects.equals(child.type, "Array")) {
            throw new ClassCastException("Node '" + key + "' is not an array");
        }
        Map<String,JSONObject> elements = child.getChildren();
        if(elements==null){
            return null;
        }
        JSONObject[] returnArray = new JSONObject[elements.size()];

        for(int i =0;i<elements.size();i++){
            returnArray[i]=elements.get(i+"");
        }
        return returnArray;
    }
    public JSONObject[] getArrayOrObject(String key){
        JSONObject[] array;
        try {
           array = getArray(key);
        }catch(ClassCastException e){
            array = new JSONObject[1];
            array[0]=getObject(key);
        }
        return array;
    }
    public int getInt(String key){
        if (children == null) {
            throw new IllegalStateException("This node has no children");
        }
        JSONObject child = this.children.get(key);
        if (child == null) {
            throw new IllegalArgumentException("Key '" + key + "' does not exist");
        }
        return Integer.parseInt(child.value);
    }


    public static JSONObject parseJSON(List<Token> tokens){
        IntRef currentToken = new IntRef();
        currentToken.value = 0;
        return parseToken(tokens,currentToken);
    }

    public static JSONObject parseToken(List<Token> tokens, IntRef currentToken){
        JSONObject returnObject;
        switch (tokens.get(currentToken.value).getType()) {
            case TokenConstants.STRING:
            case TokenConstants.NUMBER:
            case TokenConstants.TRUE:
            case TokenConstants.FALSE:
                returnObject = new JSONObject(tokens.get(currentToken.value).getType(), tokens.get(currentToken.value).getValue());
                currentToken.value++;
                return returnObject;

            case TokenConstants.LEFT_BRACE:
                return parseObject(tokens, currentToken);

            case TokenConstants.LEFT_BRACKET:
                return parseArray(tokens, currentToken);

            default:
                throw new IllegalStateException("Unexpected token type: " + tokens.get(currentToken.value).getType());
        }
    }
    public static JSONObject parseObject(List<Token> tokens, IntRef currentToken){
        String key;
        String value;
        JSONObject objectNode = new JSONObject("object", null, new HashMap<>());
        //izlaiž {
        currentToken.value++;
        while(!Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.RIGHT_BRACE)){
            if(Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.COMMA)){
                currentToken.value++;
            }
            if(Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.STRING)){
                key=tokens.get(currentToken.value).getValue();
                currentToken.value++;

                if(!Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.COLON)){
                    throw new RuntimeException("colon after key expected");
                }
                currentToken.value++;

                JSONObject valueNode = parseToken(tokens, currentToken);
                objectNode.addChild(key, valueNode);
            }
            if (currentToken.value >= tokens.size()) {
                break;
            }
        }
        currentToken.value++;
        return objectNode;

    }
    public static JSONObject parseArray(List<Token> tokens, IntRef currentToken) {
        JSONObject arrayNode = new JSONObject("Array", null, new HashMap<>());

        currentToken.value++; // izlaiz [
        int i = 0;

        while (!Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.RIGHT_BRACKET)) {


            JSONObject valueNode = parseToken(tokens, currentToken);
            arrayNode.addChild(i + "", valueNode);
            i++;


            if (Objects.equals(tokens.get(currentToken.value).getType(), TokenConstants.COMMA)) {
                currentToken.value++;
            }
        }


        currentToken.value++;

        return arrayNode;
    }
}
