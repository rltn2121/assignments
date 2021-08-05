#include <iostream>
using namespace std;
int main() {
	int m=0;
	cin >> m;
	while (m--) {
		int n=0;
		cin >> n;
		
		int k_num = 0;
		int j_num = 0;
		int c_num = 0;
		int k_sum = 0; 
		int j_sum = 0;
		int c_sum = 0;
		
		for (int i = 0; i < n; i++) {
			int x;
			cin >> x;

			if (x != 0)
			{
				if (i % 3 == 0) {
					k_num++;
					k_sum += x;
				}

				else if (i % 3 == 1) {
					j_num++;
					j_sum += x;
				}

				else {
					c_num++;
					c_sum += x;
				}
			}
		}
		cout << k_sum << ' ' << j_sum << ' ' << c_sum << endl;

		// 런타임에러: divide by zero
		if (k_num == 0)
			cout << 0 << ' ';
		else
			cout << k_sum / k_num << ' ';
		if (j_num == 0)
			cout << 0 << ' ';
		else
			cout <<j_sum / j_num << ' ';
		if (c_num == 0)
			cout << 0 << ' ';
		else
			cout << c_sum / c_num << ' ';
		cout << endl;
	}
}