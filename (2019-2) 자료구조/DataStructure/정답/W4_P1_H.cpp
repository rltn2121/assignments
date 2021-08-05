#include <iostream>
#include <string>
using namespace std;
class Stack {
public:
	Stack(int size) {
		// 동적할당 잘못함
		arr = new int[size];
		t = -1;

	}

	bool empty() {
		return t == -1;
	}

	int top() {
		if (empty())
			return -1;
		return arr[t];
	}

	void push(int x) {
		arr[++t] = x;
	}

	int pop() {
		if (empty())
			return -1;
		int ret = arr[t];
		arr[t--] = 0;
		return ret;
	}

	int size() {
		return t + 1;
	}
private:
	int *arr;
	int t;
};

int main() {
	int t;
	cin >> t;
	Stack s(10001);
	while (t--) {
		string str;
		cin >> str;
		if (str == "empty")
			cout << s.empty() << endl;

		else if (str == "top")
			cout << s.top() << endl;

		else if (str == "push") {
			int x;
			cin >> x;
			s.push(x);
		}

		else if (str == "pop") {
			cout << s.pop() << endl;
		}

		else if (str == "size") {
			cout << s.size() << endl;
		}
	}
}