#include <iostream>
using namespace std;
const int MAX =353333;
#define NOITEM 0
#define ISITEM 1
#define AVAILABLE 2
class Node {
public:
	Node(){
		this->key = -1;
		this->value = -1;
		flag = NOITEM;
	}
	int key;
	int value;
	int flag;
};

Node HashArr[MAX];
int hashfunc(int idx) {
	return idx % MAX;
}
void insert(int key, int value) {
	int probing = 1;
	while (HashArr[hashfunc(key +probing-1)].flag == ISITEM)
		probing++;
	HashArr[hashfunc(key + probing - 1)].key = key;
	HashArr[hashfunc(key + probing - 1)].value = key;
	HashArr[hashfunc(key + probing - 1)].flag = ISITEM;
}
void search(int key) {
	int probing = 1;
	while (HashArr[hashfunc(key + probing - 1)].flag != NOITEM) {
		if (HashArr[hashfunc(key + probing - 1)].key == key) {	
			cout << 1 << ' ' << probing << '\n';
			return;
		}
		probing++;
	}
	cout << 0 << ' '<< probing << '\n';
}
void remove(int key) {
	int probing = 1;
	while (HashArr[hashfunc(key + probing - 1)].flag != NOITEM) {
		if (HashArr[hashfunc(key + probing - 1)].key == key) {
			cout << 1 << ' ' << probing << '\n';
			HashArr[hashfunc(key + probing - 1)].key = -1;
			HashArr[hashfunc(key + probing - 1)].value = -1;
			HashArr[hashfunc(key + probing - 1)].flag = AVAILABLE;
			return;
		}
		probing++;
	}
	cout << 0 << ' ' << probing << '\n';
}
void clear() {
	for (int i = 0; i < MAX; i++) {
		HashArr[i].key = -1;
		HashArr[i].value = -1;
		HashArr[i].flag = NOITEM;
	}
}
int main() {
	ios_base::sync_with_stdio(false);
	cin.tie(NULL);
	int t;
	cin >> t;
	while (t--) {
		int n;
		cin >> n;
		for (int i = 0; i < n; i++) {
			int x;
			cin >> x;
			insert(x,0);
		}
		int m;
		cin >> m;
		for (int i = 0; i < m; i++) {
			int x;
			cin >> x;
			//search(x);
			remove(x);
		}
		clear();
	}
}