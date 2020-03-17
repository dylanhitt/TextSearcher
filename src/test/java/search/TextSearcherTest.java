package search;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/** Unit tests for TextSearcher. Don't modify this file. */
public class TextSearcherTest {

	public static void assertArraysEqual(Object[] expected, Object[] actual) {
		// check size first, then contents:
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	/** Simplest possible case, no context and the word occurs exactly once. */
	@Test
	public void testOneHitNoContext() throws Exception {

		String[] expected = { "sketch" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("sketch", 0);
		assertArraysEqual(expected, results);
	}

	/** Next simplest case, no context and multiple hits. */
	@Test
	public void testMultipleHitsNoContext() throws Exception {
		String[] expected = { "naturalists", "naturalists" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("naturalists", 0);
		assertArraysEqual(expected, results);
	}

	/** This is the example from the document. */
	@Test
	public void testBasicSearch() throws Exception {
		String[] expected = { "great majority of naturalists believed that species",
				"authors.  Some few naturalists, on the other" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("naturalists", 3);
		assertArraysEqual(expected, results);
	}

	/** Same as basic search but a little more context. */
	@Test
	public void testBasicMoreContext() throws Exception {
		String[] expected = {
				"Until recently the great majority of naturalists believed that species were immutable productions",
				"maintained by many authors.  Some few naturalists, on the other hand, have believed" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("naturalists", 6);
		assertArraysEqual(expected, results);
	}

	/** Tests query word with apostrophe. */
	@Test
	public void testApostropheQuery() throws Exception {
		String[] expected = { "not indeed to the animal's or plant's own good",
				"habitually speak of an animal's organisation as\r\nsomething plastic" };

		TextSearcher searcher = new TextSearcher(getFile("long_excerpt.txt"));
		String[] results = searcher.search("animal's", 4);
		assertArraysEqual(expected, results);
	}

	/** Tests numeric query word. */
	@Test
	public void testNumericQuery() throws Exception {
		String[] expected = { "enlarged in 1844 into a", "sketch of 1844--honoured me" };
		TextSearcher searcher = new TextSearcher(getFile("long_excerpt.txt"));
		String[] results = searcher.search("1844", 2);
		assertArraysEqual(expected, results);
	}

	/** Tests mixed alphanumeric query word. */
	@Test
	public void testMixedQuery() throws Exception {
		String[] expected = { "date first edition [xxxxx10x.xxx] please check" };

		TextSearcher searcher = new TextSearcher(getFile("long_excerpt.txt"));
		String[] results = searcher.search("xxxxx10x", 3);
		assertArraysEqual(expected, results);
	}

	/** Should get same results regardless of case. */
	@Test
	public void testCaseInsensitiveSearch() throws Exception {
		String[] expected = { "on the Origin of Species.  Until recently the great",
				"of naturalists believed that species were immutable productions, and",
				"hand, have believed that species undergo modification, and that" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("species", 4);
		assertArraysEqual(expected, results);

		results = searcher.search("SPECIES", 4);
		assertArraysEqual(expected, results);

		results = searcher.search("SpEcIeS", 4);
		assertArraysEqual(expected, results);
	}

	/** Hit that overlaps file start should still work. */
	@Test
	public void testNearBeginning() throws Exception {
		String[] expected = { "I will here give a brief sketch" };

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("here", 4);
		assertArraysEqual(expected, results);
	}

	/** Hit that overlaps file end should still work. */
	@Test
	public void testNearEnd() throws Exception {
		String[] expected = { "and that the existing forms of life", "generation of pre existing forms." };
		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("existing", 3);
		assertArraysEqual(expected, results);
	}

	/** Searcher can execute multiple searches after initialization. */
	@Test
	public void testMultipleSearches() throws Exception {

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] expected;
		String[] results;

		// Just runs the same queries as other tests, but on a single TextSearcher
		// instance:
		expected = new String[] { "on the Origin of Species.  Until recently the great",
				"of naturalists believed that species were immutable productions, and",
				"hand, have believed that species undergo modification, and that" };
		results = searcher.search("species", 4);
		assertArraysEqual(expected, results);

		expected = new String[] { "I will here give a brief sketch" };
		results = searcher.search("here", 4);
		assertArraysEqual(expected, results);

		expected = new String[] { "and that the existing forms of life", "generation of pre existing forms." };
		results = searcher.search("existing", 3);
		assertArraysEqual(expected, results);
	}

	/** Overlapping hits should just come back as separate hits. */
	@Test
	public void testOverlappingHits() throws Exception {
		String[] expected = { "of naturalists believed that species were immutable",
				"hand, have believed that species undergo modification",
				"undergo modification, and that the existing forms",

		};

		TextSearcher searcher = new TextSearcher(getFile("short_excerpt.txt"));
		String[] results = searcher.search("that", 3);
		assertArraysEqual(expected, results);
	}

	/** If no hits, get back an empty array. */
	@Test
	public void testNoHits() throws Exception {

		TextSearcher searcher = new TextSearcher(getFile("long_excerpt.txt"));
		String[] results = searcher.search("slejrlskejrlkajlsklejrlksjekl", 3);
		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.length);
	}

	/** Verify the tokenizer. This should always pass. */
	@Test
	public void testTokenizer() throws Exception {
		String input = "123, 789: def";
		// In this test we define words to be strings of digits
		String[] expected = { "123", ", ", "789", ": def" };
		TextTokenizer lexer = new TextTokenizer(input, "[0-9]+");
		List<String> tokens = new ArrayList<String>();
		while (lexer.hasNext()) {
			tokens.add(lexer.next());
		}
		String[] results = (String[]) tokens.toArray(new String[tokens.size()]);
		assertArraysEqual(expected, results);

		Assert.assertTrue(lexer.isWord("1029384"));
		Assert.assertFalse(lexer.isWord("1029388 "));
		Assert.assertFalse(lexer.isWord("123,456"));
	}

	// My tests -- typically would be going in another file

	@Test
	public void testHashCode() throws Exception {

		OccurenceKey key1 = new OccurenceKey("give", 0);
		OccurenceKey key2 = new OccurenceKey("give", 0);

		assertEquals(key1.hashCode(), key2.hashCode());
	}

	@Test
	public void testAddNode() throws Exception {
		TextSearcher searcher = new TextSearcher();
		LinkedNode<TokenMeta> tNode = CreateTestNode("hello", true);
		searcher.addNode(tNode);

		assertEquals(1, searcher.getMatchedNodes("hello").size());
	}

	@Test
	public void testGettingContext() throws Exception {
		TextSearcher searcher = new TextSearcher();

		LinkedNode<TokenMeta> node1 = CreateTestNode("Hello", true);
		LinkedNode<TokenMeta> node2 = CreateTestNode(", ", false);
		node1.next = node2;
		node2.prev = node1;
		LinkedNode<TokenMeta> node3 = CreateTestNode("great ", true);
		node2.next = node3;
		node3.prev = node2;
		LinkedNode<TokenMeta> node4 = CreateTestNode("world", true);
		node3.next = node4;
		node4.prev = node3;

		searcher.addNode(node1);
		searcher.addNode(node2);
		searcher.addNode(node3);
		searcher.addNode(node4);

	}

	private LinkedNode<TokenMeta> CreateTestNode(String token, boolean isWord) {
		TokenMeta tokenMeta = new TokenMeta(token, isWord);
		return new LinkedNode<TokenMeta>(tokenMeta);
	}

	private File getFile(String file) {
		ClassLoader classLoader = getClass().getClassLoader();

		return new File(classLoader.getResource(file).getFile());
	}

}
