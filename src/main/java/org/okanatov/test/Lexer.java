package org.okanatov.test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Lexer implements Iterable<Token> {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final String matchingString;
    private InputStream source;
    private Lexer lexer;

    public Lexer(InputStream source, String matchingString) {
        this(matchingString);
        this.source = source;
    }

    public Lexer(Lexer lexer, String matchingString) {
        this(matchingString);
        this.lexer = lexer;
    }

    public Token readToken() {
        InputStream inputStream;
        if (source != null) {
            inputStream = new DataInputStream(source);
        } else {
            Token source = lexer.readToken();
            if (source == null || source.getType() != 0) return source;
            else inputStream = new ByteArrayInputStream(source.toString().getBytes());
        }

        try {
            readFromStreamAndCheckForMatchingStringInBuffer(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeEmptyTokensFromArray();
        if (!tokens.isEmpty())
            return tokens.remove(0);
        return null;
    }

    @Override
    public Iterator<Token> iterator() {
        return new LexerIterator();
    }

    private Lexer(String matchingString) {
        this.matchingString = matchingString;
    }

    private void readFromStreamAndCheckForMatchingStringInBuffer(InputStream inputStream) throws IOException {
        StringBuilder buffer = new StringBuilder("");
        while (tokens.isEmpty()) {
            int ch;
            if ((ch = inputStream.read()) != -1) {
                buffer.append((char) ch);
                if (buffer.toString().contains(matchingString)) {
                    int idx = buffer.indexOf(matchingString);
                    tokens.add(new Token(buffer.substring(0, idx), 0));
                    tokens.add(new Token("[" + matchingString + "]", 1));
                }
            }
            if (ch == -1)
                tokens.add(new Token(buffer.toString(), 0));
        }
    }

    private void removeEmptyTokensFromArray() {
        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token object = iterator.next();
            String text = object.toString();
            if (text.equals("") || text.equals(" "))
                iterator.remove();
        }
    }

    private class LexerIterator implements Iterator<Token> {

        private Token token = null;

        @Override
        public boolean hasNext() {
            return ((token = Lexer.this.readToken()) != null);
        }

        @Override
        public Token next() {
            return token;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported by Lexer");
        }
    }
}
