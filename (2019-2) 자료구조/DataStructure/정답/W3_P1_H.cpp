#include <iostream>
#include <string>
using namespace std;
class node {
public:
	int data;
	node* next;
	node(int x) {
		this->data = x;
		this->next = NULL;
	}
};
class LinkedList {
public:
	node* head;
	node* tail;
	LinkedList() {
		this->head = NULL;
		this->tail = NULL;
	}

	void addFront(int x) {
		node* newNode = new node(x);
		if (head == NULL)
			head = tail = newNode;
		else
		{
			newNode->next = head;
			head = newNode;
		}
	}

	int removeFront() {
		if (empty())
			return -1;

		node* temp = head;
		int ret = temp->data;

		// 런타임 에러: head -> NULL 로 바꾼 후 접근 시도
		if (head == tail) {
			int ret = head->data; 
			head = tail = NULL;
			return ret;
		}

	

		head = head->next;
		delete temp; 
		return ret;
	}

	int front() {
		if (empty())
			return -1;
		return head->data;
	}

	bool empty() {
		return head == NULL;
	}

	void showList() {
		if (empty())
			cout << -1 << endl;
		node* current = head;
		while (current!= NULL) {
			cout << current->data << ' ';
			current = current->next;
		}
		cout << endl;
	}
	void addBack(int x) {
		node* newNode = new node(x);
		if (head == NULL)
			head = tail = newNode;
		else
		{
			tail->next = newNode;
			tail = newNode;
		}
	}


};

int main() {
	int n;
	cin >> n;
	LinkedList l;
	while (n--) {
		string str;
		cin >> str;
		if (str == "addFront") {
			int x;
			cin >> x;
			l.addFront(x);
		}

		else if (str == "removeFront") {
			cout << l.removeFront() << endl;
		}
		else if (str == "front") {
			cout << l.front() << endl;
		}
		else if (str == "empty") {
			cout << l.empty() << endl;
		}
		else if (str == "showList") {
			l.showList();
		}
		if (str == "addBack") {
			int x;
			cin >> x;
			l.addBack(x);
		}
	}
}