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
	int height;
public:
	Tree(int data) {
		root = new Node(data);
		nodeList.push_back(root);
		height = 0;
	}

	Node* getRoot() {
		return root;
	}

	void insertNode(int parent_data, int data) {
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



	void preOrder(Node* node) {
		cout << node->data << ' ';
		if (!node->child.empty()) {
			for (Node* i : node->child) {
				preOrder(i);
			}
		}
	}

	void postOrder(Node* node) {
		if (!node->child.empty()) {
			for (Node* i : node->child) {
				postOrder(i);
			}
		}
		cout << node->data << ' ';
	}

	int countHeight(Node* node) {
		if (node->child.empty())
			return 0;
		int height = 0;
		for (Node* i : node->child) {
			height = height > countHeight(i) ? height : countHeight(i);
		}
		return height + 1;
	}
};

int main() {
	int t;
	cin >> t;
	while (t--) {
		Tree tree(1);
		int num;
		cin >> num;
		while (num--) {
			int x, y;
			cin >> x>> y;
			tree.insertNode(x, y);
		}
		tree.preOrder(tree.getRoot());
		cout << endl;
		tree.postOrder(tree.getRoot());
		cout << endl;
		cout << tree.countHeight(tree.getRoot()) << endl;
	}
}