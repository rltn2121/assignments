#include <iostream>
#include <vector>

#define NOT_EXPLORED 0
#define DISCOVERY 1
#define BACK 2
#define MappingSize 1001
using namespace std;


class Vertex {
public:
	int data;
	int degree;
	bool visited;
	vector<Vertex*> adj_list;

	Vertex(int data) {
		this->data = data;
		this->degree = 0;
		this->visited = false;
	}
};

class Edge {
public:
	Vertex* src;
	Vertex* dst;
	int data;
	bool edge_stat;
	Edge(Vertex* src, Vertex* dst) {
		this->src = src;
		this->dst = dst;
		this->edge_stat = NOT_EXPLORED;
	}
};
class Node {
public:
	Node(Vertex* v) {
		this->vertex = v;
	}
	Vertex* vertex;
	Node* next;
};
class Queue {
public:
	Node* head;
	Node* tail;
	Queue() {
		head = tail = NULL;
	}
	bool isEmpty() {
		return head == NULL;
	}
	void enqueue(Vertex* v) {
		Node* n = new Node(v);
		if (head == NULL)
			head = tail = n;
		else {
			tail->next = n;
			tail = n;
		}
	}
	Vertex* dequeue() {
		if (isEmpty()) return NULL;
		Vertex* ret = head->vertex;
		if (head == tail)
			head = tail = NULL;
		else
			head = head->next;
		return ret;
	}
	/*void display() {
		Node* n = head;
		while (n != NULL) {
			cout << n->vertex->data << ' ';
			n = n->next;
		}
		cout << "\nhead: " << head->vertex->data
			<< "\ntail: " << tail->vertex->data << '\n';
	}*/
};
class Graph {
public:
	vector<Vertex*> vertex_list;
	vector<Edge*> edge_list;
	vector<vector<Edge*> > edge_matrix;
	int source;
	int destination;
	int mappingTable[MappingSize];
	Graph() {
		edge_matrix.resize(MappingSize);
		for (size_t i = 0; i < edge_matrix.size(); i++)
			edge_matrix[i].resize(MappingSize);
		for (size_t i = 0; i < MappingSize; i++)
			mappingTable[i] = -1;
		source = -1;
		destination = -1;
	}
	void mapping(int src_data, int dst_data) {
		destination = -1;
		source = -1;
		for (size_t i = 0; i <= vertex_list.size(); i++) {
			if (mappingTable[i] == src_data)
				source = i;
			if (mappingTable[i] == dst_data)
				destination = i;
			if (source != -1 && destination != -1)
				break;
		}
	}
	Vertex* findVertex(int data) {
		Vertex* v = NULL;
		for (size_t i = 0; i < vertex_list.size(); i++) {
			if (vertex_list[i]->data == data) {
				v = vertex_list[i];
				break;
			}
		}
		return v;
	}
	Edge* findEdge(int src_data, int dst_data) {
		
		mapping(src_data, dst_data);
		if (source == -1 || destination == -1)
			return NULL;
		return edge_matrix[source][destination];
	}
	void insert_vertex(int data) {
		if (findVertex(data) != NULL) return;
		Vertex* newVertex = new Vertex(data);
		vertex_list.push_back(newVertex);
		mappingTable[vertex_list.size() - 1] = data;
	}
	void insert_edge(int src_data, int dst_data) {
		Vertex* src = findVertex(src_data);
		Vertex* dst = findVertex(dst_data);
		if (findEdge(src_data, dst_data) != NULL) {
			cout << -1 << endl;
			return;
		}
		Edge* newEdge = new Edge(src, dst);
		edge_list.push_back(newEdge);
		mapping(src_data, dst_data);
		edge_matrix[source][destination] = newEdge;
		edge_matrix[destination][source] = newEdge;
		src->adj_list.push_back(dst);
		dst->adj_list.push_back(src);
		src->degree++;
		dst->degree++;
	}
	void dfs(Vertex* v) {
		if (v->visited) return;
		v->visited = true;
		cout << v->data << ' ';
		for (Vertex* u : v->adj_list)
			dfs(u);
	}
	void bfs(Vertex* v) {
		Queue q;
		q.enqueue(v);
		v->visited = true;
		cout << v->data << ' ';
		while (!q.isEmpty()) {
			Vertex* u = q.dequeue();
			for (size_t i = 0; i<u->adj_list.size(); i++) {
				if (u->adj_list[i]->visited == false) {
					q.enqueue(u->adj_list[i]);
					u->adj_list[i]->visited = true;
					cout << u->adj_list[i]->data << ' ';
				}
			}
		}
	}
};

int main() {
	int n, m, k;
	cin >> n >> m >> k;
	Graph g;
	for(int i = 0; i<n; i++){
		int x; cin >> x;
		g.insert_vertex(x);
	}
	for (int i = 0; i < m; i++) {
		int a, b; cin >> a >> b;
		g.insert_edge(a, b);
	}
	Vertex* start = g.findVertex(k);
	//g.dfs(start);
	g.bfs(start);
	cout << endl;

	
}
	