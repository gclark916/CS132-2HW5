import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VOperand;
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
	
	public static void main2(String args[])
	{
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("./test/Factorial.opt.vaporm");
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
	
	public static void main(String args[])
	{
		InputStream inputStream;
		try {
			inputStream = System.in;
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
		Input input = new Input(new HashMap<String, Integer>());
		String code = ".text\n\n  jal Main\n  li $v0 10\n  syscall\n\n";
		try {
			for (VFunction function : functions)
			{
				String functionLabel = function.ident + ":\n";
				String storeFramePointer = "  sw $fp -8($sp)\n";
				String setNewFramePointer = "  move $fp $sp\n";
				String allocateMoreStack = "  subu $sp $sp " + Integer.toString(8 + 4 * (function.stack.local + function.stack.out)) + "\n";
				String storeReturnAddr = "  sw $ra -4($fp)\n";
				
				code = code + functionLabel + storeFramePointer + setNewFramePointer + allocateMoreStack + storeReturnAddr;
				
				int labelIndex = 0;
				for (VInstr instruction : function.body)
				{
					while (labelIndex < function.labels.length && instruction.sourcePos.line > function.labels[labelIndex].sourcePos.line)
					{
						code = code + function.labels[labelIndex].ident + ":\n";
						labelIndex++;
					}
					
					if (instruction == function.body[function.body.length-1])
					{
						String loadReturnAddress = "  lw $ra -4($fp)\n";
						String loadFramePointer = "  lw $fp -8($fp)\n";
						String deallocateStack = "  addu $sp $sp " + Integer.toString(8 + 4 * (function.stack.local + function.stack.out)) + "\n";
						code = code + loadReturnAddress + loadFramePointer + deallocateStack;
					}
					code = code + instruction.accept(input, textVisitor);
				}
				
				code = code + "\n";
			}
			
			code = code + printFunc + errorFunc + heapAllocFunc + finalData;
			for (String string : input.strings.keySet())
			{
				code = code + "_str" + input.strings.get(string).toString() + ": .asciiz " + string.substring(0, string.length()-1) + "\\n\"\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return code;
	}

	private static String translateData(VDataSegment[] dataSegments) {
		String code = ".data\n\n";
		for (VDataSegment dataSegment : dataSegments)
		{
			code = code + dataSegment.ident + ":\n";
			for (VOperand.Static value : dataSegment.values)
			{
				code = code + "  " + value.toString().substring(1) + "\n";
			}
			
			code = code + "\n";
		}
		return code;
	}
	
	static String printFunc = 
			"_print:\n" +
			"  li $v0 1   # syscall: print integer\n" +
			"  syscall\n" +
			"  la $a0 _newline\n" +
			"  li $v0 4   # syscall: print string\n" +
		    "  syscall\n" +
			"  jr $ra\n\n";
	
	static String errorFunc = 
			"_error:\n" +
			"  li $v0 4   # syscall: print string\n" +
			"  syscall\n" +
			"  li $v0 10  # syscall: exit\n" +
			"  syscall\n\n";
	
	static String heapAllocFunc = 
			"_heapAlloc:\n" +
			"  li $v0 9   # syscall: sbrk\n" +
			"  syscall\n" +
			"  jr $ra\n\n";
	
	static String finalData = 
			".data\n" +
			".align 0\n" +
			"_newline: .asciiz \"\\n\"\n";
}
