package midterm;
import GenCol.*;

// enemy.java���� �����ϴ� message�� ���̸� ���ο� ���ݹ�� �� ���� �������� ����
// ���ݹ�� �� ��� Ƚ��, ������ ������, ��ü ������ ���
public class combat_result extends entity{
	// ���ݹ���� ������ �����ϴ� �迭. (skill)_info[0]: ���� ������, (skill)_info[1]: ��� Ƚ��
	private int[] A_info;
	private int[] Q_info;
	private int[] W_info;
	private int[] E_info;
	private int[] R_info;
	
	// ���ݹ���� ������ ������
	private double A_share;
	private double Q_share;
	private double W_share;
	private double E_share;
	private double R_share;
	
	// �� ������
	private double total_damage;
	
	// constructor
	public combat_result(String name, int _A_sum, int _Q_sum, int _W_sum, int _E_sum, int _R_sum)	{  
		super(name);  
		A_info = new int[2];
		Q_info = new int[2];
		W_info = new int[2];
		E_info = new int[2];
		R_info = new int[2];
		
		// ���ݹ���� ���� ������ ����
		A_info[0] = _A_sum;
		Q_info[0] = _Q_sum;
		W_info[0] = _W_sum;
		E_info[0] = _E_sum;
		R_info[0] = _R_sum;
	}
	
	// �� ������ ���
	protected void setTotalDamage(){
		total_damage =A_info[0] + Q_info[0] + W_info[0] + E_info[0] + R_info[0];
	}
	
	// ���ݹ�� �� ��� Ƚ�� ���
	protected void setSkillCount(){
		// ��� Ƚ�� = (���� ������) / (1ȸ ���� �� ������)
		A_info[1] = A_info[0] / 80;
		Q_info[1] = Q_info[0] / 120;
		W_info[1] = W_info[0] / 300;
		E_info[1] = E_info[0] / 280;
		R_info[1] = R_info[0] / 650;
	}
	
	// ���ݹ�� �� ������ ������ ���
	protected void setSkillShare(){
		if(total_damage != 0){
			// ������ ������ = (���� ������) / (�� ������) * 100
			A_share = A_info[0] / total_damage * 100;
			Q_share = Q_info[0] / total_damage * 100;
			W_share = W_info[0] / total_damage * 100;
			E_share = E_info[0] / total_damage * 100;
			R_share = R_info[0] / total_damage * 100;
		}
	}
	
	// ���� �м� ��� ���
	public void printCombatResult(){
		System.out.println("�� ���� �м� ���");
		System.out.println("\t\t������\t���Ƚ��\t������ ������");
		System.out.println("�⺻ ����   (A)\t" + A_info[0] + "\t" + A_info[1] + "\t" + String.format("%.2f", A_share) + "%");
		System.out.println("�ź��� ȭ�� (Q)\t" + Q_info[0] + "\t" + Q_info[1] + "\t" + String.format("%.2f", Q_share) + "%");
		System.out.println("������ �帧 (W)\t" + W_info[0] + "\t" + W_info[1] + "\t" + String.format("%.2f", W_share) + "%");
		System.out.println("���� �̵�   (E)\t" + E_info[0] + "\t" + E_info[1] + "\t" + String.format("%.2f", E_share) + "%");
		System.out.println("������ �ϰ� (R)\t" + R_info[0] + "\t" + R_info[1] + "\t" + String.format("%.2f", R_share) + "%");
	}
}
