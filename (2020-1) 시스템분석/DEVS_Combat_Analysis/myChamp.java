package midterm;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;
import Lab6_HW.request;
import Lab6_HW.result;

// ���� �ð����� ���� ���
// �⺻ ����(A) : ������ 80, ���� ���ð� ����
// �ź��� ȭ��(Q): ������ 120, ���� ���ð� ����
// ������ �帧(W): ������ 300, ���� ���ð� 100 sigma
// ���� �̵�(E) : ������ 280, ���� ���ð� ����
// ������ �ϰ�(R): ������ 650, ���� ���ð� 300 sigma
public class myChamp extends ViewableAtomic {
	protected attack atk_msg;        // ���������� ������ attack ��ü
	protected char skill;            // ���ݹ��
	protected int damage;            // ���ݹ���� ������
	protected int R_cooling_time;    // ������ �ϰ�(R)�� ���� ���ð�
	protected int W_cooling_time;    // ������ �帧(W)�� ���� ���ð�
	protected double int_arr_time;   // ������ �ð� ����
	
	// Constructor
	public myChamp() {
		// ù ������ 20 sigma�� ���
		this("genr", 20);
	}
	
	// Constructor
	public myChamp(String name, double Int_arr_time) {
		super(name);
   
		// ��Ʈ �̸� ����
		addOutport("out");
		addInport("in");
		// ������ �ð� ���� ����
		int_arr_time = Int_arr_time;
	}
  
	// �ʱ�ȭ
	public void initialize() {
		// attack ��ü ����
		atk_msg = new attack("none",'-',0);
		// ù ������ �⺻ ����(A)���� ����
		skill = 'A';
		damage = 80;
		// ������ �帧(W), ������ �ϰ�(R)�� ���� ���ð� ����
		R_cooling_time = 300;
		W_cooling_time = 100;
		holdIn("active", int_arr_time);
	}
	
	// �ܺο��� ���� message ó��
	public void deltext(double e, message x) {
		Continue(e);
		if (phaseIs("active"))	{
			for (int i = 0; i < x.getLength(); i++)	{
				if (messageOnPort(x, "in", i))
						holdIn("stop", INFINITY);
			}
		}
	}
	
	// ���� ���
	public void deltint() {
		if (phaseIs("active")) {
			int skill_type = 0;
			if(R_cooling_time > 0){
				if(W_cooling_time > 0){
					// ������ �帧(W), ������ �ϰ�(R)�� ���� ���ð��� 0���� Ŭ ���
					// �⺻ ����(A), �ź��� ȭ��(Q), ���� �̵�(E) �� �ϳ��� �����ϰ� ���
					skill_type = (int)(Math.random()*3);
					switch(skill_type){
					case 0:
						skill = 'A';
						damage = 80;
						break;
					case 1:
						skill = 'Q';
						damage = 120;
						break;
					case 2:
						skill = 'E';
						damage = 280;
						break;
					}
					
					R_cooling_time -= 20;  // ������ �帧(W)�� ���� ���ð� ����
					W_cooling_time -= 20;  // ������ �ϰ�(R)�� ���� ���ð� ����
				}
				// ������ �帧(W)�� ���� ���ð��� 0�� ���
				else {
					skill = 'W';
					damage = 300;
					
					R_cooling_time-= 20;   // ������ �ϰ�(R)�� ���� ���ð� ����
					W_cooling_time = 100;  // ������ �帧(W)�� ���� ���ð� �ʱ�ȭ
				}
			}
			// ������ �ϰ�(R)�� ���� ���ð��� 0�� ���
			else{
				skill = 'R';
				damage = 650;
				
				R_cooling_time = 300;  // ������ �ϰ�(R)�� ���� ���ð� �ʱ�ȭ
				if(W_cooling_time!=0)  // ������ �帧(W)�� ���� ���ð��� 0���� Ŭ ��� ���� ���ð� ����
					W_cooling_time -= 20;
			}
			// 20 sigma�� ���� �ð�
			holdIn("active", 20);
		}
	}

	// ����� ������ ������ �ܺη� ����
	public message out() {	
		message m = new message();
		String name = "";
		// ������ message�� �̸� ����
		if(skill == 'A')
			name = "�⺻ ����(A)";
		else if(skill == 'Q')
			name = "�ź��� ȭ��(Q)";
		else if(skill == 'W' )
			name = "������ �帧(W)";
		else if(skill == 'E')
			name = "���� �̵�(E)";
		else if(skill == 'R')
			name = "������ �ϰ�(R)";
		
		// ����� ������ ������ ������ attack ��ü ��� �� ����
		atk_msg = new attack(name, skill, damage); 
		m.add(makeContent("out", atk_msg));
		
		// ����� ����, ������ �帧(W), ������ �ϰ�(R)�� ���� ���ð� ���
		System.out.println("�� ����� ����: " + name);
		System.out.println("  - R ���� ���ð�: " + R_cooling_time);
		System.out.println("  - w ���� ���ð�: " + W_cooling_time);
		return m;
	}
  
	public String getTooltipText() {
		return
        super.getTooltipText()
        + "\n" + " int_arr_time: " + int_arr_time;
	}
}
