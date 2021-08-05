#include <iostream>
#include <string>
using namespace std;

class node {
public:
	node(int n) {
		data = n;
		prev = NULL;
		next = NULL;
	}
	int data;
	node* prev;
	node* next;
};

class CLinkedList {
public:
	CLinkedList() {
		count = 0;
		head = tail = NULL;
	}
	void addBack(int n) {
		node* newNode = new node(n);
		if (head == NULL)
			head = tail = newNode;
		else {
			tail->next = newNode;
			tail = newNode;
		}

		tail->next = head;
		count++;
	}

	void remove(int idx) {
		if (idx == 0) {
			head = head->next;
			tail->next = head;
		}

		else {
			node* current = head;
			for (int i = 0; i < idx - 1; i++) {
				current = current->next;
			}

			node* temp = current->next;

			if (idx == count) {
				tail = current;
				tail->next = head;
			}

			else 
				current->next = temp->next;
			
			delete temp;
		}
		count--;
	}

	void showList() {
		node* current = head;
		for(int i=0; i<count; i++) {
			cout << current->data << ' ';
			current = current->next;
		}
		cout << endl;
	}
	int count;
	node* head;
	node* tail;
};

int main() {
	int t;
	cin >> t;
	while (t--) {
		CLinkedList l;
		for (int i = 0; i < 10; i++) {
			int x;
			cin >> x;
			l.addBack(x);
		}
		for (int i = 0; i < 3; i++) {
			string str;
			cin >> str;
			int x;
			cin >> x;
			l.remove(x % l.count);
		}

		l.showList();
	}
}