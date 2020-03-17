
package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.*;

public class TextSearcher {

    // Similar to a linkedMap however linked is done on map values
    // not the whole entry
    private Map<OccurenceKey, LinkedNode<TokenMeta>> valueLinkedMap;

    /**
     * Initializes the text searcher with the contents of a text file. The current
     * implementation just reads the contents into a string and passes them to
     * #init(). You may modify this implementation if you need to.
     * 
     * @param f Input file.
     * @throws IOException
     */
    public TextSearcher(File f) throws IOException {
        valueLinkedMap = new HashMap<OccurenceKey, LinkedNode<TokenMeta>>();

        FileReader r = new FileReader(f);
        StringWriter w = new StringWriter();
        char[] buf = new char[4096];
        int readCount;

        while ((readCount = r.read(buf)) > 0) {
            w.write(buf, 0, readCount);
        }

        init(w.toString());
        r.close();
    }

    // empty constructor for testing p
    public TextSearcher() {
        valueLinkedMap = new HashMap<OccurenceKey, LinkedNode<TokenMeta>>();
    }

    /**
     * Initializes any internal data structures that are needed for this class to
     * implement search efficiently.
     */
    protected void init(String fileContents) {
        TextTokenizer lexer = new TextTokenizer(fileContents, "[A-Za-z0-9\"']+(\\s*)");
        LinkedNode<TokenMeta> prev = null;
        while (lexer.hasNext()) {
            // link em and feed em
            LinkedNode<TokenMeta> curr = this.createTokenNode(lexer);
            if (prev != null) {
                prev.next = curr;
            }
            curr.prev = prev;
            prev = curr;
            curr.next = null;

            addNode(curr);
        }
    }

    /**
     * 
     * @param queryWord    The word to search for in the file contents.
     * @param contextWords The number of words of context to provide on each side of
     *                     the query word.
     * @return One context string for each time the query word appears in the file.
     */
    public String[] search(String queryWord, int contextWords) {
        queryWord = queryWord.toLowerCase();
        List<LinkedNode<TokenMeta>> occurences = getMatchedNodes(queryWord);
        String[] foundStrings = new String[occurences.size()];
        int i = 0;
        for (LinkedNode<TokenMeta> node : occurences) {
            foundStrings[i] = getContext(node, contextWords);
            i++;
        }

        return foundStrings;
    }

    /**
     * Addds node to hashmap, increments occurence if key already exists we
     * increment the key because it handles the case of having the same word occur
     * in a file multiple times
     * 
     * @param node to add
     */
    public void addNode(LinkedNode<TokenMeta> node) {
        if (node == null) {
            throw new InvalidParameterException("Node cannot be null");
        }

        OccurenceKey key = new OccurenceKey(node.value.cleanToken, 0);
        while (valueLinkedMap.containsKey(key)) { // increment until unique key
            key.incrementOccurence();
        }
        valueLinkedMap.put(key, node);
    }

    /**
     * Gets the occurrences of a word
     * 
     * @param queryWord we are searching for
     * @return a List<LinkedNode<TokenMeta> with each linked node being a node
     *         that's key word matched the queryword
     */
    public List<LinkedNode<TokenMeta>> getMatchedNodes(String queryWord) {
        List<LinkedNode<TokenMeta>> nodes = new ArrayList<>();
        OccurenceKey key = new OccurenceKey(queryWord, 0);
        while (valueLinkedMap.containsKey(key)) { // increment until key is not founc
            nodes.add(valueLinkedMap.get(key));
            key.incrementOccurence();
        }
        return nodes;
    }

    /**
     * Gets the desired context around the node
     * 
     * @return the original string of the node and it's surrounding context
     */
    private String getContext(LinkedNode<TokenMeta> node, int context) {
        String nextContext = nextContext(node.next, context, 0, "");
        String prevContext = prevContext(node.prev, context, 0, "");

        String returnString = prevContext.concat(node.value.originalToken);
        return returnString.concat(nextContext).trim();
    }

    /**
     * Recursive function that increments throrugh next words recursively
     * 
     * @param node
     * @param context
     * @param increments
     * @param string
     * @return
     */
    private String nextContext(LinkedNode<TokenMeta> node, int context, int increments, String string) {
        if (increments >= context || node == null) {
            return "";
        }
        string = node.value.originalToken;
        if (node.value.isWord) {
            return string.concat(nextContext(node.next, context, ++increments, string));
        }
        return string.concat(nextContext(node.next, context, increments, string));
    }

    /**
     * Recursive function that increments throrugh prev words recursively
     * 
     * @param node
     * @param context
     * @param increments
     * @param string
     * @return
     */
    private String prevContext(LinkedNode<TokenMeta> node, int context, int increments, String string) {
        if (increments >= context || node == null) {
            return "";
        }
        string = node.value.originalToken;
        if (node.value.isWord) {
            return prevContext(node.prev, context, ++increments, string).concat(string);
        }
        return prevContext(node.prev, context, increments, string).concat(string);
    }

    /**
     * Creates LinkedNode of TokenMeta data
     * 
     * @param lexer
     * @return
     */
    private LinkedNode<TokenMeta> createTokenNode(TextTokenizer lexer) {
        String originalToken = lexer.next();
        boolean isWord = lexer.isWord(originalToken);

        TokenMeta tokenMeta = new TokenMeta(originalToken, isWord);

        return new LinkedNode<TokenMeta>(tokenMeta);
    }
}

// Any needed utility classes can just go in this file

/**
 * POJO that is used store tokenMeta of each token
 * 
 * @param originalToken Token without any trimming, token after regex match
 * @param cleanToken    Token trimemd and lowercase
 * @param isWord        Is the token a word according the regex pattern
 */
class TokenMeta {
    public String originalToken;
    public String cleanToken;
    public boolean isWord;

    public TokenMeta(String originalToken, boolean isWord) {
        this.originalToken = originalToken;
        this.cleanToken = originalToken.toLowerCase().trim();
        this.isWord = isWord;
    }
}

//
// S
/**
 * Node class that supports linked nodes. Supports point to previous and next
 * 
 * @param <T> Genric can take any value
 */
class LinkedNode<T> {
    public LinkedNode<T> next;
    public LinkedNode<T> prev;
    public T value;

    public LinkedNode(T value) {
        this.value = value;
    }
}

/**
 * OccurenceKey is the class used to decribe a key of the token hashmap
 * 
 * @param word      is the token that is within the file
 * @param occurence is the number of times the word has occured is the file
 *                  occurence start at 0
 */
class OccurenceKey {
    private String word;
    private int occurence;

    public OccurenceKey(String word, int occurence) {
        this.word = word;
        this.occurence = occurence;
    }

    public void incrementOccurence() {
        this.occurence++;
    }

    /**
     * @return the occurence
     */
    public int getOccurence() {
        return occurence;
    }

    /**
     * Hashcode overide. Uses both members to ensure that the keys have the same
     * hashcode
     */
    @Override
    public int hashCode() {

        int hashCode = occurence * 20;
        hashCode += word.hashCode();

        return hashCode;
    }

    /**
     * Override of standard equals Checks to make sure both value types are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) { // if same object
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }

        OccurenceKey key = (OccurenceKey) object;
        if (!this.word.equals(key.word) || this.occurence != key.occurence) {
            return false;
        }
        return true;
    }
}