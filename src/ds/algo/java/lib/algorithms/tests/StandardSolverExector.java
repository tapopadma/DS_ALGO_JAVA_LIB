package ds.algo.java.lib.algorithms.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import ds.algo.java.lib.algorithms.StandardAlgoSolver;
import ds.algo.java.lib.io.FastInputReader;

public final class StandardSolverExector {

	public static void solve(StandardAlgoSolver solver, boolean fileMode) {
		// Standard IO
		if(!fileMode) {
			InputStream inputStream = System.in;
	        OutputStream outputStream = System.out;
	        FastInputReader in = new FastInputReader(inputStream);
	        PrintWriter out = new PrintWriter(outputStream);
	        solver.solve(in, out);
	        out.close();			
		}
        // File IO
		else {
			String userHome = System.getProperty("user.home");
	        String IPfilePath = userHome + "/Downloads/ip.in";
	        String OPfilePath = userHome + "/Downloads/op.out";
	        FastInputReader fin = new FastInputReader(IPfilePath);
	        PrintWriter fout = null;
	        try {
				fout = new PrintWriter(new File(OPfilePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        solver.solve(fin, fout);
	        fout.close();
		}
	}
}
