package 토너먼트;

/***
 * BBST(트리셋)을 사용하면 모든 리그 교환 연산인 move, trade를 L log N으로 수행 가능
 * 총 시간 복잡도 = 교환 연산 횟수 * L log N = 1500(교환 연산 최대 횟수) * 4000(L 최대 크기) * 9(log N 최대 크기)
 * 총 5천 4백만
 *
 * 1. 리그 개수만큼 BBST를 둠
 * 2. 1500번의 교환 수행
***/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.StringTokenizer;

public class UserSolution {
	
	static class Player implements Comparable<Player>{
		int id;
		int ability;
		Player(int id, int ability) {
			this.id = id;
			this.ability = ability;
		}
		
		@Override
		public int compareTo(Player o) {
			if(ability == o.ability) {
				return Integer.compare(id, o.id);
			}
			return Integer.compare(o.ability, ability);
		}
	}
	
	static class Node {
		Player player;
		int priority;
		int size;
		Node left;
		Node right;
		
		Node(Player player) {
			this.player = player;
			priority = new Random().nextInt();
			size = 1;
			left = null;
			right = null;
		}
		
		void setLeft(Node left) {
			this.left = left;
			reSize();
		}
		
		void setRight(Node right) {
			this.right = right;
			reSize();
		}
		
		void reSize() {
			size = 1;
			if(left != null) size += left.size;
			if(right != null) size += right.size;
		}
	}
	
	static class NodePair {
		Node left;
		Node right;
		NodePair(Node left, Node right) {
			this.left = left;
			this.right = right;
		}
	}
	
	//BBST 구현
	static class Trip {
		Node treeRoot;
		
		Node insert(Node root, Node newNode) {
			if(root == null) return newNode;

			if(root.priority < newNode.priority) {
				NodePair nodePair = split(root, newNode.player);
				newNode.setLeft(nodePair.left);
				newNode.setRight(nodePair.right);
				return newNode;
			}
			if(root.player.compareTo(newNode.player) > 0) root.setLeft(insert(root.left, newNode));
			else root.setRight(insert(root.right, newNode));
			return root;
		}
		
		NodePair split(Node root, Player player) {
			if(root == null) return new NodePair(null, null);

			if(root.player.compareTo(player) < 0) {
				NodePair nodePair = split(root.right, player);
				root.setRight(nodePair.left);
				return new NodePair(root, nodePair.right);
			}

			NodePair nodePair = split(root.left, player);
			root.setLeft(nodePair.right);
			return new NodePair(nodePair.left, root);
		}

		private Node delete(Node root, Player player) {
			if(root == null) return root;

			if(root.player.compareTo(player) == 0) return merge(root.left, root.right);
			else if(root.player.compareTo(player) > 0) root.setLeft(delete(root.left, player));
			else root.setRight(delete(root.right, player));
			return root;
		}

		private Node merge(Node a, Node b) {
			if(a == null) return b;
			if(b == null) return a;

			if(a.priority < b.priority) {
				b.setLeft(merge(a, b.left));
				return b;
			}
			a.setRight(merge(a.right, b));
			return a;
		}

		private Node kth(Node root, int k) {
			int leftSize = 0;
			if(root.left != null) leftSize = root.left.size;

			if(k <= leftSize) return kth(root.left, k);
			else if(k == leftSize + 1) return root;
			return kth(root.right, k - leftSize - 1);
		}

		public Player pollFirst() {
			Player player = kth(treeRoot, 1).player;
			deleteNode(player);
			return player;
		}

		public Player pollLast() {
			Player player = kth(treeRoot, treeRoot.size).player;
			deleteNode(player);
			return player;
		}

		public Player pollMid() {
			Player player = kth(treeRoot, treeRoot.size / 2 + 1).player;
			deleteNode(player);
			return player;
		}

		public void insertNode(Player player) {
			treeRoot = insert(treeRoot, new Node(player));
		}

		public void deleteNode(Player player) {
			treeRoot = delete(treeRoot, player);
		}
	}

