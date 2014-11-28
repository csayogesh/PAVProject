package test;

//Author : Sabuj Kumar Jena and Pallavi Chugh

class TestCases {

	// Test Case 1
	// create single link list with a little distortion

	snode createSingleLinkList() {
		/*
		 * snode list = new snode(); snode t = list.next; int len = 4; snode
		 * temp = list; for (int i = 0; i < len; i++) { if (i % 2 == 0)
		 * temp.info = new data(1); else temp.info = new data(2); temp.next =
		 * new snode(); temp = temp.next; } temp.next = new snode();
		 */
		int i = 2;
		snode list1 = new snode();
		snode prev1 = list1;
		while (i == 10) {
			snode t1 = new snode();
			prev1.next = t1;
			prev1.data1 = new data();
			prev1.data2 = prev1.data1;
			prev1 = t1;
		}
		prev1.data1 = new data();
		prev1.data2 = new data();
		return list1;
	}

	void sPrint(snode l) {
		for (int i = 0; i < 4; i++) {
			//System.out.println(l.info);
			l = l.next;
		}
	}

	// main method
	public static void main(String args[]) {
		TestCases t = new TestCases();
		snode l = t.createSingleLinkList();
	}

}
