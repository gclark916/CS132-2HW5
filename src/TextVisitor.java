import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr.VisitorPR;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;


public class TextVisitor extends VisitorPR<Object, Object, Exception> {

	@Override
	public Object visit(Object p, VAssign a) throws Exception {
		String code = "  move " + a.dest.toString() + " " + a.source.toString() + "\n";
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
			code = "  addu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
			break;
		case "Sub":
			code = "  subu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
			break;
		case "MulS":
			code = "  mul " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
			break;
		case "Eq":
			code = "  seq " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
			break;
		case "Lt":
			code = "  sltu " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
			break;
		case "LtS":
			code = "  slt " + c.dest + " " + c.args[0].toString() + " " + c.args[1].toString() + "\n";
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
		else
			code = "  sw " + w.source + " (" + w.dest.toString() + ")\n";
		return code;
	}

	@Override
	public Object visit(Object p, VMemRead r) throws Exception {
		String code = "  lw " + r.dest + " (" + r.source.toString() + ")\n";
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
		String code = "  j " + g.target.toString() + "\n";
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
