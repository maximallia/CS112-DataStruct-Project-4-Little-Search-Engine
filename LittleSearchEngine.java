package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap< String, Occurrence > kws = new HashMap< String, Occurrence > () ;
		Scanner scan = new Scanner( new File( docFile ) ) ;
		while( scan.hasNext() ) {
			String word = scan.next() ;
			word = getKeyword( word ) ;
			if( word != null ) {
				if( kws.containsKey( word ) ) {
					kws.get( word ).frequency++ ;
				} else {
					kws.put(word, new Occurrence( docFile, 1 ) ) ;
				}
			}
		}
		scan.close() ;
		return kws ;
	}
		
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for( String kword : kws.keySet() ) {
			if( keywordsIndex.containsKey( kword ) ) {
				ArrayList< Occurrence > oList = keywordsIndex.get( kword ) ;
				oList.add( kws.get( kword ) ) ;
				insertLastOccurrence( oList ) ;
			} else {
				ArrayList< Occurrence > oList = new ArrayList< Occurrence > () ;
				oList.add( kws.get( kword ) ) ;
				keywordsIndex.put( kword, oList ) ;
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	
	private String chars = ".,?:;!'\\-\"" ;
	
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		word = word.toLowerCase() ; 
		word = word.replaceAll("[" + chars + "]+$|^[" + chars + "]+", "" ) ;
		
		if( word.matches( ".*[" + chars + "]+.*") || word.length() == 0 ) {
			word = null ;
		}
		if( word != null ) {
			for( String nWord : noiseWords ) {
				if( nWord.equals(word) ) {
					word = null ;
				}
			}
		}
		
		return word ;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	
	
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList< Integer > indexes = new ArrayList< Integer > () ;
		Occurrence last = occs.get(occs.size() - 1 ) ;
		
		int start = 0 ;
		int end = occs.size() - 2 ;
		
		while( start < end ) {
			int mid = (start + end ) / 2 ;
			Occurrence oList = occs.get( mid ) ;
			if( oList.frequency > last.frequency ) {
				start = mid + 1 ;
			} else {
				end = mid ;
			}
		}
		occs.add(start, last ) ;
		return indexes ;
		
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	
	private void addOccurrence( ArrayList< Occurrence > occs, Occurrence oList ) {
		int i;
		int len = occs.size();
		for( i = 0; i < len; i++ ) {
			Occurrence nOcc = occs.get( i ) ;
			if( nOcc.document.contentEquals( oList.document ) ) {
				nOcc.frequency += oList.frequency ;
				break ;
			}
		}
		if( i >= len ) {
			occs.add( new Occurrence( oList.document, oList.frequency ) ) ;
		}
	}
	
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList< Occurrence > occs = new ArrayList< Occurrence > () ;
		ArrayList< Occurrence > occsKW1 = keywordsIndex.containsKey( kw1 ) ? keywordsIndex.get( kw1 ) : null ;
		ArrayList< Occurrence > occsKW2 = keywordsIndex.containsKey( kw2 ) ? keywordsIndex.get( kw2 ) : null ;

		if( occsKW1 != null ) {
			for( Occurrence oList : occsKW1 ) {
				addOccurrence( occs, oList ) ;
			}
		}
		if( occsKW2 != null ) {
			for( Occurrence oList: occsKW2 ) {
				addOccurrence( occs, oList ) ;
			}
		}
		occs.sort( new Comparator< Occurrence >() {
			@Override
			public int compare( Occurrence oList1, Occurrence oList2 ) {
				return oList2.frequency - oList1.frequency ;
			}
		}) ;
		
		ArrayList< String > docs = new ArrayList< String > () ;
		for( Occurrence oList : occs ) docs.add( oList.document ) ;
		return docs ;
	}
		
	public static void main(String[] args) {
        LittleSearchEngine lse = new LittleSearchEngine();
        String docsFile = "docs.txt";
        String noiseWordsFile = "noisewords.txt";
        try {
            lse.makeIndex(docsFile, noiseWordsFile);

            ArrayList<String> result = lse.top5search("deep", "world");
            for (String s : result) System.out.println(s);

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
		
}
