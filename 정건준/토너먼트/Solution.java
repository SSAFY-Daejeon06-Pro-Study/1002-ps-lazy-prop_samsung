package 토너먼트;

import java.util.TreeSet;

/***
 * BBST(트리셋)을 사용하면 모든 리그 교환 연산인 move, trade를 L log N으로 수행 가능
 * 총 시간 복잡도 = 교환 연산 횟수 * L log N = 1500(교환 연산 최대 횟수) * 4000(L 최대 크기) * 9(log N 최대 크기)
 * 총 5천 4백만
 *
 * 1. 리그 개수만큼 treeSet을 둠
 * 2. java의 treeSet의 pollFirst(), pollLast() 메서드로 리그에서 가장 상위 선수와 가장 하위 선수를 가져올 수 있음
 *    이를 통해 move 연산 가능
 * 3. (문제점) trade를 위해선 중간 선수를 가져 올 수 있어야함
 *    java의 treeSet은 중간 선수를 가져올 수 있는 메서드가 없음
 *    하지만 A 리그에서 중간 선수의 포인터를 따로 담아두면 A 리그의 중간 선수를 바로 참조할 수 있음
 *    이를 통해 trade 연산 가능
 *    (다른 방법으로 BBST를 직접 구현할 수 있지만 구현이 매우 까다로움)
 */

public class Solution {
    static class Player implements Comparable<Player> {
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

    static class League {
        TreeSet<Player> set;
        Player mid;
        int leftSize;
        int rightSize;

        void League() {
            set = new TreeSet<>();
            mid = null;
            leftSize = 0;
            rightSize = 0;
        }

        void addPlayer(Player a) {
            set.add(a);
            if(mid.compareTo(a) < 0) leftSize += 1;
            else rightSize += 1;
            if(leftSize + rightSize + 1 == N_NUM) updateMid();
        }

        Player pollFirst() {
            leftSize -= 1;
            return set.pollFirst();
        }

        Player pollLast() {
            rightSize -= 1;
            return set.pollLast();
        }

        Player pollMid() {
            Player midPlayer = mid;
            mid = set.higher(mid);
            set.remove(mid);

            leftSize -= 1;
            return midPlayer;
        }

        void updateMid() {
            if(leftSize == rightSize) return;

            if(leftSize < rightSize) {
                mid = set.lower(mid);
                leftSize++;
                rightSize--;
            }
            else {
                mid = set.higher(mid);
                leftSize--;
                rightSize++;
            }
        }
    }

    static TreeSet<Player>[] sets; //리그 개수만큼 트리셋 생성, sets[0] = 0번 리그
    Player[] midPlayers;  //리그 개수만큼 중간 선수 존재, midPlayers[0] = 0번 리그(최상위 리그)의 중간 선수
    static int N_NUM, L_NUM;
    
    //leagueNum번 리그의 중간 선수를 가져옴
    Player pollMid(int leagueNum) {
        Player midPlayer = midPlayers[leagueNum];
        sets[leagueNum].remove(midPlayer);

        Player newMidplayer = sets[leagueNum].floor(midPlayer);
        midPlayers[leagueNum] = newMidplayer;
        return midPlayer;
    }

    void addPlayer(int leagueNum, Player newPlayer) {
        if(midPlayers[leagueNum].compareTo(newPlayer) > 0) {
            
        }
    }

    void addPlayer(int leagueNum, Player newPlayerA, Player newPlayerB) {

    }

    void init(int L, int N, int[][] players) {
        sets = new TreeSet[L];

        for(int i=0; i<L; i++) {
            for(int j=0; j<N; j++) {
                
            }
            for(int j=0; j<N; j++) {
                int id = players[0][0];
                int ability = players[0][1];
                sets[i].add(new Player(id, ability));
            }
        }
        L_NUM = L;
        N_NUM = N;
    }

    int move() {
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
        sets[0].add(maxPlayers[1]);
        for(int i=1; i<L_NUM-1; i++) {
            sets[i].add(minPlayers[i-1]);
            sets[i].add(maxPlayers[i+1]);
        }
        sets[L_NUM-1].add(minPlayers[L_NUM-2]);

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
    /*

    int trade() {
        Player[] maxPlayers = new Player[L_NUM]; //maxPlayers[i] = i번 리그의 최상위 선수
        Player[] middlePlayers = new Player[L_NUM]; //middlePlayers[i] = i번 리그의 중간 선수
        
        //교환
        //1. 각 리그의 중간 선수와 하위 선수를 뺌
        middlePlayers[0] = midPlayers[0];
        sets[0].remove(midPlayers[0]);
        upDateMidPlayers(0);

        for(int i=1; i<L_NUM-1; i++) {
            middlePlayers[i] = midPlayers[i];
            sets[0]
        }




        for(int i=1; i<L_NUM-1; i++) {
            middlePlayers[i] = midPlayers[i];
            maxPlayers[i] = sets[i].pollFirst();
            midPlayers[i] = sets[i].floor(midPlayers[i]); //새 중간 선수
        }

        maxPlayers[L_NUM-1] = sets[L_NUM-1].pollFirst();

        //2. 선수를 넣어줌
        sets[0].add(maxPlayers[1]);
        if(midPlayers[0].compareTo(maxPlayers[1]) > 0) {

        }
    }*/



    public static void main(String[] args) {

    }
}