	static Trip[] sets; //리그 개수만큼 트리 생성, sets[i] = i번 리그
	static int N_NUM, L_NUM;

	static void init(int L, int N, int[][] players) {
		sets = new Trip[L];

		for(int i=0; i<L; i++) {
			sets[i] = new Trip();

			for(int j=0; j<N; j++) {
				int id = players[i * N + j][0];
				int ability = players[i * N + j][1];
				sets[i].insertNode(new Player(id, ability));
			}
		}

		L_NUM = L;
		N_NUM = N;
	}

	static int move() {
		Player[] maxPlayers = new Player[L_NUM]; //maxPlayers[i] = i번 리그의 최상위 선수
		Player[] minPlayers = new Player[L_NUM]; //minPlayers[i] = i번 리그의 최하위 선수

		//교환
		//1. 우선 각 리그의 상위 선수와 하위 선수를 뺌(최상위 리그와 최하위 리그는 1명만)
		minPlayers[0] = sets[0].pollLast();
		for(int i=1; i<L_NUM-1; i++) {
			maxPlayers[i] = sets[i].pollFirst();
			minPlayers[i] = sets[i].pollLast();
		}
		maxPlayers[L_NUM-1] = sets[L_NUM-1].pollFirst();

		//2. 선수를 넣어줌
		sets[0].insertNode(maxPlayers[1]);
		for(int i=1; i<L_NUM-1; i++) {
			sets[i].insertNode(minPlayers[i-1]);
			sets[i].insertNode(maxPlayers[i+1]);
		}
		sets[L_NUM-1].insertNode(minPlayers[L_NUM-2]);

		//교환 되는 선수들의 ID 합 반환
		int iDSum = 0;
		iDSum += minPlayers[0].id;
		for(int i=1; i<L_NUM-1; i++) {
			iDSum += maxPlayers[i].id;
			iDSum += minPlayers[i].id;
		}
		iDSum += maxPlayers[L_NUM-1].id;
		return iDSum;
	}

	static int trade() {
		Player[] maxPlayers = new Player[L_NUM]; //maxPlayers[i] = i번 리그의 최상위 선수
		Player[] midPlayers = new Player[L_NUM]; //middlePlayers[i] = i번 리그의 중간 선수

		//교환
		//1. 각 리그의 중간 선수와 상위 선수를 뺌
		midPlayers[0] = sets[0].pollMid();
		for(int i=1; i<L_NUM-1; i++) {
			midPlayers[i] = sets[i].pollMid();
			maxPlayers[i] = sets[i].pollFirst();
		}
		maxPlayers[L_NUM-1] = sets[L_NUM-1].pollFirst();

		//2. 선수를 넣어줌
		sets[0].insertNode(maxPlayers[1]);
		for(int i=1; i<L_NUM-1; i++) {
			sets[i].insertNode(midPlayers[i-1]);
			sets[i].insertNode(maxPlayers[i+1]);
		}
		sets[L_NUM-1].insertNode(midPlayers[L_NUM-2]);

		//교환 되는 선수들의 ID 합 반환
		int iDSum = 0;
		iDSum += midPlayers[0].id;
		for(int i=1; i<L_NUM-1; i++) {
			iDSum += midPlayers[i].id;
			iDSum += maxPlayers[i].id;
		}
		iDSum += maxPlayers[L_NUM-1].id;
		return iDSum;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;

		System.out.println("리그의 개수 L와 리그당 선수 M 입력 : ");
		st = new StringTokenizer(br.readLine());
		int L = Integer.parseInt(st.nextToken());
		int N = Integer.parseInt(st.nextToken());
		int[][] players = new int[L*N][2];

		System.out.println("선수 능력치 입력, (id) 순으로 : ");
		st = new StringTokenizer(br.readLine());
		for(int i=0; i<L*N; i++) {
			players[i][0] = i;
			players[i][1] = Integer.parseInt(st.nextToken());
		}

		init(L, N, players);
		System.out.println(move());
		System.out.println(trade());
	}
}
