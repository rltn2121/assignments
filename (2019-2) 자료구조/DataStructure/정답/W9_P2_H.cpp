#include <iostream>
#include <string>
#include <vector>
using namespace std;
enum direction { MIN = 1, MAX = -1 };

class Heap {
private:
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
		if (direction == 1)
		{
			while (v[index] < v[index / 2]) {
				if (index == 1)
					return;
				swap(v[index], v[index / 2]);
				index /= 2;
			}
		}
		else
		{
			while (v[index] > v[index / 2]) {
				if (index == 1)
					return;
				swap(v[index], v[index / 2]);
				index /= 2;
			}
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

		// child가 존재할 경우
		while (child <= heap_size) {
			// left, right child 모두 존재할 경우
			if (direction == 1)
			{
				if ((child < heap_size) && v[child + 1] < v[child])
					child++;

				if (v[parent] < v[child])
					break;
			}
			else
			{
				if ((child < heap_size) && v[child + 1] > v[child])
					child++;

				if (v[parent] > v[child])
					break;
			}

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
	
	vector<int> v;
};

int main() {
	int t;
	cin >> t;
	
	while (t--) {
		Heap min_h(1);
		Heap max_h(-1);
		int a, b;
		cin >> a >> b;
		for (int i = 0; i < a; i++) {
			int x;
			cin >> x;
			min_h.insert(x);
			max_h.insert(x);
		}


		cout << min_h.v[b] << endl;

		for (int i = 0; i < b - 1; i++)
			min_h.pop();
		cout << min_h.pop() << endl;

		
		cout << max_h.v[b] << endl;

		for (int i = 0; i < b - 1; i++)
			max_h.pop();
		cout << max_h.pop() << endl;
	}
}