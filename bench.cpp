#include <bits/stdc++.h>
using namespace std;
#define endl "\n"
int main(void){
	string f = "u";
	string cpf = "11094482312";
	ofstream fout("bench.in");
	int n = 100;
	for(int i = 1; i <= n; ++i){
		fout << "1" << endl;
		fout << "u" << i << endl;	
		fout << cpf << endl;
		fout << "bh" << endl;
		fout << "u" << i << endl;	
		fout << "123" << i << endl;	
		fout << "u" << i << "@gmail.com" << endl;	
		fout << "n" << endl;
		fout << "400.00" << endl;	
		next_permutation(cpf.begin(), cpf.end());
	}
	fout << "0" << endl;
	return 0;
}
