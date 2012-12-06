import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VInstr.VisitorP;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

public class VM2M {
	public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException
	{
		Op[] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS, Op.PrintIntS, Op.HeapAllocZ, Op.Error};
		boolean allowLocals = false;
		String[] registers = {
		    "v0", "v1",
		    "a0", "a1", "a2", "a3",
		    "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
		    "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
		    "t8"};
		boolean allowStack = true;

		VaporProgram program;
		try {
			program = VaporParser.run(new InputStreamReader(in), 1, 1, java.util.Arrays.asList(ops), allowLocals, registers, allowStack);
			} catch (ProblemException ex) {
				err.println(ex.getMessage());
			    return null;
			}
		
		return program;
	}
	
	public static void main(String args[])
	{
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("./test/Factorial.vaporm");
			PrintStream errorStream = System.err;
			VaporProgram program = parseVapor(inputStream, errorStream);
			
			String data = translateData(program.dataSegments);
			String text = translateText(program.functions);
			
			System.out.print(data);
			System.out.print(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String translateText(VFunction[] functions) {
		
		TextVisitor textVisitor = new TextVisitor();
		String code = "";
		try {
			for (VFunction function : functions)
			{
				code = code + function.ident + ":\n";
				
				for (VInstr instruction : function.body)
				{
					code = code + instruction.accept(null, textVisitor);
				}
				
				code = code + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return code;
	}

	private static String translateData(VDataSegment[] dataSegments) {
		// TODO Auto-generated method stub
		String code = "";
		return code;
	}
}
