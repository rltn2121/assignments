package Lab6_HW;
import GenCol.*;

public class request extends entity {   
	int num1, num2;
	char op;
	// constructor
	public request(String name, int _num1, int _num2, char _op)	{  
		// request�� num1, num2, op�� ����
		super(name);  
		num1 = _num1;
		num2 = _num2;
		op = _op;
	}
}
