package Lab6_HW;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;

public class client extends ViewableAtomic {
	protected double int_arr_time;  // request�� �����ϴ� ����
	protected request req_msg;      // request�� ������ message
	protected result res_msg;       // result�� ������ message
	protected int num1, num2;
	protected char op;
  
	// constructor
	public client() {
		this("client", 30);
	}
  
	// constructor
	public client(String name, double Int_arr_time)	{
		super(name);
		// out port ����
		addOutport("out_server");
		addOutport("out_server2");
		// in port ���� 
		addInport("in");
		int_arr_time = Int_arr_time;
	}
	// ����, ��ü �ʱ�ȭ
	public void initialize() {
		// req_msg, res_msg �ʱ�ȭ
		req_msg = new request("none",0,0, ' ');
		res_msg = new result("none",0);
		
		// num1, num2, op�� ������ ������ �ʱ�ȭ
		num1 = (int)(Math.random()*100)+1;
		num2 = (int)(Math.random()*100)+1;
		int op_num = (int)(Math.random()*4);
		switch(op_num){
		case 0:
			op = '+';
			break;
		case 1:
			op = '-';
			break;
		case 2:
			op = '*';
			break;
		case 3:
			op = '%';
			break;
		}
		holdIn("active", int_arr_time);
	}
	// server, server2�κ��� ���� result�� ó��
	public void deltext(double e, message x) {
		Continue(e);
		if (phaseIs("active"))	{
			for (int i = 0; i < x.getLength(); i++)	{
				if (messageOnPort(x, "in", i))	
					// server, server2�� ���� ���� result�� �ܼ�â�� ���
					System.out.println("  �� result of request = " + x.getValOnPort("in", i));
			}
		}
	}
	
	// num1, num2, op �� ����
	public void deltint() {
		if (phaseIs("active")) {
			// num1, num2, op�� �������� ����
			num1 = (int)(Math.random()*100)+1;
			num2 = (int)(Math.random()*100)+1;
			int op_num = (int)(Math.random()*4);
			switch(op_num){
			case 0:
				op = '+';
				break;
			case 1:
				op = '-';
				break;
			case 2:
				op = '*';
				break;
			case 3:
				op = '%';
				break;
			}
			holdIn("active", int_arr_time);
		}
	}
	
	// deltint()���� ������ request�� transducer�� server(server2)�� ����
	public message out() {
		message m = new message();
		String name = Integer.toString(num1) + op + Integer.toString(num2);
		req_msg = new request(name, num1, num2, op); 
		// op�� * or % �̸� server�� ����
		if(op=='*' || op=='%')
			m.add(makeContent("out_server", req_msg));
		// op�� + or - �̸� server2�� ����
		else
			m.add(makeContent("out_server2", req_msg));
		return m;
	}
  
	public String getTooltipText() {
		return super.getTooltipText();
	}
}
