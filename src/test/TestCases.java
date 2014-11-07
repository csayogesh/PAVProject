package test;

//Author : Sabuj Kumar Jena and Pallavi Chugh

class TestCases {

	// Test Case 1
	// create single link list with a little distortion

	@SuppressWarnings("unused")
	snode createSingleLinkList() {
		snode list = new snode();
		snode t = list.next;
		int len = 4;
		snode temp = list;
		if (true) {
			if (true) {
				if (true) {
					for (int i = 0; i < len; i++) {
						if (i % 2 == 0)
							temp.info = new data(1);
						else
							temp.info = new data(2);
						temp.next = new snode();
						temp = temp.next;
					}
					temp.next = null;
				}
				temp.next = null;
			}
			temp.next = null;
		}
		temp.next = new snode();
		return list;
	}

	void sPrint(snode l) {
		for (int i = 0; i < 4; i++) {
			System.out.println(l.info);
			l = l.next;
		}
	}

	// main method
	@SuppressWarnings("unused")
	public static void main(String args[]) {
		TestCases t = new TestCases();
		snode l = t.createSingleLinkList();
	}
}
