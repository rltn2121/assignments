#include <iostream>
#include <string>
using namespace std;
int main() {
	int t;
	cin >> t;
	while (t--) {
		int arr[100] = { 0, };
		int index = 0;
		string str;
		cin >> str;
		for (auto i : str) {
			if (isdigit(i))
				arr[index++] = i-48;
			else
			{
				int a = arr[--index];
				int b = arr[--index];
				
				if (i == '+')
					arr[index++] = b + a;
				else if (i == '-')
					arr[index++] = b - a;
				else if (i == '*')
					arr[index++] = b * a;
				else if (i == '/')
					arr[index++] = b / a;
			}
		}
		cout << arr[0] << endl;
	}
}