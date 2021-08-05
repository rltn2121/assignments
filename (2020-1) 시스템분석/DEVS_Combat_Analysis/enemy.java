package midterm;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;
// myChamp.java���� ������ ������ ���ݹ�� �� ���� �������� �� �������� ���
// combat_result ��ü�� �����Ͽ� ���ݹ�� �� ���Ƚ��, ������ �������� �� ������ ��� �� ���
public class enemy extends ViewableAtomic {
	protected Queue q;
	protected attack atk_msg;             // myChamp.java���� ���޵� attack ��ü
	protected combat_result combat_msg;   // ���� ����� ������ combat_result ��ü
	
	// ���ݹ�� �� ���� ������
	protected int A_sum;
	protected int Q_sum;
	protected int W_sum;
	protected int E_sum;
	protected int R_sum;
	
	// ���ݹ�� �� ���ð�
	protected int A_proc_time;
	protected int Q_proc_time;
	protected int W_proc_time;
	protected int E_proc_time;
	protected int R_proc_time;
	
	// �� ������
	protected int total_damage;
	// combat_result ��ü�� �̸�
	protected String name;

	// constructor
	public enemy() {
		this("procQ");
	}
	// constructor
	public enemy(String name) {
		super(name);
		// ��Ʈ �̸� ����
		addInport("in");
		addOutport("out");
	}
	// �ʱ�ȭ
	public void initialize() {
		// queue, attack, combat_result ��ü ����
		q = new Queue();
		atk_msg = new attack("none",'-',0);
		combat_msg = new combat_result("none", 0, 0, 0, 0, 0);
		
		// ���ݹ�� �� ���� ������ �ʱ�ȭ
		A_sum = 0;
		Q_sum = 0;
		W_sum = 0;
		E_sum = 0;
		R_sum = 0;
		
		// ���ݹ�� �� ���ð� �ʱ�ȭ
		A_proc_time = 15;
		Q_proc_time = 15;
		W_proc_time = 20;
		E_proc_time = 25;
		R_proc_time = 30;
		total_damage = 0;
		name = null;
		holdIn("passive", INFINITY);
	}
	
	// myChamp�� ���� ���� ������ ���� ������, �� ������, ���Ƚ��, ������ ������ ���
	public void deltext(double e, message x) {
		Continue(e);
		// ó�� ���� �۾��� ���� ���
		if (phaseIs("passive"))	{
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// attack ��ü �ޱ�
					atk_msg = (attack)x.getValOnPort("in", i);
					// �� ������ ���
					total_damage += atk_msg.damage;
					// ���ݹ�� �� ���� ������ ���
					switch(atk_msg.skill){
					case 'A':
						A_sum += atk_msg.damage;
						holdIn("busy", A_proc_time);
						break;
					case 'Q':
						Q_sum += atk_msg.damage;
						holdIn("busy", Q_proc_time);
						break;
					case 'E':
						E_sum += atk_msg.damage;
						holdIn("busy", E_proc_time);
						break;
					case 'W':
						W_sum += atk_msg.damage;
						holdIn("busy", W_proc_time);
						break;
					case 'R':
						R_sum += atk_msg.damage;
						holdIn("busy", R_proc_time);
						break;
					}		
					// combat_result ��ü�� �̸� ����
					name = "total damage: " + total_damage;
					// combat_result ��ü ����
					combat_msg = new combat_result (name, A_sum, Q_sum, W_sum, E_sum, R_sum);
					// �� ������, ����Ƚ��, ������ ������ ���
					combat_msg.setTotalDamage();
					combat_msg.setSkillCount();
					combat_msg.setSkillShare();
				}
			}
		}
		// ó�� ���� �۾��� ���� ���
		else if (phaseIs("busy")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// attack ��ü�� �޾Ƽ� queue�� ����
					attack temp = (attack)x.getValOnPort("in", i);
					q.add(temp);
				}
			}
		}
	}
	
	// queue�� �ִ� �۾� ó��
	public void deltint() {
		if (phaseIs("busy")) {
			// queue�� ó������ ���� ��ü�� ���� ���
			if (!q.isEmpty()) {
				// queue���� attack ��ü ����
				atk_msg = (attack) q.removeFirst();
				// �� ������ ���
				total_damage += atk_msg.damage;
				// ���ݹ�� �� ���� ������ ���
				switch(atk_msg.skill){
				case 'A':
					A_sum += atk_msg.damage;
					holdIn("busy", A_proc_time);
					break;
				case 'Q':
					Q_sum += atk_msg.damage;
					holdIn("busy", Q_proc_time);
					break;
				case 'E':
					E_sum += atk_msg.damage;
					holdIn("busy", E_proc_time);
					break;
				case 'W':
					W_sum += atk_msg.damage;
					holdIn("busy", W_proc_time);
					break;
				case 'R':
					R_sum += atk_msg.damage;
					holdIn("busy", R_proc_time);
					break;
				}		
				// combat_result ��ü�� �̸� ����
				name = "total damage: " + total_damage;
				// combat_result �̸� ����
				combat_msg = new combat_result (name, A_sum, Q_sum, W_sum, E_sum, R_sum);
				// �� ������, ���Ƚ��, ������ ������ ���
				combat_msg.setTotalDamage();
				combat_msg.setSkillCount();
				combat_msg.setSkillShare();
			}
			else {
				// queue, attack, combat_result ��ü �ʱ�ȭ
				q = new Queue();
				atk_msg = new attack("none",'-',0);
				combat_msg = new combat_result("none", 0, 0, 0, 0, 0);
				holdIn("passive", INFINITY);
			}
		}
	}

	// combat_result ��ü�� �ܺη� ����
	public message out() {
		message m = new message();
		if (phaseIs("busy")){
			combat_msg.printCombatResult();
			m.add(makeContent("out", combat_msg));
		}
		return m;
	}	
	
	public String getTooltipText()	{
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}
}