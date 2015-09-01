package com.belief.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleCounterInfo {

	public int LY;
	public int RZ;
	public int SS;
	public int XC;
}
