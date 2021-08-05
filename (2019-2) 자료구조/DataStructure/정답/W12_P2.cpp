#include <iostream>
using namespace std;
const int MAX = 353333;
#define NOITEM 0
#define ISITEM 1
#define AVAILABLE 2

class Node {
public:
	Node() {
		key = -1;
		value = -1;
		flag = NOITEM;
	}
	int key;
	int value;
	int flag;
};

Node HashTable[MAX];

int hashfunc(int key) {
	return key % MAX;
}
int hashfunc2(int key) {
	return (17 - (key % 17));
}

void clear() {
	for (int i = 0; i < MAX; i++) {
		HashTable[i].key = -1;
		HashTable[i].value = -1;
		HashTable[i].flag = NOITEM;
	}
}
void insert(int key, int value) {
	int probing = 1;
	while (HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].flag == ISITEM)
		probing++;
	HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].key = key;
	HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].value = value;
	HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].flag = ISITEM;
}

void search(int key) {
	int probing = 1;
	while (HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].flag != NOITEM)
	{
		if (key == HashTable[hashfunc(hashfunc(key) + (probing - 1)*hashfunc2(key))].key) {
			cout << 1 << ' ' << probing << '\n';
			return;
		}
		probing++;
	}
	cout << 0 << ' ' << probing << '\n';
}
int main() {
	ios_base::sync_with_stdio(false);
	cin.tie(NULL);
	int t;
	cin >> t;
	while (t--) {
		int n, m, x;
		cin >> n;
		for (int i = 0; i < n; i++) {
			cin >> x;
			insert(x,0);
		}
		cin >> m;
		for (int i = 0; i < m; i++) {
			cin >> x;
			search(x);
		}
		clear();
	}
}
