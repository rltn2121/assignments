#include <iostream>
using namespace std;

class Node {
public:
	Node(int data) {
		this->data = data;
		parent = left = right = NULL;
	}
	int data;
	Node* parent;
	Node* left;
	Node* right;
};

class BST {
public:
	BST(int key) {
		root = new Node(key);
	}


	Node* lookup(Node* current, int key) {
		if (current != NULL) {
			if (key == current->data)
				return current;

			if (key > current->data)
				lookup(current->right, key);
			else
				lookup(current->left, key);
		}
		else
			return NULL;
	}

	void insert(Node* current, int key) {
		if (key == current->data)
			return;

		if (key > current->data) {
			if (current->right != NULL)
				insert(current->right, key);
			else {
				current->right = new Node(key);
				current->right->parent = current;
			}
		}
		else {
			if (current->left != NULL)
				insert(current->left, key);
			else {
				current->left = new Node(key);
				current->left->parent = current;
			}
		}
	}

	void remove(int key) {
		Node* delNode = lookup(root, key);
		if (delNode == NULL)
			return;

		if (delNode->left == NULL && delNode->right == NULL) {
			if (delNode != root) {
				if (delNode == delNode->parent->right)
					delNode->parent->right = NULL;
				else
					delNode->parent->left = NULL;
			}
			else
				root = NULL;
		}
		else if (delNode->left != NULL && delNode->right != NULL) {
			Node* predecessor = delNode;
			Node* successor = delNode->right;
			while (successor->left != NULL) {
				predecessor = successor;
				successor = successor->left;
			}
			predecessor->left = successor->right;
			successor->left = delNode->left;
			successor->right = delNode->right;
			if (delNode != root) {
				if (delNode == delNode->parent->right)
					delNode->parent->right = successor;
				else
					delNode->parent->left = successor;
			}
			else
				root = successor;
			
		}
		else {
			Node* child = NULL;
			if (delNode->right != NULL)
				child = delNode->right;
			else
				child = delNode->left;

			if (delNode != root) {
				if (delNode == delNode->parent->right)
					delNode->parent->right = child;
				else
					delNode->parent->left = child;
			}
			else
				root = child;
		}
	}

	void inorder(Node* u) {
		if (u != NULL) {
			inorder(u->left);
			cout << u->data << ' ';
			inorder(u->right);
		}
	}
	void preorder(Node* u) {
		if (u != NULL) {
			cout << u->data << ' ';
			preorder(u->left);
			preorder(u->right);
		}
	}
	Node* root;
};

int main() {
	BST b(10);

	cout << b.lookup(b.root, 10)->data << endl;

	b.insert(b.root, 5);
	b.insert(b.root, 15);
	b.insert(b.root, 13);
	b.insert(b.root, 20);
	if (b.lookup(b.root, 6) != NULL)
		cout << b.lookup(b.root, 6)->data;
	else
		cout << "찾는 값이 없습니다.\n";
	b.insert(b.root, 11);
	b.insert(b.root, 14);	//cout << b.root->data << endl;
	b.insert(b.root, 1);
	b.insert(b.root, 2);
	b.insert(b.root, 3);
	b.insert(b.root, 6);
	b.insert(b.root, 7);
	b.inorder(b.root); cout << endl;
	cout << b.lookup(b.root, 6)->data << endl;


	b.remove(2); b.inorder(b.root); cout << endl;
	b.remove(10); b.inorder(b.root); cout << endl;
	b.remove(13); b.inorder(b.root); cout << endl;

	b.insert(b.root, 2);
	b.insert(b.root, 10);
	cout << b.lookup(b.root, 2)->data << endl;
	cout << b.lookup(b.root, 10)->data << endl;
	b.inorder(b.root);
	b.preorder(b.root);




}