#include <iostream>
#include <string>
using namespace std;

class ArrayQueue {
public:
	ArrayQueue(int num) {
		q = new int[num];
		n = 0;
		f = 0;
		r = -1;
		capacity = num;
	}

	int size() {
		return n;
	}

	bool empty() {
		return n == 0;
	}

	void enqueue(int x) {
		if (n == capacity)
			cout << "Full" << endl;
		else {
			r = (r + 1) % capacity;
			q[r] = x;
			n++;
		//	cout << "f: " << f << ", r: " << r << endl;
		}
	}

	int dequeue() {
		int ret = 0;
		if (empty())
			ret = -1;
		else {
			 ret = q[f];
			f = (f + 1) % capacity;
			n--;
		//	cout << "f: " << f << ", r: " << r << endl;
		}
		return ret;
	}

	int front() {
		int ret = 0;
		if (empty())
			ret = -1;
		else 
			ret = q[f];
		
		return ret;
	}

	// rear에서 인덱스 접근 이상함
	int rear() {
		if (empty())
			return -1;
		else {
			return q[r];
		}
	}


private:
	int *q;
	int n;
	int f;
	int r;
	int capacity;
};

int main() {
	int n;
	cin >> n;
	ArrayQueue q(n);
	int t;
	cin >> t;
	while (t--) {
		string str;
		cin >> str;
		if (str == "enqueue") {
			int x;
			cin >> x;
			q.enqueue(x);
		}

		else if (str == "dequeue") {
			int ret = q.dequeue();
			if (ret == -1)
				cout << "Empty" << endl;
			else
				cout << ret << endl;
		}

		else if (str == "front") {
			int ret = q.front();
			if (ret == -1)
				cout << "Empty" << endl;
			else
				cout << ret << endl;
		}

		else if (str == "rear") {
			int ret = q.rear();
			if (ret == -1)
				cout << "Empty" << endl;
			else
				cout << ret << endl;
		}
		else if (str == "isEmpty")
			cout << q.empty() << endl;
		else if (str == "size")
			cout << q.size() << endl;

	}
}