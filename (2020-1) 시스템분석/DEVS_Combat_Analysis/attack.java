package midterm;
import GenCol.*;

// myChamp.java���� �����ϴ� message�� ���̸� ���ο� ����� ����� ����(char skill)�� ������(int damage)�� ����
public class attack extends entity {
	char skill;  // ����� ���ݹ��
	int damage;  // ����� ������ ������
	
	// constructor
	public attack(String name, char _skill, int _damage)	{  
		super(name);  
		skill = _skill;
		damage = _damage;
	}
}
