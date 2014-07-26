package com.game.base;

import java.util.regex.Pattern;

public class TestRegex {

	
	
	public static void main(String[] args) {
		//picture replace all the mark
		Pattern p = Pattern.compile("[0-9]+\\..*?((?=(<img.*?/>[0-9]+\\.))|$)");
		p.matcher("1. <img hshsdh /> sfds");
	}
}
