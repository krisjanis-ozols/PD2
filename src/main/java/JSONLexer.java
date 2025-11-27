import java.util.ArrayList;
import java.util.List;

public class JSONLexer {
    public static List<Token> lex(String input){
        List<Token> tokens = new ArrayList<>();
        char character;
        StringBuilder stringElement;
        StringBuilder numberElement;

        for(int currentPosition=0; currentPosition<input.length(); currentPosition++){
            character=input.charAt(currentPosition);
            switch (character) {
                case '{':
                    tokens.add(new Token(TokenConstants.LEFT_BRACE, character+""));
                    break;
                case '}':
                    tokens.add(new Token(TokenConstants.RIGHT_BRACE, character+""));
                    break;
                case '[':
                    tokens.add(new Token(TokenConstants.LEFT_BRACKET, character+""));
                    break;
                case ']':
                    tokens.add(new Token(TokenConstants.RIGHT_BRACKET, character+""));
                    break;
                case ':':
                    tokens.add(new Token(TokenConstants.COLON, character+""));
                    break;
                case ',':
                    tokens.add(new Token(TokenConstants.COMMA, character+""));
                    break;
                case '"':
                    stringElement = new StringBuilder();
                    currentPosition++;
                    while(input.charAt(currentPosition)!='"'){
                        stringElement.append(input.charAt(currentPosition));
                        currentPosition++;
                    }

                    tokens.add(new Token(TokenConstants.STRING, stringElement+""));
                    break;
                default:
                    if (Character.isWhitespace(character)) {
                        break;
                    }
                    else if (Character.isDigit(character)) {
                        numberElement = new StringBuilder();
                        while (Character.isDigit(input.charAt(currentPosition))){
                            numberElement.append(input.charAt(currentPosition));
                            currentPosition++;
                        }
                        currentPosition--;
                        tokens.add(new Token(TokenConstants.NUMBER, numberElement+""));
                        break;
                    }
                    else if (input.startsWith("true", currentPosition)) {
                        tokens.add(new Token(TokenConstants.TRUE, "true"));
                        currentPosition += 3;
                        break;
                    }
                    else if (input.startsWith("false", currentPosition)) {
                        tokens.add(new Token(TokenConstants.FALSE, "false"));
                        currentPosition += 4;
                        break;
                    }
                    else if (input.startsWith("null", currentPosition)) {
                        tokens.add(new Token(TokenConstants.NULL, "null"));
                        currentPosition += 3;
                        break;
                    }
                    else {
                        throw new RuntimeException("Unknown character: " + character);
                    }
            }
        }
        return tokens;
    }

}