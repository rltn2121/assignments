#include <iostream>
#include <string>
using namespace std;
class Array {
public:
	Array(int s = 10000) 
		:arr(new int[s]),n(0), size(s) {
		for (int i = 0; i < size; i++)
			arr[i] = 0;
	}
	int at(int idx) {
		if (idx >= n || idx < 0)
			return 0;
		return arr[idx];
	}

	void set(int idx, int x) {
		if (idx >= n || idx < 0)
			cout << 0 << endl;
		else
			arr[idx] = x;
	}

	void add(int idx, int x) {
		if (idx > n || idx < 0)
			arr[n++] = x;
		else {
			for (int i = n; i > idx; i--) {
				arr[i] = arr[i - 1];
			}
			arr[idx] = x;
			n++;
		}
	}

	int remove(int idx) {
		if (idx >= n || idx < 0)
			return 0;
		else {
			int ret = arr[idx];
			for (int i = idx; i < n; i++) 
				arr[i] = arr[i + 1];
				
			arr[--n] = 0;
			return ret;
		}
	}

	void printArray() {
		if (n == 0)
			cout << 0 << endl;
		else {
			for (int i = 0; i < n; i++) {
				cout << arr[i] << ' ';
			}
			cout << endl;
		}
	}
	int *arr;
	int n;
	int size;
};
using namespace std;
int main() {
	Array arr;
	int n;
	cin >> n;
	while (n--) {
		string str;
		cin >> str;
		if (str == "at") {
			int x;
			cin >> x;
			cout << arr.at(x) << endl;
		}

		else if (str == "set") {
			int i,x;
			cin >> i >> x;
			arr.set(i, x);
		}
		else if (str == "add") {
			int i, x;
			cin >> i >> x;
			arr.add(i, x);
		}
		else if (str == "remove") {
			int i;
			cin >> i;
			cout << arr.remove(i) << endl;
		}
		else if (str == "printArray") {
			arr.printArray();
		}
	}
}