#include <iostream>
#include <string>
#include <vector>
using namespace std;

class Node {
public:
	int data;
	Node* parent;
	vector<Node*> child;

	Node(int data) {
		this->data = data;
		this->parent = NULL;
	}

	void insertChild(Node* child) {
		this->child.push_back(child);
		return;
	}
};

class Tree {
private:
	Node* root;
	vector<Node*> nodeList;
public:
	Tree(int data) {
		root = new Node(data);
		nodeList.push_back(root);
	}

	void insertNode(int parent_data, int data){
		for (Node* i : nodeList) {
			if (i->data == parent_data) {
				Node* newNode = new Node(data);
				nodeList.push_back(newNode);
				i->insertChild(newNode);
				newNode->parent = i;
				return;
			}
		}
		cout << -1 << endl;
		return;
	}

	void printChild(int data) {
		for (Node* i : nodeList) {
			if (i->data == data) {
				if (i->child.empty()) {
					cout << 0 << endl;
					return;
				}
				for (Node* j : i->child) {
					cout << j->data << ' ';
				}
				cout << endl;
				return;
			}
		}
		cout << -1 << endl;
		return;
	}

	int countDepth(int data) {
		int depth = 0;
		for (Node* i : nodeList) {
			if (i->data == data) {
				while (i != NULL) {
					i = i->parent;
					depth++;
				}
			}
		}
		return depth-1;
	}
};


int main() {
	Tree tree(1);
	int t;
	cin >> t;
	while (t--) {
		string str;
		cin >> str;
		if (str == "insert") {
			int x, y;
			cin >> x >> y;
			tree.insertNode(x, y);
		}

		else if (str == "printChild") {
			int x;
			cin >> x;
			tree.printChild(x);
		}

		else if (str == "printDepth") {
			int x;
			cin >> x;
			cout << tree.countDepth(x) << endl;
		}
	}
}