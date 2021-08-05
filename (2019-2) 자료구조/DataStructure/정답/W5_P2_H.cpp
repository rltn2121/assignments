#include <iostream>
using namespace std;
class node {
public:
	node(int n) {
		data = n;
		next = NULL;
	}

	int data;
	node* next;
};

class LinkedQueue {
public:
	LinkedQueue() {
		head = NULL;
		tail = NULL;
	}

	void enqueue(int x) {
		node* newNode = new node(x);
		if (head == NULL)
			head = tail = newNode;
		else {
			tail->next = newNode;
			tail = newNode;
		}
		//cout << "head: " << head << ", tail: " << tail << endl;
	}

	int dequeue() {
		if (head == NULL)
			return -1;

		node* temp = head;
		int ret = 0;

		if (head == tail) {
			ret = head->data;
			head = tail = NULL;
		}

		else {
			ret = temp->data;
			head = head->next;
		}
		//cout << "head: " << head << ", tail: " << tail << endl;
		delete temp;
		return ret;
	}

	node* head;
	node* tail;
};

int main() {
	int t;
	cin >> t;
	
	while (t--) {
		int point = 0;
		int result = 0;
		int n;
		cin >> n;
		LinkedQueue q1;
		LinkedQueue q2;
		for (int i = 0; i < n; i++) {
			int x;
			cin >> x;
			q1.enqueue(x);
		}

		for (int i = 0; i < n; i++) {
			int x;
			cin >> x;
			q2.enqueue(x);
		}

		for (int i = 0; i < n; i++) {
			int a = q1.dequeue();
			int b = q2.dequeue();

			if (result > 0)
				a += result - 1;
			else if (result < 0)
				b += -result - 1;

			result = a - b;
		//	cout << "a: " << a << ", b: " << b << ", result: " << result << endl;
			
			if (result > 0)
				point++;
			else if (result < 0)
				point--;
			//cout << "point: " << point << endl;
		}

		if (point > 0)
			cout << 1 << endl;
		else if (point < 0)
			cout << 2 << endl;
		else
			cout << 0 << endl;
	}
}