#include <iostream>
#include <vector>
#include <string>
using namespace std;
const int mappingSize = 500;
class Vertex {
public:
	Vertex(int data) {
		this->data = data;
	}
	int data;
	vector<Vertex* >adj_list;
};

class Edge {
public:
	Edge(Vertex* src, Vertex* dst, string str) {
		this->src = src;
		this->dst = dst;
		this->str = str;
	}
	Vertex* src;
	Vertex* dst;
	string str;
};

class Graph {
public:
	vector<Vertex*> vertex_list;
	vector<Edge*> edge_list;
	vector<vector<Edge*> > edgeMatrix;
	vector<int> mappingTable;
	int source;
	int destination;

	Graph() {
		edgeMatrix.resize(mappingSize);
		for (int i = 0; i < mappingSize; i++)
			edgeMatrix[i].resize(mappingSize);
		mappingTable.resize(mappingSize);
		for (int i = 0; i < mappingSize; i++)
			mappingTable[i] = -1;
		source = -1;
		destination = -1;
	}

	void mapping(int src_data, int dst_data) {
		source = -1;
		destination = -1;
		for (int i = 0; i < mappingSize; i++) {
			if (src_data == mappingTable[i])
				source = i;
			if (dst_data == mappingTable[i])
				destination = i;
			if (source != -1 && destination != -1)
				break;
		}
	}
	Vertex* findVertex(int data) {
		for (Vertex* v : vertex_list) {
			if (data == v->data)
				return v;
		}
		return NULL;
	}
	Edge* findEdge(Vertex* u, Vertex* v) {
		mapping(u->data, v->data);
		return edgeMatrix[source][destination];
	}
	void insertVertex(int data) {
		Vertex* newVertex = new Vertex(data);
		vertex_list.push_back(newVertex);
		mappingTable[vertex_list.size() - 1] = data;
	}
	void insertEdge(int src_data, int dst_data, string str) {
		Vertex* src = findVertex(src_data);
		Vertex* dst = findVertex(dst_data);
		if (src == NULL || dst == NULL)
		{
			cout << -1 << endl;
			return;
		}
		if (findEdge(src, dst) != NULL)
			cout << -1 << endl;
		else {
			Edge* newEdge = new Edge(src, dst, str);
			edge_list.push_back(newEdge);
			src->adj_list.push_back(dst);
			dst->adj_list.push_back(src);
			mapping(src_data, dst_data);
			edgeMatrix[source][destination] = newEdge;
			edgeMatrix[destination][source] = newEdge;
		}
	}
	int isAdjacent(int src_data, int dst_data) {
		Vertex* src = findVertex(src_data);
		Vertex* dst = findVertex(dst_data);
		if (src == NULL || dst == NULL)
			return -1;
		for (Vertex* u : src->adj_list) {
			if (u->data == dst_data)
				return 1;
		}
		return 0;
	}
	int removeVertex(int data) {
		Vertex* delVertex = findVertex(data);
		if (delVertex == NULL) 
			return -1;
		
		for (int i = 0; i < vertex_list.size(); i++) {
			if (mappingTable[i] == data) {
				mappingTable.erase(mappingTable.begin() + i);		// mappingTable에서 삭제
				vertex_list.erase(vertex_list.begin() + i);			// vertex_list에서 삭제
				break;
			}
		}

		for (int i = 0; i<edge_list.size();i++) {
			if (edge_list[i]->src->data == data || edge_list[i]->dst->data == data) { // edge_list에서 삭제
				Edge* temp = edge_list[i];
				edge_list.erase(edge_list.begin() + i);
				delete temp;
				i--;
			}							
		}
		delete delVertex;
		return 0;
	}
};

int main() {
	Graph g;
	int n, m, k;
	cin >> n >> m >> k;
	for (int i = 0; i < n; i++) {
		int x;
		cin >> x;
		g.insertVertex(x);
	}
	for (int i = 0; i < m; i++) {
		int a, b;
		string str;
		cin >> a >> b >> str;
		g.insertEdge(a, b, str);
	}
	cout << g.vertex_list.size() << ' ' << g.edge_list.size() << endl;
	for (int i = 0; i < k; i++) {
		int x;
		cin >> x;
		if (g.removeVertex(x) == -1)
			cout << -1 << endl;
		else {
			cout << g.edge_list.size() << ' ';
			for (Edge* e : g.edge_list)
				cout << e->str << ' ';
			cout << endl;
		}
	}
}