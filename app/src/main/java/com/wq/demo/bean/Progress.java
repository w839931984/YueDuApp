package com.wq.demo.bean;

/**
 * Created by WQ on 2016/8/25.
 */
public class Progress {
	int bookId;
	String currentIndex;

	public long getCurrentIndex() {
		return Integer.valueOf(currentIndex);
	}

	public void setCurrentIndex(long currentIndex) {
		this.currentIndex = currentIndex+"";
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
}
