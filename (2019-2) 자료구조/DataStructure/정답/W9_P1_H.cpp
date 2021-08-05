#include <iostream>
#include <string>
#include <vector>
using namespace std;
enum direction { MIN = 1, MAX = -1 };

class Heap {
private:
	vector<int> v;
	int heap_size;
	int root_index;
	int direction;
public:
	Heap(int direction) {
		v.push_back(-1);
		this->heap_size = 0;
		this->root_index = 1;
		this->direction = direction;
	}

	void swap(int &a, int &b) {
		int temp = a;
		a = b;
		b = temp;
	}
	void insert(int e) {
		v.push_back(e);
		heap_size++;

		int index = heap_size;
		//cout << "index: " << index << endl;

		while (v[index] < v[index / 2]) {
			swap(v[index], v[index / 2]);
			index /= 2;
		}
	}

	int pop() {
		if (isEmpty())
			return -1;
		int ret = v[1];
		int index = heap_size--;
		swap(v[1], v[index]);
		v.pop_back();

		int parent = 1;
		int child = 2;

		// 이거 때문에 점수 다 날림
		// child가 존재할 경우
		while (child <= heap_size) {
			// left, right child 모두 존재할 경우
			if ((child < heap_size) && v[child + 1] < v[child])
				child++;

			if (v[parent] < v[child])
				break;

			swap(v[parent], v[child]);
			parent = child;
			child *= 2;
		}
		return ret;
	}

	int top() {
		if (isEmpty())
			return -1;
		return v[1];
	}

	int size() {
		return heap_size;
	}

	bool isEmpty() {
		return heap_size == 0;
	}

	void print() {
		if (isEmpty())
			cout << -1 << endl;
		else {
			for (int i = 1; i <= heap_size; i++)
				cout << v[i] << ' ';
		}
		cout << endl;
	}
};

int main() {
	int t;
	cin >> t;
	Heap h(1);
	while (t--) {
		string str;
		cin >> str;
		if (str == "insert") {
			int x;
			cin >> x;
			h.insert(x);
		}

		else if (str == "size")
			cout << h.size() << endl;
		else if (str == "isEmpty")
			cout << h.isEmpty() << endl;
		else if (str == "pop")
			cout << h.pop() << endl;
		else if (str == "top")
			cout << h.top() << endl;
		else if (str == "print")
			h.print();
	}
}