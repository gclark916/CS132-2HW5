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
		else
			code = "  li " + a.dest.toString() + " " + a.source.toString() + "\n";
		return code;
	}

	@Override
	public Object visit(Object p, VCall c) throws Exception {
		String call = "  jalr " + c.addr.toString() + "\n";
		return call;
	}

	@Override
	public Object visit(Object p, VBuiltIn c) throws Exception {
		String callFunction = "";
		String code = "";
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
				code = "  li " + c.dest + String.valueOf((Integer.valueOf(c.args[0].toString()) + Integer.valueOf(c.args[1].toString()))) + "\n";
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
				code = "  li " + c.dest + String.valueOf((Integer.valueOf(c.args[0].toString()) - Integer.valueOf(c.args[1].toString()))) + "\n";
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
				code = "  li " + c.dest + String.valueOf((Integer.valueOf(c.args[0].toString()) * Integer.valueOf(c.args[1].toString()))) + "\n";
			break;
		case "Eq":
			if (c.args[0].toString().startsWith("$"))
			{
				if (c.args[1].toString().startsWith("$"))
					code = "  seq " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
				else
				{
					String loadImmediate = "  li $t9 " + c.args[1].toString() + "\n";
					String setEqual = "  seq " + c.dest + " " + c.args[0].toString() + " $t9\n";
					code = loadImmediate + setEqual;
				}
			}
			else if (c.args[1].toString().startsWith("$"))
			{
				String loadImmediate = "  li $t9 " + c.args[0].toString() + "\n";
				String setEqual = "  seq " + c.dest + " $t9 " + c.args[1].toString() + "\n";
				code = loadImmediate + setEqual;
			}
			else
				code = "  li " + c.dest + (c.args[0].toString().equals(c.args[1].toString()) ? "1" : "0") + "\n";
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
				code = "  li " + c.dest + (Long.valueOf(c.args[0].toString()) < Long.valueOf(c.args[1].toString()) ? "1" : "0") + "\n";
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
				code = "  li " + c.dest + (Integer.valueOf(c.args[0].toString()) < Integer.valueOf(c.args[1].toString()) ? "1" : "0") + "\n";
			break;
		case "PrintIntS":
			String moveArgument = "  move $a0 " + c.args[0].toString() + "\n";
			callFunction = "  jal _print\n";
			code = moveArgument + callFunction;
			break;
		case "HeapAllocZ":
			String loadArgument = "  li $a0 " + c.args[0].toString() + "\n";
			callFunction = "  jal _heapAlloc\n";
			String moveReturnValue = "  move " + c.dest.toString() + " $v0\n";
			code = loadArgument + callFunction + moveReturnValue;
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
		if (w.source.toString().startsWith(":"))
		{
			String loadAddress = "  la $t9 " + w.source.toString().substring(1) + "\n";
			Global dest = (Global) w.dest;
			String storeWord = "  sw $t9 " + Integer.toString(dest.byteOffset) + "(" + dest.base.toString() + ")\n";
			code = loadAddress + storeWord;
		}
		else if (Stack.class.isInstance(w.dest))
		{
			//TODO: may have to edit index depending on stack region
			Stack dest = (Stack) w.dest;
			code = "  sw " + w.source.toString() + " " + Integer.toString(dest.index * 4) + "($sp)\n";
		}
		else
			code = "  sw " + w.source.toString() + " (" + w.dest.toString() + ")\n";
		return code;
	}

	@Override
	public Object visit(Object p, VMemRead r) throws Exception {
		String code = "";
		if (Global.class.isInstance(r.source))
		{
			Global source = (Global) r.source;
			code = "  lw " + r.dest + " " + Integer.toString(source.byteOffset) + "(" +source.base + ")\n";
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
				code = "  lw " + r.dest + " " + Integer.toString(source.index * 4) + "($sp)\n";
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
