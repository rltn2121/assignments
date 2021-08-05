package Lab6_HW;
import genDevs.modeling.*;
import GenCol.*;
import Lab6.request_msg;
import Lab6.result_msg;
import simView.*;

public class server2 extends ViewableAtomic{
	protected Queue q;						// request�� ������ queue
	protected request req_msg;				// request�� ������ message
	protected result res_msg;				// result�� ������ message
	protected double processing_time;		// request�� ó���ϴµ� �ɸ��� �ð�
	protected int result;
	
	public server2()	{
		this("server2", 20);
	}

	public server2(String name, double Processing_time)	{
		super(name);
		// in port, out port ����
		addInport("in");
		addOutport("out");
		processing_time = Processing_time;
	}
	
	// ����, ��ü �ʱ�ȭ
	public void initialize()	{
		result = 0;
		processing_time = 0;
		q = new Queue();
		req_msg = new request("none",0,0, ' ');
		res_msg = new result("none",0);
		holdIn("passive", INFINITY);
	}

	// client���� ������ request ó��
	public void deltext(double e, message x) {
		Continue(e);
		// ó�� ���� request�� ���� ���
		if (phaseIs("passive"))		{
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// in port�� ���� request�� �޾Ƽ� ó�� 
					req_msg = (request)x.getValOnPort("in", i);
					// op�� + �̸� processing_time = 20, -�̸� processing_time = 30���� ����
				switch(req_msg.op){
				case '+':
					result = req_msg.num1 + req_msg.num2;
					holdIn("busy", 20);
					break;
				case '-':
					result = req_msg.num1 - req_msg.num2;
					holdIn("busy", 30);
					break;
				
				}
			}
		}
	}
		// request�� ó�� ���� ���
		else if (phaseIs("busy")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// in port�� ���� request�� �޾Ƽ� queue�� ����
					request temp = (request)x.getValOnPort("in", i);
					q.add(temp);
				}
			}
		}
	}
	
	public void deltint() {
		if (phaseIs("busy")) {
			// queue�� ó������ ���� request�� ���� ���
			if (!q.isEmpty()) {
				// queue���� request�� ������ ó��
				req_msg = (request)q.removeFirst();
				// op�� + �̸� processing_time = 20, -�̸� processing_time = 30���� ����
				switch(req_msg.op){
				case '+':
					result = req_msg.num1 + req_msg.num2;
					holdIn("busy", 10);
					break;
				case '-':
					result = req_msg.num1 - req_msg.num2;
					holdIn("busy", 20);
					break;
				}

			}
			// queue�� ����� ��� �ʱ�ȭ
			else{
				result = 0;
				req_msg = new request("none",0,0, ' ');
				res_msg = new result("none",0);
				processing_time = 0;
				q.clear();
				holdIn("passive", INFINITY);
			}
		}
	}
	// request�� ó�� ����� client�� ����
	public message out() {
		message m = new message();
		if (phaseIs("busy")) {
			res_msg = new result(Integer.toString(result), result);
			m.add(makeContent("out", res_msg));
		}
		return m;
	}	
	
	public String getTooltipText() {
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}

}



