package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class TextSearcher {

	// might extends this into it's own class.
	// Similar to a linkedMap however linked is down on map values
	// not the whole entry
	private Map<String, LinkedNode<TokenMeta>> valueLinkedMap;

	/**
	 * Initializes the text searcher with the contents of a text file. The current
	 * implementation just reads the contents into a string and passes them to
	 * #init(). You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		valueLinkedMap = new HashMap<String, LinkedNode<TokenMeta>>();

		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;

		while ((readCount = r.read(buf)) > 0) {
			w.write(buf, 0, readCount);
		}

		init(w.toString());
	}

	/**
	 * Initializes any internal data structures that are needed for this class to
	 * implement search efficiently.
	 */
	protected void init(String fileContents) {
		TextTokenizer lexer = new TextTokenizer(fileContents, "[A-Za-z0-9\"']+(\\s*)");

		LinkedNode<TokenMeta> prev = null;
		while (lexer.hasNext()) {
			// link em and feed into hashtable
			LinkedNode<TokenMeta> curr = this.createTokenNode(lexer);
			if (prev != null) {
				prev.Next = curr;
			}
			curr.Prev = prev;
			prev = curr;
			curr.Next = null;

			valueLinkedMap.put(curr.Value.CleanToken, curr);
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
		// TODO -- fill in implementation
		return new String[0];
	}

	private LinkedNode<TokenMeta> createTokenNode(TextTokenizer lexer) {
		String originalToken = lexer.next();
		boolean isWord = lexer.isWord(originalToken);
		String cleanToken = originalToken.toLowerCase();

		TokenMeta tokenMeta = new TokenMeta(originalToken, cleanToken, isWord);

		return new LinkedNode<TokenMeta>(tokenMeta);
	}
}

// Any needed utility classes can just go in this file

// POJO that is used store tokenMeta of each token
class TokenMeta {
	public String OriginalToken;
	public String CleanToken;
	public boolean IsWord;

	public TokenMeta(String originalToken, String cleanToken, boolean isWord) {
		this.OriginalToken = originalToken;
		this.CleanToken = cleanToken;
		this.IsWord = isWord;
	}
}

// Node class that supports linked nodes
// Supports point to previous and next
class LinkedNode<T> {
	public LinkedNode<T> Next;
	public LinkedNode<T> Prev;
	public T Value;

	public LinkedNode(T value) {
		this.Value = value;
	}

	public boolean hasNext() {
		return this.Next != null;
	}

	public boolean hasPrev() {
		return this.Prev != null;
	}
}