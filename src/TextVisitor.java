import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr.VisitorPR;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRef.Stack;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;


public class TextVisitor extends VisitorPR<Object, Object, Exception> {

	@Override
	public Object visit(Object p, VAssign a) throws Exception {
		String code = "";
		if (a.source.toString().startsWith("$"))
			code = "  move " + a.dest.toString() + " " + a.source.toString() + "\n";
		else if (a.source.toString().startsWith(":"))
			code = "  la " + a.dest.toString() + " " + a.source.toString().substring(1) + "\n";
		else
			code = "  li " + a.dest.toString() + " " + a.source.toString() + "\n";
		return code;
	}

	@Override
	public Object visit(Object p, VCall c) throws Exception {
		String call = "";
		if (c.addr.toString().startsWith(":"))
			call = "  jal " + c.addr.toString().substring(1) + "\n";
		else
			call = "  jalr " + c.addr.toString() + "\n";
		return call;
	}

	@Override
	public Object visit(Object p, VBuiltIn c) throws Exception {
		String callFunction = "";
		String code = "";
		
		String setArgument = "";
		if (c.args[0].toString().startsWith("$"))
			setArgument = "  move $a0 " + c.args[0].toString() + "\n";
		else
			setArgument = "  li $a0 " + c.args[0].toString() + "\n";
		
		switch (c.op.name)
		{
		case "Add":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  addu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String add = "  addu " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + add;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String add = "  addu " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + add;
			}
			else
				code = "  li " + c.dest + " " + String.valueOf((Integer.valueOf(c.args[0].toString()) + Integer.valueOf(c.args[1].toString()))) + "\n";
			break;
		case "Sub":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  subu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String subtract = "  subu " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + subtract;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String subtract = "  subu " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + subtract;
			}
			else
				code = "  li " + c.dest + " " + String.valueOf((Integer.valueOf(c.args[0].toString()) - Integer.valueOf(c.args[1].toString()))) + "\n";
			break;
		case "MulS":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  mul " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String multiply = "  mul " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + multiply;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String multiply = "  mul " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + multiply;
			}
			else
				code = "  li " + c.dest + " " + String.valueOf((Integer.valueOf(c.args[0].toString()) * Integer.valueOf(c.args[1].toString()))) + "\n";
			break;
		case "Eq":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
				{
					String xor = "  xor $t9 " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
					String sltiu = "  sltiu " + c.dest + " $t9 1\n";
					code = xor + sltiu;
				}
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String xor = "  xor $t9 " + c.args[0].toString() + " $t9\n";
					String sltiu = "  sltiu " + c.dest + " $t9 1\n";
					code = loadImmediate + xor + sltiu;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String xor = "  xor $t9 $t9 " + c.args[1].toString() + "\n"; 
				String sltiu = "  sltiu " + c.dest + " $t9 1\n";
				code = loadImmediate + xor + sltiu;
			}
			else
				code = "  li " + c.dest + " " + (c.args[0].toString().equals(c.args[1].toString()) ? "1" : "0") + "\n";
			break;
		case "Lt":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  sltu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String setLessThanUnsigned = "  sltu " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + setLessThanUnsigned;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String setLessThanUnsigned = "  sltu " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + setLessThanUnsigned;
			}
			else
				code = "  li " + c.dest + " " + (Long.valueOf(c.args[0].toString()) < Long.valueOf(c.args[1].toString()) ? "1" : "0") + "\n";
			break;
		case "LtS":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  slt " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String setLessThan = "  slt " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + setLessThan;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String setLessThan = "  slt " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + setLessThan;
			}
			else
				code = "  li " + c.dest + " " + (Integer.valueOf(c.args[0].toString()) < Integer.valueOf(c.args[1].toString()) ? "1" : "0") + "\n";
			break;
		case "PrintIntS":
			callFunction = "  jal _print\n";
			code = setArgument + callFunction;
			break;
		case "HeapAllocZ":
			callFunction = "  jal _heapAlloc\n";
			String moveReturnValue = "  move " + c.dest.toString() + " $v0\n";
			code = setArgument + callFunction + moveReturnValue;
			break;
		case "Error":
			Input input = (Input) p;
			int stringIndex;
			if (input.strings.containsKey(c.args[0].toString()))
			{
				stringIndex = input.strings.get(c.args[0].toString());
			}
			else
			{
				stringIndex = input.strings.size();
				input.strings.put(c.args[0].toString(), input.strings.size());
			}
			String loadAddr = "  la $a0 _str" + Integer.toString(stringIndex) + "\n";
			callFunction = "  j _error\n";
			code = loadAddr + callFunction;
			break;
		default:
			throw(new Exception("bad op name at line " + c.sourcePos.line + " col " + c.sourcePos.column));
		}
		
		return code;
	}

	@Override
	public Object visit(Object p, VMemWrite w) throws Exception {
		String code = "";
		String setSourceRegister = "";
		String saveWord = "";
		String sourceRegister = "";
		if (w.source.toString().startsWith(":"))
		{
			setSourceRegister = "  la $t9 " + w.source.toString().substring(1) + "\n";
			sourceRegister = "$t9";
		}
		else if (w.source.toString().startsWith("$"))
			sourceRegister = w.source.toString();
		else
		{
			setSourceRegister = "  li $t9 " + w.source.toString() + "\n";
			sourceRegister = "$t9";
		}
		
		if (Stack.class.isInstance(w.dest))
		{
			//TODO: may have to edit index depending on stack region
			Stack dest = (Stack) w.dest;
			switch (dest.region)
			{
			case Out:
				saveWord = "  sw " + sourceRegister + " " + Integer.toString(dest.index * 4) + "($sp)\n";
				break;
			case Local:
				Input input = (Input) p;
				saveWord = "  sw " + sourceRegister + " " + Integer.toString((dest.index + input.outArgCount) * 4) + "($sp)\n";
				break;
			default:
				throw(new Exception("trying to sw into 'in' region of stack"));
			}
			
		}
		else if (Global.class.isInstance(w.dest))
		{
			Global dest = (Global) w.dest;
			saveWord = "  sw " + sourceRegister + " " +  Integer.toString(dest.byteOffset) + "(" + dest.base + ")\n";
		}
		
		code = setSourceRegister + saveWord;
		return code;
	}

	@Override
	public Object visit(Object p, VMemRead r) throws Exception {
		String code = "";
		if (Global.class.isInstance(r.source))
		{
			Global source = (Global) r.source;
			code = "  lw " + r.dest + " " + Integer.toString(source.byteOffset) + "(" + source.base + ")\n";
		}
		else if (Stack.class.isInstance(r.source))
		{
			Stack source = (Stack) r.source;
			switch (source.region)
			{
			case In:
				code = "  lw " + r.dest + " " + Integer.toString(source.index * 4) + "($fp)\n";
				break;
			case Local:
				Input input = (Input) p;
				code = "  lw " + r.dest + " " + Integer.toString((source.index + input.outArgCount) * 4) + "($sp)\n";
				break;
			default:
				throw(new Exception("trying to lw from 'out' region of stack"));	
			}
		}
		else
			code = "  lw " + r.dest + " (" + r.source.toString() + ")\n";
		return code;
	}

	@Override
	public Object visit(Object p, VBranch b) throws Exception {
		String code = "";
		if (b.positive)
			code = "  bnez " + b.value.toString() + " " + b.target.ident + "\n";
		else
			code = "  beqz " + b.value.toString() + " " + b.target.ident + "\n";
		return code;
	}

	@Override
	public Object visit(Object p, VGoto g) throws Exception {
		String code = "  j " + g.target.toString().substring(1) + "\n";
		return code;
	}

	@Override
	public Object visit(Object p, VReturn r) throws Exception {
		String moveReturnValue = "";
		if (r.value != null)
			moveReturnValue = "  move $v0 " + r.value.toString() + "\n";
		String ret = "  jr $ra\n";
		String code = moveReturnValue + ret;
		return code;
	}

}
