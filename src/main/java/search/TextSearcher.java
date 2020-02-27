package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TextSearcher {

	/**
	 * Initializes the text searcher with the contents of a text file. The current
	 * implementation just reads the contents into a string and passes them to
	 * #init(). You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
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
		List<String> tokens = new ArrayList<String>();
		while (lexer.hasNext()) {
			String s = lexer.next();
			tokens.add(s);
			boolean h = lexer.isWord(s);
			if (!h) {
				int y = 9;
			}
		}
		int x = 0;
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
}

// Any needed utility classes can just go in this file

// POJO that is used store characteristics of each token
class TokenCharacteristics {
	public String OriginalToken;
	public String CleanToken;
	public boolean IsPunctuation;

	public TokenCharacteristics(String originalToken, String cleanToken, boolean isPunctuation) {
		this.OriginalToken = originalToken;
		this.CleanToken = cleanToken;
		this.IsPunctuation = isPunctuation;
	}

}