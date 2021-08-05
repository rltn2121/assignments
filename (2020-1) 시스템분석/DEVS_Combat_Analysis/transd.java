package midterm;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;
// turnaround time, throughput ���
public class transd extends  ViewableAtomic {
	
	protected Function arrived, solved;                     // transducer�� ������ attack�� arrived��, ó���� attack�� solved�� ����
	protected double clock, total_ta, observation_time;		// ���� ���� �ð�, total turnaround time, observation_time ����
	protected int arrived_cnt, solved_cnt;					// ������ attack, ó���� attack�� ��ȣ ����

	// constructor
	public transd(String name, double Observation_time)	{
		super(name);
		// ��Ʈ �̸� ����
		addOutport("out");
		addInport("ariv");
		addInport("solved");
    
		// arrived, solved ��ü ����
		arrived = new Function();
		solved = new Function();
		// arrived_cnt(������ attack ����), solved_cnt(ó���� attack ����) �ʱ�ȭ
		arrived_cnt = 0;
		solved_cnt = 0;
    
		observation_time = Observation_time;
	}
  
	// constructor
	public transd()	{
		this("transd", 200);
	}
	
	// �ʱ�ȭ
	public void initialize()	{	
		clock = 0;
		total_ta = 0;
    
		arrived = new Function();
		solved = new Function();
		arrived_cnt = 0;
		solved_cnt = 0;
		holdIn("on", observation_time);
	}
	// myChamp���� ������ attack, enemy���� ������ combat_result��  �̿��Ͽ� turnaround time, throughput ���
	public void deltext(double e, message x)	{
		clock = clock + e;
		Continue(e);
		entity val;
 
		if(phaseIs("on")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "ariv", i)) {
					// myChamp�� attack�� transducer�� "ariv"�� ����
					val = x.getValOnPort("ariv", i);
					// name�� "job + (������ ����(arrived_cnt))" ���·� ����
					String name = "job " + Integer.toString(arrived_cnt++);
					// name�� arrived�� ����
					arrived.put(name, new doubleEnt(clock));
				}
				// enemy�� combat_result�� transducer�� "solved"�� ����
				if (messageOnPort(x, "solved", i)) {
					val = x.getValOnPort("solved", i);
					// name�� "job + (ó���� ����(solved_cnt))" ���·� ����
					String name = "job " + Integer.toString(solved_cnt++);
					// arrived�� job + (ó���� ����(solved_cnt))�� �����ϸ� turnaround time ���
					if (arrived.containsKey(name)) {
						entity ent = (entity) arrived.assoc(name);
						doubleEnt num = (doubleEnt) ent;
						double arrival_time = num.getv();
						double turn_around_time = clock - arrival_time;
						total_ta = total_ta + turn_around_time;
						// solved�� ����
						solved.put(val, new doubleEnt(clock));
					}
				}
			}
			// ó�� ��� ǥ��
	   		show_state();
	   		System.out.println("--------------------------------------------------------\n");
		}
	}
	
	// ó�� ��� ǥ��
	public void deltint() {
		if (phaseIs("on")) {
			clock = clock + sigma;
	   		show_state();
	   		System.out.println("--------------------------------------------------------\n");
	   		
	   		holdIn("off", 0);
		}
	}
	
	// ó�� ����� �ܺη� ����
	public message out() {
		message m = new message();
		
		if (phaseIs("on"))
			m.add(makeContent("out", new entity("TA: " + compute_TA())));

		return m;
	}
	// turnaround time ���
	public double compute_TA()	{
		double avg_ta_time = 0;
		if (!solved.isEmpty())
			avg_ta_time = ( (double) total_ta) / solved.size();
		return avg_ta_time;
	}

	// throughput ���
	public String compute_Thru() {
		String thruput = "";
		if (clock > 0)
			thruput = solved.size() + " / " + clock;
		return thruput;
	}
	// ó�� ��� ���
	public void show_state() {
		System.out.println("\n�� state of  " + name + ": ");
		System.out.println("- phase, sigma : " + phase + " " + sigma + " ");
		
		// ������ attack�� ��, ó���� attack�� ��, total turnaround time, average turnaround time, throughput ���
		if (arrived != null && solved != null)	{
			System.out.println("- Total jobs arrived : "+ arrived.size());
			System.out.println("- Total jobs solved : " + solved.size());
			System.out.println("  �� Total TA = " + total_ta);
			System.out.println("  �� AVG TA = " + compute_TA());
			System.out.println("  �� THRUPUT = " + compute_Thru());
		}
	}	
  
	public String getTooltipText() {
		String s = "";
		if (arrived != null && solved != null) {
			s = "\n" + "jobs arrived :" + arrived.size()
			+ "\n" + "jobs solved :" + solved.size()
			+ "\n" + "AVG TA = " + compute_TA()
			+ "\n" + "THRUPUT = " + compute_Thru();
		}
		return super.getTooltipText() + s;
	}
}
