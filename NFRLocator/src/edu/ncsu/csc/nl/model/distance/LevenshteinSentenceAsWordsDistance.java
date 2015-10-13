package edu.ncsu.csc.nl.model.distance;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.WordVertex;


/**
 * This is an adoption of the levenshtein distance formula, but for words in a sentence instead of text strings
 * 
 * Levenshtein Distance was taken from  http://www.merriampark.com/ld.htm by Michael Gilleland, who placed the source code
 * into the public domain.
 * 
 * @author John
 *
 */
public class LevenshteinSentenceAsWordsDistance extends SentenceDistance {
	
	/**
	 * Returns the smalles of the 3 parameters
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private static double min (double a, double b, double c) {
		double minValue = Math.min(a, b);
		minValue     = Math.min(c, minValue);
		return minValue;
	}

	/**
	 * Computes the Levenshtein Distance between two strings.  This distance, also referred to as the 
	 * edit distance, is the number of deletions, insertions, or substitutions required to transform s into t.
	 * 
	 * This method was changed to be case insensitive.
	 * 
	 * 		Step	Description
			1		Set n to be the length of s.
					Set m to be the length of t.
					If n = 0, return m and exit.
					If m = 0, return n and exit.
					Construct a matrix containing 0..m rows and 0..n columns.
			2	 	Initialize the first row to 0..n.
					Initialize the first column to 0..m.
			3		Examine each character of s (i from 1 to n).
			4		Examine each character of t (j from 1 to m).
			5		If s[i] equals t[j], the cost is 0.
					If s[i] doesn't equal t[j], the cost is 1.
			6		Set cell d[i,j] of the matrix equal to the minimum of:
					a. The cell immediately above plus 1: d[i-1,j] + 1.
					b. The cell immediately to the left plus 1: d[i,j-1] + 1.
					c. The cell diagonally above and to the left plus the cost: d[i-1,j-1] + cost.
			7		After the iteration steps (3, 4, 5, 6) are complete, the distance is found in cell d[n,m].
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	public double computeDistance (Sentence s, Sentence t) {
		double d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		WordVertex s_i; // ith word of s
		WordVertex t_j; // jth word of t
		double cost; // cost
		
		double offset = this.getSentenceOffsetDistance(s, t);  // for just iTrust, I do better ignoring sentence types
		

		// Step 1
		n = s.getNumberOfNodes();
		m = t.getNumberOfNodes();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new double[n+1][m+1];

		// Step 2
		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= n; i++) {
			s_i = s.getWordVertexBySortedPosition(i-1);	

			// Step 4
			for (j = 1; j <= m; j++) {
				t_j = t.getWordVertexBySortedPosition(j-1);

				cost = WordDistance.getNodeDistance(s_i, t_j);

				// Step 6
				d[i][j] = min (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
			}

		}

		// Step 7
		return d[n][m] + offset;
	}
		
	/**
	 * Returns "edit distance via words"
	 */
	public String getMethodName() {
		return "edit distance via words";
	}

}
